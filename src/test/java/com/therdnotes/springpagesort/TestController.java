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
        log.info("Received request with page={}, size={}, sortBy={}, sortDir={}",
                pageSortRequest.page(), pageSortRequest.size(),
                pageSortRequest.sortBy(), pageSortRequest.sortDir());

        return ResponseEntity.ok(pageSortRequest);
    }
}