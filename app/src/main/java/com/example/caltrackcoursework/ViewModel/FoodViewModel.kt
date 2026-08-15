package com.example.caltrackcoursework.ViewModel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caltrackcoursework.Data.DailyFood
import com.example.caltrackcoursework.Data.DailyFoodRepository
import com.example.caltrackcoursework.Data.DailyTotalDatabase
import com.example.caltrackcoursework.Data.DailyTotals
import com.example.caltrackcoursework.Data.FirebaseFood
import com.example.caltrackcoursework.Data.LocalFoodDao
import com.example.caltrackcoursework.Data.LocalFoodDatabase
import com.example.caltrackcoursework.Data.SettingsDatastore
import com.example.caltrackcoursework.Data.TargetCalDatastore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

/*This food view model is used to store any data that needs to be preserved through changes like screen rotation*/
@RequiresApi(Build.VERSION_CODES.O)
class FoodViewModel(application: Application) :
	AndroidViewModel(application)
{
	/*This data store stores the status of all notification toggles, so my notification management knows whether to display them or not*/
	val SettingsDatastore = SettingsDatastore(application)
	/*This settings flow is used to update notification settings. It is kept here so that the data is preserved between screen changes*/
	val SettingsFlow = SettingsDatastore.SettingsFlow
	/*This data store is used to store the current user's calorie target*/
	private val TargetCalDatastore = TargetCalDatastore(application)
	val CalorieTarget: Flow<Int> = TargetCalDatastore.CalorieTarget
	/*This repository contains all queries relating to my local storage*/
	private val DailyFoodRepository: DailyFoodRepository
	/*This is used to access my firebase database*/
	private val FirebaseDB = FirebaseFirestore.getInstance();
	/*This stores a list of all data in my firebase db*/
	val FirebaseFoodList: MutableStateFlow<List<FirebaseFood>> =MutableStateFlow(emptyList())
	/*This fetches the current date, which is used to find the default data my weekly totals and daily information table displays*/
	@RequiresApi(Build.VERSION_CODES.O)
	val CurrentDate: LocalDate = LocalDate.now();
	/*This is used to store the current date being displayed in the daily meals screen, so this is preserved if the user switches screens*/
	/*I need both string and date versions of this, as I need it in a date form to manipulate the date, but the data itself is actually stored
	*in my DB as a string and displayed as a string*/
	var DailyMealsDisplayedDate by mutableStateOf(LocalDate.now());
	var DailyMealsDisplayedDateString by mutableStateOf(DailyMealsDisplayedDate.toString());
	/*This is indirectly used to store the current week my weekly totals page is looking at*/
	var TotalTableWeekOffset by mutableStateOf(0);
	/*This is used to calculate the most recent date currently displayed in my weekly totals table. It is used when sharing data
	* in order to allow the week of the data being sent to be specified*/
	var TotalTableDisplayedDate by mutableStateOf(LocalDate.now());
	/*This stores a list of all calorie totals for the week being shown in my weeklytotals page*/
	var WeeklyTotals = mutableStateOf<List<DailyTotals>>(emptyList())
	/*This try-catch tries to fetch the data in my firebase database, as well as initialising Dao's
	* for my local databases, with an error log if anything in this process fails*/
	init
	{
		try {
			RealTimeFirebaseFetch()
			val LocalFoodDao = LocalFoodDatabase.getDatabase(application).LocalFoodDao()
			val DailyTotalsDao = DailyTotalDatabase.getDatabase(application).DailyTotalsDao()
			DailyFoodRepository = DailyFoodRepository(LocalFoodDao, DailyTotalsDao)
		}
		catch(e:Exception) {
			Log.e("DEBUG","Exception during init",e)
			throw e
		}
	}

	/*This function is what actually fetches the data from my firebase database*/
	private fun RealTimeFirebaseFetch()
	{
		FirebaseDB.collection("FoodData").addSnapshotListener{ CurrentData, Error ->
			/*This displays an error message if there is an error in the fetching process or if the data has somehow
			* been corrupted during the transfer*/
			if (Error != null)
			{
				Log.e("Firebase","Firebase error",Error)
				/*return @addSnapshotListener ends the function immediately to prevent further errors from accumulating*/
				return@addSnapshotListener
			}
			if(CurrentData == null)
			{
				Log.e("Firebase","Null snapshot",Error)
				return@addSnapshotListener
			}
			/*This makes use of my firebasefood class to structure the data from the database in a usable manner*/
			val FoodList = CurrentData.toObjects(FirebaseFood::class.java)
			FirebaseFoodList.value = FoodList
		}
	}

	/*This is used to add a new meal to the DailyFOod Database*/
	fun AddNewMeal(MealToAdd: DailyFood)
	{
		viewModelScope.launch{
			DailyFoodRepository.InsertMeal(MealToAdd);
		}
	}

	/*This is used to fetch totals lists for a given week. It takes the final day of that week as a parameter and
	* works backwards to fetch a list of data for the previous 7 days*/
	fun GetWeeklyTotals(InitialDate: LocalDate)
	{
		viewModelScope.launch {
			(0..6).forEach { DayOffset ->
				CheckForAndCreateTotals(InitialDate.minusDays(DayOffset.toLong()).toString())
			}
			val Result = (0..6).flatMap { DayOffset ->
				DailyFoodRepository.GetAllTotalsSingle(
					InitialDate.minusDays(DayOffset.toLong()).toString()
				)
			}
			WeeklyTotals.value = Result
		}
	}

	/*The next 4 functions also update the overall total calories for a given day,
	* ensuring the overall total remains accurate*/

	/*This is used to update the total value for the breakfast category on a given day*/
	fun UpdateBreakfastTotal(DayToAdd: String, NewBreakfastCals:Int)
	{
		viewModelScope.launch{
			DailyFoodRepository.UpdateBreakfastTotal(DayToAdd, NewBreakfastCals);
		}
	}

	/*This is used to update the total value for the lunch category on a given day*/
	fun UpdateLunchTotal(DayToAdd: String, NewLunchCals:Int)
	{
		viewModelScope.launch{
			DailyFoodRepository.UpdateLunchTotal(DayToAdd, NewLunchCals);
		}
	}

	/*This is used to update the total value for the dinner category on a given day*/
	fun UpdateDinnerTotal(DayToAdd: String, NewDinnerCals:Int)
	{
		Log.d("DB","Updating dinner")
		viewModelScope.launch{
			DailyFoodRepository.UpdateDinnerTotal(DayToAdd, NewDinnerCals);
		}
	}

	/*This is used to update the total value for the snack category on a given day*/
	fun UpdateSnackTotal(DayToAdd: String, NewSnackCals:Int)
	{
		Log.d("DB","Updating snacks")
		viewModelScope.launch{
			DailyFoodRepository.UpdateSnackTotal(DayToAdd, NewSnackCals);
		}
	}

	/*This fetches all total calorie information for a given day*/
	fun GetAllTotals(DateToUse: String): Flow<List<DailyTotals>>
	{
		return DailyFoodRepository.GetAllTotals(DateToUse);
	}

	/*These functions get all meal information for the totals, breakfast, lunch and dinner columns respectively
	I use 5 different queries for these categories of data as my daily meals page works off a for loop of arrays
	to display the data (this will be explained in more detail in the page itself), meaning 5 separate arrays are used*/
	/*This fetches a list of all breakfast items for a given day*/
	fun GetBreakfast(DateToUse: String): LiveData<List<DailyFood>>
	{
		return DailyFoodRepository.GetBreakfast(DateToUse);
	}
	/*This fetches a list of all lunch items for a given day*/
	fun GetLunch(DateToUse: String): LiveData<List<DailyFood>>
	{
		return DailyFoodRepository.GetLunch(DateToUse);
	}
	/*This fetches a list of all dinner items for a given day*/
	fun GetDinner(DateToUse: String): LiveData<List<DailyFood>>
	{
		return DailyFoodRepository.GetDinner(DateToUse);
	}
	/*This fetches a list of all snack items for a given day*/
	fun GetSnacks(DateToUse: String): LiveData<List<DailyFood>>
	{
		return DailyFoodRepository.GetSnacks(DateToUse);
	}

	/*This removes a day from dailymealsdisplayed date, allowing users to view detailed meal information for past days*/
	fun SubtractDay()
	{
		DailyMealsDisplayedDate = DailyMealsDisplayedDate.minusDays(1);
		DailyMealsDisplayedDateString = DailyMealsDisplayedDate.toString();
		Log.d("DEBUG","NewDisplayedDate = ${DailyMealsDisplayedDate}")
	}

	/*This adds a day to the dailymeals displayeddate value, allowing users to navigate forward through
	* past days*/
	fun AddDay()
	{
		DailyMealsDisplayedDate = DailyMealsDisplayedDate.plusDays(1);
		DailyMealsDisplayedDateString = DailyMealsDisplayedDate.toString();
		Log.d("DEBUG","NewDisplayedDate = ${DailyMealsDisplayedDate}")
	}

	/*This is used to check if a list of totals for a given day exists yet, and create them if they do not*/
	suspend fun CheckForTotals(DateToAdd: String): Boolean
	{
		val CheckForList: List<DailyTotals> = DailyFoodRepository.GetAllTotalsSingle(DateToAdd);
		if (CheckForList.isNullOrEmpty()) {
			return false
		}
		else{
			return true
		}
	}
	/*This is used to look for a list of totals for the current week my total table is looking at
	* If the days don't have a total list created yet, one is created filled entirely with 0 values by default*/
	suspend fun CheckForAndCreateTotals(DateToAdd: String)
	{
			if (!CheckForTotals(DateToAdd))
			{
				val NewDate = DailyTotals(
					DateOfTotal = DateToAdd,
					TotalBreakfastCals = 0,
					TotalLunchCals = 0,
					TotalDinnerCals = 0,
					TotalSnackCals = 0,
					AllTotalCals = 0
				)
				DailyFoodRepository.InsertNewTotalDay(NewDate);
			}
	}

	/*This is used to delete a users existing meal entry. It is used when editing meal entries to delete the old entry*/
	fun DeleteMealEntry(DayToDelete: String, MealType: String, CaloriesEaten: Int, IDToDelete:Int)
	{
		viewModelScope.launch {
			DailyFoodRepository.DeleteMeal(IDToDelete)
			Log.d("DEBUG","$MealType")
			/*This uses the mealtype parameter to ensure that the correct total category is being updated*/
			if(MealType == "Breakfast")
			{
				DailyFoodRepository.UpdateBreakfastTotal(DayToDelete, (-1)*CaloriesEaten)
			}
			else if(MealType == "Lunch")
			{
				DailyFoodRepository.UpdateLunchTotal(DayToDelete, (-1)*CaloriesEaten)
			}
			else if(MealType == "Dinner")
			{
				DailyFoodRepository.UpdateDinnerTotal(DayToDelete, (-1)*CaloriesEaten)
			}
			else if(MealType == "Snacks")
			{
				DailyFoodRepository.UpdateSnackTotal(DayToDelete, (-1)*CaloriesEaten)
			}
		}
	}

	//I had to put this in viewmodel as my main page won't update to register the new values upon swiping alone for some reason
	fun HandleSwipe(DragValue: Float)
	{
		if (DragValue < 0f && DailyMealsDisplayedDate < CurrentDate) {
			AddDay()
			Log.d("DEBUG", "Swiping right")
		}
		if (DragValue > 0f) {
			SubtractDay()
			Log.d("DEBUG", "Swiping left")
		}
	}
	/*This is used to update a users calorie target*/
	fun UpdateTargetCals(NewTarget: Int)
	{
		viewModelScope.launch{
			TargetCalDatastore.SetCalTarget(NewTarget)
		}
	}

}
