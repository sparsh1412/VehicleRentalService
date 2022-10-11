package com.navi.vehiclerental.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.navi.vehiclerental.constants.VehicleRentalServiceConstants;

import lombok.NonNull;

public class VehicleRentalServiceUtils {

    public static List<List<String>> getFormattedInputFromFile(@NonNull final String filePath) throws IOException {
        List<List<String>> formattedInputList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        try {
            formattedInputList = getFormattedInputList(bufferedReader);
        } catch (FileNotFoundException e) {
            System.out.printf("File with filePath: %s not found.", filePath);
            System.out.println();
        } finally {
            bufferedReader.close();
        }
        return formattedInputList;
    }

    private static List<List<String>> getFormattedInputList(final BufferedReader bufferedReader) throws IOException {
        final List<List<String>> formattedInputList = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (line != null) {
            final List<String> formattedInput =
                    Arrays.stream(line.split(VehicleRentalServiceConstants.SPACE)).collect(Collectors.toList());
            formattedInputList.add(formattedInput);
            line = bufferedReader.readLine();
        }
        return formattedInputList;
    }
}
