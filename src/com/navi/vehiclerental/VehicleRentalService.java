package com.navi.vehiclerental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.navi.vehiclerental.constants.VehicleRentalServiceConstants;
import com.navi.vehiclerental.enums.Currency;
import com.navi.vehiclerental.enums.VehicleType;
import com.navi.vehiclerental.models.Availability;
import com.navi.vehiclerental.models.Branch;
import com.navi.vehiclerental.models.Price;
import com.navi.vehiclerental.models.Vehicle;
import com.navi.vehiclerental.selectionstrategy.SortedByPriceStrategy;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
@Getter
@Builder
public class VehicleRentalService {

    @NonNull
    private Map<String, Branch> branchMap;

    public boolean addBranch(@NonNull final String branchId, @NonNull final String supportedVehicles) {
        if (branchMap.containsKey(branchId)) {
            // Ideally throw an exception. But just catering to the question requirements.
            return false;
        }

        // Different branches can have different vehicle selection strategies.
        // For now we are using SortedByPriceStrategy (as mentioned in the question).
        final Branch newBranch =
                Branch.builder().branchId(branchId).vehicleSelectionStrategy(new SortedByPriceStrategy())
                        .supportedVehicles(getSupportedVehicleSet(supportedVehicles)).vehicleInventory(new HashMap<>())
                        .build();
        branchMap.put(branchId, newBranch);
        return true;
    }

    public boolean addVehicleToBranch(@NonNull final String branchId, @NonNull final String vehicleTypeInString,
            @NonNull final String vehicleId, final double price) {
        // If branch with given branchId does't exist then return false.
        if (!branchMap.containsKey(branchId)) {
            // Ideally throw an exception. But just catering to the question requirements.
            return false;
        }

        final Branch branch = branchMap.get(branchId);
        final VehicleType vehicleType = VehicleType.valueOf(vehicleTypeInString);
        final Map<VehicleType, Map<String, Vehicle>> vehicleInventory = branch.getVehicleInventory();

        // If the branch doesn't support the vehicles of the given vehicleType, return false.
        if (!branch.getSupportedVehicles().contains(vehicleType)) {
            return false;
        }

        // If a vehicle with the given vehicleId and branchId already exists the return false.
        // It is not the job of this API to modify a vehicle. There should be a separate API for vehicle modification.
        if (vehicleInventory.containsKey(vehicleType) && vehicleInventory.get(vehicleType).containsKey(vehicleId)) {
            return false;
        }

        vehicleInventory.putIfAbsent(vehicleType, new HashMap<>());
        final Map<String, Vehicle> vehicleIdToVehicleMap = vehicleInventory.get(vehicleType);
        final Vehicle newVehicle = getNewVehicle(vehicleType, price, vehicleId);
        vehicleIdToVehicleMap.put(vehicleId, newVehicle);
        return true;
    }

    public double bookVehicle(@NonNull final String branchId, @NonNull final String vehicleTypeInString, final int startTime,
            final int endTime) {

        final VehicleType vehicleType = VehicleType.valueOf(vehicleTypeInString);

        // return -1 if any of the following scenario happens:
        // 1. The start/end time configuration is not valid.
        // 2. Branch with given branchId does't exist.
        // 3. A vehicle of given vehicleType doesn't exist in the branch.
        if (!isStartAndEndTimeValid(startTime, endTime) || !branchMap.containsKey(branchId) || !branchMap.get(branchId)
                .getVehicleInventory().containsKey(vehicleType)) {
            // I would throw an exception in this case. But just catering to the question requirements.
            return -1;
        }

        final Optional<Vehicle> bestAvailableVehicleOptional =
                getBestAvailableVehicleOptional(branchId, vehicleType, startTime, endTime);

        if (bestAvailableVehicleOptional.isPresent()) {
            final Vehicle bestAvailableVehicle = bestAvailableVehicleOptional.get();
            bookVehicleForGivenSlot(bestAvailableVehicle, startTime, endTime);
            final double priceToPay =
                    bestAvailableVehicle.getPrice().getValue() * (endTime - startTime);
            return priceToPay;
        }
        return -1;
    }

    public void displayAvailableVehiclesInBranchForGivenTimeSlot(@NonNull final String branchId, final int startTime,
            final int endTime) {
        if (!branchMap.containsKey(branchId)) {
            // Ideally throw an exception. But just catering to the question requirements.
            return;
        }
        final List<Vehicle> vehicleListForBranch = getVehicleListForBranch(branchId);
        branchMap.get(branchId).getVehicleSelectionStrategy().sortVehicleListBySelectionStrategy(vehicleListForBranch);
        final String avaliableVehicles = vehicleListForBranch.stream()
                .filter(vehicle -> isVehicleAvailableInGivenSlot(vehicle, startTime, endTime))
                .map(vehicle -> vehicle.getVehicleId())
                .collect(Collectors.joining(VehicleRentalServiceConstants.COMMA));
        if (avaliableVehicles != null) {
            System.out.println(avaliableVehicles);
        }
    }

    private Set<VehicleType> getSupportedVehicleSet(final String supportedVehicles) {
        return Arrays.stream(supportedVehicles.split(VehicleRentalServiceConstants.COMMA))
                .map(vehicle -> VehicleType.valueOf(vehicle)).collect(Collectors.toSet());
    }

    private Vehicle getNewVehicle(final VehicleType vehicleType, final double price, final String vehicleId) {
        final Price vehiclePrice = Price.builder().currency(Currency.INR).value(price).build();
        final Availability vehicleAvailability = Availability.builder()
                .bookedSlots(new boolean[VehicleRentalServiceConstants.VEHICLE_AVAILABILITY_SLOT_SIZE]).build();
        return Vehicle.builder().vehicleId(vehicleId).vehicleType(vehicleType).price(vehiclePrice)
                .availability(vehicleAvailability).build();
    }

    private boolean isStartAndEndTimeValid(final int startTime, final int endTime) {
        return startTime >= VehicleRentalServiceConstants.MINIMUM_VALID_START_TIME
                && startTime <= VehicleRentalServiceConstants.MAXIMUM_VALID_START_TIME
                && endTime >= VehicleRentalServiceConstants.MINIMUM_VALID_END_TIME
                && endTime <= VehicleRentalServiceConstants.MAXIMUM_VALID_END_TIME
                && startTime < endTime;
    }

    private Optional<Vehicle> getBestAvailableVehicleOptional(final String branchId, final VehicleType vehicleType,
            final int startTime, final int endTime) {
        final Branch branch = branchMap.get(branchId);
        final List<Vehicle> vehicleList =
                branch.getVehicleInventory().get(vehicleType).values().stream().collect(Collectors.toList());
        branch.getVehicleSelectionStrategy().sortVehicleListBySelectionStrategy(vehicleList);
        final Optional<Vehicle> bestAvailableVehicleOptional =
                vehicleList.stream().filter(vehicle -> isVehicleAvailableInGivenSlot(vehicle, startTime, endTime))
                        .findFirst();
        return bestAvailableVehicleOptional;
    }

    private boolean isVehicleAvailableInGivenSlot(final Vehicle vehicle, final int startTime, final int endTime) {
        final boolean[] bookedSlots = vehicle.getAvailability().getBookedSlots();
        for (int time = startTime - 1; time <= endTime - 2; time++) {
            if (bookedSlots[time]) {
                return false;
            }
        }
        return true;
    }

    private void bookVehicleForGivenSlot(final Vehicle vehicle, final int startTime, final int endTime) {
        final boolean[] bookedSlots = vehicle.getAvailability().getBookedSlots();
        for (int time = startTime - 1; time <= endTime - 2; time++) {
            bookedSlots[time] = true;
        }
    }

    private List<Vehicle> getVehicleListForBranch(final String branchId) {
        final List<Vehicle> vehicleListForBranch = new ArrayList<>();
        final Map<VehicleType, Map<String, Vehicle>> vehicleInventory = branchMap.get(branchId).getVehicleInventory();
        for (final VehicleType vehicleType : vehicleInventory.keySet()) {
            final Map<String, Vehicle> vehicleMap = vehicleInventory.get(vehicleType);
            for (final String vehicleId : vehicleMap.keySet()) {
                vehicleListForBranch.add(vehicleMap.get(vehicleId));
            }
        }
        return vehicleListForBranch;
    }
}
