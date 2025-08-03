# CRM System Database Design

## Version: 3.0 (Implementation Complete)
## Date: December 2024
## Author: Gokul Annadurai
## Status: ✅ DATABASE SCHEMA IMPLEMENTED AND DEPLOYED

---

## 1. Database Overview

### 1.1 Database Technology
- **Primary Database**: PostgreSQL 15
- **Read Replica**: PostgreSQL 15 (for read-heavy operations)
- **Search Engine**: Elasticsearch (for full-text search)
- **Cache**: Redis (for session and frequently accessed data)

### 1.2 Design Principles
- **Normalization**: Up to 3NF (Third Normal Form)
- **Performance**: Optimized indexing and query patterns
- **Scalability**: Horizontal partitioning and sharding ready
- **Security**: Encrypted data at rest and in transit
- **Audit Trail**: Comprehensive change tracking

---

## 2. Entity Relationship Diagram (ERD)

### 2.1 High-Level ERD
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     Users       │    │    Customers    │    │ Lead Stages     │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ PK: id          │    │ PK: id          │    │ PK: id          │
│ username        │    │ name            │    │ name            │
│ email           │    │ email           │    │ position        │
│ password        │    │ phone           │    │ created_at      │
│ roles           │    │ company         │    └─────────────────┘
│ status          │    │ source          │             │
│ created_at      │    │ created_by      │             │
│ updated_at      │    │ is_active       │             │
└─────────────────┘    │ created_at      │             │
         │             │ updated_at      │             │
         │             └─────────────────┘             │
         │                       │                     │
         │                       │                     │
         ▼                       ▼                     ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     Roles       │    │     Leads       │    │     Tasks       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ PK: role_id     │    │ PK: id          │    │ PK: id          │
│ role_name       │    │ customer_id     │    │ lead_id         │
│ permissions     │    │ assigned_to     │    │ customer_id     │
│ created_at      │    │ status          │    │ assigned_to     │
│ updated_at      │    │ current_stage_id│    │ task_type_id    │
└─────────────────┘    │ expected_close_date│  │ title           │
                       │ value           │    │ description     │
                       │ created_at      │    │ due_date        │
                       │ updated_at      │    │ is_completed    │
                       └─────────────────┘    │ created_at      │
                                              │ updated_at      │
                                              └─────────────────┘
                                                       │
                                                       │
                                                       ▼
                                              ┌─────────────────┐
                                              │   Task Types    │
                                              ├─────────────────┤
                                              │ PK: id          │
                                              │ name            │
                                              │ created_at      │
                                              └─────────────────┘
```

### 2.2 Interaction History ERD
```
┌─────────────────┐    ┌─────────────────┐
│    Customers    │    │ Interaction     │
├─────────────────┤    │    History      │
│ PK: id          │    ├─────────────────┤
│ name            │    │ PK: id          │
│ email           │    │ customer_id     │
│ phone           │    │ user_id         │
│ company         │    │ interaction_type│
│ source          │    │ notes           │
│ created_by      │    │ interaction_date│
│ is_active       │    │ created_at      │
│ created_at      │    └─────────────────┘
│ updated_at      │
└─────────────────┘
         │
         │
         ▼
┌─────────────────┐
│     Users       │
├─────────────────┤
│ PK: id          │
│ username        │
│ email           │
│ password        │
│ roles           │
│ status          │
│ created_at      │
│ updated_at      │
└─────────────────┘
```

---

## 3. Detailed Table Design

### 3.1 User Management Tables

#### 3.1.1 Users Table
```sql
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role_id INTEGER NOT NULL REFERENCES roles(role_id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INTEGER REFERENCES "user"(id),
    updated_by INTEGER REFERENCES "user"(id)
);

-- Indexes
CREATE INDEX idx_users_email ON "user"(email);
CREATE INDEX idx_users_username ON "user"(username);
CREATE INDEX idx_users_role_id ON "user"(role_id);
CREATE INDEX idx_users_status ON "user"(status);
```

#### 3.1.2 Roles Table
```sql
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    permissions JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_roles_name ON roles(role_name);
```

### 3.2 Customer Management Tables

#### 3.2.1 Customer Table
```sql
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    company VARCHAR(255),
    source VARCHAR(100), -- e.g., Referral, Website
    created_by BIGINT REFERENCES "user"(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_company ON customer(company);
CREATE INDEX idx_customer_source ON customer(source);
CREATE INDEX idx_customer_created_by ON customer(created_by);
CREATE INDEX idx_customer_is_active ON customer(is_active);
CREATE INDEX idx_customer_created_at ON customer(created_at);
```

### 3.3 Sales Pipeline Tables

#### 3.3.1 Lead Table
```sql
CREATE TABLE lead (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    assigned_to BIGINT REFERENCES "user"(id),
    status VARCHAR(50) NOT NULL DEFAULT 'Lead', -- for quick filter
    current_stage_id BIGINT REFERENCES lead_stage(id),
    expected_close_date DATE,
    value NUMERIC(12,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_lead_customer_id ON lead(customer_id);
CREATE INDEX idx_lead_assigned_to ON lead(assigned_to);
CREATE INDEX idx_lead_status ON lead(status);
CREATE INDEX idx_lead_current_stage_id ON lead(current_stage_id);
CREATE INDEX idx_lead_expected_close_date ON lead(expected_close_date);
```

#### 3.3.2 Lead Stage Table
```sql
CREATE TABLE lead_stage (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    position INTEGER NOT NULL, -- order of stages
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_lead_stage_name ON lead_stage(name);
CREATE INDEX idx_lead_stage_position ON lead_stage(position);
```

### 3.4 Task and Activity Tables

#### 3.4.1 Task Table
```sql
CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT REFERENCES lead(id) ON DELETE CASCADE,
    customer_id BIGINT REFERENCES customer(id) ON DELETE CASCADE,
    assigned_to BIGINT REFERENCES "user"(id),
    task_type_id BIGINT REFERENCES task_type(id),
    title VARCHAR(255),
    description TEXT,
    due_date TIMESTAMP,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_task_lead_id ON task(lead_id);
CREATE INDEX idx_task_customer_id ON task(customer_id);
CREATE INDEX idx_task_assigned_to ON task(assigned_to);
CREATE INDEX idx_task_task_type_id ON task(task_type_id);
CREATE INDEX idx_task_due_date ON task(due_date);
CREATE INDEX idx_task_is_completed ON task(is_completed);
```

#### 3.4.2 Task Type Table
```sql
CREATE TABLE task_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_task_type_name ON task_type(name);
```

### 3.5 Interaction History Table

#### 3.5.1 Interaction History Table
```sql
CREATE TABLE interaction_history (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES "user"(id),
    interaction_type VARCHAR(50), -- e.g., call, email, meeting
    notes TEXT,
    interaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_interaction_history_customer_id ON interaction_history(customer_id);
CREATE INDEX idx_interaction_history_user_id ON interaction_history(user_id);
CREATE INDEX idx_interaction_history_interaction_type ON interaction_history(interaction_type);
CREATE INDEX idx_interaction_history_interaction_date ON interaction_history(interaction_date);
CREATE INDEX idx_interaction_history_created_at ON interaction_history(created_at);
```

### 3.6 Audit and History Tables

#### 3.6.1 Audit Logs Table
```sql
CREATE TABLE audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('INSERT', 'UPDATE', 'DELETE')),
    old_values JSONB,
    new_values JSONB,
    user_id INTEGER REFERENCES "user"(id),
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_audit_logs_table_record ON audit_logs(table_name, record_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
```

---

## 4. Database Constraints and Relationships

### 4.1 Foreign Key Constraints
```sql
-- Customer -> Users (created_by)
ALTER TABLE customer ADD CONSTRAINT fk_customer_created_by 
    FOREIGN KEY (created_by) REFERENCES "user"(id);

-- Lead -> Customer
ALTER TABLE lead ADD CONSTRAINT fk_lead_customer 
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE;

-- Lead -> Users (assigned_to)
ALTER TABLE lead ADD CONSTRAINT fk_lead_assigned_to 
    FOREIGN KEY (assigned_to) REFERENCES "user"(id);

-- Lead -> Lead Stage
ALTER TABLE lead ADD CONSTRAINT fk_lead_stage 
    FOREIGN KEY (current_stage_id) REFERENCES lead_stage(id);

-- Task -> Lead
ALTER TABLE task ADD CONSTRAINT fk_task_lead 
    FOREIGN KEY (lead_id) REFERENCES lead(id) ON DELETE CASCADE;

-- Task -> Customer
ALTER TABLE task ADD CONSTRAINT fk_task_customer 
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE;

-- Task -> Users (assigned_to)
ALTER TABLE task ADD CONSTRAINT fk_task_assigned_to 
    FOREIGN KEY (assigned_to) REFERENCES "user"(id);

-- Task -> Task Type
ALTER TABLE task ADD CONSTRAINT fk_task_type 
    FOREIGN KEY (task_type_id) REFERENCES task_type(id);

-- Interaction History -> Customer
ALTER TABLE interaction_history ADD CONSTRAINT fk_interaction_history_customer 
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE;

-- Interaction History -> Users
ALTER TABLE interaction_history ADD CONSTRAINT fk_interaction_history_user 
    FOREIGN KEY (user_id) REFERENCES "user"(id);
```

### 4.2 Check Constraints
```sql
-- Email validation
ALTER TABLE customer ADD CONSTRAINT chk_customer_email 
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Phone validation
ALTER TABLE customer ADD CONSTRAINT chk_customer_phone 
    CHECK (phone ~* '^\+?[1-9]\d{1,14}$');

-- Lead value positive
ALTER TABLE lead ADD CONSTRAINT chk_lead_value 
    CHECK (value >= 0);

-- Task due date validation
ALTER TABLE task ADD CONSTRAINT chk_task_due_date 
    CHECK (due_date > created_at);
```

---

## 5. Indexing Strategy

### 5.1 Primary Indexes
- All primary keys are automatically indexed
- Foreign keys are indexed for join performance

### 5.2 Composite Indexes
```sql
-- Customer search optimization
CREATE INDEX idx_customer_search ON customer(name, email, company, source);

-- Lead pipeline view
CREATE INDEX idx_lead_pipeline ON lead(current_stage_id, assigned_to, expected_close_date);

-- Task management
CREATE INDEX idx_task_management ON task(assigned_to, is_completed, due_date);

-- Interaction history
CREATE INDEX idx_interaction_history_search ON interaction_history(customer_id, interaction_type, interaction_date);
```

### 5.3 Partial Indexes
```sql
-- Active customers only
CREATE INDEX idx_customer_active ON customer(id) WHERE is_active = TRUE;

-- Pending tasks only
CREATE INDEX idx_task_pending ON task(id) WHERE is_completed = FALSE;

-- Recent interactions
CREATE INDEX idx_interaction_history_recent ON interaction_history(id) 
    WHERE interaction_date > CURRENT_DATE - INTERVAL '30 days';
```

---

## 6. Data Partitioning Strategy

### 6.1 Table Partitioning
```sql
-- Partition interaction_history table by month
CREATE TABLE interaction_history_2024_01 PARTITION OF interaction_history
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE interaction_history_2024_02 PARTITION OF interaction_history
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');
```

### 6.2 Sharding Strategy
- **Customer-based sharding**: Distribute customers across multiple databases
- **Time-based sharding**: Partition historical data by year
- **Geographic sharding**: Distribute by customer location

---

## 7. Backup and Recovery

### 7.1 Backup Strategy
- **Full Backup**: Daily automated full database backup
- **Incremental Backup**: Hourly incremental backups
- **WAL Archiving**: Continuous WAL (Write-Ahead Log) archiving
- **Point-in-Time Recovery**: Support for point-in-time recovery

### 7.2 Recovery Procedures
- **RTO (Recovery Time Objective)**: < 4 hours
- **RPO (Recovery Point Objective)**: < 1 hour
- **Testing**: Monthly recovery testing

---

## 8. Performance Optimization

### 8.1 Query Optimization
- **Query Analysis**: Regular query performance analysis
- **Index Tuning**: Automatic index recommendation
- **Statistics Updates**: Regular statistics updates
- **Query Caching**: Application-level query caching

### 8.2 Connection Pooling
- **HikariCP**: Java connection pooling
- **Pool Size**: 10-50 connections per service
- **Monitoring**: Connection pool metrics

---

## 9. Security Measures

### 9.1 Data Encryption
- **At Rest**: AES-256 encryption for sensitive data
- **In Transit**: TLS 1.3 for all connections
- **Column Encryption**: Sensitive fields encrypted at column level

### 9.2 Access Control
- **Row-Level Security**: Customer data isolation
- **Column-Level Security**: Sensitive field protection
- **Audit Logging**: Comprehensive activity tracking

---

## 10. Monitoring and Maintenance

### 10.1 Database Monitoring
- **Performance Metrics**: Query execution time, throughput
- **Resource Usage**: CPU, memory, disk I/O
- **Connection Monitoring**: Active connections, connection pool status
- **Error Tracking**: Failed queries, constraint violations

### 10.2 Maintenance Tasks
- **Vacuum**: Regular table and index vacuuming
- **Analyze**: Statistics updates for query planner
- **Reindex**: Periodic index rebuilding
- **Log Rotation**: Database log file management

---

## 11. Sample Data Population

### 11.1 Lead Stages
```sql
INSERT INTO lead_stage (name, position) VALUES 
('Lead', 1),
('Qualified', 2),
('Proposal', 3),
('Negotiation', 4),
('Closed Won', 5),
('Closed Lost', 6);
```

### 11.2 Task Types
```sql
INSERT INTO task_type (name) VALUES 
('Follow-up'),
('Call'),
('Demo'),
('Meeting'),
('Email'),
('Proposal'),
('Contract Review');
```

--- 