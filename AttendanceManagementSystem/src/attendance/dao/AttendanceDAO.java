package attendance.dao;

import attendance.model.Attendance;
import attendance.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    /**
     * Upsert attendance record (INSERT … ON DUPLICATE KEY UPDATE).
     * The UNIQUE KEY uq_attendance(student_id, subject_id, date) handles conflicts.
     */
    public boolean markAttendance(Attendance a) {
        String sql =
            "INSERT INTO attendance (student_id, subject_id, date, status, marked_by, remarks) " +
            "VALUES (?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE status=VALUES(status), marked_by=VALUES(marked_by), remarks=VALUES(remarks)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt   (1, a.getStudentId());
            ps.setInt   (2, a.getSubjectId());
            ps.setDate  (3, a.getDate());
            ps.setString(4, a.getStatus());
            ps.setInt   (5, a.getMarkedBy());
            ps.setString(6, a.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** All attendance records for a given subject on a given date. */
    public List<Attendance> getAttendanceBySubjectAndDate(int subjectId, Date date) {
        List<Attendance> list = new ArrayList<>();
        String sql =
            "SELECT a.*, s.student_id AS roll, s.full_name AS sname " +
            "FROM attendance a " +
            "JOIN students s ON s.id = a.student_id " +
            "WHERE a.subject_id = ? AND a.date = ? " +
            "ORDER BY s.student_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt (1, subjectId);
            ps.setDate(2, date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance att = map(rs);
                att.setStudentRollId(rs.getString("roll"));
                att.setStudentName(rs.getString("sname"));
                list.add(att);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Attendance history for one student across all subjects. */
    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql =
            "SELECT a.*, sub.subject_name AS subname " +
            "FROM attendance a " +
            "JOIN subjects sub ON sub.id = a.subject_id " +
            "WHERE a.student_id = ? " +
            "ORDER BY a.date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance att = map(rs);
                att.setSubjectName(rs.getString("subname"));
                list.add(att);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Summary report: per-student attendance % for a subject. */
    public List<Object[]> getAttendanceReport(int subjectId) {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT s.student_id, s.full_name, s.department, " +
            "  COUNT(a.id) AS total, " +
            "  SUM(a.status='PRESENT') AS present, " +
            "  SUM(a.status='ABSENT')  AS absent, " +
            "  SUM(a.status='LATE')    AS late, " +
            "  ROUND(SUM(a.status='PRESENT')*100.0/NULLIF(COUNT(a.id),0),1) AS pct " +
            "FROM students s " +
            "LEFT JOIN attendance a ON a.student_id=s.id AND a.subject_id=? " +
            "GROUP BY s.id ORDER BY s.student_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getInt("total"),
                    rs.getInt("present"),
                    rs.getInt("absent"),
                    rs.getInt("late"),
                    rs.getDouble("pct") + "%"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Count today's present records. */
    public int getTodayPresentCount() {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM attendance " +
                     "WHERE date = CURDATE() AND status = 'PRESENT'";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Count today's absent records. */
    public int getTodayAbsentCount() {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM attendance " +
                     "WHERE date = CURDATE() AND status = 'ABSENT'";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Delete an attendance record. */
    public boolean deleteAttendance(int id) {
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setSubjectId(rs.getInt("subject_id"));
        a.setDate(rs.getDate("date"));
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        return a;
    }
}
