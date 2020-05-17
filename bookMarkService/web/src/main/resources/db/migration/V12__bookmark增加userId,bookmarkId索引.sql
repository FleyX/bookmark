ALTER TABLE `bookmark`.`bookmark`
ADD INDEX `userId_bookmarkId_index`(`userId`, `bookmarkId`) USING BTREE;