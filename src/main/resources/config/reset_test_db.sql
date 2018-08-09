-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Aug 06, 2018 at 10:14 PM
-- Server version: 5.6.40
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `seng302-2018-team200-test`
--
CREATE DATABASE IF NOT EXISTS `seng302-2018-team200-test` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `seng302-2018-team200-test`;

-- --------------------------------------------------------

--
-- Table structure for table `affected_organs`
--

DROP TABLE IF EXISTS `affected_organs`;
CREATE TABLE IF NOT EXISTS `affected_organs` (
  `Id` int(11) NOT NULL,
  `ProcedureId` int(11) NOT NULL,
  `Organ` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `conditions`
--

DROP TABLE IF EXISTS `conditions`;
CREATE TABLE IF NOT EXISTS `conditions` (
  `Id` int(11) NOT NULL,
  `ProfileId` int(11) NOT NULL,
  `Description` varchar(100) DEFAULT NULL,
  `DiagnosisDate` datetime DEFAULT NULL,
  `Chronic` tinyint(1) DEFAULT NULL,
  `Current` tinyint(1) DEFAULT NULL,
  `Past` tinyint(1) DEFAULT NULL,
  `CuredDate` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `countries`
--

DROP TABLE IF EXISTS `countries`;
CREATE TABLE IF NOT EXISTS `countries` (
  `Id` int(11) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Valid` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `drugs`
--

DROP TABLE IF EXISTS `drugs`;
CREATE TABLE IF NOT EXISTS `drugs` (
  `Id` int(11) NOT NULL,
  `ProfileId` int(11) NOT NULL,
  `Drug` varchar(50) DEFAULT NULL,
  `Current` tinyint(1) DEFAULT NULL,
  `Past` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
CREATE TABLE IF NOT EXISTS `history` (
  `Id` int(11) NOT NULL,
  `EntityType` varchar(30) DEFAULT NULL,
  `EntityId` int(11) NOT NULL,
  `Action` varchar(50) DEFAULT NULL,
  `Data` varchar(100) DEFAULT NULL,
  `DataIndex` int(11) DEFAULT NULL,
  `Timestamp` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `medical_interactions`
--

DROP TABLE IF EXISTS `medical_interactions`;
CREATE TABLE IF NOT EXISTS `medical_interactions` (
  `Id` int(11) NOT NULL,
  `DrugA` varchar(50) DEFAULT NULL,
  `DrugB` varchar(50) DEFAULT NULL,
  `Symptom` varchar(50) DEFAULT NULL,
  `Duration` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `organs`
--

DROP TABLE IF EXISTS `organs`;
CREATE TABLE IF NOT EXISTS `organs` (
  `Id` int(11) NOT NULL,
  `ProfileId` int(11) NOT NULL,
  `Organ` varchar(30) DEFAULT NULL,
  `Donated` tinyint(1) DEFAULT NULL,
  `ToDonate` tinyint(1) DEFAULT NULL,
  `Required` tinyint(1) DEFAULT NULL,
  `Received` tinyint(1) DEFAULT NULL,
  `DateRegistered` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `procedures`
--

DROP TABLE IF EXISTS `procedures`;
CREATE TABLE IF NOT EXISTS `procedures` (
  `Id` int(11) NOT NULL,
  `ProfileId` int(11) NOT NULL,
  `Summary` varchar(100) DEFAULT NULL,
  `Description` varchar(200) NOT NULL,
  `ProcedureDate` datetime DEFAULT NULL,
  `Pending` tinyint(1) DEFAULT NULL,
  `Previous` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `profiles`
--

DROP TABLE IF EXISTS `profiles`;
CREATE TABLE IF NOT EXISTS `profiles` (
  `ProfileId` int(11) NOT NULL,
  `NHI` varchar(20) DEFAULT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `IsDonor` tinyint(1) DEFAULT '0',
  `IsReceiver` tinyint(1) DEFAULT '0',
  `GivenNames` varchar(50) DEFAULT NULL,
  `Lastnames` varchar(50) DEFAULT NULL,
  `Dob` datetime DEFAULT NULL,
  `Dod` datetime DEFAULT NULL,
  `Gender` varchar(30) DEFAULT NULL,
  `Height` double DEFAULT NULL,
  `Weight` double DEFAULT NULL,
  `BloodType` varchar(5) DEFAULT NULL,
  `IsSmoker` tinyint(1) DEFAULT '0',
  `AlcoholConsumption` varchar(50) DEFAULT NULL,
  `BloodPressureSystolic` int(11) DEFAULT NULL,
  `BloodPressureDiastolic` int(11) DEFAULT NULL,
  `Address` varchar(50) DEFAULT NULL,
  `StreetNo` varchar(10) DEFAULT NULL,
  `StreetName` varchar(50) DEFAULT NULL,
  `Neighbourhood` varchar(50) DEFAULT NULL,
  `City` varchar(50) DEFAULT NULL,
  `ZipCode` int(11) DEFAULT NULL,
  `Region` varchar(30) DEFAULT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `BirthCountry` varchar(50) DEFAULT NULL,
  `Phone` varchar(30) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Created` datetime DEFAULT CURRENT_TIMESTAMP,
  `LastUpdated` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `UserId` int(11) NOT NULL,
  `Username` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `UserType` varchar(30) DEFAULT NULL,
  `Address` varchar(50) DEFAULT NULL,
  `Region` varchar(30) DEFAULT NULL,
  `Created` datetime DEFAULT CURRENT_TIMESTAMP,
  `LastUpdated` datetime DEFAULT CURRENT_TIMESTAMP,
  `IsDefault` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `affected_organs`
--
ALTER TABLE `affected_organs`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `ProcedureId` (`ProcedureId`);

--
-- Indexes for table `conditions`
--
ALTER TABLE `conditions`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `ProfileId` (`ProfileId`);

--
-- Indexes for table `countries`
--
ALTER TABLE `countries`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `drugs`
--
ALTER TABLE `drugs`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `ProfileId` (`ProfileId`);

--
-- Indexes for table `history`
--
ALTER TABLE `history`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `EntityId` (`EntityId`);

--
-- Indexes for table `medical_interactions`
--
ALTER TABLE `medical_interactions`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `organs`
--
ALTER TABLE `organs`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `ProfileId` (`ProfileId`);

--
-- Indexes for table `procedures`
--
ALTER TABLE `procedures`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `ProfileId` (`ProfileId`);

--
-- Indexes for table `profiles`
--
ALTER TABLE `profiles`
  ADD PRIMARY KEY (`ProfileId`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`UserId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `affected_organs`
--
ALTER TABLE `affected_organs`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `conditions`
--
ALTER TABLE `conditions`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `countries`
--
ALTER TABLE `countries`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `drugs`
--
ALTER TABLE `drugs`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `history`
--
ALTER TABLE `history`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `medical_interactions`
--
ALTER TABLE `medical_interactions`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `organs`
--
ALTER TABLE `organs`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `procedures`
--
ALTER TABLE `procedures`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `profiles`
--
ALTER TABLE `profiles`
  MODIFY `ProfileId` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `UserId` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `affected_organs`
--
ALTER TABLE `affected_organs`
  ADD CONSTRAINT `affected_organs_ibfk_1` FOREIGN KEY (`ProcedureId`) REFERENCES `procedures` (`Id`);

--
-- Constraints for table `conditions`
--
ALTER TABLE `conditions`
  ADD CONSTRAINT `conditions_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);

--
-- Constraints for table `drugs`
--
ALTER TABLE `drugs`
  ADD CONSTRAINT `drugs_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);

--
-- Constraints for table `history`
--
ALTER TABLE `history`
  ADD CONSTRAINT `history_ibfk_1` FOREIGN KEY (`EntityId`) REFERENCES `profiles` (`ProfileId`),
  ADD CONSTRAINT `history_ibfk_2` FOREIGN KEY (`EntityId`) REFERENCES `users` (`UserId`);

--
-- Constraints for table `organs`
--
ALTER TABLE `organs`
  ADD CONSTRAINT `organs_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);

--
-- Constraints for table `procedures`
--
ALTER TABLE `procedures`
  ADD CONSTRAINT `procedures_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

INSERT INTO `countries` (`Id`, `Name`, `Valid`) VALUES
  (13, 'NZ', 1),
  (14, 'AF', 1),
  (15, 'AX', 1),
  (16, 'AL', 1),
  (17, 'DZ', 1),
  (18, 'AS', 1),
  (19, 'AD', 1),
  (20, 'AO', 1),
  (21, 'AI', 1),
  (22, 'AQ', 1),
  (23, 'AG', 1),
  (24, 'AR', 1),
  (25, 'AM', 1),
  (26, 'AW', 1),
  (27, 'AU', 1),
  (28, 'AT', 1),
  (29, 'AZ', 1),
  (30, 'BS', 1),
  (31, 'BH', 1),
  (32, 'BD', 1),
  (33, 'BB', 1),
  (34, 'BY', 1),
  (35, 'BE', 1),
  (36, 'BZ', 1),
  (37, 'BJ', 1),
  (38, 'BM', 1),
  (39, 'BT', 1),
  (40, 'BO', 1),
  (41, 'BA', 1),
  (42, 'BW', 1),
  (43, 'BV', 1),
  (44, 'BR', 1),
  (45, 'IO', 1),
  (46, 'BN', 1),
  (47, 'BG', 1),
  (48, 'BF', 1),
  (49, 'BI', 1),
  (50, 'KH', 1),
  (51, 'CM', 1),
  (52, 'CA', 1),
  (53, 'CV', 1),
  (54, 'KY', 1),
  (55, 'CF', 1),
  (56, 'TD', 1),
  (57, 'CL', 1),
  (58, 'CN', 1),
  (59, 'CX', 1),
  (60, 'CC', 1),
  (61, 'CO', 1),
  (62, 'KM', 1),
  (63, 'CG', 1),
  (64, 'CD', 1),
  (65, 'CK', 1),
  (66, 'CR', 1),
  (67, 'CI', 1),
  (68, 'HR', 1),
  (69, 'CU', 1),
  (70, 'CY', 1),
  (71, 'CZ', 1),
  (72, 'DK', 1),
  (73, 'DJ', 1),
  (74, 'DM', 1),
  (75, 'DO', 1),
  (76, 'EC', 1),
  (77, 'EG', 1),
  (78, 'SV', 1),
  (79, 'GQ', 1),
  (80, 'ER', 1),
  (81, 'EE', 1),
  (82, 'ET', 1),
  (83, 'FK', 1),
  (84, 'FO', 1),
  (85, 'FJ', 1),
  (86, 'FI', 1),
  (87, 'FR', 1),
  (88, 'GF', 1),
  (89, 'PF', 1),
  (90, 'TF', 1),
  (91, 'GA', 1),
  (92, 'GM', 1),
  (93, 'GE', 1),
  (94, 'DE', 1),
  (95, 'GH', 1),
  (96, 'GI', 1),
  (97, 'GR', 1),
  (98, 'GL', 1),
  (99, 'GD', 1),
  (100, 'GP', 1),
  (101, 'GU', 1),
  (102, 'GT', 1),
  (103, 'GG', 1),
  (104, 'GN', 1),
  (105, 'GW', 1),
  (106, 'GY', 1),
  (107, 'HT', 1),
  (108, 'HM', 1),
  (109, 'VA', 1),
  (110, 'HN', 1),
  (111, 'HK', 1),
  (112, 'HU', 1),
  (113, 'IS', 1),
  (114, 'IN', 1),
  (115, 'ID', 1),
  (116, 'IR', 1),
  (117, 'IQ', 1),
  (118, 'IE', 1),
  (119, 'IM', 1),
  (120, 'IL', 1),
  (121, 'IT', 1),
  (122, 'JM', 1),
  (123, 'JP', 1),
  (124, 'JE', 1),
  (125, 'JO', 1),
  (126, 'KZ', 1),
  (127, 'KE', 1),
  (128, 'KI', 1),
  (129, 'KP', 1),
  (130, 'KR', 1),
  (131, 'KW', 1),
  (132, 'KG', 1),
  (133, 'LA', 1),
  (134, 'LV', 1),
  (135, 'LB', 1),
  (136, 'LS', 1),
  (137, 'LR', 1),
  (138, 'LY', 1),
  (139, 'LI', 1),
  (140, 'LT', 1),
  (141, 'LU', 1),
  (142, 'MO', 1),
  (143, 'MK', 1),
  (144, 'MG', 1),
  (145, 'MW', 1),
  (146, 'MY', 1),
  (147, 'MV', 1),
  (148, 'ML', 1),
  (149, 'MT', 1),
  (150, 'MH', 1),
  (151, 'MQ', 1),
  (152, 'MR', 1),
  (153, 'MU', 1),
  (154, 'YT', 1),
  (155, 'MX', 1),
  (156, 'FM', 1),
  (157, 'MD', 1),
  (158, 'MC', 1),
  (159, 'MN', 1),
  (160, 'MS', 1),
  (161, 'MA', 1),
  (162, 'MZ', 1),
  (163, 'MM', 1),
  (164, 'NA', 1),
  (165, 'NR', 1),
  (166, 'NP', 1),
  (167, 'NL', 1),
  (168, 'AN', 1),
  (169, 'NC', 1),
  (170, 'NI', 1),
  (171, 'NE', 1),
  (172, 'NG', 1),
  (173, 'NU', 1),
  (174, 'NF', 1),
  (175, 'MP', 1),
  (176, 'NO', 1),
  (177, 'OM', 1),
  (178, 'PK', 1),
  (179, 'PW', 1),
  (180, 'PS', 1),
  (181, 'PA', 1),
  (182, 'PG', 1),
  (183, 'PY', 1),
  (184, 'PE', 1),
  (185, 'PH', 1),
  (186, 'PN', 1),
  (187, 'PL', 1),
  (188, 'PT', 1),
  (189, 'PR', 1),
  (190, 'QA', 1),
  (191, 'RE', 1),
  (192, 'RO', 1),
  (193, 'RU', 1),
  (194, 'RW', 1),
  (195, 'SH', 1),
  (196, 'KN', 1),
  (197, 'LC', 1),
  (198, 'PM', 1),
  (199, 'VC', 1),
  (200, 'WS', 1),
  (201, 'SM', 1),
  (202, 'ST', 1),
  (203, 'SA', 1),
  (204, 'SN', 1),
  (205, 'CS', 1),
  (206, 'SC', 1),
  (207, 'SL', 1),
  (208, 'SG', 1),
  (209, 'SK', 1),
  (210, 'SI', 1),
  (211, 'SB', 1),
  (212, 'SO', 1),
  (213, 'ZA', 1),
  (214, 'GS', 1),
  (215, 'ES', 1),
  (216, 'LK', 1),
  (217, 'SD', 1),
  (218, 'SR', 1),
  (219, 'SJ', 1),
  (220, 'SZ', 1),
  (221, 'SE', 1),
  (222, 'CH', 1),
  (223, 'SY', 1),
  (224, 'TW', 1),
  (225, 'TJ', 1),
  (226, 'TZ', 1),
  (227, 'TH', 1),
  (228, 'TL', 1),
  (229, 'TG', 1),
  (230, 'TK', 1),
  (231, 'TO', 1),
  (232, 'TT', 1),
  (233, 'TN', 1),
  (234, 'TR', 1),
  (235, 'TM', 1),
  (236, 'TC', 1),
  (237, 'TV', 1),
  (238, 'UG', 1),
  (239, 'UA', 1),
  (240, 'AE', 1),
  (241, 'GB', 1),
  (242, 'US', 1),
  (243, 'UM', 1),
  (244, 'UY', 1),
  (245, 'UZ', 1),
  (246, 'VU', 1),
  (247, 'VE', 1),
  (248, 'VN', 1),
  (249, 'VG', 1),
  (250, 'VI', 1),
  (251, 'WF', 1),
  (252, 'EH', 1),
  (253, 'YE', 1),
  (254, 'ZM', 1),
  (255, 'ZW', 1);