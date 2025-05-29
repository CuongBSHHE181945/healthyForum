-- Clean tables first to avoid FK conflicts
DELETE FROM sleep_entries;
DELETE FROM user;

-- Insert users
INSERT INTO user (userID, username, password, fullname, email, gender, dob, address) VALUES
(1, 'sarah1', '{bcrypt}$2a$10$7Q0oyYcp7ZpA4G7M.P.6vOtQF8BCq4KPvIEcZBtDi0Z1c/1IlTEdu',
'Sarah Smith', 'sarah@example.com', 'Female', '1990-01-01', '123 Wellness St.'),
(2, 'alice123', 'password1', 'Alice Johnson', 'alice@example.com', 'Female', '1995-06-12', '123 Main St'),
(3, 'bob456', 'password2', 'Bob Smith', 'bob@example.com', 'Male', '1990-03-08', '456 Park Ave'),
(4, 'carol789', 'password3', 'Carol Davis', 'carol@example.com', 'Female', '1988-11-22', '789 Elm Rd');

-- Insert sleep entries
INSERT INTO sleep_entries (date, start_time, end_time, quality, notes, userID) VALUES
('2025-05-20', '22:30:00', '06:30:00', 8, 'Slept well', 1),
('2025-05-21', '23:00:00', '07:00:00', 7, 'Slight headache', 1),
('2025-05-20', '00:00:00', '08:00:00', 6, 'Woke up a few times', 2),
('2025-05-21', '22:00:00', '06:00:00', 9, 'Very restful night', 2),
('2025-05-22', '23:30:00', '06:30:00', 5, 'Too much caffeine', 2),
('2025-05-20', '21:30:00', '05:30:00', 7, 'Good sleep', 3),
('2025-05-21', '22:15:00', '06:45:00', 8, 'Felt fresh', 3),
('2025-05-22', '23:45:00', '07:15:00', 6, 'Nightmares', 3),
('2025-05-23', '22:00:00', '06:00:00', 7, 'Solid sleep', 1),
('2025-05-24', '23:00:00', '07:30:00', 9, 'Best sleep in a while', 1);
