package com.am10.kotlinarchitecture

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView

class WeatherActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WEATHER = "EXTRA_WEATHER"
        const val EXTRA_CITY_DATA = "EXTRA_CITY_DATA"
    }
    val STATE_WEATHER = "STATE_WEATHER"
    val STATE_CITY_DATA = "STATE_CITY_DATA"

    private var todayDateTextView: TextView? = null
    private var todaySubdateTextView: TextView? = null
    private var todayTelopTextView: TextView? = null
    private var todayMaxCelsiusTextView: TextView? = null
    private var todayMinCelsiusTextView: TextView? = null
    private var todayImageView: ImageView? = null

    private var tomorrowDateTextView: TextView? = null
    private var tomorrowSubdateTextView: TextView? = null
    private var tomorrowTelopTextView: TextView? = null
    private var tomorrowMaxCelsiusTextView: TextView? = null
    private var tomorrowMinCelsiusTextView: TextView? = null
    private var tomorrowImageView: ImageView? = null

    private var dayAfterTomorrowDateTextView: TextView? = null
    private var dayAfterTomorrowSubdateTextView: TextView? = null
    private var dayAfterTomorrowTelopTextView: TextView? = null
    private var dayAfterTomorrowMaxCelsiusTextView: TextView? = null
    private var dayAfterTomorrowMinCelsiusTextView: TextView? = null
    private var dayAfterTomorrowImageView: ImageView? = null

    private var progressDialog: ProgressDialog? = null

    private var weather: Weather? = null
    private var cityData: CityData = CityData(1, "", "")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        if (savedInstanceState == null) {
            val intent = intent
            weather = intent.getSerializableExtra(EXTRA_WEATHER) as Weather
            cityData = intent.getSerializableExtra(EXTRA_CITY_DATA) as CityData
        } else {
            weather = savedInstanceState.getSerializable(STATE_WEATHER) as Weather
            cityData = savedInstanceState.getSerializable(STATE_CITY_DATA) as CityData
        }

        // アクションバーに前画面に戻る機能をつける
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = cityData.name

        todayDateTextView = findViewById(R.id.textView_today_date)
        todaySubdateTextView = findViewById(R.id.textView_today_subdate)
        todayTelopTextView = findViewById(R.id.textView_today_telop)
        todayMaxCelsiusTextView = findViewById(R.id.textView_today_max_celsius)
        todayMinCelsiusTextView = findViewById(R.id.textView_today_min_celsius)
        todayImageView = findViewById(R.id.imageView_today)

        tomorrowDateTextView = findViewById(R.id.textView_tomorrow_date)
        tomorrowSubdateTextView = findViewById(R.id.textView_tomorrow_subdate)
        tomorrowTelopTextView = findViewById(R.id.textView_tomorrow_telop)
        tomorrowMaxCelsiusTextView = findViewById(R.id.textView_tomorrow_max_celsius)
        tomorrowMinCelsiusTextView = findViewById(R.id.textView_tomorrow_min_celsius)
        tomorrowImageView = findViewById(R.id.imageView_tomorrow)

        dayAfterTomorrowDateTextView = findViewById(R.id.textView_day_after_tomorrow_date)
        dayAfterTomorrowSubdateTextView = findViewById(R.id.textView_day_after_tomorrow_subdate)
        dayAfterTomorrowTelopTextView = findViewById(R.id.textView_day_after_tomorrow_telop)
        dayAfterTomorrowMaxCelsiusTextView = findViewById(R.id.textView_day_after_tomorrow_max_celsius)
        dayAfterTomorrowMinCelsiusTextView = findViewById(R.id.textView_day_after_tomorrow_min_celsius)
        dayAfterTomorrowImageView = findViewById(R.id.imageView_day_after_tomorrow)

        displayForecasts(weather?.forecasts)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable(STATE_CITY_DATA, cityData)
        savedInstanceState.putSerializable(STATE_WEATHER, weather)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_refresh -> {
                // ボタンをタップした際の処理を記述
                requestWeather(cityData.cityId)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayForecasts(forecasts: List<Forecast>?) {
        displayTodayForecast(null)
        displayTomorrowForecast(null)
        displayDayAfterTomorrowForecast(null)
        if (forecasts == null) {
            return
        }
        for ((index, forecast) in forecasts.withIndex()) {
            if (index == 0) {
                displayTodayForecast(forecast)
            } else if (index == 1) {
                displayTomorrowForecast(forecast)
            } else if (index == 2) {
                displayDayAfterTomorrowForecast(forecast)
            }
        }
    }

    private fun displayTodayForecast(forecast: Forecast?) {
        todayDateTextView?.text = forecast?.date
        todaySubdateTextView?.text = forecast?.dateLabel
        todayTelopTextView?.text = forecast?.telop
        todayMaxCelsiusTextView?.text = getMaxCelsiusFromForecast(forecast)
        todayMinCelsiusTextView?.text = getMinCelsiusFromForecast(forecast)
        displayImageFromForecast(forecast, todayImageView!!)
    }

    private fun displayTomorrowForecast(forecast: Forecast?) {
        tomorrowDateTextView?.text = forecast?.date
        tomorrowSubdateTextView?.text = forecast?.dateLabel
        tomorrowTelopTextView?.text = forecast?.telop
        tomorrowMaxCelsiusTextView?.text = getMaxCelsiusFromForecast(forecast)
        tomorrowMinCelsiusTextView?.text = getMinCelsiusFromForecast(forecast)
        displayImageFromForecast(forecast, tomorrowImageView!!)
    }

    private fun displayDayAfterTomorrowForecast(forecast: Forecast?) {
        dayAfterTomorrowDateTextView?.text = forecast?.date
        dayAfterTomorrowSubdateTextView?.text = forecast?.dateLabel
        dayAfterTomorrowTelopTextView?.text = forecast?.telop
        dayAfterTomorrowMaxCelsiusTextView?.text = getMaxCelsiusFromForecast(forecast)
        dayAfterTomorrowMinCelsiusTextView?.text = getMinCelsiusFromForecast(forecast)
        displayImageFromForecast(forecast, dayAfterTomorrowImageView!!)
    }

    private fun getMinCelsiusFromForecast(forecast: Forecast?): String {
        val vForecast = forecast ?: return "-"
        val temperature = vForecast.temperature ?: return "-"
        val min = temperature.min ?: return "-"
        val celsius = min.celsius ?: return "-"
        return if (celsius.isEmpty()) "-" else celsius + "℃"
    }

    private fun getMaxCelsiusFromForecast(forecast: Forecast?): String {
        val vForecast = forecast ?: return "-"
        val temperature = vForecast.temperature ?: return "-"
        val max = temperature.max ?: return "-"
        val celsius = max.celsius ?: return "-"
        return if (celsius.isEmpty()) "-" else celsius + "℃"
    }

    private fun displayImageFromForecast(forecast: Forecast?, imageView: ImageView) {
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

    private fun requestWeather(cityId: String) {
        showProgress("", "データ取得中...")
        NetworkManager.requestWeather(cityId, object : NetworkManager.RequestWeatherCallback {
            override fun onSuccess(weather: Weather) {
                dismissProgress()
                this@WeatherActivity.weather = weather
                displayForecasts(weather.forecasts)
            }

            override fun onFailure(message: String) {
                dismissProgress()
                val dialog = CommonDialogFragment.newInstance(
                    "通信失敗", message,
                    "OK", "", false
                )
                dialog.show(supportFragmentManager, CommonDialogFragment.DIALOG_TAG)
            }
        })
    }

    private fun showProgress(title: String, message: String) {
        dismissProgress()
        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle(title)
        progressDialog?.setMessage(message)
        progressDialog?.show()
    }

    private fun dismissProgress() {
        if (progressDialog == null) {
            return
        }
        progressDialog?.dismiss()
        progressDialog = null
    }
}
