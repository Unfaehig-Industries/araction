package tech.unfaehig_industries.tooz.araction.positional_tracking

import timber.log.Timber
import java.util.*
import kotlin.math.sqrt

class RotationalPosition(private val x: Double = 0.0,
                         private val y: Double = 0.0,
                         private val z: Double = 0.0) {

    private var xF: Double = 0.0
    private var yF: Double = 0.0
    private var zF: Double = 0.0

    init {
        val magnitude = Math.sqrt(x*x + y*y + z*z)

        if (magnitude != 0.0) {
            xF = (x / magnitude)
            yF = (y / magnitude)
            zF = (z / magnitude)
        }
    }

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

    fun getXF(): Double {
        return xF
    }
    fun getYF(): Double {
        return yF
    }
    fun getZF(): Double {
        return zF
    }

    fun calculateDirection(target: RotationalPosition): Double {

        Timber.d("Calculating Direction... ${x}, ${y}, ${z}")

//        val zeroPointX = Math.cos((target.getX() + 1) * Math.PI)
//        val zeroPointY = Math.cos((target.getY() + 1) * Math.PI)
//        val zeroPointZ = Math.cos((target.getZ() + 1) * Math.PI)
//
//        val facingPointX = Math.cos((x + 1) * Math.PI)
//        val facingPointY = Math.cos((y + 1) * Math.PI)
//        val facingPointZ = Math.cos((z + 1) * Math.PI)

        Timber.d("Calculating Direction... ${xF}, ${yF}, ${zF}")

        val facingZeroVectorX = target.getXF() - xF
        val facingZeroVectorY = target.getYF() - yF
        val facingZeroVectorZ = target.getZF() - zF

        // calc angle between facingZeroVector and Vector that points up (0,0,1)
        val angle = Math.acos(facingZeroVectorZ /
                (Math.sqrt(facingZeroVectorX * facingZeroVectorX
                        + facingZeroVectorY * facingZeroVectorY
                        + facingZeroVectorZ * facingZeroVectorZ)))

        // TODO rotate vector using quaternion

        // TODO adjust angle for head jaw

        return angle
    }

    fun getQuaternionFromEuler(yaw: Double, pitch: Double, roll: Double): Array<Double> {

//  Convert an Euler angle to a unit quaternion (normalizing it).
//
//  Input
//    :param roll: The roll (rotation around x-axis) angle in radians.
//    :param pitch: The pitch (rotation around y-axis) angle in radians.
//    :param yaw: The yaw (rotation around z-axis) angle in radians.
//
//  Output
//    :return normalized qx, qy, qz, qw: The orientation in quaternion [x,y,z,w] format
//
    var qx = Math.sin(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) - Math.cos(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2)
    var qy = Math.cos(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2)
    var qz = Math.cos(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2) - Math.sin(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2)
    var qw = Math.cos(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2)

    val magnitude = Math.sqrt(qx*qx + qy*qy + qz*qz + qw*qw)

    if (magnitude != 0.0) {
        qx /= magnitude
        qy /= magnitude
        qz /= magnitude
        qw /= magnitude
    } else {
        return arrayOf(0.0, 0.0, 0.0, 0.0)
    }


    return arrayOf(qx, qy, qz, qw)
    }

}
