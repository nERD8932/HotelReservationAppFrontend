package com.smu.mcda.hotelreservationapp.network

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class ImageData(
    val filename: String,
    val data: String
)

@Parcelize
data class HotelData(
    val hotelId: Int,
    val name: String,
    val roomNumber: Int,
    val pricePerNight: Int,
    val roomType: String,
    val location: String,
) : Parcelable

data class SearchResults(
    val hotelData: List<HotelData>,
    val imageData: Map<Int, ImageData>
)

@Parcelize
data class SearchRequest(
    val startDate: String,
    val endDate: String,
    val location: String
) : Parcelable

data class LocationData(
    val name: String,
    val locId: Int,
    val imageData: ImageData
)

data class BookRequest(
    val startDate: String,
    val endDate: String,
    val location: String,
    val guests: List<Guest>,
    val hotelId: Int,
    val roomNumber: Int,
    val paymentAmount: Int
)

@Parcelize
data class Guest (
    var index: Int,
    var firstName: String = "",
    var lastName: String = "",
    var gender: String = "Other",
    var age: String = "",
    var email: String = ""
) : Parcelable

@Parcelize
data class Guests(
    var list: List<Guest>
) : Parcelable

@Parcelize
data class BookingResponse(
    var booking: String,
    var qr: String
) : Parcelable


interface ApiService {
    @GET("media/")
    suspend fun getImages(): List<ImageData>

    @GET("locations/")
    suspend fun getLocations(): List<LocationData>

    @GET("search/")
    suspend fun searchHotels(@Query("startDate") startDate: String,
                             @Query("endDate") endDate: String,
                             @Query("location") location: String): SearchResults?

    @POST("book/")
    suspend fun book(@Body request: BookRequest): Response<BookingResponse>
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDate(): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    val date = LocalDate.parse(this, inputFormatter)
    return date.format(outputFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun nightsBetween(startDate: String, endDate: String): Long {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val start = LocalDate.parse(startDate, formatter)
    val end = LocalDate.parse(endDate, formatter)
    return ChronoUnit.DAYS.between(start, end)
}