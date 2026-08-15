package com.example.caltrackcoursework.Notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object Notifications
{
	val CHANNEL_ID = "NotificationChannel"

	/*This notification channel is used for my breakfast, lunch and dinner reminder notifciations*/
	fun CreateNotificationChannel(NotificationContext: Context)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			/*This creates my notification channel*/
			val NotificationName = "User notifications"
			val DescriptionText = "This channel is used for all notifications"
			val ImportanceLevel = NotificationManager.IMPORTANCE_DEFAULT
			val AllNotificationsChannel = NotificationChannel(CHANNEL_ID, NotificationName, ImportanceLevel)
			AllNotificationsChannel.description = DescriptionText
			/*This registers my notification channel*/
			val NotificationManager = NotificationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			NotificationManager.createNotificationChannel(AllNotificationsChannel)
		}
	}

	@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
	/*This function is used to display all notifications and takes their title, description and ID as a parameter*/
	fun DisplayNotification(NotificationContext: Context, NotificationTitle: String, NotificationDescription: String, NotificationID: Int)
	{
		val builder = NotificationCompat.Builder(NotificationContext, CHANNEL_ID)
			.setSmallIcon(android.R.drawable.ic_dialog_info)
			.setContentTitle(NotificationTitle)
			.setContentText(NotificationDescription)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
		NotificationManagerCompat.from(NotificationContext).notify(NotificationID, builder.build())
	}

	@RequiresApi(Build.VERSION_CODES.S)
	/*This is used to ensure notifications display at set times, specified using my parameters*/
	fun ScheduleNotification(NotificationContext: Context, NotificationHour: Int, NotificationMinute: Int, RecieverCode: Int, NotificationTitle: String, NotificationText: String)
	{
		/*This is used to allow my notifications to use the alarm service to time my notifications correctly*/
		val NotificationAlarmManager = NotificationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		/*This ensures the notification has the correct title, description and code data*/
		val NotificationIntent = Intent(NotificationContext, NotificationReceiver::class.java).apply{
			putExtra("Title",NotificationTitle)
			putExtra("Description",NotificationText)
			putExtra("ReceiverCode", RecieverCode)
		}
		val NotificationPendingIntent = PendingIntent.getBroadcast(NotificationContext,RecieverCode,NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
		/*This times my notification*/
		val NotificationCalendar = Calendar.getInstance().apply{
			timeInMillis = System.currentTimeMillis()
			set(Calendar.HOUR_OF_DAY, NotificationHour)
			set(Calendar.MINUTE, NotificationMinute)
			set(Calendar.SECOND, 0)
		}
		/*This checks if the time for the notification has already passed, and sets it to display tomorrow if it has*/
		if(NotificationCalendar.timeInMillis <= System.currentTimeMillis())
		{
			NotificationCalendar.add(Calendar.DAY_OF_YEAR, 1)
		}
		/*This lets the notification use the alarm system*/
		if(NotificationAlarmManager.canScheduleExactAlarms())
		{
			NotificationAlarmManager.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				NotificationCalendar.timeInMillis,
				NotificationPendingIntent)
		}
	}
}
