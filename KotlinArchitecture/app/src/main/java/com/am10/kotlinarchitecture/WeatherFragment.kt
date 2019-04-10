package com.am10.kotlinarchitecture

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class WeatherFragment: Fragment() {

    enum class Date {
        Today,
        Tomorrow,
        DayAfterTomorrow
    }

    private var todayView: WeatherCustomView? = null
    private var tomorrowView: WeatherCustomView? = null
    private var dayAfterTomorrowView: WeatherCustomView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayView = view.findViewById<WeatherCustomView>(R.id.view_today)
        tomorrowView = view.findViewById<WeatherCustomView>(R.id.view_tomorrow)
        dayAfterTomorrowView = view.findViewById<WeatherCustomView>(R.id.view_day_after_tomorrow)
    }

    fun displayForecast(date: Date, forecast: Forecast?, minCelsius: String, maxCelsius: String) {
        when (date) {
            WeatherFragment.Date.Today -> todayView!!.displayForecast(forecast, minCelsius, maxCelsius)
            WeatherFragment.Date.Tomorrow -> tomorrowView!!.displayForecast(forecast, minCelsius, maxCelsius)
            WeatherFragment.Date.DayAfterTomorrow -> dayAfterTomorrowView!!.displayForecast(forecast, minCelsius, maxCelsius)
        }
    }

    fun getImageView(date: Date): ImageView? {
        when (date) {
            WeatherFragment.Date.Today -> return todayView!!.imageView
            WeatherFragment.Date.Tomorrow -> return tomorrowView!!.imageView
            WeatherFragment.Date.DayAfterTomorrow -> return dayAfterTomorrowView!!.imageView
        }
        return null
    }
}