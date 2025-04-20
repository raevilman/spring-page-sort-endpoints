package com.therdnotes.pagesorttester;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.therdnotes.springpagesort.PageSortConfig;
import com.therdnotes.springpagesort.PageSortRequest;

import java.util.Set;

@RestController
@RequestMapping("/api/months")
@RequiredArgsConstructor
@Slf4j
public class MonthNamesController {

    private final MonthNamesService monthNamesService;

    /**
     * Retrieves month names with pagination.
     *
     * @param pageSortRequest the pagination and sorting request parameters
     * @return a paginated list of month names
     */
    @GetMapping
    @PageSortConfig(
            defaultSize = 12,
            maxSize = 12,
            validSortFields = {"name", "days"}
    )
    public ResponseEntity<Set<MonthNamesService.MonthInfo>> getMonthNames(PageSortRequest pageSortRequest) {
        log.info("Received request for month names with page={}, size={}, sortBy={}, sortDir={}",
                pageSortRequest.page(), pageSortRequest.size(),
                pageSortRequest.sortBy(), pageSortRequest.sortDir());

        Set<MonthNamesService.MonthInfo> result = monthNamesService.getMonthNames(pageSortRequest);

        log.debug("Returning {} month entries for page {}", result.size(), pageSortRequest.page());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/err")
    public ResponseEntity<String> getError() {
        log.error("This is an error endpoint");
        throw new IllegalArgumentException("This is an error endpoint");
    }
}