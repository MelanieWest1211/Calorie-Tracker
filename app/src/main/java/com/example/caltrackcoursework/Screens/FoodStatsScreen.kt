package com.example.caltrackcoursework.Screens

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.caltrackcoursework.Data.DailyTotals
import com.example.caltrackcoursework.ViewModel.FoodViewModel
import java.time.LocalDate
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/*This page displays food stat information for the past week, and serves as the default
* homepage of the app*/
/*This function is what is initially called when this page is navigated*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DisplayFoodStats(FoodViewModel: FoodViewModel, NavController: NavHostController) {
	val Context = LocalContext.current
	/*This contains a list of all total calories for the displayed week*/
	val TotalList = FoodViewModel.WeeklyTotals.value
	/*These lists store subcategories of the TotalList data*/
	val BreakfastTotal = TotalList.sumOf { it.TotalBreakfastCals }
	var LunchTotal = TotalList.sumOf { it.TotalLunchCals }
	var DinnerTotal = TotalList.sumOf { it.TotalDinnerCals }
	var SnackTotal = TotalList.sumOf { it.TotalSnackCals }
	var AllTotal = TotalList.sumOf { it.AllTotalCals }
	/*This stores the users calorie target*/
	val CalorieTarget by FoodViewModel.CalorieTarget.collectAsState(initial = 2000)
	var NewCalTarget by remember { mutableStateOf("") }
	var NewTargetErrorMessage by remember { mutableStateOf("") }
	/*This determines whether or not the UI used to change the calorie target
	should be displayed*/
	var DisplayCalorieChangeUI by remember { mutableStateOf(false) }
	/*This stores the last day of the week the table is currently displaying*/
	var InitialDate = FoodViewModel.TotalTableDisplayedDate
	/*This determines whether or not the week forward arrow should be displayed,
	and is based on whether or not the week being displayed is the most recent week*/
	var DisplayForwardArrow = InitialDate != FoodViewModel.CurrentDate;
	/*These values are used for styling my table, they are simply there to make
	styling changes easier*/
	val TextSize = 16.sp
	val RowHeight = 55.dp;
	val VerticalDividerColour = 0XFF6281b5
	/*This is called whenever either the week back arrow or week forward arrow are clicked and
	update the final weekday to be displayed*/
	LaunchedEffect(FoodViewModel.TotalTableWeekOffset)
	{
		FoodViewModel.TotalTableDisplayedDate = FoodViewModel.CurrentDate.minusDays(FoodViewModel.TotalTableWeekOffset * 7L)
	}
	/*This is used whenever there are changes to the date that should be displayed
	and is used to fetch the week totals for the new final date*/
	LaunchedEffect(FoodViewModel.TotalTableDisplayedDate)
	{
		FoodViewModel.GetWeeklyTotals(InitialDate)
	}
	/*The UI for my page is in a lazycolumn to allow for scrolling*/
	LazyColumn(modifier=Modifier.padding(horizontal=16.dp,vertical=5.dp)) {
		item {
			/*This displays my calorie target and contains a button which lets users set a new target*/
			Column {
				Text(
					"Calorie target information",
					fontSize = 27.sp,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(vertical = 5.dp)
				)
				Text(
					buildAnnotatedString{
						append("Your calorie target:")
						withStyle(style= SpanStyle(color = Color(0xff5479b8),fontWeight=FontWeight.Bold)){
						append("${CalorieTarget}")}},fontSize=20.sp)
				Button(onClick = { DisplayCalorieChangeUI = true })
				{
					Text("Change Calorie target", fontSize=16.sp)
				}
			}
		}
		item {
			/*This contains a display that lets a user set new calorie targets*/
			if (DisplayCalorieChangeUI) {
				Column {
					TextField(
						value = NewCalTarget,
						onValueChange = { NewCalTarget = it },
						label = { Text("New Calorie Target: ") },
						modifier= Modifier.fillMaxWidth())
					Row {
						Button(({ DisplayCalorieChangeUI = false }))
						{
							Text("Cancel")
						}
						Spacer(modifier=Modifier.weight(1f))
						Button(({
							NewTargetErrorMessage = SetNewCalTarget(NewCalTarget, FoodViewModel)
							if(NewTargetErrorMessage == "")
							{
								DisplayCalorieChangeUI = false
							}
						}))
						{
							Text("Set new target")
						}
					}
					/*This is displayed if there is an error, such as if a user enters a string
					in the new calorie target textbox*/
					if (NewTargetErrorMessage != "") {
						Text(NewTargetErrorMessage,color=Color.Red)
					}
				}
			}
		}
		/*This displays information about my weekly calorie history table*/
		item{
			Column{
				Text("Weekly calorie history",fontSize=27.sp,fontWeight= FontWeight.Bold,modifier=Modifier.padding(vertical=5.dp))
				Box(modifier=Modifier.border(width=1.dp,Color.LightGray,shape=RoundedCornerShape(12.dp)).background(color = Color(0xffd9e6fc),shape=RoundedCornerShape(12.dp)))
				{
					Column(modifier=Modifier.padding(vertical=5.dp,horizontal=10.dp)){
						Text("Table key",fontSize=20.sp,fontWeight= FontWeight.Bold)
						Text("B=Breakfast, L=Lunch, D=Dinner, S=Snacks, T=Total, <- = See previous week, -> = See next week")
					}
				}
			}
		}
		item{
			Row(modifier=Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
				/*This button allows users to view data about previous weeks*/
				Button(onClick = { FoodViewModel.TotalTableWeekOffset++ })
				{
					Icon(
						imageVector = Icons.AutoMirrored.Filled.ArrowBack,
						contentDescription = "previousweek"
					)
				}
				Spacer(modifier=Modifier.weight(1f))
				/*This allows users to view data about the week after the one they are currently
				viewing. It only displays if the week viewed is not the current week to prevent users
				editing future week data*/
				if (DisplayForwardArrow) {
					Button(onClick = { FoodViewModel.TotalTableWeekOffset-- })
					{
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowForward,
							contentDescription = "nextweek"
						)
					}
				}
			}
		}
		/*This displays my weekly calorie data table*/
		item {
			Column(
				modifier = Modifier.border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
			)
			{
				Column {
					/*This displays my header with column information*/
					Row(
						modifier = Modifier.fillMaxWidth().height(40.dp).padding(top=4.dp), verticalAlignment= Alignment.CenterVertically
					)
					{
						Text("Date", modifier = Modifier.weight(3f), textAlign = TextAlign.Center, fontSize=TextSize)
						Text("B", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize=TextSize)
						Text("L", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize=TextSize)
						Text("D", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize=TextSize)
						Text("S", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize=TextSize)
						Text("T", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize=TextSize)
					}
					HorizontalDivider(Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally).padding(bottom=5.dp), color=Color.LightGray)
					/*This loops through and displays food total data for the last 7 days*/
					for (DailyTotals in TotalList) {
						Row(
							/*Adds functionality so that my app navigates to the daily meal information
							for a given date when it is clicked*/
							modifier = Modifier.fillMaxWidth().clickable {
								FoodViewModel.DailyMealsDisplayedDate =
									LocalDate.parse(DailyTotals.DateOfTotal)
								FoodViewModel.DailyMealsDisplayedDateString =
									FoodViewModel.DailyMealsDisplayedDate.toString()
								NavController.navigate("DailyMeals")
							}.height(RowHeight), verticalAlignment= Alignment.CenterVertically
						)
						{
							Text(
								DailyTotals.DateOfTotal,
								modifier = Modifier.weight(3f),
								textAlign = TextAlign.Center, fontSize=TextSize
							)
							VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
							Text(
								"${DailyTotals.TotalBreakfastCals}",
								modifier = Modifier.weight(1f),
								textAlign = TextAlign.Center, fontSize=TextSize
							)
							VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
							Text(
								"${DailyTotals.TotalLunchCals}",
								modifier = Modifier.weight(1f),
								textAlign = TextAlign.Center, fontSize=TextSize
							)
							VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
							Text(
								"${DailyTotals.TotalDinnerCals}",
								modifier = Modifier.weight(1f),
								textAlign = TextAlign.Center, fontSize=TextSize
							)
							VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
							Text(
								"${DailyTotals.TotalSnackCals}",
								modifier = Modifier.weight(1f),
								textAlign = TextAlign.Center, fontSize=TextSize
							)
							VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
							Text(
								"${DailyTotals.AllTotalCals}",
								modifier = Modifier.weight(1f),
								textAlign = TextAlign.Center, fontSize=TextSize
							)
						}
					}
					/*Displays total calories for the week*/
					Row(
						modifier = Modifier.fillMaxWidth().height(RowHeight), verticalAlignment= Alignment.CenterVertically
					)
					{
						Text(
							"Total Calories",
							modifier = Modifier.weight(3f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${BreakfastTotal}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${LunchTotal}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${DinnerTotal}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${SnackTotal}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${AllTotal}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
					}
					/*Displays calorie averages for the week*/
					Row(
						modifier = Modifier.fillMaxWidth().height(RowHeight).padding(bottom=12.dp), verticalAlignment= Alignment.CenterVertically
					)
					{
						Text(
							"Average \nCalories",
							modifier = Modifier.weight(3f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${BreakfastTotal / 7}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${LunchTotal / 7}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${DinnerTotal / 7}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${SnackTotal / 7}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
						VerticalDivider(modifier = Modifier.fillMaxHeight(), color=Color(VerticalDividerColour))
						Text(
							"${AllTotal / 7}",
							modifier = Modifier.weight(1f),
							textAlign = TextAlign.Center, fontSize=TextSize
						)
					}
				}
			}
		}
		item{
			/*Displays buttons allowing users to share meal data*/
			Row(modifier = Modifier.padding(horizontal=16.dp,vertical=10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				Button(
					onClick = {
						/*Calls a function which allows for data sharing via android sharesheet*/
						ShareWeekData(Context, FoodViewModel, TotalList,
							true, BreakfastTotal, LunchTotal,
							DinnerTotal, SnackTotal, AllTotal)
					},
					modifier=Modifier.weight(7f)
				)
				{
					Row(
						modifier=Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically
					){
						Icon(
							imageVector = Icons.Default.Share,
							contentDescription = "Week share icon"
						)
						Spacer(modifier=Modifier.width(10.dp))
						Text("Share all weekly data", textAlign = TextAlign.Center)
					}
				}
				Button(
					onClick = {
						/*Calls a function which allows for data sharing via android sharesheet*/
						ShareWeekData(Context, FoodViewModel, TotalList,
							false, BreakfastTotal, LunchTotal,
							DinnerTotal, SnackTotal, AllTotal)
					},
					modifier=Modifier.weight(8f)
				)
				{
					Row(
						modifier=Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically
					){
						Icon(
							imageVector = Icons.Default.Share,
							contentDescription = "Week share icon"
						)
						Spacer(modifier=Modifier.width(10.dp))
						Text("Share total and average only",textAlign=TextAlign.Center)
					}
				}
			}
		}
	}
}

/*This function updates the calorie target*/
@RequiresApi(Build.VERSION_CODES.O)
fun SetNewCalTarget(NewTargetValue: String, FoodViewModel: FoodViewModel): String
{
	try {
		val IntCalTarget: Int = NewTargetValue.toInt()
		/*This view model is what updates the calorie value in my usercal datastore*/
		if(IntCalTarget > 0)
		{
			FoodViewModel.UpdateTargetCals(IntCalTarget)
			return ""
		}
		else
		{
			return "Please make sure you enter a positive number"
		}
	}
	catch(e: NumberFormatException)
	{
		return "Please enter your chosen target as an integer"
	}
}

/*This function allows users to share data about a given week via sharesheet*/
@RequiresApi(Build.VERSION_CODES.O)
fun ShareWeekData(Context: Context, FoodViewModel: FoodViewModel, AllWeekTotals: List<DailyTotals>, ShowAllWeekData: Boolean, BreakfastTotal: Int, LunchTotal: Int, DinnerTotal: Int, SnackTotal: Int, AllTotal: Int)
{
	var ShareIntentText = ""
	/*If the data being shared is not from the past week, this automatically specifies the week the data corresponds to*/
	if(FoodViewModel.TotalTableDisplayedDate == FoodViewModel.CurrentDate)
	{
		ShareIntentText = "Here are my calorie totals for the past week:"
	}
	else
	{
		ShareIntentText = "Here are my calorie totals for the week between ${FoodViewModel.TotalTableDisplayedDate.minusDays(7)} and ${FoodViewModel.TotalTableDisplayedDate}:"
	}
	/*This function adds additional total information if the user clicked on the 'share all weekly data' button*/
	if(ShowAllWeekData)
	{
		ShareIntentText = ShareIntentText + CompileAllDayTotals(AllWeekTotals)
	}
	/*Adds the total meal data for the past week to the share intent, as well as calorie averages*/
	ShareIntentText = ShareIntentText + "\nMy total calories for the whole week were: \n" +
			"Breakfast: ${BreakfastTotal} Calories\nLunch: ${LunchTotal} Calories\nBreakfast: ${SnackTotal} Calories\n" +
			"Overall Total: ${AllTotal} Calories\nMy average calories per meal this week were: \n" +
			"Breakfast Average:  ${BreakfastTotal/7} Calories\nLunch Average:  ${LunchTotal/7} Calories\n" +
			"Dinner Average:  ${DinnerTotal/7} Calories\nSnack Average:  ${SnackTotal/7} Calories\n" +
			"Overall Average:  ${AllTotal/7} Calories"
	/*Shares the shareintent message via android sharesheet*/
	val ShareIntent = Intent().apply {
		action = Intent.ACTION_SEND
		putExtra(Intent.EXTRA_TEXT, ShareIntentText)
		type = "text/plain"
	}
	Context.startActivity(
		Intent.createChooser(ShareIntent, "Share Via:")
	)
}

/*This function is only called if the user clicked on the more details data sharing option
and compiles calorie totals for every meal category for every weekday*/
fun CompileAllDayTotals(AllWeekTotals: List<DailyTotals>): String
{
	var WeeklyTotalMessage = ""
	for(TotalDay in AllWeekTotals)
	{
		WeeklyTotalMessage = WeeklyTotalMessage + "\n${TotalDay.DateOfTotal}:" +
				"\n  - Breakfast: ${TotalDay.TotalBreakfastCals} Calories\n  - " +
				"Lunch: ${TotalDay.TotalLunchCals} Calories\n  - " +
				"Dinner: ${TotalDay.TotalDinnerCals} Calories\n  - " +
				"Snacks: ${TotalDay.TotalSnackCals}\n  - " +
				"Total: ${TotalDay.AllTotalCals} Calories"
	}
	return WeeklyTotalMessage
}