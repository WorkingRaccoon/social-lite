-- 建立資料庫
CREATE DATABASE IF NOT EXISTS social_lite CHARACTER SET utf8mb4;
USE social_lite;

-- 建立使用者表
CREATE TABLE Users(
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '使用者 ID',
    user_name VARCHAR(50)  NOT NULL COMMENT '使用者名稱',
    phone_number VARCHAR(100) NOT NULL UNIQUE COMMENT '使用者手機號碼',
    email VARCHAR(50)  NOT NULL COMMENT '使用者電子郵件',
    password VARCHAR(255) NOT NULL COMMENT '加密後密碼',
    cover_image VARCHAR(255) NULL COMMENT '封面照片URL',
    biography TEXT NULL COMMENT '自我介紹',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '帳號建立時間'
);

-- 建立發文表
CREATE TABLE Posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '發文 ID',
    user_id INT NOT NULL COMMENT '發文者 ID',
    content TEXT NOT NULL COMMENT '發文內容',
    image VARCHAR(255) NULL COMMENT '圖片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '發佈時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 建立留言表
CREATE TABLE Comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '留言 ID',
    post_id INT NOT NULL COMMENT '所屬發文 ID',
    user_id INT NOT NULL COMMENT '留言者 ID',
    content TEXT NOT NULL COMMENT '留言內容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '留言時間',
    FOREIGN KEY (post_id) REFERENCES Posts(post_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);
