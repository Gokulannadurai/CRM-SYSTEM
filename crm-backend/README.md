# CRM Backend Services

A microservices-based backend architecture for the CRM system, built with Java 21, Spring Boot 3.x, and PostgreSQL.

## 🏗️ Architecture Overview

The backend consists of four microservices, each handling specific business domains:

- **Auth Service**: Authentication and authorization
- **User Service**: User management and administration
- **Customer Service**: Customer and company management
- **Sales Service**: Lead, task, and sales pipeline management

## 🚀 Features

### ✅ Core Features (Completed)
- **Microservices Architecture**: Scalable, maintainable service design
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Granular permission management
- **RESTful APIs**: Standardized API design with OpenAPI documentation
- **Database Management**: PostgreSQL with Flyway migrations
- **File Management**: File upload, storage, and attachment system
- **Search & Filtering**: Advanced search capabilities
- **Pagination**: Server-side pagination for all endpoints
- **Validation**: Comprehensive input validation
- **Error Handling**: Global exception handling and error responses
- **Logging**: Structured logging with SLF4J
- **Health Checks**: Application health monitoring
- **CORS Configuration**: Cross-origin resource sharing support

### 🎯 Key Capabilities
- **User Management**: Complete user lifecycle management
- **Customer Management**: Customer and company data management
- **Sales Pipeline**: Lead and task management with stage progression
- **File Attachments**: Multi-file upload and management
- **API Documentation**: Auto-generated OpenAPI documentation
- **Database Migrations**: Automated schema management
- **Security**: Comprehensive security implementation
- **Performance**: Optimized queries and caching

## 🛠️ Technology Stack

### Core Technologies
- **Java 21**: Latest LTS version with modern features
- **Spring Boot 3.x**: Rapid application development framework
- **Spring Security**: Security framework with JWT support
- **Spring Data JPA**: Data access layer with Hibernate
- **PostgreSQL 15**: Robust relational database
- **Flyway**: Database migration tool
- **Maven**: Build and dependency management

### Additional Libraries
- **Lombok**: Reduces boilerplate code
- **MapStruct**: Type-safe object mapping
- **Validation API**: Bean validation
- **OpenAPI 3**: API documentation
- **SLF4J**: Logging facade
- **HikariCP**: Database connection pooling

## 📁 Project Structure

```
crm-backend/
├── auth-service/           # Authentication service
│   ├── src/main/java/
│   │   └── com/crm/auth/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── entity/
│   │       ├── dto/
│   │       ├── config/
│   │       └── security/
│   └── src/main/resources/
│       └── application.yml
├── user-service/           # User management service
│   ├── src/main/java/
│   │   └── com/crm/user/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── entity/
│   │       ├── dto/
│   │       └── config/
│   └── src/main/resources/
│       └── application.yml
├── customer-service/       # Customer management service
│   ├── src/main/java/
│   │   └── com/crm/customer/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── entity/
│   │       ├── dto/
│   │       └── config/
│   └── src/main/resources/
│       └── application.yml
├── sales-service/          # Sales pipeline service
│   ├── src/main/java/
│   │   └── com/crm/sales/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── entity/
│   │       ├── dto/
│   │       └── config/
│   └── src/main/resources/
│       └── application.yml
├── docker-compose.yml      # Docker composition
└── MIGRATION_GUIDE.md      # Database migration guide
```

## 🚀 Getting Started

### Prerequisites

- **Java 21**: JDK 21 or higher
- **Maven 3.8+**: Build tool
- **PostgreSQL 15**: Database server
- **Docker**: For containerized deployment (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd crm-backend
   ```

2. **Database Setup**
   ```bash
   # Start PostgreSQL (if using Docker)
   docker run --name crm-postgres \
     -e POSTGRES_DB=crm_db \
     -e POSTGRES_USER=crm_user \
     -e POSTGRES_PASSWORD=crm_password \
     -p 5432:5432 \
     -d postgres:15
   ```

3. **Environment Configuration**
   Create `application.yml` files for each service with database configuration:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/crm_db
       username: crm_user
       password: crm_password
       driver-class-name: org.postgresql.Driver
     jpa:
       hibernate:
         ddl-auto: validate
       show-sql: false
   ```

4. **Build and Run Services**
   ```bash
   # Build all services
   mvn clean install

   # Run services individually
   cd auth-service && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd customer-service && mvn spring-boot:run
   cd sales-service && mvn spring-boot:run
   ```

5. **Using Docker Compose**
   ```bash
   # Start all services with Docker
   docker-compose up -d
   ```

## 🔧 Configuration

### Service Ports
- **Auth Service**: 8080
- **User Service**: 8081
- **Customer Service**: 8082
- **Sales Service**: 8083

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/crm_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `crm_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `crm_password` |
| `JWT_SECRET` | JWT signing secret | `your-secret-key` |
| `JWT_EXPIRATION` | JWT token expiration | `86400000` (24 hours) |

## 📊 API Documentation

### OpenAPI Documentation
Each service provides auto-generated API documentation:

- **Auth Service**: http://localhost:8080/swagger-ui.html
- **User Service**: http://localhost:8081/swagger-ui.html
- **Customer Service**: http://localhost:8082/swagger-ui.html
- **Sales Service**: http://localhost:8083/swagger-ui.html

### API Endpoints Overview

#### Authentication Service (Port 8080)
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/validate` - Validate JWT token

#### User Service (Port 8081)
- `GET /api/users` - Get all users (with pagination)
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/search` - Search users

#### Customer Service (Port 8082)
- `GET /api/customers` - Get all customers (with pagination)
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer
- `GET /api/customers/search` - Search customers
- `GET /api/companies` - Get all companies
- `POST /api/companies` - Create new company

#### Sales Service (Port 8083)
- `GET /api/leads` - Get all leads (with pagination)
- `GET /api/leads/{id}` - Get lead by ID
- `POST /api/leads` - Create new lead
- `PUT /api/leads/{id}` - Update lead
- `DELETE /api/leads/{id}` - Delete lead
- `GET /api/tasks` - Get all tasks (with pagination)
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `GET /api/dashboard/stats` - Get dashboard statistics

## 🔒 Security Features

### Authentication
- **JWT Tokens**: Secure token-based authentication
- **Token Refresh**: Automatic token refresh mechanism
- **Password Hashing**: BCrypt password encryption
- **Session Management**: Secure session handling

### Authorization
- **Role-Based Access Control**: Different permissions for different roles
- **Method-Level Security**: Secure individual API endpoints
- **Resource-Level Security**: Secure access to specific resources
- **CORS Configuration**: Secure cross-origin requests

### Data Protection
- **Input Validation**: Comprehensive input validation
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Cross-site scripting prevention
- **CSRF Protection**: Cross-site request forgery protection

## 📊 Database Management

### Schema Management
- **Flyway Migrations**: Automated database schema management
- **Version Control**: Database changes tracked in version control
- **Rollback Support**: Ability to rollback database changes
- **Environment Support**: Different schemas for different environments

### Database Optimization
- **Connection Pooling**: HikariCP for efficient connection management
- **Query Optimization**: Optimized database queries
- **Indexing**: Proper database indexing for performance
- **Monitoring**: Database performance monitoring

## 🧪 Testing

### Testing Strategy
- **Unit Tests**: Service and repository layer testing
- **Integration Tests**: API endpoint testing
- **Database Tests**: Database integration testing
- **Security Tests**: Authentication and authorization testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific service tests
cd user-service && mvn test
```

## 📈 Performance & Monitoring

### Performance Optimizations
- **Database Indexing**: Optimized database indexes
- **Query Optimization**: Efficient database queries
- **Caching**: Application-level caching
- **Connection Pooling**: Efficient database connections

### Monitoring
- **Health Checks**: Application health monitoring
- **Metrics**: Performance metrics collection
- **Logging**: Structured logging with SLF4J
- **Error Tracking**: Comprehensive error tracking

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker images
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Deployment
```bash
# Build production JARs
mvn clean package -DskipTests

# Run production services
java -jar auth-service/target/auth-service.jar
java -jar user-service/target/user-service.jar
java -jar customer-service/target/customer-service.jar
java -jar sales-service/target/sales-service.jar
```

### Environment Configuration
```bash
# Production environment variables
export SPRING_PROFILES_ACTIVE=production
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/crm_db
export JWT_SECRET=your-production-secret
```

## 🔧 Development

### Development Workflow
1. **Feature Development**: Create feature branches
2. **Code Review**: Submit pull requests for review
3. **Testing**: Ensure all tests pass
4. **Documentation**: Update API documentation
5. **Deployment**: Deploy to staging/production

### Code Standards
- **Java Coding Standards**: Follow Java coding conventions
- **Spring Boot Best Practices**: Follow Spring Boot guidelines
- **API Design**: RESTful API design principles
- **Documentation**: Comprehensive code documentation

## 📚 API Integration

### Frontend Integration
The backend services are designed to work seamlessly with the React.js frontend:

- **CORS Configuration**: Pre-configured for frontend integration
- **JWT Authentication**: Compatible with frontend authentication
- **Error Handling**: Standardized error responses
- **Pagination**: Consistent pagination across all endpoints

### Third-Party Integration
- **RESTful APIs**: Standard REST API design
- **OpenAPI Documentation**: Auto-generated API documentation
- **JSON Responses**: Standardized JSON response format
- **Error Codes**: Consistent HTTP status codes

## 🆘 Troubleshooting

### Common Issues
1. **Database Connection**: Check database credentials and connectivity
2. **JWT Token Issues**: Verify JWT secret and expiration settings
3. **CORS Errors**: Ensure CORS configuration matches frontend URL
4. **Port Conflicts**: Verify service ports are not in use

### Logs and Debugging
```bash
# View service logs
docker-compose logs -f auth-service
docker-compose logs -f user-service
docker-compose logs -f customer-service
docker-compose logs -f sales-service

# Enable debug logging
export LOGGING_LEVEL_COM_CRM=DEBUG
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Submit a pull request

### Code Guidelines
- Follow Java and Spring Boot best practices
- Write comprehensive tests
- Update API documentation
- Follow the established code style

---

**Status**: ✅ Production Ready
**Version**: 2.0.0
**Last Updated**: December 2024 