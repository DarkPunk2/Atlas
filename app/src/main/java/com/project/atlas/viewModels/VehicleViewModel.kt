package com.project.atlas.viewModels

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.Location
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.AuthService
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class VehicleViewModel : ViewModel() {
    private val service: VehicleInterface = VehicleService(VehicleDatabaseService())

    // LiveData que se observa desde la UI
    private val _vehicleList = MutableLiveData<List<VehicleModel>>()
    val vehicleList: LiveData<List<VehicleModel>> = _vehicleList

    private val _defaultVehicle = MutableLiveData<VehicleModel?>()
    val defaultVehicle: LiveData<VehicleModel?> = _defaultVehicle

    fun refreshVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = service.listVehicle(UserModel.eMail) ?: emptyList()

            // Ordenar la lista
            val sortedList = result.sortedWith(compareBy {
                when (it.type) { // Aquí se establece el orden personalizado
                    VehicleType.Walk -> 0  // Walk va primero
                    VehicleType.Cycle -> 1 // Cycle va segundo
                    else -> when(it.favourite){ // El resto de vehículos va después
                        true ->2
                        else ->3
                    }
                }
            })
            _vehicleList.postValue(sortedList)
        }
    }

    fun refreshDefaultVehicle(){
        var default: VehicleModel? = null
        viewModelScope.launch(Dispatchers.IO) {
            default = service.getDefaultVehicle(UserModel.eMail)
            _defaultVehicle.postValue(default)
        }
    }

    fun setDefaultVehicle(vehicle:VehicleModel){
        viewModelScope.launch(Dispatchers.IO) {
            service.setDefaultVehicle(UserModel.eMail,vehicle)
            refreshDefaultVehicle()
        }
    }
    fun deleteDefaultVehicle(){
        viewModelScope.launch(Dispatchers.IO) {
            service.deleteDefaultVehicle(UserModel.eMail)
            refreshDefaultVehicle()
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

    // Actualiza un vehículo existente
    fun update(oldAlias:String, vehicle: VehicleModel) {
        viewModelScope.launch(Dispatchers.IO) {
            service.updateVehicle(UserModel.eMail, oldAlias,vehicle)
            refreshVehicles()
        }
    }


    // Método para obtener la lista de vehículos observables
    fun listVehicles(): LiveData<List<VehicleModel>> = vehicleList
}
