package com.project.atlas.services

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.project.atlas.MainActivity
import com.project.atlas.models.VehicleModel

object LocalDefaultVehicleService {
    private const val PREF_NAME = "DefaultVehiclePreferences"
    private const val KEY_DEFAULT_VEHICLE = "localDefaultVehicle"

    // Guardar el vehículo predeterminado en SharedPreferences
    fun saveDefaultVehicle(vehicle: VehicleModel) {
        val sharedPreferences = MainActivity.appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val vehicleJson = Gson().toJson(vehicle)
        Log.d("VehicleModel", "Saving Vehicle: $vehicleJson")
        editor.putString(KEY_DEFAULT_VEHICLE, vehicleJson)
        editor.apply()
    }

    // Obtener el vehículo predeterminado desde SharedPreferences
    fun getDefaultVehicle(): VehicleModel? {
        val sharedPreferences = MainActivity.appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val vehicleJson = sharedPreferences.getString(KEY_DEFAULT_VEHICLE, null)

        return if (vehicleJson != null) {
            Gson().fromJson(vehicleJson, VehicleModel::class.java) // Deserializa el JSON
        } else {
            null
        }
    }

    // Eliminar el vehículo predeterminado
    fun removeDefaultVehicle() {
        val sharedPreferences = MainActivity.appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_DEFAULT_VEHICLE) // Elimina la clave del vehículo predeterminado
        editor.apply()
    }
}