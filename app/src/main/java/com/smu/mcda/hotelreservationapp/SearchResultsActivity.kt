package com.smu.mcda.hotelreservationapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.smu.mcda.hotelreservationapp.network.HotelData
import com.smu.mcda.hotelreservationapp.network.RetrofitClient
import com.smu.mcda.hotelreservationapp.network.SearchRequest
import com.smu.mcda.hotelreservationapp.network.SearchResults
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme



class SearchResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        val searchReq = intent.getParcelableExtra<SearchRequest>("searchReq")

        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {

                Scaffold( modifier = Modifier.fillMaxSize() ) {  innerPadding ->

                    Column (modifier = Modifier.padding(innerPadding)) {
                        Box(
                            modifier = Modifier.weight(0.1f).fillMaxWidth().padding(5.dp),
                            contentAlignment = Alignment.BottomCenter
                            )
                        {
                            Column (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ){
                                Row {
                                    Icon(Icons.Default.Search,
                                        contentDescription = "Search Results",
                                        modifier=Modifier.size(32.dp))
                                    Text(
                                        "Search Results",
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(7f, TextUnitType.Em),
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        Box(Modifier.weight(0.9f).padding(30.dp, 0.dp))
                        {
                            if (searchReq != null) {
                                SearchResults(
                                    modifier = Modifier,
                                    searchReq = searchReq
                                )
                            }
                        }
                    }
                    Box(contentAlignment = Alignment.TopStart,
                        modifier = Modifier.fillMaxWidth().padding(innerPadding)){
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

@Composable
fun SearchResults(modifier: Modifier = Modifier, searchReq: SearchRequest){
    var response by remember { mutableStateOf<SearchResults?>(null) }
    var images by remember { mutableStateOf(emptyMap<Int,  ImageBitmap?>()) }
    var loaded by remember { mutableStateOf(false)}
    // var results by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            response = RetrofitClient.api.searchHotels(
                startDate=searchReq.startDate,
                endDate = searchReq.endDate,
                location = searchReq.location
            )

            if (response!=null)
            {
                response!!.imageData.forEach { img ->
                    val i = Decoder.decodeBase64ToBitmap(img.value.data)
                    if (i!=null)
                    {
                        images += img.key to i.asImageBitmap()
                    }
                }
                loaded = true
            }

        } catch (e: Exception) {
            loaded = true
            Log.e("API_ERROR", e.toString())
        }
    }

//    LaunchedEffect(Unit) {
//        delay(2500)
//        results += "hellow world"
//    }

    if(!loaded)
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
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        if(response!!.hotelData.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(5.dp)
            ) {
                items(response!!.hotelData) { hotel ->
                    HotelEntry(
                        hotel,
                        images[hotel.hotelId]!!,
                        context = context
                    )
                }
            }
        }
        else {
            Column(
                modifier = Modifier.fillMaxSize().padding(15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Sorry, there are no available hotels for these dates and location!",
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    fontSize = TextUnit(5f, TextUnitType.Em)
                    )
            }
        }
    }
        }
}
// img: ImageBitmap, name: String, roomType: String, price: Int
@Composable
fun HotelEntry(h: HotelData, i: ImageBitmap, modifier: Modifier = Modifier, context: Context) {
    Spacer(Modifier.height(5.dp))
    Box(modifier = modifier
        .fillMaxWidth()
        .height(170.dp)
        .shadow(4.dp, shape = RoundedCornerShape(20.dp))
        .clickable(
            indication = rememberRipple(bounded = true),
            interactionSource = remember { MutableInteractionSource() }
        ) {
            val intent = Intent(context, SearchResultsActivity::class.java).apply {
                putExtra("hotelData", h)
            }

            val options = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.slide_in_bottom,
                R.anim.slide_out_top
            )
            context.startActivity(intent, options.toBundle())
        }
    ) {
        Image(
            bitmap = i,
            contentDescription = "${h.name} Hotel",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(22.dp))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .shadow(4.dp, shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
        {
            Row (Modifier.fillMaxWidth().wrapContentHeight()) {
                Column (Modifier.fillMaxWidth(0.5f),
                    horizontalAlignment = Alignment.Start) {
                    Text(
                        "${h.name} Hotel",
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left,
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        modifier = Modifier
                    )
                    Text(
                        h.location,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Left,
                        fontSize = TextUnit(3.5f, TextUnitType.Em),
                        modifier = Modifier
                    )
                }
                Column (
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                    ) {
                    Text(
                        "${h.roomType} Room",
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right,
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        modifier = Modifier
                    )
                    Text(
                        "$${h.pricePerNight} per night",
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Right,
                        fontSize = TextUnit(3f, TextUnitType.Em),
                        modifier = Modifier
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(5.dp))
}

