package com.example.caltrackcoursework.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caltrackcoursework.Data.UserSettings
import com.example.caltrackcoursework.ViewModel.FoodViewModel
import kotlinx.coroutines.launch

/*This page displays toggles allowing users to turn notifications on or off*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplaySettings(FoodViewModel: FoodViewModel)
{
	val Scope = rememberCoroutineScope()
	/*This is used to indirectly update the settings values in my settings datastore*/
	val Settings by FoodViewModel.SettingsFlow.collectAsState(initial = UserSettings())
	Column(modifier = Modifier.padding(horizontal=16.dp,vertical=5.dp))
	{
		/*This displays toggles for all notifications, breakfast notifications, lunch notifications and dinner notifications*/
		Text("Notification Settings",fontSize=27.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp))
		Row(verticalAlignment = Alignment.CenterVertically) {
			Text("Notifications:",modifier=Modifier.width(300.dp),fontWeight = FontWeight.Bold)
			/*When this toggle is checked, it will turn all subsequent toggles on or off as well*/
			Switch(
				checked = Settings.AllNotifications,
				onCheckedChange = { NewValue ->
					Scope.launch{
						FoodViewModel.SettingsDatastore.UpdateAllNotifications(NewValue)
					}
				}
			)
		}
		/*This is used to update breakfast notification settings*/
		Row(verticalAlignment = Alignment.CenterVertically){
			Text("Breakfast reminder:",modifier=Modifier.width(300.dp),fontWeight = FontWeight.Bold)
			Switch(checked = Settings.BreakfastNotifications, onCheckedChange = { NewValue ->
				Scope.launch{
					FoodViewModel.SettingsDatastore.UpdateBreakfastNotifications(NewValue)
				}
			}
			)
		}
		/*This is used to update lunch notification settings*/
		Row(verticalAlignment = Alignment.CenterVertically){
			Text("Lunch reminder:",modifier=Modifier.width(300.dp),fontWeight = FontWeight.Bold)
			Switch(checked = Settings.LunchNotifications, onCheckedChange = { NewValue ->
				Scope.launch{
					FoodViewModel.SettingsDatastore.UpdateLunchNotifications(NewValue)
				}
			}
			)
		}
		/*This is used to update dinner notification settings*/
		Row(verticalAlignment = Alignment.CenterVertically){
			Text("Dinner reminder:",modifier=Modifier.width(300.dp),fontWeight = FontWeight.Bold)
			Switch(checked = Settings.DinnerNotifications, onCheckedChange = { NewValue ->
				Scope.launch{
					FoodViewModel.SettingsDatastore.UpdateDinnerNotifications(NewValue)
				}
			}
			)
		}
	}
}