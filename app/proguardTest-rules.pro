# Proguard rules that are applied to your test apk/code.
-ignorewarnings
-dontoptimize

-keepattributes *Annotation*

-keep class androidx.test.espresso.**
# keep the class and specified members from being removed or renamed


-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn androidx.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter