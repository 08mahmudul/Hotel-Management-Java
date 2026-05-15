package com.hotel.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.hotel.exception.FileReadException;
import com.hotel.exception.FileWriteException;

public class FileHandler {

    public static List<String[]> readAll(String filePath) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    rows.add(line.split(","));
                }
            }
        } catch (IOException e) {
            throw new FileReadException("Could not read: " + filePath);
        }
        return rows;
    }

    public static void writeAll(String filePath, String header, List<String> rows) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(header);
            writer.newLine();
            for (String row : rows) {
                writer.write(row);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileWriteException("Could not write: " + filePath);
        }
    }

    public static void ensureFileExists(String filePath, String header) {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                writer.newLine();
            } catch (IOException e) {
                throw new FileWriteException("Could not create: " + filePath);
            }
        }
    }
}
