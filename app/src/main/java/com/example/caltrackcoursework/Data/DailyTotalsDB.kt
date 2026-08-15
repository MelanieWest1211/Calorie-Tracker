package com.example.caltrackcoursework.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.caltrackcoursework.Data.DailyTotalsDao

@Database(entities = [DailyTotals::class], version =1, exportSchema = false)
abstract class DailyTotalDatabase: RoomDatabase()
{
	//Fetches my dao for the daily totals database
	abstract fun DailyTotalsDao(): DailyTotalsDao
	companion object
	{
		@Volatile
		//Creates initial instance
		private var Instance: DailyTotalDatabase? = null
		fun getDatabase(context: Context):DailyTotalDatabase {
			//This checks if an instance already exists. If it is it is returned, otherwise a new instance is created
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, DailyTotalDatabase::class.java, "DailyTotalDB")
					.build().also { Instance = it }
			}
		}

	}
}