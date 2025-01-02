package com.team13.karlskronaexplorer.domain

import android.location.Location

class Position(private val latitude: Double, private val longitude: Double) {

    fun distanceTo(other: Position): Double {
        val vals = FloatArray(1)
        Location.distanceBetween(latitude, longitude, other.latitude, other.longitude, vals)

        return vals[0].toDouble()
    }

    fun format(): String {
        return "%.2f, %.2f".format(latitude, longitude)
    }

    fun getLatitude(): Double {
        return latitude
    }

    fun getLongitude(): Double {
        return longitude
    }
}