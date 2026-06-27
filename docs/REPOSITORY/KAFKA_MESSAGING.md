# HƯỚNG DẪN MESSAGING & EVENT-DRIVEN (KAFKA_MESSAGING.md)

Tài liệu này quy chuẩn việc trao đổi dữ liệu không đồng bộ thông qua Message Broker.

---

## 1. CẤU TRÚC MESSAGE & TOPIC (TOPIC STRUCTURE)

1. **Mô tả:** Cách đặt tên Topic và định dạng dữ liệu (Payload) của message.
2. **Cách viết:** Quy định naming convention cho topic và schema versioning (Avro/JSON).
3. **Nguồn thông tin:** Kafka/RabbitMQ config và từ điển sự kiện của dự án.
4. **Cách thu thập:** Truy vấn danh sách Topic hiện có trên Broker.
5. **Format gợi ý / Template áp dụng:**
   - **Topic Name**: `[Domain].[Entity].[Action]` (VD: `Order.Invoice.Created`).
   - **Payload**: Bắt buộc có `event_id`, `timestamp`, `version`.

---

## 2. QUY TẮC PRODUCER & CONSUMER (PRODUCER/CONSUMER RULES)

1. **Mô tả:** Các tiêu chuẩn về độ tin cậy khi gửi và nhận message.
2. **Cách viết:** Trả lời về tính Idempotency, Acknowledgment và Dead Letter Queue (DLQ).
3. **Nguồn thông tin:** Messaging pattern của dự án.
4. **Cách thu thập:** Đọc code phần `KafkaListener` hoặc `MessageProducer`.
5. **Format gợi ý / Template áp dụng:**
   - **Acknowledge**: `all` (cho Producer).
   - **Error Handling**: Bắt buộc có cơ chế xử lý tại DLQ cho các message bị fail nhiều lần.
