# 🍽️ Sarana Restaurant System

A modern **Restaurant Management System** built with Spring Boot 4.0, designed to manage categories, subcategories, and store configurations for restaurant operations.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?style=flat-square&logo=postgresql)
![Cloudinary](https://img.shields.io/badge/Cloudinary-Image%20Storage-blueviolet?style=flat-square)

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [API Endpoints](#-api-endpoints)
- [Configuration](#-configuration)
- [Database Schema](#-database-schema)

---

## ✨ Features

- **Category Management** - Create, update, delete, and list menu categories with image upload
- **SubCategory Management** - Manage subcategories linked to categories
- **Store Configuration** - Configure store details including address, currency, and receipt settings
- **Image Upload** - Cloudinary integration for image storage
- **Pagination & Filtering** - Advanced filtering with JPA Specifications
- **Validation** - Request validation with Bean Validation (Jakarta)
- **Global Exception Handling** - Centralized error handling with custom exceptions
- **Duplicate Checking** - Reusable unique field validation utility
- **API Documentation** - Swagger/OpenAPI documentation

---

## 🛠 Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 4.0.3 | Application Framework |
| **Spring Data JPA** | - | Database ORM |
| **PostgreSQL** | Latest | Database |
| **MapStruct** | 1.6.3 | Object Mapping |
| **Lombok** | Latest | Boilerplate Reduction |
| **Cloudinary** | 1.30.0 | Image Upload Service |
| **SpringDoc OpenAPI** | 3.0.2 | API Documentation |
| **Gradle** | Latest | Build Tool |

---

## 📁 Project Structure

```
src/main/java/com/saranaresturantsystem/
├── SaranaResturantSystemApplication.java    # Main Application
│
├── Common/                                   # Shared Utilities
│   ├── DateTimeUtils.java
│   ├── FileHandler.java                     # Cloudinary upload handler
│   └── UniqueChecker.java                   # Duplicate checking utility
│
├── Config/                                   # Configuration
│   ├── Cloud/
│   │   └── CloudinaryConfig.java
│   └── Swagger/
│       └── OpenApiConfig.java
│
├── Controllers/                              # REST Controllers
│   ├── Categories/
│   │   ├── CategoryController.java
│   │   └── SubCategoryController.java
│   └── Settings/
│       └── StoreController.java
│
├── DTO/                                      # Data Transfer Objects
│   ├── PageDTO.java
│   ├── PaginationDTO.java
│   ├── Request/
│   │   ├── CategoryRequest.java
│   │   ├── SubCategoryRequest.java
│   │   └── StoreRequest.java
│   └── Response/
│       ├── ApiResponse.java
│       ├── CategoryResponse.java
│       ├── SubCategoryResponse.java
│       └── StoreResponse.java
│
├── Enities/                                  # JPA Entities
│   ├── Category.java
│   ├── SubCategory.java
│   └── Store.java
│
├── Execption/                                # Exception Handling
│   ├── ApiExecption.java
│   ├── DuplicateResourceException.java
│   ├── ErrorResponse.java
│   ├── GbobleExecptionHandler.java
│   └── ResourceNotFoundExecption.java
│
├── Mappers/                                  # MapStruct Mappers
│   ├── CategoryMapper.java
│   ├── SubCategoryMapper.java
│   └── StoreMapper.java
│
├── Repositories/                             # Spring Data JPA Repositories
│   ├── CategoryRepository.java
│   ├── SubCategoryRepository.java
│   └── StoreRepository.java
│
├── Services/                                 # Business Logic
│   ├── CategoryService.java
│   ├── SubCategoryService.java
│   ├── StoreService.java
│   └── Implements/
│       ├── CategoryServiceImp.java
│       ├── SubCategoryServiceImp.java
│       └── StoreServiceImp.java
│
├── Specification/                            # JPA Specifications (Filtering)
│   ├── Category/
│   │   ├── CategoryFilter.java
│   │   ├── CategorySpec.java
│   │   ├── SubCategoryFilter.java
│   │   └── SubCategorySpec.java
│   └── Settings/
│
└── Utils/                                    # Utility Classes
    └── GloblePagination.java
```

---

## 🚀 Getting Started

To run this project, you need to have Java 21 and Gradle installed. Then, clone the repository and run the following commands:

```bash
./gradlew build
./gradlew run
```

Ensure you have PostgreSQL running and configure the `application.properties` file with your database details.

---

## 📚 API Documentation

API documentation is available at `/v3/api-docs` and Swagger UI at `/swagger-ui.html`.

---

## 🔌 API Endpoints

### Categories

- `GET /api/categories` - List all categories
- `POST /api/categories` - Create a new category
- `GET /api/categories/{id}` - Get category by ID
- `PUT /api/categories/{id}` - Update category by ID
- `DELETE /api/categories/{id}` - Delete category by ID

### SubCategories

- `GET /api/subcategories` - List all subcategories
- `POST /api/subcategories` - Create a new subcategory
- `GET /api/subcategories/{id}` - Get subcategory by ID
- `PUT /api/subcategories/{id}` - Update subcategory by ID
- `DELETE /api/subcategories/{id}` - Delete subcategory by ID

### Store Settings

- `GET /api/store` - Get store settings
- `PUT /api/store` - Update store settings

---

## ⚙️ Configuration

Configure your application in the `src/main/resources/application.properties` file. Set your PostgreSQL database details and Cloudinary credentials here.

---

## 🗄️ Database Schema

The database schema is managed with Flyway migrations. Check the `src/main/resources/db/migration` folder for migration scripts.

---

## 📦 Dependencies

This project uses the following key dependencies:

- **Spring Boot Starter Web** - For building web, including RESTful, applications using Spring MVC.
- **Spring Boot Starter Data JPA** - For integrating Spring Data JPA with Hibernate.
- **PostgreSQL Driver** - For PostgreSQL database connectivity.
- **Cloudinary** - For image uploading and storage.
- **SpringDoc OpenAPI** - For API documentation generation.
- **MapStruct** - For bean mapping.
- **Lombok** - For reducing boilerplate code.

Check the `build.gradle` file for the complete list of dependencies.

---

## 🛠️ Development Tools

For development, this project recommends using:

- **Spring Tool Suite (STS)** or **Eclipse** - For Java development.
- **PostgreSQL** - As the database.
- **Cloudinary** - For image storage.
- **Lombok** - For reducing boilerplate code.

---

## 📦 Packaging

The application can be packaged as a JAR file. Use the following command to build the project and create the JAR file:

```bash
./gradlew clean build
```

The JAR file will be located in the `build/libs` directory.

---

## 🚀 Deployment

For deploying the application, you can use any cloud provider or on-premise server that supports Java. Ensure that the server has Java 21 and PostgreSQL installed.

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact

For any inquiries, please contact:

- **John Doe** - [john.doe@example.com](mailto:john.doe@example.com)

---

Thank you for checking out the Sarana Restaurant System! We hope this README provides you with a comprehensive overview of the project. Happy coding!
