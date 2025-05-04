package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@Slf4j
public class TestController {

    @GetMapping
    @PageSortConfig(
            defaultSize = 12,
            maxSize = 12,
            validSortFields = {"name", "days"}
    )
    public ResponseEntity<PageSortRequest> getPageRequest(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);

        return ResponseEntity.ok(pageSortRequest);
    }

    @GetMapping("/default-sort")
    @PageSortConfig(
            defaultSize = 12,
            maxSize = 12,
            validSortFields = {"name", "days"},
            defaultSortBy = "name" // This sets a default sort field
    )
    public ResponseEntity<PageSortRequest> getPageRequestDefaultSort(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);
        return ResponseEntity.ok(pageSortRequest);
    }

    @GetMapping("/no-sort")
    @PageSortConfig(
            defaultSize = 12,
            maxSize = 12
    )
    public ResponseEntity<PageSortRequest> getPageRequestNoSort(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);

        return ResponseEntity.ok(pageSortRequest);
    }

    @GetMapping("/invalid-sort")
    @PageSortConfig(
            defaultSize = 12,
            maxSize = 12,
            validSortFields = {"name", "days"},
            defaultSortBy = "invalid" // This should trigger a validation error
    )
    public ResponseEntity<PageSortRequest> getPageRequestInvalidSort(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);

        return ResponseEntity.ok(pageSortRequest);
    }

    private static void logPageRequest(PageSortRequest pageSortRequest) {
        log.info("Received request with page={}, size={}, sortBy={}, sortDir={}",
                pageSortRequest.page(), pageSortRequest.size(),
                pageSortRequest.sortBy(), pageSortRequest.sortDir());
    }
}