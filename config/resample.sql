-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Jul 23, 2018 at 03:28 AM
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

--
-- Dumping data for table `procedures`
--

INSERT INTO `procedures` (`Id`, `ProfileId`, `Summary`, `ProcedureDate`, `Pending`, `Previous`) VALUES
  (1, 0, NULL, NULL, NULL, NULL),
  (4, 4, 'calf muscel implants', '2019-03-05 00:00:00', NULL, NULL);

--
-- Dumping data for table `profiles`
--

-- phpMyAdmin SQL Dump
-- version 4.4.15.5
-- http://www.phpmyadmin.net
--
-- Host: csse-mysql2
-- Generation Time: Jul 24, 2018 at 09:06 AM
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

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UserId`, `Username`, `Password`, `Name`, `UserType`, `Address`, `Region`, `Created`, `LastUpdated`, `IsDefault`) VALUES
  (1, 'admin', 'admin', 'admin', 'ADMIN', NULL, 'Canterbury', '2018-07-24 20:54:28', '2018-07-24 20:54:28', 1),
  (3, 'brooker', '123', 'Brooke Rakowitz', 'CLINICIAN', NULL, 'Otago', '2018-07-24 20:56:45', '2018-07-24 20:56:45', 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

INSERT INTO `profiles` (`ProfileId`, `NHI`, `Username`, `IsDonor`, `IsReceiver`, `GivenNames`, `Lastnames`, `Dob`, `Dod`, `Gender`, `Height`, `Weight`, `BloodType`, `IsSmoker`, `AloholConsumption`, `BloodPressureSystolic`, `BloodPressureDiastolic`, `Address`, `Region`, `Phone`, `Email`, `Created`, `LastUpdated`) VALUES
  (0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
  (2, '123445678', 'sp333d', 1, 1, 'Joshua', 'Wyllie', '1997-07-18 00:00:00', NULL, 'male', 183, 78, 'O', 0, '12', 140, 80, '163 waimairi road', 'canterbury', '0221379514', 'jwy31@uclive.com', NULL, NULL),
  (3, '123445678', 'brroooook', 1, 1, 'Brooke', 'Rakowitz', '1998-01-12 00:00:00', NULL, 'female', 168, 60, 'O', 0, '12', 140, 80, '163 imaginary palce', 'canterbury', '1324321432', 'bra30@uclive.com', NULL, NULL),
  (4, '123442348', 'JackONZ', 1, 1, 'Jack', 'Hay', '1997-08-12 00:00:00', NULL, 'female', 179, 1000, 'O', 0, '15', 140, 80, '1324 imaginary palce', 'canterbury', '13243324323', 'jha23@uclive.com', NULL, NULL),
  (5, '134652348', 'AlexM', 1, 1, 'Alex', 'Miller', '1989-08-18 00:00:00', NULL, 'male', 190, 82, 'O', 0, '15', 140, 80, 'somewhere in japan', 'nipon', '13243332423', 'ami31@uclive.com', NULL, NULL),
  (6, '134652348', 'MattyB', 1, 1, 'Matt', 'King', '1997-11-18 00:00:00', NULL, 'male', 180, 77, 'O', 0, '15', 140, 80, '3 noname road', 'canterbury', '1132832423', 'mki43@uclive.com', NULL, NULL),
  (7, '134652348', 'UgandanKnuckles', 1, 1, 'Lachlan', 'Brewster', '1997-04-17 00:00:00', NULL, 'male', 179, 200, 'O', 0, 'all', 190, 110, '334 noname road', 'canterbury', '1133242213', 'lbr@uclive.com', NULL, NULL),
  (8, '321432143', 'Xx_zac_xX', 1, 1, 'Zac', 'Brazendale', '1998-02-17 00:00:00', NULL, 'male', 179, 200, 'O', 0, 'all', 178, 79, '32 noname road', 'canterbury', '2343242213', 'zbr@uclive.com', NULL, NULL),
  (9, '2147483647', 'mlg_LewisWhite', 1, 1, 'Lewis', 'White', '1997-08-23 00:00:00', NULL, 'male', 179, 35, 'O', 0, 'all', 130, 50, '3243 noname road', 'canterbury', '2332442213', 'lwh63@uclive.com', NULL, NULL);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;