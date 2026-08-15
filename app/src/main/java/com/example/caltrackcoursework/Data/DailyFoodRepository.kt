package com.example.caltrackcoursework.Data

import androidx.lifecycle.LiveData
import com.example.caltrackcoursework.Data.DailyTotalsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

/*This repository can be used to access all functions from both the LocalFood and FoodTotal DAO's*/
class DailyFoodRepository(private val LocalFoodDao: LocalFoodDao, private val DailyTotalsDao: DailyTotalsDao) {
	/*All LocalFoodDao functions (these are explained in the DAO itself) */
	fun GetBreakfast(DateToUse: String): LiveData<List<DailyFood>> = LocalFoodDao.GetBreakfast(DateToUse)
	fun GetLunch(DateToUse: String): LiveData<List<DailyFood>> = LocalFoodDao.GetLunch(DateToUse)
	fun GetDinner(DateToUse: String): LiveData<List<DailyFood>> = LocalFoodDao.GetDinner(DateToUse)
	fun GetSnacks(DateToUse: String): LiveData<List<DailyFood>> = LocalFoodDao.GetSnacks(DateToUse)

	suspend fun InsertMeal(MealToAdd:DailyFood)
	{
		LocalFoodDao.insert(MealToAdd)
	}

	suspend fun DeleteMeal(IDToRemove: Int)
	{
		LocalFoodDao.DeleteMealEntry(IDToRemove);
	}

	/*All dailytotalsDao functions (again, these are explained in the DAO)*/
	fun GetAllTotals(DateToUse: String): Flow<List<DailyTotals>> = DailyTotalsDao.GetAllTotals(DateToUse)
	suspend fun GetAllTotalsSingle(DateToUse: String): List<DailyTotals> = DailyTotalsDao.GetAllTotalsSingle(DateToUse)

	suspend fun InsertNewTotalDay(DayToAdd:DailyTotals)
	{
		DailyTotalsDao.insert(DayToAdd)
	}

	/*All my update total queries call 2 dao functions, one to update the specific total
	 category where the new meal was added and another to update the calorie total for that day*/

	suspend fun UpdateBreakfastTotal(DayToAdd:String, NewBreakfastCals:Int)
	{
		DailyTotalsDao.AddBreakfastCalories(DayToAdd, NewBreakfastCals)
		DailyTotalsDao.AddTotalCalories(DayToAdd, NewBreakfastCals)
	}

	suspend fun UpdateLunchTotal(DayToAdd:String, NewLunchCals:Int)
	{
		DailyTotalsDao.AddLunchCalories(DayToAdd, NewLunchCals)
		DailyTotalsDao.AddTotalCalories(DayToAdd, NewLunchCals)
	}

	suspend fun UpdateDinnerTotal(DayToAdd:String, NewDinnerCals:Int)
	{
		DailyTotalsDao.AddDinnerCalories(DayToAdd, NewDinnerCals)
		DailyTotalsDao.AddTotalCalories(DayToAdd, NewDinnerCals)
	}

	suspend fun UpdateSnackTotal(DayToAdd:String, NewSnackCals:Int)
	{
		DailyTotalsDao.AddSnackCalories(DayToAdd, NewSnackCals)
		DailyTotalsDao.AddTotalCalories(DayToAdd, NewSnackCals)
	}


}
