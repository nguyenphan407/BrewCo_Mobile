package com.example.brewco.data.api

import android.util.Log
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

/**
 * ApiClient giữ nguyên cấu hình như bản gốc BrewCo_old để bảo đảm 1:1.
 */
object ApiClient {

    private const val BASE_URL = "https://e5d067a06378.ngrok-free.app"

    private val gson: Gson by lazy {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            Locale.getDefault()
        )
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val dateDeserializer = JsonDeserializer<Date> { json, _, _ ->
            try {
                dateFormat.parse(json.asString)
            } catch (e: Exception) {
                Log.e("ApiClient", "Date parsing error", e)
                null
            }
        }

        val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
        val localDateTimeAdapter = object : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
            override fun serialize(
                src: LocalDateTime?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement {
                return if (src == null) {
                    JsonNull.INSTANCE
                } else {
                    JsonPrimitive(src.format(dateTimeFormatter))
                }
            }

            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDateTime? {
                if (json == null || json.asString.isEmpty()) return null
                return try {
                    LocalDateTime.parse(json.asString, dateTimeFormatter)
                } catch (e: Exception) {
                    Log.e("ApiClient", "LocalDateTime parsing error", e)
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
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
