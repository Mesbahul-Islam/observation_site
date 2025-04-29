# Astronomy Observations Platform

An interactive web application for astronomy enthusiasts to share, discover, and organize astronomical observations.

## Overview

This platform allows astronomers (both amateur and professional) to document celestial observations, share them with the community, and collect observations in curated collections. The system supports rating, commenting, and search functionality to enhance user engagement.

## Features

### User Management
- **User Registration & Authentication**: Secure account creation and login
- **User Profiles**: Display user activity and contributions

### Observations
- **Create Observations**: Document celestial events with detailed information
  - Astronomical coordinates (Right Ascension, Declination)
  - Observatory location (Name, Latitude, Longitude)
  - Detailed descriptions
- **Browse Observations**: View observations shared by the community
- **Search Functionality**: Filter observations by name, date, or user
- **Interaction**: Comment on and rate observations

### Collections
- **Create Collections**: Organize observations into themed collections
- **Manage Collections**: Add/remove observations from collections
- **Export to PDF**: Generate PDF documents from collections for offline use

### Dashboard
- **Featured Content**: View popular and highly-rated observations
- **Recent Activity**: Stay updated with the latest contributions

## Technology Stack

- **Backend**: Spring Boot, Java 21
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: SQLite
- **Security**: Spring Security
- **PDF Generation**: iText library
- **Build Tool**: Maven

## Setup and Installation

### Prerequisites
- JDK 21 or higher
- Maven
- Git

### Installation Steps

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/observatory.git
   ```

2. Navigate to the project directory:
   ```
   cd observatory
   ```

3. Build the project:
   ```
   ./mvnw clean package
   ```

4. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

5. Access the application in your browser:
   ```
   http://localhost:8080
   ```

## Usage

### Creating an Account
1. Navigate to the Register page
2. Fill in username, password, and nickname
3. Submit the form to create your account

### Adding an Observation
1. Log in to your account
2. Navigate to "Observations" and click "Add New Observation"
3. Fill in the required details (name, description, coordinates, etc.)
4. Submit the form to share your observation

### Creating a Collection
1. Navigate to "Collections" and click "Create New Collection"
2. Provide a name and description for your collection
3. Add observations to your collection while browsing

### Exporting a Collection
1. Navigate to your collection's page
2. Click the "Export to PDF" button to download a formatted document


## Architecture

The application follows a layered architecture pattern:

### Core Architectural Layers

- **Presentation Layer**: Thymeleaf templates and controllers that handle HTTP requests and responses
- **Service Layer**: Business logic implementation, containing service classes that orchestrate operations
- **Data Access Layer**: Repositories that interact with the database using Spring Data JPA
- **Domain Layer**: Entity classes representing the core business objects (Observation, User, Collection, etc.)
### Key Design Patterns

- **MVC (Model-View-Controller)**: Separates the application into three interconnected components
- **Repository Pattern**: Abstracts data access operations from business logic
- **Dependency Injection**: Spring's core feature for loose coupling between components
- **DTO Pattern**: Data Transfer Objects for transferring data between subsystems