package br.com.victorwads.parkingshare.data.models

import androidx.compose.ui.geometry.Offset
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

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
}