package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.apisRequest.RuteType
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.DatabaseService
import com.project.atlas.services.RuteService
import kotlinx.coroutines.launch

class RuteViewModel: ViewModel() {
    private val _ruteState = MutableLiveData<RuteModel>()
    val ruteState: LiveData<RuteModel> = _ruteState
    private val ruteService = RuteService(DatabaseService())

    fun createRute(start: Location, end: Location, vehicle: VehicleModel, ruteType: RuteType){
        viewModelScope.launch {
            _ruteState.value = ruteService.createRute(start,end,vehicle,ruteType)
        }
    }
}