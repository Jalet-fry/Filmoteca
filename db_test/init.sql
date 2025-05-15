-- create DATABASE lab2;
-- use lab2;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: lab2
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actor`
--

DROP TABLE IF EXISTS `actor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actor` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `second_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `uniq_actor_full_name` (`first_name`,`second_name`,`last_name`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actor`
--

LOCK TABLES `actor` WRITE;
/*!40000 ALTER TABLE `actor` DISABLE KEYS */;
INSERT INTO `actor` VALUES (24,'Ewan','Gordon','McGregor'),(25,'Michael','John','Douglas'),(21,'Robert','John','Downey '),(26,'Sylvia','Sidney',''),(38,'ывм','ывмыв','ымв'),(39,'ывм','ымв','ывм');
/*!40000 ALTER TABLE `actor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `director`
--

DROP TABLE IF EXISTS `director`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `director` (
  `Id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `second_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `uniq_director_full_name` (`first_name`,`second_name`,`last_name`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `director`
--

LOCK TABLES `director` WRITE;
/*!40000 ALTER TABLE `director` DISABLE KEYS */;
INSERT INTO `director` VALUES (12,'Cristopher','Advard','Nolan'),(15,'David','Andrew','Fincher'),(27,'sdv','svd','svd'),(14,'Timothy','Walter','Burton');
/*!40000 ALTER TABLE `director` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `film`
--

DROP TABLE IF EXISTS `film`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `film` (
  `id` int NOT NULL AUTO_INCREMENT,
  `director_id` int DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `year` int DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_film` (`title`,`year`,`director_id`),
  KEY `film_director_id_fk` (`director_id`),
  CONSTRAINT `film_director_id_fk` FOREIGN KEY (`director_id`) REFERENCES `director` (`Id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `film`
--

LOCK TABLES `film` WRITE;
/*!40000 ALTER TABLE `film` DISABLE KEYS */;
INSERT INTO `film` VALUES (29,14,'Pinocio',2022,'C:ilm'),(30,14,'Beetlejuice',1988,'C:ilm'),(33,15,'вслотвылсорвыосвыис',2020,NULL);
/*!40000 ALTER TABLE `film` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `film_actors`
--

DROP TABLE IF EXISTS `film_actors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `film_actors` (
  `films_id` int DEFAULT NULL,
  `actors_id` int DEFAULT NULL,
  KEY `fk_film_actors_actor` (`actors_id`),
  KEY `fk_film_actors_film` (`films_id`),
  CONSTRAINT `fk_film_actors_actor` FOREIGN KEY (`actors_id`) REFERENCES `actor` (`Id`) ON DELETE CASCADE,
  CONSTRAINT `fk_film_actors_film` FOREIGN KEY (`films_id`) REFERENCES `film` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `film_actors`
--

LOCK TABLES `film_actors` WRITE;
/*!40000 ALTER TABLE `film_actors` DISABLE KEYS */;
INSERT INTO `film_actors` VALUES (30,25),(30,26),(29,24),(29,25),(29,26),(29,38),(33,21),(33,24),(33,25),(33,26),(33,38),(33,39);
/*!40000 ALTER TABLE `film_actors` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-14 19:02:43
