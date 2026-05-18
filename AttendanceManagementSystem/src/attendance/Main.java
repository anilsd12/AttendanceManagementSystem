package attendance;

import attendance.ui.LoginFrame;
import attendance.util.UITheme;
import javax.swing.SwingUtilities;

/**
 * Entry point for the Attendance Management System.
 *
 * Run this class to launch the application.
 * Make sure:
 *   1. MySQL is running and you have imported database.sql
 *   2. mysql-connector-java-x.x.xx.jar is on the classpath
 *   3. DBConnection.java has the correct DB_PASS for your MySQL root user
 */
public class Main {
    public static void main(String[] args) {
        UITheme.apply();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
