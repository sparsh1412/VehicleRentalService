package com.navi.vehiclerental.models;

import java.util.Map;
import java.util.Set;

import com.navi.vehiclerental.enums.VehicleType;
import com.navi.vehiclerental.selectionstrategy.VehicleSelectionStrategy;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
@Getter
@Builder
public class Branch {
    @NonNull
    private String branchId;
    @NonNull
    private VehicleSelectionStrategy vehicleSelectionStrategy;
    @NonNull
    private Set<VehicleType> supportedVehicles;
    @NonNull
    private Map<VehicleType, Map<String, Vehicle>> vehicleInventory;
}
