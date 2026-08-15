package com.example.caltrackcoursework

import android.Manifest
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.caltrackcoursework.Notifications.Notifications
import com.example.caltrackcoursework.Notifications.Notifications.ScheduleNotification
import com.example.caltrackcoursework.ui.theme.CalTrackCourseworkTheme
import com.example.caltrackcoursework.Screens.AddNewMeal
import com.example.caltrackcoursework.Screens.DisplayDailyFood
import com.example.caltrackcoursework.Screens.DisplayFoodStats
import com.example.caltrackcoursework.Screens.DisplaySettings
import com.example.caltrackcoursework.ViewModel.FoodViewModel


/*AI USE DECLARATION: I used AI for debugging purposes in various locations throughout my app*/
class MainActivity : ComponentActivity()
{
	@RequiresApi(Build.VERSION_CODES.S)
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		/*Creating my initial Notification channel. This will be used for all notification setup*/
		Notifications.CreateNotificationChannel(this)
		enableEdgeToEdge()
		setContent{
			AppSetup(Modifier)
		}
	}
}
@RequiresApi(Build.VERSION_CODES.S)
@Composable
/*This completes various setup tasks, including setting up notifications, my NavHost and the bottom navigation bar*/
fun AppSetup(modifier: Modifier = Modifier)
{
	val NavContext = LocalContext.current
	/*This is used to ensure that notifications will actually appear at set times*/
	val NotificationAlarmManager = NavContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
	/*This is initially used to ask the user for permission to send notifications, if they have not been asked*/
	val NotificationPermissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) {}
	/*This is run as soon as the app launches. It checks if the app has the necesssary permissions for
	 my reminder notifications and asks for them if it does not*/
	LaunchedEffect(Unit){
		val PermissionGranted = ContextCompat.checkSelfPermission(NavContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
		if(!PermissionGranted)
		{
			NotificationPermissionLauncher.launch(
				Manifest.permission.POST_NOTIFICATIONS
			)
		}
		//Apparently you can only allow permission for fixed-time notifications via settings, hence it not appearing as a popup
		if(!NotificationAlarmManager.canScheduleExactAlarms())
		{
			val NotificationIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
			NotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			NavContext.startActivity(NotificationIntent)
		}
	}
	/*This calls functions to set meal reminders at breakfast, lunch and dinner time*/
	ScheduleNotification(NavContext, 9, 0, 1001, "Breakfast reminder","Don't forget to log your breakfast!")
	ScheduleNotification(NavContext, 13, 0, 1002, "Lunch reminder","Don't forget to log your lunch!")
	ScheduleNotification(NavContext, 19, 0, 1003, "Dinner reminder","Don't forget to log your dinner!")
	/*This nav controller is used for screen navigation throughout my app*/
	val AppNavController : NavHostController = rememberNavController()
	/*This viewmodel instance is passed into all screens, preventing duplicate instances from being made,
	* as this could cause issues with data syncing between screens*/
	val FoodViewModel: FoodViewModel = viewModel();
	CalTrackCourseworkTheme {
		Scaffold(
			/*This creates the bottom navbar, which is used to navigate between my weekly totals, daily calories and settings screens*/
			bottomBar = {
				NavigationBar {
					/*This creates navigates to the meal stat screen, which is the default homepage of my app*/
					NavigationBarItem(
						selected = false,
						onClick = {
							AppNavController.navigate(AppScreens.MealStats.name)
						},
						icon = {
							Icon(
								imageVector = Icons.Default.Home,
								contentDescription = "meal stat page"
							)
						},
						label = {
							Text("Home",fontSize=16.sp)
						}
					)
					/*This navigates to the daily meals screen, where users can view detailed information for a given day*/
					NavigationBarItem(
						selected = false,
						onClick = {
							AppNavController.navigate(AppScreens.DailyMeals.name)
						},
						icon = {
							Icon(
								imageVector = Icons.Default.DateRange,
								contentDescription = "daily meal page"
							)
						},
						label = {
							Text("Daily meals",fontSize=16.sp)
						}
					)
					/*This navigates to my settings screen, where users can turn notifications on or off*/
					NavigationBarItem(
						selected = false,
						onClick = {
							AppNavController.navigate(AppScreens.Settings.name)
						},
						icon = {
							Icon(
								imageVector = Icons.Default.Settings,
								contentDescription = "Settings"
							)
						},
						label = {
							Text("Settings",fontSize=16.sp)
						}
					)
				}
			}
		)
		{
				innerPadding ->
			/*This sets up my nav host, which is used for all navigation between screens*/
			NavHost(
				navController = AppNavController,
				startDestination = AppScreens.MealStats.name,
				modifier = modifier.padding(innerPadding)
			)
			{
				/*This handles navigation to my daily meals page*/
				composable(AppScreens.DailyMeals.name)
				{
					DisplayDailyFood(
						FoodViewModel = FoodViewModel,
						modifier = Modifier,
						NavController = AppNavController
					)
				}
				composable(
					/*This handles navigation to my add existing meal page. This currently automatically adds the meal to the location where you
					* clicked the add meal button from, hence needing the MealDate and MealType as a parameter. In addition, it is also used to
					* edit existing meal entries, which is why the old meal ID is required*/
					AppScreens.AddExistingMeal.name+"/{MealDate}/{MealType}/{MealName}/{CaloriesEaten}/{CalsPer100}/{OldMealID}",
					arguments = listOf(
						navArgument("MealDate")
						{
							type = NavType.StringType
						},
						navArgument("MealType")
						{
							type = NavType.StringType
						},
						navArgument("MealName")
						{
							type = NavType.StringType
						},
						navArgument("CaloriesEaten")
						{
							type = NavType.StringType
						},
						navArgument("CalsPer100")
						{
							type = NavType.IntType
						},
						navArgument("OldMealID")
						{
							type = NavType.IntType
						}
					)
				)
				{
					/*This fetches the values passed in as parameters and actually passes them into the add new meal screen*/
					backStackEntry ->
					val MealDate = backStackEntry.arguments?.getString("MealDate") ?: ""
					val MealType = backStackEntry.arguments?.getString("MealType") ?: ""
					val MealName = backStackEntry.arguments?.getString("MealName") ?: ""
					val GramsEaten = backStackEntry.arguments?.getString("GramsEaten") ?: ""
					val CalsPer100 = backStackEntry.arguments?.getInt("CalsPer100") ?: 0
					val OldMealID = backStackEntry.arguments?.getInt("OldMealID") ?: 0
					AddNewMeal(
						NavController = AppNavController,
						MealDate = MealDate,
						MealType = MealType,
						FoodViewModel = FoodViewModel,
						MealName = MealName,
						GramsEaten = GramsEaten,
						CalsPer100 = CalsPer100,
						OldMealID = OldMealID
					)
				}
				/*This navigates to my meal stats page*/
				composable(AppScreens.MealStats.name)
				{
					DisplayFoodStats(
						FoodViewModel = FoodViewModel,
						NavController = AppNavController
					)
				}
				/*This handles navigation to my settings page*/
				composable(AppScreens.Settings.name)
				{
					DisplaySettings(
						FoodViewModel = FoodViewModel
					)
				}
			}
		}
	}
}

/*This enum class is just used to make navigation names easier,
 so I don't have to copy the full path every time I want to navigate to another page*/
enum class AppScreens
{
	DailyMeals,
	AddExistingMeal,
	MealStats,
	Settings
}