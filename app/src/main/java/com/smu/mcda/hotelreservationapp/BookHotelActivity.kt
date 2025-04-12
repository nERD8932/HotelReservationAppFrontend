package com.smu.mcda.hotelreservationapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.smu.mcda.hotelreservationapp.network.Guest
import com.smu.mcda.hotelreservationapp.network.Guests
import com.smu.mcda.hotelreservationapp.network.HotelData
import com.smu.mcda.hotelreservationapp.network.SearchRequest
import com.smu.mcda.hotelreservationapp.network.SearchResults
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme
import kotlinx.parcelize.Parcelize

class BookHotelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
//            val options = ActivityOptions.makeCustomAnimation(
//                this@BookHotelActivity,
//                R.anim.slide_in_top,
//                R.anim.slide_out_bottom,
//            )
//            val intent = Intent(this@BookHotelActivity, SearchResultsActivity::class.java)
            finish()
        }

        val h = intent.getParcelableExtra<HotelData>("hotelData")!!
        val searchReq = intent.getParcelableExtra<SearchRequest>("searchReq")!!


        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {

                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    AddDetails(Modifier.padding(innerPadding), h, searchReq)
                    Box(contentAlignment = Alignment.TopStart,
                        modifier = Modifier.fillMaxWidth().padding(innerPadding).padding(10.dp, 0.dp)
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(5.dp)
                                .clickable {
                                    finish()
                                }
                        )
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddDetails(modifier: Modifier = Modifier, hotelInfo: HotelData, searchReq: SearchRequest) {

    var color by remember { mutableStateOf(Color.Unspecified) }
    color = if (isSystemInDarkTheme())
    {
        Color(0.137f, 0.149f, 0.188f, 1.0f)
    }
    else {
        Color(0.898f, 0.902f, 0.945f, 1.0f)
    }

    val context = LocalContext.current
    var guests by remember { mutableStateOf<List<Guest>>(
        listOf(
            Guest(0)
        )) }

    val alterUserCount = { op: String ->
        if(op == "add") guests += Guest(guests.size)
        else guests = guests.dropLast(1)
    }

    val intmap = mapOf(1 to "One", 2 to "Two", 3 to "Three", 4 to "Four")
    var error_text by remember { mutableStateOf("") }

    Column (modifier) {
        Box(
            modifier = Modifier.weight(0.1f).fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        )
        {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ){
                Row (horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Outlined.Info,
                        contentDescription = "Info",
                        modifier=Modifier.size(24.dp))
                    Spacer(Modifier.size(10.dp))
                    Text(
                        "Guest Details",
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        fontSize = TextUnit(6f, TextUnitType.Em),
                        modifier = Modifier
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp).fillMaxWidth())
        Box(Modifier.weight(0.15f).padding(10.dp, 0.dp), contentAlignment = Alignment.BottomCenter)
        {
            UserCounter(alter=alterUserCount)
        }

        Box(Modifier.weight(0.75f).padding(30.dp, 0.dp))
        {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(5.dp, 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                items(guests) { guest ->

                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .shadow(4.dp, shape = RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .background(color)
                            .animateItemPlacement()
                    ){
                        Row (Modifier.padding(15.dp, 15.dp, 15.dp, 0.dp)){
                            Text(
                                text = "Guest ${intmap[guests.indexOf(guest) + 1]}",
                                fontWeight = FontWeight.Medium
                                )
                        }
                        Row (Modifier.padding(12.dp, 0.dp, 15.dp, 12.dp)){
                            GuestInfo(guest)
                        }

                    }
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
        Box(
            Modifier.weight(0.15f).fillMaxWidth().padding(30.dp, 5.dp),
            contentAlignment = Alignment.Center
        )
        {
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Column (Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(error_text,
                    fontSize = TextUnit(3f, TextUnitType.Em),
                    textAlign = TextAlign.Center,
                    color = Color(0.686f, 0.329f, 0.329f),
                    modifier=Modifier.padding(5.dp))

                    Button(onClick = {
                        var hasEmail = false
                        var invalid = false
                        for(g in guests)
                        {
                            if (g.firstName.isEmpty()
                                || g.lastName.isEmpty())
                            {
                                error_text = "Please fill required details!"
                                invalid = true
                            }
                            if (g.firstName.isNotEmpty()) hasEmail = true
                        }
                        if(!hasEmail&&!invalid) error_text = "Please include an email address for the booking!"
                        else {
                            if(!invalid)
                            {
                                val temp = Guests(guests)
                                val intent = Intent(context, FinalizeActivity::class.java).apply {
                                    putExtra("hotelData", hotelInfo)
                                    putExtra("searchReq", searchReq)
                                    putExtra("guests", temp)
                                }
                                context.startActivity(intent)
                            }
                        }
                    }) {
                        Text("Go to checkout", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}


@Composable
fun GuestInfo(guest: Guest) {

    var fn by remember { mutableStateOf(guest.firstName) }
    var ln by remember { mutableStateOf(guest.lastName) }
    var age by remember { mutableStateOf(guest.age) }
    var email by remember { mutableStateOf(guest.email) }

    Column {
        Row {
            OutlinedTextField(
                value = fn,
                onValueChange = {
                    fn = it
                    guest.firstName = fn },
                label = { Text("First Name*") },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(5.dp)
            )
            OutlinedTextField(
                value = ln,
                onValueChange = {
                    ln = it
                    guest.lastName = ln },
                label = { Text("Last Name*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )
        }
        Row (Modifier.fillMaxWidth()){

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    guest.email = email
                },
                label = { Text("Email Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )
        }
        Row (Modifier.fillMaxWidth()){
            GenderDropdown(guest)
            OutlinedTextField(
                value = age,
                onValueChange = {
                    if (it.isDigitsOnly())
                    {
                        age = it
                        guest.age = age
                    }
                },
                label = { Text("Age") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(guest: Guest) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Male", "Female", "Other")
    var selectedOption by remember { mutableStateOf(options[2]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(5.dp).fillMaxWidth(0.5f)
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gender") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOption = selectionOption
                        guest.gender = selectionOption
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun UserCounter(modifier: Modifier = Modifier, alter: (String) -> Unit) {

    var user_count by remember { mutableIntStateOf(1) }
    var error_text by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Icon(Icons.Outlined.Person,
                contentDescription = "Person",
                modifier=Modifier.size(45.dp))
        }
        Row {
            Text("-",
                fontSize = TextUnit(7f, TextUnitType.Em),
                textAlign = TextAlign.Center,
                modifier=Modifier
                    .size(30.dp)
                    .clickable {
                        if(user_count>1)
                        {
                            user_count-=1
                            alter("sub")
                            error_text=""
                        }
                        else error_text="You need to enter the details of at least one person!"
                    })

            Text("0${user_count}",
                fontSize = TextUnit(5f, TextUnitType.Em),
                textAlign = TextAlign.Center,
                modifier=Modifier.padding(5.dp))

            Text("+",
                fontSize = TextUnit(6f, TextUnitType.Em),
                textAlign = TextAlign.Center,
                modifier=Modifier
                    .size(30.dp)
                    .clickable {
                        if(user_count<4)
                        {
                            user_count+=1
                            alter("add")
                            error_text=""
                        }
                        else error_text="You can't have more than 4 people in one room!"
                    })
        }
        Row {
            Text(error_text,
                fontSize = TextUnit(4f, TextUnitType.Em),
                textAlign = TextAlign.Center,
                color = Color(0.686f, 0.329f, 0.329f),
                modifier=Modifier.padding(5.dp))
        }
    }
}