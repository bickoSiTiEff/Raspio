package me.bickositieff.raspio.api

import me.bickositieff.raspio.generated.ApiDecorator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class RaspioServerDecorator(private val url: String) : ApiDecorator {
    override fun Retrofit.Builder.decorate() {
        baseUrl(url)
    }

    override fun OkHttpClient.Builder.decorate() {
        connectTimeout(5, TimeUnit.SECONDS)
    }
}