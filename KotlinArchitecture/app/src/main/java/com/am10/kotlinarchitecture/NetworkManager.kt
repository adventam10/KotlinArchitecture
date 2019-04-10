package com.am10.kotlinarchitecture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

object NetworkManager {
    interface RequestWeatherCallback {
        fun onSuccess(weather: Weather)
        fun onFailure(message: String)
    }

    interface RequestImageCallback {
        fun onSuccess(bitmap: Bitmap)
        fun onFailure(message: String)
    }

    private val httpClient = OkHttpClient.Builder().build()
    private val baseURL = "http://weather.livedoor.com/forecast/webservice/json/v1"

    fun requestWeather(cityId: String, callback: RequestWeatherCallback) {
        val url = "$baseURL?city=$cityId"
        val request = Request.Builder().url(url).build()
        val mainHandler = Handler(Looper.getMainLooper())
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainHandler.post {
                    //UI操作
                    callback.onFailure(e.localizedMessage)
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val jsonData: String
                try {
                    jsonData = response.body()!!.string()
                } catch (e: IOException) {
                    mainHandler.post {
                        //UI操作
                        callback.onFailure(e.localizedMessage)
                    }
                    return
                }

                mainHandler.post(Runnable {
                    //UI操作
                    if (response.code() != 200) {
                        callback.onFailure("サーバーと通信できません。")
                        return@Runnable
                    }
                    val gson = Gson()
                    val weather = gson.fromJson(jsonData, Weather::class.java)
                    callback.onSuccess(weather)
                })
            }
        })
    }

    fun requestImage(imageUrl: String, callback: RequestImageCallback) {
        val request = Request.Builder().url(imageUrl).build()
        val mainHandler = Handler(Looper.getMainLooper())
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainHandler.post {
                    //UI操作
                    callback.onFailure(e.localizedMessage)
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body()!!.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                mainHandler.post(Runnable {
                    //UI操作
                    if (response.code() != 200) {
                        callback.onFailure("サーバーと通信できません。")
                        return@Runnable
                    }
                    callback.onSuccess(bitmap)
                })
            }
        })
    }
}