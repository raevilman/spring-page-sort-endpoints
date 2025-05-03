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
    void defaultPageAndSizeValues() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(12));
    }

    @Test
    void customPageAndSizeValues() throws Exception {
        mockMvc.perform(get("/items?page=2&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void sortByNameAscending() throws Exception {
        mockMvc.perform(get("/items?sortBy=name&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(12))
                .andExpect(jsonPath("$.sortBy").value("name"))
                .andExpect(jsonPath("$.sortDir").value("asc"));
    }

    @Test
    void sortByDaysDescending() throws Exception {
        mockMvc.perform(get("/items?sortBy=days&sortDir=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(12))
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
    void veryLargeSizeShouldBeCapped() throws Exception {
        mockMvc.perform(get("/items?size=1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Page size cannot be greater than 12"));
    }

    @Test
    void negativePageShouldBeHandledGracefully() throws Exception {
        mockMvc.perform(get("/items?page=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Page number cannot be less than 0"));
    }

    @Test
    void zeroOrNegativeSizeShouldBeHandledGracefully() throws Exception {
        mockMvc.perform(get("/items?size=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Page size cannot be less than 1"));
    }

    @Test
    void defaultSortShouldWorkWhenOnlySizeProvided() throws Exception {
        mockMvc.perform(get("/items?size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
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
    void nonNumericPageParameterShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items?page=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid page parameter: abc"));
    }

    @Test
    void nonNumericSizeParameterShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items?size=abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid size parameter: abc"));
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
    void parameterOrderShouldNotMatter() throws Exception {
        mockMvc.perform(get("/items?sortDir=desc&size=5&sortBy=days&page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.sortBy").value("days"))
                .andExpect(jsonPath("$.sortDir").value("desc"));
    }

    @Test
    void multipleValuesForSameParameterShouldUseFirst() throws Exception {
        mockMvc.perform(get("/items?page=1&page=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1));
    }
}