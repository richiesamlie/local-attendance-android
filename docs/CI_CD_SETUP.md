# CI/CD Setup for Local Attendance Android

## GitHub Actions Workflow Configuration

To set up continuous integration and continuous deployment (CI/CD) for your Local Attendance Android project using GitHub Actions, create a new workflow file in the `.github/workflows` directory.

### Workflow File Example

Create a file named `ci_cd_workflow.yml` in the `.github/workflows` directory:
```yaml
name: CI/CD Pipeline

on:
  push:
    branches:
      - main
  pull_request:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Setup JDK
        uses: actions/setup-java@v1
        with:
          java-version: '11'

      - name: Build with Gradle
        run: ./gradlew build

  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Setup JDK
        uses: actions/setup-java@v1
        with:
          java-version: '11'

      - name: Run tests
        run: ./gradlew test

  deploy:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Deploy to Firebase
        uses: wzieba/Firebase-Distribution-Github-Action@v1
        with:
          appId: ${{ secrets.FIREBASE_APP_ID }}
          token: ${{ secrets.FIREBASE_TOKEN }}
          groups: testers
          file: app/build/outputs/apk/release/app-release.apk

```

## Setup Instructions

1. **Create a GitHub Repository**: Ensure your project is in a GitHub repository.
2. **Add Secrets**: Go to your repository settings and add Firebase tokens and IDs as secrets (`FIREBASE_APP_ID`, `FIREBASE_TOKEN`).
3. **Create Workflow File**: Save the above YAML configuration in `.github/workflows/ci_cd_workflow.yml`.

## Build Automation

The CI build process is triggered on every push to the main branch. The workflow builds the Android application using Gradle.

## Testing Automation

Tests are automatically run after a successful build ensuring that no broken code is merged into the main branch.

## Deployment Procedures

Once tests pass, the application is automatically deployed to Firebase App Distribution for testing. Make sure to set up the appropriate access groups in Firebase.

---
This CI/CD setup provides a robust way to handle the development lifecycle of your Local Attendance Android application, ensuring quality and efficiency in your releases.