/*
 Navicat Premium Data Transfer

 Source Server         : AntonyCheng
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : share_study

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 21/02/2023 01:01:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_college
-- ----------------------------
DROP TABLE IF EXISTS `t_college`;
CREATE TABLE `t_college`  (
  `college_id` bigint NOT NULL COMMENT '高校唯一ID',
  `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '高校名称',
  `college_belong` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属上级',
  `college_address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '高校地址',
  `college_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '院校代码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '高校录入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '高校更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`college_id`) USING BTREE,
  UNIQUE INDEX `t_college_code_uindex`(`college_code`) USING BTREE,
  UNIQUE INDEX `t_college_name_uindex`(`college_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '高校表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_college
-- ----------------------------

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment`  (
  `comment_id` bigint NOT NULL COMMENT '评论交流唯一ID',
  `comment_resource` bigint NOT NULL COMMENT '评论所属资料ID',
  `comment_belong` bigint NOT NULL COMMENT '评论的教师用户ID',
  `comment_send` bigint NOT NULL COMMENT '接收评论的教师用户ID',
  `comment_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `comment_read` tinyint NOT NULL DEFAULT 0 COMMENT '评论是否已读（0表示未读，1表示已读）',
  `comment_status` tinyint NOT NULL DEFAULT 0 COMMENT '评论状态(0表示正常，1表示已被封禁)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`comment_id`) USING BTREE,
  UNIQUE INDEX `t_comment_comment_id_uindex`(`comment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论交流表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_comment
-- ----------------------------

-- ----------------------------
-- Table structure for t_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_resource`;
CREATE TABLE `t_resource`  (
  `resource_id` bigint NOT NULL COMMENT '教学资料唯一ID',
  `resource_belong` bigint NOT NULL COMMENT '所属老师ID',
  `resource_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教学资料名',
  `resource_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料简介',
  `resource_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教学资料所在地址',
  `resource_status` tinyint NOT NULL DEFAULT 0 COMMENT '教学资料状态（0表示正常，1表示封禁）\n',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '教学资料发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '教学资料修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`resource_id`) USING BTREE,
  UNIQUE INDEX `t_resource_resource_id_uindex`(`resource_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教学资料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_teacher
-- ----------------------------
DROP TABLE IF EXISTS `t_teacher`;
CREATE TABLE `t_teacher`  (
  `teacher_id` bigint NOT NULL COMMENT '教师用户唯一ID',
  `teacher_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师账号（具有唯一性，推荐手机号）',
  `teacher_password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师账号密码（推荐6-16位）',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师姓名',
  `teacher_avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教师头像地址',
  `teacher_gender` tinyint NOT NULL DEFAULT 0 COMMENT '教师性别（0表示男性，1表示女性）',
  `teacher_belong` bigint NOT NULL COMMENT '所属高校的id',
  `teacher_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教师邮箱',
  `teacher_score` bigint NOT NULL DEFAULT 0 COMMENT '教师创作贡献度',
  `teacher_message_total` bigint NOT NULL DEFAULT 0 COMMENT '消息总数',
  `teacher_message_read` bigint NOT NULL DEFAULT 0 COMMENT '消息已读数',
  `teacher_status` tinyint NOT NULL DEFAULT 0 COMMENT '教师状态（0表示正常，1表示封禁）',
  `teacher_role` tinyint NOT NULL DEFAULT 0 COMMENT '用户角色（0普通用户，1管理员用户，2超级管理员）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '教师录入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '教师修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`teacher_id`) USING BTREE,
  UNIQUE INDEX `t_teacher_teacher_account_uindex`(`teacher_account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教师用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_teacher
-- ----------------------------
INSERT INTO `t_teacher` VALUES (1627705621900886018, 'admin', '605c8beea36f63abeec7b55e06ebaecf', 'Admin', 'https://sharestudy-1306588126.cos.ap-chengdu.myqcloud.com/super_avatar.jpg', 0, 0, '199999999@qq.com', 0, 0, 0, 0, 2, '2023-02-21 00:23:56', '2023-02-21 00:24:27', 0);

SET FOREIGN_KEY_CHECKS = 1;
