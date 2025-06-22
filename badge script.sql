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
    userID INT NOT NULL,
    badge_id INT NOT NULL,
    earned_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (userID, badge_id),
    FOREIGN KEY (badge_id) REFERENCES badge(id),
    FOREIGN KEY (`userID`) REFERENCES `user`(`userID`)
);
