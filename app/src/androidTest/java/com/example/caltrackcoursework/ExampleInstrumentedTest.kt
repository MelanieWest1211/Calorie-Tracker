package com.example.caltrackcoursework

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.caltrackcoursework.ContentProvider.FoodContract
import com.example.caltrackcoursework.ContentProvider.FoodProvider
import com.example.caltrackcoursework.Data.DailyFood
import com.example.caltrackcoursework.Data.LocalFoodDatabase
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import kotlin.apply

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

/*This contains all of my instrumented tests for the contentprovider*/
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

	/*This checks that external apps can access my app in the firstplace*/
	@Test
	fun useAppContext() {
		// Context of the app under test.
		val appContext = InstrumentationRegistry.getInstrumentation().targetContext
		assertEquals("com.example.caltrackcoursework", appContext.packageName)
	}

	/*Testing to make sure the DAILY_FOOD path is being recognised*/
	@Test
	fun GetType_DAILY_FOOD()
	{
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.DailyFoodContract.PATH_DAILYFOOD}")
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val type = context.contentResolver.getType(uri)
		assertEquals(
			FoodContract.DailyFoodContract.CONTENT_TYPE, type
		)
	}

	/*Testing to make sure the DAILY_FOOD_ID path is being recognised*/
	@Test
	fun GetType_DAILY_FOOD_ID()
	{
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.DailyFoodContract.PATH_DAILYFOOD}/ID/1")
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val type = context.contentResolver.getType(uri)
		assertEquals(
			FoodContract.DailyFoodContract.CONTENT_ITEM_TYPE, type
		)
	}

	/*Testing to make sure the FOOD_TOTALS path is being recognised*/
	@Test
	fun GetType_FOOD_TOTALS()
	{
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.FoodTotalsContract.PATH_FOODTOTALS}")
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val type = context.contentResolver.getType(uri)
		assertEquals(
			FoodContract.FoodTotalsContract.CONTENT_TYPE, type
		)
	}

	/*Testing to make sure the FOOD_TOTALS_ID path is being recognised*/
	@Test
	fun GetType_FOOD_TOTALS_ID()
	{
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.FoodTotalsContract.PATH_FOODTOTALS}/ID/1")
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val type = context.contentResolver.getType(uri)
		assertEquals(
			FoodContract.FoodTotalsContract.CONTENT_ITEM_TYPE, type
		)
	}

	/*Testing to see if my contentprovider can fetch all hte data from the dailyfood table*/
	@Test
	fun GetAllDataQuery_DAILY_FOOD_returnsCursor()
	{
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.DailyFoodContract.PATH_DAILYFOOD}")
		val cursor = context.contentResolver.query(uri, null, null, null, null)
		assertNotNull(cursor)
		cursor?.close()
	}

	/*Testing to see if my content provider can fetch data using a specific date*/
	@Test
	fun GetAllDataByDateQuery_DAILY_FOOD_DATE_returnsCursor()
	{
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val db = LocalFoodDatabase.getDatabase(context)
		val dao = db.LocalFoodDao()
		/*Adding the dummy data my test will look for. This is needed in case there is currently no data
		* for that date, so it is guaranteed to return something*/
		val id = runBlocking {
			dao.insert(
				DailyFood(
					FoodName = "Toast",
					CaloriesEaten = 100,
					CalsPer100 = 75,
					MealType = "Breakfast",
					DateEaten = "2026-05-16",
					GramsEaten = 75
				)
			)
		}
		/*Checking to see if the dummy data was found*/
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.DailyFoodContract.PATH_DAILYFOOD}/Date/2026-05-16")
		val cursor = context.contentResolver.query(uri, null, null, null, null)
		assertNotNull(cursor)
		assertTrue(cursor!!.count >= 1)
		cursor.close()
	}

	/*Checking if my contentproviders deletion by id works*/
	@Test
	fun Delete_DAILY_FOOD_ID_removesRow()
	{
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val db = LocalFoodDatabase.getDatabase(context)
		val dao = db.LocalFoodDao()
		/*Creating dummy data to be deleted*/
		val id = runBlocking {
			dao.insert(
				DailyFood(
					FoodName = "Toast",
					CaloriesEaten = 100,
					CalsPer100 = 75,
					MealType = "Breakfast",
					DateEaten = "2026-05-16",
					GramsEaten = 75
				)
			)
		}
		/*Deletion and checking to see if the dummy data deleted successfully*/
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.DailyFoodContract.PATH_DAILYFOOD}/ID/$id")
		val rowsDeleted = context.contentResolver.delete(uri, null, null)
		assertEquals(1, rowsDeleted)
	}

	/*Test to check if my insert function in contentprovider works*/
	@Test
	fun Insert_DAILY_FOOD_returnsValidUri()
	{
		/*Creating dummydata to be inserted*/
		val context = InstrumentationRegistry.getInstrumentation().targetContext
		val values = ContentValues().apply{
			put(FoodContract.DailyFoodContract.COLUMN_FOODNAME, "Eggs")
			put(FoodContract.DailyFoodContract.COLUMN_CALORIESEATEN, 100)
			put(FoodContract.DailyFoodContract.COLUMN_CALSPER100, 75)
			put(FoodContract.DailyFoodContract.COLUMN_MEALTYPE, "Breakfast")
			put(FoodContract.DailyFoodContract.COLUMN_DATEEATEN, "2026-05-16")
			put(FoodContract.DailyFoodContract.COLUMN_GRAMSEATEN, 64)
		}
		/*Inserting the dummy data and checking if it was successfully added*/
		val uri = Uri.parse("content://${FoodContract.AUTHORITY}/${FoodContract.DailyFoodContract.PATH_DAILYFOOD}")
		val resultUri = context.contentResolver.insert(uri, values)
		val cursor = context.contentResolver.query(uri, null, null, null, null)
		assertNotNull(cursor)
		assertTrue(cursor!!.count > 0)
		cursor.close()
	}

	/*This checks whether the dailyfood database can be updated with my update query*/
	@Test
	fun UpdateDailyFood_SuccessText()
	{
		val Context = InstrumentationRegistry.getInstrumentation().targetContext
		/*Creates dummy data which will be updated*/
		val InsertValues = ContentValues().apply{
			put(FoodContract.DailyFoodContract.COLUMN_FOODNAME, "Eggs")
			put(FoodContract.DailyFoodContract.COLUMN_CALORIESEATEN, 100)
			put(FoodContract.DailyFoodContract.COLUMN_CALSPER100, 75)
			put(FoodContract.DailyFoodContract.COLUMN_DATEEATEN, "17-05-2026")
			put(FoodContract.DailyFoodContract.COLUMN_MEALTYPE, "Breakfast")
			put(FoodContract.DailyFoodContract.COLUMN_GRAMSEATEN, 64)
		}
		val InsertURI = Context.contentResolver.insert(FoodContract.DailyFoodContract.CONTENT_URI, InsertValues)
		assertNotNull(InsertURI)
		/*Storing the ID used so I can use it to test later*/
		val ID = ContentUris.parseId(InsertURI!!)
		/*Actually tries to update the dummydata*/
		val UpdatedValues = ContentValues().apply{
			put(FoodContract.DailyFoodContract.COLUMN_FOODNAME, "Scrambled Eggs")
			put(FoodContract.DailyFoodContract.COLUMN_CALORIESEATEN, 120)
			put(FoodContract.DailyFoodContract.COLUMN_CALSPER100, 80)
			put(FoodContract.DailyFoodContract.COLUMN_GRAMSEATEN, 70)
		}
		/*Creating a URI with the correct ID value*/
		val UpdateURI = Uri.withAppendedPath(FoodContract.DailyFoodContract.CONTENT_URI, "ID/$ID")
		val RowsUpdated = Context.contentResolver.update(UpdateURI, UpdatedValues, null, null)
		assertEquals(1, RowsUpdated)
		val Cursor = Context.contentResolver.query(UpdateURI, null, "${FoodContract.DailyFoodContract.COLUMN_DATEEATEN} = ?",arrayOf("2026-05-17"),null)
		assertNotNull(Cursor)
		/*Checks if the update was successful*/
		Cursor?.use{
			assertTrue(it.moveToFirst())
			assertEquals("Scrambled Eggs",it.getString(it.getColumnIndexOrThrow(FoodContract.DailyFoodContract.COLUMN_FOODNAME)))
			assertEquals(120,it.getInt(it.getColumnIndexOrThrow(FoodContract.DailyFoodContract.COLUMN_CALORIESEATEN)))
			assertEquals(80,it.getInt(it.getColumnIndexOrThrow(FoodContract.DailyFoodContract.COLUMN_CALSPER100)))
			assertEquals(70,it.getInt(it.getColumnIndexOrThrow(FoodContract.DailyFoodContract.COLUMN_GRAMSEATEN)))
		}
	}


}