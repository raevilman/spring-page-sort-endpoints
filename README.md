# Spring Page Sort Endpoints - Usage Guide

A lightweight library for standardized pagination and sorting in Spring Boot REST APIs.

## Installation

Add the dependency to your project:

```gradle
implementation 'com.therdnotes:spring-page-sort-endpoints:0.1.2'
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
        int offset = pageSortRequest.offset();     // Default: 0
        int limit = pageSortRequest.limit();       // Default: 25
        String sortBy = pageSortRequest.sortBy();
        String sortDir = pageSortRequest.sortDir(); // Default: "asc"
        
        // Use values to query your data source
        // ...
        
        return Map.of(
            "offset", offset,
            "limit", limit,
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
    defaultOffset = 0,
    defaultLimit = 10,
    minOffset = 0,
    minLimit = 1,
    maxLimit = 50,
    validSortFields = {"name", "price", "date"}
)
public Page<Product> getProducts(PageSortRequest pageSortRequest) {
    // Your implementation
}
```

## Request Examples

These query parameters are automatically parsed:

```
GET /items?offset=20&limit=10
GET /items?sortBy=name&sortDir=desc
GET /items?offset=0&limit=5&sortBy=date&sortDir=asc
```

## Validation

The library automatically validates all parameters:
- Invalid offset values (negative values)
- Invalid limit values (too small or too large)
- Invalid sort fields (if validSortFields is specified)
- Invalid sort directions (only "asc" or "desc" allowed)

When validation fails, a `400 Bad Request` response is returned with a descriptive error message.

## Features

- Sensible defaults (offset=0, limit=25, sortDir=asc)
- Case-insensitive sort direction handling
- Consistent error responses
- Parameter order independence
- Comprehensive logging

---

# Using the Annotation Processor

This annotation processor validates your `@PageSortConfig` configuration at compile time to ensure proper setup of pagination and sorting parameters.

## How It Works

The processor checks that your `@PageSortConfig` annotation is correctly configured by validating:
- If `defaultSortBy` is specified, it must be one of the `validSortFields`
- Notifies when `validSortFields` are specified but `defaultSortBy` isn't set

## Setup in Your Project

Add the library to your project:

```gradle
dependencies {
    implementation 'com.therdnotes:spring-page-sort-endpoints:0.1.2'
    
    // Important: Enable annotation processing
    annotationProcessor 'com.therdnotes:spring-page-sort-endpoints:0.1.2'
}
```

## Usage

Apply the annotation to your REST controller methods:

```java
@RestController
@RequestMapping("/items")
public class ItemsController {
    @GetMapping
    @PageSortConfig(
        defaultLimit = 10,
        maxLimit = 50,
        validSortFields = {"title", "createdAt", "price"},
        defaultSortBy = "createdAt"
    )
    public ResponseEntity<Page<Item>> getItems(PageSortRequest pageSortRequest) {
        // Implementation
    }
}
```

## Compiler Feedback

The processor will catch issues during compilation:
- Error: `defaultSortBy must be one of the validSortFields`
- Note: Suggestion to specify `defaultSortBy` when `validSortFields` is defined

This helps you catch configuration issues early, without runtime errors.

---

## Exception Handling

The library throws `PageSortValidationException` when validation of offset/sort parameters fails. You have several options for handling these exceptions:
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

---
## Requirements

- **Library Usage**: Requires Java 17+
- **Annotation Processor**: Compatible with Java 17+
- **Spring Boot**: Works with Spring Boot 3.x+

The annotation processor is compatible with all Java versions 17 and newer, including future releases.