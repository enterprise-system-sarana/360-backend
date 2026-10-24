# 📱 360 Phone Shop Management System - API Documentation

Backend RESTful API for **360 Phone Shop Management System** built with **Java 21**, **Spring Boot 3**, **Spring Security (JWT)**, **Spring Data JPA**, **PostgreSQL**, **Flyway**, **Redis**, and **S3 / RustFS Object Storage**.

---

## 📑 Table of Contents

- [Overview & Base URL](#-overview--base-url)
- [Interactive Swagger / OpenAPI](#-interactive-swagger--openapi)
- [Authentication & Security](#-authentication--security)
- [Standard API Response Format](#-standard-api-response-format)
- [API Modules & Endpoints](#-api-modules--endpoints)
  - [1. Authentication (`/api/v1/auth`)](#1-authentication-apiv1auth)
  - [2. Cash Register / POS Shifts (`/api/v1/cash-registers`)](#2-cash-register--pos-shifts-apiv1cash-registers)
  - [3. Sales Management (`/api/v1/sales`)](#3-sales-management-apiv1sales)
  - [4. Payment Processing (`/api/v1/payments`)](#4-payment-processing-apiv1payments)
  - [5. Product Catalog (`/api/v1/product`, `model`, `brand`, `category`, `variants`)](#5-product-catalog)
  - [6. Purchases & Suppliers (`/api/v1/purchases`, `/api/v1/suppliers`)](#6-purchases--suppliers)
  - [7. Inventory & Stores (`/api/v1/stores`, `/api/v1/stock`)](#7-inventory--stores)
  - [8. Customers (`/api/v1/customer`)](#8-customers-apiv1customer)
  - [9. Finances & Expenses (`/api/v1/expenses`, `expense-types`, `bank`, `currency`)](#9-finances--expenses)
  - [10. Quotations (`/api/v1/quote`)](#10-quotations-apiv1quote)
  - [11. User & Access Control (`/api/v1/users`, `/api/v1/role`, `/api/v1/permission`)](#11-user--access-control)
  - [12. Analytics & Reporting Views (`/api/v1/reports/**`)](#12-analytics--reporting-views-apiv1reports)
  - [13. File Storage & S3 (`/api/v1/files`)](#13-file-storage--s3-apiv1files)
- [Database Migrations](#-database-migrations)
- [Running Locally](#-running-locally)

---

## 🌐 Overview & Base URL

- **Default Local Base URL**: `http://localhost:8080`
- **Global API Prefix**: `/api/v1`
- **Content-Type**: `application/json` (unless specified as `multipart/form-data`)

---

## 📖 Interactive Swagger / OpenAPI

When the server is running, the interactive OpenAPI documentation and Swagger UI are accessible at:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) (or `http://localhost:8080/swagger-ui.html`)
- **OpenAPI v3 JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🔐 Authentication & Security

All protected endpoints require a JWT Bearer token in the `Authorization` header:

```http
Authorization: Bearer <your_access_token>
```

### Public Endpoints (No Auth Required)
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/send-otp-code`
- `POST /api/v1/auth/verify-otp-code`
- `POST /api/v1/auth/forgot-password`
- `POST /api/v1/auth/verify-reset-token`
- `POST /api/v1/auth/reset-password`
- `POST /api/v1/auth/verify-email`
- `POST /api/v1/auth/resend-verification`
- `GET /api/v1/files/preview-file/**`
- `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`

---

## 📦 Standard API Response Format

All responses use a standardized `ApiResponse<T>` envelope:

### Success Response Envelope
```json
{
  "success": true,
  "status": "OK",
  "message": "Operation successful",
  "payload": { ... },
  "instant": "2026-10-22T02:30:00Z"
}
```

### Paginated Response Envelope (`PageDTO`)
```json
{
  "success": true,
  "status": "OK",
  "message": "Data retrieved successfully",
  "payload": {
    "content": [ ... ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 48,
    "totalPages": 5,
    "last": false
  },
  "instant": "2026-10-22T02:30:00Z"
}
```

---

## 🚀 API Modules & Endpoints

---

### 1. Authentication (`/api/v1/auth`)

Manage user registration, authentication, token refresh, OTP, and password lifecycle.

| Method | Endpoint | Description | Auth Required | Permissions |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Log in with username/email and password | No | Public |
| `POST` | `/api/v1/auth/register` | Register new user account | No | Public |
| `POST` | `/api/v1/auth/refresh` | Exchange refresh token for new access token | No | Public |
| `POST` | `/api/v1/auth/logout` | Invalidate current user's refresh token | Yes | Authenticated |
| `POST` | `/api/v1/auth/logout-all/{userId}` | Invalidate all active tokens for a user | Yes | `user:update` |
| `POST` | `/api/v1/auth/change-password` | Change authenticated user's password | Yes | Authenticated |
| `POST` | `/api/v1/auth/send-otp-code` | Send OTP verification code to email | No | Public |
| `POST` | `/api/v1/auth/verify-otp-code` | Verify OTP code | No | Public |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset email | No | Public |
| `POST` | `/api/v1/auth/verify-reset-token` | Check if password reset token is valid | No | Public |
| `POST` | `/api/v1/auth/reset-password` | Set new password using verified token | No | Public |
| `POST` | `/api/v1/auth/verify-email` | Confirm email address via token | No | Public |
| `POST` | `/api/v1/auth/resend-verification` | Resend verification email token | No | Public |

#### Login Request Body
```json
{
  "emailOrUsername": "admin",
  "password": "Password@123"
}
```

#### Auth Response Payload
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "d8e3b1c2-...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "firstName": "Super",
    "lastName": "Admin",
    "roles": ["ROLE_ADMIN"],
    "permissions": ["sale:create", "sale:read", "report:read", "..."]
  }
}
```

---

### 2. Cash Register / POS Shifts (`/api/v1/cash-registers`)

Manage cashier daily shifts, cash drawers, opening amounts, expected totals, and closing balances.

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/cash-registers/open` | Open a new shift (Open Sale) | `sale:create` |
| `PATCH` | `/api/v1/cash-registers/{id}/close` | Close cash register & calculate variance | `sale:update` |
| `GET` | `/api/v1/cash-registers/current` | Get current logged-in user's active open shift | `sale:read` |
| `GET` | `/api/v1/cash-registers/{id}` | Get cash register details by ID | `sale:read` |
| `GET` | `/api/v1/cash-registers` | List cash registers (paginated, filters) | `sale:read` |

#### Open Register Request Body
```json
{
  "storeId": 1,
  "openingBalance": 100.00
}
```

#### Close Register Request Body
```json
{
  "closingBalance": 850.50,
  "closingNote": "Shift closed with no discrepancies"
}
```

---

### 3. Sales Management (`/api/v1/sales`)

Manage POS orders, customer checkouts, stock deductions, serial tracking, and returns.

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/sales` | Create sale order, auto-link shift & deduct stock | `sale:create` |
| `GET` | `/api/v1/sales` | List sales (paginated, filter by store, customer, status, date) | `sale:read` |
| `GET` | `/api/v1/sales/{id}` | Get sale details by ID | `sale:read` |
| `PUT` | `/api/v1/sales/{id}` | Update pending sale order | `sale:update` |
| `PATCH` | `/api/v1/sales/{id}/complete` | Mark pending sale completed & commit inventory | `sale:update` |
| `PATCH` | `/api/v1/sales/{id}/close` | Finalize and close sale | `sale:update` |
| `PATCH` | `/api/v1/sales/{id}/cancel` | Cancel sale order & return stock | `sale:update` |
| `PATCH` | `/api/v1/sales/{id}/return` | Process return for specific items/serials | `sale:update` |
| `DELETE` | `/api/v1/sales/{id}` | Soft delete sale record | `sale:delete` |

#### Create Sale Request Body
```json
{
  "date": "2026-10-22T09:30:00",
  "storeId": 1,
  "customerId": 2,
  "bankId": 1,
  "grandTotal": 1200.00,
  "discount": 50.00,
  "paidAmount": 1150.00,
  "saleStatus": "COMPLETED",
  "paymentStatus": "PAID",
  "noted": "Walk-in purchase",
  "items": [
    {
      "productId": 10,
      "quantity": 1,
      "price": 1200.00,
      "itemDiscount": 50.00,
      "productSerialIds": [101]
    }
  ]
}
```

#### Process Sale Return Request Body
```json
{
  "returnedItems": [
    {
      "saleItemId": 45,
      "returnQuantity": 1,
      "returnedProductSerialIds": [101]
    }
  ],
  "note": "Customer returned unopened device"
}
```

---

### 4. Payment Processing (`/api/v1/payments`)

Process multi-line payments (e.g. part Cash, part ABA Bank transfer), record customer debt settlements, and purchase payments.

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/payments` | Process multi-line payment with idempotency | `payment:create` |
| `GET` | `/api/v1/payments` | List payments with filters & pagination | `payment:read` |
| `GET` | `/api/v1/payments/{id}` | Get payment detail and payment lines by ID | `payment:read` |

#### Payment Request Body (Multi-line / Split Tender)
```json
{
  "saleId": 12,
  "totalAmount": 1150.00,
  "receivedAmount": 1200.00,
  "returnAmount": 50.00,
  "dueAmount": 0.00,
  "status": "COMPLETED",
  "paymentDate": "2026-10-22T09:30:00",
  "note": "Split payment",
  "idempotencyKey": "sale-12-pay-20261022093000",
  "paymentLines": [
    {
      "paymentMethod": "CASH",
      "amount": 150.00,
      "transactionNo": null,
      "bankId": null,
      "currencyId": 1
    },
    {
      "paymentMethod": "BANK",
      "amount": 1000.00,
      "transactionNo": "ABA-TXN-984210",
      "bankId": 2,
      "currencyId": 1
    }
  ]
}
```

---

### 5. Product Catalog

#### 5.1 Products (`/api/v1/product`)

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/product` | List products with pagination & filters (`brandId`, `categoryId`, `modelId`, `code`, `name`) | `product:read` |
| `GET` | `/api/v1/product/{id}` | Get product summary by ID | `product:read` |
| `GET` | `/api/v1/product/{id}/detail` | Get full product detail (stock, serials, brand, model) | `product:read` |
| `POST` | `/api/v1/product` | Create new product | `product:create` |
| `PUT` | `/api/v1/product/{id}` | Update product | `product:update` |
| `DELETE` | `/api/v1/product/{id}` | Delete product | `product:delete` |

#### 5.2 Product Serials (`/api/v1/product-serial`)

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/product-serial` | Query serial numbers (`barcode`, `productId`, `status`) | `stock:read` |

#### 5.3 Brands, Categories, Models & Variants

| Module | Base Path | Supported Operations | Permissions |
| :--- | :--- | :--- | :--- |
| **Brands** | `/api/v1/brand` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` | `brand:read`, `brand:create`, `brand:update`, `brand:delete` |
| **Categories**| `/api/v1/category` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` | `category:read`, `category:create`, `category:update`, `category:delete` |
| **Models** | `/api/v1/model` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` | `model:read`, `model:create`, `model:update`, `model:delete` |
| **Variant Types** | `/api/v1/variant-types` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` | `variant:read`, `variant:create`, `variant:update`, `variant:delete` |
| **Variant Values** | `/api/v1/variant-values` | `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` | `variant:read`, `variant:create`, `variant:update`, `variant:delete` |

---

### 6. Purchases & Suppliers

#### 6.1 Purchases (`/api/v1/purchases`)

Manage purchase orders from suppliers, serial intake, and stock additions.

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/purchases` | List purchases with pagination and query parameters | `purchase:read` |
| `GET` | `/api/v1/purchases/{id}` | Get purchase details with item breakdown | `purchase:read` |
| `POST` | `/api/v1/purchases` | Create purchase order & populate serial numbers | `purchase:create` |
| `PUT` | `/api/v1/purchases/{id}` | Update purchase order | `purchase:update` |
| `DELETE` | `/api/v1/purchases/{id}` | Delete purchase record | `purchase:delete` |

#### 6.2 Suppliers (`/api/v1/suppliers`)

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/suppliers` | List suppliers | `supplier:read` |
| `GET` | `/api/v1/suppliers/{id}` | Get supplier by ID | `supplier:read` |
| `POST` | `/api/v1/suppliers` | Register new supplier | `supplier:create` |
| `PUT` | `/api/v1/suppliers/{id}` | Update supplier | `supplier:update` |
| `DELETE` | `/api/v1/suppliers/{id}` | Delete supplier | `supplier:delete` |

---

### 7. Inventory & Stores

#### 7.1 Stores / Branches (`/api/v1/stores`)

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/stores` | List all branch stores (`name`, `code`, `city`, `status`) | `store:read` |
| `GET` | `/api/v1/stores/{id}` | Get store details by ID | `store:read` |
| `POST` | `/api/v1/stores` | Create a new branch store | `store:create` |
| `PUT` | `/api/v1/stores/{id}` | Update branch store info | `store:update` |
| `DELETE` | `/api/v1/stores/{id}` | Delete branch store | `store:delete` |

#### 7.2 Stock Management (`/api/v1/stock`)

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/stock` | View stock quantity per product and store | `stock:read` |

---

### 8. Customers (`/api/v1/customer`)

Manage customer directories, phone contacts, purchase history, and receivable tracking.

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/customer` | List customers (paginated, search by `name`, `phone`, `code`) | `customer:read` |
| `GET` | `/api/v1/customer/{id}` | Get customer by ID | `customer:read` |
| `POST` | `/api/v1/customer` | Create new customer | `customer:create` |
| `PUT` | `/api/v1/customer/{id}` | Update customer details | `customer:update` |
| `DELETE` | `/api/v1/customer/{id}` | Delete customer | `customer:delete` |

---

### 9. Finances & Expenses

| Module | Base Path | Description | Permissions |
| :--- | :--- | :--- | :--- |
| **Expenses** | `/api/v1/expenses` | Record operating expenses (`amount`, `bankId`, `storeId`, `type`) | `expense:read`, `expense:create`, `expense:update`, `expense:delete` |
| **Expense Types** | `/api/v1/expense-types` | Expense categories (Rent, Utilities, Salary, etc.) | `expense:read`, `expense:create`, `expense:update`, `expense:delete` |
| **Banks** | `/api/v1/bank` | Bank accounts, account numbers, and current balances | `bank:read`, `bank:create`, `bank:update`, `bank:delete` |
| **Currencies** | `/api/v1/currency` | Currency exchange rates (USD, KHR, etc.) | `currency:read`, `currency:create`, `currency:update`, `currency:delete` |

---

### 10. Quotations (`/api/v1/quote`)

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/quote` | List price quotes (paginated) | `quote:read` |
| `GET` | `/api/v1/quote/{id}` | Get quote by ID | `quote:read` |
| `POST` | `/api/v1/quote` | Create quote for a customer | `quote:create` |
| `PUT` | `/api/v1/quote/{id}` | Update quote | `quote:update` |
| `DELETE` | `/api/v1/quote/{id}` | Delete quote | `quote:delete` |

---

### 11. User & Access Control

Manage administrative staff, cashiers, role assignments, and granular RBAC permissions.

| Method | Endpoint | Description | Permissions |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/users` | List staff users (filter by store, active, role) | `user:read` |
| `GET` | `/api/v1/users/{id}` | Get user by ID | `user:read` |
| `POST` | `/api/v1/users` | Create staff user (with role & store scope) | `user:create` |
| `PUT` | `/api/v1/users/{id}` | Update staff user | `user:update` |
| `DELETE` | `/api/v1/users/{id}` | Delete user | `user:delete` |
| `GET` | `/api/v1/role` | List all roles | `role:read` |
| `GET` | `/api/v1/role/{id}` | Get role by ID | `role:read` |
| `GET` | `/api/v1/role/{id}/permissions` | Get permissions assigned to role | `role:read` |
| `PUT` | `/api/v1/role/{id}/permissions` | Assign permission IDs to role | `role:update` |
| `POST` | `/api/v1/role` | Create role | `role:create` |
| `PUT` | `/api/v1/role/{id}` | Update role | `role:update` |
| `DELETE` | `/api/v1/role/{id}` | Delete role | `role:delete` |
| `GET` | `/api/v1/permission` | List all system permissions | `permission:read` |
| `GET` | `/api/v1/permission-group` | List permission categories / groups | `permission:read` |

---

### 12. Analytics & Reporting Views (`/api/v1/reports/**`)

High-performance SQL-view powered analytics and financial reports. All endpoints require `report:read` permission.

| Endpoint | Method | Query Parameters | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/reports/sales` | `GET` | `startDate`, `endDate`, `storeId`, `customerId`, `userId`, `saleStatus`, `paymentStatus` | **Sales Summary Report**: Total sales, discounts, net paid, gross cost price, and net profit. |
| `/api/v1/reports/sales-items` | `GET` | `startDate`, `endDate`, `storeId`, `productId`, `page`, `size` | **Sales Item Detail Report**: Paginated item-level sales, quantities, unit costs, and profit per item. |
| `/api/v1/reports/purchases` | `GET` | `startDate`, `endDate`, `storeId`, `supplierId`, `paymentStatus`, `status` | **Purchase Summary Report**: Aggregated purchase expenditures, discounts, and outstanding due amounts. |
| `/api/v1/reports/purchase-items` | `GET` | `startDate`, `endDate`, `storeId`, `supplierId`, `productId`, `page`, `size` | **Purchase Items Report**: Detailed line items from supplier purchase orders. |
| `/api/v1/reports/payments/sales` | `GET` | `startDate`, `endDate`, `storeId`, `paymentMethod`, `bankId` | **Sale Payments Summary**: Total received, returned change, net receipts across payment channels. |
| `/api/v1/reports/payments/sales-paged` | `GET` | `startDate`, `endDate`, `storeId`, `page`, `size` | **Sale Payments Paged Report**: Paginated list of customer receipts and multi-bank lines. |
| `/api/v1/reports/payments/purchases` | `GET` | `startDate`, `endDate`, `storeId`, `supplierId` | **Purchase Payments Summary**: Total payments disbursed to suppliers. |
| `/api/v1/reports/payments/purchases-paged` | `GET` | `startDate`, `endDate`, `supplierId`, `page`, `size` | **Purchase Payments Paged Report**: Paginated list of supplier payments. |
| `/api/v1/reports/expenses` | `GET` | `startDate`, `endDate`, `storeId`, `expenseTypeId`, `bankId`, `page`, `size` | **Operating Expenses Report**: Paginated expenses filtered by category and bank account. |
| `/api/v1/reports/profit-loss` | `GET` | `startDate`, `endDate`, `months` (default: 6) | **Profit & Loss Statement**: Revenue vs Cost of Goods Sold (COGS) vs Operating Expenses = Net Profit. |
| `/api/v1/reports/product-serials` | `GET` | `productId`, `storeId`, `status`, `page`, `size` | **Serial Inventory Report**: Status tracking of serialized products across branches. |

---

### 13. File Storage & S3 (`/api/v1/files`)

Handles file uploads, image previews, and downloads backed by S3 or RustFS/MinIO.

| Method | Endpoint | Content-Type | Description | Permissions |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/files/upload-file` | `multipart/form-data` | Upload single file (`bucketName`, `file`) | `file:upload` |
| `POST` | `/api/v1/files/upload-multiple-files` | `multipart/form-data` | Upload multiple files (`bucketName`, `files`) | `file:upload` |
| `GET` | `/api/v1/files/preview-file/{fileName}` | — | Stream image / file preview directly in browser | **Public** |
| `GET` | `/api/v1/files/download-file/{fileName}` | — | Download file with attachment header | `file:read` |
| `GET` | `/api/v1/files` | — | List all files stored in a specific bucket | `file:read` |

---

## 🗄 Database Migrations

Database versioning is managed by **Flyway**:

| Version | Migration Script | Description |
| :--- | :--- | :--- |
| **V1** | [V1__initial_schema.sql](file:///d:/phone/360-backend/src/main/resources/db/migration/V1__initial_schema.sql) | **Complete relational schema** (all 39 tables, foreign keys, and indexes). |
| **V2** | [V2__create_views.sql](file:///d:/phone/360-backend/src/main/resources/db/migration/V2__create_views.sql) | **High-performance SQL views** (`view_sales_report`, `view_sales_items_report`, `view_purchase_report`, `view_purchase_items_report`, `view_sale_payments_report`, `view_purchase_payments_report`). |

---

## 💻 Running Locally

### Prerequisites
- **JDK 21** or later
- **PostgreSQL 15+**
- **Redis 7+**
- **S3 / RustFS / MinIO** (configured in `.env`)

### Build & Run
```bash
# Clone the repository
git clone <repo-url>
cd 360-backend

# Compile and run with Gradle
./gradlew bootRun
```

### Environment Configuration (`.env`)
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db-360s
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123

REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=admin@123

# Object Storage (RustFS / MinIO / S3)
S3_ENDPOINT=http://localhost:9000
S3_ACCESS_KEY=minioadmin
S3_SECRET_KEY=minioadmin
S3_REGION=us-east-1
```
