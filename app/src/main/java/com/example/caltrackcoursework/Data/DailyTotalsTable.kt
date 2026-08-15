package com.example.caltrackcoursework.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*This class is used to structure my daily totals database*/
@Entity(tableName = "DailyTotals")
data class DailyTotals(
	/*The primary key is automatically generated*/
	@PrimaryKey(autoGenerate = true) val id:Int = 0,
	/*My database stores totals for breakfast, lunch, dinner and snacks for a given day,
	as well as an overall total so that this doesn't need to be computed every time it is needed*/
	val DateOfTotal: String,
	val TotalBreakfastCals:Int,
	val TotalLunchCals:Int,
	val TotalDinnerCals:Int,
	val TotalSnackCals:Int,
	val AllTotalCals:Int
)