# Agent Handoff Details

## ProGuard Setup
To optimize the APK and obfuscate the code, ensure the following is included in your `proguard-rules.pro` file:
- Keep all model classes and any other classes that should not be obfuscated.
- Add rules to avoid stripping critical libraries or methods.

## Security-Crypto Upgrade
We have updated the security-crypto library to ensure compliance with the latest security standards. Follow these steps to upgrade:
1. Open your `build.gradle` file and modify the dependency:
   ```groovy
   implementation 'com.google.crypto.tink:tink-android:latest-version'
   ```
2. Review the [Tink documentation](https://github.com/google/tink) for any changes in usage.

## Test Coverage
Ensure that your tests cover at least 80% of the codebase. To check coverage, use the following commands:
- Configure your testing environment to report coverage.
- Execute the tests with coverage metrics and review the report.

## Documentation
Make sure all new features and changes are documented in the repository Wiki. Use Markdown format for clarity and readability.
- Include examples and usage scenarios.

## CI/CD Automation
To streamline our integration and deployment process:
1. Set up GitHub Actions for continuous integration:
   - Create a `.github/workflows/ci.yml` file.
   - Configure build and test steps in the YAML file.
2. Implement automated deployment processes for staging and production environments.

For any questions, please contact the development team.