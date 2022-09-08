package tech.unfaehig_industries.tooz.araction.positional_tracking

class RotationalPosition(private val x: Double = 0.0,
                         private val y: Double = 0.0,
                         private val z: Double = 0.0) {


    fun getPositionString(): String {
        return "x:$x y:$y z:$z"
    }

    fun getX(): Double {
        return x
    }
    fun getY(): Double {
        return y
    }
    fun getZ(): Double {
        return z
    }

    fun calculateDirection(target: RotationalPosition): Double {

        val zeroPointX = Math.cos((target.getX() + 1) * Math.PI)
        val zeroPointY = Math.cos((target.getY() + 1) * Math.PI)
        val zeroPointZ = Math.cos((target.getZ() + 1) * Math.PI)

        val facingPointX = Math.cos((x + 1) * Math.PI)
        val facingPointY = Math.cos((y + 1) * Math.PI)
        val facingPointZ = Math.cos((z + 1) * Math.PI)

        val facingZeroVectorX = zeroPointX - facingPointX
        val facingZeroVectorY = zeroPointY - facingPointY
        val facingZeroVectorZ = zeroPointZ - facingPointZ

        // TODO calc angle between facingZeroVector and Vector that points up (0,0,1)
        // TODO adjust angle vor head jaw



        return 0.0
    }

}
