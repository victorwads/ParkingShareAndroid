package br.com.victorwads.parkingshare

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> CoroutineScope.animateToTarget(
    from: T,
    target: T,
    duration: Long = 250,
    fps: Int = 120,
    onChange: (T) -> Unit,
) = launch {
    if (from == target) return@launch
    val steps: Int = (duration / 1000f * fps.toFloat()).toInt()
    val delayTime: Long = duration / steps
    val stepsValue: T = (target - from) / (steps.toFloat())

    for (i in 1..steps) {
        onChange(from + stepsValue * i)
        delay(delayTime)
    }
    onChange(target)
}

private operator fun <T> T.times(i: Int): T = when (this) {
    is Float -> (this * i.toFloat()) as T
    is Int -> (this * i) as T
    is Double -> (this * i.toDouble()) as T
    is Offset -> Offset(
        this.x * i.toFloat(),
        this.y * i.toFloat()
    ) as T

    else -> throw Exception("Not supported")
}

private operator fun <T> T.plus(any: T): T = when {
    this is Float && any is Float -> (this + any) as T
    this is Int && any is Int -> (this + any) as T
    this is Double && any is Double -> (this + any) as T
    this is Offset && any is Offset -> (this + any) as T
    else -> throw Exception("Not supported")
}

private operator fun <T> T.div(steps: Float): T = when {
    this is Float -> (this / steps) as T
    this is Int -> (this.toFloat() / steps) as T
    this is Double -> (this / steps.toDouble()) as T
    this is Offset -> Offset(
        this.x / steps,
        this.y / steps
    ) as T

    else -> throw Exception("Not supported")
}

private operator fun <T> T.minus(other: T): T = when {
    this is Float && other is Float -> (this - other) as T
    this is Int && other is Int -> (this - other) as T
    this is Double && other is Double -> (this - other) as T
    this is Offset && other is Offset -> (this - other) as T
    else -> throw Exception("Not supported")
}
