package com.therdnotes.pagesorttester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class TesterApplication {

    public static void main(String[] args) {
        log.info("Starting Page Sort Tester Application");
        SpringApplication.run(TesterApplication.class, args);
        log.info("Page Sort Tester Application started successfully");
    }
}