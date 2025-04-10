package com.smu.mcda.hotelreservationapp.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

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

interface ApiService {
    @GET("media/")
    suspend fun getImages(): List<ImageData>

    @GET("locations/")
    suspend fun getLocations(): List<LocationData>

    @GET("search/")
    suspend fun searchHotels(@Query("startDate") startDate: String,
                             @Query("endDate") endDate: String,
                             @Query("location") location: String): SearchResults?
}