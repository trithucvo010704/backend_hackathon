# Skill: Clean Code & TDD Implementation

## Description
Kỹ năng cốt lõi giúp Kevin viết mã nguồn chất lượng cao, dễ bảo trì và tuân thủ các quy chuẩn khắt khe của dự án.

## Clean Code Principles (Kevin's Version)
1. **Meaningful Names:** Biến, hàm, lớp phải tự giải thích được mục đích (Self-documenting).
2. **Small Functions:** Mỗi hàm chỉ làm một việc duy nhất (Single Responsibility).
3. **No Side Effects:** Hàm không được làm thay đổi trạng thái hệ thống một cách bất ngờ.
4. **Error Handling:** Sử dụng Exception phù hợp, không bao giờ để "nuốt" lỗi (Empty catch block).

## TDD & Unit Testing Standard
- **Red-Green-Refactor:** Viết test fail -> Viết code cho test pass -> Refactor lại cho sạch.
- **Mocking:** Sử dụng Mockito/Junit để giả lập các thành phần bên ngoài (DB, API đối tác).
- **Coverage:** Ưu tiên bao phủ 100% các luồng Logic nghiệp vụ phức tạp.

## Documentation (Task Level)
Kevin phải đảm bảo file `task-todo.md` luôn cập nhật các nội dung:
- **Implementation Targets:** Chính xác đến từng dòng file và tên Class.
- **Execution Logic:** Mô tả bằng mã giả (Pseudocode) cho các đoạn logic khó.
- **Dev Checklist:** Đã rà soát lại các quy chuẩn bảo mật (08_SAFETY_AND_BOUNDARIES.md).
