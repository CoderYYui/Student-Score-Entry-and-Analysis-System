-- User Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- SECRETARY, TEACHER, STUDENT
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120),
    failed_attempts INT DEFAULT 0,
    locked BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'APPROVED' -- PENDING, APPROVED, REJECTED
);

-- Course Table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    teacher_id BIGINT,
    class_time VARCHAR(50),
    location VARCHAR(100),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- Enrollment Table (Student-Course mapping)
CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE(student_id, course_id)
);

-- Course Objectives Table
-- Each course has multiple objectives, and each objective has weights for different assessment methods
CREATE TABLE course_objectives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    objective_index INT NOT NULL, -- 1, 2, etc.
    description TEXT,
    regular_weight DOUBLE, -- contribution of Regular Score to this objective (0.0 - 1.0)
    test_weight DOUBLE,    -- contribution of Test Score to this objective
    lab_weight DOUBLE,     -- contribution of Lab Score to this objective
    final_weight DOUBLE,   -- contribution of Final Exam Score to this objective
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Scores Table
-- Stores raw scores for each student in each course
CREATE TABLE scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    
    attendance_score DOUBLE DEFAULT 0, -- 100
    performance_score DOUBLE DEFAULT 0, -- 100
    homework_score DOUBLE DEFAULT 0, -- 100
    
    test1_score DOUBLE DEFAULT 0, -- 100
    test2_score DOUBLE DEFAULT 0, -- 100
    test3_score DOUBLE DEFAULT 0, -- 100
    test4_score DOUBLE DEFAULT 0, -- 100
    
    lab_attendance_score DOUBLE DEFAULT 0, -- 100
    lab_report_score DOUBLE DEFAULT 0, -- 100
    
    final_exam_score DOUBLE DEFAULT 0, -- 100
    absence_count INT DEFAULT 0,
    
    -- Calculated Fields (stored for performance or calculated on fly, good to store for history)
    regular_aggregate DOUBLE DEFAULT 0, -- 0.3*Att + 0.2*Perf + 0.5*HW
    test_aggregate DOUBLE DEFAULT 0,    -- Avg(Test1..4)
    lab_aggregate DOUBLE DEFAULT 0,     -- 0.2*LabAtt + 0.8*LabRep
    
    total_score DOUBLE DEFAULT 0,       -- 0.2*Reg + 0.2*Test + 0.3*Lab + 0.3*Final
    grade_label VARCHAR(5),
    
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    UNIQUE(student_id, course_id)
);

-- Satisfaction Survey Table
CREATE TABLE satisfaction_surveys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    satisfaction_level VARCHAR(20), -- 'VERY_SATISFIED', 'SATISFIED', etc.
    comment TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE score_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_attachment TEXT,
    reply_text TEXT,
    reply_attachment TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    replied_at TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);
