package com.ceres.blip.services;

import com.ceres.blip.utils.LocalUtilsService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class LicenseGenerationService extends LocalUtilsService {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int KEY_LENGTH = 25;
    private static final int SEGMENT_LENGTH = 5;
    private static final String SEPARATOR = "-";

    /**
     * Generate a random license key
     */
    public String generateRandomKey() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < KEY_LENGTH; i++) {
            if (i > 0 && i % SEGMENT_LENGTH == 0) {
                key.append(SEPARATOR);
            }
            int index = random.nextInt(CHARACTERS.length());
            key.append(CHARACTERS.charAt(index));
        }

        return key.toString();
    }

    /**
     * Generate a license key with checksum
     */
    public String generateKeyWithChecksum(String productCode) {
        SecureRandom random = new SecureRandom();
        StringBuilder baseKey = new StringBuilder();

        // Add product code prefix
        baseKey.append(productCode).append("-");

        // Generate random part
        for (int i = 0; i < 16; i++) {
            int index = random.nextInt(CHARACTERS.length());
            baseKey.append(CHARACTERS.charAt(index));
        }

        String key = baseKey.toString();
        // Add checksum
        String checksum = calculateChecksum(key);
        return key + "-" + checksum;
    }

    /**
     * Generate multiple license keys
     */
    public List<String> generateKeys(int count, String productCode) {
        Set<String> keys = new HashSet<>();
        while (keys.size() < count) {
            String key = generateKeyWithChecksum(productCode);
            keys.add(key);
        }
        return new ArrayList<>(keys);
    }

    /**
     * Simple checksum calculation
     */
    private String calculateChecksum(String input) {
        int sum = 0;
        for (char c : input.toCharArray()) {
            sum += (int) c;
        }
        // Take the last 4 characters of hex representation
        String hex = Integer.toHexString(sum).toUpperCase();
        return hex.length() > 4 ? hex.substring(hex.length() - 4) : hex;
    }

    /**
     * Validate checksum
     */
    public boolean validateChecksum(String licenseKey) {
        if (licenseKey == null || !licenseKey.contains("-")) {
            return false;
        }

        String[] parts = licenseKey.split("-");
        if (parts.length < 2) {
            return false;
        }

        String keyWithoutChecksum = String.join("-",
                Arrays.copyOf(parts, parts.length - 1));
        String providedChecksum = parts[parts.length - 1];

        String calculatedChecksum = calculateChecksum(keyWithoutChecksum);
        return calculatedChecksum.equals(providedChecksum);
    }
}
