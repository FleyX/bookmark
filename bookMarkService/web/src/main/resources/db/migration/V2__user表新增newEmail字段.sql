ALTER TABLE `bookmark`.`user`
ADD COLUMN `newEmail` varchar(255) NOT NULL DEFAULT '' COMMENT '新邮件地址，尚未确认' AFTER `email`;