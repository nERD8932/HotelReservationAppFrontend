package com.smu.mcda.hotelreservationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

data class ImageComposite(
    val name: String,
    val bitmap: ImageBitmap
)


class ImagesViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _images = mutableStateOf(
        savedStateHandle.get<Map<String, Map<Int, ImageComposite>>>("images") ?:
        mapOf(
            "bg" to emptyMap(),
            "location" to emptyMap(),
            "hotel" to emptyMap()
        )
    )
    val images: State<Map<String, Map<Int, ImageComposite>>> = _images

    fun addImage(id: Int, name: String, img: ImageBitmap, where: String) {
        _images.value = _images.value.toMutableMap().apply {
            this[where] = (this[where] ?: emptyMap()) + (id to ImageComposite(name, img))
        }
    }


    // Initialize with saved state or empty map
//    private val _images = mutableStateOf(
//        savedStateHandle.get<Map<String, Map<Int, ImageComposite>>>("images") ?: emptyMap()
//    )
//
//    val images: State<Map<String, Map<Int, ImageComposite>>> = _images

//    fun addImage(id: Int, name: String, img: ImageBitmap, where: String)
//    {
//        try {
//            val currentCategoryMap = _images.value[where] ?: emptyMap()
//            val updatedCategoryMap = currentCategoryMap + (id to ImageComposite(name, img))
//            _images.value += (where to updatedCategoryMap)
//        }
//        catch (e: Exception) {
//            Log.e("API_ERROR", e.toString())
//        }
//    }

//    fun addImage(id: Int, name: String, img: ImageBitmap, where: String) {
//        try {
//            // Get current state
//            val currentImages = _images.value.toMutableMap()
//
//            // Get or create the category map
//            val currentCategoryMap = currentImages[where] ?: emptyMap()
//
//            // Create updated map
//            val updatedCategoryMap = currentCategoryMap + (id to ImageComposite(name, img))
//
//            // Update the main map
//            currentImages[where] = updatedCategoryMap
//
//            // Update the state
//            _images.value = currentImages
//
//            // Persist to SavedStateHandle if needed
//            savedStateHandle["images"] = currentImages
//
//        } catch (e: Exception) {
//            Log.e("API_ERROR", e.toString())
//        }
//    }
}