# PROGUARD_SETUP.md

## Comprehensive ProGuard Configuration Details

### 1. Introduction to ProGuard
ProGuard is a Java class file shrinker, optimizer, and obfuscator. This helps reduce the size of your Android application while enhancing security.

### 2. Step-by-Step Instructions
#### Step 1: Enable ProGuard
1. In Android Studio, navigate to your `build.gradle` file under the app module.
2. Set the following flag within the `buildTypes` block:
   ```groovy
   buildTypes {
       release {
           minifyEnabled true
           proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
       }
   }
   ```

#### Step 2: Configure `proguard-rules.pro`
- Specify the classes and methods to keep from obfuscation.
  ```
  -keep public class * extends android.app.Activity
  -keep public class * extends android.app.Service
  ```

### 3. Minification Setup
- Minification reduces the size of your APK.  Ensure that `minifyEnabled` is set to true in the build configuration.

### 4. Mapping File Management
- The mapping file is generated during the build process and is crucial for understanding stack traces. Ensure to keep it secured and backed up.
- It can be found in `app/build/outputs/mapping/release/mapping.txt`.

### 5. Testing Procedures
- After configuration, run tests to ensure your app behaves as expected:
  - Perform a build and install the release APK on a device.
  - Conduct functional and unit tests to ensure everything works.

### 6. Troubleshooting
- If the app crashes after ProGuard is enabled, check the mapping file. Use it to decode stack traces for identifying the problem.
- Adjust your `proguard-rules.pro` based on what you're observing.

### 7. Common Issues
- **Issue:** ClassNotFoundException after minification.
  **Solution:** Ensure to use the `-keep` options in your ProGuard rules for critical classes.

- **Issue:** Obfuscated method names affect reflection calls.
  **Solution:** Explicitly keep those method names using the `-keepclassmembers` rule in your `proguard-rules.pro`

### Conclusion
Following these steps will help you successfully integrate ProGuard into your Android application, ensuring size optimization and added security.