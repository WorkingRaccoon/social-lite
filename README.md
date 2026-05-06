# Social Lite

一個使用 Vue.js + Spring Boot 開發的簡易社群平台，支援使用者註冊登入、發文、留言等功能。

---

## 專案架構

本專案採用前後端整合：

- **social-lite**：[social-lite-frontend](https://github.com/WorkingRaccoon/social-lite)

### 系統架構

```
Web Server (Vue.js)
    ↕ RESTful API
Application Server (Spring Boot)
    ↕ Stored Procedure
Database (MariaDB)
```

---

## 技術棧

### 前端
- Vue.js 3（Composition API）
- Vue Router
- Pinia（狀態管理）
- Axios（HTTP 請求）
- Tailwind CSS

### 後端
- Spring Boot 3
- Spring Security
- JWT（身份驗證）
- Redis（JWT 黑名單）
- JdbcTemplate + Stored Procedure
- Maven

### 資料庫
- MariaDB

---

## 功能列表

- 使用者以手機號碼註冊與登入
- JWT Token 身份驗證
- 登出時將 Token 加入 Redis 黑名單，防止 Token 被重複使用
- 發文（新增、列表、詳情、編輯、刪除）
- 發文支援圖片上傳
- 留言功能
- 發文列表顯示留言數量
- 刪除發文時透過 Transaction 同步刪除留言
- 刪除發文時同步刪除伺服器上的圖片資料夾
- XSS 防護
- SQL Injection 防護（Stored Procedure + 參數化查詢）
- 日夜模式切換

---

## 環境需求

- Node.js 18+
- Java 17+
- Maven 3.8+
- MariaDB 10.6+
- Redis（透過 Docker 執行）
- Docker Desktop

---

## 安裝與啟動

### 1. 資料庫設定

在 phpMyAdmin 或 MySQL CLI 執行 `backend/DB/` 資料夾內的 SQL 檔案：

```sql
source DB/ddl.sql;
source DB/stored_procedures.sql;
```

### 2. 啟動 Redis

```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

若已建立過容器，使用以下指令重新啟動：

```bash
docker start redis
```

### 3. 設定後端環境變數

編輯 `backend/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/social_lite
spring.datasource.username=你的資料庫帳號
spring.datasource.password=你的資料庫密碼
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 4. 啟動後端

```bash
cd backend
mvn spring-boot:run
```

後端預設運行於 `http://localhost:8080`

### 5. 啟動前端

```bash
cd frontend
npm install
npm run dev
```

前端預設運行於 `http://localhost:5173`

---

## API 文件

### 認證

| 方法 | 端點 | 說明 | 需要認證 |
|------|------|------|---------|
| POST | /api/users | 註冊 | ❌ |
| POST | /api/auth/token | 登入（取得 JWT） | ❌ |
| DELETE | /api/auth/token | 登出（Token 加入黑名單） | ✅ |

### 發文

| 方法 | 端點 | 說明 | 需要認證 |
|------|------|------|---------|
| GET | /api/posts | 取得所有發文 | ✅ |
| POST | /api/posts | 新增發文 | ✅ |
| GET | /api/posts/{id} | 取得單篇發文 | ✅ |
| PUT | /api/posts/{id} | 編輯發文 | ✅ |
| DELETE | /api/posts/{id} | 刪除發文 | ✅ |

### 留言

| 方法 | 端點 | 說明 | 需要認證 |
|------|------|------|---------|
| GET | /api/posts/{id}/comments | 取得發文留言 | ✅ |
| POST | /api/posts/{id}/comments | 新增留言 | ✅ |

---

## 資料庫設計

### Users
| 欄位 | 類型 | 說明 |
|------|------|------|
| user_id | INT | Primary Key |
| user_name | VARCHAR(50) | 使用者名稱 |
| phone_number | VARCHAR(100) | 手機號碼（唯一） |
| email | VARCHAR(50) | 電子郵件 |
| password | VARCHAR(255) | 加密後密碼（BCrypt） |
| cover_image | VARCHAR(255) | 封面照片路徑 |
| biography | TEXT | 自我介紹 |
| created_at | TIMESTAMP | 建立時間 |

### Posts
| 欄位 | 類型 | 說明 |
|------|------|------|
| post_id | INT | Primary Key |
| user_id | INT | Foreign Key → Users |
| content | TEXT | 發文內容 |
| image | VARCHAR(255) | 圖片路徑 |
| created_at | TIMESTAMP | 發文時間 |
| updated_at | TIMESTAMP | 更新時間 |

### Comments
| 欄位 | 類型 | 說明 |
|------|------|------|
| comment_id | INT | Primary Key |
| post_id | INT | Foreign Key → Posts |
| user_id | INT | Foreign Key → Users |
| content | TEXT | 留言內容 |
| created_at | TIMESTAMP | 留言時間 |

---

## 安全性設計

- **JWT 黑名單**：登出後 Token 存入 Redis，有效期間內無法再使用
- **Spring Security**：所有 API 皆需驗證 Token，公開路徑僅開放註冊與登入
- **Stored Procedure**：所有資料庫操作透過 Stored Procedure 執行，防止 SQL Injection
- **XSS 防護**：使用者輸入在 Service 層透過 XssUtils 進行 HTML 實體跳脫處理
- **密碼加密**：使用 BCrypt 加密儲存密碼
- **Transaction**：刪除發文時同時刪除留言，避免資料錯亂

---

## 後端專案結構

```
backend/
├── src/main/java/com/workingraccoon/backend/
│   ├── config/                          # 共用層 - 設定
│   │   ├── JwtFilter.java               # JWT 驗證 Filter
│   │   ├── SecurityConfig.java          # Spring Security 設定
│   │   └── WebConfig.java               # 靜態資源設定
│   ├── controller/                      # 展示層
│   │   ├── AuthController.java          # 登入登出
│   │   ├── CommentController.java       # 留言
│   │   ├── PostController.java          # 發文
│   │   └── RegisterController.java      # 註冊
│   ├── DTO/                             # 共用層 - 資料傳輸物件
│   │   ├── CommentRequest.java
│   │   ├── LoginRequest.java
│   │   ├── PostRequest.java
│   │   └── User.java
│   ├── repository/                      # 資料層
│   │   ├── AuthRepository.java
│   │   ├── CommentRepository.java
│   │   ├── PostRepository.java
│   │   └── RegisterRepository.java
│   ├── service/                         # 業務層
│   │   ├── AuthService.java
│   │   ├── CommentService.java
│   │   ├── PostService.java
│   │   ├── RegisterService.java
│   │   └── TokenBlacklistService.java
│   └── util/                            # 共用層 - 工具
│       ├── JwtUtil.java
│       └── XssUtils.java
└── DB/
    ├── ddl.sql                          # 資料表結構
    └── stored_procedures.sql            # Stored Procedures
```

## 前端專案結構

```
frontend/
├── src/
│   ├── views/
│   │   ├── Login.vue                    # 登入頁面
│   │   ├── Register.vue                 # 註冊頁面
│   │   ├── Forum.vue                    # 論壇首頁
│   │   └── PostDetail.vue               # 文章詳情頁面
│   ├── stores/
│   │   ├── auth.js                      # 使用者狀態管理
│   │   └── theme.js                     # 日夜模式狀態管理
│   ├── router/
│   │   └── index.js                     # 路由設定
│   ├── App.vue                          # 根組件
│   └── main.js                          # 入口檔案
```
