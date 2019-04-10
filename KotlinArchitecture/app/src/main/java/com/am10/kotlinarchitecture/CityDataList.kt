package com.am10.kotlinarchitecture

import java.io.Serializable

data class CityDataList(
    val cityDataList: List<CityData>
)

data class CityData(
    val area: Int,
    val cityId: String,
    val name: String
): Serializable