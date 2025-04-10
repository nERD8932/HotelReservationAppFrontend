package com.smu.mcda.hotelreservationapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.smu.mcda.hotelreservationapp.network.LocationData
import com.smu.mcda.hotelreservationapp.network.RetrofitClient
import com.smu.mcda.hotelreservationapp.network.SearchRequest
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) {  innerPadding ->
                    Home(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(modifier: Modifier = Modifier) {

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    val dateDisplayFormatter = remember {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    }

    var active by remember { mutableStateOf(false) }

    val height by animateFloatAsState(
        targetValue = if (!active) 0.35f else 0.1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    val context = LocalContext.current

    var query by remember { mutableStateOf("") }

    val setQ : (String) -> Unit = { q: String -> query = q}
    val setA : (Boolean) -> Unit = { a: Boolean -> active = a}

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val date = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = Calendar.getInstance().apply {
                timeInMillis = utcTimeMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            return !date.before(today)
        }

        override fun isSelectableYear(year: Int): Boolean {
            return year in currentYear..(currentYear + 1)
        }})

    Column(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {

        Box(Modifier.weight(height)) {
            // this@Column.AnimatedVisibility(visible = !active) {
                ImageSlideshow(
                    modifier = Modifier.fillMaxWidth(),
                    intervalMillis = 12000L,
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Reserver",
                        fontSize = TextUnit(9f, TextUnitType.Em),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.66f)
                            )
                            .padding(10.dp, 1.dp),
                    )
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                    )
                    Text(
                        text = "Book your dream vacation today",
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.66f)
                            )
                            .padding(10.dp, 1.dp),
                        maxLines = 1,

                        )
                }
           // }

        }
        Box(Modifier.weight(0.50f), contentAlignment = Alignment.Center) {
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Row (
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                )
                {
                    Text(
                        text="Destination",
                        textAlign = TextAlign.Left,
                        fontSize = TextUnit(4f, TextUnitType.Em),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(30.dp, 12.dp, 30.dp, 3.dp)
                            .fillMaxWidth())
                }
                Row (
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                )
                {
                    Search(
                        modifier=Modifier.padding(30.dp, 3.dp, 30.dp, 12.dp),
                        setQ=setQ,
                        setA=setA)
                }
                Row (
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.scale(0.9f),
                )
                {
                    //.background(Color.White.copy(alpha = 0.66f))
                    DateRangePicker(
                        state = date,
                        showModeToggle = false,
                        title = {
                            Text(
                                text = "Booking Dates",
                                fontSize = TextUnit(4f/0.9f, TextUnitType.Em),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(15.dp, 0.dp)
                            )
                        },
                        headline = {
                            CompositionLocalProvider(
                                LocalTextStyle provides TextStyle(
                                    fontSize = TextUnit(5f, TextUnitType.Em),
                                    textAlign = TextAlign.Center)
                            ) {
                                Row (
                                    modifier=Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ){

                                    if(date.selectedStartDateMillis != null)
                                    {
                                        Text(dateDisplayFormatter.format(date.selectedStartDateMillis))
                                    }
                                    else
                                    {
                                        Text("Check In Date")
                                    }
                                    Text(" - ")
                                    if(date.selectedEndDateMillis != null)
                                    {
                                        Text(dateDisplayFormatter.format(date.selectedEndDateMillis))
                                    }
                                    else
                                    {
                                        Text("Check Out Date")
                                    }
                                }
                            }
                        }
                    )
                }

            }
        }
        Box(Modifier
            .fillMaxSize()
            .weight(0.15f)) {

            Row (
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Absolute.Right,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp, 0.dp)
            )
            {
                Button(onClick = { clickSearch(context, date=date, location=query, formatter=dateFormatter) }) {
                    Text("Search", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun clickSearch(context: Context, date: DateRangePickerState, location: String, formatter: SimpleDateFormat) {
    if (date.selectedStartDateMillis!=null && date.selectedEndDateMillis!=null)
    {
        try {
            val searchReq = SearchRequest(
                startDate=formatter.format(date.selectedStartDateMillis),
                endDate=formatter.format(date.selectedEndDateMillis),
                location=location)

            val intent = Intent(context, SearchResultsActivity::class.java).apply {
                putExtra("searchReq", searchReq)
            }

            val options = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.slide_in_bottom,
                R.anim.slide_out_top
            )

            context.startActivity(intent, options.toBundle())
        }
        catch (e: Exception){
            Log.e("E", e.toString())
        }
    }
}

@Composable
fun ImageSlideshow(
    modifier: Modifier = Modifier,
    intervalMillis: Long = 12000L,
) {
    var bitmaps by remember { mutableStateOf<List<ImageBitmap>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var imageLoaded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch images once
    LaunchedEffect(Unit) {
        try {

            withContext(Dispatchers.IO) {

                val fetchedImages = RetrofitClient.api.getImages()

                for ((i, image) in fetchedImages.withIndex()) {
                    val img = Decoder.decodeBase64ToBitmap(image.data)?.asImageBitmap()
                    if (img != null) {
                        bitmaps += img
                        imageLoaded = true
                    }
                }
            }
//            if(vm_images["bg"]!!.values.isEmpty())
//            {
//                Log.i("STATE", "Loading from API")
//                withContext(Dispatchers.IO) {
//
//                    val fetchedImages = RetrofitClient.api.getImages()
//
//                    for ((i, image) in fetchedImages.withIndex()) {
//                        val img = Decoder.decodeBase64ToBitmap(image.data)?.asImageBitmap()
//                        if (img != null) {
//                            bitmaps += img
//                            viewModel.addImage(i, i.toString(), img, "bg")
//                        }
//                    }
//                }
//            }
//            else {
//                Log.i("STATE", "Loading from VM")
//                vm_images["bg"]!!.values.forEach {ic -> bitmaps += ic.bitmap}
//            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (imageLoaded) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(intervalMillis)
                currentIndex = (currentIndex + 1) % bitmaps.size
            }
        }
    }

    AnimatedVisibility(
        visible = bitmaps.isNotEmpty(),
        enter = fadeIn(animationSpec = tween(3000))
    ) {
        if (bitmaps.isNotEmpty()) {
            Crossfade (
                targetState = currentIndex,
                animationSpec = tween(3000) // Fade duration
            ) { index ->
                Image(
                    bitmap = bitmaps[index],
                    contentDescription = null,
                    modifier = modifier
                        .fillMaxSize()
                        .fadingEdge(
                            Brush.verticalGradient(
                                0.72f to Color.White,
                                0.97f to Color.Transparent
                            )
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
    if (bitmaps.isEmpty()){
        Spacer(modifier = modifier
            .fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HotelReservationAppTheme {
        Home(modifier = Modifier.wrapContentSize(Alignment.Center))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(modifier: Modifier = Modifier,
           setQ: (String) -> Unit,
           setA: (Boolean) -> Unit,
           ) {

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var destinations by remember { mutableStateOf(emptyList<LocationData>()) }
    var images by remember { mutableStateOf(emptyMap<String, ImageBitmap?>()) }
    var results by remember { mutableStateOf(destinations) }
    var leading by remember { mutableStateOf<@Composable() (() -> Unit)?>(null) }

    fun performSearch(input: String) {
        results = if (input.isBlank()) destinations.subList(0, min(destinations.size, 4))
        else destinations.filter { it.name.contains(input, ignoreCase = true) }
        results = results.subList(0, min(results.size, 4))

        for(result in results)
        {
            if (result.name.equals(input, ignoreCase = true))
            {
                query = results[0].name
            }
        }

        leading = if(images[query]!=null) {
            @Composable {
                Image(
                    images[query]!!,
                    "Picture of City",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
            }
        } else {
            null
        }
    }

    LaunchedEffect(Unit) {
        try {

            withContext(Dispatchers.IO) {
                destinations = RetrofitClient.api.getLocations()
                for (dest in destinations) {
                    var img = Decoder.decodeBase64ToBitmap(dest.imageData.data)?.asImageBitmap()
                    if (img != null) {
                        images += dest.name to img
                    } else {
                        img = Bitmap.createBitmap(
                            512,
                            512,
                            Bitmap.Config.ARGB_8888
                        ).asImageBitmap()
                        images += dest.name to img
                    }
                }
            }

//            if(vm_images["location"]!!.values.isNotEmpty())
//            {
//                vm_images["location"]!!.values.forEach { img -> images += img.name to img.bitmap}
//            }
//            else {
//                withContext(Dispatchers.IO) {
//                    destinations = RetrofitClient.api.getLocations()
//                    for (dest in destinations) {
//                        var img = Decoder.decodeBase64ToBitmap(dest.imageData.data)?.asImageBitmap()
//                        if (img != null) {
//                            images += dest.name to img
//                            viewModel.addImage(dest.locId, dest.name, img, "location")
//                        } else {
//                            img = Bitmap.createBitmap(
//                                512,
//                                512,
//                                Bitmap.Config.ARGB_8888
//                            ).asImageBitmap()
//                            images += dest.name to img
//                            viewModel.addImage(dest.locId, dest.name, img, "location")
//                        }
//                    }
//                }
//            }
            performSearch("")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(Modifier.padding(16.dp, 0.dp).wrapContentHeight()) {
        DockedSearchBar(
            query = query,
            onQueryChange = {
                query = it
                setQ(it)
                coroutineScope.launch {
                    performSearch(query)
                }
            },
            onSearch = {
                coroutineScope.launch {
                    performSearch(query)
                }
            },
            active = active,
            onActiveChange = {
                active = it
                setA(it)
                             },
            placeholder = { Text("Search by cities, or leave blank") },
            leadingIcon = leading,
            trailingIcon =
            {
                if(active||images[query]!=null)
                {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        modifier = Modifier.clickable
                        {
                            if(query.isNotEmpty())
                            {
                                query = ""
                                performSearch(query)
                            } else
                            {
                                active=false
                                setA(false)
                            }
                        })
                }
                else
                {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 230.dp)
                .wrapContentHeight()
                //.background(color=Color(1f, 0.5f, 0.5f, 0.33f))
            ,
        ) {
            if (destinations.isNotEmpty())
            {
                results.forEach { city ->

                    ListItem(
                        headlineContent = {
                            Row {
                                Text(
                                    city.name,
                                    fontSize = TextUnit(4f, TextUnitType.Em)
                                )
                            }
                        },
                        leadingContent = {
                            if(images[city.name]!=null)
                            {
                                Image(
                                    images[city.name]!!,
                                    "Picture of City",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                query = city.name
                                performSearch(query)
                                setQ(city.name)
                                active = false
                                setA(false)
                            }
                    )
                }
            }
            else{
                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(), // minimal and controlled padding
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier
                        .fillMaxWidth()
                        .height(12.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.width(32.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        "Finding the latest and greatest places to visit...",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                        )
                }
            }
        }
    }
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }
