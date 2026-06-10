-- 小区物业管理系统 数据库建表脚本
-- 数据库：property_management

CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE property_management;

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20),
    `email` VARCHAR(100),
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 楼栋表
CREATE TABLE IF NOT EXISTS `building` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `units` INT NOT NULL,
    `floors` INT NOT NULL,
    `description` VARCHAR(200),
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- 业主表
CREATE TABLE IF NOT EXISTS `owner` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20),
    `email` VARCHAR(100),
    `building_id` INT,
    `unit` VARCHAR(10),
    `room` VARCHAR(10),
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`building_id`) REFERENCES `building`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业主表';

-- 员工表
CREATE TABLE IF NOT EXISTS `employee` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(20),
    `position` VARCHAR(50),
    `department` VARCHAR(50),
    `is_deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 停车位表
CREATE TABLE IF NOT EXISTS `parking` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `number` VARCHAR(20) NOT NULL,
    `location` VARCHAR(100),
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0空闲/1占用',
    `owner_id` INT,
    `start_date` DATE,
    `end_date` DATE,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停车位表';

-- 报修表
CREATE TABLE IF NOT EXISTS `repair` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `owner_id` INT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `image` VARCHAR(200),
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待处理/1维修中/2已完成',
    `employee_id` INT,
    `rating` TINYINT COMMENT '1-5星评价',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`),
    FOREIGN KEY (`employee_id`) REFERENCES `employee`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- 留言投诉表
CREATE TABLE IF NOT EXISTS `complaint` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `owner_id` INT NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `reply` TEXT,
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待回复/1已回复',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`owner_id`) REFERENCES `owner`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言投诉表';

-- 初始数据：管理员账号（密码：admin123，BCrypt加密）
INSERT INTO `admin` (`username`, `password`, `name`, `phone`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800000000', 'admin@property.com');

-- 初始数据：楼栋
INSERT INTO `building` (`name`, `units`, `floors`, `description`) VALUES
('A栋', 2, 18, '高层住宅'),
('B栋', 2, 18, '高层住宅'),
('C栋', 1, 6, '多层住宅');

-- 初始数据：业主（密码：owner123）
INSERT INTO `owner` (`username`, `password`, `name`, `phone`, `email`, `building_id`, `unit`, `room`) VALUES
('owner1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13900000001', 'zhangsan@property.com', 1, '1', '101'),
('owner2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13900000002', 'lisi@property.com', 1, '1', '201'),
('owner3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', '13900000003', 'wangwu@property.com', 2, '2', '501');

-- 初始数据：员工
INSERT INTO `employee` (`name`, `phone`, `position`, `department`) VALUES
('赵维修', '13700000001', '维修工', '维修部'),
('钱保安', '13700000002', '保安', '安保部'),
('孙保洁', '13700000003', '保洁员', '保洁部');

-- 初始数据：停车位
INSERT INTO `parking` (`number`, `location`, `status`, `owner_id`, `start_date`, `end_date`) VALUES
('A-001', 'A区地下1层', 1, 1, '2026-01-01', '2026-12-31'),
('A-002', 'A区地下1层', 0, NULL, NULL, NULL),
('B-001', 'B区地下1层', 1, 2, '2026-01-01', '2026-12-31'),
('B-002', 'B区地下1层', 0, NULL, NULL, NULL);

-- 初始数据：报修
INSERT INTO `repair` (`owner_id`, `title`, `content`, `status`, `employee_id`, `rating`) VALUES
(1, '水管漏水', '厨房水管接口处漏水，已持续2天', 2, 1, 5),
(2, '门锁故障', '入户门锁无法正常锁闭', 1, 1, NULL),
(3, '楼道灯不亮', '3楼楼道灯已坏一周', 0, NULL, NULL);

-- 初始数据：投诉
INSERT INTO `complaint` (`owner_id`, `title`, `content`, `reply`, `status`) VALUES
(1, '噪音扰民', '楼上住户深夜制造噪音，严重影响休息', '已与楼上住户沟通，提醒注意作息时间', 1),
(2, '绿化问题', '小区绿化带杂草丛生', NULL, 0);
