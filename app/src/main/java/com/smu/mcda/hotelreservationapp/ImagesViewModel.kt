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
}