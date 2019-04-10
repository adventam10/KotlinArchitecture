package com.am10.kotlinarchitecture

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.*

class PrefectureListActivity : AppCompatActivity(), PrefectureListRecyclerAdapter.OnRecyclerListener, PrefectureListFragment.OnEventListener {

    val STATE_MODEL = "STATE_MODEL"
    private var fragment: PrefectureListFragment? = null
    private lateinit var model: PrefectureListModel
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefecture_list)

        supportActionBar?.title = "地域"

        savedInstanceState?.let {
            fragment = supportFragmentManager.fragments[0] as PrefectureListFragment
            model = it.getSerializable(STATE_MODEL) as PrefectureListModel
        } ?: run {
            fragment = PrefectureListFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, fragment!!)
            transaction.commit()
            model = PrefectureListModel(this)
        }
    }

    override fun onResume() {
        super.onResume()
        fragment?.setupAdapter(model.tableDataList, model.favoriteCityIdList)
        fragment?.recyclerViewRefresh()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable(STATE_MODEL, model)
    }

    override fun onAreaFilterClickListener(v: View) {
        showAreaPopupWindow(v)
    }

    override fun onFavoriteCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        model.isFavoriteChecked = isChecked
        refreshRecyclerView()
    }

    override fun onRecyclerClicked(v: View, position: Int) {
        // セルクリック処理
        showProgress("", "データ取得中...")
        fragment?.notifyItemChanged(position, model.favoriteCityIdList)
        model.selectedCityData = model.tableDataList[position]
        requestWeather(model.selectedCityData!!.cityId)
    }

    override fun onRecyclerChecked(v: View, position: Int, isCheck: Boolean) {
        val cityData = model.tableDataList[position]
        val cityId = cityData.cityId
        model.editFavoriteCityIdList(cityId, isCheck)
        PrefectureListModel.saveFavoriteCityIdList(this, model.favoriteCityIdList)
        fragment?.notifyItemChanged(position, model.favoriteCityIdList)
    }

    private fun refreshRecyclerView() {
        model.tableDataList = model.setTableDataList(model.isFavoriteChecked)
        fragment?.recyclerViewRefresh(model.tableDataList)
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
                fragment?.recyclerViewRefresh()
            }
        })
    }

    private fun showWeatherActivity(weather: Weather) {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra(WeatherActivity.EXTRA_WEATHER, weather)
        intent.putExtra(WeatherActivity.EXTRA_CITY_DATA, model.selectedCityData)
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
            AreaFilterListAdapter(this, model.areaFilterList, object : AreaFilterListAdapter.OnAreaFilterListener {
                override fun onAreaFilterChecked(area: AreaFilterListAdapter.Area, isCheck: Boolean) {
                    model.editAreaFilterList(area, isCheck)
                    allCheckBox.isChecked = model.areaFilterList.containsAll(AreaFilterListAdapter.areaList)
                    refreshRecyclerView()
                }
            })

        allCheckBox.setOnClickListener { v ->
            val checkBox = v as CheckBox
            model.areaFilterList.clear()
            if (checkBox.isChecked) {
                model.areaFilterList = ArrayList<AreaFilterListAdapter.Area>(AreaFilterListAdapter.areaList)
            }
            adapter.refresh(model.areaFilterList)
            refreshRecyclerView()
        }
        allCheckBox.isChecked = model.areaFilterList.containsAll(AreaFilterListAdapter.areaList)
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
}
