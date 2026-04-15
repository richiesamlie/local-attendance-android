# SECURITY_CRYPTO_UPGRADE Instructions

## Upgrading from androidx.security:security-crypto 1.1.0-alpha06 to 1.1.0

### Dependency Changes
1. **Update the dependency in your `build.gradle` file:**
   ```groovy
   dependencies {
       implementation 'androidx.security:security-crypto:1.1.0'
   }
   ```

### Version Verification
1. **Check the version in your `build.gradle` file** to ensure the upgrade:
   - Ensure that `androidx.security:security-crypto:1.1.0` is the only version listed.
2. **Sync your project** to ensure the new dependency is properly recognized.

### Testing Procedures
1. **Run your test suite:**
   - Use the command:  
     ```shell
     ./gradlew test
     ```  
   - Ensure all unit tests pass without issues.
2. **Perform manual testing:**
   - Verify all functionalities that use the security-crypto library to ensure compliance and correctness. 

### Verification Commands
1. **List all dependencies to confirm the upgrade:**
   ```shell
   ./gradlew dependencies
   ```
   - Look for `androidx.security:security-crypto:1.1.0` in the output list.
2. **Check for any deprecation warnings or errors** in your IDE or command line output after the upgrade.

## Conclusion
After completing the above steps, your upgrade from `1.1.0-alpha06` to `1.1.0` should be verified and functional. If any issues arise, refer to the [official androidx documentation](https://developer.android.com/jetpack/androidx/releases/security-crypto) for troubleshooting guidance.
