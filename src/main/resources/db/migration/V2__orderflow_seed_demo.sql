INSERT INTO organizations (id, organization_code, name, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'OF-DEMO', 'OrderFlow Demo Distributor', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO app_users (id, organization_id, email, password_hash, display_name, role, active, created_at, updated_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '11111111-1111-1111-1111-111111111111', 'sale.admin@orderflow.local', '$2a$10$V/kHDrlkUIw8LCy5juht1uRKhXEHTb6nXMGiN6J4pf8WkIxi1GYFK', 'Sale Admin Demo', 'SALE_ADMIN', true, now(), now()),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', '11111111-1111-1111-1111-111111111111', 'manager@orderflow.local', '$2a$10$V/kHDrlkUIw8LCy5juht1uRKhXEHTb6nXMGiN6J4pf8WkIxi1GYFK', 'Manager Demo', 'MANAGER', true, now(), now()),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', '11111111-1111-1111-1111-111111111111', 'system@orderflow.local', '$2a$10$V/kHDrlkUIw8LCy5juht1uRKhXEHTb6nXMGiN6J4pf8WkIxi1GYFK', 'System', 'SYSTEM', true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO ai_models (id, name, provider, display_name, created_at, updated_at)
VALUES ('22222222-2222-2222-2222-222222222201', 'gpt-4.1-mini', 'OPENAI', 'OpenAI GPT 4.1 Mini', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO ai_envs (id, name, provider, production_mode, enabled, priority, failure_count, created_at, updated_at)
VALUES ('22222222-2222-2222-2222-222222222202', 'local-openai-env', 'OPENAI', false, true, 1, 0, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO agent_brains (id, name, model_id, env_id, system_instruction, temperature, thinking_level, prompt_template, created_at, updated_at)
VALUES
    ('22222222-2222-2222-2222-222222222211', 'Order Extraction Brain', '22222222-2222-2222-2222-222222222201', '22222222-2222-2222-2222-222222222202', 'Extract Vietnamese plumbing supply order text into strict structured JSON. Never approve or decide business rules.', 0.1, 'LOW', 'Extract raw order text into lines, attributes, missing information, and confidence.', now(), now()),
    ('22222222-2222-2222-2222-222222222212', 'SKU Matching Brain', '22222222-2222-2222-2222-222222222201', '22222222-2222-2222-2222-222222222202', 'Explain SKU candidates only. Final SKU selection belongs to deterministic rules and human review.', 0.1, 'LOW', 'Explain candidate match reasons from catalog attributes.', now(), now()),
    ('22222222-2222-2222-2222-222222222213', 'Rule Check Brain', null, null, 'Deterministic Java rule node for price, inventory, and credit checks.', 0.0, null, 'No LLM call. Rule engine owns decisions.', now(), now()),
    ('22222222-2222-2222-2222-222222222214', 'Review Workbench Brain', null, null, 'Human review workflow logger.', 0.0, null, 'No LLM call. Record user decisions.', now(), now()),
    ('22222222-2222-2222-2222-222222222215', 'Document Brain', null, null, 'Deterministic quote and pick-list HTML generator.', 0.0, null, 'No LLM call. Generate HTML snapshots.', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO assistant_agents (id, name, skills, created_at, updated_at)
VALUES
    ('22222222-2222-2222-2222-222222222221', 'ORDERFLOW_ORCHESTRATOR', '{"orchestrate":"22222222-2222-2222-2222-222222222213"}', now(), now()),
    ('22222222-2222-2222-2222-222222222222', 'ORDER_EXTRACTION_AGENT', '{"extract":"22222222-2222-2222-2222-222222222211"}', now(), now()),
    ('22222222-2222-2222-2222-222222222223', 'SKU_MATCHING_AGENT', '{"match":"22222222-2222-2222-2222-222222222212"}', now(), now()),
    ('22222222-2222-2222-2222-222222222224', 'RULE_CHECK_AGENT', '{"check":"22222222-2222-2222-2222-222222222213"}', now(), now()),
    ('22222222-2222-2222-2222-222222222225', 'REVIEW_WORKBENCH_AGENT', '{"review":"22222222-2222-2222-2222-222222222214"}', now(), now()),
    ('22222222-2222-2222-2222-222222222226', 'DOCUMENT_AGENT', '{"generate":"22222222-2222-2222-2222-222222222215"}', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO customers (id, organization_id, sales_owner_user_id, customer_code, name, customer_type, phone, address, default_price_tier, status, note, created_at, updated_at)
VALUES
    ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'C-MINHANH', 'Công ty Minh Anh', 'CONTRACTOR', '0901000001', 'Quận 7, TP.HCM', 'STANDARD', 'ACTIVE', 'Khách công trình demo đủ hạn mức', now(), now()),
    ('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'C-DAILYAN', 'Đại lý An Phát', 'DEALER', '0901000002', 'Bình Tân, TP.HCM', 'DEALER', 'ACTIVE', 'Khách giá đại lý', now(), now()),
    ('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'C-NOQUAHAN', 'Nhà thầu Nợ Quá Hạn', 'CONTRACTOR', '0901000003', 'Thủ Đức, TP.HCM', 'STANDARD', 'ACTIVE', 'Dùng để demo credit hold', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO customer_projects (id, organization_id, customer_id, project_code, name, delivery_address, default_delivery_note, active, created_at, updated_at)
VALUES
    ('33333333-3333-3333-3333-333333333311', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', 'P-Q7', 'Công trình Quận 7', 'Đường Nguyễn Văn Linh, Quận 7', 'Giao giờ hành chính', true, now(), now()),
    ('33333333-3333-3333-3333-333333333312', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333302', 'P-BT', 'Kho đại lý Bình Tân', 'KCN Tân Tạo, Bình Tân', 'Gọi trước 30 phút', true, now(), now()),
    ('33333333-3333-3333-3333-333333333313', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333303', 'P-TD', 'Công trình Thủ Đức', 'Đường Võ Nguyên Giáp, Thủ Đức', 'Công trình đang nợ quá hạn', true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO customer_credit_profiles (customer_id, credit_limit, current_debt, overdue_debt, pending_approved_order_amount, payment_term_days, updated_at)
VALUES
    ('33333333-3333-3333-3333-333333333301', 100000000.00, 15000000.00, 0.00, 0.00, 30, now()),
    ('33333333-3333-3333-3333-333333333302', 150000000.00, 50000000.00, 0.00, 10000000.00, 45, now()),
    ('33333333-3333-3333-3333-333333333303', 30000000.00, 29000000.00, 5000000.00, 0.00, 15, now())
ON CONFLICT (customer_id) DO NOTHING;

INSERT INTO warehouses (id, organization_id, warehouse_code, name, address, active, created_at, updated_at)
VALUES ('44444444-4444-4444-4444-444444444401', '11111111-1111-1111-1111-111111111111', 'WH-MAIN', 'Kho chính', 'Bình Chánh, TP.HCM', true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_skus (id, organization_id, sku_code, product_name, product_family, material, brand, diameter_mm, nominal_size, size_system, pressure_class, thickness_mm, fitting_type, angle_degree, thread_type, reducer_from_mm, reducer_to_mm, length_m, sell_unit, base_unit, units_per_sell_unit, active, created_at, updated_at)
VALUES
    ('55555555-5555-5555-5555-555555555501', '11111111-1111-1111-1111-111111111111', 'PVC-BM-D21-PN8-4M', 'Ống PVC Bình Minh phi 21 PN8 cây 4m', 'PIPE', 'PVC', 'Bình Minh', 21, 'D21', 'METRIC', 'PN8', null, null, null, null, null, null, 4, 'cây', 'mét', 4, true, now(), now()),
    ('55555555-5555-5555-5555-555555555502', '11111111-1111-1111-1111-111111111111', 'PVC-BM-D27-PN8-4M', 'Ống PVC Bình Minh phi 27 PN8 cây 4m', 'PIPE', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', 'PN8', null, null, null, null, null, null, 4, 'cây', 'mét', 4, true, now(), now()),
    ('55555555-5555-5555-5555-555555555503', '11111111-1111-1111-1111-111111111111', 'PVC-BM-D34-PN8-4M', 'Ống PVC Bình Minh phi 34 PN8 cây 4m', 'PIPE', 'PVC', 'Bình Minh', 34, 'D34', 'METRIC', 'PN8', null, null, null, null, null, null, 4, 'cây', 'mét', 4, true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_skus (id, organization_id, sku_code, product_name, product_family, material, brand, diameter_mm, nominal_size, size_system, pressure_class, thickness_mm, fitting_type, angle_degree, thread_type, reducer_from_mm, reducer_to_mm, length_m, sell_unit, base_unit, units_per_sell_unit, active, created_at, updated_at)
VALUES
    ('55555555-5555-5555-5555-555555555504', '11111111-1111-1111-1111-111111111111', 'PPR-D25-PN20-4M', 'Ống PP-R nóng phi 25 PN20 cây 4m', 'PIPE', 'PPR', 'Tiền Phong', 25, 'D25', 'METRIC', 'PN20', null, null, null, null, null, null, 4, 'cây', 'mét', 4, true, now(), now()),
    ('55555555-5555-5555-5555-555555555505', '11111111-1111-1111-1111-111111111111', 'PPR-D32-PN20-4M', 'Ống PP-R nóng phi 32 PN20 cây 4m', 'PIPE', 'PPR', 'Tiền Phong', 32, 'D32', 'METRIC', 'PN20', null, null, null, null, null, null, 4, 'cây', 'mét', 4, true, now(), now()),
    ('55555555-5555-5555-5555-555555555506', '11111111-1111-1111-1111-111111111111', 'HDPE-D32-PN10', 'Ống HDPE phi 32 PN10', 'PIPE', 'HDPE', 'Đệ Nhất', 32, 'D32', 'METRIC', 'PN10', null, null, null, null, null, null, null, 'mét', 'mét', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555507', '11111111-1111-1111-1111-111111111111', 'CO90-PVC-D21', 'Co 90 PVC phi 21', 'FITTING', 'PVC', 'Bình Minh', 21, 'D21', 'METRIC', null, null, 'CO', 90, null, null, null, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555508', '11111111-1111-1111-1111-111111111111', 'CO90-PVC-D27', 'Co 90 PVC phi 27', 'FITTING', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', null, null, 'CO', 90, null, null, null, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555509', '11111111-1111-1111-1111-111111111111', 'CO45-PVC-D27', 'Co 45 PVC phi 27', 'FITTING', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', null, null, 'CO', 45, null, null, null, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555510', '11111111-1111-1111-1111-111111111111', 'CORT-PVC-D27', 'Co ren trong PVC phi 27', 'FITTING', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', null, null, 'CO', 90, 'REN_TRONG', null, null, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555511', '11111111-1111-1111-1111-111111111111', 'CORN-PVC-D27', 'Co ren ngoài PVC phi 27', 'FITTING', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', null, null, 'CO', 90, 'REN_NGOAI', null, null, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555512', '11111111-1111-1111-1111-111111111111', 'TE-PVC-D27', 'Tê đều PVC phi 27', 'FITTING', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', null, null, 'TE', null, null, null, null, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555513', '11111111-1111-1111-1111-111111111111', 'TEG-PVC-D34-D27', 'Tê giảm PVC 34-27', 'FITTING', 'PVC', 'Bình Minh', null, 'D34-D27', 'METRIC', null, null, 'TE_GIAM', null, null, 34, 27, null, 'cái', 'cái', 1, true, now(), now()),
    ('55555555-5555-5555-5555-555555555514', '11111111-1111-1111-1111-111111111111', 'NOI-PVC-D27', 'Nối thẳng PVC phi 27', 'FITTING', 'PVC', 'Bình Minh', 27, 'D27', 'METRIC', null, null, 'NOI', null, null, null, null, null, 'cái', 'cái', 1, true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO product_aliases (id, organization_id, alias_text, normalized_alias, sku_id, product_family, material, brand, diameter_mm, pressure_class, fitting_type, thread_type, confidence_weight, note, active, created_at, updated_at)
VALUES
    ('66666666-6666-6666-6666-666666666601', '11111111-1111-1111-1111-111111111111', 'ống bình minh phi 21', 'ong binh minh phi 21', '55555555-5555-5555-5555-555555555501', 'PIPE', 'PVC', 'Bình Minh', 21, 'PN8', null, null, 0.95, 'Direct SKU alias', true, now(), now()),
    ('66666666-6666-6666-6666-666666666602', '11111111-1111-1111-1111-111111111111', 'ống nóng 25', 'ong nong 25', '55555555-5555-5555-5555-555555555504', 'PIPE', 'PPR', null, 25, 'PN20', null, null, 0.90, 'PPR hot pipe slang', true, now(), now()),
    ('66666666-6666-6666-6666-666666666603', '11111111-1111-1111-1111-111111111111', 'co 27', 'co 27', null, 'FITTING', null, null, 27, null, 'CO', null, 0.65, 'Ambiguous co 27', true, now(), now()),
    ('66666666-6666-6666-6666-666666666604', '11111111-1111-1111-1111-111111111111', 'cút 27', 'cut 27', null, 'FITTING', null, null, 27, null, 'CO', null, 0.65, 'Ambiguous cút 27', true, now(), now()),
    ('66666666-6666-6666-6666-666666666605', '11111111-1111-1111-1111-111111111111', 'co ren trong 27', 'co ren trong 27', '55555555-5555-5555-5555-555555555510', 'FITTING', 'PVC', 'Bình Minh', 27, null, 'CO', 'REN_TRONG', 0.95, 'Threaded fitting', true, now(), now()),
    ('66666666-6666-6666-6666-666666666606', '11111111-1111-1111-1111-111111111111', 'tê giảm 34 27', 'te giam 34 27', '55555555-5555-5555-5555-555555555513', 'FITTING', 'PVC', 'Bình Minh', null, null, 'TE_GIAM', null, 0.95, 'Reducing tee', true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO price_lists (id, organization_id, price_list_code, name, customer_id, price_tier, valid_from, valid_to, priority, active, created_at, updated_at)
VALUES
    ('77777777-7777-7777-7777-777777777701', '11111111-1111-1111-1111-111111111111', 'STANDARD-2026', 'Bảng giá chuẩn 2026', null, 'STANDARD', '2026-01-01', null, 10, true, now(), now()),
    ('77777777-7777-7777-7777-777777777702', '11111111-1111-1111-1111-111111111111', 'DEALER-2026', 'Bảng giá đại lý 2026', null, 'DEALER', '2026-01-01', null, 5, true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO sku_prices (id, organization_id, price_list_id, sku_id, min_quantity, unit_price, approval_floor_price, active, created_at, updated_at)
SELECT gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '77777777-7777-7777-7777-777777777701', id, 1, 10000 + (row_number() over (order by sku_code) * 1500), 8000 + (row_number() over (order by sku_code) * 1000), true, now(), now()
FROM product_skus
WHERE organization_id = '11111111-1111-1111-1111-111111111111'
ON CONFLICT DO NOTHING;

INSERT INTO inventory_balances (id, organization_id, warehouse_id, sku_id, on_hand_quantity, reserved_quantity, available_quantity, updated_at)
SELECT gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444401', id,
       CASE WHEN sku_code = 'PPR-D32-PN20-4M' THEN 2 ELSE 200 END,
       0,
       CASE WHEN sku_code = 'PPR-D32-PN20-4M' THEN 2 ELSE 200 END,
       now()
FROM product_skus
WHERE organization_id = '11111111-1111-1111-1111-111111111111'
ON CONFLICT DO NOTHING;
