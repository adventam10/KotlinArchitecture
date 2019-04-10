package com.am10.kotlinarchitecture

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class PrefectureListActivity : AppCompatActivity(), PrefectureListRecyclerAdapter.OnRecyclerListener {

    val STATE_CITY_DATA_LIST = "STATE_CITY_DATA_LIST"
    val STATE_TABLE_DATA_LIST = "STATE_TABLE_DATA_LIST"
    val STATE_SELECTED_CITY_DATA = "STATE_SELECTED_CITY_DATA"
    val STATE_FAVORITE_CITY_ID_LIST = "STATE_FAVORITE_CITY_ID_LIST"
    val STATE_AREA_FILTER_LIST = "STATE_AREA_FILTER_LIST"
    val STATE_IS_FAVORITE_CHECKED = "STATE_IS_FAVORITE_CHECKED"
    val PREF_KEY_FAVORITE = "PREF_KEY_FAVORITE"
    private var recyclerView: RecyclerView? = null
    private var noDataTextView: TextView? = null
    private var adapter: PrefectureListRecyclerAdapter? = null
    private var progressDialog: ProgressDialog? = null
    private var cityDataList: ArrayList<CityData> = ArrayList()
    private var tableDataList: ArrayList<CityData> = ArrayList()
    private var selectedCityData: CityData? = null
    private var favoriteCityIdList: ArrayList<String> = ArrayList()
    private var areaFilterList = ArrayList<AreaFilterListAdapter.Area>(AreaFilterListAdapter.areaList)
    private var isFavoriteChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefecture_list)

        supportActionBar?.title = "地域"

        selectedCityData = null
        savedInstanceState?.let {
            cityDataList = it.getSerializable(STATE_CITY_DATA_LIST) as ArrayList<CityData>
            tableDataList = it.getSerializable(STATE_TABLE_DATA_LIST) as ArrayList<CityData>
            selectedCityData = it.getSerializable(STATE_SELECTED_CITY_DATA) as? CityData
            favoriteCityIdList = it.getSerializable(STATE_FAVORITE_CITY_ID_LIST) as ArrayList<String>
            areaFilterList =
                    it.getSerializable(STATE_AREA_FILTER_LIST) as ArrayList<AreaFilterListAdapter.Area>
            isFavoriteChecked = it.getBoolean(STATE_IS_FAVORITE_CHECKED)
        } ?: run {
            val cityDataJson = readJsonFile(this, "CityData.json")
            val gson = Gson()
            val cityDataList = gson.fromJson(cityDataJson, CityDataList::class.java)
            favoriteCityIdList = loadFavoriteCityIdList(this)
            this.cityDataList = ArrayList(cityDataList.cityDataList)
            tableDataList = ArrayList(this.cityDataList)
        }

        noDataTextView = findViewById<TextView>(R.id.textView_no_data)
        noDataTextView?.visibility = View.GONE
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        adapter = PrefectureListRecyclerAdapter(this, tableDataList, this)
        adapter?.setFavoriteCityIdList(favoriteCityIdList)
        recyclerView?.setAdapter(adapter)

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView?.addItemDecoration(itemDecoration)

        val checkBox = findViewById<CheckBox>(R.id.checkBox_favorite)
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            isFavoriteChecked = isChecked
            refreshRecyclerView()
        }
        checkBox.isChecked = isFavoriteChecked

        val button = findViewById<ImageButton>(R.id.button_area_filter)
        button.setOnClickListener { v -> showAreaPopupWindow(v) }
    }

    override fun onResume() {
        super.onResume()
        adapter?.refresh()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable(STATE_CITY_DATA_LIST, cityDataList)
        savedInstanceState.putSerializable(STATE_TABLE_DATA_LIST, tableDataList)
        if (selectedCityData != null) {
            savedInstanceState.putSerializable(STATE_SELECTED_CITY_DATA, selectedCityData)
        }
        savedInstanceState.putSerializable(STATE_FAVORITE_CITY_ID_LIST, favoriteCityIdList)
        savedInstanceState.putSerializable(STATE_AREA_FILTER_LIST, areaFilterList)
        savedInstanceState.putBoolean(STATE_IS_FAVORITE_CHECKED, isFavoriteChecked)
    }

    override fun onRecyclerClicked(v: View, position: Int) {
        // セルクリック処理
        showProgress("", "データ取得中...")
        adapter?.notifyItemChanged(position)
        selectedCityData = tableDataList[position]
        requestWeather(selectedCityData!!.cityId)
    }

    override fun onRecyclerChecked(v: View, position: Int, isCheck: Boolean) {
        val cityData = tableDataList[position]
        val cityId = cityData.cityId
        editFavoriteCityIdList(cityId, isCheck)
        saveFavoriteCityIdList(this, favoriteCityIdList)
        adapter?.setFavoriteCityIdList(favoriteCityIdList)
        adapter?.notifyItemChanged(position)
    }

    private fun refreshRecyclerView() {
        tableDataList = setTableDataList(isFavoriteChecked)
        adapter?.setCityDataList(tableDataList)
        adapter?.refresh()

        noDataTextView?.visibility = if (tableDataList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun editFavoriteCityIdList(cityId: String, isCheck: Boolean) {
        if (isCheck) {
            if (!favoriteCityIdList.contains(cityId)) {
                favoriteCityIdList.add(cityId)
            }
        } else {
            if (favoriteCityIdList.contains(cityId)) {
                favoriteCityIdList.remove(cityId)
            }
        }
    }

    private fun setTableDataList(isFavorite: Boolean): ArrayList<CityData> {
        val tableDataList = setFavoriteFilter(isFavorite, cityDataList)
        return setAreaFilter(tableDataList, areaFilterList)
    }

    private fun setFavoriteFilter(isFavorite: Boolean, cityDataList: ArrayList<CityData>): ArrayList<CityData> {
        if (!isFavorite) {
            return ArrayList(cityDataList)
        }
        val filteredList = ArrayList<CityData>()
        for (cityData in cityDataList) {
            if (favoriteCityIdList.contains(cityData.cityId)) {
                filteredList.add(cityData)
            }
        }
        return filteredList
    }

    private fun setAreaFilter(cityDataList: ArrayList<CityData>, areaList: ArrayList<AreaFilterListAdapter.Area>): ArrayList<CityData> {
        val filteredList = ArrayList<CityData>()
        for (cityData in cityDataList) {
            for (area in areaList) {
                if (cityData.area === area.id) {
                    filteredList.add(cityData)
                }
            }
        }
        return filteredList
    }

    private fun requestWeather(cityId: String) {
        NetworkManager.requestWeather(cityId, object : NetworkManager.RequestWeatherCallback {
            override fun onSuccess(weather: Weather) {
                dismissProgress()
                showWeatherActivity(weather)
            }

            override fun onFailure(message: String) {
                dismissProgress()
                val dialog = CommonDialogFragment.newInstance(
                    "通信失敗", message,
                    "OK", "", false
                )
                dialog.show(supportFragmentManager, CommonDialogFragment.DIALOG_TAG)
                adapter?.refresh()
            }
        })
    }

    private fun showWeatherActivity(weather: Weather) {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra(WeatherActivity.EXTRA_WEATHER, weather)
        intent.putExtra(WeatherActivity.EXTRA_CITY_DATA, selectedCityData)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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

    private fun showAreaPopupWindow(v: View) {
        val popupView = layoutInflater.inflate(R.layout.area_filter_popwindow, null)
        val listView = popupView.findViewById<ListView>(R.id.listView)
        val allCheckBox = popupView.findViewById<CheckBox>(R.id.checkBox)
        val adapter =
            AreaFilterListAdapter(this, areaFilterList, object : AreaFilterListAdapter.OnAreaFilterListener {
                override fun onAreaFilterChecked(area: AreaFilterListAdapter.Area, isCheck: Boolean) {
                    if (isCheck) {
                        if (!areaFilterList.contains(area)) {
                            areaFilterList.add(area)
                        }
                    } else {
                        if (areaFilterList.contains(area)) {
                            areaFilterList.remove(area)
                        }
                    }
                    allCheckBox.isChecked = areaFilterList.containsAll(AreaFilterListAdapter.areaList)
                    refreshRecyclerView()
                }
            })

        allCheckBox.setOnClickListener { v ->
            val checkBox = v as CheckBox
            areaFilterList.clear()
            if (checkBox.isChecked) {
                areaFilterList = ArrayList<AreaFilterListAdapter.Area>(AreaFilterListAdapter.areaList)
            }
            adapter.refresh(areaFilterList)
            refreshRecyclerView()
        }
        allCheckBox.isChecked = areaFilterList.containsAll(AreaFilterListAdapter.areaList)
        val button = findViewById<ImageButton>(R.id.button_area_filter)
        listView.adapter = adapter
        val popupWindow = PopupWindow(this)
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        val height = if (popupWindow.getMaxAvailableHeight(v) >= 800) 800 else popupWindow.getMaxAvailableHeight(v)
        popupWindow.height = height
        popupWindow.width = button.width
        popupWindow.contentView = popupView
        popupWindow.showAsDropDown(v)
    }

    private fun saveFavoriteCityIdList(context: Context, favoriteCityIdList: java.util.ArrayList<String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val set = HashSet(favoriteCityIdList)
        prefs.edit().putStringSet(PREF_KEY_FAVORITE, set).apply()
    }

    private fun loadFavoriteCityIdList(context: Context): ArrayList<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val set = prefs.getStringSet(PREF_KEY_FAVORITE, HashSet())
        return ArrayList(set!!)
    }

    private fun readJsonFile(context: Context, fileName: String): String? {
        val assetManager = context.assets
        try {
            val inputStream = assetManager.open(fileName)
            val inputStreamReader = InputStreamReader(inputStream)
            val br = BufferedReader(inputStreamReader)

            var data = ""
            var str: String? = br.readLine()
            while (str != null) {
                data += str
                str = br.readLine()
            }

            br.close()
            return data
        } catch (e: IOException) {
            return null
        }
    }
}
