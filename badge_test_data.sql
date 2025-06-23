-- Generated SQL for 30 Challenges and Badges
-- Challenge: Morning Yoga 7 Days
-- Insert badge source type

-- Insert challenge types
INSERT INTO challenge_type (id, name) VALUES
(1, 'PERSONAL'),
(2, 'PUBLIC');

INSERT INTO badge_source_type (name) VALUES ('CHALLENGE');

INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (1, 'Morning Yoga 7 Days', 'Complete the morning yoga 7 days challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (1, 'Yoga Starter', 'Awarded for completing the "Morning Yoga 7 Days" challenge.', '/icons/yoga.png', '/icons/yoga_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (1, 1, 1);

-- Challenge: 10 Pushups Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (2, '10 Pushups Daily', 'Complete the 10 pushups daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (2, 'Push Master', 'Awarded for completing the "10 Pushups Daily" challenge.', '/icons/pushups.png', '/icons/pushups_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (2, 2, 1);

-- Challenge: Walk 5,000 Steps
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (3, 'Walk 5,000 Steps', 'Complete the walk 5,000 steps challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (3, 'Step Beginner', 'Awarded for completing the "Walk 5,000 Steps" challenge.', '/icons/steps.png', '/icons/steps_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (3, 3, 1);

-- Challenge: Jump Rope 50x
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (4, 'Jump Rope 50x', 'Complete the jump rope 50x challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (4, 'Jumper', 'Awarded for completing the "Jump Rope 50x" challenge.', '/icons/jump.png', '/icons/jump_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (4, 4, 1);

-- Challenge: Stretch Every Morning
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (5, 'Stretch Every Morning', 'Complete the stretch every morning challenge.', 1, 14);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (5, 'Flexible Flow', 'Awarded for completing the "Stretch Every Morning" challenge.', '/icons/stretch.png', '/icons/stretch_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (5, 5, 1);

-- Challenge: 5-Min Home Workout
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (6, '5-Min Home Workout', 'Complete the 5-min home workout challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (6, 'Quick Fit', 'Awarded for completing the "5-Min Home Workout" challenge.', '/icons/homeworkout.png', '/icons/homeworkout_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (6, 6, 1);

-- Challenge: Daily Gratitude Journal
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (7, 'Daily Gratitude Journal', 'Complete the daily gratitude journal challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (7, 'Grateful Heart', 'Awarded for completing the "Daily Gratitude Journal" challenge.', '/icons/gratitude.png', '/icons/gratitude_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (7, 7, 1);

-- Challenge: 5-min Meditation
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (8, '5-min Meditation', 'Complete the 5-min meditation challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (8, 'Mindful Soul', 'Awarded for completing the "5-min Meditation" challenge.', '/icons/meditation.png', '/icons/meditation_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (8, 8, 1);

-- Challenge: No Phone After 10PM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (9, 'No Phone After 10PM', 'Complete the no phone after 10pm challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (9, 'Digital Detoxer', 'Awarded for completing the "No Phone After 10PM" challenge.', '/icons/nophone.png', '/icons/nophone_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (9, 9, 1);

-- Challenge: Deep Breathing Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (10, 'Deep Breathing Daily', 'Complete the deep breathing daily challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (10, 'Calm Spirit', 'Awarded for completing the "Deep Breathing Daily" challenge.', '/icons/breathing.png', '/icons/breathing_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (10, 10, 1);

-- Challenge: Cold Showers Challenge
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (11, 'Cold Showers Challenge', 'Complete the cold showers challenge challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (11, 'Chill Champ', 'Awarded for completing the "Cold Showers Challenge" challenge.', '/icons/coldshower.png', '/icons/coldshower_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (11, 11, 1);

-- Challenge: Screen-Free Hour
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (12, 'Screen-Free Hour', 'Complete the screen-free hour challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (12, 'Focus Freak', 'Awarded for completing the "Screen-Free Hour" challenge.', '/icons/focus.png', '/icons/focus_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (12, 12, 1);

-- Challenge: No Sugar Week
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (13, 'No Sugar Week', 'Complete the no sugar week challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (13, 'Sugar-Free', 'Awarded for completing the "No Sugar Week" challenge.', '/icons/nosugar.png', '/icons/nosugar_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (13, 13, 1);

-- Challenge: Fruit Before Noon
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (14, 'Fruit Before Noon', 'Complete the fruit before noon challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (14, 'Fruit First', 'Awarded for completing the "Fruit Before Noon" challenge.', '/icons/fruit.png', '/icons/fruit_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (14, 14, 1);

-- Challenge: Water Tracker
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (15, 'Water Tracker', 'Complete the water tracker challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (15, 'Hydration Hero', 'Awarded for completing the "Water Tracker" challenge.', '/icons/water.png', '/icons/water_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (15, 15, 1);

-- Challenge: Eat Salad Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (16, 'Eat Salad Daily', 'Complete the eat salad daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (16, 'Green Eater', 'Awarded for completing the "Eat Salad Daily" challenge.', '/icons/salad.png', '/icons/salad_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (16, 16, 1);

-- Challenge: 8 Glasses of Water
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (17, '8 Glasses of Water', 'Complete the 8 glasses of water challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (17, 'Aqua Master', 'Awarded for completing the "8 Glasses of Water" challenge.', '/icons/glasses.png', '/icons/glasses_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (17, 17, 1);

-- Challenge: Cook Healthy Meal
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (18, 'Cook Healthy Meal', 'Complete the cook healthy meal challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (18, 'Kitchen King', 'Awarded for completing the "Cook Healthy Meal" challenge.', '/icons/meal.png', '/icons/meal_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (18, 18, 1);

-- Challenge: Sleep Before 11PM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (19, 'Sleep Before 11PM', 'Complete the sleep before 11pm challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (19, 'Early Bird', 'Awarded for completing the "Sleep Before 11PM" challenge.', '/icons/sleep.png', '/icons/sleep_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (19, 19, 1);

-- Challenge: 8H Sleep Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (20, '8H Sleep Daily', 'Complete the 8h sleep daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (20, 'Sleep Champion', 'Awarded for completing the "8H Sleep Daily" challenge.', '/icons/sleeptime.png', '/icons/sleeptime_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (20, 20, 1);

-- Challenge: Wake Up Before 7AM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (21, 'Wake Up Before 7AM', 'Complete the wake up before 7am challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (21, 'Rise & Shine', 'Awarded for completing the "Wake Up Before 7AM" challenge.', '/icons/sunrise.png', '/icons/sunrise_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (21, 21, 1);

-- Challenge: No Caffeine After 6PM
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (22, 'No Caffeine After 6PM', 'Complete the no caffeine after 6pm challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (22, 'Sleep Saver', 'Awarded for completing the "No Caffeine After 6PM" challenge.', '/icons/nocaffeine.png', '/icons/nocaffeine_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (22, 22, 1);

-- Challenge: No Screen in Bed
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (23, 'No Screen in Bed', 'Complete the no screen in bed challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (23, 'Eye Rested', 'Awarded for completing the "No Screen in Bed" challenge.', '/icons/noscreen.png', '/icons/noscreen_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (23, 23, 1);

-- Challenge: Power Nap Master
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (24, 'Power Nap Master', 'Complete the power nap master challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (24, 'Nap Ninja', 'Awarded for completing the "Power Nap Master" challenge.', '/icons/nap.png', '/icons/nap_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (24, 24, 1);

-- Challenge: Read 15 Min Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (25, 'Read 15 Min Daily', 'Complete the read 15 min daily challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (25, 'Bookworm', 'Awarded for completing the "Read 15 Min Daily" challenge.', '/icons/read.png', '/icons/read_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (25, 25, 1);

-- Challenge: Practice a Hobby
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (26, 'Practice a Hobby', 'Complete the practice a hobby challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (26, 'Hobbyist', 'Awarded for completing the "Practice a Hobby" challenge.', '/icons/hobby.png', '/icons/hobby_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (26, 26, 1);

-- Challenge: Learn a New Word
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (27, 'Learn a New Word', 'Complete the learn a new word challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (27, 'Word Wizard', 'Awarded for completing the "Learn a New Word" challenge.', '/icons/vocab.png', '/icons/vocab_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (27, 27, 1);

-- Challenge: Write a Daily Journal
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (28, 'Write a Daily Journal', 'Complete the write a daily journal challenge.', 1, 10);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (28, 'Thoughtful Writer', 'Awarded for completing the "Write a Daily Journal" challenge.', '/icons/journal.png', '/icons/journal_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (28, 28, 1);

-- Challenge: Practice Gratitude
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (29, 'Practice Gratitude', 'Complete the practice gratitude challenge.', 1, 7);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (29, 'Thanks Giver', 'Awarded for completing the "Practice Gratitude" challenge.', '/icons/thanks.png', '/icons/thanks_locked.png');
INSERT INTO badge_requirement (badge_id, source_id, source_type_id) VALUES (29, 29, 1);

-- Challenge: Clean Room Daily
INSERT INTO challenge (id, name, description, type_id, duration_days) VALUES (30, 'Clean Room Daily', 'Complete the clean room daily challenge.', 1, 5);
INSERT INTO badge (id, name, description, icon, locked_icon) VALUES (30, 'Tidy Star', 'Awarded for completing the "Clean Room Daily" challenge.', '/icons/cleanroom.png', '/icons/cleanroom_locked.png');
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

