# HƯỚNG DẪN: ARCHITECTURE AUDIT (KIỂM TRA KIẾN TRÚC)

## 1. Mô tả
Tài liệu này dùng để đánh giá mức độ tuân thủ kiến trúc của mã nguồn trong một repository. Nó giúp phát hiện các vi phạm về phân tầng (layering), nguyên tắc Clean Architecture, và các quy chuẩn kỹ thuật đã đề ra, từ đó đảm bảo tính bền vững và khả năng mở rộng của hệ thống.

## 2. Cách viết
1. **Thông tin định danh:** Ghi rõ người thực hiện (Architect) và thời điểm audit.
2. **Đánh giá sự tuân thủ:** 
    - Kiểm tra các hạng mục quan trọng như: Cấu trúc thư mục, Phân chia layer, Dependency Rule (layer ngoài không phụ thuộc layer trong), Coding Standard.
    - Đánh giá trạng thái [OK/Fail/Warning].
3. **Chi tiết vi phạm & Đề xuất:** 
    - Ghi lại các đoạn code hoặc cấu trúc vi phạm kiến trúc.
    - Đưa ra giải pháp refactor hoặc hướng xử lý cụ thể.

## 3. Nguồn thông tin
- Tài liệu kiến trúc dự án ([01_PROJECT_ARCHITECTURE.md](../repository-level/01_PROJECT_ARCHITECTURE.md)).
- Quy chuẩn coding ([02_CODING_STANDARDS.md](../repository-level/02_CODING_STANDARDS.md)).
- Mã nguồn thực tế trong repository.

## 4. Cách thu thập
- Sử dụng các công cụ phân tích tĩnh (Static Analysis tools).
- Đọc code thủ công (Code Review) tập trung vào cấu trúc file và sự phụ thuộc giữa các package.
- Chạy các script kiểm tra cấu trúc thư mục tự động.
- Kiểm tra file cấu hình (ví dụ: `tsconfig.json`, `package.json`).

## 5. Format gợi ý / Template áp dụng

```markdown
# [AUDIT] ARCHITECTURE AUDIT: [Tên Repository]

**Người Audit:** [Tên Architect/Agent]
**Ngày thực hiện:** YYYY-MM-DD

## 1. TỔNG QUAN TUÂN THỦ (COMPLIANCE SUMMARY)
- **Cấu trúc Layer:** [OK/FAIL] - [Ghi chú nhanh]
- **Quy tắc Phụ thuộc (Dependency Rules):** [OK/FAIL]
- **Coding Standards:** [OK/FAIL]
- **Cấu trúc thư mục:** [OK/FAIL]

## 2. CHI TIẾT VI PHẠM (VIOLATIONS)
| Vấn đề | Vị trí (File/Folder) | Mô tả vi phạm | Đề xuất sửa đổi |
| :--- | :--- | :--- | :--- |
| Sai Layer | `/src/domain/service.ts` | Domain layer gọi trực tiếp DB infrastructure. | Chuyển qua dùng Interface/Repository pattern. |

## 3. KẾT LUẬN & HÀNH ĐỘNG TIẾP THEO
[Tóm tắt mức độ nghiêm trọng và danh sách các task refactor cần thực hiện ngay]
```
