package com.navi.vehiclerental.selectionstrategy;

import java.util.List;

import com.navi.vehiclerental.models.Vehicle;

import lombok.NonNull;

public interface VehicleSelectionStrategy {
    void sortVehicleListBySelectionStrategy(@NonNull final List<Vehicle> vehicleList);
}
