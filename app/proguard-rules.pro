
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {*;
}
# Keep custom model classes
-keep class com.casecode.pos.core.model.** { *; }
-keep class com.casecode.pos.core.firebase.model.** { *; }

# Keep iText and BouncyCastle classes for PDF generation
-keep class com.itextpdf.bouncycastle.** { *; }
-keep class com.itextpdf.bouncycastlefips.** { *; }
-keep class com.itextpdf.commons.bouncycastle.** { *; }
-keep class com.itextpdf.kernel.** { *; }
-keep class com.itextpdf.layout.** { *; }
-keep class com.itextpdf.io.** { *; }

-dontwarn org.slf4j.impl.StaticLoggerBinder
-keep class com.revenuecat.purchases.** { *; }

# Fix R8 missing classes errors for iText
-dontwarn com.itextpdf.bouncycastle.**
-dontwarn com.itextpdf.bouncycastlefips.**
-dontwarn java.awt.**
-dontwarn javax.imageio.**
