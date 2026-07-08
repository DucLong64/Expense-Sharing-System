# Triển khai production (Docker)

Hướng dẫn deploy toàn bộ ứng dụng (PostgreSQL + Backend + Frontend) lên một VPS hoặc máy chủ Linux để bạn bè truy cập qua trình duyệt.

## Kiến trúc triển khai

```text
Internet
   │
   ▼
[Nginx :80]  ── /api/* ──►  [Spring Boot :8080]
   │                              │
   └── /* (React SPA)             ▼
                            [PostgreSQL :5432]
```

- Frontend build tĩnh, Nginx phục vụ SPA và proxy `/api` tới backend.
- PostgreSQL chỉ expose nội bộ Docker network (không mở ra internet).
- JWT và mật khẩu DB lấy từ file `.env` (không commit).

## Yêu cầu

- VPS Linux (Ubuntu 22.04+ khuyên dùng) hoặc máy chủ có Docker
- Docker Engine 24+ và Docker Compose v2
- Tối thiểu **1 GB RAM**, **10 GB disk**
- Mở port **80** (và **443** nếu dùng HTTPS sau)

## Bước 1 — Chuẩn bị server

```bash
# Cài Docker (Ubuntu)
sudo apt update
sudo apt install -y git docker.io docker-compose-v2
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
# Đăng xuất / đăng nhập lại để dùng docker không cần sudo
```

## Bước 2 — Clone và cấu hình

```bash
git clone <URL_REPO_CUA_BAN> expense-sharing
cd expense-sharing

cp .env.example .env
```

Chỉnh `.env` — **bắt buộc** đổi các giá trị sau:

| Biến | Ghi chú |
| ---- | ------- |
| `POSTGRES_PASSWORD` | Mật khẩu DB mạnh |
| `JWT_SECRET` | Chuỗi ngẫu nhiên ≥ 32 ký tự (ví dụ: `openssl rand -base64 48`) |
| `CORS_ALLOWED_ORIGINS` | URL truy cập, ví dụ `http://123.45.67.89` hoặc `https://chi-tieu.example.com` |
| `HTTP_PORT` | Mặc định `80` |

Ví dụ `.env` production:

```env
POSTGRES_DB=expense_sharing
POSTGRES_USER=postgres
POSTGRES_PASSWORD=MyStr0ngDbP@ss!

JWT_SECRET=your-random-secret-at-least-32-characters-long-here
CORS_ALLOWED_ORIGINS=http://123.45.67.89
HTTP_PORT=80
```

## Bước 3 — Font PDF (tùy chọn, khuyên dùng)

Xuất PDF tiếng Việt cần font trong classpath:

```bash
# Tải Noto Sans (hoặc copy file .ttf vào)
mkdir -p backend/src/main/resources/fonts
# Đặt file NotoSans-Regular.ttf vào backend/src/main/resources/fonts/
```

Nếu bỏ qua bước này, API Excel vẫn hoạt động; PDF có thể lỗi khi xuất.

## Bước 4 — Build và chạy

Từ thư mục gốc project:

```bash
docker compose -f deployment/docker/docker-compose.prod.yml --env-file .env up -d --build
```

Kiểm tra:

```bash
docker compose -f deployment/docker/docker-compose.prod.yml ps
docker compose -f deployment/docker/docker-compose.prod.yml logs -f backend
```

- **Ứng dụng:** `http://<IP_SERVER>`
- **Swagger:** `http://<IP_SERVER>/swagger-ui.html`

Lần chạy đầu backend tự chạy Flyway migration.

## Bước 5 — Mở firewall

```bash
# UFW (Ubuntu)
sudo ufw allow 80/tcp
sudo ufw allow 22/tcp
sudo ufw enable
```

Trên cloud (AWS, GCP, Azure, Vultr...): mở **Inbound TCP 80** trong Security Group / Firewall.

## Bước 6 — Chia sẻ cho bạn bè

1. Gửi link `http://<IP_SERVER>` (hoặc domain nếu đã trỏ DNS).
2. Mỗi người **đăng ký** tài khoản riêng (username + email + mật khẩu).
3. Người tạo nhóm **mời thành viên** bằng username hoặc email chính xác (`POST /houses/{id}/members`).
4. Bắt đầu thêm khoản chi, xem công nợ, ghi nhận thanh toán / xác nhận đã nhận.

> **Lưu ý:** Hiện chưa có mã mời nhóm công khai — ADMIN/OWNER phải mời từng người.

## Cập nhật phiên bản mới

```bash
cd expense-sharing
git pull
docker compose -f deployment/docker/docker-compose.prod.yml --env-file .env up -d --build
```

Dữ liệu PostgreSQL nằm trong volume `postgres_data`, không mất khi rebuild container.

## Dừng / xóa

```bash
# Dừng
docker compose -f deployment/docker/docker-compose.prod.yml down

# Dừng và xóa toàn bộ dữ liệu DB (cẩn thận!)
docker compose -f deployment/docker/docker-compose.prod.yml down -v
```

## HTTPS (tùy chọn)

Cách đơn giản:

1. Trỏ domain về IP server.
2. Dùng [Caddy](https://caddyserver.com/) hoặc Certbot + Nginx reverse proxy phía trước container frontend.
3. Cập nhật `CORS_ALLOWED_ORIGINS` thành `https://your-domain.com` và restart backend:

```bash
docker compose -f deployment/docker/docker-compose.prod.yml --env-file .env up -d
```

Hoặc dùng **Cloudflare Tunnel** (miễn phí, không cần mở port 80) nếu không có domain riêng.

## Chạy local (chỉ PostgreSQL trong Docker)

Xem [local-development.md](local-development.md) — phù hợp khi dev trên máy cá nhân, không phải deploy cho nhiều người.

## Xử lý sự cố

| Triệu chứng | Gợi ý |
| ----------- | ----- |
| `502 Bad Gateway` trên `/api` | `docker logs expense-sharing-backend` — thường do DB chưa sẵn sàng hoặc sai `JWT_SECRET` / `DB_*` |
| Không đăng nhập được | Kiểm tra `CORS_ALLOWED_ORIGINS` khớp URL trình duyệt (kể cả `http` vs `https`) |
| PDF lỗi | Thêm `NotoSans-Regular.ttf` vào `backend/src/main/resources/fonts/` rồi build lại |
| Port 80 bị chiếm | Đổi `HTTP_PORT=8080` trong `.env` |

## Chi phí tham khảo

- VPS entry (1 GB RAM): ~$4–6/tháng (Vultr, DigitalOcean, Contabo...)
- Domain (tùy chọn): ~$10/năm
- Cloudflare Tunnel: miễn phí
