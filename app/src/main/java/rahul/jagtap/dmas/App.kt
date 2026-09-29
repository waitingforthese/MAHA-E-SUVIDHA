package rahul.jagtap.dmas

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import rahul.jagtap.dmas.api.ApiRequestHelper
import rahul.jagtap.dmas.preferences.Preferences

class App : Application() {
    @get:Synchronized
    var preferences: Preferences? = null
        private set

    @get:Synchronized
    var apiRequestHelper: ApiRequestHelper? = null
        private set

    override fun onCreate() {
        super.onCreate()
        ViewPump.init(ViewPump.builder().addInterceptor(CalligraphyInterceptor(CalligraphyConfig.Builder().setDefaultFontPath("fonts/SourceSansPro-Regular.ttf").setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath).build())).build())
        doInit()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        if (BuildConfig.DEBUG) {
            Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this)).enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build())
        }
        //Android 8.0
        createNotificationChannelForOreo()

        FirebaseApp.initializeApp(this)
        setupActivityListener() //        FirebaseInstallations.getInstance().id.addOnCompleteListener { task: Task<String?> ->
//            if (task.isSuccessful) {
//                val token = task.result
//                Log.e("token ---->>", token!!)
//                preferences?.token = token
//                if (preferences?.loggedInUser?.isAdmin == "1") {
//                    Firebase.database.reference.child(Utils.FCM_TOKEN_TABLE).child("admin").setValue(preferences?.token
//                        ?: "")
//                }
//            }
//        }
    }

    private fun setupActivityListener() {
//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
//            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
//                p0.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
//            }
//
//            override fun onActivityStarted(p0: Activity) {
//            }
//
//            override fun onActivityResumed(p0: Activity) {
//            }
//
//            override fun onActivityPaused(p0: Activity) {
//            }
//
//            override fun onActivityStopped(p0: Activity) {
//            }
//
//            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
//            }
//
//            override fun onActivityDestroyed(p0: Activity) {
//            }
//        })
    }

    private fun doInit() {
        preferences = Preferences(this)
        apiRequestHelper = ApiRequestHelper.init(this)
    }

    fun createNotificationChannelForOreo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val id = "id_my_accountant"
            // The user-visible name of the channel.
            val name: CharSequence = "App Updates"
            // The user-visible description of the channel.
            val description = "Notifications regarding our app"
            val importance = NotificationManager.IMPORTANCE_MAX
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.RED
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}