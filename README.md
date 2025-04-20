

## Exception Handling

The library throws `PageSortValidationException` when validation of page/sort parameters fails. You have several options for handling these exceptions:
1. **Use the built-in exception handler**: The library provides a default exception handler that converts `PageSortValidationException` to HTTP 400 Bad Request responses.
2. **Create a custom exception handler**: You can create your own exception handler to customize the response structure and status code.

### Option 1: Use the built-in exception handler

By default, the library includes a built-in exception handler that converts `PageSortValidationException` to HTTP 400 Bad Request responses. This handler is enabled by default.

To disable it, add this to your `application.properties` or `application.yml`:

```properties
com.therdnotes.springpagesort.exception-handling.enabled=false
```

### Option 2: Create a custom exception handler

You can create your own exception handler for `PageSortValidationException`:

```java
@RestControllerAdvice
public class YourExceptionHandler {

    @ExceptionHandler(PageSortValidationException.class)
    public ResponseEntity<?> handlePageSortValidationException(PageSortValidationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Paging/sorting query parameters validation error: "+ex.getMessage());
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
```
