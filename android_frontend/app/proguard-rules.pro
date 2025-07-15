# Proguard rules for Note Keeper - minimal configuration
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.json.**
-keep class com.example.androidfrontend.** { *; }
-keep class org.json.** { *; }
-keep class okhttp3.** { *; }
