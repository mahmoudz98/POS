
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {*;
}
# Keep custom model classes
-keep class com.casecode.pos.core.model.** { *; }
-keep class com.casecode.pos.core.firebase.model.** { *; }

-dontwarn org.slf4j.impl.StaticLoggerBinder
-keep class com.revenuecat.purchases.** { *; }
