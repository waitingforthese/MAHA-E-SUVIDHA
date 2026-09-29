package rahul.jagtap.dmas

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class SubscribeReminderWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Show the subscription popup
        val applicationContext = context.applicationContext as App
        applicationContext.preferences?.setShouldShowPopup(true)
        return Result.success()
    }
}