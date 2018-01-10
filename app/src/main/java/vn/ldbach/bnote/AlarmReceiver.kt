package vn.ldbach.bnote

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log

/**
 * Created by Duy-Bach on 1/7/2018.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Log.d("b-note", "Alarm Receiver")

        if (intent == null) {
            Log.d("b-note", "Intent is null")
            return
        }

        Log.d("b-note", intent.action)
        val bundle = intent.getBundleExtra("bundle")
        val item = bundle.getSerializable("note_item") as? TextNoteItem

        if (item == null) {
            Log.d("b-note", "Extra is null")
            return
        }

        val storage = NoteDataStorage()

        // Create notification
        val bitmap: Bitmap? = storage.loadImage(context!!, item.imageName)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationActivity = Intent(context, NotifyActivity::class.java)
        notificationActivity.putExtra("bundle", bundle)


        notificationActivity.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingActivity = PendingIntent.getActivity(
                context,
                item.uuid.hashCode(),
                notificationActivity,
                PendingIntent.FLAG_UPDATE_CURRENT)


        @Suppress("DEPRECATION")
        val notifyBuilder = NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_photo_white_36dp)
                .setContentTitle(item.header)
                .setContentText(item.content)
                .setSound(alarmSound)
                .setContentIntent(pendingActivity)


        if (bitmap != null) {
            notifyBuilder.setLargeIcon(bitmap).
                    setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.notify(System.currentTimeMillis().toInt(), notifyBuilder.build())

    }
}