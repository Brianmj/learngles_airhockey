package com.brianmj

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

data class Vector2(var xDir: Float = 0.0f, var yDir: Float = 0.0f){
    val length: Float get() =  sqrt((xDir * xDir) + (yDir * yDir))
    val lengthSqr: Float get() = (xDir * xDir) + (yDir * yDir)
    val normalized: Vector2 get() = this / length
    val toList: List<Float> get() = listOf(xDir, yDir)
}

operator fun Vector2.plus(other: Vector2): Vector2 {
    return Vector2(this.xDir + other.xDir, this.yDir + other.yDir)
}

operator fun Vector2.minus(other: Vector2): Vector2 {
    return Vector2(this.xDir - other.xDir, this.yDir - other.yDir)
}

operator fun Vector2.unaryMinus() = Vector2(-xDir, -yDir)

operator fun Vector2.times(scalar: Float): Vector2 {
    return Vector2(this.xDir * scalar, this.yDir * scalar)
}

operator fun Float.times(vector2: Vector2): Vector2 {
    return vector2 * this
}

operator fun Vector2.div(scalar: Float): Vector2 {
    return Vector2(this.xDir / scalar, this.yDir / scalar)
}

operator fun Float.div(vector2: Vector2): Vector2 {
    return Vector2(this/ vector2.xDir, this / vector2.yDir)
}

data class Vector3(var xDir: Float = 0.0f, var yDir: Float = 0.0f, var zDir: Float = 0.0f)
{
    val length: Float get() = sqrt((xDir * xDir) + (yDir * yDir) + (zDir * zDir))
    val lengthSqr: Float get() = (xDir * xDir) + (yDir * yDir) + (zDir * zDir)
    val normalized: Vector3 get() = this / length
    val toList: List<Float> get() = listOf(xDir, yDir, zDir)
}

operator fun Vector3.plus(other: Vector3) = Vector3(this.xDir + other.xDir, this.yDir + other.yDir, this.zDir + other.zDir)
operator fun Vector3.minus(other: Vector3) = Vector3(this.xDir - other.xDir, this.yDir - other.yDir, this.zDir - other.zDir)
operator fun Vector3.unaryMinus(): Vector3 = Vector3(-this.xDir, -this.yDir, -this.zDir)
operator fun Vector3.times(scalar: Float) = Vector3(this.xDir * scalar, this.yDir * scalar, this.zDir * scalar)
operator fun Float.times(vector3: Vector3) = vector3 * this
operator fun Vector3.div(scalar: Float) = Vector3(this.xDir / scalar, this.yDir / scalar, this.zDir / scalar)
operator fun Float.div(vector3: Vector3) = Vector3(this / vector3.xDir, this / vector3.yDir, this / vector3.zDir)

fun Vector3.dot(other: Vector3): Float = (xDir * other.xDir) + (yDir * other.yDir) + (zDir * other.zDir)
fun Vector3.cross(other: Vector3): Vector3 {
    val x = (this.yDir * other.zDir) - (this.zDir * other.yDir)
    val y = (this.zDir * other.xDir) - (this.xDir * other.zDir)
    val z = (this.xDir * other.yDir) - (this.yDir * other.xDir)

    return Vector3(x, y, z)
}

data class Vector4(var xDir: Float = 0.0f, var yDir: Float = 0.0f, var zDir: Float = 0.0f, var wDir: Float = 0.0f)
{
    constructor(v: Vector3): this(v.xDir, v.yDir, v.zDir, 0.0f)
    constructor(v: Vector3, wComponent: Float): this(v.xDir, v.yDir, v.zDir, wComponent)

    // for the time being, i'll include the w component in the calculations. I'm not sure if it is mathematically correct, though.
    val length: Float get() = sqrt((xDir * xDir) + (yDir * yDir) + (zDir * zDir) + (wDir * wDir))
    val lengthSqr: Float get() = (xDir * xDir) + (yDir * yDir) + (zDir * zDir) + (wDir * wDir)
    val normalized: Vector4 get() = Vector4(this.xDir / length, this.yDir / length, this.zDir / length, this.wDir / length)
    val asVector3: Vector3 get() = Vector3(xDir, yDir, zDir)
    val toList: List<Float> get() = listOf(xDir, yDir, zDir, wDir)
}

operator fun Vector4.plus(other: Vector4) = Vector4(this.xDir + other.xDir, this.yDir + other.yDir, this.zDir + other.zDir, this.wDir + other.wDir)
operator fun Vector4.minus(other: Vector4) = Vector4(this.xDir - other.xDir, this.yDir - other.yDir, this.zDir - other.zDir, this.wDir - other.wDir)
operator fun Vector4.unaryMinus(): Vector4 = Vector4(-this.xDir, -this.yDir, -this.zDir, -this.wDir)
operator fun Vector4.times(scalar: Float) = Vector4(this.xDir * scalar, this.yDir * scalar, this.zDir * scalar, this.wDir * scalar)
operator fun Float.times(vector4: Vector4) = vector4 * this

fun Vector4.dot(other: Vector4): Float = (xDir * other.xDir) + (yDir * other.yDir) + (zDir * other.zDir) + (wDir * other.wDir)
fun Vector4.cross(other: Vector4): Vector3 {
    // implemented in terms of Vector3
    val v1 = this.asVector3
    val v2 = other.asVector3
    return v1.cross(v2)
}


// UTILITIES ============================================================================

fun List<Vector2>.makeBufferVector2(): Buffer {
    val fa = flatMap { it.toList }.toFloatArray()
    val sizeOfFloat = 4
    return ByteBuffer.allocateDirect(fa.size * sizeOfFloat).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fa).flip()
}

fun List<Vector3>.makeBufferVector3(): Buffer {
    val fa = flatMap { it.toList }.toFloatArray()
    val sizeOfFloat = 4
    return ByteBuffer.allocateDirect(fa.size * sizeOfFloat).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fa).flip()
}
fun List<Vector4>.makeBufferVector4(): Buffer {
    val fa = flatMap { it.toList }.toFloatArray()
    val sizeOfFloat = 4
    return ByteBuffer.allocateDirect(fa.size * sizeOfFloat).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fa).flip()
}
