package com.example.exception;

import com.example.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Comparator;
import java.util.List;

/**
 * 全域例外處理器。
 *
 * <p>@RestControllerAdvice = @ControllerAdvice + @ResponseBody，
 * 會攔截所有 @RestController 拋出的例外，並將回傳值直接序列化成 JSON。</p>
 *
 * <p>搭配 LoggingAspect：切面負責「記錄」例外後往外拋，
 * 這裡負責「轉換」成統一格式的錯誤回應，兩者職責分離。</p>
 *
 * <p>比對順序：Spring 會挑選「最接近」的 @ExceptionHandler，
 * 因此 ResourceNotFoundException 會優先於其父類別 BusinessException，
 * 而最後的 Exception 則是所有未預期例外的保底處理。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------------------------------------------------
    // 1. 自訂業務例外（可預期的錯誤，用 warn 等級記錄即可）
    // ---------------------------------------------------------------

    /** 查無資料 -> 404 Not Found */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                               HttpServletRequest request) {
        log.warn("資源不存在: {}", ex.getMessage());
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), request);
    }

    /** 一般業務例外 -> 由例外自帶的狀態碼決定（預設 400 Bad Request） */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex,
                                                       HttpServletRequest request) {
        log.warn("業務邏輯錯誤 [{}]: {}", ex.getCode(), ex.getMessage());
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), request);
    }

    // ---------------------------------------------------------------
    // 2. 參數驗證失敗
    // ---------------------------------------------------------------

    /** @Valid 驗證 @RequestBody 失敗 -> 400，並列出每個欄位的錯誤 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .sorted(Comparator.comparing(ErrorResponse.FieldError::field))
                .toList();

        log.warn("參數驗證失敗: {}", fieldErrors);
        return validationResponse(fieldErrors, request);
    }

    /** @Validated 驗證方法參數（@RequestParam / @PathVariable）失敗 -> 400 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(v -> new ErrorResponse.FieldError(
                        v.getPropertyPath().toString(), v.getInvalidValue(), v.getMessage()))
                .sorted(Comparator.comparing(ErrorResponse.FieldError::field))
                .toList();

        log.warn("參數驗證失敗: {}", fieldErrors);
        return validationResponse(fieldErrors, request);
    }

    // ---------------------------------------------------------------
    // 3. 常見的 Spring MVC 請求錯誤
    //    （沒有單獨處理的話，會掉進最後的 Exception 而變成 500）
    // ---------------------------------------------------------------

    /** 缺少必填的查詢參數 -> 400 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest request) {
        String message = "缺少必要參數: " + ex.getParameterName() + " (" + ex.getParameterType() + ")";
        log.warn(message);
        return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", message, request);
    }

    /** 參數型別錯誤，例如 /api/users/abc 但 id 宣告為 Long -> 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String expectedType = ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName();
        String message = "參數 " + ex.getName() + " 的值 [" + ex.getValue() + "] 無法轉換為 " + expectedType;
        log.warn(message);
        return build(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", message, request);
    }

    /** Request Body 不是合法 JSON 或無法反序列化 -> 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        log.warn("請求內容無法解析: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST",
                "請求內容格式錯誤或不是合法的 JSON", request);
    }

    /** HTTP method 不支援，例如對唯讀端點發 POST -> 405 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest request) {
        String[] supported = ex.getSupportedMethods() == null ? new String[0] : ex.getSupportedMethods();
        String message = "不支援的 HTTP 方法: " + ex.getMethod()
                + "，此路徑支援的方法為 " + String.join(", ", supported);
        log.warn(message);
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", message, request);
    }

    /** 找不到對應的路由 -> 404（不處理的話會掉進保底的 500） */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                                                               HttpServletRequest request) {
        log.warn("找不到路由: {} {}", request.getMethod(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, "ENDPOINT_NOT_FOUND",
                "找不到對應的 API: " + request.getMethod() + " " + request.getRequestURI(), request);
    }

    // ---------------------------------------------------------------
    // 4. 保底：所有未預期的例外 -> 500
    // ---------------------------------------------------------------

    /**
     * 未預期的例外一律以 error 等級記錄並附上完整 stack trace，
     * 但「不要」把 ex.getMessage() 回傳給前端，避免洩漏內部實作細節。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("未預期的系統錯誤: {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "系統發生錯誤，請稍後再試或聯繫管理員", request);
    }

    // ---------------------------------------------------------------
    // 共用工具方法
    // ---------------------------------------------------------------

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
                                                HttpServletRequest request) {
        return ResponseEntity.status(status).body(ErrorResponse.of(
                status.value(), status.getReasonPhrase(), code, message, request.getRequestURI()));
    }

    private ResponseEntity<ErrorResponse> validationResponse(List<ErrorResponse.FieldError> fieldErrors,
                                                             HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "VALIDATION_FAILED",
                "請求參數驗證失敗，共 " + fieldErrors.size() + " 個欄位有誤",
                request.getRequestURI(),
                fieldErrors));
    }

    private ErrorResponse.FieldError toFieldError(FieldError error) {
        return new ErrorResponse.FieldError(
                error.getField(), error.getRejectedValue(), error.getDefaultMessage());
    }
}
