package com.smu.mcda.hotelreservationapp.network

import retrofit2.http.Body
import retrofit2.http.GET

data class ImageData(
    val filename: String,
    val data: String
)

data class HotelData(
    val hotelId: Int,
    val name: String,
    val roomNumber: Int,
    val pricePerNight: Int,
    val roomType: String,
    val location: String,
    val imageData: ImageData
)

data class SearchRequest(
    val startDate: String,
    val endDate: String,
    val location: String
)

data class LocationData(
    val name: String,
    val imageData: ImageData
)

interface ApiService {
    @GET("media/")
    suspend fun getImages(): List<ImageData>

    @GET("locations/")
    suspend fun getLocations(): List<LocationData>

    @GET("search/")
    suspend fun searchHotels(@Body request: SearchRequest): List<HotelData>
}