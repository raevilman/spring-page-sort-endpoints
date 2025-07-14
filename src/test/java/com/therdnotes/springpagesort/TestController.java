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
            defaultLimit = 12,
            maxLimit = 12,
            validSortFields = {"name", "days"}
    )
    public ResponseEntity<PageSortRequest> getPageRequest(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);

        return ResponseEntity.ok(pageSortRequest);
    }

    @GetMapping("/default-sort")
    @PageSortConfig(
            defaultLimit = 12,
            maxLimit = 12,
            validSortFields = {"name", "days"},
            defaultSortBy = "name" // This sets a default sort field
    )
    public ResponseEntity<PageSortRequest> getPageRequestDefaultSort(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);
        return ResponseEntity.ok(pageSortRequest);
    }

    @GetMapping("/no-sort")
    @PageSortConfig(
            defaultLimit = 12,
            maxLimit = 12
    )
    public ResponseEntity<PageSortRequest> getPageRequestNoSort(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);

        return ResponseEntity.ok(pageSortRequest);
    }

    @GetMapping("/invalid-sort")
    @PageSortConfig(
            defaultLimit = 12,
            maxLimit = 12,
            validSortFields = {"name", "days"},
            defaultSortBy = "invalid" // This should trigger a validation error
    )
    public ResponseEntity<PageSortRequest> getPageRequestInvalidSort(PageSortRequest pageSortRequest) {
        logPageRequest(pageSortRequest);

        return ResponseEntity.ok(pageSortRequest);
    }

    private static void logPageRequest(PageSortRequest pageSortRequest) {
        log.info("Received request with offset={}, limit={}, sortBy={}, sortDir={}",
                pageSortRequest.offset(), pageSortRequest.limit(),
                pageSortRequest.sortBy(), pageSortRequest.sortDir());
    }
}