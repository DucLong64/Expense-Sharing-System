# Hệ thống Quản lý Chi tiêu Chung (Expense Sharing System)

> Ứng dụng web giúp quản lý chi tiêu, công nợ và thanh toán giữa các thành viên trong cùng một nhóm nhà trọ, gia đình, nhóm bạn hoặc nhóm du lịch.

**Trạng thái hiện tại:** Sẵn sàng deploy và dùng thử với nhóm nhỏ. Phiên bản 1–2 hoàn thành; phiên bản 3 gần xong (còn upload hóa đơn).

---

## Giới thiệu

Trong quá trình sinh hoạt chung, các thành viên thường xuyên phát sinh nhiều khoản chi như tiền điện, nước, Internet, thuê nhà, mua đồ dùng chung, ăn uống...

Việc ghi chép thủ công hoặc nhớ ai đã trả, ai còn nợ rất dễ dẫn đến nhầm lẫn. Dự án này giúp:

- Quản lý các khoản chi minh bạch
- Theo dõi công nợ giữa các thành viên
- Tự động tính số tiền mỗi người phải trả
- Ghi nhận thanh toán (người nợ) và xác nhận đã nhận (người được nợ)
- Phân quyền theo vai trò trong nhóm
- Thông báo in-app khi có hoạt động mới

---

## Triển khai nhanh (cho bạn bè dùng)

### Cách 1 — Free (khuyên dùng): Vercel + Render + Neon

```text
Frontend (Vercel) → Backend (Render) → Database (Neon)
```

1. Tạo DB trên [Neon](https://neon.tech) (JDBC URL có `?sslmode=require`)
2. Deploy backend lên [Render](https://render.com) (`Root Directory` = `backend`)
3. Deploy frontend lên [Vercel](https://vercel.com) (`Root Directory` = `frontend`, set `VITE_API_BASE_URL`)
4. Cập nhật `CORS_ALLOWED_ORIGINS` trên Render = URL Vercel

**Hướng dẫn từng bước:** [docs/deploy-free.md](docs/deploy-free.md)

### Cách 2 — VPS Docker (ổn định hơn, có phí)

```bash
cp .env.example .env
# Chỉnh POSTGRES_PASSWORD, JWT_SECRET, CORS_ALLOWED_ORIGINS trong .env

docker compose -f deployment/docker/docker-compose.prod.yml --env-file .env up -d --build
```

Truy cập: `http://<IP_SERVER>`

**Hướng dẫn chi tiết:** [docs/deployment.md](docs/deployment.md)

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
| Tính công nợ | ✅ | ✅ | |
| Ghi nhận thanh toán (người nợ) | ✅ | ✅ | Modal + validate số tiền |
| Xác nhận đã nhận (người được nợ) | ✅ | ✅ | `POST .../confirm-received` |
| Lịch sử thanh toán | ✅ | ✅ | |
| Dashboard & tab Tổng quan | ✅ | ✅ | Hero công nợ, quick actions |
| Nhật ký hoạt động | ✅ | ✅ | Theo nhóm + toàn bộ của user |
| Thông báo in-app | ✅ | ✅ | Bell dropdown + trang danh sách |
| Hiển thị username trên UI | ✅ | ✅ | Thay UUID rút gọn |
| Profile & đổi mật khẩu | ✅ | ✅ | `/profile` |
| Xuất Excel / PDF báo cáo | ✅ | ✅ | Tab Tổng quan |
| UI/UX (modal, menu, toast) | — | ✅ | Confirm dialog, dropdown, tabs |
| Soft delete | ✅ | — | users, houses, members, expenses |
| Unit test (Backend) | ✅ | — | ~30 test classes |
| Swagger / OpenAPI | ✅ | — | `/swagger-ui.html` |
| Docker production (VPS) | ✅ | ✅ | `docker-compose.prod.yml` |
| Free deploy (Vercel + Render + Neon) | ✅ | ✅ | [docs/deploy-free.md](docs/deploy-free.md) |
| Upload hóa đơn | ⬜ | ⬜ | Phiên bản 3 |
| Frontend tests | ⬜ | ⬜ | |
| Kubernetes / CI/CD | ⬜ | ⬜ | |

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

| Luồng | Ai thực hiện | Mô tả |
| ----- | ------------ | ----- |
| Ghi nhận thanh toán | Người **nợ** | Tự ghi khi đã trả tiền cho người khác |
| Xác nhận đã nhận | Người **được nợ** | Xác nhận khi đã nhận tiền từ người nợ |

- Tự động tính ai đang nợ ai (sau khoản chi + các lần thanh toán)
- Không cho ghi nhận vượt quá số nợ hiện tại
- Lịch sử thanh toán đầy đủ

### 7. Nhật ký hoạt động

Ghi lại: thêm/sửa/xóa khoản chi, thanh toán công nợ, thêm/xóa thành viên, thay đổi nhóm...

### 8. Dashboard

- Tổng chi tiêu, tổng đã thanh toán
- Chi tiêu theo tháng / theo thành viên
- Tab **Tổng quan**: số dư cá nhân, công nợ, khoản chi & hoạt động gần đây
- Xuất Excel / PDF

### 9. Thông báo

- Khoản chi mới, thanh toán, xác nhận đã nhận, thành viên mới...
- Badge chưa đọc trên header, panel dropdown kiểu Facebook
- Trang xem tất cả thông báo

---

## Kiến trúc

Áp dụng **Clean Architecture**, **DDD** và **RESTful API**. Mỗi feature tách theo layer:

```text
Presentation  →  Application  →  Domain  →  Infrastructure
```

```text
React (Vite) ──REST──► Spring Boot ──► PostgreSQL
       │                      │
    Nginx (prod)           Flyway
```

Production: Nginx phục vụ SPA và proxy `/api` tới Spring Boot (xem [docs/deployment.md](docs/deployment.md)).

---

## Cấu trúc dự án

```text
Expense-Sharing-System/
├── backend/
│   ├── Dockerfile
│   └── src/main/java/com/expensesharing/
│       ├── common/
│       ├── config/
│       └── feature/
│           ├── auth/
│           ├── house/
│           ├── expense/
│           ├── settlement/
│           ├── dashboard/
│           ├── activity/
│           ├── report/
│           └── notification/
│   └── src/main/resources/db/migration/   # Flyway V1–V8
│
├── frontend/
│   ├── Dockerfile
│   ├── vercel.json                 # SPA rewrite (Vercel)
│   ├── nginx/default.conf
│   └── src/
│       ├── app/
│       ├── features/
│       └── shared/
│
├── deployment/docker/
│   ├── docker-compose.yml          # PostgreSQL (dev)
│   ├── docker-compose.prod.yml     # Full stack (VPS)
│   └── nginx/default.conf
│
├── docs/
│   ├── local-development.md
│   ├── deploy-free.md              # Vercel + Render + Neon
│   └── deployment.md               # VPS Docker
│
├── render.yaml                     # Blueprint Render (tuỳ chọn)
├── .env.example
└── README.md
```

---

## Công nghệ

### Backend

- Java 21, Spring Boot 3.3
- Spring Security + JWT + BCrypt
- PostgreSQL + Flyway
- SpringDoc OpenAPI, Apache POI, OpenPDF

### Frontend

- React 19 + TypeScript + Vite 8
- Tailwind CSS v4, TanStack Query, React Hook Form + Zod

### Triển khai

- **Dev:** Docker Compose (PostgreSQL) + chạy backend/frontend trên máy
- **Free production:** Vercel (FE) + Render (BE) + Neon (DB) — [docs/deploy-free.md](docs/deploy-free.md)
- **VPS production:** Docker Compose full stack — [docs/deployment.md](docs/deployment.md)

---

## API (34 endpoints)

Base URL: `/api/v1`

- Local: `http://localhost:8080`
- Free deploy: `https://<backend>.onrender.com` (FE gọi qua `VITE_API_BASE_URL`)
- VPS Docker: cùng origin qua Nginx

| Nhóm | Endpoints |
| ---- | --------- |
| **Auth** | `POST /auth/register`, `/login`, `/refresh`, `/logout` |
| **User** | `GET /users/me`, `PATCH /users/me/password` |
| **House** | `POST/GET/PUT/DELETE /houses`, `GET/POST /houses/{id}/members`, `PUT/DELETE /houses/{id}/members/{userId}`, `DELETE /houses/{id}/members/me` |
| **Expense** | `POST/GET/PUT/DELETE /houses/{id}/expenses` |
| **Settlement** | `GET /houses/{id}/debts`, `POST/GET /houses/{id}/settlements`, `POST /houses/{id}/settlements/confirm-received` |
| **Dashboard** | `GET /houses/{id}/dashboard` |
| **Report** | `GET /houses/{id}/reports/excel`, `GET /houses/{id}/reports/pdf` |
| **Notification** | `GET /notifications`, `GET /notifications/unread-count`, `PATCH /notifications/{id}/read`, `PATCH /notifications/read-all` |
| **Activity** | `GET /houses/{id}/activities`, `GET /users/me/activities` |

Tài liệu tương tác: `/swagger-ui.html`

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

**Mời thành viên:**

```json
POST /api/v1/houses/{houseId}/members
{
  "identifier": "nam_dev",
  "role": "MEMBER"
}
```

`identifier` có thể là username hoặc email (trim, khớp chính xác).

**Xác nhận đã nhận tiền (người được nợ):**

```json
POST /api/v1/houses/{houseId}/settlements/confirm-received
{
  "fromUserId": "uuid-nguoi-no",
  "amount": 150000,
  "note": "Chuyển khoản Vietcombank"
}
```

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
| `settlements` | Ghi nhận thanh toán / xác nhận đã nhận |
| `activity_logs` | Nhật ký hoạt động |
| `notifications` | Thông báo in-app |

Migration: `V1` auth → `V2` houses → `V3` expenses → `V4` settlements → `V5` activity → `V6` soft delete → `V7` username → `V8` notifications

---

## Chạy local (phát triển)

Xem [docs/local-development.md](docs/local-development.md)

**Tóm tắt:**

```bash
# 1. PostgreSQL
docker compose -f deployment/docker/docker-compose.yml up -d

# 2. Backend (port 8080)
cd backend && mvn spring-boot:run

# 3. Frontend (port 5173)
cd frontend && npm install && npm run dev
```

**Yêu cầu:** Java 21, Maven 3.9+, Node.js 20+, Docker.

---

## Lộ trình phát triển

### Phiên bản 1–2 — ✅ Hoàn thành

Auth, nhóm, khoản chi, chia phí, công nợ, dashboard, activity, username, soft delete, frontend đầy đủ.

### Phiên bản 3 — 🔄 Gần hoàn thành (tạm dừng để deploy)

- ✅ Profile, đổi mật khẩu
- ✅ Xuất Excel / PDF
- ✅ Thông báo in-app
- ✅ Xác nhận đã nhận (creditor flow)
- ✅ UI/UX: modal, tabs, dropdown, toast
- ✅ Docker production
- ⬜ Upload hóa đơn

### Phiên bản 4 — ⬜ Kế hoạch

- Ứng dụng di động, API công khai, WebSocket, đa ngôn ngữ

---

## Định hướng

Dự án hướng tới nền tảng quản lý tài chính nhóm cho nhà trọ, gia đình, nhóm bạn, câu lạc bộ, doanh nghiệp nhỏ, nhóm du lịch.

Phục vụ học tập, nghiên cứu và xây dựng Portfolio **Java Backend Developer**, đồng thời triển khai thực tế cho nhóm nhỏ.

---

## Giấy phép

MIT License
