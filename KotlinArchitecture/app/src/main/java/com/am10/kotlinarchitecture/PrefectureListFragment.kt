package com.am10.kotlinarchitecture

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import java.util.ArrayList

class PrefectureListFragment: Fragment() {
    interface OnEventListener {
        fun onAreaFilterClickListener(v: View)
        fun onFavoriteCheckedChanged(buttonView: CompoundButton, isChecked: Boolean)
    }

    var adapter: PrefectureListRecyclerAdapter? = null
    var recyclerView: RecyclerView? = null
    var noDataTextView: TextView? = null
    var areaFilterButton: ImageButton? = null
    var favoriteCheckBox: CheckBox? = null
    private var listener: OnEventListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_prefecture_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        val itemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        recyclerView?.addItemDecoration(itemDecoration)
        recyclerView?.adapter = adapter
        noDataTextView = view.findViewById(R.id.textView_no_data)
        noDataTextView?.visibility = View.GONE
        areaFilterButton = view.findViewById(R.id.button_area_filter)
        areaFilterButton?.setOnClickListener { v -> 
            listener?.onAreaFilterClickListener(v)
        }
        favoriteCheckBox = view.findViewById(R.id.checkBox_favorite)
        favoriteCheckBox?.setOnCheckedChangeListener { buttonView, isChecked -> 
            listener?.onFavoriteCheckedChanged(buttonView, isChecked)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnEventListener) {
            listener = context
        }

        if (context is PrefectureListRecyclerAdapter.OnRecyclerListener) {
            adapter =
                    PrefectureListRecyclerAdapter(context, context as PrefectureListRecyclerAdapter.OnRecyclerListener)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        adapter = null
    }

    fun setupAdapter(tableDataList: ArrayList<CityData>, favoriteCityIdList: ArrayList<String>) {
        adapter?.setCityDataList(tableDataList)
        adapter?.setFavoriteCityIdList(favoriteCityIdList)
    }

    fun notifyItemChanged(position: Int, favoriteCityIdList: ArrayList<String>) {
        adapter?.setFavoriteCityIdList(favoriteCityIdList)
        adapter?.notifyItemChanged(position)
    }

    fun recyclerViewRefresh() {
        adapter?.refresh()
    }

    fun recyclerViewRefresh(tableDataList: ArrayList<CityData>) {
        adapter?.setCityDataList(tableDataList)
        adapter?.refresh()

        noDataTextView?.visibility = if (tableDataList.isEmpty()) View.VISIBLE else View.GONE
    }
}