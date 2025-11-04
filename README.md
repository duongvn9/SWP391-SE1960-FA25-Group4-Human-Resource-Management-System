# Human Resource Management System (HRMS)

A comprehensive web-based Human Resource Management System built with Java EE technologies, designed to streamline HR operations and employee management for modern organizations.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Overview

The HRMS is a full-featured human resource management system that provides comprehensive solutions for managing employees, attendance, leave requests, overtime, payroll, and recruitment processes. The system is built with enterprise-grade security, role-based access control, and modern web technologies.

### Key Highlights

- **Role-Based Access Control (RBAC)** with 6 system roles (ADMIN, HR_MANAGER, HR_STAFF, DEPT_MANAGER, EMPLOYEE, GUEST)
- **70+ Granular Features** with permission override at role, department, and account levels
- **Comprehensive Attendance Management** with automated calculations and Vietnamese labor law compliance
- **Leave Management System** supporting 6+ leave types including half-day leaves (AM/PM)
- **Overtime Management** with strict caps (10h/day, 40h/month, 300h/year) per Vietnamese labor code
- **Recruitment Management** with public job board and application tracking
- **Payroll System** with automated salary calculations based on attendance and OT
- **Google OAuth Integration** for secure, passwordless authentication
- **AI-Powered Chatbot** using Google Gemini 2.5 Flash for Vietnamese HR support
- **Responsive Web Interface** built with JSP, JSTL, and custom CSS
- **32-Table Database Schema** with UTF8MB4 support for Vietnamese characters
- **Comprehensive Audit Trail** for all critical operations (CRUD, approvals, status changes)
- **Email Notification System** with customizable templates and rate limiting
- **Multi-format Export** (Excel, PDF) for reports and data analysis

## Features

### 🔐 Authentication & Authorization
- **Multi-factor Authentication** with Google OAuth2 integration
- **Role-Based Access Control** with hierarchical permissions
- **Session Management** with configurable timeout
- **Password Security** using BCrypt hashing
- **CSRF Protection** for secure form submissions

### 👥 Employee Management
- **Employee Profile Management** with comprehensive information
- **Account Management** with lock/unlock capabilities
- **Department & Position Management**
- **Employment Contract Management**
- **User Role Assignment**

### ⏰ Attendance Management
- **Automated Time Tracking** with check-in/check-out functionality
- **Overtime Calculation** with Vietnamese labor law compliance (10h/day, 40h/month, 300h/year caps)
- **Attendance Adjustments** with approval workflow and manager authorization
- **Period Management** with lock/unlock capabilities for payroll processing
- **Export/Import** functionality for attendance data in Excel format
- **Late/Early Detection** with configurable tolerance (default: 5 minutes)
- **Rounding Rules** for check-in (round down) and check-out (round up)
- **Paid Break Handling** for shifts ≥ 6 hours (30-minute paid break)
- **Status Calculation** with precedence rules (Approved Leave > Absent > Late/Early)
- **Work Hours Calculation** excluding unpaid breaks and late/early deductions
- **OT Hours Auto-calculation** with multipliers (1.5x weekday, 2.0x weekend, 3.0x holiday)
- **Multi-role Access** with department-based filtering for managers

### 🏖️ Leave Management
- **Multiple Leave Types**: Annual, Personal, Sick, Maternity, Paternity, Unpaid
- **Half-Day Leave Support** (AM: 08:00-12:00 / PM: 13:00-17:00 periods)
- **Leave Balance Tracking** with automatic calculations and year-based rollover
- **Approval Workflow** with multi-level authorization (Manager → HR Manager → Admin)
- **Vietnamese Holiday Calendar** integration with lunar calendar support
- **Leave Request History** with complete audit trail
- **Gender-Based Filtering** for gender-specific leave types (Maternity/Paternity)
- **Leave Type Rules** with earned amounts, max carry-over, and eligibility
- **Leave Ledger System** tracking all balance changes (Earned, Used, Adjusted, Expired)
- **File Attachments** for medical certificates and supporting documents
- **Automatic Validation** against available balance and business rules
- **Email Notifications** for request approvals/rejections

### 💰 Payroll Management
- **Automated Salary Calculations** based on attendance, overtime, and deductions
- **Payslip Generation** with detailed breakdowns (base salary, allowances, OT pay, deductions)
- **Deduction Management** for late/early penalties and unpaid leave
- **Export Functionality** for payroll reports in Excel and PDF formats
- **Salary History Tracking** with effective dates and change reasons
- **Period-Based Processing** tied to locked timesheet periods
- **JSON Storage** for flexible payslip breakdown structure
- **Multi-Component Salary** supporting base, allowances, bonuses, and deductions
- **OT Pay Integration** with multiplier-based calculations (1.5x, 2.0x, 3.0x)
- **Compliance Reporting** for tax and social insurance purposes

### 📝 Request Management
- **Unified Request System** for all request types (Leave, OT, Attendance Appeals, Recruitment)
- **Leave Requests** with multi-level approval workflow
- **Overtime Requests** with manager approval and Vietnamese labor law compliance
- **Attendance Appeals** for dispute resolution and adjustment requests
- **Recruitment Requests** with multi-level approval and budget validation
- **File Attachments** support for supporting documents and evidence
- **Request History** with complete audit trail and status transitions
- **Bulk Operations** for approving/rejecting multiple requests
- **Email Notifications** for request status changes
- **Request Expiration** automatic handling for time-sensitive requests

### 🎯 Recruitment Management
- **Job Posting Management** with rich text descriptions and requirements
- **Application Tracking** with candidate evaluation and status updates
- **Interview Scheduling** and management
- **Recruitment Workflow** with multi-level approval (Dept Manager → HR Manager → Admin)
- **Public Job Board** for external candidates (no authentication required)
- **Application Form** with CV upload and cover letter
- **Candidate Database** with contact information and application history
- **Job Posting Status** (Draft, Open, Closed, Cancelled)
- **Email Notifications** for application confirmations and status updates
- **Department-Based Filtering** for hiring managers
- **Application Deadlines** with automatic status updates

### 🤖 AI-Powered Chatbot
- **Gemini AI Integration** for intelligent HR assistance
- **Vietnamese Language Support** for natural conversations
- **Context-Aware Responses** based on company policies from chatbot-qa.json
- **Quick Suggestions** for common HR questions (leave, OT, attendance, profile)
- **Real-time Support** for leave balance, overtime eligibility, and attendance queries
- **Responsive Widget** accessible from any authenticated page
- **Error Handling** with graceful fallbacks and timeout management
- **Session-Based** authentication and user context awareness

### ⚙️ System Administration
- **System Settings** configuration via JSON-based parameters
- **Role & Permission Management** with granular feature-level RBAC (70+ features)
- **Department & Position Management** with hierarchical structure
- **Audit Logging** for all system activities (CRUD operations, status changes)
- **Account Management** with lock/unlock capabilities and status tracking
- **Feature Toggle System** for enabling/disabling features by role, account, or department
- **Permission Override** at account and department levels (GRANT/DENY)
- **Session Management** with configurable timeout and security settings
- **Email Configuration** for SMTP settings and template management
- **Holiday Calendar Management** for Vietnamese public holidays and compensatory days
- **OT Policy Configuration** with JSON-based rule engine
- **Leave Type Configuration** with gender-based eligibility and balance rules
- **Backup & Recovery** with database backup scripts

## Technology Stack

### Backend Technologies
- **Java 17** - Core programming language
- **Jakarta EE 10** - Enterprise Java platform (Jakarta EE 10.0.0)
- **Maven 3.6+** - Build automation and dependency management
- **MySQL 8.0** - Primary relational database
- **HikariCP 5.1.0** - High-performance JDBC connection pooling
- **BCrypt (jBCrypt 0.4)** - Secure password hashing
- **Gson 2.10.1** - JSON serialization/deserialization
- **Jackson 2.16.1** - JSON processing and data binding
- **Apache POI 5.2.5** - Excel file creation and manipulation
- **iText PDF 5.5.13.3** - PDF document generation
- **Hibernate Validator 8.0.1** - Bean validation framework
- **SLF4J 2.0.9 + Logback 1.4.14** - Logging framework

### Frontend Technologies
- **JSP (JavaServer Pages)** - Server-side rendering with Jakarta Servlet JSP
- **JSTL 3.0** - JSP Standard Tag Library for Jakarta EE 10
- **CSS3** - Modern styling and responsive design
- **JavaScript (ES6+)** - Client-side interactivity and validation
- **Custom CSS Framework** - Responsive layout system
- **Font Awesome** - Icon library

### Development & Testing Tools
- **JUnit 5 (5.10.0)** - Unit testing framework
- **Mockito 5.6.0** - Mocking framework for tests
- **H2 Database 2.2.224** - In-memory database for testing
- **Maven Surefire 3.2.2** - Unit test execution
- **Maven Failsafe 3.2.2** - Integration test execution
- **Maven Checkstyle 3.3.1** - Code style validation (disabled)
- **SpotBugs 4.8.1.0** - Static code analysis

### External Integrations & APIs
- **Google OAuth2** - Secure authentication provider
- **Google Gemini AI** - AI-powered chatbot (gemini-2.5-flash model)
- **Lunar Calendar Library (lunar-java)** - Vietnamese lunar calendar and holiday calculations
- **Apache HttpClient5 5.2.1** - HTTP client for API integrations
- **Jakarta Mail API 2.1.3** - Email sending functionality
- **Eclipse Angus Mail 2.0.3** - Mail implementation for Jakarta EE

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Apache Tomcat 10+** (or compatible servlet container)
- **Git** (for version control)

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/duongvn9/SWP391-SE1960-FA25-Group4-Human-Resource-Management-System.git
cd SWP391-SE1960-FA25-Group4-Human-Resource-Management-System
```

### 2. Database Setup
```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Import database schema
mysql -u root -p hrms < HRMS/db-script/hrms_mysql.sql
```

### 3. Build the Application
```bash
cd HRMS
mvn clean compile
mvn package
```

### 4. Deploy to Application Server
```bash
# Copy WAR file to Tomcat webapps directory
cp target/HRMS-1.0-SNAPSHOT.war $TOMCAT_HOME/webapps/
```

## Configuration

### Database Configuration
Update `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/hrms
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### Google OAuth Configuration
Update `src/main/resources/application.properties`:
```properties
google.oauth.client.id=your_google_client_id
google.oauth.client.secret=your_google_client_secret
google.oauth.redirect.uri=http://localhost:8080/HRMS/login-google
```

### Gemini AI Chatbot Configuration

The HRMS includes an AI-powered chatbot using Google's Gemini 2.0 Flash model to provide intelligent assistance for HR-related questions.

#### 1. Obtain Gemini API Key

1. Visit [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click **"Get API Key"** or **"Create API Key"**
4. Copy the generated API key (format: `AIza...`)

**Important**: Keep your API key secure and never commit it to version control.

#### 2. Configure API Key

**Option A: Using Environment Variable (Recommended for Production)**
```bash
# Linux/Mac
export GEMINI_API_KEY=your_actual_api_key_here

# Windows (Command Prompt)
set GEMINI_API_KEY=your_actual_api_key_here

# Windows (PowerShell)
$env:GEMINI_API_KEY="your_actual_api_key_here"
```

**Option B: Direct Configuration (Development Only)**

Update `src/main/resources/application.properties`:
```properties
# Gemini AI Chatbot Configuration
gemini.api.key=${GEMINI_API_KEY}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
gemini.api.timeout=10000

# Chatbot QA Data File
chatbot.qa.file.path=docs/chatbot-qa.json
```

**For development testing only**, you can temporarily set the key directly:
```properties
gemini.api.key=AIza...your_key_here
```

⚠️ **Security Warning**: Never commit actual API keys to Git. Always use environment variables in production.

#### 3. Prepare Chatbot Knowledge Base

The chatbot uses a JSON file containing company-specific HR policies and Q&A data. Ensure the file exists at:
```
HRMS/docs/chatbot-qa.json
```

The file should follow this structure:
```json
{
  "system_prompt": {
    "role": "Trợ lý nội bộ về chính sách và quy trình nhân sự",
    "guidelines": [
      "Trả lời bằng tiếng Việt",
      "Chỉ trả lời về chính sách công ty",
      "Nếu không biết, hãy thừa nhận"
    ]
  },
  "categories": [
    {
      "category": "Chính sách Nghỉ phép",
      "qa_pairs": [
        {
          "id": "leave_001",
          "question": "Làm sao để gửi đơn nghỉ phép?",
          "answer": "Để gửi đơn nghỉ phép, bạn thực hiện các bước sau...",
          "keywords": ["gửi đơn", "nghỉ phép"]
        }
      ]
    }
  ]
}
```

#### 4. Verify Installation

After configuration, restart the application and:
1. Login to the HRMS system
2. Look for the chatbot toggle button (💬) at the bottom-right corner
3. Click to open the chatbot widget
4. Try asking: "Làm sao để gửi đơn nghỉ phép?"

If the chatbot responds, the integration is successful!

#### 5. Troubleshooting

**Chatbot button not appearing:**
- Verify you are logged in (chatbot only available for authenticated users)
- Check browser console for JavaScript errors
- Ensure `chatbot-widget.jsp` is included in your layout

**"API key invalid" error:**
- Verify the API key is correctly set in environment variable or properties file
- Check that the key has not expired or been revoked
- Ensure no extra spaces or quotes in the key value

**Timeout errors:**
- Check your internet connection
- Verify the Gemini API endpoint is accessible
- Consider increasing `gemini.api.timeout` value

**Empty or incorrect responses:**
- Verify `chatbot-qa.json` file exists and is properly formatted
- Check application logs for JSON parsing errors
- Ensure the file path in `chatbot.qa.file.path` is correct

#### 6. API Usage and Limits

- **Model**: gemini-2.5-flash (production-ready model)
- **Free Tier**: 15 requests per minute, 1,500 requests per day
- **Timeout**: 10 seconds per request (configurable)
- **Rate Limiting**: Automatic retry with exponential backoff
- **Context Window**: Up to 1M tokens for comprehensive Q&A context

For production use with higher limits, consider upgrading to a paid Google Cloud plan.

#### 7. Additional Documentation

For detailed setup and deployment information, see:
- **[Quick Setup Guide](docs/CHATBOT_SETUP_GUIDE.md)** - Get started in 5 minutes
- **[Deployment Checklist](docs/CHATBOT_DEPLOYMENT_CHECKLIST.md)** - Complete production deployment guide

### Application Settings
Configure other settings in `application.properties`:
```properties
# Session Configuration
session.timeout=7200
session.secure=false
session.http.only=true

# File Upload Limits
upload.max.file.size=5MB
upload.max.request.size=25MB
upload.base.path=uploads

# Pagination Settings
pagination.default.size=20
pagination.max.size=100
```

## Usage
### updating....


### Default Login Credentials
- **Admin**: 
- **HR Manager**: 
- **HR Staff**: 
- **Manager**: 
- **Employee**: 

### Key Workflows

#### Employee Self-Service Portal
1. **Login** with employee credentials (username/email + password or Google OAuth)
2. **View Personal Dashboard** with attendance summary, leave balance, and pending requests
3. **Submit Leave Requests** with automatic routing to manager for approval
4. **View Attendance Records** with check-in/check-out history and OT hours
5. **Check Payslips** with detailed salary breakdown
6. **Update Personal Profile** with contact information and emergency contacts
7. **Use AI Chatbot** for quick HR policy questions

#### Manager Operations
1. **Approve/Reject Team Leave Requests** with comments and approval workflow
2. **View Team Attendance** with filtering by employee, date range, and status
3. **Create OT Requests** for team members with justification
4. **Manage Department Information** and team roster
5. **Submit Recruitment Requests** for new positions with budget approval
6. **Generate Team Reports** for attendance, leave usage, and OT
7. **Monitor Pending Requests** with dashboard notifications

#### HR Staff Operations
1. **Manage All Requests** across all departments with filtering and search
2. **Import/Export Attendance Data** in Excel format for payroll processing
3. **Generate Payroll Reports** for specified periods
4. **Manage Employee Information** including contracts and salary changes
5. **Configure Leave Types** and balance allocations
6. **Manage Job Postings** and candidate applications
7. **Process Attendance Appeals** and adjustments
8. **View Audit Logs** for compliance and troubleshooting

#### HR Manager Operations (All HR Staff capabilities plus:)
1. **Configure System Settings** including OT policies and holiday calendars
2. **Manage Roles and Permissions** with granular feature-level control
3. **Lock/Unlock Timesheet Periods** for payroll processing
4. **Approve Recruitment Requests** with budget validation
5. **Generate Executive Reports** and analytics
6. **Manage Department Structure** and organizational hierarchy
7. **Configure Email Templates** for notifications

#### Admin Operations (All capabilities plus:)
1. **Full System Configuration** including database and security settings
2. **User Account Management** with lock/unlock and role assignment
3. **Feature Toggle Management** at system, role, department, and account levels
4. **Audit Trail Review** for security and compliance monitoring
5. **Backup and Recovery** operations
6. **Performance Monitoring** and optimization
7. **Integration Management** (Google OAuth, Gemini AI, Email SMTP)

## Project Structure

```
HRMS/
├── src/main/java/group4/hrms/
│   ├── controller/              # Web controllers (50+ servlets)
│   │   ├── filter/              # Authentication, authorization, and CSRF filters
│   │   ├── profile/             # User profile management controllers
│   │   ├── setting/             # System settings controllers
│   │   └── contract/            # Employment contract controllers
│   ├── dao/                     # Data access layer (30+ DAOs)
│   │   └── impl/                # DAO implementations
│   ├── dto/                     # Data transfer objects
│   ├── model/                   # Entity models (32 entities)
│   ├── service/                 # Business logic services
│   │   └── impl/                # Service implementations
│   ├── email/                   # Email service module
│   │   ├── controller/          # Email-related controllers
│   │   └── service/             # Email sending services
│   ├── util/                    # Utility classes (20+ utilities)
│   ├── exception/               # Custom exceptions
│   └── listener/                # Application lifecycle listeners
├── src/main/resources/
│   ├── application.properties   # Application configuration
│   ├── db.properties            # Database connection settings
│   ├── docs/                    # Documentation resources
│   │   └── chatbot-qa.json      # Chatbot knowledge base
│   ├── email-templates/         # HTML email templates
│   └── META-INF/
│       └── persistence.xml      # JPA configuration
├── src/main/webapp/
│   ├── index.jsp                # Application entry point
│   ├── assets/                  # Static resources
│   │   ├── css/                 # Stylesheets (20+ CSS files)
│   │   ├── js/                  # JavaScript files
│   │   └── img/                 # Images and icons
│   ├── WEB-INF/
│   │   ├── web.xml              # Servlet configuration
│   │   ├── beans.xml            # CDI configuration
│   │   └── views/               # JSP pages organized by module
│   │       ├── auth/            # Login, registration pages
│   │       ├── dashboard/       # Dashboard views
│   │       ├── employees/       # Employee management
│   │       ├── attendance/      # Attendance tracking
│   │       ├── requests/        # Leave, OT, appeal requests
│   │       ├── recruitment/     # Job postings and applications
│   │       ├── payroll/         # Payslip management
│   │       ├── profile/         # User profile
│   │       ├── admin/           # Admin settings
│   │       ├── components/      # Reusable UI components
│   │       └── layout/          # Page layouts
│   └── META-INF/
│       └── context.xml          # Tomcat context configuration
├── src/test/                    # Test classes
│   ├── java/                    # Unit and integration tests
│   └── resources/               # Test resources
├── db-script/                   # Database migration scripts
│   ├── hrms_mysql.sql           # Main database schema
│   ├── backup-db/               # Database backups
│   └── *.sql                    # Feature-specific migrations
├── docs/                        # Project documentation
│   ├── bussinessRules.md        # Business rules and compliance
│   ├── rbac-features-urls.md    # RBAC feature mapping
│   └── request-system-flow-summary.md
├── target/                      # Build output directory
├── pom.xml                      # Maven project configuration
└── nb-configuration.xml         # NetBeans IDE configuration
```

### Key Directories Explained

**Controllers** (`src/main/java/group4/hrms/controller/`):
- 50+ servlet controllers handling HTTP requests
- Organized by feature: accounts, attendance, requests, recruitment, etc.
- Filters for authentication, authorization, and security

**Services** (`src/main/java/group4/hrms/service/`):
- Business logic layer separated from controllers
- Key services: AttendanceService, LeaveRequestService, OTRequestService, GeminiService

**DAOs** (`src/main/java/group4/hrms/dao/`):
- Database access layer with JDBC
- BaseDao for common CRUD operations
- Specialized DAOs for complex queries

**Views** (`src/main/webapp/WEB-INF/views/`):
- JSP pages organized by functional modules
- Reusable components (header, sidebar, footer, chatbot)
- Responsive layouts with custom CSS

**Database Scripts** (`db-script/`):
- Main schema: `hrms_mysql.sql` (945 lines)
- Feature migrations for OT, leave, attendance enhancements
- Backup snapshots for disaster recovery

## Database Schema

The system uses a comprehensive MySQL database schema with **32 tables** organized into logical modules:

### 🔐 Authentication & Authorization (10 tables)
- **accounts** - User authentication credentials and login information
- **auth_identities** - OAuth provider identities (Google OAuth, Local authentication)
- **auth_local_credentials** - Local password credentials with BCrypt hashing
- **roles** - System roles (ADMIN, HR_MANAGER, HR_STAFF, DEPT_MANAGER, EMPLOYEE, GUEST)
- **features** - System features and permissions (70+ granular features)
- **role_features** - Role-permission mappings for RBAC
- **account_features** - Account-specific permission overrides (GRANT/DENY)
- **account_roles** - Account-role assignments with audit trail (created_at, created_by)
- **department_features** - Department-level permission overrides
- **position_roles** - Position-role mappings for automatic role assignment

### 👥 Employee Management (5 tables)
- **users** - Employee profile information (personal details, contact, address)
- **departments** - Organizational structure and department hierarchy
- **positions** - Job positions, levels, and descriptions
- **employment_contracts** - Employment contracts with salary and allowance details
- **salary_history** - Employee salary change history and audit trail

### ⏰ Attendance & Time Management (5 tables)
- **attendance_logs** - Daily check-in/check-out records with work hours, OT hours, and status
- **timesheet_periods** - Payroll periods with lock/unlock functionality and submission tracking
- **ot_policies** - Overtime calculation policies and rules (JSON configuration)
- **holiday_calendar** - Company holiday calendars organized by year
- **holidays** - Public holidays, substitute days, and compensatory work days with OT multipliers

### 🏖️ Leave Management (3 tables)
- **leave_types** - Different types of leave (Annual, Personal, Sick, Maternity, Paternity, Unpaid, etc.)
- **leave_balances** - Employee leave balances by year and leave type
- **leave_ledger** - Leave transaction history with balance changes (Earned, Used, Adjusted, Expired)

### 📝 Request Management System (4 tables)
- **requests** - Unified request system for all request types (Leave, OT, Adjustment, Recruitment)
- **request_types** - Request type definitions with approval hierarchy and workflow configuration
- **request_transitions** - Request status change audit trail (who, when, old/new status)
- **attachments** - File attachments and external links for requests (supporting documents, evidence)

### 🎯 Recruitment Management (2 tables)
- **job_postings** - Job posting details, requirements, benefits, and application deadlines
- **applications** - Candidate applications with CV/resume, cover letter, and status tracking

### 💰 Payroll Management (1 table)
- **payslips** - Monthly payslip data with detailed salary breakdown (JSON format: base, allowances, OT, deductions)

### ⚙️ System Administration (3 tables)
- **system_parameters** - System configuration parameters (JSON-based key-value store)
- **audit_events** - Comprehensive audit trail for security and compliance (all CRUD operations)
- **outbox_messages** - Event-driven messaging for notifications and async processing (JSON payload)

### 📊 Database Features & Best Practices
- **UTF8MB4 Unicode Support** - Full support for Vietnamese characters and emojis
- **JSON Columns** - Flexible data storage for complex structures (OT policies, request details, payslip breakdowns)
- **Referential Integrity** - Comprehensive foreign keys with CASCADE and SET NULL constraints
- **Audit Trails** - Automatic created_at/updated_at timestamps on all critical tables
- **Data Integrity** - UNIQUE constraints on emails, usernames, and business keys
- **Performance Optimization** - Strategic indexes on frequently queried columns (user_id, department_id, date ranges)
- **InnoDB Storage Engine** - ACID compliance with transaction support
- **Flexible Schema** - JSON columns allow schema evolution without migrations

## API Documentation

### REST API Endpoints

The HRMS provides a RESTful API for certain operations. All API endpoints require authentication via session cookies.

#### Chatbot API
- **POST** `/chatbot/ask` - Send question to AI chatbot
  - Request: `{ "question": "Làm sao để gửi đơn nghỉ phép?" }`
  - Response: `{ "success": true, "answer": "...", "timestamp": "..." }`

#### OT Balance API
- **GET** `/api/ot-balance?userId={id}&month={month}&year={year}` - Get overtime balance for user
  - Response: JSON with total OT hours, used hours, and remaining quota

#### File Upload API
- **POST** `/upload` - Upload files for requests
  - Supports: PDF, JPG, PNG, DOCX
  - Max size: 5MB per file, 25MB total

#### Export APIs
- **GET** `/attendance/export?format=excel&...` - Export attendance data
- **GET** `/requests/export?format=excel&...` - Export request data
- **GET** `/payroll/export?format=pdf&...` - Export payroll data

### Servlet Mappings

#### Authentication & Authorization
- `/login` - Login page (GET) and authentication (POST)
- `/logout` - User logout
- `/login-google` - Google OAuth callback
- `/change-password` - Password change form

#### Dashboard
- `/dashboard` - Main dashboard (role-based views)

#### Employee Management
- `/users/list` - Employee list
- `/users/detail?id={id}` - Employee details
- `/users/create` - Create new employee
- `/users/update?id={id}` - Update employee information

#### Attendance Management
- `/attendance/record/emp` - Employee attendance records
- `/attendance/record/HR` - HR attendance management
- `/attendance/import` - Import attendance data (Excel)

#### Request Management
- `/requests/list` - Unified request list
- `/requests/detail?id={id}` - Request details
- `/requests/leave/create` - Create leave request
- `/requests/ot/create` - Create OT request
- `/requests/appeal` - Create attendance appeal
- `/requests/approve?id={id}` - Approve/reject request

#### Recruitment
- `/jobs` - Public job board
- `/job-postings/list` - Internal job posting management
- `/job-postings/create` - Create job posting
- `/applications/submit` - Submit job application

#### System Administration
- `/accounts/list` - Account management
- `/settings/system` - System configuration
- `/settings/roles` - Role and permission management

For detailed API documentation, see the [API Reference Guide](docs/api-reference.md) (coming soon).

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=LeaveRequestServiceTest

# Run integration tests
mvn verify
```

### Test Coverage
The project includes comprehensive test coverage for:
- **Unit Tests** for all service classes
- **Integration Tests** for DAO layer
- **Controller Tests** for web endpoints
- **RBAC Tests** for permission validation

### Test Data
Test data is automatically seeded using H2 in-memory database for testing scenarios.

## Contributing

We welcome contributions to the HRMS project! Please follow these guidelines:

### Development Guidelines
1. **Code Style**: Follow Google Java Style Guide with 4-space indentation
2. **Testing**: Write comprehensive tests for all new features (target: 70% coverage)
3. **Documentation**: Update README and JavaDoc for API changes
4. **Security**: Implement proper RBAC for new features with permission checks
5. **Performance**: Optimize database queries using indexes and caching
6. **Logging**: Use SLF4J with appropriate log levels (DEBUG, INFO, WARN, ERROR)
7. **Error Handling**: Use custom exceptions with meaningful error messages

### Coding Standards
- **Naming Conventions**:
  - Classes: PascalCase (e.g., `LeaveRequestService`)
  - Methods: camelCase (e.g., `calculateOvertimeHours()`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_OT_HOURS_DAILY`)
  - Database tables: snake_case (e.g., `attendance_logs`)
  
- **Package Structure**:
  - Controllers: `group4.hrms.controller`
  - Services: `group4.hrms.service` (interfaces) / `group4.hrms.service.impl` (implementations)
  - DAOs: `group4.hrms.dao`
  - Models: `group4.hrms.model`
  - DTOs: `group4.hrms.dto`
  - Utilities: `group4.hrms.util`

- **Database Conventions**:
  - All tables use UTF8MB4 Unicode collation
  - Primary keys: `id` (BIGINT AUTO_INCREMENT)
  - Foreign keys: `{table}_id` (e.g., `user_id`, `department_id`)
  - Timestamps: `created_at`, `updated_at` (DATETIME, default UTC)
  - Use JSON columns for flexible/complex data structures

### Pull Request Process
1. **Fork the repository** and create a feature branch from `main`
   ```bash
   git checkout -b feature/amazing-feature
   ```
2. **Make your changes** following the coding standards
3. **Write tests** for new functionality (unit + integration tests)
4. **Run tests** to ensure nothing is broken
   ```bash
   mvn clean test
   ```
5. **Update documentation** (README, JavaDoc, inline comments)
6. **Commit your changes** with descriptive commit messages
   ```bash
   git commit -m "Add: Amazing feature for leave management"
   ```
7. **Push to your fork** and open a Pull Request
   ```bash
   git push origin feature/amazing-feature
   ```
8. **Wait for code review** and address any feedback

### Commit Message Format
```
<Type>: <Short description>

<Detailed description (optional)>

<Issue reference (optional)>
```

**Types**: `Add`, `Fix`, `Update`, `Remove`, `Refactor`, `Docs`, `Test`, `Style`

**Examples**:
- `Add: Half-day leave support for morning/afternoon periods`
- `Fix: OT calculation error for holiday overtime`
- `Update: Improve chatbot response accuracy`
- `Refactor: Extract attendance calculation logic to service layer`

### Code Quality Checks
Before submitting a PR, ensure:
- ✅ Code compiles without errors: `mvn clean compile`
- ✅ All tests pass: `mvn test`
- ✅ No SpotBugs warnings: `mvn spotbugs:check`
- ✅ Code follows style guide (use IDE formatter)
- ✅ No security vulnerabilities introduced
- ✅ Database migrations are backward compatible

### Review Checklist
Reviewers will check for:
- [ ] Code follows project standards and conventions
- [ ] Tests are comprehensive and pass
- [ ] Documentation is updated and accurate
- [ ] RBAC permissions are properly implemented
- [ ] Database changes include migration scripts
- [ ] Error handling is robust
- [ ] Performance impact is acceptable
- [ ] Security best practices are followed

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Documentation

### Core Documentation
- **[README](README.md)** - Main project documentation (this file)
- **[Business Rules](docs/bussinessRules.md)** - Attendance, OT, and leave policy rules
- **[RBAC Features & URLs](docs/rbac-features-urls.md)** - Complete role-permission mapping
- **[Request System Flow](docs/request-system-flow-summary.md)** - Detailed request workflow documentation
- **[RBAC Frontend Guide](docs/rbac-frontend-guide.md)** - Frontend permission implementation guide


### Database Documentation
- **[Main Schema](HRMS/db-script/hrms_mysql.sql)** - Complete database schema (945 lines)
- **[Migration Scripts](HRMS/db-script/)** - Feature-specific database updates
- **[Backup Scripts](HRMS/db-script/backup-db/)** - Database backup snapshots

### Architecture Documentation
- **Controllers** - 50+ servlets handling HTTP requests
- **Services** - Business logic layer (20+ services)
- **DAOs** - Data access layer (30+ DAOs)
- **Models** - Entity classes (32 entities)
- **DTOs** - Data transfer objects for API responses
- **Utilities** - Helper classes (20+ utilities)

### API Documentation
- **REST Endpoints** - See [API Documentation](#api-documentation) section
- **Servlet Mappings** - Detailed URL patterns and HTTP methods
- **Request/Response Formats** - JSON schemas for API calls

### Development Guides
- **[Code Base Overview](docs/code-base-temp.md)** - Project structure and components
- **Testing Guide** - Unit and integration testing patterns
- **Deployment Guide** - Production deployment checklist
- **Troubleshooting Guide** - Common issues and solutions (coming soon)

## Support

For support, questions, and contributions:

### Contact Information
- **Project Lead**: Ngoc Duong Vu
- **Email**: ngocduongvu9999@gmail.com
- **GitHub**: [@duongvn9](https://github.com/duongvn9)
- **Repository**: [HRMS Project](https://github.com/duongvn9/SWP391-SE1960-FA25-Group4-Human-Resource-Management-System)

### Getting Help
- **Bug Reports**: Open an issue on [GitHub Issues](https://github.com/duongvn9/SWP391-SE1960-FA25-Group4-Human-Resource-Management-System/issues)
- **Feature Requests**: Submit via GitHub Issues with `enhancement` label
- **Documentation**: Check [docs/](docs/) folder for detailed guides
- **FAQ**: See [FAQs page](docs/faqs.md) for common questions (coming soon)

### Community Guidelines
- Be respectful and professional
- Provide detailed information when reporting issues
- Search existing issues before creating new ones
- Follow the code of conduct

### Issue Reporting Template
When reporting bugs, please include:
1. **Environment**: OS, Java version, Tomcat version, MySQL version
2. **Steps to Reproduce**: Detailed steps to trigger the issue
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Screenshots**: If applicable
6. **Error Logs**: Relevant stack traces or error messages

## Acknowledgments

### Academic Institution
- **FPT University** - Academic support, guidance, and project supervision
- **Software Engineering Program** - Course SWP391 - Software Engineering Project

### Development Team
- **Team Members**: [@duongvn9](https://github.com/duongvn9), [@HiuToluen](https://github.com/HiuToluen), [@ndduong2911](https://github.com/ndduong2911), [@hieuhehe-afk](https://github.com/hieuhehe-afk), [@HAB2005](https://github.com/HAB2005)


### Technologies & Libraries
We extend our gratitude to the open-source community and the following projects:
- **Jakarta EE** - Enterprise Java platform
- **MySQL** - Reliable database system
- **Google Gemini AI** - Advanced AI capabilities for chatbot
- **Apache Software Foundation** - POI, HttpClient, Commons libraries
- **HikariCP** - High-performance connection pool
- **SLF4J & Logback** - Logging framework
- **JUnit & Mockito** - Testing frameworks

### Compliance & References
- **Vietnamese Labor Law** - Compliance requirements and regulations
  - [Vietnam Briefing - Overtime Regulations](https://www.vietnam-briefing.com/)
  - [ASEAN Briefing - Labor Law](https://www.aseanbriefing.com/)
  - [Economica Vietnam - HR Compliance](https://www.economica.vn/)
- **Lunar Calendar Library** - Vietnamese holiday calculations by 6tail

### Special Thanks
- Course instructors and mentors for guidance and feedback
- Beta testers for valuable input and bug reports
- Open-source contributors for inspiration and code examples

### Project Context
This project is developed as part of the **SWP391 - Software Engineering Project** course at FPT University, Fall 2025 semester. The goal is to create a comprehensive, production-ready HR management system demonstrating:
- Enterprise software architecture
- Full-stack web development (Jakarta EE + JSP)
- Database design and optimization
- Security best practices (RBAC, OAuth, encryption)
- Third-party API integration (Google OAuth, Gemini AI)
- Vietnamese business context and labor law compliance

---

**Built with ❤️ by SWP391-SE1960-FA25-Group4**
