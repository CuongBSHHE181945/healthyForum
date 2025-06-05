-- Create the database
CREATE DATABASE IF NOT EXISTS healthyForum;
USE healthyForum;

-- Create Role table
CREATE TABLE IF NOT EXISTS `role` (
    `role_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(255) NOT NULL
);

-- Create User table
CREATE TABLE IF NOT EXISTS `user` (
    `userID` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `fullname` VARCHAR(255),
    `email` VARCHAR(255),
    `gender` VARCHAR(255),
    `dob` DATE,
    `address` VARCHAR(255),
    `suspended` BOOLEAN NOT NULL DEFAULT FALSE,
    `role_id` BIGINT,
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`),
    CONSTRAINT `unique_username` UNIQUE (`username`)
);

-- Create Sleep Entries table
CREATE TABLE IF NOT EXISTS `sleep_entries` (
    `sleep_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `quality` INT NOT NULL,
    `notes` VARCHAR(255),
    `userID` BIGINT NOT NULL,
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
);

-- Create Meal Planner table
CREATE TABLE IF NOT EXISTS `meal_planner` (
    `meal_id` INT PRIMARY KEY AUTO_INCREMENT,
    `meal_name` VARCHAR(255),
    `meal_date` DATE,
    `meal_type` VARCHAR(255),
    `meal_description` TEXT,
    `meal_calories` DECIMAL(10,2),
    `meal_proteins` DECIMAL(10,2),
    `meal_carbs` DECIMAL(10,2),
    `meal_fats` DECIMAL(10,2),
    `userID` BIGINT NOT NULL,
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
);

-- Create Meal Ingredient table
CREATE TABLE IF NOT EXISTS `meal_ingredient` (
    `ingredient_id` INT PRIMARY KEY AUTO_INCREMENT,
    `ingredient_name` VARCHAR(255) NOT NULL,
    `ingredient_quantity` INT NOT NULL,
    `ingredient_unit` VARCHAR(255),
    `meal_id` INT NOT NULL,
    FOREIGN KEY (`meal_id`) REFERENCES `meal_planner`(`meal_id`)
);

-- Create Feedback table
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `userID` BIGINT NOT NULL,
    `message` TEXT NOT NULL,
    `submitted_at` DATETIME NOT NULL,
    `response` TEXT,
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
);

-- Create Report table
CREATE TABLE IF NOT EXISTS `report` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,
    `userID` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL,
    `response` TEXT,
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
);

-- Create Blog table
CREATE TABLE IF NOT EXISTS `blog` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT,
    `created_at` DATETIME,
    `author_username` VARCHAR(255),
    `suspended` BOOLEAN DEFAULT FALSE,
    CONSTRAINT `fk_author_username` FOREIGN KEY (`author_username`) REFERENCES `user`(`username`)
);

-- Create Messages table
CREATE TABLE IF NOT EXISTS `messages` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sender_id` BIGINT NOT NULL,
    `receiver_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_read` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`sender_id`) REFERENCES `user`(`userID`),
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`userID`)
);

-- Insert initial roles
INSERT INTO `role` (`role_name`) VALUES 
('Admin'), 
('User');

-- Insert sample users
INSERT INTO `user` (`username`, `password`, `fullname`, `email`, `gender`, `dob`, `address`, `suspended`, `role_id`) VALUES
('sarah1', '$2a$10$Zzs1kf5WAviVMIvbUSvsVen.uHWUTjb8Qn.bnKEI21jcBqphZEtPO', 'Sarah Smith', 'sarah@example.com', 'Female', '1990-01-01', '123 Wellness St.', 0, 2),
('alice123', '$2a$10$DdACFbPlEnoLMoNRT58rRezj9Mnchy.CVolTbxrOZmo7m4AwSalra', 'Alice Johnson', 'alice@example.com', 'Female', '1995-06-12', '123 Main St', 0, 2),
('bob456', '$2a$10$6ODmXZxJge8dvXWKSpE5RuU66tMP7guAROmGoYW2D1ABxw//YJq5u', 'Bob Smith', 'bob@example.com', 'Male', '1990-03-08', '456 Park Ave', 0, 2),
('carol789', '$2a$10$4FjsyuABAig4nZTibDZXR.vMgFXmg55ubtdlre2efd17lqQdVqhwS', 'Carol Davis', 'carol@example.com', 'Female', '1988-11-22', '789 Elm Rd', 0, 2),
('admin1', '$2a$10$Qw8kZx3gqeeUXnIfwRS4l.9gHBvXwIBIsPNrKV86Xqx94H/46oQwe', 'Alice Admin', 'alice@admin.com', 1, '1990-01-01', '123 Admin St', 0, 1),
('mod1', '$2a$10$fWIBxuIsvVpJ3s0jSBG.1.bN5Jb3Dq0yWRySyZxzkXN01H.WMlCZO', 'Bob Mod', 'bob@mod.com', 1, '1992-02-02', '456 Mod Ave', 0, 2),
('user1', '$2a$10$9Y1AT//cDf.3AwoibVN6/ul8rGxUo.RAhGyElbLulcUblxN.O8mHK', 'Charlie User', 'charlie@user.com', 0, '1995-03-03', '789 User Rd', 0, 2),
('user2', '$2a$10$7bPNzIfN.fHo1Q7I0j5/OO8KyYNzgt5UQsIemZwFQPMWVt4SZTnNW', 'Dana User', 'dana@user.com', 0, '1998-04-04', '101 User Blvd', 1, 2);

-- Insert sample sleep entries
INSERT INTO `sleep_entries` (`date`, `start_time`, `end_time`, `quality`, `notes`, `userID`) VALUES
('2024-03-15', '22:00:00', '06:00:00', 8, 'Good night sleep', 1),
('2024-03-16', '23:00:00', '07:00:00', 7, 'Decent sleep', 1);

-- Insert sample meal plans
INSERT INTO `meal_planner` (`meal_name`, `meal_date`, `meal_type`, `meal_description`, `meal_calories`, `meal_proteins`, `meal_carbs`, `meal_fats`, `userID`) VALUES
('Healthy Breakfast', '2024-03-15', 'Breakfast', 'Nutritious morning meal', 500.00, 20.00, 60.00, 15.00, 1),
('Light Lunch', '2024-03-15', 'Lunch', 'Balanced lunch meal', 600.00, 25.00, 70.00, 20.00, 1);

-- Insert sample meal ingredients
INSERT INTO `meal_ingredient` (`ingredient_name`, `ingredient_quantity`, `ingredient_unit`, `meal_id`) VALUES
('Oatmeal', 100, 'grams', 1),
('Banana', 1, 'piece', 1),
('Chicken Breast', 150, 'grams', 2),
('Brown Rice', 100, 'grams', 2);

-- Insert sample blog posts
INSERT INTO `blog` (`title`, `content`, `created_at`, `author_username`, `suspended`) VALUES
('Healthy Eating Tips', 'Eat more vegetables and fruits every day.', NOW(), 'user1', FALSE),
('Workout Routines', 'Try HIIT for better results.', NOW(), 'user2', FALSE),
('Mental Health Matters', 'Meditation helps reduce stress.', NOW(), 'user1', TRUE);

-- Insert sample feedbacks
INSERT INTO `feedback` (`userID`, `message`, `submitted_at`, `response`) VALUES
(2, 'Great site, very helpful!', '2024-06-01 10:15:00', NULL),
(3, 'I found a bug in the forum.', '2024-06-02 14:30:00', NULL);

-- Insert sample reports
INSERT INTO `report` (`title`, `content`, `userID`, `created_at`, `response`) VALUES
('Spam Post', 'User xyz is posting spam links.', 1, '2024-06-03 09:00:00', NULL),
('Inappropriate Content', 'There is an offensive comment in thread 123.', 2, '2024-06-03 16:45:00', NULL);

-- Insert sample messages
INSERT INTO `messages` (`sender_Id`, `receiver_Id`, `content`, `is_read`) VALUES
(1, 2, 'Hi Bob, how are you?', FALSE),
(2, 1, 'Hi Alice, I am good. You?', TRUE),
(1, 3, 'Hey Charlie, long time no see!', FALSE),
(3, 1, 'Hi Alice! Yes, indeed.', TRUE),
(2, 3, 'Charlie, are you coming to the meeting?', FALSE),
(3, 2, 'Yes, I will be there!', TRUE);