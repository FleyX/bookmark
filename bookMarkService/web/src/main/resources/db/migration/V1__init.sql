/*
 Navicat Premium Data Transfer

 Source Server         : 10.82.17.91_3307
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : 10.82.17.91:3307
 Source Schema         : bookmark

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 15/07/2019 18:24:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bookmark
-- ----------------------------
drop table IF EXISTS `bookmark`;
create TABLE `bookmark`  (
  `bookmarkId` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `userId` int(10) UNSIGNED NOT NULL COMMENT '所属用户id',
  `path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点路径,不包含自身',
  `type` tinyint(4) NOT NULL COMMENT '节点类别',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `icon` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sort` int(10) UNSIGNED NOT NULL,
  `addTime` bigint(20) NOT NULL COMMENT '添加书签时间',
  `createTime` bigint(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`bookmarkId`) USING BTREE,
  UNIQUE INDEX `userId_path_name_unique_index`(`userId`, `path`, `name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for url
-- ----------------------------
drop table IF EXISTS `url`;
create TABLE `url`  (
  `urlId` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `method` enum('GET','POST','PUT','DELETE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '0:公共接口，1：需登陆鉴权',
  PRIMARY KEY (`urlId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'url表，用于记录url' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of url
-- ----------------------------
insert into `url` VALUES (2, 'PUT', '/bookmark/uploadBookmarkFile', 1);
insert into `url` VALUES (3, 'PUT', '/user', 0);
insert into `url` VALUES (4, 'POST', '/user/login', 0);
insert into `url` VALUES (5, 'GET', '/user/authCode', 0);
insert into `url` VALUES (6, 'POST', '/user/resetPassword', 0);
insert into `url` VALUES (7, 'GET', '/bookmark/currentUser/path', 1);

-- ----------------------------
-- Table structure for user
-- ----------------------------
drop table IF EXISTS `user`;
create TABLE `user`  (
  `userId` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名20位以内数字，字母组合',
  `email` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` char(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '明文（6-20位数字密码组合）+userId 进行sha1签名',
  `createTime` bigint(20) NOT NULL DEFAULT 0,
  `lastLoginTime` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`userId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

