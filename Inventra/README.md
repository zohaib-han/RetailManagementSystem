# Inventra - Retail Management System

A comprehensive desktop application for managing retail operations including inventory management, point-of-sale (POS), sales reporting, and customer takeaway services.

## Features

- **User Management**: Role-based access control for admins, cashiers, and customers
- **Point of Sale (POS)**: Complete POS system for cashier transactions
- **Inventory Management**: Track and manage product inventory
- **Sales Management**: Record and process sales transactions
- **Return Processing**: Handle customer item returns efficiently
- **Takeaway Services**: Manage customer takeaway orders and bookings
- **Reporting**: Generate daily sales and comprehensive sales reports
- **Database Integration**: Persistent data storage with SQL backend

## Tech Stack

- **Language**: Java 21
- **GUI Framework**: JavaFX 21 (with FXML)
- **Build Tool**: Maven
- **Database**: SQLite/SQL
- **Testing**: JUnit 5

## Prerequisites

- Java Development Kit (JDK) 21 or higher
- Maven 3.6.0 or higher
- SQL database (SQLite or compatible SQL database)

## Installation

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Inventra
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn javafx:run
   ```
   Or using the Maven wrapper:
   ```bash
   ./mvnw javafx:run          # macOS/Linux
   mvnw.cmd javafx:run        # Windows
   ```

## Project Structure

```
Inventra/
├── src/
│   ├── main/
│   │   ├── java/com/example/inventra/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── SceneController.java         # Scene management
│   │   │   ├── Controller/                  # UI Controllers
│   │   │   │   ├── LoginController.java
│   │   │   │   ├── cashierPOSController.java
│   │   │   │   ├── adminManageProductsController.java
│   │   │   │   ├── adminManageUser.java
│   │   │   │   ├── adminSalesReportController.java
│   │   │   │   └── ...
│   │   │   ├── DBHandler/                   # Database operations
│   │   │   │   ├── DataBaseHandler.java
│   │   │   │   ├── UserAuthentication.java
│   │   │   │   ├── ProductHandler.java
│   │   │   │   ├── POSHandler.java
│   │   │   │   └── ...
│   │   │   └── OOP/                         # Domain models
│   │   │       ├── Admin.java
│   │   │       ├── Cashier.java
│   │   │       ├── Customer.java
│   │   │       └── ...
│   │   └── resources/com/example/inventra/
│   │       ├── fxml/                        # UI layouts
│   │       ├── css/                         # Stylesheets
│   │       └── font/                        # Custom fonts
│   └── test/
├── pom.xml                                  # Maven configuration
└── README.md                                # This file
```

## Usage

### Login
1. Launch the application
2. Enter your credentials (admin/cashier/customer roles available)
3. Access role-specific features based on user type

### Admin Functions
- Manage product inventory
- Manage user accounts
- View daily sales reports
- Process purchase orders
- Monitor sales metrics

### Cashier Functions
- Process transactions via POS
- Handle item returns
- Manage takeaway orders
- View transaction history

### Customer Functions
- Place takeaway orders
- Track booking status
- View order details

## Building from Source

### Clean Build
```bash
mvn clean
```

### Compile
```bash
mvn compile
```

### Package
```bash
mvn package
```

### Run Tests
```bash
mvn test
```

## Database Configuration

The application uses a SQL database for persistent storage. Ensure your database connection is properly configured in the database handler classes:
- [DBHandler/SQLDatabase.java](src/main/java/com/example/inventra/DBHandler/SQLDatabase.java)
- [DBHandler/DataBaseHandler.java](src/main/java/com/example/inventra/DBHandler/DataBaseHandler.java)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Development

### Code Structure
- **Controllers**: Handle UI events and user interactions
- **DBHandler**: Manage all database operations
- **OOP Models**: Domain objects representing business entities
- **FXML/CSS**: UI definitions and styling

### IDE Setup

**IntelliJ IDEA**
1. Open project as Maven project
2. Configure JDK to use Java 21
3. Enable JavaFX support

**Eclipse**
1. Import as existing Maven project
2. Install JavaFX e(fx)clipse plugin
3. Configure build path with JavaFX SDK

**VS Code**
1. Install Extension Pack for Java
2. Install JavaFX Support extension
3. Set `java.configuration.runtimes` to Java 21

## License

This project is proprietary. Unauthorized copying or distribution is prohibited.

## Support

For issues, questions, or suggestions, please contact the development team or open an issue in the repository.

---

**Version**: 1.0-SNAPSHOT  
**Last Updated**: January 2026
