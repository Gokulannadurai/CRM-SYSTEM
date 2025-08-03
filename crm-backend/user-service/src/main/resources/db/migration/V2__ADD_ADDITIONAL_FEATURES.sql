-- =====================================================
-- CRM System Database Migration V2
-- Add Additional Features
-- =====================================================

-- Create trigger function for updating updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_DATE;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updating updated_at
CREATE TRIGGER update_user_updated_at 
    BEFORE UPDATE ON "user"
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_role_updated_at 
    BEFORE UPDATE ON role
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customer_updated_at 
    BEFORE UPDATE ON customer
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_lead_updated_at 
    BEFORE UPDATE ON lead
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_task_updated_at 
    BEFORE UPDATE ON task
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Views for Common Queries
-- =====================================================

-- Customer summary view
CREATE VIEW customer_summary AS
SELECT 
    c.id,
    c.name,
    c.email,
    c.company,
    c.source,
    c.is_active,
    c.created_at,
    COUNT(l.id) as lead_count,
    COUNT(t.id) as task_count,
    COUNT(ih.id) as interaction_count
FROM customer c
LEFT JOIN lead l ON c.id = l.customer_id
LEFT JOIN task t ON c.id = t.customer_id
LEFT JOIN interaction_history ih ON c.id = ih.customer_id
GROUP BY c.id, c.name, c.email, c.company, c.source, c.is_active, c.created_at;

-- Lead pipeline view
CREATE VIEW lead_pipeline AS
SELECT 
    l.id,
    l.customer_id,
    c.name as customer_name,
    c.company as customer_company,
    l.assigned_to,
    u.first_name || ' ' || u.last_name as assigned_user,
    l.status,
    ls.name as stage_name,
    ls.position as stage_position,
    l.expected_close_date,
    l.value,
    l.created_at,
    l.updated_at
FROM lead l
JOIN customer c ON l.customer_id = c.id
JOIN lead_stage ls ON l.current_stage_id = ls.id
LEFT JOIN "user" u ON l.assigned_to = u.id;

-- Task summary view
CREATE VIEW task_summary AS
SELECT 
    t.id,
    t.title,
    t.description,
    t.due_date,
    t.is_completed,
    t.created_at,
    t.updated_at,
    tt.name as task_type,
    t.assigned_to,
    u.first_name || ' ' || u.last_name as assigned_user,
    t.customer_id,
    c.name as customer_name,
    t.lead_id
FROM task t
JOIN task_type tt ON t.task_type_id = tt.id
LEFT JOIN "user" u ON t.assigned_to = u.id
LEFT JOIN customer c ON t.customer_id = c.id;

-- User activity view
CREATE VIEW user_activity AS
SELECT 
    u.id,
    u.username,
    u.first_name || ' ' || u.last_name as full_name,
    COUNT(l.id) as lead_count,
    COUNT(t.id) as task_count,
    COUNT(ih.id) as interaction_count,
    u.last_login_at
FROM "user" u
LEFT JOIN lead l ON u.id = l.assigned_to
LEFT JOIN task t ON u.id = t.assigned_to
LEFT JOIN interaction_history ih ON u.id = ih.user_id
WHERE u.is_active = true AND u.is_deleted = false
GROUP BY u.id, u.username, u.first_name, u.last_name, u.last_login_at;

-- =====================================================
-- Stored Procedures for Analytics
-- =====================================================

-- Get customer statistics
CREATE OR REPLACE FUNCTION get_customer_stats()
RETURNS TABLE (
    total_customers BIGINT,
    active_customers BIGINT,
    new_customers_this_month BIGINT,
    top_source VARCHAR(50),
    avg_leads_per_customer NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_customers,
        COUNT(*) FILTER (WHERE is_active = true) as active_customers,
        COUNT(*) FILTER (WHERE created_at >= DATE_TRUNC('month', CURRENT_DATE)) as new_customers_this_month,
        (SELECT source FROM customer GROUP BY source ORDER BY COUNT(*) DESC LIMIT 1) as top_source,
        ROUND(AVG(lead_count), 2) as avg_leads_per_customer
    FROM customer_summary;
END;
$$ LANGUAGE plpgsql;

-- Get lead statistics
CREATE OR REPLACE FUNCTION get_lead_stats()
RETURNS TABLE (
    total_leads BIGINT,
    qualified_leads BIGINT,
    avg_lead_value NUMERIC,
    conversion_rate NUMERIC,
    avg_time_to_close INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_leads,
        COUNT(*) FILTER (WHERE status = 'Qualified') as qualified_leads,
        ROUND(AVG(value), 2) as avg_lead_value,
        ROUND((COUNT(*) FILTER (WHERE status = 'Closed Won')::NUMERIC / COUNT(*) * 100), 2) as conversion_rate,
        AVG(EXTRACT(DAY FROM (updated_at - created_at)))::INTEGER as avg_time_to_close
    FROM lead;
END;
$$ LANGUAGE plpgsql;

-- Get task statistics
CREATE OR REPLACE FUNCTION get_task_stats()
RETURNS TABLE (
    total_tasks BIGINT,
    completed_tasks BIGINT,
    overdue_tasks BIGINT,
    completion_rate NUMERIC,
    task_type_distribution JSON
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_tasks,
        COUNT(*) FILTER (WHERE is_completed = true) as completed_tasks,
        COUNT(*) FILTER (WHERE due_date < CURRENT_DATE AND is_completed = false) as overdue_tasks,
        ROUND((COUNT(*) FILTER (WHERE is_completed = true)::NUMERIC / COUNT(*) * 100), 2) as completion_rate,
        (SELECT json_object_agg(tt.name, type_count.count)
         FROM task_type tt
         LEFT JOIN (
             SELECT tt2.name, COUNT(t2.id) as count
             FROM task_type tt2
             LEFT JOIN task t2 ON tt2.id = t2.task_type_id
             GROUP BY tt2.name
         ) type_count ON tt.name = type_count.name) as task_type_distribution
    FROM task t
    CROSS JOIN (
        SELECT tt2.name, COUNT(t2.id) as count
        FROM task_type tt2
        LEFT JOIN task t2 ON tt2.id = t2.task_type_id
        GROUP BY tt2.name
    ) type_count;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- Sample Data - 3 Entries Each for Customer, Lead, Task
-- =====================================================

-- Insert sample users
INSERT INTO "user" (username, email, password, first_name, last_name, mobile_number, is_active, is_deleted) VALUES 
('sales_manager', 'manager@crm.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Manager', '+1234567891', TRUE, FALSE),
('sales_rep1', 'rep1@crm.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sarah', 'Johnson', '+1234567892', TRUE, FALSE),
('sales_rep2', 'rep2@crm.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mike', 'Davis', '+1234567893', TRUE, FALSE);

-- Assign roles to sample users
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id 
FROM "user" u, role r
WHERE u.username = 'sales_manager' AND r.name = 'SALES_MANAGER';

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id 
FROM "user" u, role r
WHERE u.username = 'sales_rep1' AND r.name = 'SALES_REP';

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id 
FROM "user" u, role r
WHERE u.username = 'sales_rep2' AND r.name = 'SALES_REP';

-- =====================================================
-- Insert 3 Customer Entries
-- =====================================================

INSERT INTO customer (name, email, phone, company, source, description, created_by, is_active) VALUES
('John Smith', 'john.smith@techcorp.com', '+1-555-0101', 'TechCorp Solutions', 'Website', 'Senior IT Manager interested in enterprise solutions. Looking for comprehensive CRM with advanced reporting and integration capabilities.', 1, true),
('Sarah Johnson', 'sarah.j@innovateinc.com', '+1-555-0102', 'Innovate Inc', 'Referral', 'VP of Operations seeking process automation and workflow optimization. Company has 200+ employees across 5 locations.', 1, true),
('Michael Chen', 'mchen@startupxyz.com', '+1-555-0103', 'StartupXYZ', 'Cold Call', 'Founder of fast-growing startup with 25 employees. Needs scalable CRM solution with mobile access and API integration.', 1, true);

-- =====================================================
-- Insert 3 Lead Entries
-- =====================================================

INSERT INTO lead (title, customer_id, assigned_to, status, current_stage_id, expected_close_date, value, additional_notes) VALUES
('Enterprise CRM Implementation', 1, 2, 'Qualified', 2, CURRENT_DATE + INTERVAL '45 days', 150000.00, 'Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified. Technical requirements include SSO, API integration, and custom reporting.'),
('Process Automation Project', 2, 3, 'Proposal', 3, CURRENT_DATE + INTERVAL '30 days', 85000.00, 'Looking to automate sales and marketing processes. Technical requirements gathered. Need workflow automation, email integration, and analytics dashboard.'),
('Startup CRM Setup', 3, 4, 'Lead', 1, CURRENT_DATE + INTERVAL '60 days', 25000.00, 'Early-stage startup with 25 employees. Need scalable solution with mobile access, basic reporting, and integration with existing tools.');

-- =====================================================
-- Insert 3 Task Entries
-- =====================================================

INSERT INTO task (lead_id, customer_id, assigned_to, task_type_id, title, description, due_date, is_completed, is_started) VALUES
(1, 1, 2, 3, 'Product Demo for TechCorp', 'Schedule and conduct comprehensive product demonstration for John Smith and his IT team. Focus on enterprise features, security, and integration capabilities.', CURRENT_DATE + INTERVAL '5 days', false, true),
(2, 2, 3, 4, 'Requirements Gathering Meeting', 'Schedule meeting with Sarah and her operations team to gather detailed requirements for process automation. Document current workflows and pain points.', CURRENT_DATE + INTERVAL '10 days', false, false),
(3, 3, 4, 1, 'Initial Discovery Call', 'Conduct initial discovery call with Michael to understand startup needs, budget constraints, and growth plans. Discuss timeline and implementation approach.', CURRENT_DATE + INTERVAL '1 day', false, true);

-- =====================================================
-- Insert Interaction History Data
-- =====================================================

INSERT INTO interaction_history (customer_id, user_id, interaction_type, notes, interaction_date) VALUES
(1, 2, 'Call', 'Initial contact with John Smith. Discussed current pain points with existing CRM system. Customer is interested in enterprise features and security compliance.', CURRENT_DATE - INTERVAL '5 days'),
(1, 2, 'Email', 'Sent product brochure, case studies for similar enterprise implementations, and security documentation.', CURRENT_DATE - INTERVAL '4 days'),
(1, 2, 'Meeting', 'Conducted discovery meeting with John and his IT team. Gathered technical requirements, timeline expectations, and budget approval process.', CURRENT_DATE - INTERVAL '2 days'),
(2, 3, 'Call', 'Cold call to Sarah Johnson. Discussed process automation needs and current operational challenges. Customer was receptive and scheduled follow-up.', CURRENT_DATE - INTERVAL '7 days'),
(2, 3, 'Email', 'Sent automation case studies, ROI calculator, and workflow optimization examples to Sarah.', CURRENT_DATE - INTERVAL '6 days'),
(2, 3, 'Meeting', 'Requirements gathering meeting with Sarah and operations team. Identified key automation opportunities and documented current processes.', CURRENT_DATE - INTERVAL '3 days'),
(3, 4, 'Call', 'Initial contact with Michael Chen. Discussed startup challenges, growth plans, and current tool limitations. Budget range established at $20K-$30K.', CURRENT_DATE - INTERVAL '4 days'),
(3, 4, 'Email', 'Sent startup package information, pricing, and mobile app screenshots to Michael.', CURRENT_DATE - INTERVAL '3 days'),
(3, 4, 'Meeting', 'Product walkthrough for Michael focusing on startup-friendly features, scalability, and integration options.', CURRENT_DATE - INTERVAL '1 day');

-- =====================================================
-- Migration completed successfully
-- ===================================================== 