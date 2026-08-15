package com.example.caltrackcoursework.ContentProvider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.caltrackcoursework.Data.DailyTotalDatabase
import com.example.caltrackcoursework.Data.DailyTotalsDao
import com.example.caltrackcoursework.Data.LocalFoodDao
import com.example.caltrackcoursework.Data.LocalFoodDatabase
import com.example.caltrackcoursework.ContentProvider.FoodContract
import com.example.caltrackcoursework.Data.DailyFood
import com.example.caltrackcoursework.Data.DailyTotals
import kotlinx.coroutines.runBlocking

/*This content provider can be used by external apps to fetch or modify my apps data*/
class FoodProvider : ContentProvider()
{
	/*These variables access my food database, the total food database and all queries related to them*/
	private lateinit var FoodDB: LocalFoodDatabase
	private lateinit var TotalDB: DailyTotalDatabase
	private lateinit var FoodDataDAO: LocalFoodDao
	private lateinit var TotalsDao: DailyTotalsDao

	/*These specify codes for the different types of queries my contentprovider uses*/
	companion object
	{
		/*This is used for general daily food queries*/
		private const val DAILY_FOOD = 100
		/*This is used for daily food queries where the ID is needed as a parameter*/
		private const val DAILY_FOOD_ID = 101
		/*This is used for daily food queries where the date is needed as a parameter*/
		private const val DAILY_FOOD_DATE = 102
		/*This is used for general food total queries*/
		private const val FOOD_TOTALS = 103
		/*This is used for food total queries where the date is needed as a parameter*/
		private const val FOOD_TOTALS_DATE = 104
		/*This is used for food total queries where the ID is needed as a parameter*/
		private const val FOOD_TOTALS_ID = 105
	}

	/*This matches URIs to the existing valid paths*/
	private val URIMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
		addURI(FoodContract.AUTHORITY, FoodContract.DailyFoodContract.PATH_DAILYFOOD, DAILY_FOOD)
		addURI(FoodContract.AUTHORITY, "${FoodContract.DailyFoodContract.PATH_DAILYFOOD}/ID/#", DAILY_FOOD_ID)
		addURI(FoodContract.AUTHORITY, "${FoodContract.DailyFoodContract.PATH_DAILYFOOD}/Date/*", DAILY_FOOD_DATE)
		addURI(FoodContract.AUTHORITY, FoodContract.FoodTotalsContract.PATH_FOODTOTALS, FOOD_TOTALS)
		addURI(FoodContract.AUTHORITY, "${FoodContract.FoodTotalsContract.PATH_FOODTOTALS}/ID/#", FOOD_TOTALS_ID)
		addURI(FoodContract.AUTHORITY, "${FoodContract.FoodTotalsContract.PATH_FOODTOTALS}/Date/*", FOOD_TOTALS_DATE)
	}

	/*This uses the URIMatcher to check the type of the URI sent*/
	override fun getType(URIToCheck: Uri): String?
	{
		return when (URIMatcher.match(URIToCheck))
		{
			DAILY_FOOD -> FoodContract.DailyFoodContract.CONTENT_TYPE
			DAILY_FOOD_ID -> FoodContract.DailyFoodContract.CONTENT_ITEM_TYPE
			FOOD_TOTALS -> FoodContract.FoodTotalsContract.CONTENT_TYPE
			FOOD_TOTALS_ID-> FoodContract.FoodTotalsContract.CONTENT_ITEM_TYPE
			else ->
				throw IllegalArgumentException("Unknown URI: $URIToCheck")
		}
	}

	/*This creates new instances of my databases and DAOs*/
	override fun onCreate(): Boolean {
		context?.let { Context ->
			FoodDB = LocalFoodDatabase.getDatabase(Context)
			TotalDB = DailyTotalDatabase.getDatabase(Context)
			FoodDataDAO = FoodDB.LocalFoodDao()
			TotalsDao = TotalDB.DailyTotalsDao()
		}
		return true
	}

	/*These queries all let you fetch data from the databases*/
	override fun query(
		uri: Uri,
		projection: Array<out String?>?,
		selection: String?,
		selectionArgs: Array<out String?>?,
		sortOrder: String?
	): Cursor? {
		return when (URIMatcher.match(uri))
		{
			/*This fetches all data from the dailyfood database*/
			DAILY_FOOD -> {
				FoodDataDAO.GetAllDataCursor()
			}
			/*This fetches all data with a given id from the dailyfood database*/
			DAILY_FOOD_ID -> {
				val ID = uri.lastPathSegment?.toInt()
					?: throw IllegalArgumentException("Invalid ID in URI: $uri")
				FoodDataDAO.GetAllDataByIDCursor(ID)
			}
			/*This fetches all data with a given date from the dailyfood database*/
			DAILY_FOOD_DATE -> {
				val Date = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid Date in URI: $uri")
				FoodDataDAO.GetAllDataByDateCursor(Date)
			}
			/*This fetches all data from the totalfood database*/
			FOOD_TOTALS -> {
				TotalsDao.GetAllTotalsCursor()
			}
			/*This fetches all data with a certain id from the foodtotals database*/
			FOOD_TOTALS_ID -> {
				val ID = uri.lastPathSegment?.toInt() ?: throw IllegalArgumentException("Invalid ID in URI: $uri")
				TotalsDao.GetTotalByIDCursor(ID)
			}
			/*This fetches all data with a certain date from the foodtotals database*/
			FOOD_TOTALS_DATE -> {
				val Date = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid Date in URI: $uri")
				TotalsDao.GetTotalsByDateCursor(Date)
			}
			else -> {
				throw IllegalArgumentException("Unknown URI: $uri")
			}
		}
	}

	/*This is used to delete data from my databases*/
	override fun delete(
		uri: Uri,
		selection: String?,
		selectionArgs: Array<out String?>?
	): Int
	{
		val RowsDeleted: Int
		when(URIMatcher.match(uri))
		{
			/*These deletes a single piece of data from the dailyfood database, using its ID*/
			DAILY_FOOD_ID ->
			{
				val ID = uri.lastPathSegment?.toInt() ?: throw IllegalArgumentException("Unknown ID at: $uri")
				RowsDeleted = FoodDataDAO.DeleteFoodByID(ID)
			}
			/*These deletes all dailyfood data from a given day*/
			DAILY_FOOD_DATE ->
			{
				val Date = uri.lastPathSegment ?: throw IllegalArgumentException("Unknown ID at: $uri")
				RowsDeleted = FoodDataDAO.DeleteFoodByDate(Date)
			}
			/*These deletes a single piece of data from the foodtotals database, using its ID*/
			FOOD_TOTALS_ID ->
			{
				val ID = uri.lastPathSegment?.toInt() ?: throw IllegalArgumentException("Unknown ID at: $uri")
				RowsDeleted = TotalsDao.DeleteTotalsByID(ID)
			}
			/*These deletes all foodtotals data from a given day*/
			FOOD_TOTALS_DATE ->
			{
				val Date = uri.lastPathSegment ?: throw IllegalArgumentException("Unknown ID at: $uri")
				RowsDeleted = TotalsDao.DeleteTotalsByDate(Date)
			}
			else -> throw IllegalArgumentException("Unsupported URI used: $uri")
		}
		if(RowsDeleted > 0)
		{
			context?.contentResolver?.notifyChange(uri, null)
		}
		return RowsDeleted
	}

	/*This is used to insert new data into my databases*/
	override fun insert(
		uri: Uri,
		values: ContentValues?
	): Uri?
	{
		return when(URIMatcher.match(uri))
		{
			/*This creates a new foodtotals value*/
			FOOD_TOTALS ->
			{
				val DateOfTotal = values?.getAsString(FoodContract.FoodTotalsContract.COLUMN_DATEOFTOTAL)
				val TotalBreakfastCals = values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_BREAKFASTTOTAL)
				val TotalLunchCals = values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_LUNCHTOTAL)
				val TotalDinnerCals = values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_DINNERTOTAL)
				val TotalSnackCals = values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_SNACKTOTAL)
				val AllTotalCals = values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_OVERALLTOTAL)
				if (
					DateOfTotal == null ||
					TotalBreakfastCals == null ||
					TotalLunchCals == null ||
					TotalDinnerCals == null ||
					TotalSnackCals == null ||
					AllTotalCals == null
				)
				{
					throw IllegalArgumentException("Missing necessary values")
				}
				val TotalsToAdd = DailyTotals(
					DateOfTotal = DateOfTotal,
					TotalBreakfastCals = TotalBreakfastCals,
					TotalLunchCals = TotalLunchCals,
					TotalDinnerCals = TotalDinnerCals,
					TotalSnackCals = TotalSnackCals,
					AllTotalCals = AllTotalCals
				)
				val id = runBlocking{
					TotalsDao.insert(TotalsToAdd)
				}
				val ReturnURI = ContentUris.withAppendedId(FoodContract.DailyFoodContract.CONTENT_URI, id)
				context?.contentResolver?.notifyChange(ReturnURI, null)
				ReturnURI
			}
			/*This creates a new dailyfood value*/
			DAILY_FOOD->
			{
				val FoodName = values?.getAsString(FoodContract.DailyFoodContract.COLUMN_FOODNAME)
				val CaloriesEaten = values?.getAsInteger(FoodContract.DailyFoodContract.COLUMN_CALORIESEATEN)
				val CalsPer100 = values?.getAsInteger(FoodContract.DailyFoodContract.COLUMN_CALSPER100)
				val MealType = values?.getAsString(FoodContract.DailyFoodContract.COLUMN_MEALTYPE)
				val DateEaten = values?.getAsString(FoodContract.DailyFoodContract.COLUMN_DATEEATEN)
				val GramsEaten = values?.getAsInteger(FoodContract.DailyFoodContract.COLUMN_GRAMSEATEN)
				if (
					FoodName == null ||
					CaloriesEaten == null ||
					CalsPer100 == null ||
					MealType == null ||
					DateEaten == null ||
					GramsEaten == null
				)
				{
					throw IllegalArgumentException("Insert is being queried - Missing necessary values")
				}
				val FoodToAdd = DailyFood(
					FoodName = FoodName,
					CaloriesEaten = CaloriesEaten,
					CalsPer100 = CalsPer100,
					MealType = MealType,
					DateEaten = DateEaten,
					GramsEaten = GramsEaten
				)
				val id = runBlocking{
					FoodDataDAO.insert(FoodToAdd)
				}
				val ReturnURI = ContentUris.withAppendedId(FoodContract.DailyFoodContract.CONTENT_URI, id)
				context?.contentResolver?.notifyChange(ReturnURI, null)
				ReturnURI
			}
			else ->
			{
				throw IllegalArgumentException("Unsupported URI: $uri")
			}
		}
	}

	/*This is used to update existing values in my totalfood and dailyfood databases*/
	override fun update(
		uri: Uri,
		values: ContentValues?,
		selection: String?,
		selectionArgs: Array<out String?>?
	): Int {
		return when(URIMatcher.match(uri)) {
			/*This updates the dailyfood data for a given ID*/
			DAILY_FOOD_ID -> {
				val FoodName = values?.getAsString(FoodContract.DailyFoodContract.COLUMN_FOODNAME)
				val CaloriesEaten =
					values?.getAsInteger(FoodContract.DailyFoodContract.COLUMN_CALORIESEATEN)
				val CalsPer100 =
					values?.getAsInteger(FoodContract.DailyFoodContract.COLUMN_CALSPER100)
				val IDToUse = ContentUris.parseId(uri).toInt()
				val GramsEaten =
					values?.getAsInteger(FoodContract.DailyFoodContract.COLUMN_GRAMSEATEN)
				if (
					FoodName == null ||
					CaloriesEaten == null ||
					CalsPer100 == null ||
					GramsEaten == null
				) {
					throw IllegalArgumentException("Foodname, calseaton, calsper100 or gramseaten is null - Missing necessary values")
				}
				val RowsUpdated = FoodDataDAO.UpdateDailyFoodContentProvider(IDToUse, FoodName, CaloriesEaten, GramsEaten, CalsPer100)
				if(RowsUpdated > 0)
				{
					context?.contentResolver?.notifyChange(uri, null)
				}
				RowsUpdated
			}
			/*This updates the food totals data for a given ID*/
			FOOD_TOTALS_ID -> {
				val IDToUse = ContentUris.parseId(uri).toInt()
				val TotalBreakfastCals =
					values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_BREAKFASTTOTAL)
				val TotalLunchCals =
					values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_LUNCHTOTAL)
				val TotalDinnerCals =
					values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_DINNERTOTAL)
				val TotalSnackCals =
					values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_SNACKTOTAL)
				val AllTotalCals =
					values?.getAsInteger(FoodContract.FoodTotalsContract.COLUMN_OVERALLTOTAL)
				if (
					TotalBreakfastCals == null ||
					TotalLunchCals == null ||
					TotalDinnerCals == null ||
					TotalSnackCals == null ||
					AllTotalCals == null
				) {
					throw IllegalArgumentException("Totalcals is being queried - Missing necessary values")
				}
				val RowsUpdated = TotalsDao.UpdateTotalsContentProvider(IDToUse, TotalBreakfastCals, TotalLunchCals, TotalDinnerCals, TotalSnackCals, AllTotalCals)
				if (RowsUpdated > 0)
				{
					context?.contentResolver?.notifyChange(uri, null)
				}
				RowsUpdated
			}
			else ->
			{
				throw IllegalArgumentException("Unsupported URI: $uri")
			}
		}
	}
}