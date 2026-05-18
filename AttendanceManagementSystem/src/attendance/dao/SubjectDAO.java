package attendance.dao;

import attendance.model.Subject;
import attendance.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY subject_code";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSubject(Subject s) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, department) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDepartment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateSubject(Subject s) {
        String sql = "UPDATE subjects SET subject_code=?, subject_name=?, department=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDepartment());
            ps.setInt(4, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteSubject(int id) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM subjects";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Subject map(ResultSet rs) throws SQLException {
        Subject s = new Subject();
        s.setId(rs.getInt("id"));
        s.setSubjectCode(rs.getString("subject_code"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setDepartment(rs.getString("department"));
        return s;
    }
}
