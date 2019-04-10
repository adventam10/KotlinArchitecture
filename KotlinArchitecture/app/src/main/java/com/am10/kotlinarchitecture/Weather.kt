package com.am10.kotlinarchitecture

import java.io.Serializable

data class Weather(
    val copyright: Copyright?,
    val description: Description?,
    val forecasts: List<Forecast>,
    val link: String?,
    val location: Location?,
    val pinpointLocations: List<PinpointLocation>?,
    val publicTime: String?,
    val title: String?
): Serializable

data class Copyright(
    val image: Image?,
    val link: String?,
    val provider: List<Provider>?,
    val title: String?
): Serializable

data class Image(
    val height: Int?,
    val link: String?,
    val title: String?,
    val url: String?,
    val width: Int?
): Serializable

data class Provider(
    val link: String?,
    val name: String?
): Serializable

data class Location(
    val area: String?,
    val city: String?,
    val prefecture: String?
): Serializable

data class Forecast(
    val date: String?,
    val dateLabel: String?,
    val image: Image?,
    val telop: String?,
    val temperature: Temperature?
): Serializable

data class Temperature(
    val max: Max?,
    val min: Max?
): Serializable

data class Max(
    val celsius: String?,
    val fahrenheit: String?
): Serializable

data class PinpointLocation(
    val link: String?,
    val name: String?
): Serializable

data class Description(
    val publicTime: String?,
    val text: String?
): Serializable