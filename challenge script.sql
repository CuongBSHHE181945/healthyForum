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
    user_id BIGINT NOT NULL,
    challenge_id INT NOT NULL,
    join_date DATE,
    status VARCHAR(20),
    last_check_date DATE,

    FOREIGN KEY (user_id) REFERENCES user(userID),
    FOREIGN KEY (challenge_id) REFERENCES challenge(id)
);

-- Insert challenge types
INSERT INTO challenge_type (id, name) VALUES
(1, 'PERSONAL'),
(2, 'PUBLIC');

-- Insert challenges
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES
(1, '10,000 Steps Daily', 'Walk at least 10,000 steps every day.', 1, 30),
(2, 'No Sugar Week', 'Avoid all added sugars for a week.', 1, 7),
(3, 'Community Clean-Up', 'Join the community clean-up event.', 2, 1),
(4, 'Hydration Challenge', 'Drink 2 liters of water daily.', 1, 14),
(5, 'Public Yoga Session', 'Participate in a public yoga session.', 2, 1);
