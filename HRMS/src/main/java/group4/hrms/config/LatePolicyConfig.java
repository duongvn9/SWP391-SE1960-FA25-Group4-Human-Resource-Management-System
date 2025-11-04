package group4.hrms.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration class for late policy settings
 * Supports both ladder and rounding modes for calculating lateness deductions
 */
public class LatePolicyConfig {

    @JsonProperty("mode")
    private String mode; // "ladder" or "rounding"

    @JsonProperty("grace_minutes_per_event")
    private int graceMinutesPerEvent;

    @JsonProperty("ladder_bands")
    private List<LadderBand> ladderBands;

    @JsonProperty("rounding_unit_minutes")
    private Integer roundingUnitMinutes; // For rounding mode

    // Default constructor
    public LatePolicyConfig() {}

    // Getters and setters
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getGraceMinutesPerEvent() {
        return graceMinutesPerEvent;
    }

    public void setGraceMinutesPerEvent(int graceMinutesPerEvent) {
        this.graceMinutesPerEvent = graceMinutesPerEvent;
    }

    public List<LadderBand> getLadderBands() {
        return ladderBands;
    }

    public void setLadderBands(List<LadderBand> ladderBands) {
        this.ladderBands = ladderBands;
    }

    public Integer getRoundingUnitMinutes() {
        return roundingUnitMinutes;
    }

    public void setRoundingUnitMinutes(Integer roundingUnitMinutes) {
        this.roundingUnitMinutes = roundingUnitMinutes;
    }

    /**
     * Validate configuration integrity
     *
     * @return true if configuration is valid, false otherwise
     */
    public boolean isValid() {
        if (mode == null || (!mode.equals("ladder") && !mode.equals("rounding"))) {
            return false;
        }

        if (graceMinutesPerEvent < 0) {
            return false;
        }

        if ("ladder".equals(mode)) {
            return ladderBands != null && !ladderBands.isEmpty() &&
                   ladderBands.stream().allMatch(LadderBand::isValid);
        }

        if ("rounding".equals(mode)) {
            return roundingUnitMinutes != null && roundingUnitMinutes > 0;
        }

        return false;
    }

    /**
     * Inner class representing a ladder band for late policy
     */
    public static class LadderBand {
        @JsonProperty("min")
        private int min;

        @JsonProperty("max")
        private int max;

        @JsonProperty("unpaid_hours")
        private Double unpaidHours;

        @JsonProperty("formula")
        private String formula; // For dynamic calculation like "ceil(minutes/60)"

        // Default constructor
        public LadderBand() {}

        // Constructor
        public LadderBand(int min, int max, Double unpaidHours, String formula) {
            this.min = min;
            this.max = max;
            this.unpaidHours = unpaidHours;
            this.formula = formula;
        }

        // Getters and setters
        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public Double getUnpaidHours() {
            return unpaidHours;
        }

        public void setUnpaidHours(Double unpaidHours) {
            this.unpaidHours = unpaidHours;
        }

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        /**
         * Check if this band applies to the given minutes
         *
         * @param minutes Late minutes to check
         * @return true if this band applies
         */
        public boolean applies(int minutes) {
            return minutes >= min && minutes <= max;
        }

        /**
         * Calculate unpaid hours for given minutes using this band
         *
         * @param minutes Late minutes
         * @return Unpaid hours
         */
        public double calculateUnpaidHours(int minutes) {
            if (unpaidHours != null) {
                return unpaidHours;
            }

            if ("ceil(minutes/60)".equals(formula)) {
                return Math.ceil(minutes / 60.0);
            }

            // Default fallback
            return 0.0;
        }

        /**
         * Validate band configuration
         *
         * @return true if valid
         */
        public boolean isValid() {
            return min >= 0 && max >= min &&
                   (unpaidHours != null || formula != null);
        }
    }
}