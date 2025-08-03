# CRM System API Documentation

## Version: 3.0 (Implementation Complete)
## Date: December 2024
## Author: Gokul Annadurai
## Status: ✅ ALL APIs IMPLEMENTED AND TESTED

---

## Table of Contents

1. [Microservices Overview](#microservices-overview)
2. [Authentication Service API](#authentication-service-api)
3. [User Service API](#user-service-api)
4. [Customer Service API](#customer-service-api)
5. [Sales Service API](#sales-service-api)
6. [Error Handling](#error-handling)
7. [Rate Limiting](#rate-limiting)
8. [API Examples](#api-examples)
9. [Swagger Documentation](#swagger-documentation)

---

## Microservices Overview

The CRM system is built using a microservices architecture with four distinct services:

### Service Architecture

| Service | Port | Purpose | Base URL |
|---------|------|---------|----------|
| **Auth Service** | 8081 | Authentication & Authorization | `http://localhost:8081/api/v1` |
| **User Service** | 8082 | User Management | `http://localhost:8082/api/v1` |
| **Customer Service** | 8083 | Customer Management | `http://localhost:8083/api/v1` |
| **Sales Service** | 8084 | Leads & Tasks Management | `http://localhost:8084/api/v1` |

### Authentication

#### JWT Token Authentication

All API endpoints require JWT token authentication except for login endpoints.

**Header Format:**
```
Authorization: Bearer <jwt_token>
```

**Token Format:**
- **Type**: JWT (JSON Web Token)
- **Algorithm**: HS256
- **Expiration**: 24 hours
- **Refresh Token**: 7 days

### Service Communication

- **Inter-Service Communication**: REST APIs with JWT token validation
- **Database**: Each service has its own database schema
- **File Storage**: Centralized file storage for attachments
- **Caching**: Redis for session management and data caching

### API Endpoints Summary

#### Auth Service (Port: 8081)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | User authentication |
| POST | `/auth/refresh` | Refresh JWT token |
| POST | `/auth/logout` | User logout |

#### User Service (Port: 8082)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | Get all users (paginated) |
| GET | `/users/{id}` | Get user by ID |
| POST | `/users` | Create new user |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Delete user |
| GET | `/roles` | Get all roles |
| GET | `/roles/{id}` | Get role by ID |

#### Customer Service (Port: 8083)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customers` | Get all customers (paginated) |
| GET | `/customers/{id}` | Get customer by ID |
| POST | `/customers` | Create new customer |
| PUT | `/customers/{id}` | Update customer |
| DELETE | `/customers/{id}` | Delete customer |
| GET | `/companies` | Get all companies |
| POST | `/companies` | Create new company |

#### Sales Service (Port: 8084)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/leads` | Get all leads (paginated) |
| GET | `/leads/{id}` | Get lead by ID |
| POST | `/leads` | Create new lead |
| PUT | `/leads/{id}` | Update lead |
| DELETE | `/leads/{id}` | Delete lead |
| PATCH | `/leads/{id}/next-stage` | Move lead to next stage |
| PATCH | `/leads/{id}/assign` | Assign lead to user |
| GET | `/tasks` | Get all tasks (paginated) |
| GET | `/tasks/{id}` | Get task by ID |
| POST | `/tasks` | Create new task |
| PUT | `/tasks/{id}` | Update task |
| DELETE | `/tasks/{id}` | Delete task |
| PATCH | `/tasks/{id}/complete` | Mark task as completed |

---

## Authentication Service API

**Base URL:** `http://localhost:8081/api/v1`

### Authentication Endpoints

#### 1. User Login

**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and return JWT token

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@crm.com",
      "firstName": "System",
      "lastName": "Administrator",
      "role": {
        "roleId": 1,
        "roleName": "ADMIN",
        "description": "System Administrator with full access"
      },
      "status": "ACTIVE",
      "lastLoginAt": "2024-12-15T10:30:00Z"
    }
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid credentials",
  "error": "AUTHENTICATION_FAILED"
}
```

#### 2. Refresh Token

**Endpoint:** `POST /auth/refresh`

**Description:** Refresh JWT token using refresh token

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

## User Service API

**Base URL:** `http://localhost:8082/api/v1`

### User Management Endpoints

#### 1. Get All Users

**Endpoint:** `GET /users`

**Description:** Get all users (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: id)
- `direction` (optional): Sort direction (asc/desc, default: asc)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@crm.com",
        "firstName": "System",
        "lastName": "Administrator",
        "phone": null,
        "role": {
          "roleId": 1,
          "roleName": "ADMIN",
          "description": "System Administrator with full access"
        },
        "status": "ACTIVE",
        "lastLoginAt": "2024-12-15T10:30:00Z",
        "createdAt": "2024-12-01T00:00:00Z",
        "updatedAt": "2024-12-15T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  }
}
```

#### 2. Get User by ID

**Endpoint:** `GET /users/{userId}`

**Description:** Get user by ID (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@crm.com",
    "firstName": "System",
    "lastName": "Administrator",
    "phone": null,
    "role": {
      "roleId": 1,
      "roleName": "ADMIN",
      "description": "System Administrator with full access"
    },
    "status": "ACTIVE",
    "lastLoginAt": "2024-12-15T10:30:00Z",
    "createdAt": "2024-12-01T00:00:00Z",
    "updatedAt": "2024-12-15T10:30:00Z"
  }
}
```

#### 5. Create User

**Endpoint:** `POST /users`

**Description:** Create new user (Admin only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "newuser",
  "email": "newuser@crm.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "roleId": 3
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 5,
    "username": "newuser",
    "email": "newuser@crm.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "role": {
      "roleId": 3,
      "roleName": "SALES_REPRESENTATIVE",
      "description": "Sales Representative with customer access"
    },
    "status": "ACTIVE",
    "createdAt": "2024-12-15T11:00:00Z",
    "updatedAt": "2024-12-15T11:00:00Z"
  }
}
```

#### 6. Update User

**Endpoint:** `PUT /users/{userId}`

**Description:** Update user (Admin only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "phone": "+1234567891",
  "roleId": 2,
  "status": "ACTIVE"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": 5,
    "username": "newuser",
    "email": "newuser@crm.com",
    "firstName": "John Updated",
    "lastName": "Doe Updated",
    "phone": "+1234567891",
    "role": {
      "roleId": 2,
      "roleName": "SALES_MANAGER",
      "description": "Sales Manager with team management access"
    },
    "status": "ACTIVE",
    "updatedAt": "2024-12-15T11:30:00Z"
  }
}
```

#### 7. Delete User

**Endpoint:** `DELETE /users/{userId}`

**Description:** Delete user (Admin only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User deleted successfully"
}
```

### Role Management Endpoints

#### 8. Get All Roles

**Endpoint:** `GET /roles`

**Description:** Get all roles (Admin only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Roles retrieved successfully",
  "data": [
    {
      "roleId": 1,
      "roleName": "ADMIN",
      "description": "System Administrator with full access",
      "permissions": {
        "all": true
      },
      "createdAt": "2024-12-01T00:00:00Z",
      "updatedAt": "2024-12-01T00:00:00Z"
    },
    {
      "roleId": 2,
      "roleName": "SALES_MANAGER",
      "description": "Sales Manager with team management access",
      "permissions": {
        "sales": true,
        "reports": true,
        "team": true
      },
      "createdAt": "2024-12-01T00:00:00Z",
      "updatedAt": "2024-12-01T00:00:00Z"
    }
  ]
}
```

---

---

## Customer Service API

**Base URL:** `http://localhost:8083/api/v1`

### Customer Management Endpoints

#### 1. Create Customer

**Endpoint:** `POST /customers`

**Description:** Create new customer

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Smith",
  "email": "john.smith@example.com",
  "phone": "+1234567890",
  "company": "TechCorp Inc",
  "source": "Website"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Customer created successfully",
  "data": {
    "id": 1,
    "name": "John Smith",
    "email": "john.smith@example.com",
    "phone": "+1234567890",
    "company": "TechCorp Inc",
    "source": "Website",
    "createdBy": 1,
    "isActive": true,
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 2. Get All Customers

**Endpoint:** `GET /customers`

**Description:** Get all customers with pagination

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: id)
- `direction` (optional): Sort direction (asc/desc, default: asc)
- `isActive` (optional): Filter by active status
- `company` (optional): Filter by company name
- `source` (optional): Filter by source

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Customers retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "John Smith",
        "email": "john.smith@example.com",
        "phone": "+1234567890",
        "company": "TechCorp Inc",
        "source": "Website",
        "createdBy": 1,
        "isActive": true,
        "createdAt": "2024-12-15T12:00:00Z",
        "updatedAt": "2024-12-15T12:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  }
}
```

#### 3. Get Customer by ID

**Endpoint:** `GET /customers/{customerId}`

**Description:** Get customer by ID

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Customer retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Smith",
    "email": "john.smith@example.com",
    "phone": "+1234567890",
    "company": "TechCorp Inc",
    "source": "Website",
    "createdBy": 1,
    "isActive": true,
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 4. Get Customer by Email

**Endpoint:** `GET /customers/email/{email}`

**Description:** Get customer by email address

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Customer retrieved successfully",
  "data": {
    "id": 1,
    "name": "John Smith",
    "email": "john.smith@example.com",
    "phone": "+1234567890",
    "company": "TechCorp Inc",
    "source": "Website",
    "createdBy": 1,
    "isActive": true,
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 5. Search Customers

**Endpoint:** `GET /customers/search`

**Description:** Search customers by name, email, or company

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `query` (required): Search query
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "John Smith",
        "email": "john.smith@example.com",
        "phone": "+1234567890",
        "company": "TechCorp Inc",
        "source": "Website",
        "createdBy": 1,
        "isActive": true,
        "createdAt": "2024-12-15T12:00:00Z",
        "updatedAt": "2024-12-15T12:00:00Z"
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### 6. Update Customer

**Endpoint:** `PUT /customers/{customerId}`

**Description:** Update customer

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Smith Updated",
  "email": "john.updated@example.com",
  "phone": "+1234567891",
  "company": "TechCorp Inc Updated",
  "source": "Referral",
  "isActive": true
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Customer updated successfully",
  "data": {
    "id": 1,
    "name": "John Smith Updated",
    "email": "john.updated@example.com",
    "phone": "+1234567891",
    "company": "TechCorp Inc Updated",
    "source": "Referral",
    "createdBy": 1,
    "isActive": true,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 7. Change Customer Active Status

**Endpoint:** `PATCH /customers/{customerId}/status`

**Description:** Change customer active status (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "isActive": false
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Customer status updated successfully",
  "data": {
    "id": 1,
    "isActive": false,
    "updatedAt": "2024-12-15T12:35:00Z"
  }
}
```

#### 8. Delete Customer

**Endpoint:** `DELETE /customers/{customerId}`

**Description:** Delete customer (Admin only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Customer deleted successfully"
}
```

#### 9. Get Customer Statistics

**Endpoint:** `GET /customers/stats`

**Description:** Get customer statistics (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Statistics retrieved successfully",
  "data": {
    "totalCustomers": 100,
    "activeCustomers": 85,
    "inactiveCustomers": 15,
    "customersBySource": {
      "Website": 40,
      "Referral": 30,
      "Cold Call": 20,
      "Other": 10
    }
  }
}
```

### Interaction History Endpoints

#### 10. Create Interaction History

**Endpoint:** `POST /interaction-history`

**Description:** Create new interaction history record

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "customerId": 1,
  "interactionType": "Call",
  "notes": "Initial contact made. Customer interested in our solution.",
  "interactionDate": "2024-12-15T14:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Interaction history created successfully",
  "data": {
    "id": 1,
    "customerId": 1,
    "userId": 1,
    "interactionType": "Call",
    "notes": "Initial contact made. Customer interested in our solution.",
    "interactionDate": "2024-12-15T14:00:00Z",
    "createdAt": "2024-12-15T14:00:00Z",
    "updatedAt": "2024-12-15T14:00:00Z"
  }
}
```

#### 11. Get Interaction History by Customer

**Endpoint:** `GET /interaction-history/customer/{customerId}`

**Description:** Get interaction history for a specific customer

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Interaction history retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "customerId": 1,
        "userId": 1,
        "interactionType": "Call",
        "notes": "Initial contact made. Customer interested in our solution.",
        "interactionDate": "2024-12-15T14:00:00Z",
        "createdAt": "2024-12-15T14:00:00Z",
        "updatedAt": "2024-12-15T14:00:00Z"
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### 12. Get Recent Interaction History

**Endpoint:** `GET /interaction-history/customer/{customerId}/recent`

**Description:** Get recent interaction history for a customer (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `limit` (optional): Number of records (default: 10)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Recent interaction history retrieved successfully",
  "data": [
    {
      "id": 1,
      "customerId": 1,
      "userId": 1,
      "interactionType": "Call",
      "notes": "Initial contact made. Customer interested in our solution.",
      "interactionDate": "2024-12-15T14:00:00Z",
      "createdAt": "2024-12-15T14:00:00Z",
      "updatedAt": "2024-12-15T14:00:00Z"
    }
  ]
}
```

#### 13. Update Interaction History

**Endpoint:** `PUT /interaction-history/{interactionId}`

**Description:** Update interaction history (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "interactionType": "Meeting",
  "notes": "Updated notes for the interaction",
  "interactionDate": "2024-12-15T15:00:00Z"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Interaction history updated successfully",
  "data": {
    "id": 1,
    "customerId": 1,
    "userId": 1,
    "interactionType": "Meeting",
    "notes": "Updated notes for the interaction",
    "interactionDate": "2024-12-15T15:00:00Z",
    "updatedAt": "2024-12-15T15:00:00Z"
  }
}
```

#### 14. Delete Interaction History

**Endpoint:** `DELETE /interaction-history/{interactionId}`

**Description:** Delete interaction history (Admin only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Interaction history deleted successfully"
}
```

---

---

## Sales Service API

**Base URL:** `http://localhost:8084/api/v1`

### Lead Management Endpoints

#### 1. Create Lead

**Endpoint:** `POST /leads/update`

**Description:** Create new lead

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data
```

**Request Body (multipart/form-data):**
```
title: Enterprise CRM Implementation
customerId: 1
assignedTo: 2
status: Lead
currentStageId: 1
expectedCloseDate: 2024-12-31
value: 50000.00
additionalNotes: Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.
attachments: [file1.pdf, file2.docx] (multiple files, optional)
```

**Note:** The `attachments` field should contain the actual files to be uploaded, not JSON data. File uploads are optional with a maximum of 3 documents per Lead.

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Lead created successfully",
  "data": {
    "id": 1,
    "title": "Enterprise CRM Implementation",
    "customerId": 1,
    "assignedTo": 2,
    "status": "Lead",
    "currentStageId": 1,
    "expectedCloseDate": "2024-12-31",
    "value": 50000.00,
    "additionalNotes": "Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.",
    "attachments": [
      {
        "url": "https://example.com/files/proposal.pdf",
        "fileName": "proposal.pdf",
        "fileType": ".pdf",
        "displayOrder": 1
      }
    ],
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 2. Get All Leads

**Endpoint:** `GET /leads`

**Description:** Get all leads with pagination

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: id)
- `direction` (optional): Sort direction (asc/desc, default: asc)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Leads retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Enterprise CRM Implementation",
        "customerId": 1,
        "assignedTo": 2,
        "status": "Lead",
        "currentStageId": 1,
        "expectedCloseDate": "2024-12-31",
        "value": 50000.00,
        "additionalNotes": "Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.",
        "attachments": [
          {
            "url": "https://example.com/files/proposal.pdf",
            "fileName": "proposal.pdf",
            "fileType": ".pdf",
            "displayOrder": 1
          }
        ],
        "createdAt": "2024-12-15T12:00:00Z",
        "updatedAt": "2024-12-15T12:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  }
}
```

#### 3. Get Lead by ID

**Endpoint:** `GET /leads/{id}`

**Description:** Get lead by ID

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead retrieved successfully",
  "data": {
    "id": 1,
    "title": "Enterprise CRM Implementation",
    "customerId": 1,
    "assignedTo": 2,
    "status": "Lead",
    "currentStageId": 1,
    "expectedCloseDate": "2024-12-31",
    "value": 50000.00,
    "additionalNotes": "Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.",
    "attachments": [
      {
        "url": "https://example.com/files/proposal.pdf",
        "fileName": "proposal.pdf",
        "fileType": ".pdf",
        "displayOrder": 1
      }
    ],
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 5. Delete Lead

**Endpoint:** `DELETE /leads/{id}`

**Description:** Delete lead (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead deleted successfully"
}
```

#### 6. Get Leads by Customer

**Endpoint:** `GET /leads/customer/{customerId}`

**Description:** Get leads for a specific customer

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Leads retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Enterprise CRM Implementation",
      "customerId": 1,
      "assignedTo": 2,
      "status": "Lead",
      "currentStageId": 1,
      "expectedCloseDate": "2024-12-31",
      "value": 50000.00,
      "additionalNotes": "Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.",
      "attachments": [
        {
          "url": "https://example.com/files/proposal.pdf",
          "fileName": "proposal.pdf",
          "fileType": ".pdf",
          "displayOrder": 1
        }
      ],
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 7. Get Leads by Assigned User

**Endpoint:** `GET /leads/assigned/{assignedTo}`

**Description:** Get leads assigned to a specific user

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Leads retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Enterprise CRM Implementation",
      "customerId": 1,
      "assignedTo": 2,
      "status": "Lead",
      "currentStageId": 1,
      "expectedCloseDate": "2024-12-31",
      "value": 50000.00,
      "additionalNotes": "Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.",
      "attachments": [
        {
          "url": "https://example.com/files/proposal.pdf",
          "fileName": "proposal.pdf",
          "fileType": ".pdf",
          "displayOrder": 1
        }
      ],
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 8. Get Leads by Status

**Endpoint:** `GET /leads/status/{status}`

**Description:** Get leads by status

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Leads retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Enterprise CRM Implementation",
      "customerId": 1,
      "assignedTo": 2,
      "status": "Lead",
      "currentStageId": 1,
      "expectedCloseDate": "2024-12-31",
      "value": 50000.00,
      "additionalNotes": "Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.",
      "attachments": [
        {
          "url": "https://example.com/files/proposal.pdf",
          "fileName": "proposal.pdf",
          "fileType": ".pdf",
          "displayOrder": 1
        }
      ],
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 9. Get Leads by Current Stage

**Endpoint:** `GET /leads/stage/{currentStageId}`

**Description:** Get leads by current stage

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Leads retrieved successfully",
  "data": [
    {
      "id": 1,
      "customerId": 1,
      "assignedTo": 2,
      "status": "Lead",
      "currentStageId": 1,
      "expectedCloseDate": "2024-12-31",
      "value": 50000.00,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 10. Get Overdue Leads

**Endpoint:** `GET /leads/overdue`

**Description:** Get leads that are overdue

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Overdue leads retrieved successfully",
  "data": [
    {
      "id": 1,
      "customerId": 1,
      "assignedTo": 2,
      "status": "Lead",
      "currentStageId": 1,
      "expectedCloseDate": "2024-12-10",
      "value": 50000.00,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 11. Get Leads Due Soon

**Endpoint:** `GET /leads/due-soon`

**Description:** Get leads due soon (within specified days)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `days` (optional): Number of days (default: 7)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Leads due soon retrieved successfully",
  "data": [
    {
      "id": 1,
      "customerId": 1,
      "assignedTo": 2,
      "status": "Lead",
      "currentStageId": 1,
      "expectedCloseDate": "2024-12-20",
      "value": 50000.00,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 12. Move Lead to Next Stage

**Endpoint:** `PATCH /leads/{id}/next-stage`

**Description:** Move lead to the next stage in the pipeline

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead moved to next stage successfully",
  "data": {
    "id": 1,
    "customerId": 1,
    "assignedTo": 2,
    "status": "Qualified",
    "currentStageId": 2,
    "expectedCloseDate": "2024-12-31",
    "value": 50000.00,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 13. Move Lead to Previous Stage

**Endpoint:** `PATCH /leads/{id}/previous-stage`

**Description:** Move lead to the previous stage in the pipeline

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead moved to previous stage successfully",
  "data": {
    "id": 1,
    "customerId": 1,
    "assignedTo": 2,
    "status": "Lead",
    "currentStageId": 1,
    "expectedCloseDate": "2024-12-31",
    "value": 50000.00,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 14. Assign Lead

**Endpoint:** `PATCH /leads/{id}/assign`

**Description:** Assign lead to a user (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `assignedTo` (required): User ID to assign the lead to

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead assigned successfully",
  "data": {
    "id": 1,
    "customerId": 1,
    "assignedTo": 3,
    "status": "Lead",
    "currentStageId": 1,
    "expectedCloseDate": "2024-12-31",
    "value": 50000.00,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 15. Get Lead Statistics

**Endpoint:** `GET /leads/stats`

**Description:** Get lead statistics (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead statistics retrieved successfully",
  "data": {
    "totalLeads": 100,
    "leadsByStatus": {
      "Lead": 30,
      "Qualified": 25,
      "Proposal": 20,
      "Closed Won": 15,
      "Closed Lost": 10
    },
    "leadsByStage": {
      "Stage 1": 30,
      "Stage 2": 25,
      "Stage 3": 20,
      "Stage 4": 15,
      "Stage 5": 10
    },
    "totalValue": 5000000.00,
    "averageValue": 50000.00
  }
}
```

#### 16. Get Pipeline Statistics

**Endpoint:** `GET /leads/pipeline/stats`

**Description:** Get pipeline statistics (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Pipeline statistics retrieved successfully",
  "data": {
    "pipelineStages": [
      {
        "stageId": 1,
        "stageName": "Lead",
        "leadCount": 30,
        "totalValue": 1500000.00
      },
      {
        "stageId": 2,
        "stageName": "Qualified",
        "leadCount": 25,
        "totalValue": 1250000.00
      }
    ],
    "conversionRates": {
      "leadToQualified": 83.33,
      "qualifiedToProposal": 80.00,
      "proposalToClosed": 75.00
    }
  }
}
```

### Lead Stage Management Endpoints

#### 17. Create Lead Stage

**Endpoint:** `POST /lead-stages`

**Description:** Create new lead stage (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Qualified",
  "position": 2
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Lead stage created successfully",
  "data": {
    "id": 2,
    "name": "Qualified",
    "position": 2,
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 18. Get All Lead Stages

**Endpoint:** `GET /lead-stages`

**Description:** Get all lead stages

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead stages retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Lead",
      "position": 1,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    },
    {
      "id": 2,
      "name": "Qualified",
      "position": 2,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 19. Get Lead Stage by ID

**Endpoint:** `GET /lead-stages/{id}`

**Description:** Get lead stage by ID

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead stage retrieved successfully",
  "data": {
    "id": 1,
    "name": "Lead",
    "position": 1,
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 20. Update Lead Stage

**Endpoint:** `PUT /lead-stages/{id}`

**Description:** Update lead stage (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Qualified Lead",
  "position": 2
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead stage updated successfully",
  "data": {
    "id": 2,
    "name": "Qualified Lead",
    "position": 2,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 21. Delete Lead Stage

**Endpoint:** `DELETE /lead-stages/{id}`

**Description:** Delete lead stage (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Lead stage deleted successfully"
}
```

### Task Management Endpoints

#### 22. Create Task

**Endpoint:** `POST /tasks/update`

**Description:** Create new task

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data
```

**Request Body (multipart/form-data):**
```
leadId: 1
customerId: 1
assignedTo: 2
taskTypeId: 1
title: Product Demo for TechCorp
description: Schedule and conduct comprehensive product demonstration for John Smith and his team
dueDate: 2024-12-20T14:00:00Z
isStarted: true
attachments: [demo-script.pdf, presentation.pptx] (multiple files)
```

**Note:** The `attachments` field should contain the actual files to be uploaded, not JSON data.

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 1,
    "leadId": 1,
    "customerId": 1,
    "assignedTo": 2,
    "taskTypeId": 1,
    "title": "Product Demo for TechCorp",
    "description": "Schedule and conduct comprehensive product demonstration for John Smith and his team",
    "dueDate": "2024-12-20T14:00:00Z",
    "isCompleted": false,
    "isStarted": true,
    "attachments": [
      {
        "url": "https://example.com/files/demo-script.pdf",
        "fileName": "demo-script.pdf",
        "fileType": ".pdf",
        "displayOrder": 1
      }
    ],
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 23. Get All Tasks

**Endpoint:** `GET /tasks`

**Description:** Get all tasks with pagination

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: id)
- `direction` (optional): Sort direction (asc/desc, default: asc)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "leadId": 1,
        "customerId": 1,
        "assignedTo": 2,
        "taskTypeId": 1,
        "title": "Follow up call",
        "description": "Call customer to discuss proposal",
        "dueDate": "2024-12-20T14:00:00Z",
        "isCompleted": false,
        "isStarted": true,
        "attachments": [
          {
            "url": "https://example.com/files/proposal.pdf",
            "fileName": "proposal.pdf",
            "fileType": "application/pdf",
            "displayOrder": 1
          }
        ],
        "createdAt": "2024-12-15T12:00:00Z",
        "updatedAt": "2024-12-15T12:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1
  }
}
```

#### 24. Get Task by ID

**Endpoint:** `GET /tasks/{id}`

**Description:** Get task by ID

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task retrieved successfully",
  "data": {
    "id": 1,
    "leadId": 1,
    "customerId": 1,
    "assignedTo": 2,
    "taskTypeId": 1,
    "title": "Follow up call",
    "description": "Call customer to discuss proposal",
    "dueDate": "2024-12-20T14:00:00Z",
    "isCompleted": false,
    "isStarted": true,
    "attachments": [
      {
        "url": "https://example.com/files/proposal.pdf",
        "fileName": "proposal.pdf",
        "fileType": "application/pdf",
        "displayOrder": 1
      }
    ],
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 29. Get Tasks by Assigned User

**Endpoint:** `GET /tasks/assigned/{assignedTo}`

**Description:** Get tasks assigned to a specific user

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": [
    {
      "id": 1,
      "leadId": 1,
      "customerId": 1,
      "assignedTo": 2,
      "taskTypeId": 1,
      "title": "Follow up call",
      "description": "Call customer to discuss proposal",
      "dueDate": "2024-12-20T14:00:00Z",
      "isCompleted": false,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 30. Get Tasks by Type

**Endpoint:** `GET /tasks/type/{taskTypeId}`

**Description:** Get tasks by task type

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": [
    {
      "id": 1,
      "leadId": 1,
      "customerId": 1,
      "assignedTo": 2,
      "taskTypeId": 1,
      "title": "Follow up call",
      "description": "Call customer to discuss proposal",
      "dueDate": "2024-12-20T14:00:00Z",
      "isCompleted": false,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 31. Get Tasks by Completion Status

**Endpoint:** `GET /tasks/status/{isCompleted}`

**Description:** Get tasks by completion status

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": [
    {
      "id": 1,
      "leadId": 1,
      "customerId": 1,
      "assignedTo": 2,
      "taskTypeId": 1,
      "title": "Follow up call",
      "description": "Call customer to discuss proposal",
      "dueDate": "2024-12-20T14:00:00Z",
      "isCompleted": false,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 32. Get Overdue Tasks

**Endpoint:** `GET /tasks/overdue`

**Description:** Get tasks that are overdue

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Overdue tasks retrieved successfully",
  "data": [
    {
      "id": 1,
      "leadId": 1,
      "customerId": 1,
      "assignedTo": 2,
      "taskTypeId": 1,
      "title": "Follow up call",
      "description": "Call customer to discuss proposal",
      "dueDate": "2024-12-10T14:00:00Z",
      "isCompleted": false,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 33. Get Tasks Due Soon

**Endpoint:** `GET /tasks/due-soon`

**Description:** Get tasks due soon (within specified hours)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `hours` (optional): Number of hours (default: 24)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks due soon retrieved successfully",
  "data": [
    {
      "id": 1,
      "leadId": 1,
      "customerId": 1,
      "assignedTo": 2,
      "taskTypeId": 1,
      "title": "Follow up call",
      "description": "Call customer to discuss proposal",
      "dueDate": "2024-12-16T14:00:00Z",
      "isCompleted": false,
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 34. Mark Task as Completed

**Endpoint:** `PATCH /tasks/{id}/complete`

**Description:** Mark task as completed

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task marked as completed successfully",
  "data": {
    "id": 1,
    "leadId": 1,
    "customerId": 1,
    "assignedTo": 2,
    "taskTypeId": 1,
    "title": "Follow up call",
    "description": "Call customer to discuss proposal",
    "dueDate": "2024-12-20T14:00:00Z",
    "isCompleted": true,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 35. Mark Task as Incomplete

**Endpoint:** `PATCH /tasks/{id}/incomplete`

**Description:** Mark task as incomplete

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task marked as incomplete successfully",
  "data": {
    "id": 1,
    "leadId": 1,
    "customerId": 1,
    "assignedTo": 2,
    "taskTypeId": 1,
    "title": "Follow up call",
    "description": "Call customer to discuss proposal",
    "dueDate": "2024-12-20T14:00:00Z",
    "isCompleted": false,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 36. Assign Task

**Endpoint:** `PATCH /tasks/{id}/assign`

**Description:** Assign task to a user (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `assignedTo` (required): User ID to assign the task to

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task assigned successfully",
  "data": {
    "id": 1,
    "leadId": 1,
    "customerId": 1,
    "assignedTo": 3,
    "taskTypeId": 1,
    "title": "Follow up call",
    "description": "Call customer to discuss proposal",
    "dueDate": "2024-12-20T14:00:00Z",
    "isCompleted": false,
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 37. Get Task Statistics

**Endpoint:** `GET /tasks/stats`

**Description:** Get task statistics (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task statistics retrieved successfully",
  "data": {
    "totalTasks": 100,
    "completedTasks": 75,
    "pendingTasks": 25,
    "overdueTasks": 10,
    "tasksByType": {
      "Follow-up": 30,
      "Call": 25,
      "Demo": 20,
      "Meeting": 15,
      "Email": 10
    },
    "completionRate": 75.0
  }
}
```

### Task Type Management Endpoints

#### 38. Create Task Type

**Endpoint:** `POST /task-types`

**Description:** Create new task type (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Demo"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Task type created successfully",
  "data": {
    "id": 3,
    "name": "Demo",
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 39. Get All Task Types

**Endpoint:** `GET /task-types`

**Description:** Get all task types

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Follow-up",
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    },
    {
      "id": 2,
      "name": "Call",
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    },
    {
      "id": 3,
      "name": "Demo",
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 40. Get Task Type by ID

**Endpoint:** `GET /task-types/{id}`

**Description:** Get task type by ID

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task type retrieved successfully",
  "data": {
    "id": 1,
    "name": "Follow-up",
    "createdAt": "2024-12-15T12:00:00Z",
    "updatedAt": "2024-12-15T12:00:00Z"
  }
}
```

#### 41. Update Task Type

**Endpoint:** `PUT /task-types/{id}`

**Description:** Update task type (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Follow-up Call"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task type updated successfully",
  "data": {
    "id": 1,
    "name": "Follow-up Call",
    "updatedAt": "2024-12-15T12:30:00Z"
  }
}
```

#### 42. Delete Task Type

**Endpoint:** `DELETE /task-types/{id}`

**Description:** Delete task type (Admin/Sales Manager only)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task type deleted successfully"
}
```

#### 43. Search Task Types

**Endpoint:** `GET /task-types/search`

**Description:** Search task types by name

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `name` (required): Search query

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task types search completed successfully",
  "data": [
    {
      "id": 1,
      "name": "Follow-up",
      "createdAt": "2024-12-15T12:00:00Z",
      "updatedAt": "2024-12-15T12:00:00Z"
    }
  ]
}
```

#### 44. Check Task Type Existence

**Endpoint:** `GET /task-types/exists/{name}`

**Description:** Check if task type exists by name

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task type existence checked successfully",
  "data": true
}
```

---

## Error Handling

### Standard Error Response Format

All error responses follow this format:

```json
{
  "success": false,
  "message": "Error description",
  "error": "ERROR_CODE",
  "timestamp": "2024-12-15T12:00:00Z",
  "path": "/api/v1/customers",
  "details": {
    "field": "Additional error details"
  }
}
```

### Common Error Codes

| HTTP Status | Error Code | Description |
|-------------|------------|-------------|
| 400 | VALIDATION_ERROR | Request validation failed |
| 401 | UNAUTHORIZED | Authentication required |
| 403 | FORBIDDEN | Insufficient permissions |
| 404 | NOT_FOUND | Resource not found |
| 409 | CONFLICT | Resource conflict (e.g., duplicate email) |
| 422 | UNPROCESSABLE_ENTITY | Business logic validation failed |
| 500 | INTERNAL_SERVER_ERROR | Internal server error |

### Validation Error Example

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Validation failed",
  "error": "VALIDATION_ERROR",
  "timestamp": "2024-12-15T12:00:00Z",
  "path": "/api/v1/customers",
  "details": {
    "email": "Invalid email format",
    "phone": "Phone number must be valid"
  }
}
```

---

## Rate Limiting

### Rate Limit Headers

All API responses include rate limiting headers:

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640000000
```

### Rate Limits

- **Authentication endpoints**: 10 requests per minute
- **User management**: 100 requests per minute
- **Customer management**: 200 requests per minute
- **Interaction history**: 300 requests per minute
- **Lead management**: 200 requests per minute
- **Task management**: 300 requests per minute
- **Lead stage management**: 50 requests per minute
- **Task type management**: 50 requests per minute

### Rate Limit Exceeded Response

**Response (429 Too Many Requests):**
```json
{
  "success": false,
  "message": "Rate limit exceeded",
  "error": "RATE_LIMIT_EXCEEDED",
  "retryAfter": 60
}
```

---

## Examples

### Complete Customer Creation Flow

1. **Login to get JWT token:**
```bash
curl -X POST http://localhost:8081/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

2. **Create customer using JWT token:**
```bash
curl -X POST http://localhost:8082/api/v1/customers \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane.doe@example.com",
    "phone": "+1234567890",
    "company": "Example Corp",
    "source": "Website"
  }'
```

3. **Add interaction history:**
```bash
curl -X POST http://localhost:8082/api/v1/interaction-history \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "interactionType": "Call",
    "notes": "Initial contact made",
    "interactionDate": "2024-12-15T16:00:00Z"
  }'
```

### Search and Filter Examples

**Search customers by name:**
```bash
curl -X GET "http://localhost:8082/api/v1/customers/search?query=John" \
  -H "Authorization: Bearer <jwt_token>"
```

**Get customers by company:**
```bash
curl -X GET "http://localhost:8082/api/v1/customers?company=TechCorp" \
  -H "Authorization: Bearer <jwt_token>"
```

**Get active customers only:**
```bash
curl -X GET "http://localhost:8082/api/v1/customers?isActive=true" \
  -H "Authorization: Bearer <jwt_token>"
```

### Complete Sales Pipeline Flow

1. **Create a lead:**
```bash
curl -X POST http://localhost:8083/api/v1/leads \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "assignedTo": 2,
    "status": "Lead",
    "currentStageId": 1,
    "expectedCloseDate": "2024-12-31",
    "value": 50000.00
  }'
```

2. **Create a task for the lead:**
```bash
curl -X POST http://localhost:8083/api/v1/tasks \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "leadId": 1,
    "customerId": 1,
    "assignedTo": 2,
    "taskTypeId": 1,
    "title": "Initial contact call",
    "description": "Call customer to introduce our solution",
    "dueDate": "2024-12-20T14:00:00Z"
  }'
```

3. **Move lead to next stage:**
```bash
curl -X PATCH http://localhost:8084/api/v1/leads/1/next-stage \
  -H "Authorization: Bearer <jwt_token>"
```

4. **Mark task as completed:**
```bash
curl -X PATCH http://localhost:8084/api/v1/tasks/1/complete \
  -H "Authorization: Bearer <jwt_token>"
```

5. **Get lead statistics:**
```bash
curl -X GET http://localhost:8084/api/v1/leads/stats \
  -H "Authorization: Bearer <jwt_token>"
```

### Sales Pipeline Management Examples

**Get leads by stage:**
```bash
curl -X GET "http://localhost:8084/api/v1/leads/stage/1" \
  -H "Authorization: Bearer <jwt_token>"
```

**Get overdue leads:**
```bash
curl -X GET "http://localhost:8084/api/v1/leads/overdue" \
  -H "Authorization: Bearer <jwt_token>"
```

**Get tasks due soon:**
```bash
curl -X GET "http://localhost:8084/api/v1/tasks/due-soon?hours=24" \
  -H "Authorization: Bearer <jwt_token>"
```

**Assign lead to different user:**
```bash
curl -X PATCH "http://localhost:8084/api/v1/leads/1/assign?assignedTo=3" \
  -H "Authorization: Bearer <jwt_token>"
```

**Get pipeline statistics:**
```bash
curl -X GET "http://localhost:8084/api/v1/leads/pipeline/stats" \
  -H "Authorization: Bearer <jwt_token>"
```

---

## Health Check Endpoints

### Auth Service Health
- **Endpoint:** `GET http://localhost:8081/actuator/health`
- **Description:** Check authentication service health status

### User Service Health
- **Endpoint:** `GET http://localhost:8082/actuator/health`
- **Description:** Check user service health status

### Customer Service Health
- **Endpoint:** `GET http://localhost:8083/actuator/health`
- **Description:** Check customer service health status

### Sales Service Health
- **Endpoint:** `GET http://localhost:8084/actuator/health`
- **Description:** Check sales service health status

### Database Health
- **Endpoint:** `GET http://localhost:8081/actuator/health/db`
- **Description:** Check database connectivity

---

## Swagger Documentation

### Auth Service Swagger
- **URL:** `http://localhost:8081/swagger-ui.html`
- **API Docs:** `http://localhost:8081/v3/api-docs`
- **Description:** Authentication and authorization API documentation

### User Service Swagger
- **URL:** `http://localhost:8082/swagger-ui.html`
- **API Docs:** `http://localhost:8082/v3/api-docs`
- **Description:** User management API documentation

### Customer Service Swagger
- **URL:** `http://localhost:8083/swagger-ui.html`
- **API Docs:** `http://localhost:8083/v3/api-docs`
- **Description:** Customer management API documentation

### Sales Service Swagger
- **URL:** `http://localhost:8084/swagger-ui.html`
- **API Docs:** `http://localhost:8084/v3/api-docs`
- **Description:** Sales pipeline, leads, and tasks API documentation

---

## Support

For API support and questions:
- **Email:** support@crm.com
- **Documentation:** https://docs.crm.com/api
- **Status Page:** https://status.crm.com 