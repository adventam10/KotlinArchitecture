package com.am10.kotlinarchitecture

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Serializable
import java.util.*

class PrefectureListModel(context: Context): Serializable {

    companion object {
        const val PREF_KEY_FAVORITE = "PREF_KEY_FAVORITE"

        fun saveFavoriteCityIdList(context: Context, favoriteCityIdList: ArrayList<String>) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val set = HashSet(favoriteCityIdList)
            prefs.edit().putStringSet(PREF_KEY_FAVORITE, set).apply()
        }

        fun loadFavoriteCityIdList(context: Context): ArrayList<String> {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val set = prefs.getStringSet(PREF_KEY_FAVORITE, HashSet())
            return ArrayList(set!!)
        }

        fun readJsonFile(context: Context, fileName: String): String? {
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

        fun getCityDataList(context: Context): ArrayList<CityData> {
            val cityDataJson = readJsonFile(context, "CityData.json")
            val gson = Gson()
            val cityDataList = gson.fromJson(cityDataJson, CityDataList::class.java)
            return ArrayList(cityDataList.cityDataList)
        }
    }
    var cityDataList: ArrayList<CityData> = getCityDataList(context)
    var tableDataList: ArrayList<CityData> = getCityDataList(context)
    var selectedCityData: CityData? = null
    var favoriteCityIdList: ArrayList<String> = loadFavoriteCityIdList(context)
    var areaFilterList = ArrayList<AreaFilterListAdapter.Area>(AreaFilterListAdapter.areaList)
    var isFavoriteChecked = false

    fun editFavoriteCityIdList(cityId: String, isCheck: Boolean) {
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

    fun editAreaFilterList(area: AreaFilterListAdapter.Area, isCheck: Boolean) {
        if (isCheck) {
            if (!areaFilterList.contains(area)) {
                areaFilterList.add(area)
            }
        } else {
            if (areaFilterList.contains(area)) {
                areaFilterList.remove(area)
            }
        }
    }

    fun setTableDataList(isFavorite: Boolean): ArrayList<CityData> {
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
}