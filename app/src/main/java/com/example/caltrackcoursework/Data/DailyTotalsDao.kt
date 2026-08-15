package com.example.caltrackcoursework.Data

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.caltrackcoursework.Data.DailyTotals
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTotalsDao {

	/*This inserts a new day into the DailyTotals database*/
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(DayToAdd: DailyTotals): Long

	/*These functions are all used by my contentprovider*/
	/*Fetches all information in the dailytotals table*/
	@Query("SELECT * FROM DailyTotals")
	fun GetAllTotalsCursor(): Cursor

	/*Fetches a single piece of data from dailytotals using its ID*/
	@Query("SELECT * FROM DailyTotals WHERE id = :IDToUse")
	fun GetTotalByIDCursor(IDToUse: Int): Cursor

	/*Fetches a single piece of data from dailytotals using its date*/
	@Query("SELECT * FROM DailyTotals WHERE DateOfTotal = :DateToUse")
	fun GetTotalsByDateCursor(DateToUse: String): Cursor

	/*Updates existing dailytotals data, using the ID to ensure the correct data is updated*/
	@Query("UPDATE DailyTotals SET TotalBreakfastCals = :NewBreakfastTotal, TotalLunchCals = :NewLunchTotal, TotalDinnerCals = :NewDinnerTotal, TotalSnackCals = :NewSnackTotal, AllTotalCals = :NewOverallTotal WHERE id = :IDToUse")
	fun UpdateTotalsContentProvider(IDToUse: Int, NewBreakfastTotal: Int, NewLunchTotal: Int, NewDinnerTotal: Int, NewSnackTotal: Int, NewOverallTotal: Int): Int

	/*This deletes a single value from dailytotals using the ID*/
	@Query("DELETE FROM DailyTotals WHERE id = :IDToUse")
	fun DeleteTotalsByID(IDToUse: Int): Int

	/*This deletes the total values in my database for a given date*/
	@Query("DELETE FROM DailyTotals WHERE DateOfTotal = :DateToUse")
	fun DeleteTotalsByDate(DateToUse: String): Int

	/*The following functions are used in my app*/

	/*Uses the date to fetch all total information for a given day and returns it as a flow list*/
	@Query("SELECT * FROM DailyTotals WHERE DateOfTotal = :DateOfTotal")
	fun GetAllTotals(DateOfTotal: String): Flow<List<DailyTotals>>

	/*This fetches all total data for a given date*/
	@Query("SELECT * FROM DailyTotals WHERE DateOfTotal = :DateOfTotal")
	suspend fun GetAllTotalsSingle(DateOfTotal: String): List<DailyTotals>

	/*This updates the breakfast calories for a given date using a parameter value*/
	@Query("UPDATE DailyTotals SET TotalBreakfastCals = TotalBreakfastCals + :NewBreakfastCals WHERE DateOfTotal = :DateOfTotal")
	suspend fun AddBreakfastCalories(DateOfTotal: String, NewBreakfastCals: Int)

	/*This updates the lunch calories for a given date using a parameter value*/
	@Query("UPDATE DailyTotals SET TotalLunchCals = TotalLunchCals + :NewLunchCals WHERE DateOfTotal = :DateOfTotal")
	suspend fun AddLunchCalories(DateOfTotal: String, NewLunchCals: Int)

	/*This updates the dinner calories for a given date using a parameter value*/
	@Query("UPDATE DailyTotals SET TotalDinnerCals = TotalDinnerCals + :NewDinnerCals WHERE DateOfTotal = :DateOfTotal")
	suspend fun AddDinnerCalories(DateOfTotal: String, NewDinnerCals: Int)

	/*This updates the snack calories for a given date using a parameter value*/
	@Query("UPDATE DailyTotals SET TotalSnackCals = TotalSnackCals + :NewSnackCals WHERE DateOfTotal = :DateOfTotal")
	suspend fun AddSnackCalories(DateOfTotal: String, NewSnackCals: Int)

	/*This updates the total calories for a given date using a parameter value*/
	@Query("UPDATE DailyTotals SET AllTotalCals = AllTotalCals + :NewTotalCals WHERE DateOfTotal = :DateOfTotal")
	suspend fun AddTotalCalories(DateOfTotal: String, NewTotalCals: Int)
}