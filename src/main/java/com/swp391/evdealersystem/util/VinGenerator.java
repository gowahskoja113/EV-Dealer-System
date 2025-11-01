package com.swp391.evdealersystem.util;

import org.springframework.stereotype.Component;

@Component
public class VinGenerator {

    public String colorToLetter(String color) {
        if (color == null) return "X";
        return switch (color.trim().toLowerCase()) {
            case "red" -> "R";
            case "blue" -> "B";
            case "white" -> "W";
            case "black" -> "K"; // tránh trùng 'B'
            case "green" -> "G";
            case "yellow" -> "Y";
            case "silver" -> "S";
            case "gray", "grey" -> "E";
            default -> String.valueOf(Character.toUpperCase(color.charAt(0)));
        };
    }

    public String buildVin(int year, Long vehicleId, String colorLetter, int seqNo) {
        return "VIN" + year + vehicleId + colorLetter + String.format("%04d", seqNo);
    }
}
