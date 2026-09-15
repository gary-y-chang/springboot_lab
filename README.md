# Lesson 05 · Part B 範例專案

SLF4J ＋ Logback 搭配 Spring AOP，自動記錄所有 Controller API 的進入與離開日誌；
再以 `@RestControllerAdvice` ＋ `@ExceptionHandler` 統一處理所有例外，回傳一致的 JSON 錯誤格式。

## 專案結構

```
src-lesson05/
├── pom.xml
└── src/main/
    ├── java/com/example/
    │   ├── DemoApplication.java
    │   ├── aspect/LoggingAspect.java                # @Around 切面：前置／後置／異常記錄
    │   ├── controller/UserController.java           # 不含任何日誌或 try-catch
    │   ├── dto/
    │   │   ├── ErrorResponse.java                   # 統一錯誤回應格式
    │   │   └── CreateUserRequest.java               # @Valid 驗證示範
    │   └── exception/
    │       ├── BusinessException.java               # 業務例外（自帶 code ＋ HTTP 狀態）
    │       ├── ResourceNotFoundException.java       # 查無資料 -> 404
    │       └── GlobalExceptionHandler.java          # @RestControllerAdvice 全域例外處理
    └── resources/
        ├── application.yml                          # dev / prod 兩個 Profile
        └── logback-spring.xml                       # CONSOLE ＋ RollingFile Appender
```

## 兩者的職責分工

| 元件 | 負責 |
| --- | --- |
| `LoggingAspect`（AOP） | **記錄**方法進出與例外，記完後把例外往外拋 |
| `GlobalExceptionHandler` | **轉換**例外為統一的 JSON 錯誤回應與 HTTP 狀態碼 |

Controller 因此完全不需要寫 `log.xxx()` 或 `try-catch`，業務邏輯保持乾淨。

## 執行

```bash
cd src-lesson05

# dev：只輸出到主控台
mvn spring-boot:run

# prod：主控台 ＋ 寫入 ./logs/app-info.log
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# 打包後執行
mvn clean package
java -jar target/lesson05-logging-aop-1.0.0.jar --spring.profiles.active=prod
```

## 驗證：AOP 日誌

```bash
curl "http://localhost:8080/api/greet?name=John"
```

控制台輸出：

```
2026-09-12 12:50:35.355 [http-nio-8080-exec-1]  INFO com.example.aspect.LoggingAspect - >> Invoke [UserController -> greetUser], Parameters: [John]
2026-09-12 12:50:35.356 [http-nio-8080-exec-1]  INFO com.example.aspect.LoggingAspect - << Exit [UserController -> greetUser], Return: Hello, John!, Duration: 0ms
```

以 `prod` 啟動時，同一批日誌會同時寫入 `./logs/app-info.log`；滿 10MB 或跨日即滾動歸檔至 `./logs/archived/`，保留 30 天、總量上限 2GB。

## 驗證：全域例外處理

所有錯誤都回傳同一種結構（`fieldErrors` 只有驗證失敗時才會出現）：

```json
{
  "timestamp": "2026-09-12T12:50:35.610",
  "status": 404,
  "error": "Not Found",
  "code": "RESOURCE_NOT_FOUND",
  "message": "找不到 User，id = 99",
  "path": "/api/users/99"
}
```

### 各種情境對照表

| # | 指令 | 例外 | 狀態 | code |
| --- | --- | --- | --- | --- |
| 1 | `GET "localhost:8080/api/greet?name=John"` | — | 200 | — |
| 2 | `GET "localhost:8080/api/greet"` | `MissingServletRequestParameterException` | 400 | `MISSING_PARAMETER` |
| 3 | `GET "localhost:8080/api/users/99"` | `ResourceNotFoundException` | 404 | `RESOURCE_NOT_FOUND` |
| 4 | `GET "localhost:8080/api/users/abc"` | `MethodArgumentTypeMismatchException` | 400 | `TYPE_MISMATCH` |
| 5 | `GET "localhost:8080/api/nope"` | `NoResourceFoundException` | 404 | `ENDPOINT_NOT_FOUND` |
| 6 | `GET -X DELETE "localhost:8080/api/greet?name=X"` | `HttpRequestMethodNotSupportedException` | 405 | `METHOD_NOT_ALLOWED` |
| 7 | POST 空 name／錯 email／age=200 | `MethodArgumentNotValidException` | 400 | `VALIDATION_FAILED` |
| 8 | POST `{"name":"Alice",...}`（已存在） | `BusinessException` | 409 | `USER_ALREADY_EXISTS` |
| 9 | POST `{"name":"Carol",...}` | — | 201 | — |
| 10 | POST 壞掉的 JSON | `HttpMessageNotReadableException` | 400 | `MALFORMED_REQUEST` |
| 11 | `curl "localhost:8080/api/divide?a=10&b=0"` | `ArithmeticException` | 500 | `INTERNAL_ERROR` |

驗證失敗（第 7 項）會逐欄列出原因：

```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"","email":"not-an-email","age":200}'
```

```json
{
  "timestamp": "2026-09-12T12:50:43.139",
  "status": 400,
  "error": "Bad Request",
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

console 中指定 chcp 碼：

```bash
chcp 65001
```


# Lesson 06 · Spring MVC Web 請求處理

## 檔案對照

| 檔案 | 對應投影片 |
|---|---|
| `controller/BookController.java` + `dto/BookDto.java` | 4 種參數接收方式（@PathVariable / @RequestParam / @RequestBody / @ModelAttribute） |
| `dto/UserResponseDto.java` + `controller/UserProfileController.java` | Jackson 註解控制 JSON（@JsonProperty / @JsonFormat / @JsonInclude / @JsonIgnore） |
| `dto/UserRegisterDto.java` + `controller/AuthController.java` | @Valid 與 Bean Validation |
| `dto/ProductDto.java`、`service/ProductService.java`、`controller/ProductController.java` | Lab 1：商品 CRUD ＋ Lab 3：驗證 |
| `controller/CategoryProductController.java` | Lab 2：路徑變數 ＋ 查詢參數並用 |


# Lesson 07 · Spring Data JPA 基礎篇 — 範例程式與 Lab 參考解

Spring Boot **4.1.1** / Java 21 / Maven。

## 執行

```bash
cd labs-lesson07
./mvnw spring-boot:run      # 或 mvn spring-boot:run
```

啟動後：

- REST 端點：<http://localhost:8080/employees>

## 目錄對照

| 路徑 | 對應投影片 |
| --- | --- |
| `entity/Employee.java` | B.1 Entity 定義、@Entity 規則、B.2 欄位映射 |
| `repository/EmployeeRepository.java` | C.1 JpaRepository、Lab 1 參考解 |
| `service/EmployeeService.java` | C.2 內建 CRUD、Lab 2 參考解 |
| `controller/EmployeeController.java` | Lab 2 串接三層與 REST 端點 |
| `examples/User*.java` | 課堂範例（標準用法 / 自訂擴充 / 分頁排序） |
| `resources/application.yml` | A.4 連線設定（H2 預設、MySQL 註解版） |
| `resources/application-mysql.properties` | A.4 方案 A：properties 寫法 |
| `resources/data.sql` | H2 初始化資料 |

## Lab 對照

- **Lab 1**：`Employee` Entity ＋ `EmployeeRepository` 完成 CRUD（取代記憶體 List）。
- **Lab 2**：Controller → Service → Repository → H2，讓 REST 端點真正操作資料庫。
