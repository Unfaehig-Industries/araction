package tech.unfaehig_industries.tooz.araction.sensor_data


class SafeSensorReading (val sensor: String) {
    var data: Map<String, Double> = emptyMap()
        private set
    var len = data.size

    constructor(sensor: String, x: Double) : this(sensor) {
        data = data.plus(Pair("x", x))
    }

    constructor(sensor: String, x: Double, y: Double, z: Double) : this(sensor) {
        data = data.plus(Pair("x", x))
        data = data.plus(Pair("y", y))
        data = data.plus(Pair("z", z))
    }
    constructor(sensor: String, w: Double, x: Double, y: Double, z: Double) : this(sensor) {
        data = data.plus(Pair("w", w))
        data = data.plus(Pair("x", x))
        data = data.plus(Pair("y", y))
        data = data.plus(Pair("z", z))
    }

    fun dataString(): String {
        var sensorReadingString = "$sensor: "
        for (value in data) {
            sensorReadingString = sensorReadingString.plus(value.toString()).plus(", ")
        }

        return sensorReadingString
    }
}