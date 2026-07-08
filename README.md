# Hệ thống Quản lý Chi tiêu Chung (Expense Sharing System)

> Ứng dụng web giúp quản lý chi tiêu, công nợ và thanh toán giữa các thành viên trong cùng một nhóm nhà trọ, gia đình, nhóm bạn hoặc nhóm du lịch.

**Trạng thái hiện tại:** Phiên bản 1–2 hoàn thành. Phiên bản 3 đang triển khai (profile, đổi MK, xuất báo cáo đã xong).

---

## Giới thiệu

Trong quá trình sinh hoạt chung, các thành viên thường xuyên phát sinh nhiều khoản chi như tiền điện, nước, Internet, thuê nhà, mua đồ dùng chung, ăn uống...

Việc ghi chép thủ công hoặc nhớ ai đã trả, ai còn nợ rất dễ dẫn đến nhầm lẫn. Dự án này giúp:

- Quản lý các khoản chi minh bạch
- Theo dõi công nợ giữa các thành viên
- Tự động tính số tiền mỗi người phải trả
- Phân quyền theo vai trò trong nhóm
- Mở rộng thành nền tảng quản lý tài chính nhóm

---

## Tiến độ triển khai

| Hạng mục | Backend | Frontend | Ghi chú |
| -------- | ------- | -------- | ------- |
| Đăng ký / Đăng nhập (username) | ✅ | ✅ | JWT + Refresh Token |
| Quản lý nhóm (CRUD) | ✅ | ✅ | Soft delete |
| Quản lý thành viên & phân quyền | ✅ | ✅ | OWNER / ADMIN / MEMBER / VIEWER |
| Mời thành viên (username/email) | ✅ | ✅ | Nhập chính xác identifier |
| CRUD khoản chi | ✅ | ✅ | |
| Chia chi phí (EQUAL / FIXED / %) | ✅ | ✅ | |
| Tính công nợ & ghi nhận thanh toán | ✅ | ✅ | |
| Lịch sử thanh toán | ✅ | ✅ | |
| Dashboard thống kê | ✅ | ✅ | |
| Nhật ký hoạt động | ✅ | ✅ | Theo nhóm + toàn bộ của user |
| Hiển thị username trên UI | ✅ | ✅ | Thay UUID rút gọn |
| Soft delete | ✅ | — | users, houses, members, expenses |
| Unit test (Backend) | ✅ | — | ~29 test classes |
| Swagger / OpenAPI | ✅ | — | `/swagger-ui.html` |
| `GET /users/me` (profile) | ✅ | ✅ | Trang `/profile` |
| Đổi mật khẩu | ✅ | ✅ | `PATCH /users/me/password` |
| Xuất Excel / PDF báo cáo nhóm | ✅ | ✅ | Tab Dashboard |
| Upload hóa đơn | ⬜ | ⬜ | Phiên bản 3 |
| Thông báo | ✅ | ✅ | In-app, badge chưa đọc |
| Frontend tests | ⬜ | ⬜ | |
| Kubernetes / CI/CD | ⬜ | ⬜ | Chỉ có Docker Compose |

---

## Chức năng chi tiết

### 1. Quản lý tài khoản

| Chức năng | Trạng thái |
| --------- | ---------- |
| Đăng ký (username, email, password, fullName) | ✅ |
| Đăng nhập bằng username | ✅ |
| JWT Authentication | ✅ |
| Refresh Token | ✅ |
| Đăng xuất | ✅ |
| Username không đổi sau đăng ký | ✅ |
| Xem thông tin tài khoản (`GET /users/me`) | ✅ |
| Đổi mật khẩu | ✅ |

### 2. Quản lý nhóm

Một người dùng có thể tham gia nhiều nhóm:

```text
🏠 Phòng trọ Duy Tân
🏠 Gia đình
🏠 Du lịch Đà Nẵng
```

| Chức năng | Trạng thái |
| --------- | ---------- |
| Tạo nhóm | ✅ |
| Chỉnh sửa thông tin nhóm | ✅ |
| Mời thành viên (username hoặc email) | ✅ |
| Đổi vai trò / Xóa thành viên | ✅ |
| Rời nhóm | ✅ |
| Xóa nhóm (soft delete, cascade) | ✅ |

### 3. Phân quyền thành viên

| Vai trò | Quyền |
| ------- | ----- |
| OWNER | Toàn quyền |
| ADMIN | Quản lý thành viên và khoản chi |
| MEMBER | Thêm và chỉnh sửa khoản chi của mình |
| VIEWER | Chỉ được xem |

### 4. Quản lý khoản chi

Mỗi khoản chi gồm: tiêu đề, mô tả, số tiền, người thanh toán, danh sách người tham gia, ngày phát sinh, ghi chú.

Hóa đơn đính kèm: **chưa triển khai** (Phiên bản 3).

### 5. Chia chi phí

| Loại chia | Trạng thái |
| --------- | ---------- |
| Chia đều (EQUAL) | ✅ |
| Chia theo số tiền cố định (FIXED) | ✅ |
| Chia theo phần trăm (PERCENTAGE) | ✅ |
| Chia theo trọng số / số ngày ở | ⬜ Tương lai |

### 6. Công nợ & thanh toán

- Tự động tính ai đang nợ ai
- Ghi nhận thanh toán giữa các thành viên
- Lịch sử thanh toán

### 7. Nhật ký hoạt động

Ghi lại: thêm/sửa/xóa khoản chi, thanh toán công nợ, thêm/xóa thành viên, thay đổi nhóm...

### 8. Dashboard

- Tổng chi tiêu
- Tổng đã thanh toán
- Chi tiêu theo tháng
- Chi tiêu theo thành viên (hiển thị username)
- Công nợ hiện tại (trong response API)

### 9. Thông báo (Tương lai)

- Có khoản chi mới
- Có người thanh toán
- Nhắc thanh toán
- Thành viên mới tham gia

---

## Kiến trúc

Áp dụng **Clean Architecture**, **DDD** và **RESTful API**. Mỗi feature tách theo layer:

```text
Presentation  →  Application  →  Domain  →  Infrastructure
```

```text
React (Vite) ──REST──► Spring Boot ──► PostgreSQL
                            │
                         Flyway
```

Có thể bổ sung sau: Redis, Message Queue, Object Storage, Kubernetes, Monitoring, CI/CD.

---

## Cấu trúc dự án

```text
Expense-Sharing-System/
├── backend/
│   └── src/main/java/com/expensesharing/
│       ├── common/              # exception, response, port, support
│       ├── config/              # Security, ...
│       └── feature/
│           ├── auth/            # application / domain / infrastructure / presentation
│           ├── house/
│           ├── expense/
│           ├── settlement/
│           ├── dashboard/
│           └── activity/
           └── report/
│   └── src/main/resources/db/migration/   # Flyway V1–V7
│
├── frontend/
│   └── src/
│       ├── app/                 # router, providers
│       ├── features/            # auth, house, expense, settlement, dashboard, activity
│       └── shared/              # api, components, hooks, utils
│
├── deployment/docker/         # docker-compose.yml (PostgreSQL)
├── docs/                        # local-development.md
└── README.md
```

---

## Công nghệ

### Backend

- Java 21
- Spring Boot 3.3
- Spring Security + JWT + BCrypt
- Spring Data JPA
- PostgreSQL + Flyway
- SpringDoc OpenAPI (Swagger)
- Apache POI (Excel export)
- OpenPDF (PDF export)
- Lombok
- JUnit 5

### Frontend

- React 19 + TypeScript
- Vite 8
- Tailwind CSS v4
- TanStack Query (React Query)
- React Hook Form + Zod
- Axios + React Router

### Triển khai

- Docker + Docker Compose (PostgreSQL) — **đã có**
- Kubernetes, Nginx, CI/CD — **chưa có**

---

## API (33 endpoints)

Base URL: `http://localhost:8080/api/v1`

| Nhóm | Endpoints |
| ---- | --------- |
| **Auth** | `POST /auth/register`, `/login`, `/refresh`, `/logout` |
| **User** | `GET /users/me`, `PATCH /users/me/password` |
| **House** | `POST/GET/PUT/DELETE /houses`, `GET/POST /houses/{id}/members`, `PUT/DELETE /houses/{id}/members/{userId}`, `DELETE /houses/{id}/members/me` |
| **Expense** | `POST/GET/PUT/DELETE /houses/{id}/expenses` |
| **Settlement** | `GET /houses/{id}/debts`, `POST/GET /houses/{id}/settlements` |
| **Dashboard** | `GET /houses/{id}/dashboard` |
| **Report** | `GET /houses/{id}/reports/excel`, `GET /houses/{id}/reports/pdf` |
| **Notification** | `GET /notifications`, `GET /notifications/unread-count`, `PATCH /notifications/{id}/read`, `PATCH /notifications/read-all` |
| **Activity** | `GET /houses/{id}/activities`, `GET /users/me/activities` |

Tài liệu tương tác: **http://localhost:8080/swagger-ui.html**

### Ví dụ request

**Đăng ký:**

```json
POST /api/v1/auth/register
{
  "username": "long_dev",
  "email": "long@example.com",
  "password": "password123",
  "fullName": "Nguyen Van Long"
}
```

**Đăng nhập:**

```json
POST /api/v1/auth/login
{
  "username": "long_dev",
  "password": "password123"
}
```

**Mời thành viên:**

```json
POST /api/v1/houses/{houseId}/members
{
  "identifier": "nam_dev",
  "role": "MEMBER"
}
```

`identifier` có thể là username hoặc email (tự trim, khớp chính xác).

---

## Mô hình dữ liệu

| Bảng | Mô tả |
| ---- | ----- |
| `users` | Tài khoản (username unique, soft delete) |
| `refresh_tokens` | Refresh token |
| `houses` | Nhóm chi tiêu |
| `house_members` | Thành viên & vai trò |
| `expenses` | Khoản chi |
| `expense_participants` | Phần chia của từng thành viên |
| `settlements` | Ghi nhận thanh toán |
| `activity_logs` | Nhật ký hoạt động |

Migration: `V1` auth → `V2` houses → `V3` expenses → `V4` settlements → `V5` activity → `V6` soft delete → `V7` username → `V8` notifications

---

## Chạy local

Xem hướng dẫn chi tiết: [docs/local-development.md](docs/local-development.md)

**Tóm tắt:**

```bash
# 1. PostgreSQL
docker compose -f deployment/docker/docker-compose.yml up -d

# 2. Backend (port 8080)
cd backend && mvn spring-boot:run

# 3. Frontend (port 5173)
cd frontend && npm install && npm run dev
```

**Yêu cầu:** Java 21, Maven 3.9+, Node.js 20+, Docker Desktop.

---

## Lộ trình phát triển

### Phiên bản 1 — ✅ Hoàn thành

- Đăng ký / Đăng nhập
- Quản lý nhóm
- CRUD khoản chi
- Chia đều
- Tính công nợ

### Phiên bản 2 — ✅ Hoàn thành

- Chia theo phần trăm & số tiền cố định
- Dashboard
- Nhật ký hoạt động
- Frontend đầy đủ (React + các tab nhóm)
- Username, soft delete, hiển thị username

### Phiên bản 3 — 🔄 Đang triển khai

- ✅ `GET /users/me` + trang profile
- ✅ Đổi mật khẩu
- ✅ Xuất Excel / PDF báo cáo nhóm
- ✅ Thông báo in-app
- ⬜ Upload hóa đơn

### Phiên bản 4 — ⬜ Kế hoạch

- Ứng dụng di động
- API công khai
- WebSocket
- Đa ngôn ngữ

---

## Định hướng

Dự án hướng tới nền tảng quản lý tài chính nhóm cho nhà trọ, gia đình, nhóm bạn, câu lạc bộ, doanh nghiệp nhỏ, nhóm du lịch.

Phục vụ học tập, nghiên cứu và xây dựng Portfolio **Java Backend Developer**, đồng thời hướng tới triển khai thực tế trong môi trường doanh nghiệp.

---

## Giấy phép

MIT License
