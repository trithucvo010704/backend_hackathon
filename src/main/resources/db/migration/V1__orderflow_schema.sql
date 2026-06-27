CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    username VARCHAR(255),
    is_active BOOLEAN,
    remember_token VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE ai_models (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    display_name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE ai_envs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    service_account_json TEXT,
    production_mode BOOLEAN NOT NULL,
    api_key TEXT,
    location VARCHAR(255),
    project_id VARCHAR(255),
    enabled BOOLEAN NOT NULL,
    priority INTEGER NOT NULL,
    last_used_at TIMESTAMP,
    cooldown_until TIMESTAMP,
    failure_count INTEGER NOT NULL,
    last_error TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE mcp_servers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    url VARCHAR(255),
    sse_endpoint VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE agent_brains (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255),
    model_id UUID,
    env_id UUID,
    system_instruction TEXT,
    temperature DOUBLE PRECISION,
    thinking_level VARCHAR(255),
    google_search BOOLEAN,
    include_thoughts BOOLEAN,
    mcp_server_ids TEXT,
    prompt_template TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE assistant_agents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    category_id UUID,
    skills TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE agent_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_name VARCHAR(255),
    skill VARCHAR(255),
    correlation_type VARCHAR(255),
    correlation_id UUID,
    provider VARCHAR(64),
    model_name VARCHAR(255),
    duration_ms INTEGER,
    finish_reason VARCHAR(255),
    brain_id UUID,
    prompt TEXT,
    input TEXT,
    output TEXT,
    output_text TEXT,
    tools TEXT,
    error TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE app_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    email VARCHAR(255),
    password_hash VARCHAR(255),
    display_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    sales_owner_user_id UUID,
    customer_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    customer_type VARCHAR(50) NOT NULL,
    phone VARCHAR(255),
    address VARCHAR(255),
    default_price_tier VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE customer_projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    project_code VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    delivery_address VARCHAR(255),
    default_delivery_note VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE customer_credit_profiles (
    customer_id UUID PRIMARY KEY,
    credit_limit NUMERIC NOT NULL,
    current_debt NUMERIC NOT NULL,
    overdue_debt NUMERIC NOT NULL,
    pending_approved_order_amount NUMERIC NOT NULL,
    payment_term_days INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    warehouse_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE product_skus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    sku_code VARCHAR(80) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_family VARCHAR(255) NOT NULL,
    material VARCHAR(255),
    brand VARCHAR(255),
    diameter_mm NUMERIC,
    nominal_size VARCHAR(255),
    size_system VARCHAR(255),
    pressure_class VARCHAR(255),
    thickness_mm NUMERIC,
    fitting_type VARCHAR(255),
    angle_degree INTEGER,
    thread_type VARCHAR(255),
    reducer_from_mm NUMERIC,
    reducer_to_mm NUMERIC,
    length_m NUMERIC,
    sell_unit VARCHAR(255) NOT NULL,
    base_unit VARCHAR(255) NOT NULL,
    units_per_sell_unit NUMERIC NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE product_aliases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    alias_text VARCHAR(255) NOT NULL,
    normalized_alias VARCHAR(255) NOT NULL,
    sku_id UUID,
    product_family VARCHAR(255),
    material VARCHAR(255),
    brand VARCHAR(255),
    diameter_mm NUMERIC,
    pressure_class VARCHAR(255),
    fitting_type VARCHAR(255),
    thread_type VARCHAR(255),
    confidence_weight NUMERIC NOT NULL,
    note VARCHAR(255),
    active BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE price_lists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    price_list_code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    customer_id UUID,
    price_tier VARCHAR(255),
    valid_from DATE NOT NULL,
    valid_to DATE,
    priority INTEGER NOT NULL,
    active BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE sku_prices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    price_list_id UUID NOT NULL,
    sku_id UUID NOT NULL,
    min_quantity NUMERIC NOT NULL,
    unit_price NUMERIC NOT NULL,
    approval_floor_price NUMERIC,
    active BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE inventory_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    sku_id UUID NOT NULL,
    on_hand_quantity NUMERIC NOT NULL,
    reserved_quantity NUMERIC NOT NULL,
    available_quantity NUMERIC,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE raw_order_texts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    source_channel VARCHAR(255) NOT NULL,
    raw_text TEXT NOT NULL,
    normalized_text TEXT,
    extraction_result JSONB,
    pasted_by_user_id UUID,
    received_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE draft_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    order_no VARCHAR(255) NOT NULL,
    raw_order_text_id UUID,
    customer_id UUID,
    project_id UUID,
    warehouse_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    requested_delivery_date DATE,
    delivery_note TEXT,
    total_amount NUMERIC NOT NULL,
    clarification_question TEXT,
    created_by_user_id UUID,
    ready_for_review_at TIMESTAMP WITH TIME ZONE,
    approved_by_user_id UUID,
    approved_at TIMESTAMP WITH TIME ZONE,
    rejected_by_user_id UUID,
    rejected_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE draft_order_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID NOT NULL,
    line_no INTEGER NOT NULL,
    raw_line_text TEXT NOT NULL,
    item_description TEXT,
    quantity NUMERIC NOT NULL,
    requested_unit VARCHAR(255),
    extracted_attributes JSONB NOT NULL,
    selected_sku_id UUID,
    selected_by_user_id UUID,
    selected_at TIMESTAMP WITH TIME ZONE,
    unit_price NUMERIC,
    price_source VARCHAR(255),
    line_amount NUMERIC,
    confidence_score NUMERIC,
    clarification_question TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE sku_candidates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_line_id UUID NOT NULL,
    sku_id UUID NOT NULL,
    rank_no INTEGER NOT NULL,
    confidence_score NUMERIC NOT NULL,
    match_reason TEXT,
    matched_attributes JSONB NOT NULL,
    missing_attributes JSONB NOT NULL,
    source VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE price_checks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_line_id UUID NOT NULL,
    sku_id UUID NOT NULL,
    price_list_id UUID,
    quantity NUMERIC,
    proposed_unit_price NUMERIC,
    reference_unit_price NUMERIC,
    approval_floor_price NUMERIC,
    status VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    checked_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE inventory_checks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_line_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    sku_id UUID NOT NULL,
    requested_quantity NUMERIC,
    on_hand_quantity NUMERIC,
    reserved_quantity NUMERIC,
    available_quantity NUMERIC,
    status VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    checked_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE credit_checks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    order_amount NUMERIC,
    credit_limit NUMERIC,
    current_debt NUMERIC,
    overdue_debt NUMERIC,
    pending_approved_order_amount NUMERIC,
    projected_debt NUMERIC,
    status VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    checked_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE order_holds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID NOT NULL,
    draft_order_line_id UUID,
    hold_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    rule_code VARCHAR(255),
    reason TEXT NOT NULL,
    payload JSONB,
    created_by_actor_type VARCHAR(50) NOT NULL,
    released_by_user_id UUID,
    released_at TIMESTAMP WITH TIME ZONE,
    release_note VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE inventory_reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    sku_id UUID NOT NULL,
    draft_order_line_id UUID NOT NULL,
    quantity NUMERIC,
    status VARCHAR(50),
    reserved_at TIMESTAMP WITH TIME ZONE,
    released_at TIMESTAMP WITH TIME ZONE,
    consumed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE draft_order_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    html_snapshot TEXT,
    pdf_path VARCHAR(255),
    generated_by_user_id UUID,
    generated_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE review_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID NOT NULL,
    draft_order_line_id UUID,
    action_type VARCHAR(255) NOT NULL,
    comment VARCHAR(255),
    before_data JSONB,
    after_data JSONB,
    actor_user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id UUID NOT NULL,
    actor_type VARCHAR(50) NOT NULL,
    actor_user_id UUID,
    event_type VARCHAR(255) NOT NULL,
    before_data JSONB,
    after_data JSONB,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE processing_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    draft_order_id UUID NOT NULL,
    stage VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    finished_at TIMESTAMP WITH TIME ZONE,
    duration_ms INTEGER,
    metadata JSONB
);

CREATE TABLE spring_ai_chat_memory (
    conversation_id VARCHAR(36) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),
    "timestamp" TIMESTAMP NOT NULL
);

CREATE INDEX idx_app_users_email ON app_users (lower(email));
CREATE INDEX idx_customers_org_code ON customers (organization_id, customer_code);
CREATE INDEX idx_product_skus_org_code ON product_skus (organization_id, sku_code);
CREATE INDEX idx_product_aliases_org_active ON product_aliases (organization_id, active);
CREATE INDEX idx_draft_orders_org_status ON draft_orders (organization_id, status, created_at);
CREATE INDEX idx_draft_order_lines_order ON draft_order_lines (draft_order_id, line_no);
CREATE INDEX idx_order_holds_order_status ON order_holds (draft_order_id, status);
CREATE INDEX idx_inventory_balances_lookup ON inventory_balances (organization_id, warehouse_id, sku_id);
CREATE INDEX spring_ai_chat_memory_conversation_id_timestamp_idx
    ON spring_ai_chat_memory(conversation_id, "timestamp");
