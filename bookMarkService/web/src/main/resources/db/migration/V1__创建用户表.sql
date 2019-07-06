CREATE TABLE `user` (
  `userId`     int UNSIGNED NOT NULL AUTO_INCREMENT,
  `username`   varchar(20)  NOT NULL
  COMMENT '用户名20位以内数字，字母组合',
  `email`      varchar(40)  NOT NULL,
  `icon`       varchar(50)  NOT NULL,
  `password`   char(40)     NOT NULL
  COMMENT '明文（6-20位数字密码组合）+userId 进行sha1签名',
  `createTime` bigint       NOT NULL DEFAULT 0,
  `lastLoginTime` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`userId`)
)
  ENGINE = InnoDB;