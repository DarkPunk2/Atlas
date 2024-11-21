package com.project.atlas.Services;

import androidx.annotation.NonNull;

import com.project.atlas.Exceptions.vehicleWrongBusinessRulesException;
import com.project.atlas.Interfaces.VehicleInterface;
import com.project.atlas.Models.VehicleModel;

public class VehicleService implements VehicleInterface {
    private VehicleDatabaseService dbService;
    public VehicleService(VehicleDatabaseService dbService){
        this.dbService = dbService;
    }

    @Override
    public boolean addVehicle(@NonNull String user, @NonNull VehicleModel vehicle){
        return dbService.addVehicle(user,vehicle);
    }

    @Override
    public boolean checkEntry(@NonNull String user, @NonNull VehicleModel vehicle) {
        return dbService.checkEntry(user, vehicle);
    }

    public boolean checkBusinessRules(VehicleModel vehicle) throws vehicleWrongBusinessRulesException {
        throw new vehicleWrongBusinessRulesException("Vehículo no válido");
    }
}