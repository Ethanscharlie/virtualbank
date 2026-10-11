~/.android-cli/bin/android emulator start medium_phone
./gradlew assembleDebug
~/.android-cli/bin/android run --apks app/build/outputs/apk/debug/app-debug.apk
