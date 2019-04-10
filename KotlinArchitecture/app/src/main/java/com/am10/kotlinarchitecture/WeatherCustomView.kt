package com.am10.kotlinarchitecture

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class WeatherCustomView: LinearLayout {
    private var dateTextView: TextView? = null
    private var subdateTextView: TextView? = null
    private var telopTextView: TextView? = null
    private var maxCelsiusTextView: TextView? = null
    private var minCelsiusTitleTextView: TextView? = null
    private var maxCelsiusTitleTextView: TextView? = null
    private var minCelsiusTextView: TextView? = null
    var imageView: ImageView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.weatherCustomViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // このへんで初期化処理
        LayoutInflater.from(context).inflate(R.layout.weather_layout, this, true)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.WeatherCustomView,
            defStyleAttr, 0
        )

        val fontSize = a.getInteger(R.styleable.WeatherCustomView_fontSize, 15).toFloat()
        a.recycle()
        dateTextView = findViewById(R.id.textView_date)
        dateTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        subdateTextView = findViewById(R.id.textView_subdate)
        subdateTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        telopTextView = findViewById(R.id.textView_telop)
        telopTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        maxCelsiusTextView = findViewById(R.id.textView_max_celsius)
        maxCelsiusTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        minCelsiusTextView = findViewById(R.id.textView_min_celsius)
        minCelsiusTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        minCelsiusTitleTextView = findViewById(R.id.textView_min_celsius_title)
        minCelsiusTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        maxCelsiusTitleTextView = findViewById(R.id.textView_max_celsius_title)
        maxCelsiusTitleTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
        imageView = findViewById(R.id.imageView)
    }

    fun displayForecast(forecast: Forecast?, minCelsius: String, maxCelsius: String) {
        if (forecast == null) {
            dateTextView?.text = ""
            subdateTextView?.text = ""
            telopTextView?.text = ""
        } else {
            dateTextView?.text = forecast.date
            subdateTextView?.text = forecast.dateLabel
            telopTextView?.text = forecast.telop
        }
        maxCelsiusTextView?.text = maxCelsius
        minCelsiusTextView?.text = minCelsius
    }
}