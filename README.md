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

## 🎯 Overview

The HRMS is a full-featured human resource management system that provides comprehensive solutions for managing employees, attendance, leave requests, overtime, payroll, and recruitment processes. The system is built with enterprise-grade security, role-based access control, and modern web technologies.

### Key Highlights

- **Role-Based Access Control (RBAC)** with 6 system roles (ADMIN, HRM, HR, MANAGER, EMPLOYEE, GUEST)
- **Comprehensive Attendance Management** with automated calculations and overtime tracking
- **Leave Management System** supporting various leave types including half-day leaves
- **Overtime Management** with Vietnamese labor law compliance
- **Recruitment Management** with application tracking
- **Payroll System** with automated calculations
- **Google OAuth Integration** for secure authentication
- **Responsive Web Interface** built with JSP and modern CSS

## ✨ Features

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
- **Overtime Calculation** with Vietnamese labor law compliance
- **Attendance Adjustments** with approval workflow
- **Period Management** with lock/unlock capabilities
- **Export/Import** functionality for attendance data
- **Late/Early Detection** with configurable tolerance

### 🏖️ Leave Management
- **Multiple Leave Types**: Annual, Personal, Sick, Maternity, Unpaid
- **Half-Day Leave Support** (AM/PM periods)
- **Leave Balance Tracking** with automatic calculations
- **Approval Workflow** with multi-level authorization
- **Vietnamese Holiday Calendar** integration
- **Leave Request History** and reporting

### 💰 Payroll Management
- **Automated Salary Calculations** based on attendance and overtime
- **Payslip Generation** with detailed breakdowns
- **Deduction Management** for late/early penalties
- **Export Functionality** for payroll reports
- **Salary History Tracking**

### 📝 Request Management
- **Unified Request System** for all request types
- **Leave Requests** with approval workflow
- **Overtime Requests** with manager approval
- **Attendance Appeals** for dispute resolution
- **Recruitment Requests** with multi-level approval

### 🎯 Recruitment Management
- **Job Posting Management**
- **Application Tracking** with candidate evaluation
- **Interview Scheduling** and management
- **Recruitment Workflow** with approval levels

### ⚙️ System Administration
- **System Settings** configuration
- **Role & Permission Management**
- **Department & Position Management**
- **Audit Logging** for all system activities
- **Backup & Recovery** capabilities

## 🛠️ Technology Stack

### Backend Technologies
- **Java 17** - Core programming language
- **Jakarta EE 10** - Enterprise Java platform
- **Maven** - Build and dependency management
- **MySQL 8.0** - Primary database
- **HikariCP** - High-performance connection pooling
- **BCrypt** - Password hashing
- **Jackson/Gson** - JSON processing
- **Apache POI** - Excel file processing
- **iText PDF** - PDF generation

### Frontend Technologies
- **JSP (JavaServer Pages)** - Server-side rendering
- **JSTL** - JSP Standard Tag Library
- **CSS3** - Styling and responsive design
- **JavaScript (ES6+)** - Client-side functionality
- **Bootstrap** - UI framework (implied from structure)

### Development Tools
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **H2 Database** - In-memory testing database
- **Maven Checkstyle** - Code quality checks
- **SpotBugs** - Static analysis

### External Integrations
- **Google OAuth2** - Authentication provider
- **Lunar Calendar Library** - Vietnamese holiday calculations
- **Apache HttpClient** - HTTP client for external APIs

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Apache Tomcat 10+** (or compatible servlet container)
- **Git** (for version control)

## 🚀 Installation

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

## ⚙️ Configuration

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

### Application Settings
Configure other settings in `application.properties`:
```properties
# Session timeout (seconds)
session.timeout=7200

# File upload limits
upload.max.file.size=10MB
upload.max.request.size=50MB

# Pagination settings
pagination.default.size=20
pagination.max.size=100
```

## 🎮 Usage: updating....

### Starting the Application
1. Start MySQL database service
2. Start Apache Tomcat server
3. Navigate to `http://localhost:8080/HRMS`

### Default Login Credentials
- **Admin**: 
- **HR Manager**: 
- **HR Staff**: 
- **Manager**: 
- **Employee**: 

### Key Workflows

#### Employee Self-Service
1. **Login** with employee credentials
2. **View Dashboard** with personal information
3. **Submit Leave Requests** with manager approval
4. **View Attendance Records** and payslips
5. **Update Personal Profile**

#### Manager Operations
1. **Approve/Reject** team leave requests
2. **View Team Attendance** and reports
3. **Manage Department** information
4. **Submit Recruitment** requests

#### HR Operations
1. **Manage All Requests** across departments
2. **Import/Export** attendance data
3. **Generate Payroll** reports
4. **Manage Employee** information
5. **Configure System** settings

## 📁 Project Structure

```
HRMS/
├── src/main/java/group4/hrms/
│   ├── controller/          # Web controllers
│   ├── dao/                 # Data access objects
│   ├── dto/                 # Data transfer objects
│   ├── model/               # Entity models
│   ├── service/             # Business logic services
│   ├── util/                # Utility classes
│   └── exception/            # Custom exceptions
├── src/main/resources/
│   ├── application.properties
│   ├── db.properties
│   └── META-INF/
├── src/main/webapp/
│   ├── assets/              # Static resources (CSS, JS, images)
│   ├── WEB-INF/views/       # JSP pages
│   └── WEB-INF/web.xml      # Web configuration
├── src/test/                # Test classes
├── db-script/               # Database scripts
└── docs/                    # Documentation
```

## 🗄️ Database Schema

The system uses a comprehensive database schema with the following key tables:

### Core Tables
- **accounts** - User authentication and login information
- **users** - Employee profile information
- **departments** - Organizational structure
- **positions** - Job positions and roles
- **roles** - System roles (ADMIN, HRM, HR, MANAGER, EMPLOYEE, GUEST)
- **features** - System features and permissions

### Attendance & Time Management
- **attendance_logs** - Daily attendance records
- **timesheet_periods** - Payroll periods
- **ot_policies** - Overtime calculation policies
- **holiday_calendar** - Company holidays

### Leave Management
- **leave_types** - Different types of leave
- **leave_balances** - Employee leave balances
- **leave_ledger** - Leave transaction history
- **requests** - All types of requests (leave, OT, appeals)

### Payroll & Compensation
- **salary_history** - Employee salary records
- **payslips** - Monthly payslip data
- **employment_contracts** - Contract information

### RBAC (Role-Based Access Control)
- **role_features** - Role-permission mappings
- **account_features** - Account-specific permissions
- **department_features** - Department-specific permissions
- **position_roles** - Position-role mappings

## 📚 API Documentation

Updating...

## 🧪 Testing

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

## 🤝 Contributing

### Development Guidelines
1. **Code Style**: Follow Google Java Style Guide
2. **Testing**: Write tests for all new features
3. **Documentation**: Update documentation for API changes
4. **Security**: Implement proper RBAC for new features
5. **Performance**: Optimize database queries and caching

### Pull Request Process
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Quality
- **Checkstyle**: Automated code style checking
- **SpotBugs**: Static analysis for bug detection
- **Unit Tests**: Minimum 80% code coverage
- **Integration Tests**: All critical workflows tested

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- **Email**: ngocduongvu9999@gmail.com
- **Documentation**: [Project Wiki](https://github.com/duongvn9/SWP391-SE1960-FA25-Group4-Human-Resource-Management-System/blob/main/README.md)

## 🙏 Acknowledgments

- **FPT University** - Academic support and guidance
- **Team 4 Members** - duongvn9, updating....
- **Open Source Community** - Libraries and frameworks used
- **Vietnamese Labor Law** - Compliance requirements and regulations

---

**Built with ❤️ by SWP391-SE1960-FA25-Group4**
