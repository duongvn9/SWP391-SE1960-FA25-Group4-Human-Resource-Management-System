package group4.hrms.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class for text normalization operations
 * Provides methods to normalize Vietnamese text to uppercase without diacritics
 */
public final class TextNormalizationUtil {

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{M}");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("[\\r\\n]+");

    // Private constructor to prevent instantiation
    private TextNormalizationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Normalizes Vietnamese text by removing diacritics and converting to uppercase
     * 
     * @param input The input text with diacritics
     * @return Text without diacritics in uppercase, or empty string if input is
     *         null/empty
     */
    public static String normalizeVietnameseText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Normalize the text to decompose characters
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);

        // Remove diacritical marks
        String withoutDiacritics = DIACRITICS_PATTERN.matcher(normalized).replaceAll("");

        // Convert to uppercase and return
        return withoutDiacritics.toUpperCase();
    }

    /**
     * Cleans and normalizes text for consistent processing
     * 
     * @param text The input text
     * @return Cleaned and normalized text
     */
    public static String cleanText(String text) {
        if (text == null) {
            return "";
        }

        return LINE_BREAK_PATTERN.matcher(
                WHITESPACE_PATTERN.matcher(text.trim()).replaceAll(" ")).replaceAll(" ");
    }

    /**
     * Normalizes date format to DD/MM/YYYY
     * 
     * @param dateStr Date string in various formats
     * @return Normalized date in DD/MM/YYYY format, or empty string if invalid
     */
    public static String normalizeDateFormat(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "";
        }

        // Convert various date separators to /
        String normalized = dateStr.trim().replaceAll("[-.]", "/");

        // Ensure DD/MM/YYYY format
        if (normalized.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] parts = normalized.split("/");
            if (parts.length == 3) {
                String day = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                return day + "/" + month + "/" + parts[2];
            }
        }

        return normalized;
    }

    /**
     * Normalizes gender information to standard format
     * 
     * @param gender Gender string
     * @return Normalized gender (NAM or NU), or empty string if unrecognized
     */
    public static String normalizeGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return "";
        }

        String normalized = normalizeVietnameseText(gender.toLowerCase());

        if (normalized.contains("NAM") || normalized.contains("MALE") || normalized.equals("M")) {
            return "NAM";
        } else if (normalized.contains("NU") || normalized.contains("FEMALE") || normalized.equals("F")) {
            return "NU";
        }

        return normalizeVietnameseText(gender);
    }

    /**
     * Validates if a string is not null and not empty after trimming
     * 
     * @param value String to validate
     * @return true if string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}