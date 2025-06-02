-- Create the database
CREATE DATABASE IF NOT EXISTS healthyForum;
USE healthyForum;

-- Create User table
CREATE TABLE IF NOT EXISTS user (
    userID BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    gender VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- Create Sleep Entries table
CREATE TABLE IF NOT EXISTS sleep_entries (
    sleep_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    quality INT NOT NULL,
    notes VARCHAR(255),
    userID BIGINT NOT NULL,
    FOREIGN KEY (userID) REFERENCES user(userID)
);

-- Create Meal Planner table
CREATE TABLE IF NOT EXISTS meal_planner (
    meal_id INT PRIMARY KEY AUTO_INCREMENT,
    meal_name VARCHAR(255),
    meal_date DATE,
    meal_type VARCHAR(255),
    meal_description TEXT,
    meal_calories DECIMAL(10,2),
    meal_proteins DECIMAL(10,2),
    meal_carbs DECIMAL(10,2),
    meal_fats DECIMAL(10,2),
    userID BIGINT NOT NULL,
    FOREIGN KEY (userID) REFERENCES user(userID)
);

-- Create Meal Ingredient table
CREATE TABLE IF NOT EXISTS meal_ingredient (
    ingredient_id INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_name VARCHAR(255) NOT NULL,
    ingredient_quantity INT NOT NULL,
    ingredient_unit VARCHAR(255),
    meal_id INT NOT NULL,
    FOREIGN KEY (meal_id) REFERENCES meal_planner(meal_id)
);

-- Insert sample data (passwords are hashed using BCrypt)
INSERT INTO user (username, password, fullname, email, gender, dob, address) VALUES
('admin', '$2a$10$rDkPvvAFV6GgJddt4gK1IOBwY0LhUzY/6TqgGGZ6mGFEOgfYD2Zpi', 'Admin User', 'admin@healthyforum.com', 'Male', '1990-01-01', '123 Admin St'),
('user1', '$2a$10$rDkPvvAFV6GgJddt4gK1IOBwY0LhUzY/6TqgGGZ6mGFEOgfYD2Zpi', 'John Doe', 'john@example.com', 'Male', '1995-05-15', '456 User Ave');

-- Insert sample sleep entries
INSERT INTO sleep_entries (date, start_time, end_time, quality, notes, userID) VALUES
('2024-03-15', '22:00:00', '06:00:00', 8, 'Good night sleep', 1),
('2024-03-16', '23:00:00', '07:00:00', 7, 'Decent sleep', 1);

-- Insert sample meal plans
INSERT INTO meal_planner (meal_name, meal_date, meal_type, meal_description, meal_calories, meal_proteins, meal_carbs, meal_fats, userID) VALUES
('Healthy Breakfast', '2024-03-15', 'Breakfast', 'Nutritious morning meal', 500.00, 20.00, 60.00, 15.00, 1),
('Light Lunch', '2024-03-15', 'Lunch', 'Balanced lunch meal', 600.00, 25.00, 70.00, 20.00, 1);

-- Insert sample meal ingredients
INSERT INTO meal_ingredient (ingredient_name, ingredient_quantity, ingredient_unit, meal_id) VALUES
('Oatmeal', 100, 'grams', 1),
('Banana', 1, 'piece', 1),
('Chicken Breast', 150, 'grams', 2),
('Brown Rice', 100, 'grams', 2); 