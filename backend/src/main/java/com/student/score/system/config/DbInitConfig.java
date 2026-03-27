package com.student.score.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class DbInitConfig {
    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            List<String> ddlStatements = List.of(
                    "ALTER TABLE courses ADD COLUMN class_time VARCHAR(100)",
                    "ALTER TABLE courses ADD COLUMN location VARCHAR(100)",
                    "ALTER TABLE users ADD COLUMN email VARCHAR(120)",
                    "ALTER TABLE users ADD COLUMN failed_attempts INT DEFAULT 0",
                    "ALTER TABLE users ADD COLUMN locked BOOLEAN DEFAULT FALSE",
                    "ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'APPROVED'",
                    "ALTER TABLE scores ADD COLUMN absence_count INT DEFAULT 0",
                    "CREATE TABLE IF NOT EXISTS score_messages (" +
                            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                            "course_id BIGINT NOT NULL, " +
                            "student_id BIGINT NOT NULL, " +
                            "teacher_id BIGINT NOT NULL, " +
                            "question_text TEXT NOT NULL, " +
                            "question_attachment TEXT, " +
                            "reply_text TEXT, " +
                            "reply_attachment TEXT, " +
                            "status VARCHAR(20) DEFAULT 'PENDING', " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "replied_at TIMESTAMP NULL, " +
                            "FOREIGN KEY (course_id) REFERENCES courses(id), " +
                            "FOREIGN KEY (student_id) REFERENCES users(id), " +
                            "FOREIGN KEY (teacher_id) REFERENCES users(id)" +
                            ")"
            );

            for (String statement : ddlStatements) {
                try {
                    jdbcTemplate.execute(statement);
                } catch (Exception exception) {
                    System.out.println("Schema init skipped: " + exception.getMessage());
                }
            }
        };
    }
}
