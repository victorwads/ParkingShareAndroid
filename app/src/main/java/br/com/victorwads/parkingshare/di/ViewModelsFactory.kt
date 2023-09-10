@file:Suppress("UNCHECKED_CAST")

package br.com.victorwads.parkingshare.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.victorwads.parkingshare.data.ParkingSpotsFirebaseRepository
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel

class ViewModelsFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ParkingEditViewModel::class.java) -> {
                return ParkingEditViewModel(
                    ParkingSpotsFirebaseRepository()
                ) as T
            }

            else -> throw IllegalArgumentException("Classe ViewModel desconhecida")
        }
    }
}
