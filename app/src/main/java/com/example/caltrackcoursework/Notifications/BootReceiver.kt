package com.example.caltrackcoursework.Notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.caltrackcoursework.Data.SettingsDatastore
import com.example.caltrackcoursework.Notifications.Notifications.ScheduleNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/*This boot reciever is used to fetch notification data when the device starts up. This ensures that the
* notifications can display even if the app isn't open at the time*/
class BootReceiver : BroadcastReceiver()
{
	@RequiresApi(Build.VERSION_CODES.S)
	override fun onReceive(NotificationContext: Context, NotificationIntent: Intent?)
	{

		Log.d("DEBUG","boot reciever activated")
		if(NotificationIntent?.action == Intent.ACTION_BOOT_COMPLETED) return
		/*This allows my function to become async, letting the receiver check if the notifications have been toggled
		* on and only setting them up if they have been*/
		val ToggleChecker = goAsync()
		CoroutineScope(Dispatchers.IO).launch{
			try {
				/*This is used to fetch notification toggle data from my datastore*/
				val SettingsDatastore = SettingsDatastore(NotificationContext)
				/*This stores the notification toggle data as a flow*/
				val CurrentSettings = SettingsDatastore.SettingsFlow.first()
				if (CurrentSettings.BreakfastNotifications) {
					ScheduleNotification(
						NotificationContext,
						9,
						0,
						1001,
						"Breakfast reminder",
						"Don't forget to log your breakfast!"
					)
				}
				if (CurrentSettings.LunchNotifications) {
					ScheduleNotification(
						NotificationContext,
						13,
						0,
						1002,
						"Lunch reminder",
						"Don't forget to log your lunch!"
					)
				}
				if (CurrentSettings.DinnerNotifications) {
					ScheduleNotification(
						NotificationContext,
						19,
						0,
						1003,
						"Dinner reminder",
						"Don't forget to log your dinner!"
					)
				}
			}
			finally{
				ToggleChecker.finish()
			}
		}
	}
}
