# Student Score Entry and Analysis System

## Project Overview
This is a full-stack application for managing student scores and analyzing course objectives achievement.

### Use Cases Implemented
1. **User Login**: Multi-role support (Teacher, Secretary, Student).
2. **Score Entry**: Teacher can enter detailed scores (Attendance, Tests, Lab, Final).
3. **Automated Calculation**: System calculates aggregates and total grades automatically.
4. **Analysis**: Visualizes Course Objective Achievement based on complex weighting rules.
5. **Report Generation**: Mock AI report generation.

## Technology Stack
- **Backend**: Java 17, Spring Boot 3, MyBatis, H2 Database (In-memory).
- **Frontend**: Vue 3, Vite, Axios, Pinia, Chart.js.

## Project Structure
- `backend/`: Spring Boot application.
- `frontend/`: Vue.js application.

## Setup Instructions

### Backend
1. Open the `backend` folder in VS Code or IntelliJ.
2. Run `mvn spring-boot:run` or just run `StudentScoreSystemApplication.java`.
3. Server runs on `http://localhost:8080`.
4. H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, No password).

### Frontend
1. Open the `frontend` folder in VS Code terminal.
2. Run `npm install`.
3. Run `npm run dev`.
4. Access the app at `http://localhost:5173`.

## Default Credentials
- **Teacher**: `teacher1` / `123456`
- **Student**: `student1` / `123456`
