package com.smu.mcda.hotelreservationapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.smu.mcda.hotelreservationapp.network.BookRequest
import com.smu.mcda.hotelreservationapp.network.Guest
import com.smu.mcda.hotelreservationapp.network.Guests
import com.smu.mcda.hotelreservationapp.network.HotelData
import com.smu.mcda.hotelreservationapp.network.RetrofitClient
import com.smu.mcda.hotelreservationapp.network.SearchRequest
import com.smu.mcda.hotelreservationapp.network.formatDate
import com.smu.mcda.hotelreservationapp.network.nightsBetween
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FinalizeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        val hotelInfo = intent.getParcelableExtra<HotelData>("hotelData")!!
        val searchReq = intent.getParcelableExtra<SearchRequest>("searchReq")!!
        val guests = intent.getParcelableExtra<Guests>("guests")!!.list

        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {

                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->

                    Column (modifier=Modifier.padding(innerPadding)) {
                        Box(
                            modifier = Modifier.weight(0.1f).fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        )
                        {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.ShoppingCart,
                                        contentDescription = "Book",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.size(10.dp))
                                    Text(
                                        "Confirm and Book",
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(6f, TextUnitType.Em),
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        Box(
                            Modifier.weight(0.9f).padding(20.dp, 30.dp),
                            contentAlignment = Alignment.Center

                        )
                        {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Finalize(hotelInfo, searchReq, guests)
                                Spacer(Modifier.height(20.dp))
                                BookButton(hotelInfo, searchReq, guests)
                            }
                        }
                    }
                    Box(contentAlignment = Alignment.TopStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .padding(10.dp, 0.dp)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookButton(hotelInfo: HotelData, searchReq: SearchRequest, guests: List<Guest>){

    val context = LocalContext.current

    Button(onClick = {

        val b = BookRequest(
            startDate = searchReq.startDate,
            endDate = searchReq.endDate,
            location = hotelInfo.location,
            guests = guests,
            hotelId = hotelInfo.hotelId,
            roomNumber = hotelInfo.roomNumber,
            paymentAmount = (nightsBetween(searchReq.startDate, searchReq.endDate) * hotelInfo.pricePerNight).toInt()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.api.book(b)
            if (response.isSuccessful) {
                val booking = response.body()
                val intent = Intent(context, SuccessActivity::class.java).apply {
                    putExtra("booking", booking)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            } else {
                val intent = Intent(context, FailureActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
    ) {
        Text("Book", textAlign = TextAlign.Center)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Finalize(hotelInfo: HotelData, searchReq: SearchRequest, guests: List<Guest>) {

    val nights = nightsBetween(searchReq.startDate, searchReq.endDate)

    var color: Color by remember { mutableStateOf(Color.Unspecified) }
    color = if (isSystemInDarkTheme())
    {
        Color(0.137f, 0.149f, 0.188f, 1.0f)
    }
    else {
        Color(0.898f, 0.902f, 0.945f, 1.0f)
    }

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Text(
            "Booking Details",
            fontSize = TextUnit(5f, TextUnitType.Em),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(10.dp, 5.dp)
        )
        //HorizontalDivider()
        Column (modifier = Modifier
            .shadow(4.dp, shape = RoundedCornerShape(20.dp))
            .background(color, RoundedCornerShape(20.dp))
            .padding(25.dp)){
            Row (
                horizontalArrangement = Arrangement.Center,
            ) {
                Column(Modifier.wrapContentWidth(), verticalArrangement = Arrangement.SpaceBetween)
                {
                    Text(
                        "Hotel: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Room Type: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Cost per Night: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "From: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "To: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Number of Guests: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Total Nights: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${hotelInfo.name}, ${hotelInfo.location}",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        hotelInfo.roomType,
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        "\$${hotelInfo.pricePerNight}",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        searchReq.startDate.formatDate(),
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        searchReq.endDate.formatDate(),
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        guests.size.toString(),
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )
                    Text(
                        nights.toString(),
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                    )

                }
            }
            HorizontalDivider()
            Row (horizontalArrangement = Arrangement.Center) {
                Column(Modifier.wrapContentWidth(), verticalArrangement = Arrangement.Bottom) {
                    Text(
                        "Total: ",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End,  verticalArrangement = Arrangement.Bottom){
                    Text(
                        "\$${(nights * hotelInfo.pricePerNight)}",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}