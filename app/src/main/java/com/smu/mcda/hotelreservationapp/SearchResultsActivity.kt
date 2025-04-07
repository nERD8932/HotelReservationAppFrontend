package com.smu.mcda.hotelreservationapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.smu.mcda.hotelreservationapp.network.RetrofitClient
import com.smu.mcda.hotelreservationapp.network.SearchRequest
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val searchReq = SearchRequest(startDate=startDate!!, endDate=endDate!!, location=location!!)
                val result = RetrofitClient.api.searchHotels(searchReq)

            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
            }
        }


        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {

                Scaffold( modifier = Modifier.fillMaxSize() ) {  innerPadding ->
                    SearchResults(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResults(modifier: Modifier = Modifier){

}