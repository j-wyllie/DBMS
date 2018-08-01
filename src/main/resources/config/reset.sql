-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Aug 01, 2018 at 01:59 AM
-- Server version: 5.6.40
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `seng302-2018-team200-prod`
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
  `Received` tinyint(1) DEFAULT NULL
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