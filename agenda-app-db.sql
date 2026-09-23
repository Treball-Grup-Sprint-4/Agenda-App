-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema agenda-app-database
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `agenda-app-database` ;

-- -----------------------------------------------------
-- Schema agenda-app-database
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `agenda-app-database` DEFAULT CHARACTER SET utf8 ;
USE `agenda-app-database` ;

-- -----------------------------------------------------
-- Table `agenda-app-database`.`event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `agenda-app-database`.`event` (
  `id` INT NOT NULL,
  `name` VARCHAR(256) NOT NULL,
  `date` DATE NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `recurrence` ENUM('NONE', 'ANUAL', 'MONTHLY', 'WEEKLY') NOT NULL,
  `repeat_until` DATE NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `event_id_UNIQUE` (`id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `agenda-app-database`.`task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `agenda-app-database`.`task` (
  `id` INT NOT NULL,
  `name` VARCHAR(256) NOT NULL,
  `priority` ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL,
  `status` ENUM('PENDING', 'COMPLETED') NOT NULL,
  `expiration_date` DATE NOT NULL,
  `created_at` TIMESTAMP NOT NULL,
  `completed_at` TIMESTAMP NULL,
  `event_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_task_event_idx` (`event_id` ASC) VISIBLE,
  CONSTRAINT `fk_task_event`
    FOREIGN KEY (`event_id`)
    REFERENCES `agenda-app-database`.`event` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `agenda-app-database`.`note`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `agenda-app-database`.`note` (
  `id` INT NOT NULL,
  `content` VARCHAR(800) NULL,
  `created_at` TIMESTAMP NULL,
  `task_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_note_task1_idx` (`task_id` ASC) VISIBLE,
  CONSTRAINT `fk_note_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `agenda-app-database`.`task` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
