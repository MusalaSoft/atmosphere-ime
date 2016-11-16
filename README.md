See our site for better context of this readme. [Click here](http://atmosphereframework.com/)

# atmosphere-ime
This is a simple implementation of an input keyboard for Android, part of the ATMOSPHERE mobile testing framework. It is needed by tests that require text input or other text operations, such as cut, copy, select, etc.

## Project setup

## Android SDK
This is an Android project, so you will need to have a local Android SDK installation (build-tools `v25.0.0` and API 25 are required). You can find more info on how to setup the Android SDK [here][1]. Note that if you plan to work on the atmosphere-ime you can download the whole Android Studio + Android SDK bundle.

## Build the project
You can build the project using the included gradle wrapper.

* On Windows run `gradlew build` in the project root directory
* On Linux/macOS run `./gradlew build` in the project root directory

## Making changes
If you make changes to this project and would like to use your new version in another ATMOSPHERE framework project that depends on this one, after a successful build also run:
* `./gradlew publishToMavenLocal` (Linux/macOS)
* `gradlew publishToMavenLocal` (Windows)

to publish the jar to your local Maven repository. The ATMOSPHERE framework projects are configured to use the artifact published in the local Maven repository first.

[1]: https://github.com/MusalaSoft/atmosphere-docs/blob/master/setup/android_sdk.md
