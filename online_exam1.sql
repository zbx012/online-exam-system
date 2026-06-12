CREATE DATABASE  IF NOT EXISTS `online_exam` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `online_exam`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: online_exam
-- ------------------------------------------------------
-- Server version	5.7.44-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `choice_questions`
--

DROP TABLE IF EXISTS `choice_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `choice_questions` (
  `question_id` int(11) NOT NULL,
  `optionA` text NOT NULL,
  `optionB` text NOT NULL,
  `optionC` text NOT NULL,
  `optionD` text NOT NULL,
  `answer` char(1) NOT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `choice_questions_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `choice_questions`
--

LOCK TABLES `choice_questions` WRITE;
/*!40000 ALTER TABLE `choice_questions` DISABLE KEYS */;
INSERT INTO `choice_questions` VALUES (1,'2','3','1','6','A'),(2,'2','3','4','6','A'),(6,'1','2','3','6','D'),(9,'10','20','30','40','B'),(10,'10','11','12','12','A'),(12,'11','10','13','12','A');
/*!40000 ALTER TABLE `choice_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_question`
--

DROP TABLE IF EXISTS `exam_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_question` (
  `question_id` int(11) NOT NULL,
  `exam_id` int(11) NOT NULL,
  `order_num` int(11) NOT NULL,
  PRIMARY KEY (`question_id`,`exam_id`),
  UNIQUE KEY `uk_exam_order` (`exam_id`,`order_num`),
  CONSTRAINT `exam_question_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `exam_question_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_question`
--

LOCK TABLES `exam_question` WRITE;
/*!40000 ALTER TABLE `exam_question` DISABLE KEYS */;
INSERT INTO `exam_question` VALUES (1,40,1),(3,40,3),(1,46,1),(2,46,2),(4,46,4),(5,46,5),(6,46,6),(7,46,7),(8,46,8),(3,46,10),(1,49,1),(2,49,2),(3,49,3),(4,49,4),(5,49,5),(6,49,6),(7,49,7),(8,49,8),(9,55,1),(8,55,2),(7,55,3),(12,59,1),(13,59,2),(11,59,3);
/*!40000 ALTER TABLE `exam_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_student`
--

DROP TABLE IF EXISTS `exam_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_student` (
  `student_id` int(11) NOT NULL,
  `exam_id` int(11) NOT NULL,
  `score` int(11) DEFAULT NULL,
  PRIMARY KEY (`student_id`,`exam_id`),
  KEY `exam_id` (`exam_id`),
  CONSTRAINT `exam_student_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `exam_student_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_student`
--

LOCK TABLES `exam_student` WRITE;
/*!40000 ALTER TABLE `exam_student` DISABLE KEYS */;
INSERT INTO `exam_student` VALUES (10006,41,22),(10006,46,30),(10006,55,1),(10006,59,3),(10016,41,NULL),(10016,43,NULL),(10016,46,NULL);
/*!40000 ALTER TABLE `exam_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exams`
--

DROP TABLE IF EXISTS `exams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exams` (
  `exam_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` enum('draft','published','closed') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`exam_id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exams`
--

LOCK TABLES `exams` WRITE;
/*!40000 ALTER TABLE `exams` DISABLE KEYS */;
INSERT INTO `exams` VALUES (40,'java应用开发',111111,'2025-12-07 02:50:00','2025-12-09 03:50:00','draft'),(41,'java2',111,'2025-12-09 17:53:00','2025-12-09 15:55:00','published'),(42,'java',222,NULL,NULL,'draft'),(43,'java21',2,'2025-12-07 18:10:00','2025-12-07 19:49:00','published'),(44,'java112',0,'2025-12-21 06:26:00','2025-12-21 06:31:00','published'),(45,'1234',11121213,NULL,NULL,'draft'),(46,'小学数学1',10005,'2025-12-13 05:41:00','2026-01-02 05:39:00','draft'),(49,'小学数学2',10005,'2025-12-03 10:10:00','2025-12-30 12:10:00','published'),(55,'小学数学5',10005,'2025-12-22 21:36:00','2025-12-23 00:36:00','published'),(58,'小学数学6',10005,'2025-12-22 21:52:00','2025-12-22 23:52:00','published'),(59,'小学数学7',10005,'2025-12-22 21:59:00','2025-12-22 23:59:00','published');
/*!40000 ALTER TABLE `exams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fill_questions`
--

DROP TABLE IF EXISTS `fill_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fill_questions` (
  `question_id` int(11) NOT NULL,
  `answer` varchar(20) NOT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `fill_questions_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fill_questions`
--

LOCK TABLES `fill_questions` WRITE;
/*!40000 ALTER TABLE `fill_questions` DISABLE KEYS */;
INSERT INTO `fill_questions` VALUES (3,'2'),(4,'3'),(5,'1'),(7,'4'),(8,'4'),(11,'25'),(13,'7');
/*!40000 ALTER TABLE `fill_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `question_id` int(11) NOT NULL AUTO_INCREMENT,
  `question_content` text NOT NULL,
  `score` int(11) NOT NULL,
  `type` enum('CQ','FB','EQ') NOT NULL,
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'1+1=?',2,'CQ'),(2,'1+1=?',2,'CQ'),(3,'1+1=__',5,'FB'),(4,'1+2=__',2,'FB'),(5,'2-1',1,'FB'),(6,'2+4',3,'CQ'),(7,'9-5=__',1,'FB'),(8,'2+2=__',1,'FB'),(9,'10+10=',3,'CQ'),(10,'20-10',4,'CQ'),(11,'20+5',3,'FB'),(12,'2+9',2,'CQ'),(13,'3+4=__',3,'FB');
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_num` varchar(10) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `student_name` varchar(100) NOT NULL,
  PRIMARY KEY (`student_num`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `teacher_num` varchar(10) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `teacher_name` varchar(100) NOT NULL,
  PRIMARY KEY (`teacher_num`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `teachers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `user_type` enum('student','teacher','admin') NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_unique` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10020 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (10004,'admin','e10adc3949ba59abbe56e057f20f883e','114@qq.com','admin'),(10005,'test01','e10adc3949ba59abbe56e057f20f883e','test01@qq.com','teacher'),(10006,'test02','e10adc3949ba59abbe56e057f20f883e','newemail@qq.co','student'),(10007,'test03','e10adc3949ba59abbe56e057f20f883e','test03@qq.com','student'),(10008,'test10','e10adc3949ba59abbe56e057f20f883e',NULL,'teacher'),(10009,'zhangsan2','96e79218965eb72c92a549dd5a330112','zhangsan@qq.com','student'),(10010,'test12','e10adc3949ba59abbe56e057f20f883e','zhangsan@qq.com','student'),(10011,'test13','e10adc3949ba59abbe56e057f20f883e','zhangsan@qq.com','student'),(10012,'test14','e10adc3949ba59abbe56e057f20f883e','zhangsan@qq.com','student'),(10013,'test15','e10adc3949ba59abbe56e057f20f883e','zhangsan@qq.com','teacher'),(10014,'test16','e10adc3949ba59abbe56e057f20f883e','zhangsan@qq.com','teacher'),(10015,'test17','e10adc3949ba59abbe56e057f20f883e','zhangsan@qq.com','teacher'),(10016,'test100','821f3157e1a3456bfe1a000a1adf0862','12345@qq.com','student'),(10019,'test18','25f9e794323b453885f5181f1b624d0b','email@qq.com','teacher');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-31 10:41:32
