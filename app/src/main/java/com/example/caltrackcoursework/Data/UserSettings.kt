package com.example.caltrackcoursework.Data

/*This data class is simply used to make structuring the
notification setting datastore easier*/
data class UserSettings (
	val AllNotifications: Boolean = true,
	val BreakfastNotifications: Boolean = true,
	val LunchNotifications: Boolean = true,
	val DinnerNotifications: Boolean = true
)