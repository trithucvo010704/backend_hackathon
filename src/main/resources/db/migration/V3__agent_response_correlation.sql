ALTER TABLE agent_responses
    ADD COLUMN IF NOT EXISTS agent_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS correlation_type VARCHAR(80),
    ADD COLUMN IF NOT EXISTS correlation_id UUID,
    ADD COLUMN IF NOT EXISTS provider VARCHAR(64),
    ADD COLUMN IF NOT EXISTS model_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS duration_ms INTEGER;

CREATE INDEX IF NOT EXISTS idx_agent_responses_correlation
    ON agent_responses(correlation_type, correlation_id);
