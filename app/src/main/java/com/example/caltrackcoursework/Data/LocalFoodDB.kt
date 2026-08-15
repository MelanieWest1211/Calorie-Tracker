package com.example.caltrackcoursework.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailyFood::class], version =2, exportSchema = false)
abstract class LocalFoodDatabase: RoomDatabase()
{
	/*Fetches an instance of my LocalFoodDao so the DAO queries can be performed*/
	abstract fun LocalFoodDao(): LocalFoodDao
	companion object
	{
		@Volatile
		//Creates initial LocalFood instance
		private var Instance: LocalFoodDatabase? = null
		fun getDatabase(context: Context):LocalFoodDatabase {
			/*If the instance exists it is returned, otherwise a new instance is created*/
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, LocalFoodDatabase::class.java, "FoodDB")
					.build().also { Instance = it }
			}
		}

	}
}