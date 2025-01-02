

# Keep the entry points of the application.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.core.app.NotificationCompat$Style
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends androidx.appcompat.app.ActionBar
-keep public class * extends androidx.appcompat.widget.Toolbar
-keep public class * extends androidx.lifecycle.ViewModel
-keep public class * extends androidx.lifecycle.AndroidViewModel

# Keep the classes and methods used by the Android Support Library.
-dontwarn android.support.**
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-dontoptimize

# Some methods are only called from tests, so make sure the shrinker keeps them.


-keep class androidx.drawerlayout.widget.DrawerLayout { *; }
-keep class androidx.test.espresso.**


-keep class android.arch.** { *; }

# Proguard rules that are applied to your test apk/code.
-ignorewarnings

-keepattributes *Annotation*

-dontnote junit.framework.**
-dontnote junit.runner.**

-dontwarn androidx.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
# Uncomment this if you use Mockito
#-dontwarn org.mockito.**

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {*;
}
# Keep custom model classes
-keep class com.casecode.pos.core.model.data.** { *; }
-keep class com.casecode.pos.core.firebase.services.model.** { *; }
