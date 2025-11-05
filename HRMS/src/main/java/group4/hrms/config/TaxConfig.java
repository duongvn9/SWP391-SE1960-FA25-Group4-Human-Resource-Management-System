package group4.hrms.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class for tax calculation settings
 * Currently supports flat tax rate mode
 */
public class TaxConfig {

    @JsonProperty("enable_tax")
    private boolean enableTax;

    @JsonProperty("mode")
    private String mode; // Currently only "flat" is supported

    @JsonProperty("flat_rate")
    private double flatRate; // Tax rate as decimal (e.g., 0.05 for 5%)

    // Default constructor
    public TaxConfig() {}

    // Constructor
    public TaxConfig(boolean enableTax, String mode, double flatRate) {
        this.enableTax = enableTax;
        this.mode = mode;
        this.flatRate = flatRate;
    }

    // Getters and setters
    public boolean isEnableTax() {
        return enableTax;
    }

    public void setEnableTax(boolean enableTax) {
        this.enableTax = enableTax;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public double getFlatRate() {
        return flatRate;
    }

    public void setFlatRate(double flatRate) {
        this.flatRate = flatRate;
    }

    /**
     * Validate configuration integrity
     *
     * @return true if configuration is valid, false otherwise
     */
    public boolean isValid() {
        // If tax is disabled, configuration is valid regardless of other fields
        if (!enableTax) {
            return true;
        }

        // If tax is enabled, mode must be "flat" and rate must be valid
        if (!"flat".equals(mode)) {
            return false;
        }

        // Flat rate should be between 0 and 1 (0% to 100%)
        return flatRate >= 0.0 && flatRate <= 1.0;
    }

    @Override
    public String toString() {
        return String.format("TaxConfig{enableTax=%s, mode='%s', flatRate=%.4f}",
                           enableTax, mode, flatRate);
    }
}