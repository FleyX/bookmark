CREATE TABLE bookmark.pin_bookmark
(
    id         int auto_increment NOT NULL,
    userId     int NOT NULL COMMENT '用户id',
    bookmarkId int NOT NULL COMMENT '书签id',
    sort       MEDIUMINT UNSIGNED NOT NULL COMMENT '排序，从1开始',
    createDate BIGINT NULL COMMENT '创建时间',
    CONSTRAINT pin_bookmark_pk PRIMARY KEY (id)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci
COMMENT='首页固定的书签';
CREATE INDEX pin_bookmark_userId_IDX USING BTREE ON bookmark.pin_bookmark (userId);
CREATE UNIQUE INDEX pin_bookmark_bookmarkId_IDX USING BTREE ON bookmark.pin_bookmark (bookmarkId);


-- 新增书签权限
INSERT INTO `bookmark`.`url`VALUES (13, 'GET', '/home/pin', 0);
INSERT INTO `bookmark`.`url`VALUES (14, 'PUT', '/home/pin', 1);
INSERT INTO `bookmark`.`url`VALUES (15, 'DELETE', '/home/pin', 1);
