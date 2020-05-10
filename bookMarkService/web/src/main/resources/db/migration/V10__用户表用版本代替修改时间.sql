update bookmark.user
set bookmarkChangeTime=0;
ALTER TABLE `bookmark`.`user`
    CHANGE COLUMN `bookmarkChangeTime` `version` int(10) UNSIGNED NOT NULL DEFAULT 0 AFTER `lastLoginTime`;
