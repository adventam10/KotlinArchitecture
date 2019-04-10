package com.am10.kotlinarchitecture

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class WeatherActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WEATHER = "EXTRA_WEATHER"
        const val EXTRA_CITY_DATA = "EXTRA_CITY_DATA"
    }
    val STATE_MODEL = "STATE_MODEL"
    
    private var fragment: WeatherFragment? = null
    private var model = WeatherModel()
    private var progressDialog: ProgressDialog? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        if (savedInstanceState == null) {
            val intent = intent
            model.weather = intent.getSerializableExtra(EXTRA_WEATHER) as Weather
            model.cityData = intent.getSerializableExtra(EXTRA_CITY_DATA) as CityData
            fragment = WeatherFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragment!!)
            transaction.commit()
        } else {
            fragment = supportFragmentManager.fragments[0] as WeatherFragment
            model = savedInstanceState.getSerializable(STATE_MODEL) as WeatherModel
        }
        
        // アクションバーに前画面に戻る機能をつける
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = model.cityData.name
    }

    override fun onResume() {
        super.onResume()
        displayForecasts(model.weather?.forecasts)
    }
    
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable(STATE_MODEL, model)
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
                requestWeather(model.cityData.cityId)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayForecasts(forecasts: List<Forecast>?) {
        fragment?.displayForecast(WeatherFragment.Date.Today, null, model.getMinCelsiusFromForecast(null), model.getMaxCelsiusFromForecast(null))
        fragment?.displayForecast(WeatherFragment.Date.Tomorrow, null, model.getMinCelsiusFromForecast(null), model.getMaxCelsiusFromForecast(null))
        fragment?.displayForecast(WeatherFragment.Date.DayAfterTomorrow, null, model.getMinCelsiusFromForecast(null), model.getMaxCelsiusFromForecast(null))
        if (forecasts == null) {
            return
        }
        for ((index, forecast) in forecasts.withIndex()) {
            var date = WeatherFragment.Date.Today
            if (index == 0) {
                date = WeatherFragment.Date.Today
            } else if (index == 1) {
                date = WeatherFragment.Date.Tomorrow
            } else if (index == 2) {
                date = WeatherFragment.Date.DayAfterTomorrow
            }
            fragment?.displayForecast(date, forecast, model.getMinCelsiusFromForecast(forecast), model.getMaxCelsiusFromForecast(forecast))
            model.displayImageFromForecast(forecast, fragment!!.getImageView(date)!!)
        }
    }
    
    private fun requestWeather(cityId: String) {
        showProgress("", "データ取得中...")
        NetworkManager.requestWeather(cityId, object : NetworkManager.RequestWeatherCallback {
            override fun onSuccess(weather: Weather) {
                dismissProgress()
                this@WeatherActivity.model.weather = weather
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
