# 全域例外處理 · 實作重點

以 `@RestControllerAdvice` ＋ `@ExceptionHandler` 集中處理所有 Controller 拋出的例外，
讓 Controller 完全不需要寫 `try-catch`，並保證前端永遠收到同一種 JSON 錯誤格式。

環境：Spring Boot 4.1.1 / Spring Framework 7.0.9 / Java 21

---

## 1. 檔案與職責

| 檔案 | 職責 |
| --- | --- |
| `exception/GlobalExceptionHandler.java` | 全域例外處理器，把例外轉成統一回應 |
| `exception/BusinessException.java` | 業務例外基底，自帶 `code` ＋ `HttpStatus` |
| `exception/ResourceNotFoundException.java` | 繼承上者，固定 404 |
| `dto/ErrorResponse.java` | 統一錯誤回應格式（record） |
| `dto/CreateUserRequest.java` | `@Valid` 驗證示範用的請求 Body |

---

## 2. 核心設計：三個決策

### 決策一：例外自帶狀態碼，而不是每種例外寫一個 handler

```java
public class BusinessException extends RuntimeException {
    private final String code;        // 給前端辨識，例如 USER_ALREADY_EXISTS
    private final HttpStatus status;  // 預設 400，可自行指定
}
```

新增一種業務錯誤時**不用改 handler**，直接丟出即可：

```java
throw new BusinessException("使用者 " + name + " 已存在",
                            "USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
```

### 決策二：統一回應格式，選用欄位用 `NON_NULL` 隱藏

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp, int status, String error,
        String code, String message, String path,
        List<FieldError> fieldErrors) { }   // 只有驗證失敗時才出現
```

### 決策三：與 AOP 分工，各做一件事

| 元件 | 做什麼 |
| --- | --- |
| `LoggingAspect`（`@Around`） | **記錄**例外，然後 `throw throwable` 往外拋 |
| `GlobalExceptionHandler` | **轉換**例外為 HTTP 狀態 ＋ JSON |

實際輸出可以看到兩者接力：

```
ERROR LoggingAspect          - !! [UserController -> getUser] Error: 找不到 User，id = 99
WARN  GlobalExceptionHandler - 資源不存在: 找不到 User，id = 99
```

---

## 3. Handler 分層與對照表

```java
@Slf4j
@RestControllerAdvice     // = @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("資源不存在: {}", ex.getMessage());
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), request);
    }
    // ...
}
```

| 層 | 例外 | 狀態 | `code` |
| --- | --- | --- | --- |
| 1 業務 | `ResourceNotFoundException` | 404 | `RESOURCE_NOT_FOUND` |
| 1 業務 | `BusinessException` | 例外自帶 | 例外自帶 |
| 2 驗證 | `MethodArgumentNotValidException`（`@Valid` Body） | 400 | `VALIDATION_FAILED` |
| 2 驗證 | `ConstraintViolationException`（`@Validated` 參數） | 400 | `VALIDATION_FAILED` |
| 3 MVC | `MissingServletRequestParameterException` | 400 | `MISSING_PARAMETER` |
| 3 MVC | `MethodArgumentTypeMismatchException` | 400 | `TYPE_MISMATCH` |
| 3 MVC | `HttpMessageNotReadableException` | 400 | `MALFORMED_REQUEST` |
| 3 MVC | `HttpRequestMethodNotSupportedException` | 405 | `METHOD_NOT_ALLOWED` |
| 3 MVC | `NoResourceFoundException` | 404 | `ENDPOINT_NOT_FOUND` |
| 4 保底 | `Exception` | 500 | `INTERNAL_ERROR` |

---

## 4. 四個踩雷點

**① 有了保底 handler，就必須單獨處理框架例外**

`@ExceptionHandler(Exception.class)` 會把 `NoResourceFoundException`（找不到路由）
也吃掉，讓原本的 **404 變成 500**。第 3 層的存在就是為了攔在保底之前。

**② 比對規則是「最接近優先」，不是宣告順序**

`ResourceNotFoundException` 會命中自己的 handler，而不是父類別 `BusinessException` 的。
所以繼承體系可以安心使用，但也要注意別不小心被父類別 handler 蓋掉。

**③ 500 不要回傳 `ex.getMessage()`**

內部訊息（SQL、路徑、類別名）會洩漏實作細節。stack trace 寫進日誌，前端只給通用訊息：

```java
log.error("未預期的系統錯誤: {} {}", request.getMethod(), request.getRequestURI(), ex);
return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
             "系統發生錯誤，請稍後再試或聯繫管理員", request);
```

**④ 例外要分級記錄**

| 類型 | 等級 | 是否記 stack trace |
| --- | --- | --- |
| 可預期的業務例外 | `warn` | 否，訊息即足夠 |
| 未預期的系統例外 | `error` | 是，`log.error(msg, ex)` 最後一個參數傳 `ex` |

---

## 5. 相依套件

Boot 4 沿用原名（不像 `web` → `webmvc`、`aop` → `aspectj` 有改名）：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

---

## 6. 快速驗證

```bash
mvn spring-boot:run

curl "localhost:8080/api/users/99"          # 404 RESOURCE_NOT_FOUND
curl "localhost:8080/api/greet"             # 400 MISSING_PARAMETER
curl "localhost:8080/api/users/abc"         # 400 TYPE_MISMATCH
curl "localhost:8080/api/nope"              # 404 ENDPOINT_NOT_FOUND
curl "localhost:8080/api/divide?a=10&b=0"   # 500 INTERNAL_ERROR

curl -X POST "localhost:8080/api/users" -H "Content-Type: application/json" \
     -d '{"name":"","email":"not-an-email","age":200}'   # 400 VALIDATION_FAILED
```

驗證失敗會逐欄回報：

```json
{
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "請求參數驗證失敗，共 3 個欄位有誤",
  "path": "/api/users",
  "fieldErrors": [
    { "field": "age",   "rejectedValue": 200,            "message": "年齡不可大於 150" },
    { "field": "email", "rejectedValue": "not-an-email", "message": "Email 格式不正確" },
    { "field": "name",  "rejectedValue": "",             "message": "姓名不可為空白" }
  ]
}
```

