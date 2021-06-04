package me.bickositieff.raspio.api

import me.bickositieff.raspio.ApiDecorator
import retrofit2.Retrofit

class ServerURLDecorator(private val url: String) : ApiDecorator {
    override fun Retrofit.Builder.decorate() {
        baseUrl(url)
    }
}