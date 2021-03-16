ALTER TABLE `bookmark`.`user`
ADD UNIQUE INDEX `userNameIndex`(`username`) USING BTREE COMMENT '用户名索引',
ADD UNIQUE INDEX `emailIndex`(`email`) USING BTREE COMMENT '邮箱索引';