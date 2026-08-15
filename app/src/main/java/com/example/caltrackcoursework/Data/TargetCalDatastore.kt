package com.example.caltrackcoursework.Data
import android.content.Context;
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*This datastore just stores the value of the calorie target the user has set for themselves*/
val Context.TargetDataStore by preferencesDataStore("CalTarget")
class TargetCalDatastore(private val context: Context)
{

	/*This specifies that the value of calorie target will always have to be an int
	* by linking it to an int key*/
	companion object{
		val CalorieTargetKey = intPreferencesKey("CalorieTarget")
	}
	/*This value is used to store the current target in my app*/
	val CalorieTarget: Flow<Int> = context.TargetDataStore.data.map{
		DefaultVal ->
		DefaultVal[CalorieTargetKey] ?: 2000
	}

	/*This function updates the users calorie target*/
	suspend fun SetCalTarget(ValToSet: Int)
	{
		context.TargetDataStore.edit { NewSetter ->
			NewSetter[CalorieTargetKey] = ValToSet
		}
	}
}