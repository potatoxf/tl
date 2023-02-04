/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.7.28 : Database - cim
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `base_task_error` */

DROP TABLE IF EXISTS `base_task_error`;

CREATE TABLE `base_task_error` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_record_id` int(64) NOT NULL COMMENT '任务执行记录Id',
  `error_key` varchar(1024) NOT NULL COMMENT '信息关键字',
  `error_value` text COMMENT '信息内容',
  `created_time` bigint(13) NOT NULL COMMENT '创建时间',
  `updated_time` bigint(13) DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务出错现场信息表';

/*Table structure for table `base_task_information` */

DROP TABLE IF EXISTS `base_task_information`;

CREATE TABLE `base_task_information` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_no` varchar(64) NOT NULL COMMENT '任务编号',
  `task_name` varchar(64) NOT NULL COMMENT '任务名称',
  `scheduler_rule` varchar(64) NOT NULL COMMENT '定时规则表达式',
  `executor_no` varchar(128) NOT NULL COMMENT '执行方',
  `send_type` tinyint(4) DEFAULT NULL COMMENT '发送方式',
  `url` varchar(64) DEFAULT NULL COMMENT '请求地址',
  `execute_parameter` varchar(2000) DEFAULT NULL COMMENT '执行参数',
  `time_key` varchar(32) NOT NULL COMMENT '执行时间格式值',
  `frozen_time` bigint(13) DEFAULT NULL COMMENT '冻结时间',
  `unfrozen_time` bigint(13) DEFAULT NULL COMMENT '解冻时间',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `version` int(11) NOT NULL COMMENT '版本号：需要乐观锁控制',
  `created_time` bigint(13) NOT NULL COMMENT '创建时间',
  `updated_time` bigint(13) DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务信息表';

/*Table structure for table `base_task_record` */

DROP TABLE IF EXISTS `base_task_record`;

CREATE TABLE `base_task_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_no` varchar(64) NOT NULL COMMENT '任务编号',
  `time_key` varchar(32) DEFAULT NULL COMMENT '执行时间格式值',
  `status` tinyint(4) NOT NULL COMMENT '任务状态',
  `fail_count` int(10) DEFAULT NULL COMMENT '失败统计数',
  `fail_reason` varchar(64) DEFAULT NULL COMMENT '失败错误描述',
  `execute_time` bigint(13) NOT NULL COMMENT '执行时间',
  `created_time` bigint(13) NOT NULL COMMENT '创建时间',
  `updated_time` bigint(13) NOT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_records_taskno` (`task_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务执行情况记录表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
