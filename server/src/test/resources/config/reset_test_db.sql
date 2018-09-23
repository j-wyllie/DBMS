-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Sep 23, 2018 at 09:43 AM
-- Server version: 5.6.40
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `seng302-2018-team200-test`
--

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
-- Table structure for table `hla_type`
--

DROP TABLE IF EXISTS `hla_type`;
CREATE TABLE IF NOT EXISTS `hla_type` (
  `groupX` text NOT NULL,
  `groupY` text NOT NULL,
  `secondary` mediumtext NOT NULL,
  `profileId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `hospitals`
--

DROP TABLE IF EXISTS `hospitals`;
CREATE TABLE IF NOT EXISTS `hospitals` (
  `Id` int(11) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `Address` varchar(100) DEFAULT NULL,
  `Latitude` double DEFAULT NULL,
  `Longitude` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `locale`
--

DROP TABLE IF EXISTS `locale`;
CREATE TABLE IF NOT EXISTS `locale` (
  `LocaleId` int(11) NOT NULL,
  `UserId` int(11) DEFAULT NULL,
  `ProfileId` int(11) DEFAULT NULL,
  `DateTimeFormat` varchar(50) DEFAULT NULL,
  `NumberFormat` varchar(50) DEFAULT NULL
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
  `Expired` tinyint(1) DEFAULT NULL,
  `UserId` int(11) DEFAULT NULL,
  `ExpiryDate` datetime DEFAULT NULL,
  `Note` varchar(200) DEFAULT NULL,
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
  `NHI` varchar(20) UNIQUE DEFAULT NULL,
  `Username` varchar(50) UNIQUE DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
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
  `CountryOfDeath` varchar(30) DEFAULT NULL,
  `CityOfDeath` varchar(30) DEFAULT NULL,
  `RegionOfDeath` varchar(30) DEFAULT NULL,
  `Phone` varchar(30) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Created` datetime DEFAULT CURRENT_TIMESTAMP,
  `LastUpdated` datetime DEFAULT CURRENT_TIMESTAMP,
  `PreferredName` varchar(50) DEFAULT NULL,
  `PreferredGender` varchar(30) DEFAULT NULL,
  `ImageName` varchar(50) DEFAULT NULL,
  `LastBloodDonation` datetime DEFAULT CURRENT_TIMESTAMP,
  `BloodDonationPoints` int(11) DEFAULT NULL,
  `Token` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `UserId` int(11) NOT NULL,
  `Username` varchar(50) UNIQUE DEFAULT NULL,
  `Password` varchar(100) DEFAULT NULL,
  `Name` varchar(100) DEFAULT NULL,
  `UserType` varchar(30) DEFAULT NULL,
  `Address` varchar(50) DEFAULT NULL,
  `Region` varchar(30) DEFAULT NULL,
  `Country` varchar(50) DEFAULT NULL,
  `Created` datetime DEFAULT CURRENT_TIMESTAMP,
  `LastUpdated` datetime DEFAULT CURRENT_TIMESTAMP,
  `IsDefault` tinyint(1) DEFAULT '0',
  `ImageName` varchar(50) DEFAULT NULL,
  `Token` int(11) DEFAULT NULL
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
-- Indexes for table `hla_type`
--
ALTER TABLE `hla_type`
  ADD PRIMARY KEY (`profileId`);

--
-- Indexes for table `hospitals`
--
ALTER TABLE `hospitals`
  ADD PRIMARY KEY (`Id`),
  ADD UNIQUE KEY `Id` (`Id`);

--
-- Indexes for table `medical_interactions`
--
ALTER TABLE `locale`
  ADD PRIMARY KEY (`LocaleId`);

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
-- AUTO_INCREMENT for table `hospitals`
--
ALTER TABLE `hospitals`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `locale`
--
ALTER TABLE `locale`
  MODIFY `LocaleId` int(11) NOT NULL AUTO_INCREMENT;
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
-- Constraints for table `hla_type`
--
ALTER TABLE `hla_type`
  ADD CONSTRAINT `hla_type_profile` FOREIGN KEY (`profileId`) REFERENCES `profiles` (`ProfileId`) ON DELETE CASCADE;

--
-- Constraints for table `locale`
--
ALTER TABLE `locale`
  ADD CONSTRAINT `locale_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);
ALTER TABLE `locale`
  ADD CONSTRAINT `locale_ibfk_2` FOREIGN KEY (`UserId`) REFERENCES `users` (`UserId`);

--
-- Constraints for table `organs`
--
ALTER TABLE `organs`
  ADD CONSTRAINT `organs_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);
ALTER TABLE `organs`
  ADD CONSTRAINT `organs_ibfk_2` FOREIGN KEY (`UserId`) REFERENCES `users` (`UserId`);

--
-- Constraints for table `procedures`
--
ALTER TABLE `procedures`
  ADD CONSTRAINT `procedures_ibfk_1` FOREIGN KEY (`ProfileId`) REFERENCES `profiles` (`ProfileId`);


DELETE FROM `users` WHERE Username IN ('Username', 'Pleb');

INSERT INTO `users` (`Username`, `Name`, `UserType`, `Address`, `Region`) VALUES
  ('Username', 'Tim Hamblin', 'ADMIN', '69 Yeetville', 'Yeetus'),
  ('Pleb', 'Brooke rasdasdk', 'ADMIN', '68 Yeetville', 'Yeetskeet');


INSERT INTO `countries` (`Id`, `Name`, `Valid`) VALUES
  (1, 'NZ', 1),
  (2, 'AF', 1),
  (3, 'AL', 1),
  (4, 'DZ', 1),
  (5, 'AD', 1),
  (6, 'AO', 1),
  (7, 'AG', 1),
  (8, 'AR', 1),
  (9, 'AM', 1),
  (10, 'AW', 1),
  (11, 'AU', 1),
  (12, 'AT', 1),
  (13, 'AZ', 1),
  (14, 'BS', 1),
  (15, 'BH', 1),
  (16, 'BD', 1),
  (17, 'BB', 1),
  (18, 'BY', 1),
  (19, 'BE', 1),
  (20, 'BZ', 1),
  (21, 'BJ', 1),
  (22, 'BM', 1),
  (23, 'BT', 1),
  (24, 'BO', 1),
  (25, 'BA', 1),
  (26, 'BW', 1),
  (27, 'BR', 1),
  (28, 'BN', 1),
  (29, 'BG', 1),
  (30, 'BF', 1),
  (31, 'BI', 1),
  (32, 'KH', 1),
  (33, 'CM', 1),
  (34, 'CA', 1),
  (35, 'CV', 1),
  (36, 'CF', 1),
  (37, 'TD', 1),
  (38, 'CL', 1),
  (39, 'CN', 1),
  (40, 'CO', 1),
  (41, 'KM', 1),
  (42, 'CG', 1),
  (43, 'CD', 1),
  (44, 'CK', 1),
  (45, 'CR', 1),
  (46, 'CI', 1),
  (47, 'HR', 1),
  (48, 'CU', 1),
  (49, 'CY', 1),
  (50, 'CZ', 1),
  (51, 'DK', 1),
  (52, 'DJ', 1),
  (53, 'DM', 1),
  (54, 'DO', 1),
  (55, 'EC', 1),
  (56, 'EG', 1),
  (57, 'SV', 1),
  (58, 'GQ', 1),
  (59, 'ER', 1),
  (60, 'EE', 1),
  (61, 'ET', 1),
  (62, 'FJ', 1),
  (63, 'FI', 1),
  (64, 'FR', 1),
  (65, 'GA', 1),
  (66, 'GM', 1),
  (67, 'GE', 1),
  (68, 'DE', 1),
  (69, 'GH', 1),
  (70, 'GR', 1),
  (71, 'GD', 1),
  (72, 'GT', 1),
  (73, 'GN', 1),
  (74, 'GW', 1),
  (75, 'GY', 1),
  (76, 'HT', 1),
  (77, 'VA', 1),
  (78, 'HN', 1),
  (79, 'HU', 1),
  (80, 'IS', 1),
  (81, 'IN', 1),
  (82, 'ID', 1),
  (83, 'IR', 1),
  (84, 'IQ', 1),
  (85, 'IE', 1),
  (86, 'IL', 1),
  (87, 'IT', 1),
  (88, 'JM', 1),
  (89, 'JP', 1),
  (90, 'JO', 1),
  (91, 'KZ', 1),
  (92, 'KE', 1),
  (93, 'KI', 1),
  (94, 'KP', 1),
  (95, 'KR', 1),
  (96, 'KW', 1),
  (97, 'KG', 1),
  (98, 'LA', 1),
  (99, 'LV', 1),
  (100, 'LB', 1),
  (101, 'LS', 1),
  (102, 'LR', 1),
  (103, 'LY', 1),
  (104, 'LI', 1),
  (105, 'LT', 1),
  (106, 'LU', 1),
  (107, 'MK', 1),
  (108, 'MG', 1),
  (109, 'MW', 1),
  (110, 'MY', 1),
  (111, 'MV', 1),
  (112, 'ML', 1),
  (113, 'MT', 1),
  (114, 'MH', 1),
  (115, 'MQ', 1),
  (116, 'MR', 1),
  (117, 'MU', 1),
  (118, 'MX', 1),
  (119, 'FM', 1),
  (120, 'MD', 1),
  (121, 'MC', 1),
  (122, 'MN', 1),
  (123, 'ME', 1),
  (124, 'MA', 1),
  (125, 'MZ', 1),
  (126, 'MM', 1),
  (127, 'NA', 1),
  (128, 'NR', 1),
  (129, 'NP', 1),
  (130, 'NL', 1),
  (131, 'NI', 1),
  (132, 'NE', 1),
  (133, 'NG', 1),
  (134, 'NU', 1),
  (135, 'NO', 1),
  (136, 'OM', 1),
  (137, 'PK', 1),
  (138, 'PW', 1),
  (139, 'PS', 1),
  (140, 'PA', 1),
  (141, 'PG', 1),
  (142, 'PY', 1),
  (143, 'PE', 1),
  (144, 'PH', 1),
  (145, 'PL', 1),
  (146, 'PT', 1),
  (147, 'QA', 1),
  (148, 'RO', 1),
  (149, 'RU', 1),
  (150, 'RW', 1),
  (151, 'KN', 1),
  (152, 'LC', 1),
  (153, 'VC', 1),
  (154, 'WS', 1),
  (155, 'SM', 1),
  (156, 'ST', 1),
  (157, 'SA', 1),
  (158, 'SN', 1),
  (159, 'CS', 1),
  (160, 'SC', 1),
  (161, 'SL', 1),
  (162, 'SG', 1),
  (163, 'SK', 1),
  (164, 'SI', 1),
  (165, 'SB', 1),
  (166, 'SO', 1),
  (167, 'ZA', 1),
  (168, 'SS', 1),
  (169, 'ES', 1),
  (170, 'LK', 1),
  (171, 'SD', 1),
  (172, 'SR', 1),
  (173, 'SZ', 1),
  (174, 'SE', 1),
  (175, 'CH', 1),
  (176, 'SY', 1),
  (177, 'TW', 1),
  (178, 'TJ', 1),
  (179, 'TZ', 1),
  (180, 'TH', 1),
  (181, 'TL', 1),
  (182, 'TG', 1),
  (183, 'TO', 1),
  (184, 'TT', 1),
  (185, 'TN', 1),
  (186, 'TR', 1),
  (187, 'TM', 1),
  (188, 'TV', 1),
  (189, 'UG', 1),
  (190, 'UA', 1),
  (191, 'AE', 1),
  (192, 'GB', 1),
  (193, 'US', 1),
  (194, 'UY', 1),
  (195, 'UZ', 1),
  (196, 'VU', 1),
  (197, 'VE', 1),
  (198, 'VN', 1),
  (199, 'YE', 1),
  (200, 'ZM', 1),
  (201, 'ZW', 1);

