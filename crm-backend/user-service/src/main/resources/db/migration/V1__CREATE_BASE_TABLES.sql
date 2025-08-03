-- =====================================================
-- CRM System Database Migration V1
-- Create Base Tables
-- =====================================================

-- Create role table
CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    level INTEGER NOT NULL DEFAULT 0,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create user table
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at DATE,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create user_role table (join table for many-to-many relationship)
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_role_user_role UNIQUE (user_id, role_id),
    PRIMARY KEY (user_id, role_id)
);

-- Create customer table
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    company VARCHAR(255),
    source VARCHAR(100),
    description VARCHAR(500),
    created_by BIGINT REFERENCES "user"(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create lead_stage table
CREATE TABLE lead_stage (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    position INTEGER NOT NULL, -- order of stages
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create lead table
CREATE TABLE lead (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL, -- for quick filter
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    customer_name VARCHAR(255),
    assigned_to BIGINT REFERENCES "user"(id),
    user_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'Lead',
    attachments JSONB,
    current_stage_id BIGINT REFERENCES lead_stage(id),
    expected_close_date DATE,
    value NUMERIC(12,2),
    additional_notes VARCHAR(500) DEFAULT NULL,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create task_type table
CREATE TABLE task_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create task table
CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT REFERENCES lead(id) ON DELETE CASCADE,
    customer_id BIGINT REFERENCES customer(id) ON DELETE CASCADE,
    assigned_to BIGINT REFERENCES "user"(id),
    task_type_id BIGINT REFERENCES task_type(id),
    title VARCHAR(255),
    description TEXT,
    attachments JSONB,
    due_date DATE,
    is_completed BOOLEAN DEFAULT FALSE,
    is_started BOOLEAN DEFAULT FALSE,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create interaction_history table
CREATE TABLE interaction_history (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES "user"(id),
    interaction_type VARCHAR(50), -- e.g., call, email, meeting
    notes TEXT,
    interaction_date DATE DEFAULT CURRENT_DATE,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- Create audit_logs table
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
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- User indexes
CREATE INDEX idx_user_username ON "user"(username);
CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_mobile_number ON "user"(mobile_number);
CREATE INDEX idx_user_is_active ON "user"(is_active);
CREATE INDEX idx_user_is_deleted ON "user"(is_deleted);
CREATE INDEX idx_user_created_at ON "user"(created_at);
CREATE INDEX idx_user_last_login_at ON "user"(last_login_at);

-- Composite index for search
CREATE INDEX idx_user_search ON "user"(first_name, last_name, email);

-- Index for active and non-deleted users
CREATE INDEX idx_user_active_not_deleted ON "user"(id) WHERE is_active = TRUE AND is_deleted = FALSE;

-- Role indexes
CREATE INDEX idx_role_name ON role(name);
CREATE INDEX idx_role_level ON role(level);
CREATE INDEX idx_role_created_at ON role(created_at);

-- User role indexes
CREATE INDEX idx_user_role_user_id ON user_role(user_id);
CREATE INDEX idx_user_role_role_id ON user_role(role_id);

-- Customer indexes
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_company ON customer(company);
CREATE INDEX idx_customer_source ON customer(source);
CREATE INDEX idx_customer_created_by ON customer(created_by);
CREATE INDEX idx_customer_is_active ON customer(is_active);
CREATE INDEX idx_customer_created_at ON customer(created_at);

-- Lead indexes
CREATE INDEX idx_lead_customer_id ON lead(customer_id);
CREATE INDEX idx_lead_assigned_to ON lead(assigned_to);
CREATE INDEX idx_lead_status ON lead(status);
CREATE INDEX idx_lead_current_stage_id ON lead(current_stage_id);
CREATE INDEX idx_lead_expected_close_date ON lead(expected_close_date);

-- Task indexes
CREATE INDEX idx_task_lead_id ON task(lead_id);
CREATE INDEX idx_task_customer_id ON task(customer_id);
CREATE INDEX idx_task_assigned_to ON task(assigned_to);
CREATE INDEX idx_task_task_type_id ON task(task_type_id);
CREATE INDEX idx_task_due_date ON task(due_date);
CREATE INDEX idx_task_is_completed ON task(is_completed);

-- Interaction history indexes
CREATE INDEX idx_interaction_history_customer_id ON interaction_history(customer_id);
CREATE INDEX idx_interaction_history_user_id ON interaction_history(user_id);
CREATE INDEX idx_interaction_history_interaction_type ON interaction_history(interaction_type);
CREATE INDEX idx_interaction_history_interaction_date ON interaction_history(interaction_date);
CREATE INDEX idx_interaction_history_created_at ON interaction_history(created_at);

-- Audit logs indexes
CREATE INDEX idx_audit_logs_table_record ON audit_logs(table_name, record_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

-- =====================================================
-- Composite Indexes for Common Queries
-- =====================================================

-- Customer search optimization
CREATE INDEX idx_customer_search ON customer(name, email, company, source);

-- Lead pipeline view
CREATE INDEX idx_lead_pipeline ON lead(current_stage_id, assigned_to, expected_close_date);

-- Task management
CREATE INDEX idx_task_management ON task(assigned_to, is_completed, due_date);

-- Interaction history
CREATE INDEX idx_interaction_history_search ON interaction_history(customer_id, interaction_type, interaction_date);

-- =====================================================
-- Partial Indexes for Performance
-- =====================================================

-- Active customers only
CREATE INDEX idx_customer_active ON customer(id) WHERE is_active = TRUE;

-- Pending tasks only
CREATE INDEX idx_task_pending ON task(id) WHERE is_completed = FALSE;

-- =====================================================
-- Constraints
-- =====================================================

-- Email validation for customer
ALTER TABLE customer ADD CONSTRAINT chk_customer_email 
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Lead value positive
ALTER TABLE lead ADD CONSTRAINT chk_lead_value 
    CHECK (value >= 0);

-- Task due date validation
ALTER TABLE task ADD CONSTRAINT chk_task_due_date 
    CHECK (due_date >= created_at);

-- =====================================================
-- Sample Data Population
-- =====================================================

-- Insert default roles
INSERT INTO role (name, level) VALUES
    ('ADMIN', 100),
    ('SALES_MANAGER', 80),
    ('SALES_REP', 60),
    ('CUSTOMER_SERVICE', 40),
    ('USER', 20);

-- Insert default admin user (password: admin123)
INSERT INTO "user" (username, email, password, first_name, last_name, mobile_number, is_active, is_deleted)
VALUES (
    'admin',
    'admin@crm.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'Admin',
    'User',
    '+1234567890',
    TRUE,
    FALSE
);

-- Assign ADMIN role to default admin user
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id 
FROM "user" u, role r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Insert default lead stages
INSERT INTO lead_stage (name, position) VALUES 
('Lead', 1),
('Qualified', 2),
('Proposal', 3),
('Negotiation', 4),
('Closed Won', 5),
('Closed Lost', 6);

-- Insert default task types
INSERT INTO task_type (name) VALUES
                                 ('Discovery Call'),
                                 ('Requirements Gathering'),
                                 ('Technical Review'),
                                 ('Stakeholder Meeting'),
                                 ('Contract Review'),
                                 ('Implementation Planning'),
                                 ('Training Session'),
                                 ('Go-Live Support'),
                                 ('Post-Sale Follow-up'),
                                 ('Quarterly Review');