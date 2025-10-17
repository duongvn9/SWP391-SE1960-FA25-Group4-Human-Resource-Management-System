-- Migration: Add substitute day fields to holidays table
-- Purpose: Support BR-OT-02 - Distinguish between original holidays (300% OT) and substitute days (200% OT)
-- Date: 2025-10-15

-- Add is_substitute column
ALTER TABLE holidays
ADD COLUMN is_substitute BOOLEAN DEFAULT FALSE COMMENT 'TRUE if this is a compensatory/substitute day';

-- Add original_holiday_date column
ALTER TABLE holidays
ADD COLUMN original_holiday_date DATE NULL COMMENT 'Original holiday date if this is a substitute day';

-- Add index for better query performance
CREATE INDEX idx_holidays_is_substitute ON holidays(is_substitute);
CREATE INDEX idx_holidays_original_date ON holidays(original_holiday_date);

-- Update existing data: Mark all existing holidays as original (not substitute)
UPDATE holidays SET is_substitute = FALSE WHERE is_substitute IS NULL;

-- Add comment to table
ALTER TABLE holidays COMMENT = 'Public holidays and substitute days. Substitute days have 200% OT rate, original holidays have 300% OT rate.';
