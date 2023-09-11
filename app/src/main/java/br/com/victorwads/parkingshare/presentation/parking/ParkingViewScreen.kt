package br.com.victorwads.parkingshare.presentation.parking

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory
import br.com.victorwads.parkingshare.presentation.parking.components.ParkingGraph
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel

@Preview(device = Devices.PIXEL_4, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.TABLET, showSystemUi = true)
@Composable
fun ParkingViewScreen(
    viewModel: ParkingEditViewModel = viewModel(factory = PreviewViewModelsFactory())
) {
    ParkingGraph(controller = viewModel.graphController)
    LaunchedEffect(true) {
        viewModel.init()
    }
}
