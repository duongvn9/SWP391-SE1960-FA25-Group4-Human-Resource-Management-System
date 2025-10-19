# Requirements Document

## Introduction

The Request Detail page displays comprehensive information about a specific request (leave request, OT request, recruitment request, etc.) when accessed from the request list. This page provides a detailed view of all request information, including type-specific details, approval history, and available actions based on the user's role and request status.

The page will dynamically adapt its content based on the request type (LEAVE_REQUEST, OT_REQUEST, RECRUITMENT_REQUEST, ATTENDANCE_APPEAL) and display relevant information specific to each type. Users can view their own request details or, for managers, view and take action on subordinate requests.

This feature enhances the existing request management system by providing a dedicated detail view that consolidates all request information in one place, supporting both employee self-service and managerial approval workflows.

## Requirements

### Requirement 1: Load and Display Request by ID

**User Story:** As a user, I want to view detailed information about a specific request by accessing its detail page, so that I can see all relevant information in one place.

#### Acceptance Criteria

1. WHEN the user clicks on a request from the request list THEN the system SHALL navigate to the request detail page with the request ID in the URL
2. WHEN the detail page loads THEN the system SHALL retrieve the request data from the database using the request ID
3. IF the request ID is invalid or not found THEN the system SHALL display a "Request not found" error message
4. IF the user does not have permission to view the request THEN the system SHALL display an "Access denied" error message
5. WHEN the request is successfully loaded THEN the system SHALL display all basic request information (ID, title, type, status, created date, updated date)

### Requirement 2: Display Request Creator Information

**User Story:** As a user viewing a request detail, I want to see who created the request, so that I can identify the requester.

#### Acceptance Criteria

1. WHEN displaying request details THEN the system SHALL show the creator's full name
2. WHEN displaying request details THEN the system SHALL show the creator's employee code
3. WHEN displaying request details THEN the system SHALL show the creator's department name
4. WHEN displaying request details THEN the system SHALL show the request creation timestamp in format "dd/MM/yyyy HH:mm"
5. IF the creator information is not available THEN the system SHALL display "N/A" for missing fields

### Requirement 3: Display Leave Request Specific Details

**User Story:** As a user viewing a leave request detail, I want to see all leave-specific information, so that I can understand the leave request fully.

#### Acceptance Criteria

1. WHEN the request type is LEAVE_REQUEST THEN the system SHALL parse and display the leave detail JSON
2. WHEN displaying leave details THEN the system SHALL show the leave type name (Annual Leave, Sick Leave, etc.)
3. WHEN displaying leave details THEN the system SHALL show the start date and end date in format "dd/MM/yyyy"
4. WHEN displaying leave details THEN the system SHALL show the total number of working days requested
5. WHEN displaying leave details THEN the system SHALL show whether it is a half-day leave (Yes/No)
6. IF it is a half-day leave THEN the system SHALL show the period (AM or PM)
7. WHEN displaying leave details THEN the system SHALL show the duration in days (0.5 for half-day, full days otherwise)
8. WHEN displaying leave details THEN the system SHALL show the reason for leave
9. IF a certificate is required THEN the system SHALL indicate this requirement
10. IF an attachment is uploaded THEN the system SHALL provide a link to download the attachment

### Requirement 4: Display OT Request Specific Details

**User Story:** As a user viewing an OT request detail, I want to see all OT-specific information, so that I can understand the overtime request fully.

#### Acceptance Criteria

1. WHEN the request type is OT_REQUEST THEN the system SHALL parse and display the OT detail JSON
2. WHEN displaying OT details THEN the system SHALL show the OT date in format "dd/MM/yyyy"
3. WHEN displaying OT details THEN the system SHALL show the start time and end time in format "HH:mm"
4. WHEN displaying OT details THEN the system SHALL show the total OT hours
5. WHEN displaying OT details THEN the system SHALL show the reason for overtime
6. WHEN displaying OT details THEN the system SHALL show the work description or tasks to be completed
7. IF an attachment is uploaded THEN the system SHALL provide a link to download the attachment

### Requirement 5: Display Request Status and Approval Information

**User Story:** As a user viewing a request detail, I want to see the current status and approval history, so that I can track the request's progress.

#### Acceptance Criteria

1. WHEN displaying request status THEN the system SHALL use color-coded badges (yellow for PENDING, green for APPROVED, red for REJECTED, gray for CANCELLED)
2. WHEN the request status is PENDING THEN the system SHALL display "Waiting for approval"
3. WHEN the request status is APPROVED THEN the system SHALL display the approver's name and approval date
4. WHEN the request status is REJECTED THEN the system SHALL display the approver's name, rejection date, and rejection reason
5. WHEN the request status is CANCELLED THEN the system SHALL display the cancellation date
6. IF manager notes are available THEN the system SHALL display them in the approval information section

### Requirement 6: Display Available Actions Based on User Role and Request Status

**User Story:** As a user viewing a request detail, I want to see only the actions I'm allowed to perform, so that I can take appropriate actions on the request.

#### Acceptance Criteria

1. WHEN the user views any request detail THEN the system SHALL display a "Back to List" button
2. WHEN the user is a manager AND the request is from a subordinate AND the status is PENDING THEN the system SHALL display "Approve" button
3. WHEN the user is a manager AND the request is already processed (APPROVED, REJECTED, or CANCELLED) THEN the system SHALL NOT display approval action buttons
4. WHEN the user is not a manager OR the request is not from a subordinate THEN the system SHALL NOT display approval action buttons
5. WHEN the detail page is in read-only mode THEN the system SHALL NOT display any edit or delete buttons

### Requirement 7: Enable Request Approval Using Existing Modal (Manager Only)

**User Story:** As a manager, I want to approve or reject subordinate requests from the detail page using the same approval modal from the request list, so that I have a consistent approval experience.

#### Acceptance Criteria

1. WHEN the manager clicks "Approve" button THEN the system SHALL open the same approval modal used in the request list
2. WHEN the approval modal opens THEN the system SHALL display request information (ID, title, employee name)
3. WHEN the approval modal opens THEN the system SHALL provide "Approve" and "Reject" action buttons
4. WHEN the manager clicks "Approve" in the modal THEN the system SHALL update the request status to APPROVED
5. WHEN the manager clicks "Reject" in the modal THEN the system SHALL display a text field for rejection reason
6. WHEN the manager submits rejection THEN the system SHALL validate that a rejection reason is provided
7. WHEN the approval/rejection is successful THEN the system SHALL display a success message and refresh the detail page
8. IF the approval/rejection fails THEN the system SHALL display an error message with details
9. WHEN approving a leave request THEN the system SHALL deduct the leave days from the employee's leave balance

### Requirement 8: Support Navigation and Breadcrumbs

**User Story:** As a user viewing a request detail, I want clear navigation options, so that I can easily return to the request list or navigate to other pages.

#### Acceptance Criteria

1. WHEN the detail page loads THEN the system SHALL display a breadcrumb navigation showing "Home > Requests > Request Detail"
2. WHEN the user clicks on "Requests" in the breadcrumb THEN the system SHALL navigate back to the request list page
3. WHEN the detail page loads THEN the system SHALL display a "Back to List" button
4. WHEN the user clicks "Back to List" THEN the system SHALL navigate to the request list page
5. IF the user came from a filtered list THEN the system SHOULD preserve the filter state when returning to the list

### Requirement 9: Support Mobile-Responsive Design

**User Story:** As a user accessing the system from a mobile device, I want the request detail page to be mobile-friendly, so that I can view request details on any device.

#### Acceptance Criteria

1. WHEN the user accesses the page from a mobile device THEN the system SHALL display a mobile-optimized layout
2. WHEN viewing on mobile THEN the system SHALL stack information sections vertically
3. WHEN viewing on mobile THEN the system SHALL use touch-friendly buttons with adequate spacing
4. WHEN viewing on mobile THEN the system SHALL maintain all functionality available on desktop
5. WHEN viewing on mobile THEN the system SHALL ensure text is readable without horizontal scrolling

### Requirement 10: Handle Different Request Types Dynamically

**User Story:** As a system, I want to dynamically display request details based on the request type, so that each request type shows its relevant information.

#### Acceptance Criteria

1. WHEN the request type is LEAVE_REQUEST THEN the system SHALL display the leave request detail template
2. WHEN the request type is OT_REQUEST THEN the system SHALL display the OT request detail template
3. WHEN the request type is RECRUITMENT_REQUEST THEN the system SHALL display the recruitment request detail template
4. WHEN the request type is ATTENDANCE_APPEAL THEN the system SHALL display the attendance appeal detail template
5. IF the request type is unknown or not supported THEN the system SHALL display basic request information only
6. WHEN displaying type-specific details THEN the system SHALL gracefully handle missing or malformed JSON data
