# Chạy Backend Local

## Yêu cầu

- Java 21
- Maven 3.9+
- Docker Desktop (để chạy PostgreSQL)

## 1. Khởi động PostgreSQL

Từ thư mục gốc project:

```bash
docker compose -f deployment/docker/docker-compose.yml up -d
```

Kiểm tra container:

```bash
docker compose -f deployment/docker/docker-compose.yml ps
```

## 2. Cấu hình môi trường

### Cách A — Dùng Spring profile `local` (khuyên dùng)

```bash
copy backend\src\main\resources\application-local.yml.example backend\src\main\resources\application-local.yml
```

File `application-local.yml` đã được gitignore, dùng cho cấu hình dev cá nhân.

### Cách B — Dùng biến môi trường

```bash
copy .env.example .env
```

Sau đó set biến môi trường từ `.env` trước khi chạy app.

PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/expense_sharing"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="local-dev-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256"
$env:SPRING_PROFILES_ACTIVE="local"
```

## 3. Chạy backend

```bash
cd backend
mvn spring-boot:run
```

Hoặc với profile local:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## 4. Kiểm tra

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

Ví dụ đăng ký:

```bash
curl -X POST http://localhost:8080/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"long_dev\",\"email\":\"long@example.com\",\"password\":\"password123\",\"fullName\":\"Nguyen Van Long\"}"
```

Ví dụ đăng nhập:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"long_dev\",\"password\":\"password123\"}"
```

## 5. Chạy test

```bash
cd backend
mvn test
```

## 6. Chạy frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend chạy tại http://localhost:5173.

- Dev có thể dùng Vite proxy (`/api` -> backend) hoặc gọi trực tiếp backend nhờ CORS.
- CORS mặc định cho phép `http://localhost:5173`, chỉnh qua `CORS_ALLOWED_ORIGINS` (phân tách bằng dấu phẩy nếu nhiều origin).

## Dừng PostgreSQL

```bash
docker compose -f deployment/docker/docker-compose.yml down
```

Xóa cả dữ liệu:

```bash
docker compose -f deployment/docker/docker-compose.yml down -v
```
