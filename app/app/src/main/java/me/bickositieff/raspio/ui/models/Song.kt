package me.bickositieff.raspio.ui.models

import androidx.annotation.DrawableRes

class Song(
    val path: String,
    val title: String,
    val artist: String,
    val duration: Int,
    @DrawableRes
    val image: Int?
) {
    fun durationToString(): String{
        return ""+duration/60 + ":" + duration%60
    }
}