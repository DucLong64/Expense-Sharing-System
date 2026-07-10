# Free deploy: Vercel + Render + Neon

Hướng dẫn deploy miễn phí để bạn bè dùng thử.

```text
Browser
   │
   ▼
Frontend — Vercel (React / Vite)
   │  VITE_API_BASE_URL → https://<backend>.onrender.com
   ▼
Backend — Render Web Service (Spring Boot)
   │  JDBC + SSL
   ▼
Database — Neon PostgreSQL
```

---

## 0. Chuẩn bị

- Tài khoản [GitHub](https://github.com) (repo đã push)
- [Neon](https://neon.tech) — free
- [Render](https://render.com) — free
- [Vercel](https://vercel.com) — free

Thứ tự: **Neon → Render → Vercel** (cần URL backend trước khi set env frontend).

---

## 1. Neon (Database)

1. Đăng ký / đăng nhập Neon → **Create project**.
2. Chọn region gần (ví dụ Singapore / Singapore nếu có).
3. Vào **Dashboard → Connection details**.
4. Chọn connection string kiểu **JDBC** hoặc lấy host/user/password rồi ghép:

```text
jdbc:postgresql://ep-xxxx.region.aws.neon.tech/neondb?sslmode=require
```

Ghi lại:

| Biến | Giá trị |
| ---- | ------- |
| `DB_URL` | JDBC URL **có** `?sslmode=require` |
| `DB_USERNAME` | user Neon (thường `neondb_owner` hoặc tương tự) |
| `DB_PASSWORD` | password Neon |

> Flyway chạy tự động khi backend start lần đầu — không cần tạo bảng thủ công.

---

## 2. Render (Backend)

### 2.1 Tạo Web Service

1. [Render Dashboard](https://dashboard.render.com) → **New → Web Service**.
2. Connect repo GitHub `Expense-Sharing-System`.
3. Cấu hình:

| Field | Giá trị |
| ----- | ------- |
| **Name** | `expense-sharing-api` (tuỳ bạn) |
| **Region** | gần Neon nếu được |
| **Root Directory** | `backend` |
| **Runtime** | Docker **hoặc** Java |
| **Instance type** | Free |

#### Cách A — Docker (khuyên dùng)

| Field | Giá trị |
| ----- | ------- |
| Runtime | Docker |
| Dockerfile Path | `Dockerfile` (trong `backend/`) |

#### Cách B — Native Java

| Field | Giá trị |
| ----- | ------- |
| Runtime | Java |
| Build Command | `mvn -DskipTests package` |
| Start Command | `java $JAVA_OPTS -jar target/expense-sharing-backend-0.0.1-SNAPSHOT.jar` |

### 2.2 Environment variables (Render)

Thêm các biến sau (Environment):

| Key | Value |
| --- | ----- |
| `DB_URL` | `jdbc:postgresql://ep-....neon.tech/neondb?sslmode=require` |
| `DB_USERNAME` | từ Neon |
| `DB_PASSWORD` | từ Neon |
| `JWT_SECRET` | chuỗi ngẫu nhiên ≥ 32 ký tự (`openssl rand -base64 48`) |
| `CORS_ALLOWED_ORIGINS` | tạm `http://localhost:5173` — **cập nhật sau** khi có URL Vercel |
| `JAVA_OPTS` | `-Xms128m -Xmx400m -XX:+UseSerialGC` (quan trọng trên Free 512MB) |

> Render tự inject `PORT`. App đã đọc `${PORT}` — không cần set `SERVER_PORT`.

### 2.3 Health check (tuỳ chọn)

- Health Check Path: `/api-docs` (public, không cần JWT)

### 2.4 Deploy

Bấm **Create Web Service** / **Deploy**.

Khi xong, copy URL dạng:

```text
https://expense-sharing-api.onrender.com
```

Kiểm tra nhanh: mở `https://<backend>.onrender.com/swagger-ui.html`

---

## 3. Vercel (Frontend)

1. [Vercel](https://vercel.com) → **Add New Project** → import cùng repo.
2. Cấu hình:

| Field | Giá trị |
| ----- | ------- |
| **Root Directory** | `frontend` |
| **Framework Preset** | Vite |
| **Build Command** | `npm run build` |
| **Output Directory** | `dist` |
| **Install Command** | `npm install` |

3. **Environment Variables**:

| Key | Value |
| --- | ----- |
| `VITE_API_BASE_URL` | `https://<backend>.onrender.com` (không có `/` cuối) |

4. Deploy.

Copy URL frontend, ví dụ:

```text
https://expense-sharing.vercel.app
```

`frontend/vercel.json` đã cấu hình SPA rewrite (`/*` → `index.html`) cho React Router.

---

## 4. Cập nhật CORS trên Render

Quay lại Render → Environment → sửa:

```text
CORS_ALLOWED_ORIGINS=https://expense-sharing.vercel.app
```

Nếu có preview URL Vercel thêm (phân tách bằng dấu phẩy):

```text
CORS_ALLOWED_ORIGINS=https://expense-sharing.vercel.app,https://expense-sharing-git-main-xxx.vercel.app
```

**Manual Deploy** lại backend (hoặc Restart) để áp dụng CORS.

---

## 5. Kiểm tra end-to-end

1. Mở URL Vercel.
2. Đăng ký tài khoản → đăng nhập.
3. Tạo nhóm → mời bạn bằng username/email.
4. Thêm khoản chi → xem công nợ / thanh toán.

Swagger: `https://<backend>.onrender.com/swagger-ui.html`

---

## 6. Cập nhật code sau này

- Push lên `main` (hoặc branch đã gắn) → Render & Vercel auto-deploy (nếu bật).
- Đổi `VITE_*` trên Vercel → **phải Redeploy** frontend (biến được bake lúc build).

---

## Lưu ý Free tier

| Hiện tượng | Nguyên nhân | Cách xử lý |
| ---------- | ----------- | ---------- |
| Lần mở app đầu chậm 30–60s | Render Free sleep sau ~15 phút idle | Đợi cold start; lần sau nhanh hơn |
| Backend OOM / crash | Free chỉ ~512MB RAM | Giữ `JAVA_OPTS=-Xms128m -Xmx400m -XX:+UseSerialGC` |
| CORS error trên browser | Sai origin | `CORS_ALLOWED_ORIGINS` khớp đúng URL Vercel (`https://...`) |
| DB connection fail | Thiếu SSL | URL phải có `?sslmode=require` |
| PDF tiếng Việt lỗi | Thiếu font | Thêm `NotoSans-Regular.ttf` vào `backend/src/main/resources/fonts/` rồi redeploy |

---

## Checklist nhanh

- [ ] Neon project + JDBC URL có `sslmode=require`
- [ ] Render Web Service (`Root Directory` = `backend`) + env DB/JWT/CORS/JAVA_OPTS
- [ ] Swagger backend mở được
- [ ] Vercel (`Root Directory` = `frontend`) + `VITE_API_BASE_URL`
- [ ] Cập nhật `CORS_ALLOWED_ORIGINS` = URL Vercel + restart Render
- [ ] Đăng ký / tạo nhóm / mời bạn thành công

---

## So với Docker VPS

| | Free (Vercel + Render + Neon) | VPS Docker |
|--|-------------------------------|------------|
| Chi phí | $0 | ~$4–6/tháng |
| Cold start | Có (Render) | Không |
| HTTPS | Có sẵn | Tự cấu hình |
| Phù hợp | Demo / nhóm nhỏ | Dùng ổn định hơn |

Hướng dẫn VPS: [deployment.md](deployment.md).
