-- ==================== UPDATE REQUESTS TABLE FOR LEAVE REQUESTS ====================
-- Add specific columns for leave request data instead of using JSON

-- Add leave-specific columns to requests table
ALTER TABLE requests
ADD COLUMN leave_type_id BIGINT NULL AFTER request_type_id,
ADD COLUMN start_date DATE NULL,
ADD COLUMN end_date DATE NULL,
ADD COLUMN day_count INT NULL,
ADD COLUMN reason TEXT NULL,
ADD CONSTRAINT FK_requests_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types (id) ON DELETE SET NULL ON UPDATE CASCADE;

-- Add indexes for better performance
CREATE INDEX IX_requests_leave_type ON requests (leave_type_id);

CREATE INDEX IX_requests_dates ON requests (start_date, end_date);

CREATE INDEX IX_requests_user_dates ON requests (
    created_by_user_id,
    start_date,
    end_date
);

-- Comments for documentation
ALTER TABLE requests COMMENT = 'Main requests table supporting all request types. For leave requests, use leave_type_id, start_date, end_date, day_count, reason columns';

-- Verify the changes
DESCRIBE requests;