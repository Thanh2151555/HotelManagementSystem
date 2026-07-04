# Phân Tích Phân Khúc Phần Mềm Quản Lý Khách Sạn (PMS)

## Tổng Quan Về Các Giải Pháp PMS Phổ Biến
Dựa trên phân tích các hệ thống PMS hàng đầu thị trường hiện nay:
- **Opera PMS (Oracle):** Giải pháp toàn diện, đồ sộ, thường dùng cho các chuỗi khách sạn lớn, resort 4-5 sao. Mạnh mẽ về quản lý đa thuộc tính nhưng chi phí cao và cần thời gian đào tạo dài.
- **Cloudbeds:** Nền tảng cloud-based hiện đại, giao diện thân thiện, tích hợp sẵn Channel Manager. Phù hợp cho khách sạn vừa và nhỏ (SME), hostel, boutique hotel.
- **eZee (eZee Absolute):** Phổ biến tại thị trường Châu Á, giá cả phải chăng, nhiều tính năng tùy biến tốt cho các quy mô khách sạn khác nhau.
- **Hotelogix:** Hệ thống cloud-based linh hoạt, dễ dàng triển khai, phù hợp cho các khách sạn độc lập tầm trung muốn số hóa quy trình nhanh chóng.
- **Little Hotelier (by SiteMinder):** Thiết kế chuyên biệt cho B&B, nhà nghỉ, và khách sạn siêu nhỏ. Tập trung mạnh vào tính dễ sử dụng và quản lý kênh phân phối (OTA).

---

## 1. Các Quy Trình Làm Việc Chung (Common Hotel Workflows)
Các hệ thống PMS đều được thiết kế xoay quanh vòng đời của khách lưu trú (Guest Journey):

1. **Quy trình Đặt phòng (Pre-arrival / Reservation):**
   - Khách đặt qua OTA (Agoda, Booking) / Website / Điện thoại.
   - Hệ thống tự động ghi nhận, trừ quỹ phòng (inventory), và gửi email xác nhận.
2. **Quy trình Nhận phòng (Check-in / Front Desk):**
   - Lễ tân tìm kiếm hồ sơ booking, xác minh danh tính.
   - Thu tiền cọc (Deposit), phát hành chìa khóa/thẻ từ.
   - Cập nhật trạng thái phòng thành "Đã nhận" (Occupied).
3. **Quy trình Lưu trú (In-house / Stay):**
   - Ghi nhận các chi phí phát sinh (Minibar, Nhà hàng, Spa, Giặt ủi) vào tài khoản phòng (Room Folio).
   - Buồng phòng (Housekeeping) nhận lệnh làm phòng và cập nhật trạng thái dọn dẹp.
4. **Quy trình Trả phòng (Check-out / Departure):**
   - Lễ tân kiểm tra các khoản phí cuối cùng.
   - Gộp hóa đơn, thực hiện thanh toán, xuất hóa đơn (Invoice).
   - Đổi trạng thái phòng thành "Trống - Bẩn" (Vacant Dirty) để buồng phòng dọn dẹp.
5. **Quy trình Kiểm toán đêm (Night Audit):**
   - Thực hiện vào cuối ngày để chốt doanh thu, đẩy tiền phòng vào hóa đơn khách, và cập nhật ngày làm việc mới cho hệ thống.

---

## 2. Các Module Cốt Lõi (Common Modules)
Để vận hành trơn tru, một phần mềm PMS tiêu chuẩn thường được chia thành các phân hệ sau:
- **Front Office (Tiền sảnh):** Màn hình chính (Tape Chart/Dashboard) để thao tác check-in/out, kéo thả đổi phòng.
- **Reservation (Đặt phòng):** Quản lý danh sách booking, khách đoàn (Group booking), chính sách hủy.
- **Housekeeping (Buồng phòng):** Theo dõi trạng thái phòng (Sạch/Bẩn/Đang sửa), phân công công việc.
- **Channel Manager (Quản lý Kênh phân phối):** Đồng bộ phòng trống và giá cả lên các trang OTA theo thời gian thực.
- **Billing & Accounting (Kế toán & Thu ngân):** Quản lý công nợ, hóa đơn, tích hợp máy quẹt thẻ, theo dõi thu chi.
- **CRM & Guest Profile (Quản lý Khách hàng):** Lưu trữ thông tin khách, sở thích, lịch sử lưu trú.
- **Reporting & Analytics (Báo cáo):** Cung cấp các chỉ số quan trọng như công suất phòng (Occupancy), RevPAR, ADR.

---

## 3. Các Tính Năng Bắt Buộc Phải Có (Must-have Features)
Bất kể quy mô nào, khách sạn cũng cần các tính năng sống còn sau:
- **Lịch biểu trực quan (Interactive Calendar/Tape Chart):** Cho phép lễ tân nhìn thấy tổng quan tình trạng phòng trong tháng/tuần và thao tác trực tiếp trên đó.
- **Đồng bộ hóa OTA (2-way Channel Integration):** Ngăn chặn tình trạng "overbooking" (bán lố phòng).
- **Quản lý giá đa dạng (Rate Plan Management):** Cài đặt giá linh hoạt theo mùa, cuối tuần, hoặc theo hạng phòng.
- **Quản lý hóa đơn đa dạng (Split/Merge Folios):** Khả năng tách hóa đơn (VD: Công ty trả tiền phòng, khách tự trả tiền ăn) hoặc gộp hóa đơn cho khách đoàn.
- **Phân quyền người dùng (Role-based Access Control):** Đảm bảo bảo mật thông tin (Lễ tân không được xem báo cáo tài chính tổng, buồng phòng chỉ thấy trạng thái phòng).
- **Hỗ trợ đa tiền tệ và đa phương thức thanh toán.**

---

## 4. Các Tính Năng Hữu Ích/Nâng Cao (Nice-to-have Features)
Các tính năng giúp tạo lợi thế cạnh tranh và nâng cao trải nghiệm khách hàng:
- **Mobile PMS / Cloud Access:** Quản lý có thể theo dõi khách sạn từ xa bằng điện thoại.
- **Tích hợp POS (Point of Sale):** Quản lý nhà hàng, quán cafe, spa trong khách sạn và đồng bộ thẳng hóa đơn lên phòng khách.
- **Self Check-in / Kiosk:** Khách tự làm thủ tục tại máy hoặc trên điện thoại (Contactless check-in).
- **Email/SMS Automation:** Tự động gửi email chúc mừng sinh nhật, thông tin hướng dẫn trước khi đến, hoặc xin đánh giá sau khi đi.
- **Tích hợp khóa từ thông minh (Door Lock Integration):** Tạo thẻ từ trực tiếp từ phần mềm PMS mà không cần dùng hệ thống khóa riêng biệt.
- **Dynamic Pricing (Định giá động):** Gợi ý điều chỉnh giá tự động dựa trên công suất phòng hiện tại.

---

## 5. Các Tính Năng AI Trong Tương Lai Của Ngành Khách Sạn (Future AI Features)
Thị trường PMS đang dịch chuyển mạnh mẽ sang việc ứng dụng Trí tuệ nhân tạo (AI):
- **Hyper-Dynamic Pricing (Định giá động siêu cấp):** AI phân tích dữ liệu lịch sử, thời tiết địa phương, sự kiện sắp diễn ra, và giá của đối thủ cạnh tranh để liên tục tự động cập nhật giá phòng tối ưu nhất theo thời gian thực.
- **AI Concierge & Chatbot:** Trợ lý ảo xử lý các yêu cầu đặt phòng, trả lời câu hỏi thường gặp (hồ bơi mở mấy giờ?), đặt dịch vụ phòng thông qua WhatsApp/Zalo/Website mà không cần sự can thiệp của con người.
- **Cá nhân hóa trải nghiệm (Hyper-Personalization):** AI phân tích dữ liệu từ CRM để đoán trước nhu cầu. VD: Khách thường xuyên gọi cà phê đen vào buổi sáng các lần lưu trú trước, hệ thống tự động gợi ý lễ tân/nhà hàng chuẩn bị.
- **Tự động hóa vận hành bằng IoT + AI:** Tự động điều chỉnh điều hòa, ánh sáng dựa trên việc khách có trong phòng hay không, dự đoán khi nào thiết bị (TV, máy lạnh) cần bảo trì trước khi hỏng.
- **Phân tích Cảm xúc (Sentiment Analysis):** AI tự động "quét" tất cả các đánh giá (reviews) trên mạng và phân loại cảm xúc, cảnh báo ngay cho quản lý nếu có nguy cơ khủng hoảng truyền thông.
- **Smart Staff Scheduling (Xếp lịch nhân sự thông minh):** AI dự đoán lượng khách check-in/out hoặc các yêu cầu dịch vụ để tối ưu hóa việc sắp xếp ca làm việc cho bộ phận Tiền sảnh và Buồng phòng.
