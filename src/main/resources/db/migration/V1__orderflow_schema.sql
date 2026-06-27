CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    username VARCHAR(255),
    is_active BOOLEAN,
    remember_token VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS ai_models (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    display_name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_envs (
    id UUID PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS mcp_servers (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    url VARCHAR(255),
    sse_endpoint VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent_brains (
    id UUID PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS assistant_agents (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id UUID,
    skills TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS agent_responses (
    id UUID PRIMARY KEY,
    agent_name VARCHAR(255),
    skill VARCHAR(255),
    correlation_type VARCHAR(80),
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

CREATE TABLE IF NOT EXISTS spring_ai_chat_memory (
    conversation_id VARCHAR(36) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')),
    "timestamp" TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS spring_ai_chat_memory_conversation_id_timestamp_idx
    ON spring_ai_chat_memory(conversation_id, "timestamp");

CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY,
    organization_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS app_users (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    email VARCHAR(255),
    password_hash VARCHAR(255),
    display_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_app_users_org_email
    ON app_users(organization_id, lower(email))
    WHERE email IS NOT NULL;

CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    sales_owner_user_id UUID REFERENCES app_users(id),
    customer_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    customer_type VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    default_price_tier VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    note TEXT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_customers_org_code ON customers(organization_id, customer_code);

CREATE TABLE IF NOT EXISTS customer_projects (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    customer_id UUID NOT NULL REFERENCES customers(id),
    project_code VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    delivery_address TEXT,
    default_delivery_note TEXT,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS customer_credit_profiles (
    customer_id UUID PRIMARY KEY REFERENCES customers(id),
    credit_limit DECIMAL(18,2) NOT NULL,
    current_debt DECIMAL(18,2) NOT NULL,
    overdue_debt DECIMAL(18,2) NOT NULL,
    pending_approved_order_amount DECIMAL(18,2) NOT NULL,
    payment_term_days INTEGER NOT NULL,
    updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS warehouses (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    warehouse_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_warehouses_org_code ON warehouses(organization_id, warehouse_code);

CREATE TABLE IF NOT EXISTS product_skus (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    sku_code VARCHAR(80) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_family VARCHAR(50) NOT NULL,
    material VARCHAR(50),
    brand VARCHAR(100),
    diameter_mm DECIMAL(10,2),
    nominal_size VARCHAR(50),
    size_system VARCHAR(30),
    pressure_class VARCHAR(50),
    thickness_mm DECIMAL(10,2),
    fitting_type VARCHAR(80),
    angle_degree INTEGER,
    thread_type VARCHAR(50),
    reducer_from_mm DECIMAL(10,2),
    reducer_to_mm DECIMAL(10,2),
    length_m DECIMAL(10,2),
    sell_unit VARCHAR(30) NOT NULL,
    base_unit VARCHAR(30) NOT NULL,
    units_per_sell_unit DECIMAL(18,4) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_product_skus_org_code ON product_skus(organization_id, sku_code);

CREATE TABLE IF NOT EXISTS product_aliases (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    alias_text VARCHAR(255) NOT NULL,
    normalized_alias VARCHAR(255) NOT NULL,
    sku_id UUID REFERENCES product_skus(id),
    product_family VARCHAR(50),
    material VARCHAR(50),
    brand VARCHAR(100),
    diameter_mm DECIMAL(10,2),
    pressure_class VARCHAR(50),
    fitting_type VARCHAR(80),
    thread_type VARCHAR(50),
    confidence_weight DECIMAL(5,4) NOT NULL,
    note TEXT,
    active BOOLEAN,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_product_aliases_org_normalized ON product_aliases(organization_id, normalized_alias);

CREATE TABLE IF NOT EXISTS price_lists (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    price_list_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    customer_id UUID REFERENCES customers(id),
    price_tier VARCHAR(50),
    valid_from DATE NOT NULL,
    valid_to DATE,
    priority INTEGER NOT NULL,
    active BOOLEAN,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_price_lists_lookup ON price_lists(organization_id, customer_id, price_tier, active);

CREATE TABLE IF NOT EXISTS sku_prices (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    price_list_id UUID NOT NULL REFERENCES price_lists(id),
    sku_id UUID NOT NULL REFERENCES product_skus(id),
    min_quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    approval_floor_price DECIMAL(18,2),
    active BOOLEAN,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_sku_prices_lookup ON sku_prices(organization_id, price_list_id, sku_id, active);

CREATE TABLE IF NOT EXISTS inventory_balances (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    sku_id UUID NOT NULL REFERENCES product_skus(id),
    on_hand_quantity DECIMAL(18,4) NOT NULL,
    reserved_quantity DECIMAL(18,4) NOT NULL,
    available_quantity DECIMAL(18,4),
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_inventory_balances_sku_wh ON inventory_balances(organization_id, warehouse_id, sku_id);

CREATE TABLE IF NOT EXISTS raw_order_texts (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    source_channel VARCHAR(50) NOT NULL,
    raw_text TEXT NOT NULL,
    normalized_text TEXT,
    extraction_result JSONB,
    pasted_by_user_id UUID REFERENCES app_users(id),
    received_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS draft_orders (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    order_no VARCHAR(50) NOT NULL,
    raw_order_text_id UUID REFERENCES raw_order_texts(id),
    customer_id UUID REFERENCES customers(id),
    project_id UUID REFERENCES customer_projects(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    status VARCHAR(50) NOT NULL,
    requested_delivery_date DATE,
    delivery_note TEXT,
    total_amount DECIMAL(18,2) NOT NULL,
    clarification_question TEXT,
    created_by_user_id UUID REFERENCES app_users(id),
    ready_for_review_at TIMESTAMPTZ,
    approved_by_user_id UUID REFERENCES app_users(id),
    approved_at TIMESTAMPTZ,
    rejected_by_user_id UUID REFERENCES app_users(id),
    rejected_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_draft_orders_org_order_no ON draft_orders(organization_id, order_no);
CREATE INDEX IF NOT EXISTS idx_draft_orders_org_status ON draft_orders(organization_id, status);
CREATE INDEX IF NOT EXISTS idx_draft_orders_org_customer ON draft_orders(organization_id, customer_id);

CREATE TABLE IF NOT EXISTS draft_order_lines (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID NOT NULL REFERENCES draft_orders(id),
    line_no INTEGER NOT NULL,
    raw_line_text TEXT NOT NULL,
    item_description TEXT,
    quantity DECIMAL(18,4) NOT NULL,
    requested_unit VARCHAR(30),
    extracted_attributes JSONB NOT NULL,
    selected_sku_id UUID REFERENCES product_skus(id),
    selected_by_user_id UUID REFERENCES app_users(id),
    selected_at TIMESTAMPTZ,
    unit_price DECIMAL(18,2),
    price_source VARCHAR(100),
    line_amount DECIMAL(18,2),
    confidence_score DECIMAL(5,4),
    clarification_question TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_draft_order_lines_order_line_no ON draft_order_lines(draft_order_id, line_no);

CREATE TABLE IF NOT EXISTS sku_candidates (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_line_id UUID NOT NULL REFERENCES draft_order_lines(id),
    sku_id UUID NOT NULL REFERENCES product_skus(id),
    rank_no INTEGER NOT NULL,
    confidence_score DECIMAL(5,4) NOT NULL,
    match_reason TEXT,
    matched_attributes JSONB NOT NULL,
    missing_attributes JSONB NOT NULL,
    source VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_sku_candidates_line ON sku_candidates(draft_order_line_id, rank_no);

CREATE TABLE IF NOT EXISTS price_checks (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_line_id UUID NOT NULL REFERENCES draft_order_lines(id),
    sku_id UUID NOT NULL REFERENCES product_skus(id),
    price_list_id UUID REFERENCES price_lists(id),
    quantity DECIMAL(18,4),
    proposed_unit_price DECIMAL(18,2),
    reference_unit_price DECIMAL(18,2),
    approval_floor_price DECIMAL(18,2),
    status VARCHAR(20) NOT NULL,
    reason TEXT,
    checked_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS inventory_checks (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_line_id UUID NOT NULL REFERENCES draft_order_lines(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    sku_id UUID NOT NULL REFERENCES product_skus(id),
    requested_quantity DECIMAL(18,4),
    on_hand_quantity DECIMAL(18,4),
    reserved_quantity DECIMAL(18,4),
    available_quantity DECIMAL(18,4),
    status VARCHAR(20) NOT NULL,
    reason TEXT,
    checked_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS credit_checks (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID NOT NULL REFERENCES draft_orders(id),
    customer_id UUID NOT NULL REFERENCES customers(id),
    order_amount DECIMAL(18,2),
    credit_limit DECIMAL(18,2),
    current_debt DECIMAL(18,2),
    overdue_debt DECIMAL(18,2),
    pending_approved_order_amount DECIMAL(18,2),
    projected_debt DECIMAL(18,2),
    status VARCHAR(20) NOT NULL,
    reason TEXT,
    checked_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS order_holds (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID NOT NULL REFERENCES draft_orders(id),
    draft_order_line_id UUID REFERENCES draft_order_lines(id),
    hold_type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    rule_code VARCHAR(100),
    reason TEXT NOT NULL,
    payload JSONB,
    created_by_actor_type VARCHAR(30) NOT NULL,
    released_by_user_id UUID REFERENCES app_users(id),
    released_at TIMESTAMPTZ,
    release_note TEXT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_order_holds_order_status ON order_holds(draft_order_id, status);

CREATE TABLE IF NOT EXISTS review_actions (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID NOT NULL REFERENCES draft_orders(id),
    draft_order_line_id UUID REFERENCES draft_order_lines(id),
    action_type VARCHAR(80) NOT NULL,
    comment TEXT,
    before_data JSONB,
    after_data JSONB,
    actor_user_id UUID NOT NULL REFERENCES app_users(id),
    created_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS inventory_reservations (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    sku_id UUID NOT NULL REFERENCES product_skus(id),
    draft_order_line_id UUID NOT NULL REFERENCES draft_order_lines(id),
    quantity DECIMAL(18,4),
    status VARCHAR(30),
    reserved_at TIMESTAMPTZ,
    released_at TIMESTAMPTZ,
    consumed_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS processing_events (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID NOT NULL REFERENCES draft_orders(id),
    stage VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    duration_ms INTEGER,
    metadata JSONB
);

CREATE INDEX IF NOT EXISTS idx_processing_events_order ON processing_events(draft_order_id, started_at);

CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID REFERENCES draft_orders(id),
    aggregate_type VARCHAR(80) NOT NULL,
    aggregate_id UUID NOT NULL,
    actor_type VARCHAR(30) NOT NULL,
    actor_user_id UUID REFERENCES app_users(id),
    event_type VARCHAR(100) NOT NULL,
    before_data JSONB,
    after_data JSONB,
    metadata JSONB,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_audit_events_order ON audit_events(draft_order_id, created_at);

CREATE TABLE IF NOT EXISTS draft_order_documents (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    draft_order_id UUID NOT NULL REFERENCES draft_orders(id),
    document_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    html_snapshot TEXT,
    pdf_path TEXT,
    generated_by_user_id UUID REFERENCES app_users(id),
    generated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_draft_order_documents_order ON draft_order_documents(draft_order_id, document_type);
