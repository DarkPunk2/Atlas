package com.project.atlas.ViewModels

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Interfaces.VehicleInterface
import com.project.atlas.Models.Location
import com.project.atlas.Models.UserModel
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Services.AuthService
import com.project.atlas.Services.VehicleDatabaseService
import com.project.atlas.Services.VehicleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class VehicleViewModel : ViewModel() {
    private val service: VehicleInterface = VehicleService(VehicleDatabaseService())

    // LiveData que se observa desde la UI
    private val _vehicleList = MutableLiveData<List<VehicleModel>>()
    val vehicleList: LiveData<List<VehicleModel>> = _vehicleList

    // Obtiene los vehículos desde la base de datos y actualiza la lista
    fun refreshVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = service.listVehicle(UserModel.eMail) ?: emptyList()
            _vehicleList.postValue(result) // Actualiza LiveData en el hilo principal
        }
    }

    // Añade un nuevo vehículo y refresca la lista
    fun add(vehicle: VehicleModel) {
        viewModelScope.launch(Dispatchers.IO) {
            service.addVehicle(UserModel.eMail, vehicle)
            refreshVehicles() // Actualiza la lista después de añadir
        }
    }

    // Elimina un vehículo y refresca la lista
    fun delete(vehicle: VehicleModel) {
        viewModelScope.launch(Dispatchers.IO) {
            service.deleteVehicle(UserModel.eMail, vehicle.alias!!)
            refreshVehicles() // Actualiza la lista después de eliminar
        }
    }

    // Actualiza un vehículo existente (puedes implementar esto según tus necesidades)
    fun update(oldAlias:String, vehicle: VehicleModel) {
        viewModelScope.launch(Dispatchers.IO) {
            service.updateVehicle(UserModel.eMail, oldAlias,vehicle)
            refreshVehicles()
        }
    }

    // Método para obtener la lista de vehículos observables
    fun listVehicles(): LiveData<List<VehicleModel>> = vehicleList
}
