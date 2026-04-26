# ─────────────────────────────────────────────────────────────────────────────
# Grab Gully — ProGuard / R8 rules
# app/proguard-rules.pro
# ─────────────────────────────────────────────────────────────────────────────

# ── Android standard ─────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# ── Kotlinx Serialization ─────────────────────────────────────────────────────
# Required for all @Serializable data classes
-keepclassmembers,allowobfuscation class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep @kotlinx.serialization.Serializable class com.grabgully.app.** { *; }
-keepnames class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.json.** { *; }

# ── Retrofit 2 ────────────────────────────────────────────────────────────────
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Run: npm install playwright && npx playwright install chromium
Unit
-dontwarn retrofit2.-KotlinExtensions
-dontwarn retrofit2.adapter.rxjava3.**

# ── OkHttp ────────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okhttp3.internal.** { *; }
-keep interface okhttp3.** { *; }

# ── Room ──────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# ── Hilt / Dagger ────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ── Coil 3 ────────────────────────────────────────────────────────────────────
-dontwarn coil3.**

# ── CueLinks SDK ─────────────────────────────────────────────────────────────
# Keep all CueLinks classes to prevent affiliate interception from breaking
-keep class com.cuelinks.** { *; }
-dontwarn com.cuelinks.**

# ── Supabase / Ktor ───────────────────────────────────────────────────────────
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# ── Firebase / FCM ────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }

# ── Vico (Charts) ─────────────────────────────────────────────────────────────
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# ── Data models — keep for Retrofit JSON deserialization ─────────────────────
-keep class com.grabgully.app.data.model.** { *; }
-keep class com.grabgully.app.data.api.** { *; }

# ── Crash reporting source maps ───────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-printmapping build/outputs/mapping/release/mapping.txt
