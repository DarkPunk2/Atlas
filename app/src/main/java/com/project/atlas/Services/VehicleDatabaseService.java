package com.project.atlas.Services;

import androidx.annotation.NonNull;
import com.project.atlas.Interfaces.VehicleInterface;
import com.project.atlas.Models.VehicleModel;

public class VehicleDatabaseService implements VehicleInterface {
    @Override
    public boolean addVehicle(@NonNull String user, @NonNull VehicleModel vehicle){
        return false;
    }
    public boolean checkConnection(){
        return false;
    }
    @Override
    public boolean checkEntry(@NonNull String user, @NonNull VehicleModel vehicle) {
        return false;
    }
}