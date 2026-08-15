package com.example.caltrackcoursework.Data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*This data store is used to store the toggle values for notifications*/
private val Context.SettingsDataStore by preferencesDataStore("Settings")
class SettingsDatastore(private val context: Context) {
		/*Creates objects to store values for the breakfast, lunch and dinner toggles, as well as one
		* to store the toggle for all notifications*/
		companion object{
			val AllNotifications = booleanPreferencesKey("AllNotifications")
			val BreakfastNotifications = booleanPreferencesKey("BreakfastNotifications")
			val LunchNotifications = booleanPreferencesKey("LunchNotifications")
			val DinnerNotifications = booleanPreferencesKey("DinnerNotifications")
		}

		/*Creating a UserSettings object to store the settings values in my app*/
		val SettingsFlow: Flow<UserSettings> = context.SettingsDataStore.data.map{
			AllSettings -> UserSettings(
				AllNotifications = AllSettings[AllNotifications] ?: true,
				BreakfastNotifications = AllSettings[BreakfastNotifications] ?: true,
				LunchNotifications = AllSettings[LunchNotifications] ?: true,
				DinnerNotifications = AllSettings[DinnerNotifications] ?: true
			)
		}

		/*This updates the value of all notification toggles and is called when all notifications
		is toggled on or off*/
		suspend fun UpdateAllNotifications(ValToSet: Boolean)
		{
			context.SettingsDataStore.edit { NewSetter ->
				NewSetter[AllNotifications] = ValToSet
				NewSetter[BreakfastNotifications] = ValToSet
				NewSetter[LunchNotifications] = ValToSet
				NewSetter[DinnerNotifications] = ValToSet
			}
		}

		/*This updates the value of the breakfast notification toggle and is called when 'breakfast notifications'
		is toggled on or off*/
		suspend fun UpdateBreakfastNotifications(ValToSet: Boolean)
		{
			context.SettingsDataStore.edit { NewSetter ->
				NewSetter[BreakfastNotifications] = ValToSet
			}
		}

		/*This updates the value of the lunch notification toggle and is called when 'lunch notifications'
		is toggled on or off*/
		suspend fun UpdateLunchNotifications(ValToSet: Boolean)
		{
			context.SettingsDataStore.edit { NewSetter ->
				NewSetter[LunchNotifications] = ValToSet
			}
		}

		/*This updates the value of the dinner notification toggle and is called when 'dinner notifications'
		is toggled on or off*/
		suspend fun UpdateDinnerNotifications(ValToSet: Boolean) {
			context.SettingsDataStore.edit { NewSetter ->
				NewSetter[DinnerNotifications] = ValToSet
			}
		}
	}