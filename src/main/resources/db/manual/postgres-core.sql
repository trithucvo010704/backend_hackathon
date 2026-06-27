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
    skill VARCHAR(255),
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
