package com.am10.kotlinarchitecture

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import java.util.ArrayList

class PrefectureListRecyclerAdapter (context: Context, cityDataList: ArrayList<CityData>, listener: OnRecyclerListener): RecyclerView.Adapter<PrefectureListRecyclerAdapter.ViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cityDataList: ArrayList<CityData> = cityDataList
    private val context: Context = context
    private val listener: OnRecyclerListener = listener
    private var selectedRow = -1
    private var favoriteCityIdList: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PrefectureListRecyclerAdapter.ViewHolder {
        // 表示するレイアウトを設定
        return ViewHolder(inflater.inflate(R.layout.prefecture_list_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        // データ表示
        if (cityDataList.size > i) {
            val data = cityDataList.get(i)
            viewHolder.textView.setText(data.name)
            viewHolder.checkBox.isChecked = favoriteCityIdList.contains(data.cityId)
        } else {
            viewHolder.checkBox.isChecked = false
        }

        viewHolder.checkBox.setOnClickListener { v -> listener.onRecyclerChecked(v, i, viewHolder.checkBox.isChecked) }
        if (selectedRow == i) {
            viewHolder.rowLayout.setBackgroundColor(Color.parseColor("#e0e0e0"))
        } else {
            viewHolder.rowLayout.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        // クリック処理
        viewHolder.itemView.setOnClickListener { v ->
            selectedRow = i
            listener.onRecyclerClicked(v, i)
        }
    }

    override fun getItemCount(): Int {
        return cityDataList.size
    }

    fun setCityDataList(cityDataList: ArrayList<CityData>) {
        this.cityDataList = cityDataList
    }

    fun setFavoriteCityIdList(favoriteCityIdList: ArrayList<String>) {
        this.favoriteCityIdList = favoriteCityIdList
    }

    fun refresh() {
        selectedRow = -1
        notifyDataSetChanged()
    }

    // ViewHolder(固有ならインナークラスでOK)
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textView: TextView = itemView.findViewById(R.id.textView_prefecture)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkBox_favorite)
        var rowLayout: LinearLayout = itemView.findViewById(R.id.layout_row)
    }

    interface OnRecyclerListener {
        fun onRecyclerClicked(v: View, position: Int)
        fun onRecyclerChecked(v: View, position: Int, isCheck: Boolean)
    }
}