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

