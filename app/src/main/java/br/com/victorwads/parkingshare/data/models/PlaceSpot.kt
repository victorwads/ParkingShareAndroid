package br.com.victorwads.parkingshare.data.models

import androidx.compose.ui.geometry.Offset
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlin.math.abs

data class PlaceSpot(
    @JvmField @DocumentId
    var id: String = "",
    @JvmField @PropertyName("floor")
    var floor: String = "T",
    @JvmField @PropertyName("position")
    var position: Position = Position(),
    @JvmField @PropertyName("size")
    var size: Size = Size(),
    @JvmField @PropertyName("owners")
    var owners: List<String> = listOf(),
) {
    data class Size(
        @JvmField @PropertyName("w")
        var width: Float = 100f,
        @JvmField @PropertyName("h")
        var height: Float = 200f
    )

    data class Position(
        @JvmField @PropertyName("x")
        var x: Float = 0f,
        @JvmField @PropertyName("y")
        var y: Float = 0f
    ) {
        operator fun plusAssign(offset: Offset) {
            x += offset.x
            y += offset.y
        }

        operator fun plus(offset: Offset): Position {
            return Position(x + offset.x, y + offset.y)
        }

        fun toOffset() = Offset(x, y)
    }

    @get:Exclude
    val left: Float get() = position.x

    @get:Exclude
    val top: Float get() = position.y

    @get:Exclude
    val right: Float get() = position.x + size.width

    @get:Exclude
    val bottom: Float get() = position.y + size.height

    @get:Exclude
    val centerX: Float get() = position.x + (size.width / 2)

    @get:Exclude
    val centerY: Float get() = position.y + (size.height / 2)

    @Exclude
    fun isInside(ofSpot: List<PlaceSpot>): PlaceSpot? {
        for (spot in ofSpot) if (isInside(spot)) return spot
        return null
    }

    @Exclude
    fun isInside(ofSpot: PlaceSpot): Boolean {
        if (id == ofSpot.id) return false
        val horizontalOverlap = (right > ofSpot.left) && (ofSpot.right > left)
        val verticalOverlap = (bottom > ofSpot.top) && (ofSpot.bottom > top)
        return horizontalOverlap && verticalOverlap
    }

    fun fixPosition(hitSpot: PlaceSpot) {
        if (hitSpot.id == id) return
        processPosition(hitSpot)?.let {
            alignWith(hitSpot, it)
        }
    }

    @Exclude
    fun processPosition(fromSpot: PlaceSpot): Alignment? {
        if (fromSpot.id == id) return null
        if (isInside(fromSpot)) {
            val deltaX = fromSpot.centerX - centerX
            val deltaY = fromSpot.centerY - centerY

            return if (abs(deltaX) > abs(deltaY)) {
                if (centerX > fromSpot.centerX) Alignment.RIGHT
                else Alignment.LEFT
            } else {
                if (centerY > fromSpot.centerY) Alignment.BOTTOM
                else Alignment.TOP
            }
        }
        return null
    }

    @Exclude
    fun alignWith(square2: PlaceSpot, alignment: Alignment) {
        if (square2.id == id) return
        position = when (alignment) {
            Alignment.LEFT -> Position(square2.left - size.width, square2.top)
            Alignment.RIGHT -> Position(square2.left + square2.size.width, square2.top)
            Alignment.TOP -> Position(square2.left, square2.top - size.height)
            Alignment.BOTTOM -> Position(square2.left, square2.top + square2.size.height)
        }
    }

    enum class Alignment {
        LEFT, RIGHT, TOP, BOTTOM
    }
}
