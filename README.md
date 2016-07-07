# atmosphere-ime
This is a simple implementation of an input keyboard for Android, part of the ATMOSPHERE mobile testing framework. It is needed by tests that require text input or other text operations, such as cut, copy, select, etc.

## Project setup
>This project depends on the [atmosphere-commons](https://github.com/MusalaSoft/atmosphere-commons) libraries, so make sure you publish atmosphere-commons to your local Maven repository first. You can find instructions on how to do that by following the link.

### Android SDK
This is an Android project, so you will need to have a local Android SDK installation. You can find more info on how to setup the Android SDK [here][1]. Note that if you plan to work on the atmosphere-ime you can download the whole Android Studio + Android SDK bundle.

### Build the project
You can build the project using the included gradle wrapper.

* On Windows run `gradlew build` in the project root directory
* On Linux/macOS run `./gradlew build` in the project root directory

### Publish to Maven Local
If the build is successful, also run

* `./gradlew publishToMavenLocal` (Linux/macOS) or
* `gradlew publishToMavenLocal` (Windows)

to publish the APK file to the local Maven repository so other projects that depend on it can use it.

[1]: https://github.com/MusalaSoft/atmosphere-docs/blob/master/setup/android_sdk.md
