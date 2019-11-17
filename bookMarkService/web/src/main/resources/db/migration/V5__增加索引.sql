ALTER TABLE `bookmark`.`bookmark`
ADD INDEX `userId_path_index`(`userId`, `path`) USING BTREE;