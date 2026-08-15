package com.example.caltrackcoursework.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

/*This structures my daily food database*/
@Entity(tableName = "DailyFood")
data class DailyFood(
	/*Automatically generated primary key*/
	@PrimaryKey(autoGenerate = true) val id:Int = 0,
	/*It stores the name of the meal, the number of calories eaten, the date it
	* was eaten, the type of meal (e.g. breakfast, lunch, dinner or snack), the
	* number of grams eaten and the number of calories in 100 grams of the meal
	* (I store this here as it is needed when editing an existing instance of the
	* meal)*/
	val FoodName:String,
	val CaloriesEaten:Int,
	val DateEaten: String,
	val MealType: String,
	val GramsEaten: Int,
	val CalsPer100: Int
)