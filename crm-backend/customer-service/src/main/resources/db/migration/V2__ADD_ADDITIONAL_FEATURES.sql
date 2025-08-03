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
    u.email,
    r.name as role_name,
    u.is_active,
    u.last_login_at,
    COUNT(DISTINCT l.id) as assigned_leads,
    COUNT(DISTINCT t.id) as assigned_tasks,
    COUNT(DISTINCT ih.id) as interactions_created
FROM "user" u
LEFT JOIN user_role ur ON u.id = ur.user_id
LEFT JOIN role r ON ur.role_id = r.id
LEFT JOIN lead l ON u.id = l.assigned_to
LEFT JOIN task t ON u.id = t.assigned_to
LEFT JOIN interaction_history ih ON u.id = ih.user_id
WHERE u.is_active = TRUE AND u.is_deleted = FALSE
GROUP BY u.id, u.username, u.first_name, u.last_name, u.email, r.name, u.is_active, u.last_login_at;

-- =====================================================
-- Functions for Common Operations
-- =====================================================

-- Function to get customer statistics
CREATE OR REPLACE FUNCTION get_customer_stats()
RETURNS TABLE(
    total_customers BIGINT,
    active_customers BIGINT,
    inactive_customers BIGINT,
    customers_with_leads BIGINT,
    customers_with_tasks BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_customers,
        COUNT(*) FILTER (WHERE is_active = true) as active_customers,
        COUNT(*) FILTER (WHERE is_active = false) as inactive_customers,
        COUNT(DISTINCT l.customer_id) as customers_with_leads,
        COUNT(DISTINCT t.customer_id) as customers_with_tasks
    FROM customer c
    LEFT JOIN lead l ON c.id = l.customer_id
    LEFT JOIN task t ON c.id = t.customer_id;
END;
$$ LANGUAGE plpgsql;

-- Function to get lead statistics
CREATE OR REPLACE FUNCTION get_lead_stats()
RETURNS TABLE(
    total_leads BIGINT,
    leads_by_stage JSONB,
    total_value NUMERIC,
    avg_value NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_leads,
        jsonb_object_agg(ls.name, stage_count.count) as leads_by_stage,
        COALESCE(SUM(l.value), 0) as total_value,
        COALESCE(AVG(l.value), 0) as avg_value
    FROM lead l
    JOIN lead_stage ls ON l.current_stage_id = ls.id
    CROSS JOIN (
        SELECT ls2.name, COUNT(l2.id) as count
        FROM lead_stage ls2
        LEFT JOIN lead l2 ON ls2.id = l2.current_stage_id
        GROUP BY ls2.name
    ) stage_count;
END;
$$ LANGUAGE plpgsql;

-- Function to get task statistics
CREATE OR REPLACE FUNCTION get_task_stats()
RETURNS TABLE(
    total_tasks BIGINT,
    completed_tasks BIGINT,
    pending_tasks BIGINT,
    overdue_tasks BIGINT,
    tasks_by_type JSONB
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_tasks,
        COUNT(*) FILTER (WHERE is_completed = true) as completed_tasks,
        COUNT(*) FILTER (WHERE is_completed = false) as pending_tasks,
        COUNT(*) FILTER (WHERE is_completed = false AND due_date < CURRENT_DATE) as overdue_tasks,
        jsonb_object_agg(tt.name, type_count.count) as tasks_by_type
    FROM task t
    JOIN task_type tt ON t.task_type_id = tt.id
    CROSS JOIN (
        SELECT tt2.name, COUNT(t2.id) as count
        FROM task_type tt2
        LEFT JOIN task t2 ON tt2.id = t2.task_type_id
        GROUP BY tt2.name
    ) type_count;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- Additional Sample Data
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


INSERT INTO customer (name, email, phone, company, source, description, created_by, is_active) VALUES
('John Smith', 'john.smith@techcorp.com', '+1-555-0101', 'TechCorp Solutions', 'Website', 'Senior IT Manager interested in enterprise solutions', 1, true),
('Sarah Johnson', 'sarah.j@innovateinc.com', '+1-555-0102', 'Innovate Inc', 'Referral', 'VP of Operations looking for process automation', 1, true),
('Michael Chen', 'mchen@startupxyz.com', '+1-555-0103', 'StartupXYZ', 'Cold Call', 'Founder seeking CRM solution for growing team', 1, true),
('Emily Davis', 'emily.davis@globaltech.com', '+1-555-0104', 'GlobalTech Industries', 'Trade Show', 'Director of Sales Operations', 1, true),
('David Wilson', 'dwilson@consultpro.com', '+1-555-0105', 'ConsultPro Group', 'Website', 'Managing Partner interested in client management tools', 1, true),
('Lisa Anderson', 'lisa.anderson@retailplus.com', '+1-555-0106', 'RetailPlus Chain', 'Referral', 'Operations Manager for retail chain', 1, true),
('Robert Taylor', 'rtaylor@manufacturing.com', '+1-555-0107', 'Manufacturing Co', 'Cold Call', 'Plant Manager looking for inventory management', 1, true),
('Jennifer Brown', 'jbrown@healthcare.org', '+1-555-0108', 'Healthcare Solutions', 'Website', 'IT Director for healthcare network', 1, true),
('Christopher Lee', 'clee@fintech.com', '+1-555-0109', 'FinTech Innovations', 'Trade Show', 'CTO seeking compliance management tools', 1, true),
('Amanda Garcia', 'agarcia@education.edu', '+1-555-0110', 'Education Institute', 'Referral', 'Administrative Director for educational institution', 1, true);

-- =====================================================
-- Insert Real Lead Data
-- =====================================================

INSERT INTO lead (title, customer_id, assigned_to, status, current_stage_id, expected_close_date, value, additional_notes) VALUES
('Enterprise CRM Implementation', 1, 2, 'Qualified', 2, CURRENT_DATE + INTERVAL '45 days', 150000.00, 'Large enterprise looking for comprehensive CRM solution. Budget approved, decision makers identified.'),
('Process Automation Project', 2, 3, 'Proposal', 3, CURRENT_DATE + INTERVAL '30 days', 85000.00, 'Looking to automate sales and marketing processes. Technical requirements gathered.'),
('Startup CRM Setup', 3, 4, 'Lead', 1, CURRENT_DATE + INTERVAL '60 days', 25000.00, 'Early-stage startup with 15 employees. Need scalable solution.'),
('Sales Operations Platform', 4, 2, 'Negotiation', 4, CURRENT_DATE + INTERVAL '15 days', 120000.00, 'Multi-national company. Contract terms being finalized.'),
('Consulting Practice Management', 5, 3, 'Qualified', 2, CURRENT_DATE + INTERVAL '40 days', 65000.00, 'Growing consulting practice needs client management system.'),
('Retail Chain Integration', 6, 4, 'Lead', 1, CURRENT_DATE + INTERVAL '90 days', 200000.00, 'Large retail chain with 50+ locations. Complex integration requirements.'),
('Manufacturing Inventory System', 7, 2, 'Proposal', 3, CURRENT_DATE + INTERVAL '35 days', 95000.00, 'Manufacturing company needs inventory and order management.'),
('Healthcare Compliance Platform', 8, 3, 'Qualified', 2, CURRENT_DATE + INTERVAL '50 days', 180000.00, 'Healthcare network requires HIPAA-compliant solution.'),
('FinTech Compliance Management', 9, 4, 'Negotiation', 4, CURRENT_DATE + INTERVAL '20 days', 140000.00, 'FinTech startup needs regulatory compliance tools.'),
('Educational Institution CRM', 10, 2, 'Lead', 1, CURRENT_DATE + INTERVAL '75 days', 75000.00, 'Educational institution needs student and alumni management.');

-- =====================================================
-- Insert Real Task Data
-- =====================================================

INSERT INTO task (lead_id, customer_id, assigned_to, task_type_id, title, description, due_date, is_completed, is_started) VALUES
(1, 1, 2, 3, 'Product Demo for TechCorp', 'Schedule and conduct comprehensive product demonstration for John Smith and his team', CURRENT_DATE + INTERVAL '5 days', false, true),
(1, 1, 2, 6, 'Prepare Proposal for TechCorp', 'Create detailed proposal including pricing, timeline, and implementation plan', CURRENT_DATE + INTERVAL '7 days', false, false),
(2, 2, 3, 2, 'Follow-up Call with Sarah', 'Call Sarah to discuss technical requirements and answer questions about automation features', CURRENT_DATE + INTERVAL '2 days', false, true),
(2, 2, 3, 4, 'Requirements Gathering Meeting', 'Schedule meeting with Sarah and her technical team to gather detailed requirements', CURRENT_DATE + INTERVAL '10 days', false, false),
(3, 3, 4, 1, 'Initial Discovery Call', 'Conduct initial discovery call with Michael to understand startup needs and budget', CURRENT_DATE + INTERVAL '1 day', false, true),
(3, 3, 4, 5, 'Send Startup Package Info', 'Email Michael with startup-specific pricing and features information', CURRENT_DATE + INTERVAL '3 days', false, false),
(4, 4, 2, 6, 'Contract Review Meeting', 'Review contract terms with Emily and legal team', CURRENT_DATE + INTERVAL '5 days', false, true),
(4, 4, 2, 4, 'Final Negotiation Call', 'Final negotiation call to discuss pricing and terms', CURRENT_DATE + INTERVAL '8 days', false, false),
(5, 5, 3, 3, 'Demo for ConsultPro', 'Conduct product demonstration for David and his partners', CURRENT_DATE + INTERVAL '12 days', false, false),
(5, 5, 3, 2, 'Follow-up on Demo Questions', 'Address any questions from the demo and provide additional information', CURRENT_DATE + INTERVAL '15 days', false, false),
(6, 6, 4, 1, 'Initial Retail Assessment', 'Conduct initial assessment of retail chain requirements and current systems', CURRENT_DATE + INTERVAL '7 days', false, true),
(6, 6, 4, 4, 'Stakeholder Meeting', 'Meet with key stakeholders from retail chain to discuss integration needs', CURRENT_DATE + INTERVAL '14 days', false, false),
(7, 7, 2, 3, 'Manufacturing Demo', 'Demonstrate inventory management features to Robert and his team', CURRENT_DATE + INTERVAL '8 days', false, false),
(7, 7, 2, 6, 'Manufacturing Proposal', 'Prepare detailed proposal for manufacturing inventory system', CURRENT_DATE + INTERVAL '12 days', false, false),
(8, 8, 3, 2, 'Healthcare Compliance Call', 'Discuss HIPAA compliance requirements with Jennifer', CURRENT_DATE + INTERVAL '3 days', false, true),
(8, 8, 3, 4, 'Technical Architecture Review', 'Review technical architecture with healthcare IT team', CURRENT_DATE + INTERVAL '10 days', false, false),
(9, 9, 4, 6, 'FinTech Contract Finalization', 'Finalize contract terms for compliance management platform', CURRENT_DATE + INTERVAL '5 days', false, true),
(9, 9, 4, 2, 'Compliance Feature Demo', 'Demonstrate compliance features to Christopher and compliance team', CURRENT_DATE + INTERVAL '8 days', false, false),
(10, 10, 2, 1, 'Education Institution Discovery', 'Conduct discovery call with Amanda to understand educational needs', CURRENT_DATE + INTERVAL '4 days', false, true),
(10, 10, 2, 5, 'Send Education Package', 'Email Amanda with education-specific features and pricing', CURRENT_DATE + INTERVAL '6 days', false, false);

-- =====================================================
-- Insert Real Interaction History Data
-- =====================================================

INSERT INTO interaction_history (customer_id, user_id, interaction_type, notes, interaction_date) VALUES
(1, 2, 'Call', 'Initial contact with John Smith. Discussed current pain points with existing CRM system. Customer is interested in enterprise features.', CURRENT_DATE - INTERVAL '5 days'),
(1, 2, 'Email', 'Sent product brochure and case studies for similar enterprise implementations.', CURRENT_DATE - INTERVAL '4 days'),
(1, 2, 'Meeting', 'Conducted discovery meeting with John and his IT team. Gathered technical requirements and timeline expectations.', CURRENT_DATE - INTERVAL '2 days'),
(2, 3, 'Call', 'Cold call to Sarah Johnson. Discussed process automation needs. Customer was receptive and scheduled follow-up.', CURRENT_DATE - INTERVAL '7 days'),
(2, 3, 'Email', 'Sent automation case studies and ROI calculator to Sarah.', CURRENT_DATE - INTERVAL '6 days'),
(2, 3, 'Meeting', 'Requirements gathering meeting with Sarah and operations team. Identified key automation opportunities.', CURRENT_DATE - INTERVAL '3 days'),
(3, 4, 'Call', 'Initial contact with Michael Chen. Discussed startup challenges and growth plans. Budget range established.', CURRENT_DATE - INTERVAL '4 days'),
(3, 4, 'Email', 'Sent startup package information and pricing to Michael.', CURRENT_DATE - INTERVAL '3 days'),
(4, 2, 'Call', 'Follow-up call with Emily Davis. Discussed sales operations challenges and current system limitations.', CURRENT_DATE - INTERVAL '6 days'),
(4, 2, 'Meeting', 'Product demonstration for Emily and her team. Received positive feedback on features.', CURRENT_DATE - INTERVAL '4 days'),
(4, 2, 'Call', 'Contract discussion with Emily and procurement team. Pricing and terms negotiated.', CURRENT_DATE - INTERVAL '2 days'),
(5, 3, 'Call', 'Initial contact with David Wilson. Discussed consulting practice management needs.', CURRENT_DATE - INTERVAL '8 days'),
(5, 3, 'Email', 'Sent consulting practice case studies and feature overview to David.', CURRENT_DATE - INTERVAL '7 days'),
(6, 4, 'Call', 'Cold call to Lisa Anderson. Discussed retail chain operations and integration needs.', CURRENT_DATE - INTERVAL '5 days'),
(6, 4, 'Email', 'Sent retail integration case studies and technical overview to Lisa.', CURRENT_DATE - INTERVAL '4 days'),
(7, 2, 'Call', 'Initial contact with Robert Taylor. Discussed manufacturing inventory challenges.', CURRENT_DATE - INTERVAL '6 days'),
(7, 2, 'Email', 'Sent manufacturing case studies and inventory management features to Robert.', CURRENT_DATE - INTERVAL '5 days'),
(8, 3, 'Call', 'Initial contact with Jennifer Brown. Discussed healthcare compliance requirements and HIPAA considerations.', CURRENT_DATE - INTERVAL '4 days'),
(8, 3, 'Email', 'Sent healthcare compliance documentation and security features to Jennifer.', CURRENT_DATE - INTERVAL '3 days'),
(9, 4, 'Call', 'Follow-up call with Christopher Lee. Discussed FinTech compliance requirements and regulatory needs.', CURRENT_DATE - INTERVAL '5 days'),
(9, 4, 'Meeting', 'Compliance feature demonstration for Christopher and compliance team.', CURRENT_DATE - INTERVAL '3 days'),
(10, 2, 'Call', 'Initial contact with Amanda Garcia. Discussed educational institution needs and student management requirements.', CURRENT_DATE - INTERVAL '3 days'),
(10, 2, 'Email', 'Sent education-specific features and pricing to Amanda.', CURRENT_DATE - INTERVAL '2 days');

-- =====================================================
-- Migration completed successfully
-- ===================================================== 