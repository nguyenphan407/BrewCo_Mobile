package com.example.brewco.data.api

import android.util.Log
import com.example.brewco.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


object ApiClient {

    private const val BASE_URL = "https://e5d067a06378.ngrok-free.app"


    private const val CONNECT_TIMEOUT_SEC = 15L
    private const val READ_TIMEOUT_SEC = 30L
    private const val WRITE_TIMEOUT_SEC = 30L

    private val gson: Gson by lazy {
        val dateDeserializer = JsonDeserializer<Date> { json, _, _ -> parseServerDateOrNull(json?.asString) }

        val localDateTimeAdapter = object : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
            private val formatter = DateTimeFormatter.ISO_DATE_TIME

            override fun serialize(
                src: LocalDateTime?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement = if (src == null) JsonNull.INSTANCE else JsonPrimitive(src.format(formatter))

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDateTime? {
                val raw = json?.asString?.trim().orEmpty()
                if (raw.isEmpty()) return null


                val normalized = raw.removeSuffix("Z")
                return try {
                    LocalDateTime.parse(normalized, formatter)
                } catch (e: Exception) {
                    Log.e("ApiClient", "LocalDateTime parsing error: $raw", e)
                    null
                }
            }
        }

        GsonBuilder()
            .registerTypeAdapter(Date::class.java, dateDeserializer)
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeAdapter)
            .create()
    }

    val apiService: ApiService by lazy {
        val logLevel = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {

            HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = logLevel
            })
            .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ApiService::class.java)
    }


    private fun parseServerDateOrNull(raw: String?): Date? {
        val value = raw?.trim().orEmpty()
        if (value.isEmpty()) return null

        val candidates = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss"
        )

        for (pattern in candidates) {
            try {
                val fmt = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                return fmt.parse(value.removeSuffix("Z"))
            } catch (_: Exception) {

            }
        }

        Log.w("ApiClient", "Không parse được Date từ server: $value")
        return null
    }
}
