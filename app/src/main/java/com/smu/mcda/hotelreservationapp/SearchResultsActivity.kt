package com.smu.mcda.hotelreservationapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smu.mcda.hotelreservationapp.network.HotelData
import com.smu.mcda.hotelreservationapp.network.ImageData
import com.smu.mcda.hotelreservationapp.network.RetrofitClient
import com.smu.mcda.hotelreservationapp.network.SearchRequest
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        onBackPressedDispatcher.addCallback(this) {
            val options = ActivityOptions.makeCustomAnimation(
                this@SearchResultsActivity,
                R.anim.slide_out_bottom,
                R.anim.slide_in_top,
            )
            finish()
            val intent = Intent(this@SearchResultsActivity, MainActivity::class.java)
            startActivity(intent, options.toBundle())
        }

        val location = intent.getStringExtra("location")
        val startDate = intent.getStringExtra("startDate")
        val endDate = intent.getStringExtra("endDate")

        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {

                Scaffold( modifier = Modifier.fillMaxSize() ) {  innerPadding ->
                    SearchResults(
                        modifier = Modifier.padding(innerPadding),
                        startDate=startDate!!,
                        endDate=endDate!!,
                        location=location!!
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResults(modifier: Modifier = Modifier, startDate: String, endDate: String, location: String){
    var results by remember { mutableStateOf(emptyList<HotelData>()) }

//    LaunchedEffect(Unit) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val searchReq = SearchRequest(startDate=startDate, endDate=endDate, location=location)
//                results = RetrofitClient.api.searchHotels(searchReq)
//
//            } catch (e: Exception) {
//                Log.e("API_ERROR", e.toString())
//            }
//        }
//    }

    LaunchedEffect(Unit) {
        delay(2500)
        results += HotelData(
            1,
            name = "Hotel",
            roomNumber = 1,
            pricePerNight = 1,
            roomType = "Normal",
            location = "City",
            imageData = ImageData(filename = "a.jpeg", "a"),
        )
    }

    if(results.isEmpty())
    {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
    else {
        HotelEntry()
    }
}

@Composable
fun HotelEntry() {
    Image(
        ,
        "Picture of City",
        modifier = Modifier.size(30.dp).clip(CircleShape)
    )
}
