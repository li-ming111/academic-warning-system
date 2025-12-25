/*
SQLyog Ultimate v11.3 (64 bit)
MySQL - 5.7.32-log : Database - ssmhn4b5
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`ssmhn4b5` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `ssmhn4b5`;

/*Table structure for table `chat` */

DROP TABLE IF EXISTS `chat`;

CREATE TABLE `chat` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `userid` bigint(20) NOT NULL COMMENT '用户id',
                        `adminid` bigint(20) DEFAULT NULL COMMENT '管理员id',
                        `ask` longtext COMMENT '提问',
                        `reply` longtext COMMENT '回复',
                        `isreply` int(11) DEFAULT NULL COMMENT '是否回复',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376647504 DEFAULT CHARSET=utf8 COMMENT='在线咨询';

/*Data for the table `chat` */

insert  into `chat`(`id`,`addtime`,`userid`,`adminid`,`ask`,`reply`,`isreply`) values (81,'2021-04-14 12:27:13',1,1,'提问1','回复1',1),(82,'2021-04-14 12:27:13',2,2,'提问2','回复2',2),(83,'2021-04-14 12:27:13',3,3,'提问3','回复3',3),(84,'2021-04-14 12:27:13',4,4,'提问4','回复4',4),(85,'2021-04-14 12:27:13',5,5,'提问5','回复5',5),(86,'2021-04-14 12:27:13',6,6,'提问6','回复6',6),(1618376647503,'2021-04-14 13:04:07',1618376563186,NULL,'你好啊',NULL,1);

/*Table structure for table `config` */

DROP TABLE IF EXISTS `config`;

CREATE TABLE `config` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                          `name` varchar(100) NOT NULL COMMENT '配置参数名称',
                          `value` varchar(100) DEFAULT NULL COMMENT '配置参数值',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='配置文件';

/*Data for the table `config` */

insert  into `config`(`id`,`name`,`value`) values (1,'picture1','http://localhost:8080/ssmhn4b5/upload/picture1.jpg'),(2,'picture2','http://localhost:8080/ssmhn4b5/upload/picture2.jpg'),(3,'picture3','http://localhost:8080/ssmhn4b5/upload/picture3.jpg'),(6,'homepage',NULL);

/*Table structure for table `huiyuan` */

DROP TABLE IF EXISTS `huiyuan`;

CREATE TABLE `huiyuan` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `xuehao` varchar(200) NOT NULL COMMENT '学号',
                           `mima` varchar(200) NOT NULL COMMENT '密码',
                           `xingming` varchar(200) NOT NULL COMMENT '姓名',
                           `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
                           `nianling` int(11) DEFAULT NULL COMMENT '年龄',
                           `dianhua` varchar(200) DEFAULT NULL COMMENT '电话',
                           `youxiang` varchar(200) DEFAULT NULL COMMENT '邮箱',
                           `shenfenzheng` varchar(200) DEFAULT NULL COMMENT '身份证',
                           `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `xuehao` (`xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376563187 DEFAULT CHARSET=utf8 COMMENT='会员';

/*Data for the table `huiyuan` */

insert  into `huiyuan`(`id`,`addtime`,`xuehao`,`mima`,`xingming`,`xingbie`,`nianling`,`dianhua`,`youxiang`,`shenfenzheng`,`zhaopian`) values (51,'2021-04-14 12:27:13','会员1','123456','姓名1','男',1,'13823888881','773890001@qq.com','440300199101010001','http://localhost:8080/ssmhn4b5/upload/huiyuan_zhaopian1.jpg'),(52,'2021-04-14 12:27:13','会员2','123456','姓名2','女',23,'13823888882','773890002@qq.com','440300199202020002','http://localhost:8080/ssmhn4b5/upload/huiyuan_zhaopian2.jpg'),(53,'2021-04-14 12:27:13','会员3','123456','姓名3','男',3,'13823888883','773890003@qq.com','440300199303030003','http://localhost:8080/ssmhn4b5/upload/huiyuan_zhaopian3.jpg'),(54,'2021-04-14 12:27:13','会员4','123456','姓名4','男',4,'13823888884','773890004@qq.com','440300199404040004','http://localhost:8080/ssmhn4b5/upload/huiyuan_zhaopian4.jpg'),(55,'2021-04-14 12:27:13','会员5','123456','姓名5','男',5,'13823888885','773890005@qq.com','440300199505050005','http://localhost:8080/ssmhn4b5/upload/huiyuan_zhaopian5.jpg'),(56,'2021-04-14 12:27:13','会员6','123456','姓名6','男',6,'13823888886','773890006@qq.com','440300199606060006','http://localhost:8080/ssmhn4b5/upload/huiyuan_zhaopian6.jpg'),(1618376430224,'2021-04-14 13:00:30','333','333','李大哥','男',33,'12312312312','2@qq.com','123123123123123123','http://localhost:8080/ssmhn4b5/upload/1618376429498.jpg'),(1618376563186,'2021-04-14 13:02:43','100','123456','李四','男',21,'12312312312','213@qq.com','123131123123123123','http://localhost:8080/ssmhn4b5/upload/1618376532578.jpg');

/*Table structure for table `huiyuanshenqing` */

DROP TABLE IF EXISTS `huiyuanshenqing`;

CREATE TABLE `huiyuanshenqing` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `xuehao` varchar(200) NOT NULL COMMENT '学号',
                                   `mima` varchar(200) NOT NULL COMMENT '密码',
                                   `xingming` varchar(200) NOT NULL COMMENT '姓名',
                                   `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
                                   `nianling` int(11) DEFAULT NULL COMMENT '年龄',
                                   `dianhua` varchar(200) DEFAULT NULL COMMENT '电话',
                                   `youxiang` varchar(200) DEFAULT NULL COMMENT '邮箱',
                                   `shenfenzheng` varchar(200) DEFAULT NULL COMMENT '身份证',
                                   `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
                                   `sfsh` varchar(200) DEFAULT '否' COMMENT '是否审核',
                                   `shhf` longtext COMMENT '审核回复',
                                   `userid` bigint(20) DEFAULT NULL COMMENT '用户id',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376538421 DEFAULT CHARSET=utf8 COMMENT='会员申请';

/*Data for the table `huiyuanshenqing` */

insert  into `huiyuanshenqing`(`id`,`addtime`,`xuehao`,`mima`,`xingming`,`xingbie`,`nianling`,`dianhua`,`youxiang`,`shenfenzheng`,`zhaopian`,`sfsh`,`shhf`,`userid`) values (71,'2021-04-14 12:27:13','学号1','密码1','姓名1','性别1',1,'13823888881','773890001@qq.com','440300199101010001','http://localhost:8080/ssmhn4b5/upload/huiyuanshenqing_zhaopian1.jpg','是','',1),(72,'2021-04-14 12:27:13','学号2','密码2','姓名2','性别2',2,'13823888882','773890002@qq.com','440300199202020002','http://localhost:8080/ssmhn4b5/upload/huiyuanshenqing_zhaopian2.jpg','是','',2),(73,'2021-04-14 12:27:13','学号3','密码3','姓名3','性别3',3,'13823888883','773890003@qq.com','440300199303030003','http://localhost:8080/ssmhn4b5/upload/huiyuanshenqing_zhaopian3.jpg','是','',3),(74,'2021-04-14 12:27:13','学号4','密码4','姓名4','性别4',4,'13823888884','773890004@qq.com','440300199404040004','http://localhost:8080/ssmhn4b5/upload/huiyuanshenqing_zhaopian4.jpg','是','',4),(75,'2021-04-14 12:27:13','学号5','密码5','姓名5','性别5',5,'13823888885','773890005@qq.com','440300199505050005','http://localhost:8080/ssmhn4b5/upload/huiyuanshenqing_zhaopian5.jpg','是','',5),(76,'2021-04-14 12:27:13','学号6','密码6','姓名6','性别6',6,'13823888886','773890006@qq.com','440300199606060006','http://localhost:8080/ssmhn4b5/upload/huiyuanshenqing_zhaopian6.jpg','是','',6),(1618376538420,'2021-04-14 13:02:17','100','123456','李四','男',21,'12312312312','213@qq.com','123131123123123123','http://localhost:8080/ssmhn4b5/upload/1618376532578.jpg','是','tongguo',1618376476788);

/*Table structure for table `huodongcanyu` */

DROP TABLE IF EXISTS `huodongcanyu`;

CREATE TABLE `huodongcanyu` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `huodongmingcheng` varchar(200) NOT NULL COMMENT '活动名称',
                                `huodongleixing` varchar(200) NOT NULL COMMENT '活动类型',
                                `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
                                `jubanriqi` varchar(200) DEFAULT NULL COMMENT '举办日期',
                                `jubanshetuan` varchar(200) DEFAULT NULL COMMENT '举办社团',
                                `huodongdidian` varchar(200) DEFAULT NULL COMMENT '活动地点',
                                `canyushijian` datetime DEFAULT NULL COMMENT '参与时间',
                                `xuehao` varchar(200) DEFAULT NULL COMMENT '学号',
                                `xingming` varchar(200) DEFAULT NULL COMMENT '姓名',
                                `sfsh` varchar(200) DEFAULT '否' COMMENT '是否审核',
                                `shhf` longtext COMMENT '审核回复',
                                `userid` bigint(20) DEFAULT NULL COMMENT '用户id',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376634102 DEFAULT CHARSET=utf8 COMMENT='活动参与';

/*Data for the table `huodongcanyu` */

insert  into `huodongcanyu`(`id`,`addtime`,`huodongmingcheng`,`huodongleixing`,`zhaopian`,`jubanriqi`,`jubanshetuan`,`huodongdidian`,`canyushijian`,`xuehao`,`xingming`,`sfsh`,`shhf`,`userid`) values (61,'2021-04-14 12:27:13','活动名称1','活动类型1','http://localhost:8080/ssmhn4b5/upload/huodongcanyu_zhaopian1.jpg','举办日期1','举办社团1','活动地点1','2021-04-14 12:27:13','学号1','姓名1','是','',1),(62,'2021-04-14 12:27:13','活动名称2','活动类型2','http://localhost:8080/ssmhn4b5/upload/huodongcanyu_zhaopian2.jpg','举办日期2','举办社团2','活动地点2','2021-04-14 12:27:13','学号2','姓名2','是','',2),(63,'2021-04-14 12:27:13','活动名称3','活动类型3','http://localhost:8080/ssmhn4b5/upload/huodongcanyu_zhaopian3.jpg','举办日期3','举办社团3','活动地点3','2021-04-14 12:27:13','学号3','姓名3','是','',3),(64,'2021-04-14 12:27:13','活动名称4','活动类型4','http://localhost:8080/ssmhn4b5/upload/huodongcanyu_zhaopian4.jpg','举办日期4','举办社团4','活动地点4','2021-04-14 12:27:13','学号4','姓名4','是','',4),(65,'2021-04-14 12:27:13','活动名称5','活动类型5','http://localhost:8080/ssmhn4b5/upload/huodongcanyu_zhaopian5.jpg','举办日期5','举办社团5','活动地点5','2021-04-14 12:27:13','学号5','姓名5','是','',5),(66,'2021-04-14 12:27:13','活动名称6','活动类型6','http://localhost:8080/ssmhn4b5/upload/huodongcanyu_zhaopian6.jpg','举办日期6','举办社团6','活动地点6','2021-04-14 12:27:13','学号6','姓名6','是','',6),(1618376634101,'2021-04-14 13:03:54','代码大赛','编程','http://localhost:8080/ssmhn4b5/upload/1618376367834.jpg','2021-04-15','程序员社','西科楼','2021-04-14 13:03:52','100','李四','是','准予参加',1618376563186);

/*Table structure for table `news` */

DROP TABLE IF EXISTS `news`;

CREATE TABLE `news` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `title` varchar(200) NOT NULL COMMENT '标题',
                        `introduction` longtext COMMENT '简介',
                        `picture` varchar(200) NOT NULL COMMENT '图片',
                        `content` longtext NOT NULL COMMENT '内容',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8 COMMENT='校园资讯';

/*Data for the table `news` */

insert  into `news`(`id`,`addtime`,`title`,`introduction`,`picture`,`content`) values (101,'2021-04-14 12:27:13','标题1','简介1','http://localhost:8080/ssmhn4b5/upload/news_picture1.jpg','内容1'),(102,'2021-04-14 12:27:13','标题2','简介2','http://localhost:8080/ssmhn4b5/upload/news_picture2.jpg','内容2'),(103,'2021-04-14 12:27:13','标题3','简介3','http://localhost:8080/ssmhn4b5/upload/news_picture3.jpg','内容3'),(104,'2021-04-14 12:27:13','标题4','简介4','http://localhost:8080/ssmhn4b5/upload/news_picture4.jpg','内容4'),(105,'2021-04-14 12:27:13','标题5','简介5','http://localhost:8080/ssmhn4b5/upload/news_picture5.jpg','内容5'),(106,'2021-04-14 12:27:13','标题6','简介6','http://localhost:8080/ssmhn4b5/upload/news_picture6.jpg','内容6');

/*Table structure for table `shetuanhuodong` */

DROP TABLE IF EXISTS `shetuanhuodong`;

CREATE TABLE `shetuanhuodong` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `huodongmingcheng` varchar(200) DEFAULT NULL COMMENT '活动名称',
                                  `huodongleixing` varchar(200) DEFAULT NULL COMMENT '活动类型',
                                  `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
                                  `jubanriqi` date DEFAULT NULL COMMENT '举办日期',
                                  `jubanshetuan` varchar(200) DEFAULT NULL COMMENT '举办社团',
                                  `huodongdidian` varchar(200) DEFAULT NULL COMMENT '活动地点',
                                  `huodongshizhang` int(11) DEFAULT NULL COMMENT '活动时长',
                                  `huodongneirong` longtext COMMENT '活动内容',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376387694 DEFAULT CHARSET=utf8 COMMENT='社团活动';

/*Data for the table `shetuanhuodong` */

insert  into `shetuanhuodong`(`id`,`addtime`,`huodongmingcheng`,`huodongleixing`,`zhaopian`,`jubanriqi`,`jubanshetuan`,`huodongdidian`,`huodongshizhang`,`huodongneirong`) values (41,'2021-04-14 12:27:13','活动名称1','活动类型1','http://localhost:8080/ssmhn4b5/upload/shetuanhuodong_zhaopian1.jpg','2021-04-14','举办社团1','活动地点1',1,'活动内容1'),(42,'2021-04-14 12:27:13','活动名称2','活动类型2','http://localhost:8080/ssmhn4b5/upload/shetuanhuodong_zhaopian2.jpg','2021-04-14','举办社团2','活动地点2',2,'活动内容2'),(43,'2021-04-14 12:27:13','活动名称3','活动类型3','http://localhost:8080/ssmhn4b5/upload/shetuanhuodong_zhaopian3.jpg','2021-04-14','举办社团3','活动地点3',3,'活动内容3'),(44,'2021-04-14 12:27:13','活动名称4','活动类型4','http://localhost:8080/ssmhn4b5/upload/shetuanhuodong_zhaopian4.jpg','2021-04-14','举办社团4','活动地点4',4,'活动内容4'),(45,'2021-04-14 12:27:13','活动名称5','活动类型5','http://localhost:8080/ssmhn4b5/upload/shetuanhuodong_zhaopian5.jpg','2021-04-14','举办社团5','活动地点5',5,'活动内容5'),(46,'2021-04-14 12:27:13','活动名称6','活动类型6','http://localhost:8080/ssmhn4b5/upload/shetuanhuodong_zhaopian6.jpg','2021-04-14','举办社团6','活动地点6',6,'活动内容6'),(1618376387693,'2021-04-14 12:59:47','代码大赛','编程','http://localhost:8080/ssmhn4b5/upload/1618376367834.jpg','2021-04-15','程序员社','西科楼',2,'<p>比赛打代码，头越秃越强</p>');

/*Table structure for table `shetuanshenqingxinxi` */

DROP TABLE IF EXISTS `shetuanshenqingxinxi`;

CREATE TABLE `shetuanshenqingxinxi` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `shetuanmingcheng` varchar(200) DEFAULT NULL COMMENT '社团名称',
                                        `shetuantupian` varchar(200) DEFAULT NULL COMMENT '社团图片',
                                        `shetuanleibie` varchar(200) DEFAULT NULL COMMENT '社团类别',
                                        `shenqingshijian` date DEFAULT NULL COMMENT '申请时间',
                                        `xuehao` varchar(200) DEFAULT NULL COMMENT '学号',
                                        `xingming` varchar(200) DEFAULT NULL COMMENT '姓名',
                                        `shenqingliyou` longtext COMMENT '申请理由',
                                        `sfsh` varchar(200) DEFAULT '否' COMMENT '是否审核',
                                        `shhf` longtext COMMENT '审核回复',
                                        `userid` bigint(20) DEFAULT NULL COMMENT '用户id',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376615540 DEFAULT CHARSET=utf8 COMMENT='社团申请信息';

/*Data for the table `shetuanshenqingxinxi` */

insert  into `shetuanshenqingxinxi`(`id`,`addtime`,`shetuanmingcheng`,`shetuantupian`,`shetuanleibie`,`shenqingshijian`,`xuehao`,`xingming`,`shenqingliyou`,`sfsh`,`shhf`,`userid`) values (21,'2021-04-14 12:27:13','社团名称1','http://localhost:8080/ssmhn4b5/upload/shetuanshenqingxinxi_shetuantupian1.jpg','社团类别1','2021-04-14','学号1','姓名1','申请理由1','是','',1),(22,'2021-04-14 12:27:13','社团名称2','http://localhost:8080/ssmhn4b5/upload/shetuanshenqingxinxi_shetuantupian2.jpg','社团类别2','2021-04-14','学号2','姓名2','申请理由2','是','',2),(23,'2021-04-14 12:27:13','社团名称3','http://localhost:8080/ssmhn4b5/upload/shetuanshenqingxinxi_shetuantupian3.jpg','社团类别3','2021-04-14','学号3','姓名3','申请理由3','是','',3),(24,'2021-04-14 12:27:13','社团名称4','http://localhost:8080/ssmhn4b5/upload/shetuanshenqingxinxi_shetuantupian4.jpg','社团类别4','2021-04-14','学号4','姓名4','申请理由4','是','',4),(25,'2021-04-14 12:27:13','社团名称5','http://localhost:8080/ssmhn4b5/upload/shetuanshenqingxinxi_shetuantupian5.jpg','社团类别5','2021-04-14','学号5','姓名5','申请理由5','是','',5),(26,'2021-04-14 12:27:13','社团名称6','http://localhost:8080/ssmhn4b5/upload/shetuanshenqingxinxi_shetuantupian6.jpg','社团类别6','2021-04-14','学号6','姓名6','申请理由6','是','666',6),(1618376615539,'2021-04-14 13:03:35','程序员社','http://localhost:8080/ssmhn4b5/upload/1618376333075.jpg','编程','2021-04-14','100','李四','<p>我想变秃也想变强</p>','是','好好好',1618376563186);

/*Table structure for table `storeup` */

DROP TABLE IF EXISTS `storeup`;

CREATE TABLE `storeup` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `userid` bigint(20) NOT NULL COMMENT '用户id',
                           `refid` bigint(20) DEFAULT NULL COMMENT '收藏id',
                           `tablename` varchar(200) DEFAULT NULL COMMENT '表名',
                           `name` varchar(200) NOT NULL COMMENT '收藏名称',
                           `picture` varchar(200) NOT NULL COMMENT '收藏图片',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收藏表';

/*Data for the table `storeup` */

/*Table structure for table `token` */

DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                         `userid` bigint(20) NOT NULL COMMENT '用户id',
                         `username` varchar(100) NOT NULL COMMENT '用户名',
                         `tablename` varchar(100) DEFAULT NULL COMMENT '表名',
                         `role` varchar(100) DEFAULT NULL COMMENT '角色',
                         `token` varchar(200) NOT NULL COMMENT '密码',
                         `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                         `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='token表';

/*Data for the table `token` */

insert  into `token`(`id`,`userid`,`username`,`tablename`,`role`,`token`,`addtime`,`expiratedtime`) values (1,1,'abo','users','管理员','a5gq36q50lfd1w9bfkr22yj5cenqdsdf','2021-04-14 12:57:38','2021-04-14 14:04:19'),(2,1618376476788,'100','xuesheng','学生','ntw0u0a0pal5st5m60rcf4cchanguvwa','2021-04-14 13:01:23','2021-04-14 14:02:55'),(3,1618376563186,'100','huiyuan','会员','md2p9zww3g6mau9ylwg07ca3ajh1aqxe','2021-04-14 13:03:04','2021-04-14 14:05:06');

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                         `username` varchar(100) NOT NULL COMMENT '用户名',
                         `password` varchar(100) NOT NULL COMMENT '密码',
                         `role` varchar(100) DEFAULT '管理员' COMMENT '角色',
                         `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`password`,`role`,`addtime`) values (1,'abo','abo','管理员','2021-04-14 12:27:13');

/*Table structure for table `xiaoyuanshetuan` */

DROP TABLE IF EXISTS `xiaoyuanshetuan`;

CREATE TABLE `xiaoyuanshetuan` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `shetuanmingcheng` varchar(200) DEFAULT NULL COMMENT '社团名称',
                                   `shetuantupian` varchar(200) DEFAULT NULL COMMENT '社团图片',
                                   `chenglishijian` date DEFAULT NULL COMMENT '成立时间',
                                   `shetuanleibie` varchar(200) DEFAULT NULL COMMENT '社团类别',
                                   `shetuanrenshu` int(11) DEFAULT NULL COMMENT '社团人数',
                                   `shetuanjieshao` longtext COMMENT '社团介绍',
                                   `clicktime` datetime DEFAULT NULL COMMENT '最近点击时间',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376351267 DEFAULT CHARSET=utf8 COMMENT='校园社团';

/*Data for the table `xiaoyuanshetuan` */

insert  into `xiaoyuanshetuan`(`id`,`addtime`,`shetuanmingcheng`,`shetuantupian`,`chenglishijian`,`shetuanleibie`,`shetuanrenshu`,`shetuanjieshao`,`clicktime`) values (31,'2021-04-14 12:27:13','社团名称1','http://localhost:8080/ssmhn4b5/upload/xiaoyuanshetuan_shetuantupian1.jpg','2021-04-14','社团类别1',1,'社团介绍1','2021-04-14 12:27:13'),(32,'2021-04-14 12:27:13','社团名称2','http://localhost:8080/ssmhn4b5/upload/xiaoyuanshetuan_shetuantupian2.jpg','2021-04-14','社团类别2',2,'社团介绍2','2021-04-14 13:03:14'),(33,'2021-04-14 12:27:13','社团名称3','http://localhost:8080/ssmhn4b5/upload/xiaoyuanshetuan_shetuantupian3.jpg','2021-04-14','社团类别3',3,'社团介绍3','2021-04-14 12:27:13'),(34,'2021-04-14 12:27:13','社团名称4','http://localhost:8080/ssmhn4b5/upload/xiaoyuanshetuan_shetuantupian4.jpg','2021-04-14','社团类别4',4,'社团介绍4','2021-04-14 12:27:13'),(35,'2021-04-14 12:27:13','社团名称5','http://localhost:8080/ssmhn4b5/upload/xiaoyuanshetuan_shetuantupian5.jpg','2021-04-14','社团类别5',5,'社团介绍5','2021-04-14 12:27:13'),(36,'2021-04-14 12:27:13','社团名称6','http://localhost:8080/ssmhn4b5/upload/xiaoyuanshetuan_shetuantupian6.jpg','2021-04-14','社团类别6',6,'社团介绍6','2021-04-14 13:01:43'),(1618376351266,'2021-04-14 12:59:10','程序员社','http://localhost:8080/ssmhn4b5/upload/1618376333075.jpg','2021-04-14','编程',10,'<p>打代码，程序猿</p>','2021-04-14 13:03:38');

/*Table structure for table `xuesheng` */

DROP TABLE IF EXISTS `xuesheng`;

CREATE TABLE `xuesheng` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `xuehao` varchar(200) NOT NULL COMMENT '学号',
                            `mima` varchar(200) NOT NULL COMMENT '密码',
                            `xingming` varchar(200) NOT NULL COMMENT '姓名',
                            `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
                            `nianling` int(11) DEFAULT NULL COMMENT '年龄',
                            `dianhua` varchar(200) DEFAULT NULL COMMENT '电话',
                            `youxiang` varchar(200) DEFAULT NULL COMMENT '邮箱',
                            `shenfenzheng` varchar(200) DEFAULT NULL COMMENT '身份证',
                            `zhaopian` varchar(200) DEFAULT NULL COMMENT '照片',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `xuehao` (`xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=1618376476789 DEFAULT CHARSET=utf8 COMMENT='学生';

/*Data for the table `xuesheng` */

insert  into `xuesheng`(`id`,`addtime`,`xuehao`,`mima`,`xingming`,`xingbie`,`nianling`,`dianhua`,`youxiang`,`shenfenzheng`,`zhaopian`) values (11,'2021-04-14 12:27:13','学生1','123456','姓名1','男',1,'13823888881','773890001@qq.com','440300199101010001','http://localhost:8080/ssmhn4b5/upload/xuesheng_zhaopian1.jpg'),(12,'2021-04-14 12:27:13','学生2','123456','姓名2','女',22,'13823888882','773890002@qq.com','440300199202020002','http://localhost:8080/ssmhn4b5/upload/xuesheng_zhaopian2.jpg'),(13,'2021-04-14 12:27:13','学生3','123456','姓名3','男',3,'13823888883','773890003@qq.com','440300199303030003','http://localhost:8080/ssmhn4b5/upload/xuesheng_zhaopian3.jpg'),(14,'2021-04-14 12:27:13','学生4','123456','姓名4','男',4,'13823888884','773890004@qq.com','440300199404040004','http://localhost:8080/ssmhn4b5/upload/xuesheng_zhaopian4.jpg'),(15,'2021-04-14 12:27:13','学生5','123456','姓名5','男',5,'13823888885','773890005@qq.com','440300199505050005','http://localhost:8080/ssmhn4b5/upload/xuesheng_zhaopian5.jpg'),(16,'2021-04-14 12:27:13','学生6','123456','姓名6','男',6,'13823888886','773890006@qq.com','440300199606060006','http://localhost:8080/ssmhn4b5/upload/xuesheng_zhaopian6.jpg'),(1618376292837,'2021-04-14 12:58:12','123','123','张三啊','男',33,'12312312312','123@qq.com','123123123123123123','http://localhost:8080/ssmhn4b5/upload/1618376292278.jpg'),(1618376476788,'2021-04-14 13:01:16','100','123456','李四','男',21,'12312312312','213@qq.com','123131123123123123','http://localhost:8080/ssmhn4b5/upload/1618376494403.jpg');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
