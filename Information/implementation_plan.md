# Thiết kế MVP: Hệ thống Quản lý Nhà nghỉ (Đồ án Tốt nghiệp)

Hệ thống được thiết kế dành cho nhà nghỉ nhỏ (15-20 phòng, 5-8 tầng), tối ưu hóa luồng nghiệp vụ thực tế tại Việt Nam dưới dạng một ứng dụng Web (Web Application).

## User Review Required
> [!IMPORTANT]
> Bản thiết kế đã được tinh chỉnh theo các yêu cầu chi tiết nhất của bạn (Java 21, SSMS, phòng đặc biệt, đổi giá theo mùa...). Vui lòng kiểm tra lại. Nếu bạn đã hoàn toàn hài lòng với thiết kế này, hãy nhấn **Proceed** để chốt phương án và chúng ta có thể chuyển sang bước tiếp theo.

---

## 1. Yêu Cầu Công Nghệ (Tech Stack)
Dựa trên định hướng phát triển đồ án, hệ thống sẽ sử dụng các công nghệ sau:

**Backend:**
- Ngôn ngữ & Framework: **Java 21**, Spring Boot
- ORM: Spring Data JPA
- Bảo mật: Spring Security (Authentication + Authorization)
- Quản lý Dependency: Maven

**Cơ sở dữ liệu (Database):**
- Microsoft SQL Server (Áp dụng Stored Procedure, Window Function để tính toán phức tạp)
- DB Tool: **SQL Server Management Studio (SSMS)**

**Frontend (Lộ trình phát triển):**
- **Phase 1:** HTML, CSS, Vanilla JS (Phục vụ CRUD cơ bản)
- **Phase 2:** Tích hợp Bootstrap (Làm UI đẹp, Responsive)
- **Phase 3:** Tích hợp Thymeleaf (Hoàn chỉnh theo mô hình MVC Java Web)

**Bảo mật (Authentication):**
- Phase 1: Session Login
- Phase 2: JWT
- Phase 3: Refresh Token

**Công cụ & Quản lý Source:**
- IDE: IntelliJ IDEA Community
- API Testing: Postman
- Source Control: Git, GitHub (chưa cần Git Flow giai đoạn đầu)

---

## 2. Cấu Trúc Phân Quyền (Roles)
Hệ thống cần phân tích rõ 5 vai trò (Roles) tham gia, giúp bảo mật và định hướng UX:
1. **Admin:** Quản trị viên hệ thống (cài đặt cấu hình lõi, quản lý tài khoản).
2. **Chủ nhà nghỉ:** Có toàn quyền xem báo cáo doanh thu, sửa giá phòng theo mùa, quản lý chi phí.
3. **Lễ tân:** Xử lý luồng Check-in/Check-out, tạo booking, thu tiền (không được xóa hóa đơn hay xem báo cáo tài chính tổng).
4. **Nhân viên dọn dẹp (Housekeeping):** Tài khoản giới hạn chỉ để xem danh sách phòng cần dọn và bấm xác nhận "Đã dọn xong" để thông báo real-time cho lễ tân.
5. **Khách hàng:** Có thể đăng nhập để đặt phòng online, xem lịch sử đặt phòng, gửi đánh giá.

---

## 3. Các Module Cốt Lõi (MVP Modules)

### 3.1. Quản lý Tài khoản & Phân quyền
- Xử lý xác thực (Authentication) và phân quyền (Authorization) dựa trên 5 Roles đã định nghĩa. 

### 3.2. Quản lý Danh mục Phòng (Room Management)
- Thiết lập sơ đồ 15-20 phòng từ tầng 1 đến tầng 8.
- **Phân loại phòng:** Phòng đơn (Single), Phòng đôi (Double), và **Phòng đặc biệt** (Special/VIP - ví dụ phòng có ban công).

### 3.3. Sơ đồ Phòng Trực quan (Room Dashboard)
- Hiển thị phòng bằng lưới màu sắc (Trống - xanh lá, Đang ở - đỏ, Đang dọn - vàng).
- **Yêu cầu UI đặc thù:** Tích hợp **phân trang (Pagination)**, ví dụ hiển thị 5 phòng trên 1 trang để giao diện gọn gàng, dễ nhìn.

### 3.4. Quản lý Đặt phòng (Reservation) - *Nghiệp vụ quan trọng*
Xử lý 2 nghiệp vụ đặt phòng chính xác theo thực tế:
- **Trường hợp 1 (Đặt phòng cụ thể - VD: P201):** Khóa chính xác phòng đó (không cho người khác đặt). Ghi nhận cọc tiền (`deposit_amount`). Trạng thái: `CONFIRMED`. Khi khách check-in, xác minh khách và không thu lại khoản tiền cọc.
- **Trường hợp 2 (Chỉ chọn loại phòng - VD: Double Room):** Hệ thống chỉ giữ quỹ phòng của loại Double, không khóa phòng cụ thể. Khi khách đến check-in, Lễ tân xem phòng Double nào trống/sạch thì mới tiến hành gán phòng đó cho khách (Dynamic Room Assignment).

### 3.5. Nhận phòng & Trả phòng (Check-in & Check-out)
- Thực hiện luồng giao nhận chìa khóa, cập nhật trạng thái phòng.
- Gộp tiền phòng, tiền dịch vụ, trừ tiền cọc, xuất hóa đơn cuối cùng cho khách hàng.

### 3.6. Quản lý Khách hàng (Guest Management)
- Lưu trữ thông tin định danh phục vụ khai báo lưu trú.
- **Yêu cầu UI:** Danh sách khách hàng phải có **phân trang (Pagination)**, hiển thị tối đa **20 bản ghi / trang**.

### 3.7. Quản lý Bảng Giá (Rate Management) - *Business Logic cốt lõi*
Thuật toán tính giá phòng cần đáp ứng linh hoạt các mốc giá của nhà nghỉ:
- **Giá Cố Định:**
  - **Theo giờ:** Giờ đầu là 50.000 VNĐ, các giờ tiếp theo là 10.000 VNĐ/giờ.
  - **Qua đêm:** Phòng thường qua đêm 130.000 VNĐ, phòng đôi qua đêm 180.000 VNĐ.
  - **Theo ngày:** Phòng đơn ngày 200.000 VNĐ, phòng đôi ngày 250.000 VNĐ.
- **Thay đổi giá theo mùa / sự kiện (Dynamic Pricing):** Mở rộng tính năng cho phép Chủ nhà nghỉ thay đổi, điều chỉnh giá linh hoạt vào các dịp Lễ, Tết, hoặc cuối tuần (Ví dụ: tăng thêm 20% vào dịp Lễ).

### 3.8. Quản lý Dịch vụ & Yêu cầu thêm (Services & Requests)
- Quản lý bán các sản phẩm tiêu dùng (nước uống, mì tôm).
- **Tính năng mở rộng:** Hệ thống gợi ý (recommend) gọi nước và cho phép ghi nhận các **yêu cầu thêm đồ** từ khách (khăn tắm, bao cao su, bàn chải, v.v.), cộng trực tiếp vào hóa đơn (nếu có tính phí) hoặc để buồng phòng mang lên.

### 3.9. Quản lý Trạng thái Dọn dẹp (Housekeeping Status)
- Luồng thông tin hai chiều giữa Lễ tân và Người dọn dẹp để đảm bảo phòng luôn sẵn sàng trước khi giao cho khách mới.

### 3.10. Báo cáo & Phân tích Bảo trì (Dashboard & Maintenance Analytics)
- Báo cáo doanh thu và công suất phòng cơ bản.
- **Tính năng mở rộng (Phân tích bảo trì):** Khi khách hàng gửi đánh giá phòng, hệ thống ghi nhận các sản phẩm/thiết bị bị hỏng. Website sẽ phân tích dữ liệu này và cảnh báo lên Dashboard, giúp nhà nghỉ chủ động mua đồ dự phòng thay thế trực tiếp.
