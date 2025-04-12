package com.smu.mcda.hotelreservationapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.smu.mcda.hotelreservationapp.network.BookingResponse
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme

class SuccessActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@SuccessActivity, MainActivity::class.java)
            this@SuccessActivity.startActivity(intent)
            finish()
        }

        val booking = intent.getParcelableExtra<BookingResponse>("booking")
        val img_bit = Decoder.decodeBase64ToBitmap(booking!!.qr)!!



        enableEdgeToEdge()
        setContent {
            HotelReservationAppTheme {

                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Column (Modifier.padding(innerPadding)) {
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
                                    Icon(Icons.Outlined.Check,
                                        contentDescription = "Success",
                                        modifier=Modifier.size(24.dp))
                                    Spacer(Modifier.size(10.dp))
                                    Text(
                                        "Success",
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(6f, TextUnitType.Em),
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.weight(0.9f).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Success(img_bit, booking)
                        }
                    }

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
                                    val intent = Intent(this@SuccessActivity, MainActivity::class.java)
                                    this@SuccessActivity.startActivity(intent)
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
fun Success(img_bit: Bitmap, booking: BookingResponse)
{

    val scaledBitmap = remember(img_bit) {
        Bitmap.createScaledBitmap(
            img_bit,
            img_bit.width * 10,
            img_bit.height * 10,
            false
        )
    }

    Column (Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(
            "Booking Confirmed!",
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            fontSize = TextUnit(7f, TextUnitType.Em),
            modifier = Modifier
        )
        Image(
            bitmap = scaledBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.5f),
        )
        Text(
            "Booking ID:\n${booking.booking}",
            textAlign = TextAlign.Center,
            fontSize = TextUnit(4f, TextUnitType.Em),
            modifier = Modifier
        )
    }

}