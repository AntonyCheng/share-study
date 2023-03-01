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

 Date: 01/03/2023 21:26:26
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
INSERT INTO `t_college` VALUES (1627965942293053441, '哈尔滨商业大学', '10240', '2023-02-21 17:38:21', '2023-02-22 01:18:22', 0);
INSERT INTO `t_college` VALUES (1628076603207450626, '北京大学', '10001', '2023-02-22 00:58:05', '2023-02-22 01:18:22', 0);
INSERT INTO `t_college` VALUES (1628446055090606081, '测试大学', '88888', '2023-02-23 01:26:09', '2023-03-01 20:55:55', 0);

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
  `comment_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论中所带的文件OSSUrl',
  `comment_read_status` tinyint NOT NULL DEFAULT 0 COMMENT '评论是否已读（0表示未读，1表示已读）',
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
INSERT INTO `t_comment` VALUES (1, 2, 1628303246513643522, 1628303016208605185, '这是user发给admin的消息', NULL, 0, 0, '2023-02-28 22:30:17', '2023-02-28 22:30:17', 0);
INSERT INTO `t_comment` VALUES (2, 3, 1628303016208605185, 1628303184442138626, '这是admin发给super的消息', NULL, 0, 0, '2023-02-28 22:31:03', '2023-02-28 22:31:03', 0);
INSERT INTO `t_comment` VALUES (3, 1, 1628303184442138626, 1628303246513643522, '这是super发给user的消息', NULL, 0, 0, '2023-02-28 22:31:32', '2023-02-28 22:31:32', 0);

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
INSERT INTO `t_resource` VALUES (1, 1628303016208605185, '测试admin', '测试admin', 'test@admin.com', 0, '2023-02-28 08:02:14', '2023-03-01 10:16:37', 0);
INSERT INTO `t_resource` VALUES (2, 1628303246513643522, '测试user', '测试user', 'test@user.com', 0, '2023-02-28 21:46:05', '2023-03-01 10:16:37', 0);
INSERT INTO `t_resource` VALUES (3, 1628303184442138626, '测试super', '测试super', 'test@super.com', 0, '2023-02-28 21:46:52', '2023-02-28 21:46:52', 0);

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
INSERT INTO `t_teacher` VALUES (1628303016208605185, 'admin', '605c8beea36f63abeec7b55e06ebaecf', '管理员', 'https://sharestudy-1306588126.cos.ap-chengdu.myqcloud.com/super_avatar.jpg', 0, 1627965942293053441, 'admin@admin.com', 0, 0, 0, 0, 1, '2023-02-22 15:57:46', '2023-02-28 09:31:38', 0);
INSERT INTO `t_teacher` VALUES (1628303184442138626, 'super', '393b05d1a9652eef4b882773c81eae60', '超级管理员', 'https://sharestudy-1306588126.cos.ap-chengdu.myqcloud.com/super_avatar.jpg', 0, 1628076603207450626, 'super@super.com', 0, 0, 0, 0, 2, '2023-02-22 15:58:26', '2023-03-01 18:12:50', 0);
INSERT INTO `t_teacher` VALUES (1628303246513643522, 'user', '4af7ffbd5015c334748d00060b9ce132', '用户', 'https://sharestudy-1306588126.cos.ap-chengdu.myqcloud.com/super_avatar.jpg', 0, 1627965942293053441, 'user@user.com', 0, 0, 0, 0, 0, '2023-02-22 15:58:41', '2023-02-26 16:06:49', 0);
INSERT INTO `t_teacher` VALUES (1630917423728640002, 'test', '7137fcb1c6a440365676694dce1bbafe', '测试注册', 'http://dummyimage.com/100x100', 0, 1628446055090606081, 'test@test.com', 0, 0, 0, 0, 0, '2023-03-01 21:06:29', '2023-03-01 21:06:29', 0);

SET FOREIGN_KEY_CHECKS = 1;
