# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.2] - 2025-07-14

### Changed
- **BREAKING CHANGE**: Replaced `page` and `size` parameters with `offset` and `limit` for more precise pagination control
- Updated `PageSortRequest` record to use `offset` and `limit` fields instead of `page` and `size`
- Updated `@PageSortConfig` annotation attributes:
  - `defaultPage` → `defaultOffset`
  - `defaultSize` → `defaultLimit`
  - `minPage` → `minOffset`
  - `minSize` → `minLimit`
  - `maxSize` → `maxLimit`
- Updated query parameter names in REST endpoints to use `offset` and `limit`
- Updated all documentation and examples to reflect the new parameter names

### Migration Guide
To upgrade from 0.1.1 to 0.1.2:
1. Update query parameters: `?page=1&size=10` → `?offset=10&limit=10`
2. Update annotation attributes in `@PageSortConfig`
3. Update code accessing `PageSortRequest` fields: `.page()` → `.offset()`, `.size()` → `.limit()`

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

[Unreleased]: https://github.com/raevilman/spring-page-sort-endpoints/compare/v0.1.2...HEAD
[0.1.2]: https://github.com/raevilman/spring-page-sort-endpoints/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/raevilman/spring-page-sort-endpoints/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/raevilman/spring-page-sort-endpoints/releases/tag/v0.1.0