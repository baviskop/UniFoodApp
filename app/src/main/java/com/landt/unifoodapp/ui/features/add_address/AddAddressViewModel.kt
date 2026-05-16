package com.landt.unifoodapp.ui.features.add_address

import android.location.Address
import androidx.lifecycle.ViewModel
import com.landt.unifoodapp.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.landt.unifoodapp.data.models.ReverseGeoCodeRequest
import com.landt.unifoodapp.data.remote.ApiResponse
import com.landt.unifoodapp.data.remote.safeApiCall
import com.landt.unifoodapp.location.LocationManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    val foodApi: FoodApi,
    private val locationManager: LocationManager,
    ) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent>()
    val event = _event.asSharedFlow()

    private val _address = MutableStateFlow<com.landt.unifoodapp.data.models.Address?>(null)
    val address = _address.asStateFlow()
    fun getLocation() = locationManager.getLocation()

    fun reverseGeocode(lat: Double, lon: Double) {
        // Reverse geocode the lat and lon to get the address
        viewModelScope.launch {
            _address.value = null
            val address = safeApiCall { foodApi.reverseGeocode(ReverseGeoCodeRequest(lat, lon)) }
            when (address) {
                is ApiResponse.Success -> {
                    _address.value = address.data
                    _uiState.value = AddAddressState.Success
                }

                else -> {
                    _address.value = null
                    _uiState.value = AddAddressState.Error("Failed to reverse geocode")
                }
            }
        }
    }

    fun onAddAddressClicked() {
        viewModelScope.launch {
            _uiState.value = AddAddressState.AddressStoring
            val result = safeApiCall { foodApi.storeAddress(address.value!!) }
            when (result) {
                is ApiResponse.Success -> {
                    _uiState.value = AddAddressState.Success
                    _event.emit(AddAddressEvent.NavigateToAddressList)
                }

                else -> {
                    _uiState.value = AddAddressState.Error("Failed to store address")
                }
            }
        }
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