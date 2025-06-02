use healthyForum;

CREATE TABLE `role` (
    `role_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(255) NOT NULL
);

CREATE TABLE `user` (
    `userID` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `fullname` VARCHAR(255),
    `email` VARCHAR(255),
    `gender` BOOLEAN NOT NULL,
    `dob` DATE,
    `address` VARCHAR(255),
    `suspended` BOOLEAN NOT NULL DEFAULT FALSE,
    `role_id` BIGINT,
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role`(`role_id`)
);

ALTER TABLE user ADD CONSTRAINT unique_username UNIQUE (username);

CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    message TEXT,
    submitted_at DATETIME,
    response TEXT
);

CREATE TABLE report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    created_at DATETIME,
    response TEXT
);

CREATE TABLE blog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at DATETIME,
    author_username VARCHAR(255),
    suspended BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_author_username FOREIGN KEY (author_username) REFERENCES user(username)
);


CREATE TABLE health_assessment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    age INT,
    gender VARCHAR(10),
    height DOUBLE,
    weight DOUBLE,
    smoker BOOLEAN,
    exercise_days_per_week INT,
    sleep_pattern VARCHAR(20),
    risk_level VARCHAR(20),
    health_suggestions TEXT,
    userID BIGINT,
    assessment_date DATETIME,
    FOREIGN KEY (userID) REFERENCES user(userID)
);

-- Insert roles
INSERT INTO `role` (`role_name`) VALUES ('Admin'), ('User');


INSERT INTO `user` (`username`, `password`, `fullname`, `email`, `gender`, `dob`, `address`, `suspended`, `role_id`) VALUES
('admin1', 'adminpass', 'Alice Admin', 'alice@admin.com', 1, '1990-01-01', '123 Admin St', 0, 1),
('mod1', 'modpass', 'Bob Mod', 'bob@mod.com', 1, '1992-02-02', '456 Mod Ave', 0, 2),
('user1', 'userpass', 'Charlie User', 'charlie@user.com', 0, '1995-03-03', '789 User Rd', 0, 2),
('user2', 'userpass2', 'Dana User', 'dana@user.com', 0, '1998-04-04', '101 User Blvd', 1, 2);

-- Insert sample feedbacks
INSERT INTO feedback (username, message, submitted_at, response) VALUES
('alice', 'Great site, very helpful!', '2024-06-01 10:15:00', NULL),
('bob', 'I found a bug in the forum.', '2024-06-02 14:30:00', NULL);

-- Insert sample reports
INSERT INTO report (title, content, created_at, response) VALUES
('Spam Post', 'User xyz is posting spam links.', '2024-06-03 09:00:00', NULL),
('Inappropriate Content', 'There is an offensive comment in thread 123.', '2024-06-03 16:45:00', NULL);

INSERT INTO blog (title, content, created_at, author_username, suspended) VALUES
('Healthy Eating Tips', 'Eat more vegetables and fruits every day.', NOW(), 'user1', FALSE),
('Workout Routines', 'Try HIIT for better results.', NOW(), 'user2', FALSE),
('Mental Health Matters', 'Meditation helps reduce stress.', NOW(), 'user1', TRUE);

INSERT INTO health_assessment (
    age, 
    gender, 
    height, 
    weight, 
    smoker, 
    exercise_days_per_week, 
    sleep_pattern, 
    risk_level, 
    health_suggestions,
    userID, 
    assessment_date
) VALUES 
(25, 'Male', 175.0, 70.0, false, 4, 'Good', 'Low', 
'Keep maintaining your healthy lifestyle!', 
1, '2024-03-01 10:00:00'),

(35, 'Female', 165.0, 58.0, true, 2, 'Fair', 'Moderate', 
'Some lifestyle improvements needed. Consider increasing exercise to 4-5 days per week and maintaining better sleep patterns.', 
1, '2024-03-02 11:30:00'),

(45, 'Male', 180.0, 85.0, true, 1, 'Poor', 'High', 
'Immediate lifestyle changes recommended. Try to exercise at least 3-4 days per week, quit smoking, and improve sleep habits.', 
1, '2024-03-03 14:15:00'),

(30, 'Female', 160.0, 55.0, false, 5, 'Good', 'Low', 
'Keep maintaining your healthy lifestyle!', 
1, '2024-03-04 09:45:00'),

(52, 'Male', 178.0, 90.0, true, 2, 'Fair', 'High', 
'Immediate lifestyle changes recommended. Try to exercise at least 3-4 days per week, quit smoking, and improve sleep habits.', 
1, '2024-03-05 16:20:00');

select * from health_assessment;


