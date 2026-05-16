package com.landt.unifoodapp.ui.features.add_address

import android.location.Address
import androidx.lifecycle.ViewModel
import com.landt.unifoodapp.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val uiState = _uiState.asStateFlow()

    fun reverseGeocode(lat: Double, lon: Double) {
        // Reverse geocode the lat and lon to get the address


    }

    fun addAddress(address: Address) {

    }

    sealed class AddAddressEvent {
        object NavigateToAddressList : AddAddressEvent()
    }

    sealed class AddAddressState {
        object Loading : AddAddressState()
        object Success : AddAddressState()
        data class Error(val message: String) : AddAddressState()
    }
}