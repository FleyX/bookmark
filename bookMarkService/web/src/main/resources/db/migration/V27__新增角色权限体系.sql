-- 角色表
CREATE TABLE `role`
(
    roleId      int(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code        varchar(50)      NOT NULL COMMENT '角色编码，唯一',
    name        varchar(50)      NOT NULL COMMENT '角色名称',
    description varchar(200)     NOT NULL DEFAULT '' COMMENT '描述',
    status      tinyint          NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    buildIn     tinyint          NOT NULL DEFAULT 0 COMMENT '1内置角色(不可改删)',
    createTime  bigint(20)       NOT NULL DEFAULT 0,
    updateTime  bigint(20)       NOT NULL DEFAULT 0,
    UNIQUE KEY codeIndex (code)
);

-- 用户角色绑定表
CREATE TABLE user_role
(
    userRoleId int(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId     int(10) UNSIGNED NOT NULL,
    roleId     int(10) UNSIGNED NOT NULL,
    createTime bigint(20)       NOT NULL DEFAULT 0,
    UNIQUE KEY user_role_unique (userId, roleId),
    KEY roleIdIndex (roleId)
);

-- 角色权限绑定表，绑定 permissions.yml 中的权限 code
CREATE TABLE role_permission
(
    rolePermissionId int(10) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    roleId           int(10) UNSIGNED NOT NULL,
    permissionCode   varchar(100)     NOT NULL COMMENT 'permissions.yml 中的权限 code',
    createTime       bigint(20)       NOT NULL DEFAULT 0,
    UNIQUE KEY role_permission_unique (roleId, permissionCode),
    KEY permissionCodeIndex (permissionCode)
);

-- 内置超级管理员角色
INSERT INTO `role` (code, name, description, status, buildIn, createTime, updateTime)
VALUES ('super_admin', '超级管理员', '内置角色，拥有全部权限', 1, 1, 1791967179128, 1791967179128);

-- 最早注册的用户绑定为超级管理员
INSERT INTO user_role (userId, roleId, createTime)
SELECT userId, (SELECT roleId FROM `role` WHERE code = 'super_admin'), 1791967179128
FROM user
ORDER BY userId ASC
LIMIT 1;
