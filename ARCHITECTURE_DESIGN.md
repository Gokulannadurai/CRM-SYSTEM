# CRM System Architecture Design

## Version: 3.0 (Implementation Complete)
## Date: December 2024
## Author: Gokul Annadurai
## Status: ✅ ARCHITECTURE IMPLEMENTED AND DEPLOYED

---

## 1. System Architecture Overview

### 1.1 Architecture Pattern
The CRM system follows a **Microservices Architecture** pattern with the following characteristics:
- **Frontend**: React.js Single Page Application (SPA)
- **Backend**: Java Spring Boot microservices
- **Database**: PostgreSQL with read replicas
- **Message Queue**: Apache Kafka for asynchronous processing
- **Caching**: Redis for session and data caching
- **API Gateway**: Spring Cloud Gateway for routing and security

### 1.2 High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│  React.js SPA (Web) │ React Native (Mobile) │ Third-party Apps  │
└─────────────────────┴─────────────────────────┴─────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY LAYER                          │
├─────────────────────────────────────────────────────────────────┤
│  Spring Cloud Gateway (Load Balancer, Rate Limiting, Auth)      │
└─────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌───────────────────────────────────────────────────────────────┐
│                    MICROSERVICES LAYER                        │
├─────────────────┬─────────────────┬─────────────────┬─────────┤
│  Auth           │  User           │  Customer       │  Sales  │
│  Service        │  Service        │  Service        │ Service │
│  (Port: 8081)   │  (Port: 8082)   │  (Port: 8083)   │(Port:8084)│
├─────────────────┼─────────────────┼─────────────────┼─────────┤
│  • JWT Auth     │  • User Mgmt    │  • Customer     │ • Leads │
│  • RBAC         │  • Role Mgmt    │    Profiles     │ • Tasks │
│  • Security     │  • Permissions  │  • Companies    │ • Pipeline│
│  • Sessions     │  • Validation   │  • Validation   │• Reports│
└─────────────────┴─────────────────┴─────────────────┴─────────┘
                                    │
                                    ▼
┌───────────────────────────────────────────────────────────────┐
│                     DATA LAYER                                │
├─────────────────┬─────────────────┬─────────────────┬─────────┤
│  PostgreSQL     │  Redis          │  File Storage   │  Logs   │
│  (Primary DB)   │  (Cache)        │  (AWS S3 )      │ (Files) │
├─────────────────┼─────────────────┼─────────────────┼─────────┤
│  • Auth DB      │  • Sessions     │  • Attachments  │  • App  │
│  • User DB      │  • Cache        │  • Documents    │  Logs   │
│  • Customer DB  │  • Temp Data    │  • Images       │  • Audit│
│  • Sales DB     │                 │  • Videos       │  Logs   │
└─────────────────┴─────────────────┴─────────────────┴─────────┘
```

---

## 2. Microservices Architecture

### 2.1 Service Decomposition

#### 2.1.1 Auth Service (Port: 8081)
**Purpose**: Handle authentication and authorization
**Responsibilities**:
- User authentication and login
- JWT token generation and validation
- Role-based access control (RBAC)
- Session management and security
- Password encryption and validation

**Key Components**:
- AuthController
- JwtService
- UserService
- RoleService
- SecurityConfig
- PasswordEncoder

**Database Tables**:
- `users` - User accounts and authentication data
- `roles` - Role definitions and permissions
- `user_roles` - User-role relationships

#### 2.1.2 User Service (Port: 8082)
**Purpose**: Manage user profiles and user-related operations
**Responsibilities**:
- User CRUD operations
- User profile management
- Role assignment and management
- User search and filtering
- User data validation

**Key Components**:
- UserController
- UserService
- UserRepository
- RoleService
- UserValidator
- UserMapper

**Database Tables**:
- `users` - User profiles and information
- `roles` - Role definitions
- `user_roles` - User-role mappings

#### 2.1.3 Customer Service (Port: 8083)
**Purpose**: Manage customer profiles and customer-related data
**Responsibilities**:
- Customer CRUD operations
- Customer search and filtering
- Customer data validation
- Company management
- Customer interaction tracking

**Key Components**:
- CustomerController
- CustomerService
- CustomerRepository
- CompanyService
- CustomerValidator
- CustomerMapper

**Database Tables**:
- `customers` - Customer profiles and information
- `companies` - Company information
- `customer_companies` - Customer-company relationships

#### 2.1.4 Sales Service (Port: 8084)
**Purpose**: Handle sales pipeline, lead management, and task management
**Responsibilities**:
- Lead management and tracking
- Lead stage progression
- Task creation and assignment
- Sales pipeline visualization
- Sales reporting and analytics
- File attachment management

**Key Components**:
- LeadController
- TaskController
- LeadService
- TaskService
- LeadRepository
- TaskRepository
- FileService
- AttachmentService

**Database Tables**:
- `leads` - Sales leads and opportunities
- `lead_stages` - Lead pipeline stages
- `tasks` - Task records and assignments
- `attachments` - File attachments for leads and tasks

#### 2.1.5 Cross-Service Features
**File Management**: Integrated within Sales Service for lead and task attachments
**Reporting**: Basic reporting capabilities within Sales Service for lead and task analytics
**Notifications**: Toast notifications handled by frontend with backend success/error responses
**Search & Filtering**: Implemented across all services with pagination support

---

## 3. Component Architecture

### 3.1 Frontend Architecture (React.js)

#### 3.1.1 Component Structure
```
src/
├── components/
│   ├── common/
│   │   ├── Header/
│   │   ├── Sidebar/
│   │   ├── Footer/
│   │   └── Loading/
│   ├── customer/
│   │   ├── CustomerList/
│   │   ├── CustomerForm/
│   │   ├── CustomerDetail/
│   │   └── InteractionHistory/
│   ├── sales/
│   │   ├── LeadList/
│   │   ├── LeadForm/
│   │   ├── LeadDetail/
│   │   ├── Pipeline/
│   │   └── LeadStages/
│   ├── tasks/
│   │   ├── TaskList/
│   │   ├── TaskForm/
│   │   ├── TaskDetail/
│   │   ├── TaskTypes/
│   │   └── TaskCalendar/
│   └── reports/
│       ├── Dashboard/
│       ├── SalesReports/
│       └── Analytics/
├── services/
│   ├── api/
│   ├── auth/
│   └── utils/
├── hooks/
├── context/
├── pages/
└── utils/
```

#### 3.1.2 State Management
- **Redux Toolkit**: Global state management
- **React Query**: Server state management
- **Context API**: Theme and user preferences

### 3.2 Backend Architecture (Java Spring Boot)

#### 3.2.1 Service Layer Structure
```
com.crm.service/
├── customer/
│   ├── CustomerController.java
│   ├── CustomerService.java
│   ├── CustomerRepository.java
│   ├── InteractionHistoryController.java
│   ├── InteractionHistoryService.java
│   └── dto/
├── sales/
│   ├── SalesController.java
│   ├── LeadService.java
│   ├── LeadRepository.java
│   ├── LeadStageService.java
│   ├── LeadStageRepository.java
│   └── dto/
├── task/
│   ├── TaskController.java
│   ├── TaskService.java
│   ├── TaskRepository.java
│   ├── TaskTypeService.java
│   ├── TaskTypeRepository.java
│   └── dto/
├── auth/
│   ├── AuthController.java
│   ├── JwtService.java
│   ├── UserService.java
│   └── dto/
└── common/
    ├── exception/
    ├── config/
    └── util/
```

#### 3.2.2 Cross-Cutting Concerns
- **Logging**: SLF4J with Logback
- **Exception Handling**: Global exception handler
- **Validation**: Bean Validation (JSR-303)
- **Security**: Spring Security with JWT
- **Caching**: Spring Cache with Redis
- **Monitoring**: Micrometer with Prometheus

---

## 4. Data Flow Architecture

### 4.1 Request Flow
1. **Client Request**: React SPA makes HTTP request
2. **API Gateway**: Routes request to appropriate service
3. **Service Processing**: Business logic execution
4. **Database Access**: Data persistence/retrieval
5. **Response**: JSON response back to client

### 4.2 Event-Driven Architecture
- **Event Publishing**: Services publish events to Kafka
- **Event Consumption**: Services consume events for data consistency
- **Event Types**: CustomerCreated, LeadAssigned, TaskCompleted

### 4.3 Caching Strategy
- **L1 Cache**: Application-level caching (Redis)
- **L2 Cache**: Database query caching
- **CDN**: Static asset caching

---

## 5. Security Architecture

### 5.1 Authentication Flow
1. **Login**: Username/password authentication
2. **JWT Generation**: Server generates JWT token
3. **Token Storage**: Client stores token securely
4. **Request Authorization**: Token included in API calls
5. **Token Validation**: Server validates token on each request

### 5.2 Authorization Model
- **Role-Based Access Control (RBAC)**
- **Resource-Level Permissions**
- **API-Level Security**

### 5.3 Data Security
- **Encryption**: AES-256 for data at rest
- **TLS**: HTTPS for data in transit
- **Audit Logging**: Comprehensive activity tracking

---

## 6. Scalability Architecture

### 6.1 Horizontal Scaling
- **Load Balancing**: Multiple service instances
- **Database Sharding**: Horizontal data partitioning
- **Caching**: Distributed caching with Redis cluster

### 6.2 Performance Optimization
- **Database Indexing**: Optimized query performance
- **Connection Pooling**: Efficient database connections
- **CDN**: Global content delivery
- **Compression**: Gzip compression for responses

### 6.3 Monitoring and Observability
- **Application Metrics**: Prometheus + Grafana
- **Distributed Tracing**: Jaeger for request tracing
- **Log Aggregation**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Health Checks**: Service health monitoring

---

## 7. Deployment Architecture

### 7.1 Containerization
- **Docker**: Containerized services
- **Docker Compose**: Local development
- **Kubernetes**: Production orchestration

### 7.2 CI/CD Pipeline
```
Code Commit → Build → Test → Security Scan → Deploy → Monitor
```

### 7.3 Environment Strategy
- **Development**: Local Docker environment
- **Staging**: Cloud-based staging environment
- **Production**: Multi-region cloud deployment

---

## 8. Integration Architecture

### 8.1 External Integrations
- **Email Service**: SendGrid/AWS SES
- **SMS Service**: Twilio
- **File Storage**: AWS S3/MinIO
- **Calendar**: Google Calendar API
- **Payment**: Stripe/PayPal

### 8.2 API Design
- **RESTful APIs**: Standard HTTP methods
- **GraphQL**: For complex data queries
- **WebSocket**: Real-time notifications
- **API Versioning**: Semantic versioning

---

## 9. Technology Stack

### 9.1 Frontend
- **Framework**: React.js 18.x
- **State Management**: Redux Toolkit
- **UI Library**: Material-UI or Ant Design
- **HTTP Client**: Axios
- **Build Tool**: Vite

### 9.2 Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Database**: PostgreSQL 15
- **Cache**: Redis 7.x
- **Message Queue**: Apache Kafka

### 9.3 Infrastructure
- **Container**: Docker
- **Orchestration**: Kubernetes
- **Cloud**: AWS/Azure/GCP
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack

---

## 10. Database Schema Integration

### 10.1 Core Entities
- **Customer**: Central entity for customer management
- **Lead**: Sales pipeline management
- **Task**: Activity and task management
- **User**: Authentication and authorization
- **Interaction History**: Customer communication tracking

### 10.2 Service-Database Mapping
- **Customer Service**: `customer`, `interaction_history` tables
- **Sales Service**: `lead`, `lead_stage` tables
- **Task Service**: `task`, `task_type` tables
- **Auth Service**: `user`, `roles` tables

### 10.3 Data Relationships
- **Customer → Lead**: One-to-many relationship
- **Customer → Task**: One-to-many relationship
- **Customer → Interaction History**: One-to-many relationship
- **Lead → Task**: One-to-many relationship
- **User → Customer**: One-to-many (created_by relationship)
- **User → Lead**: One-to-many (assigned_to relationship)
- **User → Task**: One-to-many (assigned_to relationship)

---

## 11. Risk Mitigation

### 11.1 Technical Risks
- **Service Failure**: Circuit breaker pattern
- **Database Failure**: Read replicas and failover
- **Network Issues**: Retry mechanisms and timeouts
- **Security Breaches**: Regular security audits

### 11.2 Operational Risks
- **Data Loss**: Automated backups and disaster recovery
- **Performance Issues**: Load testing and monitoring
- **Scalability**: Auto-scaling and resource management

---