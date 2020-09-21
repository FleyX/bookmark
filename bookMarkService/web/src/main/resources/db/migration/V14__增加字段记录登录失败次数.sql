ALTER TABLE `bookmark`.`user`
MODIFY COLUMN `email` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生效邮箱' AFTER `username`,
MODIFY COLUMN `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像' AFTER `newEmail`,
MODIFY COLUMN `createTime` bigint(20) NOT NULL DEFAULT 0 COMMENT '创建时间' AFTER `password`,
MODIFY COLUMN `lastLoginTime` bigint(20) NOT NULL DEFAULT 0 COMMENT '上次登录时间' AFTER `createTime`,
MODIFY COLUMN `version` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '书签修改版本' AFTER `lastLoginTime`;