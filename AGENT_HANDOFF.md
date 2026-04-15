# Agent Handoff Details

## ProGuard Setup
ProGuard is enabled for release builds (`minifyEnabled true` in build.gradle). The proguard-rules.pro file should contain:
- Keep all model classes and any other classes that should not be obfuscated.
- Add rules to avoid stripping critical libraries or methods.

## Security-Crypto Upgrade
The project uses `androidx.security:security-crypto:1.1.0` for encrypted SharedPreferences.
No upgrade to Google Tink is required - the current solution meets security requirements.

## Test Coverage
Target 80% code coverage. To check coverage:
- Run `./gradlew testDebugUnitTest jacocoTestReport --no-daemon`
- Review `app/build/reports/jacoco/testDebugUnitTest jacocoTestReport/html/index.html`

## Documentation
All new features and changes should be documented in the `/docs` directory using Markdown format.
- Include examples and usage scenarios.
- Update relevant docs (ARCHITECTURE.md, TESTING.md, etc.)

## CI/CD Automation
GitHub Actions workflow is at `.github/workflows/android.yml`:
- Uses actions/checkout@v4 and actions/setup-java@v4
- Java 17 (temurin distribution)
- Runs build and generates coverage reports on push/PR to main