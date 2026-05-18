# 📋 Attendance Management System
### Java Swing + JDBC + MySQL

A full-featured desktop application to track daily student attendance, manage students and subjects, and generate attendance reports — built with **Java Swing** for the GUI and **JDBC** to connect with **MySQL**.

---

## ✨ Features

| Module | What you can do |
|---|---|
| **Login** | Role-based login (Admin / Teacher / Student) |
| **Dashboard** | Live stats: total students, subjects, present/absent today |
| **Student Management** | Add, edit, delete, search students |
| **Subject Management** | Add, edit, delete subjects |
| **Mark Attendance** | Select subject + date → mark each student PRESENT / ABSENT / LATE in a table; bulk "Mark All" buttons |
| **Reports – Subject Summary** | Attendance % per student for any subject; colour-coded (green/yellow/red) |
| **Reports – Student History** | Full attendance log for any individual student |
| **User Management** | Admin-only: create/edit/delete user accounts |

---

## 🗂 Project Structure

```
AttendanceManagementSystem/
├── database.sql                         ← Run this in MySQL first
└── src/
    └── attendance/
        ├── Main.java                    ← Entry point
        ├── model/
        │   ├── User.java
        │   ├── Student.java
        │   ├── Subject.java
        │   └── Attendance.java
        ├── dao/
        │   ├── UserDAO.java
        │   ├── StudentDAO.java
        │   ├── SubjectDAO.java
        │   └── AttendanceDAO.java
        ├── ui/
        │   ├── LoginFrame.java
        │   ├── MainFrame.java
        │   ├── StudentPanel.java
        │   ├── SubjectPanel.java
        │   ├── MarkAttendancePanel.java
        │   ├── ReportPanel.java
        │   └── UserPanel.java
        └── util/
            ├── DBConnection.java
            └── UITheme.java
```

---

## ⚙️ Setup Instructions

### 1. Requirements
- **Java JDK 8+**
- **MySQL 5.7 / 8.x**
- **mysql-connector-java** JAR (download from https://dev.mysql.com/downloads/connector/j/)
- Any IDE: IntelliJ IDEA / Eclipse / NetBeans

---

### 2. Database Setup

Open MySQL Workbench (or the MySQL CLI) and run:

```sql
SOURCE /path/to/AttendanceManagementSystem/database.sql;
```

This creates the `attendance_db` database with all tables and sample data.

---

### 3. Configure DB Password

Open `src/attendance/util/DBConnection.java` and update:

```java
private static final String DB_PASS = "root";  // ← your MySQL password
```

---

### 4. Add MySQL Connector JAR

**IntelliJ IDEA:**
1. File → Project Structure → Modules → Dependencies
2. Click `+` → JARs or Directories
3. Select `mysql-connector-java-x.x.xx.jar`

**Eclipse:**
1. Right-click project → Build Path → Configure Build Path
2. Libraries → Add External JARs
3. Select `mysql-connector-java-x.x.xx.jar`

---

### 5. Run

Run `attendance.Main` (or `LoginFrame.main()` for the login window directly).

---

## 🔐 Default Credentials

| Role    | Username  | Password    |
|---------|-----------|-------------|
| Admin   | admin     | admin123    |
| Teacher | teacher1  | teacher123  |
| Student | student1  | student123  |

---

## 🖥 UI Screenshots (description)

- **Login Screen** – Dark blue brand panel + white login form
- **Dashboard** – 4 stat cards + quick action buttons
- **Mark Attendance** – Dropdown-based status editor with colour-coded cells
- **Reports** – Attendance % table with green/yellow/red highlights

---

## 🛠 Tech Stack

| Layer    | Technology                  |
|----------|-----------------------------|
| Language | Java 8+                     |
| GUI      | Java Swing (JFrame, JTable, JComboBox, JSpinner) |
| Database | MySQL 5.7 / 8.x             |
| Driver   | JDBC (mysql-connector-java) |
| Pattern  | DAO (Data Access Object)    |

---

## 📌 Notes

- The `UNIQUE KEY uq_attendance(student_id, subject_id, date)` constraint prevents duplicate entries; re-saving on the same date updates the existing record (upsert).
- Attendance percentage is highlighted: **≥ 75% green**, **50–74% yellow**, **< 50% red**.
- The application uses a singleton `DBConnection`; for production consider a connection pool (HikariCP).
