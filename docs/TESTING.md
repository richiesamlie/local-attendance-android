# Testing Guide

This document provides a comprehensive guide on the various types of testing used in the project, including unit testing, integration testing, UI testing, as well as the overall test structure. Additionally, you will find instructions on how to run tests and generate coverage reports.

## Table of Contents
1. [Unit Testing](#unit-testing)
2. [Integration Testing](#integration-testing)
3. [UI Testing](#ui-testing)
4. [Test Structure](#test-structure)
5. [Running Tests](#running-tests)
6. [Coverage Reports](#coverage-reports)

## Unit Testing

Unit testing focuses on testing individual components or functions in isolation. In this project, the following practices should be followed:
- Ensure that every function has corresponding unit tests.
- Use mocking for external dependencies to isolate the unit being tested.
- Follow the Arrange-Act-Assert pattern for clear test structure.

### Example
```java
@Test
public void testExampleFunction() {
    // Arrange
    int expected = 10;
    int input = 5;

    // Act
    int result = exampleFunction(input);

    // Assert
    assertEquals(expected, result);
}
```

## Integration Testing

Integration testing focuses on verifying that different components work together correctly. Important guidelines include:
- Test the integration points between modules.
- Use real instances of database or web service when necessary, but consider using a staging environment.

### Example
```java
@Test
public void testIntegrationOfModuleAAndB() {
    // Code to test integrated modules
}
```

## UI Testing

UI testing ensures that the user interface behaves as expected. This can be achieved using tools such as Espresso or UI Automator. Testing should cover:
- User flows
- Button clicks
- Data entry forms

### Example
```java
@Test
public void testButtonClick() {
    onView(withId(R.id.button)).perform(click());
    onView(withId(R.id.textView)).check(matches(withText("Button Clicked")));
}
```

## Test Structure

The project should follow a structured approach to organizing tests:
- Place unit tests in the `src/test/java` directory.
- Place integration tests in the `src/integrationTest/java` directory.
- Place UI tests in the `src/androidTest/java` directory.

## Running Tests

To run the tests, use the following command commands:
- **Unit tests:** `./gradlew test`
- **Integration tests:** `./gradlew integrationTest`
- **UI tests:** `./gradlew connectedAndroidTest`

## Coverage Reports

To generate coverage reports, ensure that the following plugin is applied in your `build.gradle` file:
```groovy
apply plugin: 'jacoco'
```

Run the tests with coverage by using:
```bash
./gradlew test jacocoTestReport
```

This will generate a coverage report that can be found in `build/reports/jacoco/test/html`.

---

For any issues related to testing, please refer to the documentation or reach out to the team.