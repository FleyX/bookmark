ALTER TABLE `bookmark`.`user`
add COLUMN `lastActiveTime` datetime NOT NULL default CURRENT_TIMESTAMP COMMENT '上次活跃时间' ;