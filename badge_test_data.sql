-- Badge System Minimal Test Data
-- Only 4 badges, only CHALLENGE source type

-- Insert badge source type
INSERT INTO badge_source_type (name) VALUES ('CHALLENGE');

-- Insert badges
INSERT INTO badge (name, description, icon, locked_icon) VALUES
('First Post', 'Awarded for making your first post', '/images/badges/unlocked/first post badge.png', '/images/badges/locked/first post badge - locked.png'),
('Consistency Star', 'Awarded for posting consistently for a week', '/images/badges/unlocked/consistency star.png', '/images/badges/locked/consistency star-locked.png'),
('Water Bottle', 'Awarded for logging your water intake for 7 days', '/images/badges/unlocked/water-bottle.png', '/images/badges/locked/water-bottle-locked.png'),
('1 Month Streak', 'Awarded for a 1 month activity streak', '/images/badges/unlocked/1 month streak.png', '/images/badges/locked/1 month streak - locked.png');

-- Insert badge requirements (all use CHALLENGE, id=1)
INSERT INTO badge_requirement (badge_id, source_type_id, source_id, value) VALUES
(1, 1, 1, '1'),   -- First Post: challenge 1, value 1 (e.g. first post)
(2, 1, 2, '7'),   -- Consistency Star: challenge 2, value 7 (e.g. 7 days)
(3, 1, 3, '7'),   -- Water Bottle: challenge 3, value 7 (e.g. 7 days)
(4, 1, 4, '30');  -- 1 Month Streak: challenge 4, value 30 (e.g. 30 days)

-- Insert user_badge assignments (example users: 1, 2, 3)
INSERT INTO user_badge (userID, badge_id, earned_at) VALUES
(1, 1, '2024-06-01 10:00:00'),
(1, 2, '2024-06-08 10:00:00'),
(2, 1, '2024-06-02 11:00:00'),
(2, 3, '2024-06-09 12:00:00'),
(3, 4, '2024-06-15 09:00:00'); 