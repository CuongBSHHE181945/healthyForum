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
    `password` VARCHAR(255) NULL,
    `fullname` VARCHAR(255),
    `email` VARCHAR(255),
    `gender` VARCHAR(255),
    `dob` DATE,
    `address` VARCHAR(255),
    `provider` VARCHAR(255) NOT NULL,
    `verification_code` VARCHAR(64),
    `enabled` BOOLEAN NOT NULL DEFAULT FALSE,
    `suspended` BOOLEAN NOT NULL DEFAULT FALSE,
    `reset_password_token` VARCHAR(64),
    `reset_token_expiry` DATETIME,
    `role_id` BIGINT,
    `google_id` VARCHAR(255) UNIQUE,
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`),
    CONSTRAINT `unique_username` UNIQUE (`username`)
);

-- Create Post table (add created_at, updated_at, banned, visibility)
CREATE TABLE IF NOT EXISTS `post` (
    `post_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,
    `is_draft` BOOLEAN NOT NULL DEFAULT FALSE,
    `visibility` VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `banned` BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`userID`)
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
    `report_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `reporter_id` BIGINT NOT NULL,
    `reported_post_id` BIGINT,
    `reported_user_id` BIGINT,
    `reason` TEXT NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `resolution` TEXT,
    FOREIGN KEY (`reporter_id`) REFERENCES `user`(`userID`),
    FOREIGN KEY (`reported_post_id`) REFERENCES `post`(`post_id`),
    FOREIGN KEY (`reported_user_id`) REFERENCES `user`(`userID`)
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

-- Table: badge
CREATE TABLE badge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(255) NOT NULL,
    locked_icon VARCHAR(255), -- optional if you apply grayscale in frontend
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table: badge_source_type (e.g., CHALLENGE, GOAL, ACHIEVEMENT, etc.)
CREATE TABLE badge_source_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- ENUM-like: CHALLENGE, GOAL, SYSTEM_EVENT, etc.
);

-- Table: badge_requirement (connects badge to a source type and specific source)
CREATE TABLE badge_requirement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    badge_id INT NOT NULL,
    source_type_id INT NOT NULL,
    source_id INT NOT NULL, -- ID of the challenge/goal/etc
    value VARCHAR(255),     -- optional (e.g., target steps, score)

    FOREIGN KEY (badge_id) REFERENCES badge(id),
    FOREIGN KEY (source_type_id) REFERENCES badge_source_type(id)
);

-- Table: user_badge (user owns a badge)
CREATE TABLE user_badge (
    userID BIGINT NOT NULL,
    badge_id INT NOT NULL,
    earned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    displayed BIT NOT NULL DEFAULT 0,

    PRIMARY KEY (userID, badge_id),
    FOREIGN KEY (badge_id) REFERENCES badge(id),
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
);

CREATE TABLE challenge_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- e.g., PERSONAL, PUBLIC
);

CREATE TABLE challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type_id INT NOT NULL,
    duration_days INT NOT NULL,

    FOREIGN KEY (type_id) REFERENCES challenge_type(id)
);

CREATE TABLE user_challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userID BIGINT NOT NULL,
    challenge_id INT NOT NULL,
    join_date DATE,
    status VARCHAR(20),
    last_check_date DATE,

    FOREIGN KEY (userID) REFERENCES user(userID),
    FOREIGN KEY (challenge_id) REFERENCES challenge(id)
);

-- Insert initial roles
INSERT INTO `role` (`role_name`) VALUES 
('ADMIN'), 
('USER');

-- Insert sample users
INSERT INTO `user` (`username`, `password`, `fullname`, `email`, `gender`, `dob`, `address`, `provider`, `enabled`, `suspended`, `role_id`) VALUES
('sarah1', '$2a$10$Zzs1kf5WAviVMIvbUSvsVen.uHWUTjb8Qn.bnKEI21jcBqphZEtPO', 'Sarah Smith', 'sarah@example.com', 'Female', '1990-01-01', '123 Wellness St.', 'local', TRUE, 0, 2),
('alice123', '$2a$10$DdACFbPlEnoLMoNRT58rRezj9Mnchy.CVolTbxrOZmo7m4AwSalra', 'Alice Johnson', 'alice@example.com', 'Female', '1995-06-12', '123 Main St', 'local', TRUE, 0, 2),
('bob456', '$2a$10$6ODmXZxJge8dvXWKSpE5RuU66tMP7guAROmGoYW2D1ABxw//YJq5u', 'Bob Smith', 'bob@example.com', 'Male', '1990-03-08', '456 Park Ave', 'local', TRUE, 0, 2),
('carol789', '$2a$10$4FjsyuABAig4nZTibDZXR.vMgFXmg55ubtdlre2efd17lqQdVqhwS', 'Carol Davis', 'carol@example.com', 'Female', '1988-11-22', '789 Elm Rd', 'local', TRUE, 0, 2),
('admin1', '$2a$10$Qw8kZx3gqeeUXnIfwRS4l.9gHBvXwIBIsPNrKV86Xqx94H/46oQwe', 'Alice Admin', 'alice@admin.com', 'Female', '1990-01-01', '123 Admin St', 'local', TRUE, 0, 1),
('mod1', '$2a$10$fWIBxuIsvVpJ3s0jSBG.1.bN5Jb3Dq0yWRySyZxzkXN01H.WMlCZO', 'Bob Mod', 'bob@mod.com', 'Male', '1992-02-02', '456 Mod Ave', 'local', TRUE, 0, 2),
('user1', '$2a$10$9Y1AT//cDf.3AwoibVN6/ul8rGxUo.RAhGyElbLulcUblxN.O8mHK', 'Charlie User', 'charlie@user.com', 'Male', '1995-03-03', '789 User Rd', 'local', TRUE, 0, 2),
('user2', '$2a$10$7bPNzIfN.fHo1Q7I0j5/OO8KyYNzgt5UQsIemZwFQPMWVt4SZTnNW', 'Dana User', 'dana@user.com', 'Female', '1998-04-04', '101 User Blvd', 'local', TRUE, 1, 2);

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

-- Insert sample messages
INSERT INTO `messages` (`sender_Id`, `receiver_Id`, `content`, `is_read`) VALUES
(1, 2, 'Hi Bob, how are you?', FALSE),
(2, 1, 'Hi Alice, I am good. You?', TRUE),
(1, 3, 'Hey Charlie, long time no see!', FALSE),
(3, 1, 'Hi Alice! Yes, indeed.', TRUE),
(2, 3, 'Charlie, are you coming to the meeting?', FALSE),
(3, 2, 'Yes, I will be there!', TRUE);

-- Generated SQL for 30 Challenges and Badges
-- Challenge: Morning Yoga 7 Days
-- Insert badge source type

-- Insert challenge types
INSERT INTO challenge_type (id, name) VALUES
(1, 'PERSONAL'),
(2, 'PUBLIC');

INSERT INTO badge_source_type (name) VALUES ('CHALLENGE');

INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (1, 'Morning Yoga 7 Days', 'Complete the morning yoga 7 days challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (1, 'Yoga Starter', 'Awarded for completing the "Morning Yoga 7 Days" challenge.', '/images/badges/unlocked/yoga.png', '/images/badges/locked/yoga_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (1, 1, 1);

-- Challenge: 10 Pushups Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (2, '10 Pushups Daily', 'Complete the 10 pushups daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (2, 'Push Master', 'Awarded for completing the "10 Pushups Daily" challenge.', '/images/badges/unlocked/pushups.png', '/images/badges/locked/pushups_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (2, 2, 1);

-- Challenge: Walk 5,000 Steps
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (3, 'Walk 5,000 Steps', 'Complete the walk 5,000 steps challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (3, 'Step Beginner', 'Awarded for completing the "Walk 5,000 Steps" challenge.', '/images/badges/unlocked/steps.png', '/images/badges/locked/steps_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (3, 3, 1);

-- Challenge: Jump Rope 50x
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (4, 'Jump Rope 50x', 'Complete the jump rope 50x challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (4, 'Jumper', 'Awarded for completing the "Jump Rope 50x" challenge.', '/images/badges/unlocked/jump.png', '/images/badges/locked/jump_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (4, 4, 1);

-- Challenge: Stretch Every Morning
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (5, 'Stretch Every Morning', 'Complete the stretch every morning challenge.', 1, 14);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (5, 'Flexible Flow', 'Awarded for completing the "Stretch Every Morning" challenge.', '/images/badges/unlocked/stretch.png', '/images/badges/locked/stretch_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (5, 5, 1);

-- Challenge: 5-Min Home Workout
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (6, '5-Min Home Workout', 'Complete the 5-min home workout challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (6, 'Quick Fit', 'Awarded for completing the "5-Min Home Workout" challenge.', '/images/badges/unlocked/homeworkout.png', '/images/badges/locked/homeworkout_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (6, 6, 1);

-- Challenge: Daily Gratitude Journal
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (7, 'Daily Gratitude Journal', 'Complete the daily gratitude journal challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (7, 'Grateful Heart', 'Awarded for completing the "Daily Gratitude Journal" challenge.', '/images/badges/unlocked/gratitude.png', '/images/badges/locked/gratitude_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (7, 7, 1);

-- Challenge: 5-min Meditation
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (8, '5-min Meditation', 'Complete the 5-min meditation challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (8, 'Mindful Soul', 'Awarded for completing the "5-min Meditation" challenge.', '/images/badges/unlocked/meditation.png', '/images/badges/locked/meditation_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (8, 8, 1);

-- Challenge: No Phone After 10PM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (9, 'No Phone After 10PM', 'Complete the no phone after 10pm challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (9, 'Digital Detoxer', 'Awarded for completing the "No Phone After 10PM" challenge.', '/images/badges/unlocked/nophone.png', '/images/badges/locked/nophone_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (9, 9, 1);

-- Challenge: Deep Breathing Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (10, 'Deep Breathing Daily', 'Complete the deep breathing daily challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (10, 'Calm Spirit', 'Awarded for completing the "Deep Breathing Daily" challenge.', '/images/badges/unlocked/breathing.png', '/images/badges/locked/breathing_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (10, 10, 1);

-- Challenge: Cold Showers Challenge
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (11, 'Cold Showers Challenge', 'Complete the cold showers challenge challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (11, 'Chill Champ', 'Awarded for completing the "Cold Showers Challenge" challenge.', '/images/badges/unlocked/coldshower.png', '/images/badges/locked/coldshower_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (11, 11, 1);

-- Challenge: Screen-Free Hour
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (12, 'Screen-Free Hour', 'Complete the screen-free hour challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (12, 'Focus Freak', 'Awarded for completing the "Screen-Free Hour" challenge.', '/images/badges/unlocked/focus.png', '/images/badges/locked/focus_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (12, 12, 1);

-- Challenge: No Sugar Week
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (13, 'No Sugar Week', 'Complete the no sugar week challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (13, 'Sugar-Free', 'Awarded for completing the "No Sugar Week" challenge.', '/images/badges/unlocked/nosugar.png', '/images/badges/locked/nosugar_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (13, 13, 1);

-- Challenge: Fruit Before Noon
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (14, 'Fruit Before Noon', 'Complete the fruit before noon challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (14, 'Fruit First', 'Awarded for completing the "Fruit Before Noon" challenge.', '/images/badges/unlocked/fruit.png', '/images/badges/locked/fruit_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (14, 14, 1);

-- Challenge: Water Tracker
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (15, 'Water Tracker', 'Complete the water tracker challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (15, 'Hydration Hero', 'Awarded for completing the "Water Tracker" challenge.', '/images/badges/unlocked/water.png', '/images/badges/locked/water_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (15, 15, 1);

-- Challenge: Eat Salad Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (16, 'Eat Salad Daily', 'Complete the eat salad daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (16, 'Green Eater', 'Awarded for completing the "Eat Salad Daily" challenge.', '/images/badges/unlocked/salad.png', '/images/badges/locked/salad_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (16, 16, 1);

-- Challenge: 8 Glasses of Water
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (17, '8 Glasses of Water', 'Complete the 8 glasses of water challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (17, 'Aqua Master', 'Awarded for completing the "8 Glasses of Water" challenge.', '/images/badges/unlocked/glasses.png', '/images/badges/locked/glasses_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (17, 17, 1);

-- Challenge: Cook Healthy Meal
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (18, 'Cook Healthy Meal', 'Complete the cook healthy meal challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (18, 'Kitchen King', 'Awarded for completing the "Cook Healthy Meal" challenge.', '/images/badges/unlocked/meal.png', '/images/badges/locked/meal_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (18, 18, 1);

-- Challenge: Sleep Before 11PM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (19, 'Sleep Before 11PM', 'Complete the sleep before 11pm challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (19, 'Early Bird', 'Awarded for completing the "Sleep Before 11PM" challenge.', '/images/badges/unlocked/sleep.png', '/images/badges/locked/sleep_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (19, 19, 1);

-- Challenge: 8H Sleep Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (20, '8H Sleep Daily', 'Complete the 8h sleep daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (20, 'Sleep Champion', 'Awarded for completing the "8H Sleep Daily" challenge.', '/images/badges/unlocked/sleeptime.png', '/images/badges/locked/sleeptime_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (20, 20, 1);

-- Challenge: Wake Up Before 7AM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (21, 'Wake Up Before 7AM', 'Complete the wake up before 7am challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (21, 'Rise & Shine', 'Awarded for completing the "Wake Up Before 7AM" challenge.', '/images/badges/unlocked/sunrise.png', '/images/badges/locked/sunrise_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (21, 21, 1);

-- Challenge: No Caffeine After 6PM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (22, 'No Caffeine After 6PM', 'Complete the no caffeine after 6pm challenge.', 1, 1);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (22, 'Sleep Saver', 'Awarded for completing the "No Caffeine After 6PM" challenge.', '/images/badges/unlocked/nocaffeine.png', '/images/badges/locked/nocaffeine_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (22, 22, 1);

-- Challenge: No Screen in Bed
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (23, 'No Screen in Bed', 'Complete the no screen in bed challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (23, 'Eye Rested', 'Awarded for completing the "No Screen in Bed" challenge.', '/images/badges/unlocked/noscreen.png', '/images/badges/locked/noscreen_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (23, 23, 1);

-- Challenge: Power Nap Master
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (24, 'Power Nap Master', 'Complete the power nap master challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (24, 'Nap Ninja', 'Awarded for completing the "Power Nap Master" challenge.', '/images/badges/unlocked/nap.png', '/images/badges/locked/nap_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (24, 24, 1);

-- Challenge: Read 15 Min Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (25, 'Read 15 Min Daily', 'Complete the read 15 min daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (25, 'Bookworm', 'Awarded for completing the "Read 15 Min Daily" challenge.', '/images/badges/unlocked/read.png', '/images/badges/locked/read_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (25, 25, 1);

-- Challenge: Practice a Hobby
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (26, 'Practice a Hobby', 'Complete the practice a hobby challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (26, 'Hobbyist', 'Awarded for completing the "Practice a Hobby" challenge.', '/images/badges/unlocked/hobby.png', '/images/badges/locked/hobby_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (26, 26, 1);

-- Challenge: Learn a New Word
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (27, 'Learn a New Word', 'Complete the learn a new word challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (27, 'Word Wizard', 'Awarded for completing the "Learn a New Word" challenge.', '/images/badges/unlocked/vocab.png', '/images/badges/locked/vocab_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (27, 27, 1);

-- Challenge: Write a Daily Journal
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (28, 'Write a Daily Journal', 'Complete the write a daily journal challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (28, 'Thoughtful Writer', 'Awarded for completing the "Write a Daily Journal" challenge.', '/images/badges/unlocked/journal.png', '/images/badges/locked/journal_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (28, 28, 1);

-- Challenge: Practice Gratitude
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (29, 'Practice Gratitude', 'Complete the practice gratitude challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (29, 'Thanks Giver', 'Awarded for completing the "Practice Gratitude" challenge.', '/images/badges/unlocked/thanks.png', '/images/badges/locked/thanks_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (29, 29, 1);

-- Challenge: Clean Room Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (30, 'Clean Room Daily', 'Complete the clean room daily challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (30, 'Tidy Star', 'Awarded for completing the "Clean Room Daily" challenge.', '/images/badges/unlocked/cleanroom.png', '/images/badges/locked/cleanroom_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (30, 30, 1);

-- Badge System Minimal Test Data
-- only CHALLENGE source type
-- Insert user_badge assignments
INSERT INTO user_badge (userID, badge_id, earned_at) VALUES
(1, 1, '2024-06-01 10:00:00'),
(1, 2, '2024-06-08 10:00:00'),
(2, 1, '2024-06-02 11:00:00'),
(2, 3, '2024-06-09 12:00:00'),
(3, 4, '2024-06-15 09:00:00');

