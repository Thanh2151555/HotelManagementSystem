# Danh sách Công việc: Dự án Quản lý Nhà nghỉ (Đồ án Tốt nghiệp)

Danh sách này giúp theo dõi tiến độ triển khai dự án dựa trên bản thiết kế MVP đã chốt. 
Ký hiệu:
- `[ ]` Chưa bắt đầu
- `[/]` Đang tiến hành
- `[x]` Đã hoàn thành

## Giai đoạn 1: Thiết kế Cơ sở dữ liệu (Database Design)
- `[x]` Phân tích thiết kế Schema & ERD cho 10 Modules (Users, Roles, Rooms, RoomTypes, Reservations, Guests, Services, Rates, Invoices...)
- `[x]` Viết script SQL (DDL) tạo bảng trên SQL Server.
- `[ ]` Viết Stored Procedure xử lý các nghiệp vụ phức tạp (như tính tổng tiền dựa trên giờ/ngày/đêm theo bảng giá).
- `[ ]` Tạo script Seed Data (Dữ liệu mẫu ban đầu cho Admin, một vài loại phòng, phòng thực tế).

## Giai đoạn 2: Khởi tạo Project (Project Setup)
- `[x]` Khởi tạo project Spring Boot (Java 21, Maven).
- `[x]` Tích hợp các dependencies: Spring Web, Spring Data JPA, Spring Security, SQL Server Driver, Validation.
- `[x]` Thiết lập cấu trúc thư mục chuẩn (Entity, Repository, Service, Controller, DTO, Security, Exception, Config).
- `[ ]` Cấu hình kết nối SQL Server trong `application.yml`.

## Giai đoạn 3: Phát triển Backend (APIs)
- `[ ]` **Module 1:** API Quản lý Tài khoản & Phân quyền (Auth, Roles: Admin, Chủ, Lễ tân, Dọn dẹp, Khách).
- `[ ]` **Module 2:** API Quản lý Danh mục Phòng (Phòng đơn, đôi, đặc biệt).
- `[ ]` **Module 3:** API Quản lý Khách hàng (có phân trang 20 bản ghi/trang).
- `[ ]` **Module 4:** API Quản lý Bảng Giá (Dynamic Pricing theo giờ, đêm, ngày, mùa).
- `[ ]` **Module 5:** API Quản lý Đặt phòng (2 luồng: Đặt đích danh và Đặt loại phòng).
- `[ ]` **Module 6:** API Nhận phòng (Check-in) & Trả phòng (Check-out).
- `[ ]` **Module 7:** API Dịch vụ & Yêu cầu thêm (Recommend nước, khăn, v.v...).
- `[ ]` **Module 8:** API Quản lý Trạng thái dọn dẹp.
- `[ ]` **Module 9:** API Báo cáo doanh thu & Phân tích bảo trì.

## Giai đoạn 4: Phát triển Frontend (Phase 1 & 2 - HTML/Bootstrap/JS)
- `[ ]` UI/UX Layout tổng thể (Sidebar, Header, Footer).
- `[ ]` Xây dựng Màn hình Sơ đồ Phòng Trực quan (có phân trang 5 phòng/hàng).
- `[ ]` Xây dựng Form Đặt phòng & Check-in (có logic bắt validate).
- `[ ]` Xây dựng Màn hình Danh sách Khách hàng & Báo cáo.
- `[ ]` Call API từ Backend lên giao diện.

## Giai đoạn 5: Tích hợp nâng cao & Tối ưu (Phase 3)
- `[ ]` Chuyển đổi và nhúng dữ liệu qua Thymeleaf (nếu cần render SSR).
- `[ ]` Nâng cấp bảo mật từ Session Login lên JWT (Access/Refresh Token).
- `[ ]` Fix bugs và tối ưu hiệu năng các câu truy vấn.

## Giai đoạn 6: Kiểm thử & Bàn giao
- `[ ]` Test toàn bộ luồng bằng Postman.
- `[ ]` Hoàn thiện tài liệu tổng kết (Walkthrough).
