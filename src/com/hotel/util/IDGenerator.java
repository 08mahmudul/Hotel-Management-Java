package com.hotel.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IDGenerator {

    public static String generate(String prefix, String filePath) {
        String lastId = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String current;
            reader.readLine(); // skip header
            while ((current = reader.readLine()) != null) {
                if (!current.trim().isEmpty()) {
                    lastId = current.split(",")[0].trim();
                }
            }
        } catch (IOException e) {
            // file doesn't exist yet — start from 001
            return prefix + "-001";
        }

        if (lastId == null) {
            return prefix + "-001";
        }

        String[] parts = lastId.split("-");
        int number = Integer.parseInt(parts[parts.length - 1]);
        return prefix + "-" + String.format("%03d", number + 1);
    }
}
