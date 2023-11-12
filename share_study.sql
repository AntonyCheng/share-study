/*
 Navicat Premium Data Transfer

 Target Server Type    : MySQL
 Target Server Version : 80028 (8.0.28)
 File Encoding         : 65001

 Date: 12/11/2023 13:51:04
*/
CREATE database if NOT EXISTS `share_study` default character set utf8mb4 collate utf8mb4_general_ci;
use
    `share_study`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_collect
-- ----------------------------
DROP TABLE IF EXISTS `t_collect`;
CREATE TABLE `t_collect`  (
  `collect_id` bigint NOT NULL COMMENT '收藏唯一ID',
  `collect_belong` bigint NOT NULL COMMENT '收藏者ID',
  `collect_resource` bigint NOT NULL COMMENT '被收藏的教学资料ID',
  `collect_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被收藏的教学资料名称',
  `collect_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '被收藏的教学资料简介',
  `collect_status` tinyint NOT NULL DEFAULT 0 COMMENT '收藏状态（0表示已经收藏，1表示取消收藏）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '收藏时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '（0表示未删除，1表示已删除）',
  PRIMARY KEY (`collect_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_college
-- ----------------------------
DROP TABLE IF EXISTS `t_college`;
CREATE TABLE `t_college`  (
  `college_id` bigint NOT NULL COMMENT '高校唯一ID',
  `college_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '高校名称',
  `college_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '院校代码',
  `college_location` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学校地理坐标',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '高校录入时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '高校更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`college_id`) USING BTREE,
  UNIQUE INDEX `t_college_code_uindex`(`college_code` ASC) USING BTREE,
  UNIQUE INDEX `t_college_name_uindex`(`college_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '高校表' ROW_FORMAT = Dynamic;

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
  `comment_read_status` tinyint NOT NULL DEFAULT 0 COMMENT '评论是否已读（0表示未读，1表示已读，2表示接收者已删除）',
  `comment_status` tinyint NOT NULL DEFAULT 0 COMMENT '评论状态(0表示正常，1表示已被封禁)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`comment_id`) USING BTREE,
  UNIQUE INDEX `t_comment_comment_id_uindex`(`comment_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论交流表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_resource`;
CREATE TABLE `t_resource`  (
  `resource_id` bigint NOT NULL COMMENT '教学资料唯一ID',
  `resource_belong` bigint NOT NULL COMMENT '所属老师ID',
  `resource_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教学资料名',
  `resource_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料简介',
  `resource_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料所在地址',
  `resource_score` bigint NOT NULL DEFAULT 0 COMMENT '教学资料收藏数',
  `resource_tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料课程名称标签',
  `resource_status` tinyint NOT NULL DEFAULT 0 COMMENT '教学资料状态（0表示正常，1表示封禁）\n',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '教学资料发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '教学资料修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`resource_id`) USING BTREE,
  UNIQUE INDEX `t_resource_resource_id_uindex`(`resource_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教学资料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_resource_censor
-- ----------------------------
DROP TABLE IF EXISTS `t_resource_censor`;
CREATE TABLE `t_resource_censor`  (
  `censor_id` bigint NOT NULL COMMENT '教学资料审核唯一ID',
  `resource_belong` bigint NOT NULL COMMENT '所属老师ID',
  `resource_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教学资料名',
  `resource_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料简介',
  `resource_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料所在地址',
  `censor_admin_1_id` bigint NULL DEFAULT 0 COMMENT '第一审核员ID',
  `censor_admin_1_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第一审核员姓名',
  `censor_admin_1_result` bigint NULL DEFAULT 0 COMMENT '第一审核员审核结果（0表示未有审核结果，1表示审查通过，2表示审查未通过）',
  `censor_admin_2_id` bigint NULL DEFAULT 0 COMMENT '第二审核员ID',
  `censor_admin_2_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第二审核员姓名',
  `censor_admin_2_result` bigint NULL DEFAULT 0 COMMENT '第二审核员审核结果（0表示未有审核结果，1表示审查通过，2表示审查未通过）',
  `censor_admin_3_id` bigint NULL DEFAULT 0 COMMENT '第三审核员ID',
  `censor_admin_3_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三审核员姓名',
  `censor_admin_3_result` bigint NULL DEFAULT 0 COMMENT '第三审核员审核结果（0表示未有审核结果，1表示审查通过，2表示审查未通过）',
  `resource_tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教学资料课程名称标签',
  `censor_status` int NOT NULL DEFAULT 0 COMMENT '审查状态（0是未审查，1是正在审查，2是审查通过，3是审查未通过）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '教学资料审核发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '教学资料审核更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`censor_id`) USING BTREE,
  UNIQUE INDEX `censor_id`(`censor_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教学资料审查表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag`  (
  `tag_id` bigint NOT NULL COMMENT '资料标签唯一ID',
  `tag_belong` bigint NOT NULL COMMENT '所属学校ID',
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资料标签名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '资料标签发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '资料标签修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `t_teacher_teacher_account_uindex`(`teacher_account` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教师用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_teacher_censor
-- ----------------------------
DROP TABLE IF EXISTS `t_teacher_censor`;
CREATE TABLE `t_teacher_censor`  (
  `censor_id` bigint NOT NULL COMMENT '教师注册审核唯一ID',
  `teacher_account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师账号（具有唯一性，推荐手机号）',
  `teacher_password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师账号密码（推荐6-16位）',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '教师姓名',
  `teacher_avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教师头像地址',
  `teacher_gender` tinyint NOT NULL DEFAULT 0 COMMENT '教师性别（0表示男性，1表示女性）',
  `teacher_belong` bigint NOT NULL COMMENT '所属高校的id',
  `teacher_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '教师邮箱',
  `censor_admin_id` bigint NULL DEFAULT 0 COMMENT '审核员ID',
  `censor_admin_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核员姓名',
  `censor_status` int NOT NULL DEFAULT 0 COMMENT '审核状态（0是未审核，1是审核通过，2是审核未通过，3是请求已发布）',
  `censor_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '审核反馈',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '教师用户注册审核发布时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '教师用户注册审核更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`censor_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户注册审核表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
