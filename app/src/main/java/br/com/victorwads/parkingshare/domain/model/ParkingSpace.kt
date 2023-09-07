package br.com.victorwads.parkingshare.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.util.UUID

data class ParkingSpace(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    var position: Offset = Offset(0f, 0f),
    val size: Size = Size(100f, 200f),
)