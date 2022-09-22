package tech.unfaehig_industries.tooz.araction.positional_tracking

import timber.log.Timber
import java.util.*

class RotationalPosition(private val x: Double = 0.0,
                         private val y: Double = 0.0,
                         private val z: Double = 0.0) {

    private var xF: Double = 0.0
    private var yF: Double = 0.0
    private var zF: Double = 0.0

    init {
        val vect = rotateVector(0.0,1.0,0.0, getQuaternionFromEuler(x, y, z))
        xF = vect[0]
        yF = vect[1]
        zF = vect[2]
    }

    fun getRotationString(): String {
        return "x:$x y:$y z:$z"
    }
    fun getPositionString(): String {
        return "x:$xF y:$yF z:$zF"
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

        Timber.d("Calculating Direction... ${xF}, ${yF}, ${zF}")

        val facingZeroVectorX = target.getXF() - xF
        val facingZeroVectorY = target.getYF() - yF
        val facingZeroVectorZ = target.getZF() - zF

        // calc angle between facingZeroVector and Vector that points up (0,0,1)
        // TODO adjust angle for head jaw by using vector pointing up from head (xF,yF,zF)
        val angle = Math.acos(facingZeroVectorZ /
                (Math.sqrt(facingZeroVectorX * facingZeroVectorX
                        + facingZeroVectorY * facingZeroVectorY
                        + facingZeroVectorZ * facingZeroVectorZ)))

        return angle
    }

    fun getQuaternionFromEuler(yaw: Double, pitch: Double, roll: Double): Array<Double> {

//  Convert an Euler angle to a unit quaternion (not normalizing it).
//
//  Input
//    :param roll: The roll (rotation around x-axis) angle in radians.
//    :param pitch: The pitch (rotation around y-axis) angle in radians.
//    :param yaw: The yaw (rotation around z-axis) angle in radians.
//
//  Output
//    :return (not normalized) qx, qy, qz, qw: The orientation in quaternion [x,y,z,w] format
//
    val qx = Math.sin(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) - Math.cos(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2)
    val qy = Math.cos(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2)
    val qz = Math.cos(roll/2) * Math.cos(pitch/2) * Math.sin(yaw/2) - Math.sin(roll/2) * Math.sin(pitch/2) * Math.cos(yaw/2)
    val qw = Math.cos(roll/2) * Math.cos(pitch/2) * Math.cos(yaw/2) + Math.sin(roll/2) * Math.sin(pitch/2) * Math.sin(yaw/2)

//    val magnitude = Math.sqrt(qx*qx + qy*qy + qz*qz + qw*qw)
//
//    if (magnitude != 0.0) {
//        qx /= magnitude
//        qy /= magnitude
//        qz /= magnitude
//        qw /= magnitude
//    } else {
//        return arrayOf(0.0, 0.0, 0.0, 0.0)
//    }


    return arrayOf(qx, qy, qz, qw)
    }

    fun rotateVector (x: Double, y: Double, z: Double, quat: Array<Double>): Array<Double> {

        var u = 0.0
        var v = 0.0
        var w = 0.0
        val theta = quat[3]

        val magnitude = Math.sqrt(quat[0]*quat[0] + quat[1]*quat[1] + quat[2]*quat[2])

        if (magnitude != 0.0) {
            u = quat[0] / magnitude
            v = quat[1] / magnitude
            w = quat[2] / magnitude
        }

        val xPrime: Double =
            u * (u * x + v * y + w * z) * (1.0 - Math.cos(theta)) + x * Math.cos(theta) + (-w * y + v * z) * Math.sin(
                theta
            )
        val yPrime: Double =
            v * (u * x + v * y + w * z) * (1.0 - Math.cos(theta)) + y * Math.cos(theta) + (w * x - u * z) * Math.sin(
                theta
            )
        val zPrime: Double =
            w * (u * x + v * y + w * z) * (1.0 - Math.cos(theta)) + z * Math.cos(theta) + (-v * x + u * y) * Math.sin(
                theta
            )

        return arrayOf(xPrime, yPrime, zPrime)
    }
}
