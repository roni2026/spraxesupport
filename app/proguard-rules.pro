# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# kotlinx.serialization
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.spraxe.support.**$$serializer { *; }
-keepclassmembers class com.spraxe.support.** { *** Companion; }
-keepclasseswithmembers class com.spraxe.support.** { kotlinx.serialization.KSerializer serializer(...); }

# Supabase / Ktor
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.**
