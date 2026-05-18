package attendance.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager.
 * Update DB_URL / DB_USER / DB_PASS to match your MySQL setup.
 */
public class DBConnection {

    // ── Configuration ─────────────────────────────────────────
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/attendance_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";          // ← change to your password
    // ──────────────────────────────────────────────────────────

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java JAR to classpath.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try { connection.close(); }
            catch (SQLException ignored) {}
            connection = null;
        }
    }
}
