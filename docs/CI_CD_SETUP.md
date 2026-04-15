# CI/CD Setup for Local Attendance Android

## GitHub Actions Workflow Configuration

To set up continuous integration and continuous deployment (CI/CD) for your Local Attendance Android project using GitHub Actions, create a new workflow file in the `.github/workflows` directory.

### Workflow File Example

Create a file named `android.yml` in the `.github/workflows` directory:
```yaml
name: Android CI

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

env:
  FORCE_JAVASCRIPT_ACTIONS_TO_NODE24: true

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v4

    - name: set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build with Gradle
      run: ./gradlew clean assembleDebug --no-daemon

    - name: Generate Jacoco Test Coverage Report
      run: ./gradlew testDebugUnitTest jacocoTestReport --no-daemon

    - name: Upload Coverage Report
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: coverage-report
        path: app/build/reports/jacoco/testDebugUnitTest jacocoTestReport/html/index.html

    - name: Upload Debug APK
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## Setup Instructions

1. **GitHub Repository**: Already set up at `https://github.com/richiesamlie/local-attendance-android`
2. **Workflow File**: Already configured at `.github/workflows/android.yml`
3. **Optional Secrets**: Add Firebase tokens for deployment (`FIREBASE_APP_ID`, `FIREBASE_TOKEN`)

## Build Automation

The CI build process is triggered on every push to the main branch. The workflow builds the Android application using Gradle and generates test coverage reports.

## Testing Automation

Tests are automatically run with coverage reports generated via JaCoCo. Reports are uploaded as artifacts for review.

## Deployment (Optional)

For Firebase deployment, add secrets and extend the workflow:
- `FIREBASE_APP_ID`: Firebase App ID
- `FIREBASE_TOKEN`: Firebase token

---
This CI/CD setup provides a robust way to handle the development lifecycle of your Local Attendance Android application, ensuring quality and efficiency in your releases.