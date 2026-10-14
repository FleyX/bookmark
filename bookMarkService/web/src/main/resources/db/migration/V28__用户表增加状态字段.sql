ALTER TABLE `bookmark`.`user`
    ADD COLUMN `status` tinyint NOT NULL DEFAULT 1 COMMENT '1正常 0禁用' AFTER `version`;
