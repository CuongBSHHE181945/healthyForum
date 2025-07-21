# Healthy Forum

A web application for health tracking, meal planning, sleep logging, and community engagement.

---

## Setup Instructions

### 1. Pre-setup
- IntelliJ Community Edition for easy used
- MySQL Workbench

### 2. Install Java & Gradle
- Make sure you have **JDK 17** and **Gradle** installed.

### 3. Database Setup (MySQL)
- Use the file "database_setup.sql" in MySQL to setup and recive sample data
- About food_item data, Import this "Book1.csv", here is the step:
 + Right click 'healthyforum' at database schema, choose 'Table Data Import Wizard'
 + Browse and locate the "Book1.csv"
 + Select Destination: Use existing table: "healthyforum:food_item"
 + Click next until the import data is completed (Click slowly to prevent error)

### 4. Configure Application Properties
- Edit `src/main/resources/application.properties` to set your MySQL credentials and any other environment-specific settings.

### 5. Run the Application
- Run command in terminal: "./gradlew clean build" to check database connection
- bootRun the project to start running
- The app will be available at [http://localhost:8081](http://localhost:8081/login)

## Troubleshooting

- If you encounter database errors, check your MySQL credentials and that the SQL scripts ran successfully.
- For OAuth issues, ensure your Google credentials are correct and redirect URIs are set.

-----
