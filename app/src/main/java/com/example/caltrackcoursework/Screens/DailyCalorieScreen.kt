package com.example.caltrackcoursework.Screens

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.caltrackcoursework.Data.DailyFood
import com.example.caltrackcoursework.Data.DailyTotals
import com.example.caltrackcoursework.ViewModel.FoodViewModel

/*This function is what is initially run when this screen is entered*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayDailyFood(FoodViewModel: FoodViewModel, NavController: NavHostController, modifier: Modifier = Modifier)
{
	/*This stores the date the user is currently looking at meal logs for*/
	var DisplayedDateString: String = FoodViewModel.DailyMealsDisplayedDateString
	val Context = LocalContext.current
	/*This stores a list of all food totals for the displayed day*/
	val TotalList by FoodViewModel.GetAllTotals(DisplayedDateString).collectAsState(initial = emptyList())
	/*This checks if the current date displayed has a total data list yet, and creates one if it does not*/
	LaunchedEffect(DisplayedDateString)
	{
		FoodViewModel.CheckForAndCreateTotals(DisplayedDateString)
	}
	/*This stores the total number of calories eaten for a given day*/
	val WholeDayTotal = TotalList.firstOrNull()?.AllTotalCals ?: 0
	/*Lists of data for breakfast, lunch, dinner and snack meals*/
	val BreakfastList by FoodViewModel.GetBreakfast(DisplayedDateString).observeAsState(emptyList());
	val LunchList by FoodViewModel.GetLunch(DisplayedDateString).observeAsState(emptyList());
	val DinnerList by FoodViewModel.GetDinner(DisplayedDateString).observeAsState(emptyList());
	val SnackList by FoodViewModel.GetSnacks(DisplayedDateString).observeAsState(emptyList());
	/*This stores the users current calorie target*/
	val CalorieTarget by FoodViewModel.CalorieTarget.collectAsState(initial=2000)
	/*This is used to measure horizontal swiping, as this is how users navigate between days*/
	var TotalDrag by remember{mutableFloatStateOf(0f)}
	/*All UI is inside a lazy column to allow for scrolling*/
	LazyColumn(
		modifier = modifier.padding(horizontal=16.dp,vertical=5.dp).pointerInput(Unit)
		{
			/*This checks if a user is swiping horizontally. Once they stop, a function is called which will
			change the date being displayed if appropriate*/
			detectHorizontalDragGestures( onDragStart = {}, onHorizontalDrag =  { DragChange, DragAmount ->
				TotalDrag = DragAmount },
				onDragEnd =
				{
					FoodViewModel.HandleSwipe(TotalDrag);
					TotalDrag = 0f
				}
				)
		}
	) {
		/*Displays all food data for a given day*/
		item {
			Text(
				buildAnnotatedString{
					append("Date:")
					withStyle(style= SpanStyle(color = Color(0xff5479b8),fontWeight=FontWeight.Bold)){
						append(DisplayedDateString)}},fontSize=27.sp,fontWeight = FontWeight.Bold)
			/*I use the DisplayFood function to display all meal data for a meal category, as they are being displayed in an identical manner*/
			DisplayFood(
				FoodViewModel,
				NavController,
				DisplayedDateString,
				"Breakfast",
				BreakfastList,
				modifier,
				TotalList
			)
			DisplayFood(
				FoodViewModel,
				NavController,
				DisplayedDateString,
				"Lunch",
				LunchList,
				modifier,
				TotalList
			)
			DisplayFood(
				FoodViewModel,
				NavController,
				DisplayedDateString,
				"Dinner",
				DinnerList,
				modifier,
				TotalList
			)
			DisplayFood(
				FoodViewModel,
				NavController,
				DisplayedDateString,
				"Snacks",
				SnackList,
				modifier,
				TotalList
			)
		}
		/*Displays the calorie total for the displayed date, as well as buttons to share food data*/
		item {
			Text("Total: " + WholeDayTotal,fontSize=27.sp,fontWeight=FontWeight.Bold)
			Button(
				/*Calls a function which allows users to share logs for a given day via sharesheet*/
				onClick = {
					ShareDayData(Context, CalorieTarget,TotalList, BreakfastList, LunchList, DinnerList, SnackList)
				}
			)
			{
				Row(verticalAlignment=Alignment.CenterVertically)
				{
					Icon(
						imageVector = Icons.Default.Share,
						contentDescription = "Share today's data"
					)
					Spacer(modifier=Modifier.width(10.dp))
					Text("Share today's meals", fontSize=18.sp, textAlign=TextAlign.Center)
				}
			}
		}
	}
}

/*This function is used to display data about a given meal type (breakfast, lunch, dinner, snack)*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayFood(FoodViewModel: FoodViewModel, NavController: NavHostController, CurrentDate: String, MealName: String, ListToDisplay: List<DailyFood>, modifier: Modifier, TotalList: List<DailyTotals>)
{
	/*Haptic feedback if a user holds down on meal data*/
	val Haptics = LocalHapticFeedback.current
	Text(
		text = MealName,fontSize=27.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp)
	)
	Column {
		Box(
			modifier = Modifier.fillMaxWidth().border(
				width = 1.dp,
				Color.LightGray,
				shape = RoundedCornerShape(12.dp)
			).background(color = Color(0xffd9e6fc), shape = RoundedCornerShape(12.dp))
		)
		{
			Column {
				if (ListToDisplay.isNotEmpty()) {
					/*This loops through every meal in the respective type list and displays it */
					for (Meal in ListToDisplay) {
						/*These variables are used to determine if an item is being long-pressed and
						if the editing dropdown menu should be displayed*/
						var DisplayDropdown by remember { mutableStateOf(false) }
						var CurrentlyPressed by remember { mutableStateOf(false) }
						val PressAnimation by animateFloatAsState(
							if (CurrentlyPressed) 0.4f else 1f,
							label = ""
						)
						Text(
							text = Meal.FoodName + " - " + Meal.CaloriesEaten + " Cals",fontSize=18.sp,
							modifier =  modifier.padding(start= 16.dp, top=10.dp, end =16.dp, bottom=5.dp)
								.alpha(PressAnimation)
								.pointerInput(Unit)
								{
									/*Long-press detection*/
									detectTapGestures(
										onPress = {
											CurrentlyPressed = true
											try {
												awaitRelease()
											} finally {
												CurrentlyPressed = false
											}
										},
										onLongPress = {
											Haptics.performHapticFeedback(HapticFeedbackType.LongPress)
											/*DisplayDropdown is what determines if the edit dropdown menu is displayed
											for that meal item or not*/
											DisplayDropdown = true;
										}
									)
								}
						)
						/*Displays a drop down menu to allow users to edit or delete meals*/
						DropdownMenu(
							expanded = DisplayDropdown,
							onDismissRequest = { DisplayDropdown = false }
						)
						{
							DropdownMenuItem(
								{ Text("Delete item") },
								onClick = {
									/*This just deletes the meal entry from the menu*/
									FoodViewModel.DeleteMealEntry(
										Meal.DateEaten,
										Meal.MealType,
										Meal.CaloriesEaten,
										Meal.id
									)
								}
							)
							DropdownMenuItem(
								{ Text("Edit item") },
								onClick = {
									/*Navigates to the addexisting meal screen, passing the meal ID, meal type, date eaten and meal name in as parameters
									so that the entry can be successfully edited*/
									NavController.navigate("AddExistingMeal/${Meal.DateEaten}/${Meal.MealType}/${Meal.FoodName}/${Meal.GramsEaten}/${Meal.CalsPer100}/${Meal.id}")
								}
							)
						}
					}
				} else {
					Text("You haven't added anything to this yet!", fontSize=18.sp, modifier = modifier.padding(16.dp))
				}
			}
		}
	}
	/*Displays the total values for each meal category, checking beforehand which one should be displayed*/
	if (MealName == "Breakfast")
	{
		val BreakfastTotal = TotalList.firstOrNull()?.TotalBreakfastCals ?: 0
		Text("Total: "+BreakfastTotal, fontSize=20.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp))
	}
	else if (MealName == "Lunch")
	{
		val LunchTotal = TotalList.firstOrNull()?.TotalLunchCals ?: 0
		Text("Total: "+LunchTotal, fontSize=20.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp))
	}
	else if (MealName == "Dinner")
	{
		val DinnerTotal = TotalList.firstOrNull()?.TotalDinnerCals ?: 0
		Text("Total: "+DinnerTotal, fontSize=20.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp))
	}
	else if (MealName == "Snacks")
	{
		val SnackTotal = TotalList.firstOrNull()?.TotalSnackCals ?: 0
		Text("Total: "+SnackTotal, fontSize=20.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=10.dp))
	}
	/*Navigation to the add existing meal screen so a user can log a new meal for this meal type*/
	Button(
		onClick = {
			/*OldMealID is set to 0 to indicate a new meal is being added, rather than an existing one being edited*/
			NavController.navigate("AddExistingMeal/${CurrentDate}/${MealName}/${""}/${"0"}/${0}/${0}")
		}
	)
	{
		Text("Add food", fontSize=18.sp)
	}
}

/*This function lets users share food log data via android sharesheet*/
fun ShareDayData(Context: Context, CalorieTarget: Int, TotalList: List<DailyTotals>, BreakfastList: List<DailyFood>, LunchList: List<DailyFood>, DinnerList: List<DailyFood>, SnackList: List<DailyFood>)
{
	/*Building initial intent text, as well as the total breakfast calories eaten*/
	var ShareIntentText = "Hi, here are my total calories today. " +
			"\nBreakfast: ${TotalList.firstOrNull()?.TotalBreakfastCals ?: 0}"
	/*Adding data about every meal in the breakfast category for the day being shared*/
	for(BreakfastMeal in BreakfastList)
	{
		ShareIntentText = ShareIntentText + "\n${BreakfastMeal.FoodName}: ${BreakfastMeal.CaloriesEaten} Calories"
	}
	ShareIntentText = ShareIntentText + "\nLunch:${TotalList.firstOrNull()?.TotalLunchCals ?: 0}"
	for(LunchMeal in LunchList)
	{
		ShareIntentText = ShareIntentText + "\n${LunchMeal.FoodName}: ${LunchMeal.CaloriesEaten} Calories"
	}
	ShareIntentText = ShareIntentText + "\nDinner:${TotalList.firstOrNull()?.TotalDinnerCals ?: 0}"
	for(DinnerMeal in DinnerList)
	{
		ShareIntentText = ShareIntentText + "\n${DinnerMeal.FoodName}: ${DinnerMeal.CaloriesEaten} Calories"
	}
	ShareIntentText = ShareIntentText + "\nSnacks:${TotalList.firstOrNull()?.TotalSnackCals ?: 0}"
	for(SnackMeal in SnackList)
	{
		ShareIntentText = ShareIntentText + "\n${SnackMeal.FoodName}: ${SnackMeal.CaloriesEaten} Calories"
	}
	/*Displaying total calories eaten for that day*/
	ShareIntentText = ShareIntentText + "\nTotal:${TotalList.firstOrNull()?.AllTotalCals ?: 0}"
	/*Displaying the difference between calories eaten and the target, allowing for easier comparison*/
	val TargetActualCalDifference = CalorieTarget - (TotalList.firstOrNull()?.AllTotalCals ?: 0)
	if(TargetActualCalDifference > 0)
	{
		ShareIntentText = ShareIntentText+"\nThis is ${TargetActualCalDifference} calories below my target!";
	}
	else
	{
		ShareIntentText = ShareIntentText+"\nThis is ${TargetActualCalDifference*(-1)} calories above my target!";
	}
	/*Sharing via android sharesheet*/
	val ShareIntent = Intent().apply {
		action = Intent.ACTION_SEND
		putExtra(Intent.EXTRA_TEXT, ShareIntentText)
		type = "text/plain"
	}
	Context.startActivity(
		Intent.createChooser(ShareIntent, "Share Via:")
	)
}