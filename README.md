# NHTS Store

NHTS Store is a comprehensive store management application built with Java, Spring Boot, and Swing/JavaFX. It provides functionality for managing products, customers, orders, invoices, and more in a retail environment.

## Technologies Used

### Backend
- **Java 21**: Core programming language
- **Spring Boot 3.4.3**: Application framework
- **Spring Data JPA**: Data access layer
- **Spring Security**: Authentication and authorization
- **Flyway**: Database migration
- **Microsoft SQL Server**: Database

### Frontend
- **Java Swing**: UI components
- **JavaFX 21.0.6**: UI components
- **FlatLaf**: Modern look and feel for Swing
- **MaterialFX**: Material design components for JavaFX
- **MigLayout**: Layout manager for Swing

### Other Libraries
- **Lombok**: Reduces boilerplate code
- **ZXing**: Barcode/QR code generation
- **iText & PDFBox**: PDF generation for invoices
- **RxJava**: Reactive programming

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 21
- Docker and Docker Compose (for database)
- Maven

### Database Setup
1. Start the SQL Server database using Docker Compose:
   ```
   docker-compose up -d
   ```
   This will:
   - Start a SQL Server 2022 instance on port 14303
   - Create the database with the name `nhts-store`
   - Set up the database with username `sa` and password `Nhts!123456`

### Application Setup
1. Clone the repository
2. Build the application using Maven:
   ```
   mvn clean package
   ```

## Running the Application

### Using Maven
```
mvn spring-boot:run
```

### Using JAR file
```
java -jar target/nhts-store-0.0.1-SNAPSHOT.jar
```

The application will:
1. Show a loading screen with progress during startup
2. Initialize the database using Flyway migrations if needed
3. Open the main application window

## Features

- **User Management**: Role-based access control with different permission levels
- **Product Management**: Add, edit, and manage products with categories
- **Supplier Management**: Track and manage suppliers
- **Customer Management**: Maintain customer information
- **Order Processing**: Create and manage orders
- **Invoicing**: Generate and export invoices as PDF
- **Point of Sale**: Process sales transactions
- **Reporting**: View sales and inventory reports

## Application Structure

- **UI Components**: Located in `com.nhom4.nhtsstore.ui` package
- **Business Logic**: Services in `com.nhom4.nhtsstore.services` package
- **Data Access**: Repositories in `com.nhom4.nhtsstore.repositories` package
- **Domain Models**: Entities in `com.nhom4.nhtsstore.entities` package
- **Database Migrations**: Located in `src/main/resources/db/migration`

## Configuration

The application can be configured through:
- `application.yml`: Main configuration file
- `application.properties`: Additional properties

Key configuration options:
- Database connection details
- Invoice export directory
- UI theme settings