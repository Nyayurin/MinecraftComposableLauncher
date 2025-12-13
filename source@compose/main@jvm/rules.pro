-dontobfuscate
-dontoptimize

-keep class com.sun.jna.** { *; }
-keep class io.ktor.serialization.kotlinx.** { *; }
-keep class io.ktor.**
-keep class androidx.compose.** { *; }

-dontwarn ch.qos.logback.**
-dontwarn androidx.compose.desktop.DesktopTheme_jvmKt

-dontnote com.sun.jna.**
-dontnote org.freedesktop.dbus.**
-dontnote ch.qos.logback.**
-dontnote io.ktor.**
-dontnote org.slf4j.**