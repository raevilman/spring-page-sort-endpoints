# Spring Page Sort Endpoints - Usage Guide

A lightweight library for standardized pagination and sorting in Spring Boot REST APIs.

## Installation

Add the dependency to your project:

```gradle
implementation 'com.therdnotes:spring-page-sort-endpoints:0.1.0'
```

## Basic Usage

1. Annotate RestController methods with `@PageSortConfig`
2. Inject `PageSortRequest` as a parameter in your controller method:

```java
@RestController
public class ItemController {
    
    @GetMapping("/items")
    @PageSortConfig
    public Map<String, Object> getItems(PageSortRequest pageSortRequest) {
        // Access pagination params with:
        int page = pageSortRequest.page();     // Default: 0
        int size = pageSortRequest.size();     // Default: 25
        String sortBy = pageSortRequest.sortBy();
        String sortDir = pageSortRequest.sortDir(); // Default: "asc"
        
        // Use values to query your data source
        // ...
        
        return Map.of(
            "page", page,
            "size", size,
            "sortBy", sortBy,
            "sortDir", sortDir
        );
    }
}
```

## Custom Configuration

Apply the `@PageSortConfig` annotation to customize validation:

```java
@GetMapping("/products")
@PageSortConfig(
    defaultPage = 0,
    defaultSize = 10,
    minPage = 0,
    minSize = 1,
    maxSize = 50,
    validSortFields = {"name", "price", "date"}
)
public Page<Product> getProducts(PageSortRequest pageSortRequest) {
    // Your implementation
}
```

## Request Examples

These query parameters are automatically parsed:

```
GET /items?page=2&size=10
GET /items?sortBy=name&sortDir=desc
GET /items?page=0&size=5&sortBy=date&sortDir=asc
```

## Validation

The library automatically validates all parameters:
- Invalid page numbers (negative values)
- Invalid page sizes (too small or too large)
- Invalid sort fields (if validSortFields is specified)
- Invalid sort directions (only "asc" or "desc" allowed)

When validation fails, a `400 Bad Request` response is returned with a descriptive error message.

## Features

- Sensible defaults (page=0, size=25, sortDir=asc)
- Case-insensitive sort direction handling
- Consistent error responses
- Parameter order independence
- Comprehensive logging

## Requirements

- Spring Boot 3.4.3+
- Java 17+

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
