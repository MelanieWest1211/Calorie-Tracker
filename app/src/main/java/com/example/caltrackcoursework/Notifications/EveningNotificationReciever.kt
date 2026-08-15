package com.example.caltrackcoursework.Notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.caltrackcoursework.Data.SettingsDatastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/*This notification reciever initially sets up notifications to be displayed today when the app is opened*/
class NotificationReceiver : BroadcastReceiver() {
	@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
	override fun onReceive(NotificationContext: Context, NotificationIntent: Intent?)
	{
		val PendingResult = goAsync()
		CoroutineScope(Dispatchers.IO).launch{
			try {
				/*This is used to fetch notification toggle data from my datastore*/
				val SettingsDatastore = SettingsDatastore(NotificationContext)
				/*This stores the notification toggle data as a flow*/
				val CurrentSettings = SettingsDatastore.SettingsFlow.first()
				val NotificationTitle = NotificationIntent?.getStringExtra("Title") ?: "Title"
				val NotificationDescription = NotificationIntent?.getStringExtra("Description") ?: "Description"
				val NotificationCode = NotificationIntent?.getIntExtra("ReceiverCode", 1000) ?: 1000
				/*This checks if the respective notification has actually been toggled on or off and sets ShouldDisplay accordingly*/
				val ShouldDisplay = when(NotificationCode)
				{
					1001 -> CurrentSettings.BreakfastNotifications
					1002 -> CurrentSettings.LunchNotifications
					1003 -> CurrentSettings.DinnerNotifications
					else -> true
				}
				if(ShouldDisplay)
				{
					Notifications.CreateNotificationChannel(NotificationContext)
					Notifications.DisplayNotification(NotificationContext, NotificationTitle, NotificationDescription, NotificationCode)
				}
			}
			finally
			{
				PendingResult.finish()
			}
		}
	}
}