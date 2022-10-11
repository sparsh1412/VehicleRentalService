package com.navi.vehiclerental.selectionstrategy;

import java.util.Collections;
import java.util.List;

import com.navi.vehiclerental.models.Vehicle;

import lombok.NonNull;

public class SortedByPriceStrategy implements VehicleSelectionStrategy {

    @Override
    public void sortVehicleListBySelectionStrategy(final @NonNull List<Vehicle> vehicleList) {
        Collections.sort(vehicleList, (vehicle1, vehicle2) -> {
            if (vehicle1.getPrice().getValue() <= vehicle2.getPrice().getValue()) {
                return -1;
            }
            return 1;
        });
    }
}
