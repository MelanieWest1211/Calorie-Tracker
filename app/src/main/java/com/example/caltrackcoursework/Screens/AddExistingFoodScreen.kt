package com.example.caltrackcoursework.Screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caltrackcoursework.Data.DailyFood
import com.example.caltrackcoursework.ViewModel.FoodViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/*This composable function is what initially runs when the screen is entered*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddNewMeal(NavController: NavController, FoodViewModel: FoodViewModel, MealDate: String, MealType: String, MealName: String, GramsEaten: String, CalsPer100: Int, OldMealID: Int)
{
	Log.d("DEBUG","Meal type = $MealType")
	/*This stores the value of the name of the meal being added - it is set to MealName
	initially, as this page is also used for editing existing meals, meaning the original name
	would initially be displayed*/
	var NewMealName by rememberSaveable {mutableStateOf(MealName)}
	/*BrandNewMealName is only used if a user wants to create an entirely new meal, so that it can be
	 added to the database without causing an error if a user already entered a value in the
	 choose food item dropdown*/
	var BrandNewMealName by rememberSaveable {mutableStateOf("")}
	/*This stores the amount of food eaten (in grams) of the meal being added*/
	var GramsEaten by rememberSaveable {mutableStateOf(GramsEaten)}
	/*This stores the number of calories per 100 grams when an already existing
	 meal type is being logged*/
	var CalsPer100 by rememberSaveable {mutableStateOf(CalsPer100)}
	/*This is used to store the calories (per 100g) of a new meal a user is creating*/
	var NewCalsPer100 by rememberSaveable {mutableStateOf("")}
	/*This is used to display an error message if a user enters an invalid value in grams eaten,
	or any other input box*/
	var ErrorMessage by rememberSaveable {mutableStateOf(value="")}
	/*This stores whether or not the food list dropdown should be displayed*/
	var expanded by remember { mutableStateOf(false) }
	/*This stores whether or not the UI to create a new meal should be displayed*/
	var CreateNewFoodUI by rememberSaveable {mutableStateOf(value=false)}
	/*This stores a list of all firebase food data*/
	val FirebaseFoodList = FoodViewModel.FirebaseFoodList.collectAsState();
	/*This stores a list of all the food names in my firebase DB*/
	val FoodNameList = FirebaseFoodList.value
	/*This stores the value inside my choose food item dropdown, if nothing has been chosen yet
	text prompting the user to select an item is displayed*/
	var CurrentSelectedText by rememberSaveable { mutableStateOf("Select food item")}
	/*This indirectly checks if the user is editing an existing meal entry and will initially display
	the name of the meal being edited if this is the case*/
	if (NewMealName != "")
	{
		CurrentSelectedText = NewMealName
	}
	/*All UI is displayed in a lazy column so that users can scroll down to view the rest of the page
	 if necessary*/
	LazyColumn(modifier = Modifier.padding(horizontal=16.dp,vertical=5.dp)){
		/*This section displays the add new meal title and food item dropdown*/
		item {
			Text(
				text = "Add new meal",fontSize=27.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp)
			)
			Box(
				modifier = Modifier
					.padding(16.dp)
			)
			{
				Row(verticalAlignment = Alignment.CenterVertically)
				{
					Text("Choose food item: ",modifier=Modifier.width(140.dp))
					Box(
						modifier = Modifier
							.border(1.dp, Color.Gray)
							.padding(15.dp)
							.clickable { expanded = true }
							.width(200.dp)
					)
					{
						Row {
							Text(CurrentSelectedText)
							Icon(
								imageVector = Icons.Default.ArrowDropDown,
								contentDescription = "dropdown arrow"
							)
						}
						/*Ensures that clicking on the selection box will expand/hide the menu*/
						DropdownMenu(
							expanded = expanded,
							onDismissRequest = { expanded = false },
							modifier = Modifier.width(150.dp)
						) {
							/*This updates my meal data with the values for the item
							in the dropdown list when it is selected*/
							FoodNameList.forEach { option ->
								DropdownMenuItem(
									text = { Text(option.FoodName) },
									onClick = {
										CurrentSelectedText = option.FoodName
										NewMealName = option.FoodName
										CalsPer100 = option.CaloriesPer100
									}
								)
							}
						}
					}
				}
			}
		}
		/*Displays the grams eaten text input box*/
		item {
			Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp))
			{
				Text("Enter grams eaten: ",modifier=Modifier.width(140.dp))
				/*Updates gramseaten when the textbox value is changed*/
				TextField(
					value = GramsEaten,
					onValueChange = { GramsEaten = it},
					singleLine = true)
			}
		}
		item {
			Text("Can't find the food you're looking for? Enter it manually by clicking the create new food button!",modifier = Modifier.padding(vertical = 10.dp),fontStyle= FontStyle.Italic,color=Color(0xffb4b8b5))
		}
		/*Displays the add food, cancel and create new food buttons*/
		item {
			Row(modifier=Modifier.fillMaxWidth(),horizontalArrangement= Arrangement.spacedBy(12.dp)) {
				Button(
					onClick = {
						/*Calls a function that will log the meal data into my database*/
						ErrorMessage = CreateNewMeal(
							NavController,
							FoodViewModel,
							NewMealName,
							MealType,
							MealDate,
							GramsEaten,
							CalsPer100,
							OldMealID
						);
					}
				)
				{
					Text("Add food")
				}
				Button(
					onClick = {
						/*This will navigate back to the previous screen, ensuring no new data is logged*/
						NavController.popBackStack()
					}
				)
				{
					Text("Cancel")
				}
				Button(
					onClick = {
						/*Displays UI which lets users create a new type of food to be stored in firebase*/
						CreateNewFoodUI = true
					}
				)
				{
					Text("Create new food")
				}

			}
		}
		/*Displays the UI that lets users create a new meal and add it to the firebase database*/
		if(CreateNewFoodUI)
		{
			item {
				Column {
					Text(
						text = "Create New Meal",fontSize=27.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp)
					)
					Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp))
					{
						Text("Meal Name: ",modifier=Modifier.width(110.dp))
						TextField(
							value = BrandNewMealName,
							onValueChange = { BrandNewMealName = it })
					}
					Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp))
					{
						Text("Grams eaten: ",modifier=Modifier.width(110.dp))
						TextField(
							value = GramsEaten,
							onValueChange = {
								GramsEaten = it
								Log.d("DEBUG", "${GramsEaten}")
							})
					}
					Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp))
					{
						Text("Cals/100g: ",modifier=Modifier.width(110.dp))
						TextField(
							value = NewCalsPer100,
							onValueChange = { NewCalsPer100 = it })
					}
					Button(
						onClick = {
							/*Calls a function that will add the new meal to the firebase database*/
							ErrorMessage = AddNewFoodToDB(
								NavController,
								FoodViewModel,
								BrandNewMealName,
								MealType,
								MealDate,
								GramsEaten,
								NewCalsPer100,
								OldMealID
							);
						}
					)
					{
						Text("Create new food")
					}
					Text("Note: Creating a new meal will automatically add it to your meal log",modifier = Modifier.padding(vertical = 10.dp),fontStyle= FontStyle.Italic,color=Color(0xffb4b8b5))
				}
			}
		}
		/*If invalid data has been entered, an error message is displayed here*/
		if(ErrorMessage != null)
		{
			item {
				Text(ErrorMessage,color=Color.Red)
			}
		}
	}
}


/*This function adds a new meal to the firebase database, before calling a function that will log it locally*/
/*This function and CreateNewMeal return strings in case there is an error, so that an error message can be displayed*/
@RequiresApi(Build.VERSION_CODES.O)
fun AddNewFoodToDB(NavController: NavController, FoodViewModel: FoodViewModel, MealName: String, MealType: String, DateEaten: String, GramsEaten: String, CalsPer100: String, OldMealID:Int): String {
	try {
		Log.d("DEBUG","Add new meal called - cals per 100 - $CalsPer100 - Grams eaten = $GramsEaten")
		/*This value is converted to an integer as it is stored as one in firebase*/
		val CalsPer100Int = CalsPer100.toInt();
		var FirebaseDB = FirebaseFirestore.getInstance();
		val TestData = hashMapOf(
			"CaloriesPer100" to CalsPer100Int,
			"FoodName" to MealName
		)
		/*Adding the data to the firebase database*/
		FirebaseDB.collection("FoodData").document(MealName)
			.set(TestData, SetOptions.merge())
		/*This calls a function that will log the meal for the given day*/
		return CreateNewMeal(NavController, FoodViewModel, MealName, MealType, DateEaten, GramsEaten, CalsPer100Int, OldMealID)
	}
	catch (e : NumberFormatException)
	{
		return "Please make sure you enter the calories eaten and calories per 100g as an integer";
	}
}

/*This function adds a meal log to my dailymeals database*/
@RequiresApi(Build.VERSION_CODES.O)
fun CreateNewMeal(NavController: NavController, FoodViewModel: FoodViewModel, MealName: String, MealType: String, DateEaten: String, GramsEaten: String, CalsPer100: Int, OldMealID:Int): String
{
	try
	{
		/*Converting grams eaten to int as it is stored as an int in my database*/
		val GramsEatenInt = GramsEaten.toInt();
		/*This calculates the number of calories eaten, based on the number of grams eaten and the calories in 100g of the food*/
		val CaloriesEaten = GramsEatenInt/100 * CalsPer100
		/*Creates a new meal object and stores it in my dailymeals database*/
		val NewMeal = DailyFood(
			FoodName = MealName,
			CaloriesEaten = CaloriesEaten,
			DateEaten = DateEaten,
			MealType = MealType,
			GramsEaten = GramsEatenInt,
			CalsPer100 = CalsPer100
		)
		FoodViewModel.AddNewMeal(NewMeal)
		/*This checks the MealType. This was originally a parameter passed in when the user first navigated into the screen and
		has been passed down into this function. This is checked to ensure that the correct total values are updated*/
		if (MealType == "Breakfast")
		{
			FoodViewModel.UpdateBreakfastTotal(DateEaten, CaloriesEaten)
		}
		else if (MealType == "Lunch")
		{
			FoodViewModel.UpdateLunchTotal(DateEaten, CaloriesEaten)
		}
		else if (MealType == "Dinner")
		{
			FoodViewModel.UpdateDinnerTotal(DateEaten, CaloriesEaten)
		}
		else if (MealType == "Snacks")
		{
			FoodViewModel.UpdateSnackTotal(DateEaten, CaloriesEaten)
		}
		/*OldMealID is originally passed in and will have a value of 0, unless an existing meal is being edited.
		If this is the case, this deletes the original meal entry for this meal, so that it can be replaced with an entry
		with the edited values*/
		if(OldMealID != 0)
		{
			FoodViewModel.DeleteMealEntry(DateEaten,MealType,CaloriesEaten,OldMealID)
		}
		NavController.popBackStack();
		return "";
	}
	/*Error message in case a user entered a string in the 'grams eaten' input box*/
	catch (e : NumberFormatException)
	{
		return "Please make sure you enter the calories eaten as an integer";
	}
}
