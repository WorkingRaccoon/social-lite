-- Stored Procedures
DELIMITER //

CREATE PROCEDURE sp_create_post(
    IN p_user_id INT,
    IN p_content TEXT,
    IN p_image VARCHAR(255)
)
BEGIN
INSERT INTO Posts (user_id, content, image)
VALUES (p_user_id, p_content, p_image);
SELECT LAST_INSERT_ID() AS post_id;
END //

CREATE PROCEDURE sp_get_all_posts()
BEGIN
    SELECT p.post_id, p.content, p.image, p.created_at, p.updated_at,
           u.user_name, u.cover_image,
           COUNT(c.comment_id) AS comment_count
    FROM Posts p
             JOIN Users u ON p.user_id = u.user_id
             LEFT JOIN Comments c ON p.post_id = c.post_id
    GROUP BY p.post_id, p.content, p.image, p.created_at, p.updated_at, u.user_name, u.cover_image
    ORDER BY p.created_at DESC;
END //

CREATE PROCEDURE sp_update_post(
    IN p_post_id INT,
    IN p_user_id INT,
    IN p_content TEXT,
    IN p_image VARCHAR(255)
)
BEGIN
UPDATE Posts
SET content = p_content, image = p_image
WHERE post_id = p_post_id AND user_id = p_user_id;
END //

CREATE PROCEDURE sp_delete_post(
    IN p_post_id INT,
    IN p_user_id INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '刪除失敗，已回滾';
END;

START TRANSACTION;

-- 先刪除該文章的所有留言
DELETE FROM Comments WHERE post_id = p_post_id;

-- 再刪除文章
DELETE FROM Posts WHERE post_id = p_post_id AND user_id = p_user_id;

COMMIT;
END //

CREATE PROCEDURE sp_create_comment(
    IN p_post_id INT,
    IN p_user_id INT,
    IN p_content TEXT
)
BEGIN
INSERT INTO Comments (post_id, user_id, content)
VALUES (p_post_id, p_user_id, p_content);
SELECT LAST_INSERT_ID() AS comment_id;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE sp_get_post_by_id(IN p_post_id INT)
BEGIN
SELECT p.post_id, p.content, p.image, p.created_at, p.updated_at,
       p.user_id, u.user_name, u.cover_image
FROM Posts p
         JOIN Users u ON p.user_id = u.user_id
WHERE p.post_id = p_post_id;
END //

CREATE PROCEDURE sp_get_comments_by_post(IN p_post_id INT)
BEGIN
SELECT c.comment_id, c.content, c.created_at,
       u.user_name, u.cover_image
FROM Comments c
         JOIN Users u ON c.user_id = u.user_id
WHERE c.post_id = p_post_id
ORDER BY c.created_at ASC;
END //

DELIMITER ;