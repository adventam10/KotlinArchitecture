package com.am10.kotlinarchitecture

import android.graphics.Bitmap
import android.widget.ImageView
import java.io.Serializable

class WeatherModel: Serializable {
    var weather: Weather? = null
    var cityData: CityData = CityData(1, "", "")

    fun getMinCelsiusFromForecast(forecast: Forecast?): String {
        val vForecast = forecast ?: return "-"
        val temperature = vForecast.temperature ?: return "-"
        val min = temperature.min ?: return "-"
        val celsius = min.celsius ?: return "-"
        return if (celsius.isEmpty()) "-" else celsius + "℃"
    }

    fun getMaxCelsiusFromForecast(forecast: Forecast?): String {
        val vForecast = forecast ?: return "-"
        val temperature = vForecast.temperature ?: return "-"
        val max = temperature.max ?: return "-"
        val celsius = max.celsius ?: return "-"
        return if (celsius.isEmpty()) "-" else celsius + "℃"
    }

    fun displayImageFromForecast(forecast: Forecast?, imageView: ImageView) {
        if (forecast == null) {
            imageView.setImageResource(R.drawable.icon_no_image)
            return
        }
        if (forecast!!.image == null) {
            imageView.setImageResource(R.drawable.icon_no_image)
            return
        }
        val urlText = forecast!!.image?.url
        if (urlText == null || urlText!!.isEmpty()) {
            imageView.setImageResource(R.drawable.icon_no_image)
            return
        }
        NetworkManager.requestImage(urlText, object : NetworkManager.RequestImageCallback {
            override fun onSuccess(bitmap: Bitmap) {
                imageView.setImageBitmap(bitmap)
            }

            override fun onFailure(message: String) {
                imageView.setImageResource(R.drawable.icon_no_image)
            }
        })
    }
}