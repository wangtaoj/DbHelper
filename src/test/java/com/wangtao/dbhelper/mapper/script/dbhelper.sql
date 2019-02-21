CREATE DATABASE `dbhelper`;

USE `dbhelper`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(32) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` int(11) DEFAULT NULL COMMENT '0: male, 1: female',
  `birthday` date DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

insert into `user`(`id`,`username`,`password`,`age`,`gender`,`birthday`,`update_time`) values (1,'wangtao','123456',20,1,'1997-05-03','2018-10-27 12:17:11'),(2,'汪涛','123456',21,1,'1997-05-03','2018-11-13 13:39:26'),(3,'赵六','123456',21,1,'1997-11-05','2018-11-14 15:56:16'),(4,'Jane','123456',22,0,'1996-11-13','2018-11-14 15:56:44'),(5,'Mike','123456',22,0,'1996-11-14','2018-11-14 15:57:39'),(6,'麻子','123456',22,1,'1996-11-14','2018-11-14 15:57:58'),(7,'王五','123456',24,0,'1994-05-04','2018-11-14 15:58:40'),(8,'小李','123456',18,1,'2000-11-14','2018-11-15 15:59:02'),(9,'张小凡','123456',24,1,'1994-11-15','2018-11-14 15:59:52'),(10,'碧瑶','123456',23,0,'1995-11-14','2018-11-14 16:00:15'),(11,'黄蓉','123456',23,0,'1995-11-14','2018-11-14 16:00:38'),(12,'郭靖','123456',24,1,'1994-11-14','2018-11-14 16:00:58');

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PROVINCE` varchar(10) NOT NULL,
  `CITY` varchar(50) NOT NULL,
  `ADDRESS` varchar(255) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

insert into `address`(`ID`,`PROVINCE`,`CITY`,`ADDRESS`,`USER_ID`) values (1,'湖南','长沙','中南林业科技大学',1);
	