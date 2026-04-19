# LiteRT-LM — keep all inference engine classes
-keep class com.google.ai.edge.litertlm.** { *; }
-dontwarn com.google.ai.edge.litertlm.**

# Keep BLE callback classes (referenced by Android framework via reflection)
-keep class com.cropdoc.app.data.ble.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Coil image loader
-keep class coil.** { *; }
-dontwarn coil.**

# Keep data model classes (used in JSON serialization)
-keep class com.cropdoc.app.data.model.** { *; }
