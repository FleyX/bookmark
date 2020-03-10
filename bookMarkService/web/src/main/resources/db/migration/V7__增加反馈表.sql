CREATE TABLE `bookmark`.`feedback`  (
  `feedbackId` int(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `userId` int(0) NOT NULL,
  `type` enum('bug','advice') NOT NULL,
  `content` varchar(1000) NOT NULL,
  PRIMARY KEY (`feedbackId`)
);