-- Create the database
CREATE DATABASE IF NOT EXISTS healthyForum;
USE healthyForum;

-- Create Role table
CREATE TABLE IF NOT EXISTS `role` (
    `role_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(255) NOT NULL
);

-- Create User table (updated with health fields)
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `gender` VARCHAR(255),
    `dob` DATE,
    `address` VARCHAR(255),
    `age` INT,
    `height` DOUBLE,
    `weight` DOUBLE,
    `role_id` BIGINT NOT NULL,
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`)
);

-- Create User Account table (separated from User)
CREATE TABLE IF NOT EXISTS `user_accounts` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL UNIQUE,
    `username` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255),
    `provider` VARCHAR(255) NOT NULL DEFAULT 'local',
    `google_id` VARCHAR(255) UNIQUE,
    `enabled` BOOLEAN NOT NULL DEFAULT FALSE,
    `suspended` BOOLEAN NOT NULL DEFAULT FALSE,
    `verification_code` VARCHAR(64),
    `reset_password_token` VARCHAR(64),
    `reset_token_expiry` DATETIME,
    `last_login` DATETIME,
    CONSTRAINT `fk_user_account_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
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
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- Create Sleep Entries table
CREATE TABLE IF NOT EXISTS `sleep_entries` (
    `sleep_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `date` DATE NOT NULL,
    `start_time` TIME NOT NULL,
    `end_time` TIME NOT NULL,
    `quality` INT NOT NULL,
    `notes` VARCHAR(255),
    `user_id` BIGINT NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
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
    `user_id` BIGINT NOT NULL,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
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

-- Create Exercise Schedule table
CREATE TABLE IF NOT EXISTS `exercise_schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `date` DATE NOT NULL,
    `time` TIME NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `duration` INT NOT NULL,
    `intensity` VARCHAR(50) NOT NULL,
    `calories` INT,
    `is_calories_estimated` BOOLEAN DEFAULT TRUE,
    `notes` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

-- Create Feedback table
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `message` TEXT NOT NULL,
    `submitted_at` DATETIME NOT NULL,
    `response` TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
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
    FOREIGN KEY (`reporter_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`reported_post_id`) REFERENCES `post`(`post_id`),
    FOREIGN KEY (`reported_user_id`) REFERENCES `user`(`id`)
);

-- Create Blog table
CREATE TABLE IF NOT EXISTS `blog` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT,
    `created_at` DATETIME,
    `author_username` VARCHAR(255),
    `suspended` BOOLEAN DEFAULT FALSE,
    CONSTRAINT `fk_author_username` FOREIGN KEY (`author_username`) REFERENCES `user_accounts`(`username`)
);

-- Create Messages table
CREATE TABLE IF NOT EXISTS `messages` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sender_id` BIGINT NOT NULL,
    `receiver_id` BIGINT NOT NULL,
    `content` TEXT NOT NULL,
    `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_read` BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (`sender_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`)
);

-- Create Health Assessment table
CREATE TABLE IF NOT EXISTS `health_assessment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `bmi` DOUBLE,
    `sleep_score` DOUBLE,
    `nutrition_score` DOUBLE,
    `exercise_score` DOUBLE,
    `overall_score` DOUBLE,
    `risk_level` VARCHAR(255),
    `recommendations` TEXT,
    `user_id` BIGINT NOT NULL,
    `assessment_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE keyword (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(255) NOT NULL UNIQUE
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
    user_id BIGINT NOT NULL,
    badge_id INT NOT NULL,
    earned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    displayed BIT NOT NULL DEFAULT 0,

    PRIMARY KEY (user_id, badge_id),
    FOREIGN KEY (badge_id) REFERENCES badge(id),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);

CREATE TABLE challenge_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL -- e.g., PERSONAL, PUBLIC
);

CREATE TABLE challenge_category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status BIT DEFAULT 1,
    type_id INT NOT NULL,
    category_id INT NOT NULL,
    duration_days INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES challenge_type(id),
    FOREIGN KEY (category_id) REFERENCES challenge_category(id)
);

-- Add creator_id to challenge and set default
ALTER TABLE challenge
ADD COLUMN creator_id BIGINT,
ADD CONSTRAINT fk_challenge_creator FOREIGN KEY (creator_id) REFERENCES user(id);



-- Create user_challenge table (must come before evidence_post)
CREATE TABLE user_challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    challenge_id INT NOT NULL,
    join_date DATE,
    status VARCHAR(20),
    last_check_date DATE,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (challenge_id) REFERENCES challenge(id)
);

-- Create evidence_post table (references user_challenge)
CREATE TABLE IF NOT EXISTS evidence_post (
    evidence_post_id INT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_challenge_id INT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    vote_based BOOLEAN DEFAULT FALSE,
    vote_timeout DATETIME,
    fallback_to_admin BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_evidence_post_post FOREIGN KEY (post_id) REFERENCES post(post_id),
    CONSTRAINT fk_evidence_post_user_challenge FOREIGN KEY (user_challenge_id) REFERENCES user_challenge(id)
);

-- Create evidence_react table (references evidence_post)
CREATE TABLE IF NOT EXISTS evidence_react (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    evidence_post_id INT NOT NULL,
    reaction_type ENUM('LIKE', 'DISLIKE') NOT NULL,
    UNIQUE KEY user_post_reaction (user_id, evidence_post_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (evidence_post_id) REFERENCES evidence_post(evidence_post_id)
);

-- Create Post Reaction table
CREATE TABLE IF NOT EXISTS post_reaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    liked BOOLEAN NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_post (user_id, post_id)
);

-- Create Comment table
CREATE TABLE IF NOT EXISTS comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP ,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP UPDATE ON CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Create groups table
CREATE TABLE `groups` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    member_count INT DEFAULT 0,
    FOREIGN KEY (created_by) REFERENCES user(id) ON DELETE CASCADE
);

-- Create group_members table for many-to-many relationship
CREATE TABLE group_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role ENUM('ADMIN', 'MODERATOR', 'MEMBER') DEFAULT 'MEMBER',
    FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY unique_group_member (group_id, user_id)
);

-- Tạo bảng group_posts
CREATE TABLE group_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    group_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    image_url VARCHAR(500),
    
    CONSTRAINT fk_group_posts_group_id 
        FOREIGN KEY (group_id) REFERENCES `groups`(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_group_posts_author_id 
        FOREIGN KEY (author_id) REFERENCES user(id) 
        ON DELETE CASCADE,
        
    INDEX idx_group_posts_group_id (group_id),
    INDEX idx_group_posts_author_id (author_id),
    INDEX idx_group_posts_created_at (created_at)
);

-- Tạo bảng group_post_comments
CREATE TABLE group_post_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_group_post_comments_post_id 
        FOREIGN KEY (post_id) REFERENCES group_posts(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_group_post_comments_author_id 
        FOREIGN KEY (author_id) REFERENCES user(id) 
        ON DELETE CASCADE,
        
    INDEX idx_group_post_comments_post_id (post_id),
    INDEX idx_group_post_comments_author_id (author_id),
    INDEX idx_group_post_comments_created_at (created_at)
);

-- Insert initial roles
INSERT INTO `role` (`role_id`, `role_name`) VALUES 
(1, 'ADMIN'), 
(2, 'USER');

-- Insert sample users with explicit IDs
INSERT INTO `user` (`id`, `full_name`, `email`, `gender`, `dob`, `address`, `age`, `height`, `weight`, `role_id`) VALUES
(1, 'Sarah Smith', 'sarah@example.com', 'Female', '1990-01-01', '123 Wellness St.', 34, 165.0, 60.0, 2),
(2, 'Alice Johnson', 'alice@example.com', 'Female', '1995-06-12', '123 Main St', 29, 160.0, 55.0, 2),
(3, 'Bob Smith', 'bob@example.com', 'Male', '1990-03-08', '456 Park Ave', 34, 175.0, 70.0, 2),
(4, 'Carol Davis', 'carol@example.com', 'Female', '1988-11-22', '789 Elm Rd', 35, 170.0, 65.0, 2),
(5, 'Alice Admin', 'alice@admin.com', 'Female', '1990-01-01', '123 Admin St', 34, 165.0, 60.0, 1),
(6, 'Bob Mod', 'bob@mod.com', 'Male', '1992-02-02', '456 Mod Ave', 32, 180.0, 75.0, 2),
(7, 'Charlie User', 'charlie@user.com', 'Male', '1995-03-03', '789 User Rd', 29, 170.0, 68.0, 2),
(8, 'Dana User', 'dana@user.com', 'Female', '1998-04-04', '101 User Blvd', 26, 155.0, 50.0, 2);

-- Insert sample user accounts with explicit IDs
INSERT INTO `user_accounts` (`id`, `user_id`, `username`, `password`, `provider`, `enabled`, `suspended`) VALUES
(1, 1, 'sarah1', '$2a$10$Zzs1kf5WAviVMIvbUSvsVen.uHWUTjb8Qn.bnKEI21jcBqphZEtPO', 'local', TRUE, FALSE),
(2, 2, 'alice123', '$2a$10$DdACFbPlEnoLMoNRT58rRezj9Mnchy.CVolTbxrOZmo7m4AwSalra', 'local', TRUE, FALSE),
(3, 3, 'bob456', '$2a$10$6ODmXZxJge8dvXWKSpE5RuU66tMP7guAROmGoYW2D1ABxw//YJq5u', 'local', TRUE, FALSE),
(4, 4, 'carol789', '$2a$10$4FjsyuABAig4nZTibDZXR.vMgFXmg55ubtdlre2efd17lqQdVqhwS', 'local', TRUE, FALSE),
(5, 5, 'admin1', '$2a$10$Qw8kZx3gqeeUXnIfwRS4l.9gHBvXwIBIsPNrKV86Xqx94H/46oQwe', 'local', TRUE, FALSE),
(6, 6, 'mod1', '$2a$10$fWIBxuIsvVpJ3s0jSBG.1.bN5Jb3Dq0yWRySyZxzkXN01H.WMlCZO', 'local', TRUE, FALSE),
(7, 7, 'user1', '$2a$10$9Y1AT//cDf.3AwoibVN6/ul8rGxUo.RAhGyElbLulcUblxN.O8mHK', 'local', TRUE, FALSE),
(8, 8, 'user2', '$2a$10$7bPNzIfN.fHo1Q7I0j5/OO8KyYNzgt5UQsIemZwFQPMWVt4SZTnNW', 'local', TRUE, TRUE);

-- Sample posts for feed testing
INSERT INTO post (user_id, title, content, is_draft, visibility, created_at, updated_at, banned)
VALUES
(1, 'Morning Yoga Success', 'Just finished my 7-day yoga challenge!', FALSE, 'PUBLIC', '2024-06-01 08:00:00', '2024-06-01 08:00:00', FALSE),
(2, 'Healthy Breakfast', 'Oatmeal and fruit to start the day.', FALSE, 'PUBLIC', '2024-06-02 07:30:00', '2024-06-02 07:30:00', FALSE),
(3, 'Late Night Thoughts', 'Couldn''t sleep, so I wrote a poem.', FALSE, 'PUBLIC', '2024-06-02 23:45:00', '2024-06-02 23:45:00', FALSE),
(4, 'Draft: My Fitness Plan', 'Still working on my new fitness plan.', TRUE, 'DRAFTS', '2024-06-03 10:00:00', '2024-06-03 10:00:00', FALSE),
(5, 'Water Tracker', 'Drank 8 glasses today!', FALSE, 'PUBLIC', '2024-06-03 18:00:00', '2024-06-03 18:00:00', FALSE),
(6, 'Evening Walk', 'Beautiful sunset during my walk.', FALSE, 'PUBLIC', '2024-06-04 19:30:00', '2024-06-04 19:30:00', FALSE);

-- Sample post reactions (likes/dislikes)
INSERT INTO post_reaction (post_id, user_id, liked)
VALUES
(1, 2, TRUE),
(1, 3, TRUE),
(2, 1, TRUE),
(2, 3, FALSE),
(3, 1, TRUE),
(3, 2, FALSE),
(5, 1, TRUE),
(5, 2, TRUE),
(5, 3, TRUE),
(6, 1, FALSE);

-- Sample comments
INSERT INTO comment (post_id, user_id, content, created_at)
VALUES
(1, 2, 'Congrats on finishing!', '2024-06-01 09:00:00'),
(1, 3, 'Great job!', '2024-06-01 09:15:00'),
(2, 1, 'Looks delicious!', '2024-06-02 08:00:00'),
(3, 2, 'Nice poem, share more!', '2024-06-03 00:10:00'),
(5, 3, 'Keep it up!', '2024-06-03 19:00:00');

-- Insert sample sleep entries
INSERT INTO `sleep_entries` (`date`, `start_time`, `end_time`, `quality`, `notes`, `user_id`) VALUES
('2024-03-15', '22:00:00', '06:00:00', 8, 'Good night sleep', 1),
('2024-03-16', '23:00:00', '07:00:00', 7, 'Decent sleep', 1);

-- Insert sample meal plans
INSERT INTO `meal_planner` (`meal_name`, `meal_date`, `meal_type`, `meal_description`, `meal_calories`, `meal_proteins`, `meal_carbs`, `meal_fats`, `user_id`) VALUES
('Healthy Breakfast', '2024-03-15', 'Breakfast', 'Nutritious morning meal', 500.00, 20.00, 60.00, 15.00, 1),
('Light Lunch', '2024-03-15', 'Lunch', 'Balanced lunch meal', 600.00, 25.00, 70.00, 20.00, 1);

-- Insert sample meal ingredients
INSERT INTO `meal_ingredient` (`ingredient_name`, `ingredient_quantity`, `ingredient_unit`, `meal_id`) VALUES
('Oatmeal', 100, 'grams', 1),
('Banana', 1, 'piece', 1),
('Chicken Breast', 150, 'grams', 2),
('Brown Rice', 100, 'grams', 2);

-- Sample meal plans for user_id 2 (Alice Johnson)
INSERT INTO `meal_planner` (`meal_id`, `meal_name`, `meal_date`, `meal_type`, `meal_description`, `meal_calories`, `meal_proteins`, `meal_carbs`, `meal_fats`, `user_id`) VALUES
(3, 'Power Lunch', '2024-03-16', 'Lunch', 'Protein-packed lunch', 700.00, 35.00, 80.00, 25.00, 2),
(4, 'Evening Salad', '2024-03-16', 'Dinner', 'Fresh veggie salad', 350.00, 8.00, 40.00, 15.00, 2);

INSERT INTO `meal_ingredient` (`ingredient_id`, `ingredient_name`, `ingredient_quantity`, `ingredient_unit`, `meal_id`) VALUES
(5, 'Grilled Chicken Breast', 150, 'grams', 3),
(6, 'Brown Rice', 100, 'grams', 3),
(7, 'Broccoli', 80, 'grams', 3),
(8, 'Lettuce', 50, 'grams', 4),
(9, 'Tomato', 1, 'pieces', 4),
(10, 'Olive Oil', 1, 'tablespoons', 4);

-- Insert sample meal plans for user_id 3 (Bob Smith)
INSERT INTO `meal_planner` (`meal_id`, `meal_name`, `meal_date`, `meal_type`, `meal_description`, `meal_calories`, `meal_proteins`, `meal_carbs`, `meal_fats`, `user_id`) VALUES
(5, 'Morning Oats', '2024-03-17', 'Breakfast', 'Oatmeal with fruit', 400.00, 12.00, 60.00, 8.00, 3),
(6, 'Steak Dinner', '2024-03-17', 'Dinner', 'Classic steak and potatoes', 800.00, 40.00, 70.00, 35.00, 3);

INSERT INTO `meal_ingredient` (`ingredient_id`, `ingredient_name`, `ingredient_quantity`, `ingredient_unit`, `meal_id`) VALUES
(11, 'Oatmeal', 60, 'grams', 5),
(12, 'Banana', 1, 'pieces', 5),
(13, 'Milk', 200, 'milliliters', 5),
(14, 'Beef Steak', 200, 'grams', 6),
(15, 'Potato', 150, 'grams', 6),
(16, 'Butter', 1, 'tablespoons', 6);


-- Insert sample blog posts
INSERT INTO `blog` (`title`, `content`, `created_at`, `author_username`, `suspended`) VALUES
('Healthy Eating Tips', 'Eat more vegetables and fruits every day.', NOW(), 'user1', FALSE),
('Workout Routines', 'Try HIIT for better results.', NOW(), 'user2', FALSE),
('Mental Health Matters', 'Meditation helps reduce stress.', NOW(), 'user1', TRUE);

-- Insert sample feedbacks
INSERT INTO `feedback` (`user_id`, `message`, `submitted_at`, `response`) VALUES
(2, 'Great site, very helpful!', '2024-06-01 10:15:00', NULL),
(3, 'I found a bug in the forum.', '2024-06-02 14:30:00', NULL);

-- Insert sample messages
INSERT INTO `messages` (`sender_id`, `receiver_id`, `content`, `is_read`) VALUES
(1, 2, 'Hi Bob, how are you?', FALSE),
(2, 1, 'Hi Alice, I am good. You?', TRUE),
(1, 3, 'Hey Charlie, long time no see!', FALSE),
(3, 1, 'Hi Alice! Yes, indeed.', TRUE),
(2, 3, 'Charlie, are you coming to the meeting?', FALSE),
(3, 2, 'Yes, I will be there!', TRUE);

-- Insert sample health assessments
INSERT INTO `health_assessment` (`bmi`, `sleep_score`, `nutrition_score`, `exercise_score`, `overall_score`, `risk_level`, `recommendations`, `user_id`) VALUES
(22.0, 8.0, 7.5, 8.5, 8.0, 'Low', 'Keep up the good work!', 1),
(21.5, 7.5, 7.0, 8.0, 7.5, 'Low', 'Maintain your healthy habits!', 2),
(22.9, 7.0, 6.5, 9.0, 7.5, 'Low', 'Great job, stay active!', 3),
(22.5, 8.0, 7.0, 8.0, 7.7, 'Low', 'Keep up the good work!', 4),
(22.0, 8.0, 7.5, 8.5, 8.0, 'Low', 'Keep up the good work!', 5),
(23.1, 7.5, 7.0, 9.0, 7.9, 'Low', 'Excellent routine!', 6),
(23.5, 7.0, 6.5, 8.0, 7.3, 'Low', 'Stay consistent!', 7),
(20.8, 8.0, 7.5, 7.0, 7.5, 'Low', 'Keep up the good work!', 8);

-- Sample Exercise Schedules
INSERT INTO `exercise_schedule` (`user_id`, `date`, `time`, `type`, `name`, `duration`, `intensity`, `calories`, `is_calories_estimated`, `notes`)
VALUES
(1, '2024-07-20', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 315, TRUE, 'Felt great, nice weather.'),
(1, '2024-07-20', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxing session.'),
(2, '2024-07-21', '06:30:00', 'Walking', 'Park Walk', 40, 'Low', 140, TRUE, 'Walked with a friend.'),
(3, '2024-07-21', '19:00:00', 'Weight Lifting', 'Upper Body', 50, 'High', 350, TRUE, 'Intense workout.');

-- Generated SQL for 30 Challenges and Badges
-- Challenge: Morning Yoga 7 Days
-- Insert badge source type

-- Insert challenge types
INSERT INTO challenge_type (id, name) VALUES
(1, 'PERSONAL'),
(2, 'PUBLIC');

--  Insert challenge categories
INSERT INTO challenge_category (id, name) VALUES
(1, 'Fitness'),
(2, 'Mindfulness'),
(3, 'Diet & Hydration'),
(4, 'Sleep'),
(5, 'Digital Wellness'),
(6, 'Personal Growth');

INSERT INTO badge_source_type (name) VALUES ('CHALLENGE');

-- Insert challenges with status and category_id
INSERT INTO challenge (id, name, description, status, type_id, category_id, duration_days) VALUES
(1, 'Morning Yoga 7 Days', 'Complete the morning yoga 7 days challenge.', 1, 1, 1, 7),
(2, '10 Pushups Daily', 'Complete the 10 pushups daily challenge.', 1, 1, 1, 10),
(3, 'Walk 5,000 Steps', 'Complete the walk 5,000 steps challenge.', 1, 1, 1, 7),
(4, 'Jump Rope 50x', 'Complete the jump rope 50x challenge.', 1, 1, 1, 5),
(5, 'Stretch Every Morning', 'Complete the stretch every morning challenge.', 1, 1, 6, 14),
(6, '5-Min Home Workout', 'Complete the 5-min home workout challenge.', 1, 1, 1, 10),
(7, 'Daily Gratitude Journal', 'Complete the daily gratitude journal challenge.', 1, 1, 2, 7),
(8, '5-min Meditation', 'Complete the 5-min meditation challenge.', 1, 1, 2, 10),
(9, 'No Phone After 10PM', 'Complete the no phone after 10pm challenge.', 1, 1, 5, 7),
(10, 'Deep Breathing Daily', 'Complete the deep breathing daily challenge.', 1, 1, 2, 5),
(11, 'Cold Showers Challenge', 'Complete the cold showers challenge.', 1, 1, 4, 5),
(12, 'Screen-Free Hour', 'Complete the screen-free hour challenge.', 1, 1, 5, 10),
(13, 'No Sugar Week', 'Complete the no sugar week challenge.', 1, 1, 3, 7),
(14, 'Fruit Before Noon', 'Complete the fruit before noon challenge.', 1, 1, 3, 10),
(15, 'Water Tracker', 'Complete the water tracker challenge.', 1, 1, 3, 7),
(16, 'Eat Salad Daily', 'Complete the eat salad daily challenge.', 1, 1, 3, 10),
(17, '8 Glasses of Water', 'Complete the 8 glasses of water challenge.', 1, 1, 3, 5),
(18, 'Cook Healthy Meal', 'Complete the cook healthy meal challenge.', 1, 1, 3, 7),
(19, 'Sleep Before 11PM', 'Complete the sleep before 11pm challenge.', 1, 1, 4, 7),
(20, '8H Sleep Daily', 'Complete the 8h sleep daily challenge.', 1, 1, 4, 10),
(21, 'Wake Up Before 7AM', 'Complete the wake up before 7am challenge.', 1, 1, 4, 5),
(22, 'No Caffeine After 6PM', 'Complete the no caffeine after 6pm challenge.', 1, 1, 4, 1),
(23, 'No Screen in Bed', 'Complete the no screen in bed challenge.', 1, 1, 5, 5),
(24, 'Power Nap Master', 'Complete the power nap master challenge.', 1, 1, 4, 5),
(25, 'Read 15 Min Daily', 'Complete the read 15 min daily challenge.', 1, 1, 6, 10),
(26, 'Practice a Hobby', 'Complete the practice a hobby challenge.', 1, 1, 6, 7),
(27, 'Learn a New Word', 'Complete the learn a new word challenge.', 1, 1, 6, 7),
(28, 'Write a Daily Journal', 'Complete the write a daily journal challenge.', 1, 1, 6, 10),
(29, 'Practice Gratitude', 'Complete the practice gratitude challenge.', 1, 1, 2, 7),
(30, 'Clean Room Daily', 'Complete the clean room daily challenge.', 1, 1, 6, 5);

UPDATE challenge SET creator_id = 5 WHERE creator_id IS NULL;

-- Insert badges with icon and locked_icon
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES
(1, 'Yoga Starter', 'Awarded for completing the "Morning Yoga 7 Days" challenge.', '/images/badges/unlocked/yoga.png', '/images/badges/locked/yoga_locked.png'),
(2, 'Push Master', 'Awarded for completing the "10 Pushups Daily" challenge.', '/images/badges/unlocked/pushups.png', '/images/badges/locked/pushups_locked.png'),
(3, 'Step Beginner', 'Awarded for completing the "Walk 5,000 Steps" challenge.', '/images/badges/unlocked/steps.png', '/images/badges/locked/steps_locked.png'),
(4, 'Jumper', 'Awarded for completing the "Jump Rope 50x" challenge.', '/images/badges/unlocked/jump.png', '/images/badges/locked/jump_locked.png'),
(5, 'Flexible Flow', 'Awarded for completing the "Stretch Every Morning" challenge.', '/images/badges/unlocked/stretch.png', '/images/badges/locked/stretch_locked.png'),
(6, 'Quick Fit', 'Awarded for completing the "5-Min Home Workout" challenge.', '/images/badges/unlocked/homeworkout.png', '/images/badges/locked/homeworkout_locked.png'),
(7, 'Grateful Heart', 'Awarded for completing the "Daily Gratitude Journal" challenge.', '/images/badges/unlocked/gratitude.png', '/images/badges/locked/gratitude_locked.png'),
(8, 'Mindful Soul', 'Awarded for completing the "5-min Meditation" challenge.', '/images/badges/unlocked/meditation.png', '/images/badges/locked/meditation_locked.png'),
(9, 'Digital Detoxer', 'Awarded for completing the "No Phone After 10PM" challenge.', '/images/badges/unlocked/nophone.png', '/images/badges/locked/nophone_locked.png'),
(10, 'Calm Spirit', 'Awarded for completing the "Deep Breathing Daily" challenge.', '/images/badges/unlocked/breathing.png', '/images/badges/locked/breathing_locked.png'),
(11, 'Chill Champ', 'Awarded for completing the "Cold Showers Challenge" challenge.', '/images/badges/unlocked/coldshower.png', '/images/badges/locked/coldshower_locked.png'),
(12, 'Focus Freak', 'Awarded for completing the "Screen-Free Hour" challenge.', '/images/badges/unlocked/focus.png', '/images/badges/locked/focus_locked.png'),
(13, 'Sugar-Free', 'Awarded for completing the "No Sugar Week" challenge.', '/images/badges/unlocked/nosugar.png', '/images/badges/locked/nosugar_locked.png'),
(14, 'Fruit First', 'Awarded for completing the "Fruit Before Noon" challenge.', '/images/badges/unlocked/fruit.png', '/images/badges/locked/fruit_locked.png'),
(15, 'Hydration Hero', 'Awarded for completing the "Water Tracker" challenge.', '/images/badges/unlocked/water.png', '/images/badges/locked/water_locked.png'),
(16, 'Green Eater', 'Awarded for completing the "Eat Salad Daily" challenge.', '/images/badges/unlocked/salad.png', '/images/badges/locked/salad_locked.png'),
(17, 'Aqua Master', 'Awarded for completing the "8 Glasses of Water" challenge.', '/images/badges/unlocked/glasses.png', '/images/badges/locked/glasses_locked.png'),
(18, 'Kitchen King', 'Awarded for completing the "Cook Healthy Meal" challenge.', '/images/badges/unlocked/meal.png', '/images/badges/locked/meal_locked.png'),
(19, 'Early Bird', 'Awarded for completing the "Sleep Before 11PM" challenge.', '/images/badges/unlocked/sleep.png', '/images/badges/locked/sleep_locked.png'),
(20, 'Sleep Champion', 'Awarded for completing the "8H Sleep Daily" challenge.', '/images/badges/unlocked/sleeptime.png', '/images/badges/locked/sleeptime_locked.png'),
(21, 'Rise & Shine', 'Awarded for completing the "Wake Up Before 7AM" challenge.', '/images/badges/unlocked/sunrise.png', '/images/badges/locked/sunrise_locked.png'),
(22, 'Sleep Saver', 'Awarded for completing the "No Caffeine After 6PM" challenge.', '/images/badges/unlocked/nocaffeine.png', '/images/badges/locked/nocaffeine_locked.png'),
(23, 'Eye Rested', 'Awarded for completing the "No Screen in Bed" challenge.', '/images/badges/unlocked/noscreen.png', '/images/badges/locked/noscreen_locked.png'),
(24, 'Nap Ninja', 'Awarded for completing the "Power Nap Master" challenge.', '/images/badges/unlocked/nap.png', '/images/badges/locked/nap_locked.png'),
(25, 'Bookworm', 'Awarded for completing the "Read 15 Min Daily" challenge.', '/images/badges/unlocked/read.png', '/images/badges/locked/read_locked.png'),
(26, 'Hobbyist', 'Awarded for completing the "Practice a Hobby" challenge.', '/images/badges/unlocked/hobby.png', '/images/badges/locked/hobby_locked.png'),
(27, 'Word Wizar', 'Awarded for completing the "Learn a New Word" challenge.', '/images/badges/unlocked/vocab.png', '/images/badges/locked/vocab_locked.png'),
(28, 'Thoughtful Writer', 'Awarded for completing the "Write a Daily Journal" challenge.', '/images/badges/unlocked/journal.png', '/images/badges/locked/journal_locked.png'),
(29, 'Thanks Giver', 'Awarded for completing the "Practice Gratitude" challenge.', '/images/badges/unlocked/thanks.png', '/images/badges/locked/thanks_locked.png'),
(30, 'Tidy Star', 'Awarded for completing the "Clean Room Daily" challenge.', '/images/badges/unlocked/cleanroom.png', '/images/badges/locked/cleanroom_locked.png');

-- Insert badge requirements
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 1),
(6, 6, 1),
(7, 7, 1),
(8, 8, 1),
(9, 9, 1),
(10, 10, 1),
(11, 11, 1),
(12, 12, 1),
(13, 13, 1),
(14, 14, 1),
(15, 15, 1),
(16, 16, 1),
(17, 17, 1),
(18, 18, 1),
(19, 19, 1),
(20, 20, 1),
(21, 21, 1),
(22, 22, 1),
(23, 23, 1),
(24, 24, 1),
(25, 25, 1),
(26, 26, 1),
(27, 27, 1),
(28, 28, 1),
(29, 29, 1),
(30, 30, 1);

-- Insert user_badge assignments
INSERT INTO user_badge (user_id, badge_id, earned_at) VALUES
(1, 1, '2024-06-01 10:00:00'),
(1, 2, '2024-06-08 10:00:00'),
(2, 1, '2024-06-02 11:00:00'),
(2, 3, '2024-06-09 12:00:00'),
(3, 4, '2024-06-15 09:00:00');

-- Add more sample sleep entries for users 1, 2, 3 (2025-07-01 to 2025-07-30)
INSERT INTO `sleep_entries` (`date`, `start_time`, `end_time`, `quality`, `notes`, `user_id`) VALUES
('2025-07-01', '22:30:00', '06:30:00', 8, 'Good sleep', 1),
('2025-07-02', '23:00:00', '07:00:00', 7, 'Slept late', 1),
('2025-07-03', '22:45:00', '06:45:00', 8, 'Normal', 1),
('2025-07-04', '22:00:00', '06:00:00', 8, 'Good', 1),
('2025-07-05', '23:15:00', '07:15:00', 7, 'Slept late', 1),
('2025-07-06', '22:30:00', '06:30:00', 8, 'Good', 1),
('2025-07-07', '22:00:00', '06:00:00', 8, 'Normal', 1),
('2025-07-08', '23:00:00', '07:00:00', 7, 'Slept late', 1),
('2025-07-09', '22:30:00', '06:30:00', 8, 'Good', 1),
('2025-07-10', '22:45:00', '06:45:00', 8, 'Normal', 1),
('2025-07-01', '23:00:00', '07:00:00', 7, 'Decent', 2),
('2025-07-02', '22:30:00', '06:30:00', 8, 'Good', 2),
('2025-07-03', '22:45:00', '06:45:00', 8, 'Normal', 2),
('2025-07-04', '23:15:00', '07:15:00', 7, 'Slept late', 2),
('2025-07-05', '22:00:00', '06:00:00', 8, 'Good', 2),
('2025-07-06', '22:30:00', '06:30:00', 8, 'Normal', 2),
('2025-07-07', '23:00:00', '07:00:00', 7, 'Decent', 2),
('2025-07-08', '22:45:00', '06:45:00', 8, 'Good', 2),
('2025-07-09', '22:00:00', '06:00:00', 8, 'Normal', 2),
('2025-07-10', '23:15:00', '07:15:00', 7, 'Slept late', 2),
('2025-07-01', '22:00:00', '06:00:00', 8, 'Good', 3),
('2025-07-02', '22:30:00', '06:30:00', 8, 'Normal', 3),
('2025-07-03', '23:00:00', '07:00:00', 7, 'Decent', 3),
('2025-07-04', '22:45:00', '06:45:00', 8, 'Good', 3),
('2025-07-05', '22:00:00', '06:00:00', 8, 'Normal', 3),
('2025-07-06', '23:15:00', '07:15:00', 7, 'Slept late', 3),
('2025-07-07', '22:30:00', '06:30:00', 8, 'Good', 3),
('2025-07-08', '22:00:00', '06:00:00', 8, 'Normal', 3),
('2025-07-09', '23:00:00', '07:00:00', 7, 'Decent', 3),
('2025-07-10', '22:45:00', '06:45:00', 8, 'Good', 3),
('2025-07-10', '22:45:00', '06:30:00', 9, 'Normal', 1),
('2025-07-11', '23:00:00', '06:30:00', 7, 'Normal', 1),
('2025-07-12', '23:00:00', '06:30:00', 7, 'Slept late', 1),
('2025-07-13', '22:00:00', '06:30:00', 6, 'Okay', 1),
('2025-07-14', '23:00:00', '06:30:00', 6, 'Slept late', 1),
('2025-07-15', '22:00:00', '06:30:00', 7, 'Decent', 1),
('2025-07-16', '23:15:00', '06:30:00', 9, 'Decent', 1),
('2025-07-17', '22:45:00', '06:30:00', 6, 'Okay', 1),
('2025-07-18', '22:00:00', '06:30:00', 9, 'Decent', 1),
('2025-07-19', '23:00:00', '06:30:00', 6, 'Decent', 1),
('2025-07-20', '22:00:00', '06:30:00', 8, 'Decent', 1),
('2025-07-21', '22:45:00', '06:30:00', 7, 'Good', 1),
('2025-07-22', '23:00:00', '06:30:00', 7, 'Decent', 1),
('2025-07-23', '22:45:00', '06:30:00', 9, 'Decent', 1),
('2025-07-24', '22:30:00', '06:30:00', 9, 'Normal', 1),
('2025-07-25', '22:30:00', '06:30:00', 9, 'Normal', 1),
('2025-07-26', '23:00:00', '06:30:00', 8, 'Slept late', 1),
('2025-07-27', '22:45:00', '06:30:00', 9, 'Decent', 1),
('2025-07-28', '22:00:00', '06:30:00', 6, 'Normal', 1),
('2025-07-29', '22:00:00', '06:30:00', 7, 'Slept late', 1),
('2025-07-30', '22:45:00', '06:30:00', 6, 'Okay', 1),
('2025-07-31', '23:15:00', '06:30:00', 9, 'Slept late', 1),
('2025-07-10', '22:00:00', '06:30:00', 9, 'Okay', 2),
('2025-07-11', '22:00:00', '06:30:00', 6, 'Slept late', 2),
('2025-07-12', '22:45:00', '06:30:00', 7, 'Slept late', 2),
('2025-07-13', '22:45:00', '06:30:00', 8, 'Good', 2),
('2025-07-14', '23:00:00', '06:30:00', 6, 'Normal', 2),
('2025-07-15', '23:15:00', '06:30:00', 6, 'Slept late', 2),
('2025-07-16', '23:00:00', '06:30:00', 6, 'Good', 2),
('2025-07-17', '23:15:00', '06:30:00', 6, 'Okay', 2),
('2025-07-18', '23:15:00', '06:30:00', 8, 'Decent', 2),
('2025-07-19', '23:15:00', '06:30:00', 7, 'Okay', 2),
('2025-07-20', '22:00:00', '06:30:00', 8, 'Decent', 2),
('2025-07-21', '22:30:00', '06:30:00', 7, 'Good', 2),
('2025-07-22', '23:00:00', '06:30:00', 6, 'Okay', 2),
('2025-07-23', '23:15:00', '06:30:00', 9, 'Good', 2),
('2025-07-24', '22:00:00', '06:30:00', 6, 'Slept late', 2),
('2025-07-25', '22:00:00', '06:30:00', 9, 'Normal', 2),
('2025-07-26', '23:00:00', '06:30:00', 7, 'Slept late', 2),
('2025-07-27', '23:15:00', '06:30:00', 7, 'Normal', 2),
('2025-07-28', '23:00:00', '06:30:00', 9, 'Decent', 2),
('2025-07-29', '23:00:00', '06:30:00', 8, 'Good', 2),
('2025-07-30', '22:30:00', '06:30:00', 6, 'Good', 2),
('2025-07-31', '23:00:00', '06:30:00', 8, 'Slept late', 2),
('2025-07-10', '22:00:00', '06:30:00', 6, 'Good', 3),
('2025-07-11', '23:15:00', '06:30:00', 8, 'Decent', 3),
('2025-07-12', '23:15:00', '06:30:00', 7, 'Good', 3),
('2025-07-13', '22:30:00', '06:30:00', 7, 'Normal', 3),
('2025-07-14', '23:00:00', '06:30:00', 8, 'Okay', 3),
('2025-07-15', '22:45:00', '06:30:00', 8, 'Decent', 3),
('2025-07-16', '22:45:00', '06:30:00', 6, 'Slept late', 3),
('2025-07-17', '22:00:00', '06:30:00', 6, 'Slept late', 3),
('2025-07-18', '23:00:00', '06:30:00', 7, 'Slept late', 3),
('2025-07-19', '22:45:00', '06:30:00', 6, 'Slept late', 3),
('2025-07-20', '22:00:00', '06:30:00', 6, 'Normal', 3),
('2025-07-21', '23:15:00', '06:30:00', 7, 'Slept late', 3),
('2025-07-22', '23:00:00', '06:30:00', 7, 'Decent', 3),
('2025-07-23', '22:30:00', '06:30:00', 6, 'Good', 3),
('2025-07-24', '23:15:00', '06:30:00', 9, 'Normal', 3),
('2025-07-25', '23:00:00', '06:30:00', 9, 'Slept late', 3),
('2025-07-26', '22:00:00', '06:30:00', 7, 'Normal', 3),
('2025-07-27', '23:15:00', '06:30:00', 6, 'Okay', 3),
('2025-07-28', '23:15:00', '06:30:00', 8, 'Decent', 3),
('2025-07-29', '22:30:00', '06:30:00', 6, 'Good', 3),
('2025-07-30', '22:00:00', '06:30:00', 9, 'Okay', 3),
('2025-07-31', '22:30:00', '06:30:00', 6, 'Slept late', 3);


-- Add more sample meal plans for users 1, 2, 3 (2025-07-01 to 2025-07-10)
INSERT INTO `meal_planner` (`meal_name`, `meal_date`, `meal_type`, `meal_description`, `meal_calories`, `meal_proteins`, `meal_carbs`, `meal_fats`, `user_id`) VALUES
('Oatmeal Breakfast', '2025-07-01', 'Breakfast', 'Oatmeal with banana', 400.00, 10.00, 60.00, 8.00, 1),
('Chicken Lunch', '2025-07-01', 'Lunch', 'Grilled chicken and rice', 600.00, 35.00, 70.00, 15.00, 1),
('Salad Dinner', '2025-07-01', 'Dinner', 'Green salad', 350.00, 8.00, 40.00, 10.00, 1),
('Oatmeal Breakfast', '2025-07-02', 'Breakfast', 'Oatmeal with apple', 420.00, 11.00, 62.00, 9.00, 1),
('Chicken Lunch', '2025-07-02', 'Lunch', 'Chicken and veggies', 610.00, 36.00, 68.00, 14.00, 1),
('Salad Dinner', '2025-07-02', 'Dinner', 'Salad with tuna', 370.00, 12.00, 38.00, 11.00, 1),
('Oatmeal Breakfast', '2025-07-03', 'Breakfast', 'Oatmeal with berries', 410.00, 10.00, 61.00, 8.00, 1),
('Chicken Lunch', '2025-07-03', 'Lunch', 'Chicken and rice', 605.00, 34.00, 72.00, 16.00, 1),
('Salad Dinner', '2025-07-03', 'Dinner', 'Salad with egg', 360.00, 9.00, 39.00, 10.00, 1),
('Oatmeal Breakfast', '2025-07-04', 'Breakfast', 'Oatmeal with banana', 400.00, 10.00, 60.00, 8.00, 1),
('Oatmeal Breakfast', '2025-07-01', 'Breakfast', 'Oatmeal with banana', 400.00, 10.00, 60.00, 8.00, 2),
('Chicken Lunch', '2025-07-01', 'Lunch', 'Grilled chicken and rice', 600.00, 35.00, 70.00, 15.00, 2),
('Salad Dinner', '2025-07-01', 'Dinner', 'Green salad', 350.00, 8.00, 40.00, 10.00, 2),
('Oatmeal Breakfast', '2025-07-02', 'Breakfast', 'Oatmeal with apple', 420.00, 11.00, 62.00, 9.00, 2),
('Chicken Lunch', '2025-07-02', 'Lunch', 'Chicken and veggies', 610.00, 36.00, 68.00, 14.00, 2),
('Salad Dinner', '2025-07-02', 'Dinner', 'Salad with tuna', 370.00, 12.00, 38.00, 11.00, 2),
('Oatmeal Breakfast', '2025-07-03', 'Breakfast', 'Oatmeal with berries', 410.00, 10.00, 61.00, 8.00, 2),
('Chicken Lunch', '2025-07-03', 'Lunch', 'Chicken and rice', 605.00, 34.00, 72.00, 16.00, 2),
('Salad Dinner', '2025-07-03', 'Dinner', 'Salad with egg', 360.00, 9.00, 39.00, 10.00, 2),
('Oatmeal Breakfast', '2025-07-04', 'Breakfast', 'Oatmeal with banana', 400.00, 10.00, 60.00, 8.00, 2),
('Oatmeal Breakfast', '2025-07-01', 'Breakfast', 'Oatmeal with banana', 400.00, 10.00, 60.00, 8.00, 3),
('Chicken Lunch', '2025-07-01', 'Lunch', 'Grilled chicken and rice', 600.00, 35.00, 70.00, 15.00, 3),
('Salad Dinner', '2025-07-01', 'Dinner', 'Green salad', 350.00, 8.00, 40.00, 10.00, 3),
('Oatmeal Breakfast', '2025-07-02', 'Breakfast', 'Oatmeal with apple', 420.00, 11.00, 62.00, 9.00, 3),
('Chicken Lunch', '2025-07-02', 'Lunch', 'Chicken and veggies', 610.00, 36.00, 68.00, 14.00, 3),
('Salad Dinner', '2025-07-02', 'Dinner', 'Salad with tuna', 370.00, 12.00, 38.00, 11.00, 3),
('Oatmeal Breakfast', '2025-07-03', 'Breakfast', 'Oatmeal with berries', 410.00, 10.00, 61.00, 8.00, 3),
('Chicken Lunch', '2025-07-03', 'Lunch', 'Chicken and rice', 605.00, 34.00, 72.00, 16.00, 3),
('Salad Dinner', '2025-07-03', 'Dinner', 'Salad with egg', 360.00, 9.00, 39.00, 10.00, 3),
('Oatmeal Breakfast', '2025-07-04', 'Breakfast', 'Oatmeal with banana', 400.00, 10.00, 60.00, 8.00, 3),
('Oatmeal Breakfast', '2025-07-10', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-10', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-10', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-11', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-11', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-11', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-12', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-12', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-12', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-13', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-13', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-13', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-14', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-14', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-14', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-15', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-15', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-15', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-16', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-16', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-16', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-17', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-17', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-17', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-18', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-18', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-18', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-19', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-19', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-19', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-20', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-20', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-20', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-21', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-21', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-21', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-22', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-22', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-22', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-23', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-23', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-23', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-24', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-24', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-24', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-25', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-25', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-25', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-26', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-26', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-26', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-27', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-27', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-27', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-28', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-28', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-28', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-29', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-29', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-29', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-30', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-30', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-30', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-31', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 1),
('Chicken Lunch', '2025-07-31', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 1),
('Salad Dinner', '2025-07-31', 'Dinner', 'Green salad', 350, 8, 40, 10, 1),
('Oatmeal Breakfast', '2025-07-10', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-10', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-10', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-11', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-11', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-11', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-12', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-12', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-12', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-13', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-13', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-13', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-14', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-14', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-14', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-15', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-15', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-15', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-16', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-16', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-16', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-17', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-17', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-17', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-18', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-18', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-18', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-19', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-19', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-19', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-20', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-20', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-20', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-21', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-21', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-21', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-22', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-22', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-22', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-23', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-23', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-23', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-24', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-24', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-24', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-25', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-25', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-25', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-26', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-26', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-26', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-27', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-27', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-27', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-28', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-28', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-28', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-29', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-29', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-29', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-30', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-30', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-30', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-31', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 2),
('Chicken Lunch', '2025-07-31', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 2),
('Salad Dinner', '2025-07-31', 'Dinner', 'Green salad', 350, 8, 40, 10, 2),
('Oatmeal Breakfast', '2025-07-10', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-10', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-10', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-11', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-11', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-11', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-12', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-12', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-12', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-13', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-13', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-13', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-14', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-14', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-14', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-15', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-15', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-15', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-16', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-16', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-16', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-17', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-17', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-17', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-18', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-18', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-18', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-19', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-19', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-19', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-20', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-20', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-20', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-21', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-21', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-21', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-22', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-22', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-22', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-23', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-23', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-23', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-24', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-24', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-24', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-25', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-25', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-25', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-26', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-26', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-26', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-27', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-27', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-27', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-28', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-28', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-28', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-29', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-29', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-29', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-30', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-30', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-30', 'Dinner', 'Green salad', 350, 8, 40, 10, 3),
('Oatmeal Breakfast', '2025-07-31', 'Breakfast', 'Oatmeal with banana', 400, 10, 60, 8, 3),
('Chicken Lunch', '2025-07-31', 'Lunch', 'Grilled chicken and rice', 600, 35, 70, 15, 3),
('Salad Dinner', '2025-07-31', 'Dinner', 'Green salad', 350, 8, 40, 10, 3);


-- Add more sample exercise schedules for users 1, 2, 3 (2025-07-01 to 2025-07-10)
INSERT INTO `exercise_schedule` (`user_id`, `date`, `time`, `type`, `name`, `duration`, `intensity`, `calories`, `is_calories_estimated`, `notes`) VALUES
(1, '2025-07-01', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(1, '2025-07-01', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(1, '2025-07-02', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(1, '2025-07-03', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(1, '2025-07-04', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(1, '2025-07-05', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(1, '2025-07-06', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(1, '2025-07-07', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(1, '2025-07-08', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(1, '2025-07-09', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-01', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(2, '2025-07-01', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-02', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(2, '2025-07-03', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(2, '2025-07-04', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(2, '2025-07-05', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-06', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(2, '2025-07-07', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(2, '2025-07-08', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(2, '2025-07-09', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(3, '2025-07-01', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(3, '2025-07-01', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(3, '2025-07-02', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(3, '2025-07-03', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(3, '2025-07-04', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(3, '2025-07-05', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(3, '2025-07-06', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(3, '2025-07-07', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(3, '2025-07-08', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(3, '2025-07-09', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(1, '2025-07-10', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Felt good'),
(1, '2025-07-10', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Intense'),
(1, '2025-07-11', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(1, '2025-07-11', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(1, '2025-07-12', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(1, '2025-07-12', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(1, '2025-07-13', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(1, '2025-07-13', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(1, '2025-07-14', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(1, '2025-07-14', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Good pace'),
(1, '2025-07-15', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(1, '2025-07-15', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(1, '2025-07-16', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Good pace'),
(1, '2025-07-16', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Good pace'),
(1, '2025-07-17', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Felt good'),
(1, '2025-07-17', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(1, '2025-07-18', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(1, '2025-07-18', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Good pace'),
(1, '2025-07-19', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(1, '2025-07-19', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(1, '2025-07-20', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(1, '2025-07-20', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(1, '2025-07-21', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Good pace'),
(1, '2025-07-21', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Felt good'),
(1, '2025-07-22', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(1, '2025-07-22', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(1, '2025-07-23', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(1, '2025-07-23', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Good pace'),
(1, '2025-07-24', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Good pace'),
(1, '2025-07-24', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Relaxed'),
(1, '2025-07-25', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Felt good'),
(1, '2025-07-25', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(1, '2025-07-26', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Intense'),
(1, '2025-07-26', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(1, '2025-07-27', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Intense'),
(1, '2025-07-27', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(1, '2025-07-28', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Good pace'),
(1, '2025-07-28', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(1, '2025-07-29', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Felt good'),
(1, '2025-07-29', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(1, '2025-07-30', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(1, '2025-07-30', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(1, '2025-07-31', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Nice weather'),
(1, '2025-07-31', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(2, '2025-07-10', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Good pace'),
(2, '2025-07-10', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-11', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(2, '2025-07-11', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(2, '2025-07-12', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Felt good'),
(2, '2025-07-12', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(2, '2025-07-13', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Felt good'),
(2, '2025-07-13', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Nice weather'),
(2, '2025-07-14', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Relaxed'),
(2, '2025-07-14', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(2, '2025-07-15', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Felt good'),
(2, '2025-07-15', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(2, '2025-07-16', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(2, '2025-07-16', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-17', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(2, '2025-07-17', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(2, '2025-07-18', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(2, '2025-07-18', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(2, '2025-07-19', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Intense'),
(2, '2025-07-19', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(2, '2025-07-20', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(2, '2025-07-20', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-21', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(2, '2025-07-21', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Intense'),
(2, '2025-07-22', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Intense'),
(2, '2025-07-22', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Good pace'),
(2, '2025-07-23', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Good pace'),
(2, '2025-07-23', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(2, '2025-07-24', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(2, '2025-07-24', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(2, '2025-07-25', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(2, '2025-07-25', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(2, '2025-07-26', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(2, '2025-07-26', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Relaxed'),
(2, '2025-07-27', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(2, '2025-07-27', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(2, '2025-07-28', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(2, '2025-07-28', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Nice weather'),
(2, '2025-07-29', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(2, '2025-07-29', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(2, '2025-07-30', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Felt good'),
(2, '2025-07-30', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(2, '2025-07-31', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(2, '2025-07-31', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(3, '2025-07-10', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(3, '2025-07-10', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-11', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Intense'),
(3, '2025-07-11', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(3, '2025-07-12', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(3, '2025-07-12', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(3, '2025-07-13', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(3, '2025-07-13', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Relaxed'),
(3, '2025-07-14', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Intense'),
(3, '2025-07-14', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Felt good'),
(3, '2025-07-15', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Good pace'),
(3, '2025-07-15', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(3, '2025-07-16', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(3, '2025-07-16', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Relaxed'),
(3, '2025-07-17', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Nice weather'),
(3, '2025-07-17', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(3, '2025-07-18', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(3, '2025-07-18', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense'),
(3, '2025-07-19', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Relaxed'),
(3, '2025-07-19', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Felt good'),
(3, '2025-07-20', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Relaxed'),
(3, '2025-07-20', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-21', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(3, '2025-07-21', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(3, '2025-07-22', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Nice weather'),
(3, '2025-07-22', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-23', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(3, '2025-07-23', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-24', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Felt good'),
(3, '2025-07-24', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-25', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Nice weather'),
(3, '2025-07-25', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(3, '2025-07-26', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Intense'),
(3, '2025-07-26', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Intense'),
(3, '2025-07-27', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Good pace'),
(3, '2025-07-27', '07:00:00', 'Running', 'Morning Run', 30, 'Medium', 300, TRUE, 'Felt good'),
(3, '2025-07-28', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Good pace'),
(3, '2025-07-28', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Relaxed'),
(3, '2025-07-29', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-29', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Good pace'),
(3, '2025-07-30', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Felt good'),
(3, '2025-07-30', '18:00:00', 'Weight Lifting', 'Strength', 50, 'High', 350, TRUE, 'Nice weather'),
(3, '2025-07-31', '07:00:00', 'Cycling', 'Morning Ride', 40, 'Medium', 250, TRUE, 'Nice weather'),
(3, '2025-07-31', '18:00:00', 'Yoga', 'Evening Yoga', 45, 'Low', 110, TRUE, 'Intense');