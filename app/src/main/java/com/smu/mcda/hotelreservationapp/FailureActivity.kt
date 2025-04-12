package com.smu.mcda.hotelreservationapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.smu.mcda.hotelreservationapp.ui.theme.HotelReservationAppTheme

class FailureActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

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
                                    Icon(Icons.Outlined.Close,
                                        contentDescription = "Fail",
                                        modifier=Modifier.size(24.dp))
                                    Spacer(Modifier.size(10.dp))
                                    Text(
                                        "Something went wrong",
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
                            Column (Modifier.fillMaxWidth(0.9f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center){
                                Text(
                                    ":(",
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    fontSize = TextUnit(10f, TextUnitType.Em),
                                )
                                Text(
                                    "Something seems to have gone wrong, please try again later!",
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    fontSize = TextUnit(7f, TextUnitType.Em),
                                    modifier = Modifier
                                )
                            }
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
                                    finish()
                                }
                        )
                    }
                }
            }
        }
    }
}