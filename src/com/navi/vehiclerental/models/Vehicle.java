package com.navi.vehiclerental.models;

import com.navi.vehiclerental.enums.VehicleType;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
@Getter
@Builder
public class Vehicle {
    @NonNull
    private String vehicleId;
    @NonNull
    private VehicleType vehicleType;
    @NonNull
    private Price price;
    @NonNull
    private Availability availability;
}
