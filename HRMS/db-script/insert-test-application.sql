-- Insert test application for testing
USE hrms;

-- Insert a test application (assuming job_id 6 exists from the log)
INSERT INTO applications (
    job_id, status, full_name, email, phone, 
    dob, gender, hometown, address_line1, city, country,
    created_at
) VALUES (
    6, 'new', 'Nguyen Van Test', 'test@example.com', '0123456789',
    '1990-01-01', 'Male', 'Ha Noi', '123 Test Street', 'Ha Noi', 'Vietnam',
    NOW()
);

SELECT 'Test application inserted successfully!' as Status;

-- Show the inserted application
SELECT * FROM applications WHERE email = 'test@example.com';