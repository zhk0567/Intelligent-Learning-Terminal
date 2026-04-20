# HeritageMusic — release 混淆与压缩补充规则

# --- Kotlin ---
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# --- Retrofit / OkHttp ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# --- Gson（Retrofit GsonConverterFactory）---
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# 网络 DTO 与接口实现（反序列化需要字段名）
-keep class com.intangibleheritage.music.core.network.** { *; }

# --- AndroidX / Compose / Navigation ---
-keep class androidx.navigation.** { *; }
-keep class androidx.compose.** { *; }

# --- DataStore ---
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# --- Media3 / ExoPlayer ---
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# --- Coil ---
-keep class coil.** { *; }

# --- 应用入口 ---
-keep class com.intangibleheritage.music.HeritageApplication { *; }
-keep class com.intangibleheritage.music.MainActivity { *; }
