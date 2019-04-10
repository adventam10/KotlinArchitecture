package com.am10.kotlinarchitecture

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import java.util.*

class AreaFilterListAdapter(context: Context, areaCheckList: ArrayList<Area>, listener: OnAreaFilterListener) : BaseAdapter() {
    enum class Area// コンストラクタの定義
    private constructor(// フィールドの定義
        var id: Int
    ) {
        Hokkaido(0),
        Tohoku(1),
        Kanto(2),
        Chubu(3),
        Kinki(4),
        Chugoku(5),
        Shikoku(6),
        Kyushu(7);

        fun getName() :String {
            when (this) {
                Hokkaido -> return "北海道"
                Tohoku -> return "東北"
                Kanto -> return "関東"
                Chubu -> return "中部"
                Kinki -> return "近畿"
                Chugoku -> return "中国"
                Shikoku -> return "四国"
                Kyushu -> return "九州"
            }
        }
    }

    internal var context: Context = context
    internal var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    companion object {
        val areaList = ArrayList(Arrays.asList(*Area.values()))
    }
    internal var areaCheckList: ArrayList<Area> = areaCheckList
    private val listener: OnAreaFilterListener = listener

    fun refresh(areaCheckList: ArrayList<Area>) {
        this.areaCheckList = areaCheckList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return areaList.size
    }

    override fun getItem(position: Int): Any {
        return areaList[position]
    }

    override fun getItemId(position: Int): Long {
        return areaList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = layoutInflater!!.inflate(R.layout.area_filter_list_item, parent, false)
        val area = areaList[position]
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        checkBox.isChecked = areaCheckList.contains(area)
        checkBox.text = area.getName()
        checkBox.setOnClickListener { v ->
            val areaCheckBox = v as CheckBox
            listener.onAreaFilterChecked(area, areaCheckBox.isChecked)
        }
        return view
    }

    interface OnAreaFilterListener {
        fun onAreaFilterChecked(area: Area, isCheck: Boolean)
    }
}