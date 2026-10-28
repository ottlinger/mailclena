# Agentic instructions

This file contains instructions for the AI agent to follow when interacting with
the user or in prompt sessions.

## How to build the project

- Run the following command to build the project:

  ```bash
  ./mvnw clean test
  ```

- test coverage must not be less than 85% branch coverage and code quality must
  be at least B (SonarCloud). If these conditions are not met, the build should
  fail.
- integration tests must not fail. If they do, the build should fail.

## Architecture

The application runs as a tool and allows to log into mail accounts and remove all
mails. It is built with Java and Maven.

## Code style

- Avoid star imports in Java files.
- Prefer AssertJ for assertions in tests.
- Configure new dependencies in the `pom.xml` file with a bom reference if possible
  to ensure consistent versions across the project.
- Tests should be written using JUnit 5 and AssertJ and can use TLS-exceptions for
  testing.
- TLS exceptions are permitted only in local GreenMail test sessions. Do not weaken
  TLS verification in production code or modify JVM-wide security settings.
