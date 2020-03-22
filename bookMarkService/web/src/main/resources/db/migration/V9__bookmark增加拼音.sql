ALTER TABLE `bookmark`.`bookmark`
ADD COLUMN `searchKey` text NOT NULL COMMENT '前端用于搜索的key' AFTER `name`;

INSERT INTO `bookmark`.`url` VALUES (10,'POST', '/bookmark/allPinyinCreate', 1);
