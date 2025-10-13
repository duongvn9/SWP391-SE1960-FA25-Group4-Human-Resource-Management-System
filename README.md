# SWP391-SE1960-FA25-Group4-Human-Resource-Management-System

## Human Resource Management System (HRMS)

A comprehensive HR management system built with Java, JSP, and MySQL.

## Features

### Leave Request Management
- Create and manage leave requests
- Multiple leave types (Annual, Sick, Unpaid, etc.)
- Comprehensive validation rules:
  - Overlap detection
  - Leave balance validation
  - OT conflict detection
  - Pending request warnings
- Real-time balance display
- Manager approval workflow

### Overtime (OT) Request Management
- Create and manage OT requests
- Multiple OT types (Weekday, Weekend, Holiday, Compensatory)
- Pay multiplier calculation
- Daily, weekly, monthly, and annual limits
- Manager creation for employees
- Balance tracking and display

## Documentation

- [Leave Validation Rules](docs/leave-validation-rules.md) - Comprehensive guide to leave request validation
- [Business Rules](docs/bussinessRules.md) - Core business logic and rules
- [Architecture Summary](HRMS_Architecture_Summary.md) - System architecture overview

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 10.x

### Setup

1. Clone the repository
```bash
git clone <repository-url>
cd HRMS
```

2. Configure database
```bash
# Create database
mysql -u root -p < db-script/hrms_mysql.sql

# Update database connection in src/main/resources/db.properties
```

3. Build the project
```bash
mvn clean install
```

4. Run tests
```bash
mvn test
```

5. Deploy to Tomcat
```bash
mvn package
# Copy target/HRMS.war to Tomcat webapps directory
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Suite
```bash
# Leave Request Service Tests
mvn test -Dtest=LeaveRequestServiceTest

# OT Request Service Tests
mvn test -Dtest=OTRequestServiceTest
```

## Project Structure

```
HRMS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── group4/hrms/
│   │   │       ├── controller/    # Servlet controllers
│   │   │       ├── service/       # Business logic
│   │   │       ├── dao/           # Data access layer
│   │   │       ├── model/         # Entity models
│   │   │       ├── dto/           # Data transfer objects
│   │   │       ├── util/          # Utility classes
│   │   │       └── exception/     # Custom exceptions
│   │   ├── resources/             # Configuration files
│   │   └── webapp/                # Web resources (JSP, CSS, JS)
│   └── test/
│       └── java/                  # Unit tests
├── db-script/                     # Database scripts
├── docs/                          # Documentation
└── pom.xml                        # Maven configuration
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Update documentation
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.