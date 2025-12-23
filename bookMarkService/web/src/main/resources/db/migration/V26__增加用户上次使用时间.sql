ALTER TABLE `bookmark`.`user`
add COLUMN `lastActiveTime` bigint  NOT NULL default 0 COMMENT '上次活跃时间' ;