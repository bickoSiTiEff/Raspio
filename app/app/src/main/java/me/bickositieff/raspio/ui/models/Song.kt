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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }


}