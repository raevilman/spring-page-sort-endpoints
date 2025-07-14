package com.therdnotes.springpagesort;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class PageSortEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void defaultOffsetAndLimitValues() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.limit").value(12));
    }

    @Test
    void customOffsetAndLimitValues() throws Exception {
        mockMvc.perform(get("/items?offset=2&limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(2))
                .andExpect(jsonPath("$.limit").value(5));
    }

    @Test
    void sortByNameAscending() throws Exception {
        mockMvc.perform(get("/items?sortBy=name&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.limit").value(12))
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.sortDir").value("asc"));
    }

    @Test
    void sortByDaysDescending() throws Exception {
        mockMvc.perform(get("/items?sortBy=days&sortDir=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.limit").value(12))
                .andExpect(jsonPath("$.sortBy").value("days"))
                .andExpect(jsonPath("$.sortDir").value("desc"));
    }

    @Test
    void invalidSortFieldShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items?sortBy=invalid&sortDir=asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid sort field: invalid. Valid options are: name, days"));
    }

    @Test
    void invalidSortDirectionShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items?sortBy=name&sortDir=invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid sort direction: invalid. Valid options are: asc, desc"));
    }

    @Test
    void veryLargeLimitShouldBeCapped() throws Exception {
        mockMvc.perform(get("/items?limit=1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Limit cannot be greater than 12"));
    }

    @Test
    void negativeOffsetShouldBeHandledGracefully() throws Exception {
        mockMvc.perform(get("/items?offset=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Offset cannot be less than 0"));
    }

    @Test
    void zeroOrNegativeLimitShouldBeHandledGracefully() throws Exception {
        mockMvc.perform(get("/items?limit=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Limit cannot be less than 1"));
    }

    @Test
    void defaultSortShouldWorkWhenOnlyLimitProvided() throws Exception {
        mockMvc.perform(get("/items?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.limit").value(5))
                .andExpect(jsonPath("$.sortBy").doesNotExist());
    }

    @Test
    void sortByValidFieldButNoDirectionShouldUseDefaultAscending() throws Exception {
        mockMvc.perform(get("/items?sortBy=name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.sortDir").value("asc"));
    }

    @Test
    void nonNumericOffsetParameterShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items?offset=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid offset parameter: abc"));
    }

    @Test
    void nonNumericLimitParameterShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items?limit=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid limit parameter: abc"));
    }

    @Test
    void emptySortByWithDirectionShouldBeValid() throws Exception {
        mockMvc.perform(get("/items?sortBy=&sortDir=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortBy").isEmpty())
                .andExpect(jsonPath("$.sortDir").value("desc"));
    }

    @Test
    void shouldHandleInsensitiveSortDirection() throws Exception {
        mockMvc.perform(get("/items?sortBy=name&sortDir=DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.sortDir").value("desc"));
    }

    @Test
    void noSortEndpointShouldRejectSorting() throws Exception {
        mockMvc.perform(get("/items/no-sort?sortBy=name"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Sorting is not allowed for this resource"));
    }

    @Test
    void noSortEndpointShouldAcceptPagination() throws Exception {
        mockMvc.perform(get("/items/no-sort?offset=1&limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(1))
                .andExpect(jsonPath("$.limit").value(5))
                .andExpect(jsonPath("$.sortBy").doesNotExist());
    }

    @Test
    void invalidSortEndpointShouldFailValidation() throws Exception {
        mockMvc.perform(get("/items/invalid-sort"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid sort field: invalid. Valid options are: name, days"));
    }

    @Test
    void defaultSortByTest() throws Exception {
        mockMvc.perform(get("/items/default-sort"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$.limit").value(12))
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.sortDir").value("asc"));
    }

    @Test
    void defaultSortByIsOverriddenByRequestParam() throws Exception {
        // Tests that an explicit sortBy parameter overrides the defaultSortBy value
        mockMvc.perform(get("/items/default-sort?sortBy=days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortBy").value("days"));
    }

    @Test
    void emptyRequestParamDoesNotOverrideDefaultSortBy() throws Exception {
        // Tests that an empty sortBy parameter doesn't override defaultSortBy
        mockMvc.perform(get("/items/default-sort?sortBy="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortBy").value("name"));
    }

    @Test
    void defaultSortByCanBeUsedWithCustomSortDir() throws Exception {
        // Tests that defaultSortBy works with a custom sort direction
        mockMvc.perform(get("/items/default-sort?sortDir=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.sortDir").value("desc"));
    }

    @Test
    void parameterOrderShouldNotMatter() throws Exception {
        mockMvc.perform(get("/items?sortDir=desc&limit=5&sortBy=days&offset=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(1))
                .andExpect(jsonPath("$.limit").value(5))
                .andExpect(jsonPath("$.sortBy").value("days"))
                .andExpect(jsonPath("$.sortDir").value("desc"));
    }

    @Test
    void multipleValuesForSameParameterShouldUseFirst() throws Exception {
        mockMvc.perform(get("/items?offset=1&offset=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offset").value(1));
    }


}