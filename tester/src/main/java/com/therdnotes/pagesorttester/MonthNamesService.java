package com.therdnotes.pagesorttester;

import com.therdnotes.springpagesort.PageSortRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MonthNamesService {

    private final List<MonthInfo> monthInfoList;

    public MonthNamesService() {
        log.info("Initializing MonthNamesService with month information");
        // Using ArrayList for efficient sorting
        this.monthInfoList = Arrays.stream(Month.values())
                .map(month -> {
                    String name = month.name().charAt(0) + month.name().substring(1).toLowerCase();
                    int days = month.length(Year.now().isLeap());
                    return new MonthInfo(name, days);
                })
                .collect(Collectors.toList());
        log.debug("Initialized month info: {}", monthInfoList);
    }

    /**
      * Returns a subset of month information based on pagination and sorting parameters.
      *
      * @param pageSortRequest the pagination and sorting request parameters
      * @return a subset of month information for the requested page
      */
    public Set<MonthInfo> getMonthNames(PageSortRequest pageSortRequest) {
        int page = pageSortRequest.page();
        int size = pageSortRequest.size();
        String sortBy = pageSortRequest.sortBy();
        String sortDir = pageSortRequest.sortDir();

        log.debug("Retrieving month info for page {} with size {}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        // Create a sorted copy based on the sort criteria
        List<MonthInfo> sortedList = new ArrayList<>(monthInfoList);

        if (sortBy != null && !sortBy.isEmpty()) {
            boolean isAscending = sortDir == null || "asc".equalsIgnoreCase(sortDir);

            if ("name".equalsIgnoreCase(sortBy)) {
                log.debug("Sorting by name, ascending: {}", isAscending);
                sortedList.sort(isAscending ?
                        Comparator.comparing(MonthInfo::name) :
                        Comparator.comparing(MonthInfo::name).reversed());
            } else if ("days".equalsIgnoreCase(sortBy)) {
                log.debug("Sorting by days, ascending: {}", isAscending);
                sortedList.sort(isAscending ?
                        Comparator.comparingInt(MonthInfo::days) :
                        Comparator.comparingInt(MonthInfo::days).reversed());
            }
        }

        int fromIndex = page * size;
        if (fromIndex >= sortedList.size()) {
            log.warn("Requested page {} exceeds available data, returning empty set", page);
            return Set.of();
        }

        int toIndex = Math.min(fromIndex + size, sortedList.size());
        log.debug("Fetching items from index {} to {}", fromIndex, toIndex);

        return new LinkedHashSet<>(sortedList.subList(fromIndex, toIndex));
    }

    /**
     * Record to hold month information
     */
    public record MonthInfo(String name, int days) {
        @Override
        public String toString() {
            return name + " (" + days + " days)";
        }
    }
}