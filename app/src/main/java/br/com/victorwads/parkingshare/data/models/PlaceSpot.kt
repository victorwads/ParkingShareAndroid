package br.com.victorwads.parkingshare.data.models

import androidx.compose.ui.geometry.Offset
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class PlaceSpot(
    @DocumentId val id: String,
    @PropertyName("floor") val floor: String = "T",
    @PropertyName("position") var position: Position = Position(0f, 0f),
    @PropertyName("size") var size: Size = Size(100f, 200f),
    @PropertyName("owners") val owners: List<String> = listOf(),
) {
    constructor() : this("") //Needed for Firebase

    data class Size(
        @PropertyName("width") val width: Float, @PropertyName("height") val height: Float
    ) {
        constructor() : this(0f, 0f) //Needed for Firebase
    }

    data class Position(@PropertyName("x") var x: Float, @PropertyName("y") var y: Float) {

        constructor() : this(0f, 0f) //Needed for Firebase

        operator fun plusAssign(offset: Offset) {
            x += offset.x
            y += offset.y
        }

        operator fun plus(offset: Offset): Position {
            return Position(x + offset.x, y + offset.y)
        }
    }
}