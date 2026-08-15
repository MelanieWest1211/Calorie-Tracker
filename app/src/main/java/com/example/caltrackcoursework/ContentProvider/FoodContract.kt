package com.example.caltrackcoursework.ContentProvider

import android.net.Uri

object FoodContract {
	const val AUTHORITY =
		"com.caltrackcoursework.ContentProvider.ContentProvider"
	val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")
	/*This is used to specify path and column names when accessing the dailyfood database*/
	object DailyFoodContract {
		/*DailyFood path setup*/
		const val PATH_DAILYFOOD = "DailyMeals"
		val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DAILYFOOD)
		const val CONTENT_TYPE =
			"vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_DAILYFOOD"
		const val CONTENT_ITEM_TYPE =
			"vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_DAILYFOOD"
		/*Dailyfood column naming*/
		const val COLUMN_ID = "id"
		const val COLUMN_FOODNAME = "FoodName"
		const val COLUMN_DATEEATEN = "DateEaten"
		const val COLUMN_CALORIESEATEN = "CaloriesEaten"
		const val COLUMN_MEALTYPE = "MealType"
		const val COLUMN_GRAMSEATEN = "GramsEaten"
		const val COLUMN_CALSPER100 = "CalsPer100"
	}
	/*This is used to specify path and column names when accessing the foodtotals database*/
	object FoodTotalsContract {
		/*Foodtotals path setup*/
		const val PATH_FOODTOTALS = "FoodTotals"
		val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOODTOTALS)
		const val CONTENT_TYPE =
			"vnd.android.cursor.dir/vnd.$AUTHORITY.$PATH_FOODTOTALS"
		const val CONTENT_ITEM_TYPE =
			"vnd.android.cursor.item/vnd.$AUTHORITY.$PATH_FOODTOTALS"
		/*Foodtotals column naming*/
		const val COLUMN_ID = "id"
		const val COLUMN_DATEOFTOTAL = "DateOfTotal"
		const val COLUMN_BREAKFASTTOTAL = "TotalBreakfastCals"
		const val COLUMN_LUNCHTOTAL = "TotalLunchCals"
		const val COLUMN_DINNERTOTAL = "TotalDinnerCals"
		const val COLUMN_SNACKTOTAL = "TotalSnackCals"
		const val COLUMN_OVERALLTOTAL = "AllTotalCals"
	}
}
