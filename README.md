## **Hệ thống Quản lý Chi tiêu Chung (Expense Sharing System)** 

Ứng dụng web giúp quản lý chi tiêu, công nợ và thanh toán giữa các thành viên trong cùng một nhóm như nhà trọ, gia đình, nhóm bạn hoặc nhóm du lịch. 

## **Giới thiệu** 

Trong quá trình sinh hoạt chung, các thành viên thường xuyên phát sinh nhiều khoản chi như: 

- Tiền điện 

- Tiền nước 

- Tiền Internet 

- Tiền thuê nhà 

- Mua đồ dùng chung 

- Ăn uống 

- Các khoản phát sinh khác 

Việc ghi chép thủ công hoặc nhớ ai đã trả, ai còn nợ rất dễ dẫn đến nhầm lẫn, thất lạc hoặc tranh cãi. 

Dự án này được xây dựng nhằm giúp: 

- Quản lý các khoản chi một cách minh bạch. 

- Theo dõi công nợ giữa các thành viên. 

- Tự động tính toán số tiền mỗi người phải trả. 

- Hỗ trợ phân quyền người dùng. 

- Dễ dàng mở rộng thành một nền tảng quản lý tài chính nhóm trong tương lai. 

- 

## © **Mục tiêu** 

- Xây dựng hệ thống theo kiến trúc dễ mở rộng. 

- Áp dụng các best practice của Spring Boot. 

- Phù hợp triển khai thực tế bằng Docker và Kubernetes. 

- Là dự án phục vụ học tập và xây dựng Portfolio Backend Java. 

## **Chức năng** 

## **1. Quản lý tài khoản** 

- Đăng ký 

- Đăng nhập 

- Đổi mật khẩu 

• JWT Authentication • Refresh Token 

## **2. Quản lý nhóm** 

Một người dùng có thể tham gia nhiều nhóm khác nhau. 

Ví dụ: 

```
 Phòng trọ Duy Tân
 Gia đình
```

```
 Du lịch Đà Nẵng
```

```
 Team Company
```

Chức năng: 

- Tạo nhóm • Chỉnh sửa thông tin nhóm • Mời thành viên • Rời nhóm • Xóa nhóm 

## **3. Quản lý thành viên** 

Các quyền mặc định: 

|Vai trò|Quyền|
|---|---|
|OWNER|Toàn quyền|
|ADMIN|Quản lý thành viên và khoản chi|
|MEMBER|Thêm và chỉnh sửa khoản chi của mình|
|VIEWER|Chỉ được xem|



Trong tương lai có thể mở rộng thành hệ thống RBAC. 

## **4. Quản lý khoản chi** 

Mỗi khoản chi gồm: 

- Tiêu đề 

- Mô tả • Số tiền • Người thanh toán • Danh sách người tham gia 

- Ngày phát sinh 

- Ghi chú 

- Hóa đơn (phiên bản sau) 

Ví dụ: 

- Tiền điện tháng 7 • Tiền Internet 

- Mua gạo • Ăn lẩu 

- Mua nước uống 

## **5. Chia chi phí** 

Hệ thống hỗ trợ nhiều cách chia tiền. 

## **Chia đều** 

Ví dụ: 

```
900.000
Long
Nam
Huy
=> Mỗi người 300.000
```

## **Chia theo số tiền cố định** 

```
Long : 500.000
Nam : 300.000
Huy : 100.000
```

## **Chia theo phần trăm** 

```
Long : 50%
Nam : 30%
```

```
Huy : 20%
```

Có thể mở rộng: 

- Chia theo trọng số 

- Chia theo số ngày ở 

- Chia theo số lần sử dụng 

## **6. Công nợ** 

Hệ thống tự động tính: 

- Ai đang nợ ai 

- Tổng số tiền còn nợ 

- Lịch sử thanh toán 

Ví dụ: 

```
Long nợ Nam 150.000
Nam nợ Huy 90.000
```

## **7. Nhật ký hoạt động** 

Lưu lại toàn bộ lịch sử thay đổi. 

Ví dụ: 

- Thêm khoản chi 

- Chỉnh sửa khoản chi 

- Xóa khoản chi 

- Thanh toán công nợ 

- Thêm thành viên 

- Xóa thành viên 

## **8. Thống kê** 

Dashboard hiển thị: 

- Tổng chi tiêu 

- Chi tiêu theo tháng 

- Chi tiêu theo thành viên 

- Công nợ hiện tại 

- Tổng số tiền đã thanh toán 

## **9. Thông báo (Tương lai)** 

- Có khoản chi mới 

- Có người thanh toán 

- Nhắc thanh toán 

- Thành viên mới tham gia 

## **Kiến trúc hệ thống** 

```
Frontend
    │
REST API
    │
Spring Boot
    │
Service Layer
    │
Repository
    │
PostgreSQL
```

Các thành phần có thể bổ sung sau: 

- Redis • RabbitMQ / Kafka • Object Storage • Kubernetes • Monitoring • CI/CD 

## **Cấu trúc dự án** 

```
expense-sharing/
```

- `├── backend/ │   ├── api/ │   ├── service/` 

- `│   ├── repository/ │   ├── domain/ │   ├── security/ │   ├── common/` 


```
│   └── config/
│
├── frontend/
│
├── deployment/
│   ├── docker/
│   ├── kubernetes/
│   └── nginx/
│
├── docs/
│
└── README.md
```

## **Công nghệ sử dụng** 

## **Backend** 

- Java 21 • Spring Boot • Spring Security • Spring Validation • Spring Data JPA hoặc MyBatis • PostgreSQL • Flyway • Lombok 

## **Frontend** 

- React • TypeScript • TailwindCSS • React Query 

## **Triển khai** 

- Docker • Docker Compose • Kubernetes 

- Nginx 

Có thể mở rộng: 

- Harbor 

- Rancher • GitHub Actions 

- Jenkins 


## **Mô hình dữ liệu chính** 

Các thực thể cốt lõi: 

- User 

- House 

- HouseMember 

- Expense 

- ExpenseParticipant 

- Settlement 

- AuditLog 

## **Lộ trình phát triển** 

## **Phiên bản 1** 

- Đăng ký / Đăng nhập • Quản lý nhóm 

- CRUD khoản chi 

- Chia đều • Tính công nợ 

## **Phiên bản 2** 

- Chia theo phần trăm 

- Chia theo số tiền 

- Dashboard 

- Nhật ký hoạt động 

## **Phiên bản 3** 

- Upload hóa đơn 

- Xuất PDF 

- Xuất Excel 

- Thông báo 

## **Phiên bản 4** 

- Ứng dụng di động 

- API công khai 

- WebSocket 

- Đa ngôn ngữ 


## © **Định hướng phát triển** 

Dự án được thiết kế theo hướng **Domain-Driven Design (DDD)** và **RESTful API** , đảm bảo khả năng mở rộng khi số lượng người dùng và chức năng tăng lên. 

Trong tương lai, hệ thống có thể phát triển thành nền tảng quản lý tài chính nhóm dành cho: 

- Nhà trọ 

- Gia đình 

- Nhóm bạn 

- Câu lạc bộ 

- Doanh nghiệp nhỏ 

- Nhóm du lịch 

## **Giấy phép** 

MIT License 

## **Tác giả** 

Dự án được phát triển nhằm mục đích học tập, nghiên cứu và xây dựng Portfolio cho vị trí **Java Backend Developer** , đồng thời hướng tới khả năng triển khai thực tế trong môi trường doanh nghiệp. 


