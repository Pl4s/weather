package com.alexplas.weathernew.utils

class WindDeg {

    companion object {

        fun toTextualDescription(deg: Int): String {
            if (deg >= 0 && deg <= 21 || deg >= 337 && deg <= 360) return "N"
            if (deg >= 22 && deg <= 44) return "NNE"
            if (deg >= 45 && deg <= 66) return "NE"
            if (deg >= 67 && deg <= 89) return "ENE"
            if (deg >= 90 && deg <= 111) return "E"
            if (deg >= 112 && deg <= 134) return "ESE"
            if (deg >= 135 && deg <= 156) return "SE"
            if (deg >= 157 && deg <= 179) return "SSE"
            if (deg >= 180 && deg <= 201) return "S"
            if (deg >= 202 && deg <= 224) return "SSW"
            if (deg >= 225 && deg <= 246) return "SW"
            if (deg >= 247 && deg <= 269) return "WSW"
            if (deg >= 270 && deg <= 291) return "W"
            if (deg >= 292 && deg <= 314) return "WNW"
            if (deg >= 315 && deg <= 336) return "NW"

            return "no data"
        }
    }
}