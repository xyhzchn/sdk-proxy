/*
Navicat MySQL Data Transfer

Source Server         : ceshi
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : ceshi

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2019-09-26 17:54:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `config2`
-- ----------------------------
DROP TABLE IF EXISTS `config2`;
CREATE TABLE `config2` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '域名url',
  `req_open` tinyint(4) DEFAULT '0' COMMENT '请求参数：0-不开启1-开启',
  `req_params` text COMMENT '请求参数（明文）',
  `resp_open` tinyint(255) DEFAULT '0' COMMENT '响应参数：0-不开启1-开启',
  `resp_params` text COMMENT '响应参数（明文）',
  `ctime` datetime DEFAULT NULL,
  `utime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_url` (`url`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `datacheck`;
CREATE TABLE `datacheck` (
  `id` varchar(100) NOT NULL,
  `projectName` varchar(100) NOT NULL,
  `logType` varchar(100) NOT NULL,
  `logDate` varchar(10) NOT NULL,
  `sourceCount` bigint(20) DEFAULT '0',
  `kafkaCount` bigint(20) DEFAULT NULL,
  `flumeCount` bigint(20) DEFAULT '0',
  `state` int(1) DEFAULT '0',
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `flumeDeviationCount` bigint(20) DEFAULT '0',
  `kafkaDeviationCount` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS `detail_count`;
CREATE TABLE `detail_count` (
  `k` varchar(255) NOT NULL,
  `v` varchar(255) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  `utime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`k`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `log2db`;
CREATE TABLE `log2db` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` int(4) DEFAULT '0' COMMENT 'success|fail',
  `requestTime` datetime DEFAULT NULL COMMENT '请求开始时间',
  `responseTime` datetime DEFAULT NULL COMMENT '响应时间',
  `createAt` datetime DEFAULT NULL COMMENT '创建时间',
  `sendParams` text COMMENT '参数',
  `backData` text COMMENT '返回数据',
  `hostAndURI` text,
  `reqOriginParam` text,
  `respOriginParam` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2010 DEFAULT CHARSET=utf8;
