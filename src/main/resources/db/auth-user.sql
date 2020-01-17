DROP DATABASE IF EXISTS `user_service`;
CREATE DATABASE `user_service` DEFAULT CHARACTER SET utf8;
USE user_service;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auth_oauth
-- ----------------------------
DROP TABLE IF EXISTS `auth_oauth`;
CREATE TABLE `auth_oauth`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，与auth_user 唯一对应',
  `oauth_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '第三方授权来源、类型等',
  `oauth_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '授权id',
  `union_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '微信等用户唯一标识',
  `credential` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '登陆凭证',
  `refresh_token` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用于刷新验证的token。',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_uid_type`(`user_id`, `oauth_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '第三方授权表，存储用户授权信息。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_resource`;
CREATE TABLE `auth_resource`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `res_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '资源的名称',
  `res_type` tinyint(2) NOT NULL DEFAULT 1 COMMENT '分类：1 资源类型(根节点); 2 rest-api; 3 menu',
  `parent_id` int(11) NOT NULL DEFAULT -1 COMMENT '资源父ID。顶级 id -1',
  `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '资源定位符',
  `method` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '访问方式 GET POST PUT DELETE PATCH',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '资源状态，是否禁用等',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资源表，存储访问资源的url、method等信息。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_resource
-- ----------------------------
INSERT INTO `auth_resource` VALUES (1, 'category_api', 1, -1, ' ', ' ', 0, '2019-11-15 15:26:32', '2019-11-15 15:26:32');
INSERT INTO `auth_resource` VALUES (2, 'category_module', 1, -1, ' ', ' ', 0, '2019-11-15 15:26:32', '2019-11-15 15:26:32');

-- ----------------------------
-- Table structure for auth_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称， 应以role_ 为前缀',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '角色状态 初始化状态 0； 删除状态 -1',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_role
-- ----------------------------
INSERT INTO `auth_role` VALUES (1, 'role_admin', 0, '2019-11-15 15:26:34', '2019-11-15 15:26:34');
INSERT INTO `auth_role` VALUES (2, 'role_user', 0, '2019-11-15 15:26:34', '2019-11-15 15:26:34');

-- ----------------------------
-- Table structure for auth_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `auth_role_resource`;
CREATE TABLE `auth_role_resource`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色-权限表id',
  `role_id` int(11) NOT NULL COMMENT '角色ID，与auth_role 表唯一对应',
  `resource_id` int(11) NOT NULL COMMENT '资源ID，与resource_tbale表唯一对应',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_roleid_resourceid`(`role_id`, `resource_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色-资源表，依据角色访问资源。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_role_resource
-- ----------------------------
INSERT INTO `auth_role_resource` VALUES (1, 1, 1, '2019-11-15 15:26:35', '2019-11-15 15:26:35');

-- ----------------------------
-- Table structure for auth_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user`  (
  `id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账户名称',
  `slat_password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '加盐密码',
  `salt` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '盐',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户手机号',
  `mail` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '用户状态',
  `group_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '所属组织ID, 默认属于0组',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_mail`(`mail`) USING BTREE,
  UNIQUE INDEX `uk_phone_username_mail`(`phone`, `username`, `mail`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表，存储用户账户密码等登陆信息。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_user
-- ----------------------------
INSERT INTO `auth_user` VALUES (1, 'guodongzhang', '580c52e3666927a42b8bdef6dc685f08', '0ozb0l', '18888888888', 'guodongzhang@linkdoc.com', 0, 0, '2019-11-15 15:26:34', '2019-12-24 11:34:32');
INSERT INTO `auth_user` VALUES (2, 'test', '580c52e3666927a42b8bdef6dc685f08', '0ozb0l', NULL, NULL, 0, 0, '2019-12-30 20:35:01', '2019-12-30 20:35:12');

-- ----------------------------
-- Table structure for auth_user_info
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_info`;
CREATE TABLE `auth_user_info`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID，与auth_user 唯一对应',
  `real_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户真名',
  `sex` tinyint(2) NOT NULL DEFAULT -1 COMMENT '性别  -1 未知； 1 男； 2 女',
  `birth` bigint(14) NOT NULL DEFAULT 0 COMMENT '生日 最高精度 年月日时分秒 14位',
  `avatar_url` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '头像地址',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_uid`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息表，从用户表拆分出来，存储用户信息。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_user_info
-- ----------------------------
INSERT INTO `auth_user_info` VALUES (1, 1, '超级管理员', 1, 20191101081635, '1', '2019-11-15 15:26:34', '2019-11-15 15:26:34');

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role`  (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户-角色 id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id，与auth_user 唯一对应',
  `role_id` int(11) NOT NULL COMMENT '角色id，与auth_role 唯一对应',
  `time_created` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `time_updated` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_uid_roleid`(`role_id`, `user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户-角色，关联表。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_user_role
-- ----------------------------
INSERT INTO `auth_user_role` VALUES (1, 1, 1, '2019-11-15 15:26:34', '2019-12-23 14:19:25');

SET FOREIGN_KEY_CHECKS = 1;
