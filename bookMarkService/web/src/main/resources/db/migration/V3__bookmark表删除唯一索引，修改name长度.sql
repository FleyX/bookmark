ALTER TABLE `bookmark`.`bookmark`
MODIFY COLUMN `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL AFTER `type`,
DROP INDEX `userId_path_name_unique_index`;