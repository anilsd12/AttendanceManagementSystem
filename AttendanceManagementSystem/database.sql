-- ============================================================
--  Attendance Management System — Database Setup Script
--  Run this in MySQL before launching the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;

-- ── Users (Admin / Teacher / Student login) ──────────────────
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    role        ENUM('ADMIN','TEACHER','STUDENT') NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Students ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   VARCHAR(20)  NOT NULL UNIQUE,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(100),
    phone        VARCHAR(15),
    department   VARCHAR(100),
    user_id      INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ── Subjects ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS subjects (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20)  NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    department   VARCHAR(100),
    teacher_id   INT,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ── Attendance ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT  NOT NULL,
    subject_id   INT  NOT NULL,
    date         DATE NOT NULL,
    status       ENUM('PRESENT','ABSENT','LATE') NOT NULL DEFAULT 'ABSENT',
    marked_by    INT,
    remarks      VARCHAR(255),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_attendance (student_id, subject_id, date),
    FOREIGN KEY (student_id)  REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id)  REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (marked_by)   REFERENCES users(id)    ON DELETE SET NULL
);

-- ── Seed Data ────────────────────────────────────────────────
-- Default admin account  (password: admin123)
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN');

-- Sample teacher  (password: teacher123)
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('teacher1', 'teacher123', 'Prof. Ramesh Kumar', 'TEACHER');

-- Sample student user  (password: student123)
INSERT IGNORE INTO users (username, password, full_name, role)
VALUES ('student1', 'student123', 'Arjun Sharma', 'STUDENT');

-- Sample students
INSERT IGNORE INTO students (student_id, full_name, email, phone, department, user_id)
VALUES
    ('STU001', 'Arjun Sharma',   'arjun@email.com',   '9876543210', 'Computer Science', 3),
    ('STU002', 'Priya Patel',    'priya@email.com',   '9876543211', 'Computer Science', NULL),
    ('STU003', 'Rohit Verma',    'rohit@email.com',   '9876543212', 'Electronics',      NULL),
    ('STU004', 'Sneha Reddy',    'sneha@email.com',   '9876543213', 'Computer Science', NULL),
    ('STU005', 'Karan Mehta',    'karan@email.com',   '9876543214', 'Electronics',      NULL);

-- Sample subjects
INSERT IGNORE INTO subjects (subject_code, subject_name, department, teacher_id)
VALUES
    ('CS101', 'Data Structures',        'Computer Science', 2),
    ('CS102', 'Database Management',    'Computer Science', 2),
    ('EC101', 'Digital Electronics',    'Electronics',      2),
    ('CS103', 'Operating Systems',      'Computer Science', 2);

-- ── Stored Procedure: Attendance % per student per subject ──
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS GetAttendanceReport(IN subj_id INT)
BEGIN
    SELECT
        s.student_id,
        s.full_name,
        COUNT(a.id)                                         AS total_classes,
        SUM(a.status = 'PRESENT')                           AS present,
        SUM(a.status = 'ABSENT')                            AS absent,
        SUM(a.status = 'LATE')                              AS late,
        ROUND(SUM(a.status = 'PRESENT') * 100.0 / COUNT(a.id), 2) AS percentage
    FROM students s
    LEFT JOIN attendance a ON a.student_id = s.id AND a.subject_id = subj_id
    GROUP BY s.id
    ORDER BY s.student_id;
END //
DELIMITER ;
