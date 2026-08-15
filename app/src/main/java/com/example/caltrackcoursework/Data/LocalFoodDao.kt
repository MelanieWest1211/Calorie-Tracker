package com.example.caltrackcoursework.Data

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalFoodDao {

	/*This function is used in my content provider and in the app itself*/
	/*This function adds a new meal record to my LocalFood database*/
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(MealToAdd: DailyFood): Long

	/*These functions are used by my contentprovider*/

	/*This function updates existing food records with new food name, calories eaten, grams eaten and calsPer100 values*/
	@Query("UPDATE DailyFood SET FoodName= :NewFoodName, CaloriesEaten = :NewCalsEaten, GramsEaten = :NewGramsEaten, CalsPer100 = :NewCalsPer100 WHERE id = :IDToUse")
	fun UpdateDailyFoodContentProvider(IDToUse: Int, NewFoodName: String, NewCalsEaten: Int, NewGramsEaten: Int, NewCalsPer100: Int): Int

	/*This function fetches all data from my dailyfood table*/
	@Query("SELECT * FROM DailyFood")
	fun GetAllDataCursor(): Cursor

	/*This fetches a specific DailyFood value using its ID*/
	@Query("SELECT * FROM DailyFood WHERE id = :IDToUse")
	fun GetAllDataByIDCursor(IDToUse: Int): Cursor

	/*This fetches all DailyFood information for a given date*/
	@Query("SELECT * FROM DailyFood WHERE DateEaten = :DateToUse")
	fun GetAllDataByDateCursor(DateToUse: String): Cursor

	/*This deletes an instance of DailyFood, using the ID to identify what to delete
	* While this is very similar to my other delete function, I still require both as a
	* suspend function is needed for my app*/
	@Query("DELETE FROM DailyFood WHERE id = :IDToUse")
	fun DeleteFoodByID(IDToUse: Int): Int

	/*This is used to delete all dailyfood items for a given day*/
	@Query("DELETE FROM DailyFood WHERE DateEaten = :DateToUse")
	fun DeleteFoodByDate(DateToUse: String): Int

	/*These functions are used in my app*/

	/*This function is used to fetch all breakfast food eaten on a given day*/
	@Query("SELECT * FROM DailyFood WHERE DateEaten = :DateEaten AND MealType = 'Breakfast'")
	fun GetBreakfast(DateEaten: String): LiveData<List<DailyFood>>

	/*This function is used to fetch all lunch food eaten on a given day*/
	@Query("SELECT * FROM DailyFood WHERE DateEaten = :DateEaten AND MealType = 'Lunch'")
	fun GetLunch(DateEaten: String): LiveData<List<DailyFood>>

	/*This function is used to fetch all dinner food eaten on a given day*/
	@Query("SELECT * FROM DailyFood WHERE DateEaten = :DateEaten AND MealType = 'Dinner'")
	fun GetDinner(DateEaten: String): LiveData<List<DailyFood>>

	/*This function is used to fetch all snacks eaten on a given day*/
	@Query("SELECT * FROM DailyFood WHERE DateEaten = :DateEaten AND MealType = 'Snacks'")
	fun GetSnacks(DateEaten: String): LiveData<List<DailyFood>>

	/*This function is used to delete a meal from the database using its ID*/
	@Query("DELETE FROM DailyFood WHERE id = :IDToDelete")
	suspend fun DeleteMealEntry(IDToDelete: Int)
}