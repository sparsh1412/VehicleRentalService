package com.navi.vehiclerental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import com.navi.vehiclerental.utils.VehicleRentalServiceUtils;

public class Driver {
    public static void main(String[] args) throws IOException {

        /**
         * TODO:
         * 1. Implement Dynamic Pricing
         * 2. Write UTs
         */

        // Initializing Vehicle Rental Service
        System.out.println("Initializing Vehicle Rental Service...");
        final VehicleRentalService vehicleRentalService =
                VehicleRentalService.builder().branchMap(new HashMap<>()).build();
        System.out.println("Initialization complete!");

        // Taking file path as Input
        System.out.println("Please input the file path from which the input has to fetched.");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String filePath = reader.readLine();
        System.out.printf("Received %s as the file path", filePath);
        System.out.println();

        // Printing output
        final List<List<String>> formattedInputList = VehicleRentalServiceUtils.getFormattedInputFromFile(filePath);
        for (final List<String> formattedInputLine : formattedInputList) {
            callApiRelevantToInputLine(vehicleRentalService, formattedInputLine);
        }
    }

    private static void callApiRelevantToInputLine(final VehicleRentalService vehicleRentalService,
            final List<String> inputLine) {
        switch (inputLine.get(0)) {
            case "ADD_BRANCH":
                System.out.println(vehicleRentalService.addBranch(inputLine.get(1), inputLine.get(2)));
                break;
            case "ADD_VEHICLE":
                System.out.println(vehicleRentalService
                        .addVehicleToBranch(inputLine.get(1), inputLine.get(2), inputLine.get(3),
                                Double.parseDouble(inputLine.get(4))));
                break;
            case "BOOK":
                System.out.println(vehicleRentalService.bookVehicle(inputLine.get(1), inputLine.get(2),
                        Integer.parseInt(inputLine.get(3)), Integer.parseInt(inputLine.get(4))));
                break;
            case "DISPLAY_VEHICLES":
                vehicleRentalService.displayAvailableVehiclesInBranchForGivenTimeSlot(inputLine.get(1),
                        Integer.parseInt(inputLine.get(2)), Integer.parseInt(inputLine.get(3)));
                break;
            default:
                // Ideally throw an exception.
        }
    }
}
