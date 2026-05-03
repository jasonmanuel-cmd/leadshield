# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep app-specific classes
-keep class com.mctb.autoreply.data.** { *; }
-keep class com.mctb.autoreply.receiver.** { *; }
-keep class com.mctb.autoreply.service.** { *; }
-keep class com.mctb.autoreply.util.** { *; }

# Keep BroadcastReceiver classes
-keep public class * extends android.content.BroadcastReceiver

# Keep Service classes
-keep public class * extends android.app.Service

# Keep navigation and sealed classes
-keep class com.mctb.autoreply.ui.navigation.** { *; }

# Enable optimizations but keep debug info
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
