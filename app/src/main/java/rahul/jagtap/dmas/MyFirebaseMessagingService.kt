package rahul.jagtap.dmas

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import rahul.jagtap.dmas.utils.Utils
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"
    var app: App? = null
    var mContext: Context? = null

    override fun onNewToken(s: String) {
        Log.e("fcm token", s)
        app = application as App
        if (!TextUtils.isEmpty(s)) {
            app?.preferences?.token = s
            if (app?.preferences?.loggedInUser?.isAdmin == "1") {
                Firebase.database.reference.child(Utils.FCM_TOKEN_TABLE).child("admin").setValue(app?.preferences?.token
                    ?: "")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        app = application as App
        mContext = applicationContext
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification?.body)
            val data = remoteMessage.data
            Log.d(TAG, "Map data: $data")
            sendNotification(remoteMessage.notification?.body, mContext?.getString(R.string.app_name).toString(), data)
        }
    }

    private fun sendNotification(message: String?, title: String, data: Map<String, String>?) {
        val intent: Intent
        if (app?.preferences?.isLoggedInUser == true) {
            intent = Intent(this, MainActivity::class.java)
            if (data != null) {
                intent.putExtra("type", data["type"])
                intent.putExtra("msg", message)
            }
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
        val n: Notification
        val notificationBuilder = NotificationCompat.Builder(this, "id_my_accountant")
        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setLargeIcon(largeIcon)
            //            notificationBuilder.setSmallIcon(R.drawable.ic_notification_logo);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        } else {
            //            notificationBuilder.setSmallIcon(R.drawable.ic_notification_logo);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }
        notificationBuilder.setContentText(message)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.priority = Notification.PRIORITY_MAX
        notificationBuilder.setVibrate(longArrayOf(1000, 1000))
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
        notificationBuilder.setContentTitle(resources.getString(R.string.app_name))
        notificationBuilder.setContentIntent(pendingIntent)
        n = notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message)).build()
        n.flags = n.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)
        //Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            val random = Random()
            val m = random.nextInt(9999 - 1000) + 1000
            notificationManager.notify(m /* ID of notification */, notificationBuilder.build())
        }
    }
}