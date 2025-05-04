# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).



## [0.1.1] - 2025-05-04

### Added
- Support for default sort field configuration with `defaultSortBy` attribute in `@PageSortConfig`
- Compile-time validation to ensure `defaultSortBy` is one of the configured `validSortFields`
- Friendly advice when `validSortFields` are configured but `defaultSortBy` is not set

## [0.1.0] - 2025-05-03

### Added
- Initial release of Spring Page Sort Endpoints library
- `PageSortRequest` record for handling pagination and sorting parameters
- Auto-configuration for Spring Boot applications
- Argument resolver for request parameters
- Exception handling with configurable response format
- Default values (page=0, size=25, sortDir=asc)
- Support for customizable validation

### Dependencies
- Spring Boot 3.4.3
- Java 17

[Unreleased]: https://github.com/raevilman/spring-page-sort-endpoints/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/raevilman/spring-page-sort-endpoints/releases/tag/v0.1.0