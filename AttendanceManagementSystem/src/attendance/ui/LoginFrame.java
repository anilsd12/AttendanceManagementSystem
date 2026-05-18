package attendance.ui;

import attendance.dao.UserDAO;
import attendance.model.User;
import attendance.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton        btnLogin;
    private JLabel         lblError;

    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        UITheme.apply();
        initUI();
    }

    private void initUI() {
        setTitle("Attendance Management System – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Root panel (two columns) ─────────────────────────
        JPanel root = new JPanel(new GridLayout(1, 2));
        add(root);

        // Left – decorative brand panel
        JPanel brand = new JPanel(new GridBagLayout());
        brand.setBackground(UITheme.SIDEBAR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 12, 0);

        JLabel icon = new JLabel("📋", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setForeground(Color.WHITE);
        brand.add(icon, gbc);

        gbc.gridy = 1;
        JLabel appName = new JLabel("Attendance MS", SwingConstants.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appName.setForeground(Color.WHITE);
        brand.add(appName, gbc);

        gbc.gridy = 2;
        JLabel tagline = new JLabel("Track Daily Attendance with Ease", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(180, 200, 255));
        brand.add(tagline, gbc);

        root.add(brand);

        // Right – login form
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(UITheme.CONTENT_BG);
        JPanel form = buildForm();
        formWrapper.add(form);
        root.add(formWrapper);
    }

    private JPanel buildForm() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.CONTENT_BG);
        p.setPreferredSize(new Dimension(340, 380));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalStrut(10));
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        p.add(Box.createVerticalStrut(28));

        // Username
        p.add(makeLabel("Username"));
        txtUsername = new JTextField(20);
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setBorder(UITheme.fieldBorder());
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(txtUsername);
        p.add(Box.createVerticalStrut(14));

        // Password
        p.add(makeLabel("Password"));
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setBorder(UITheme.fieldBorder());
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(txtPassword);
        p.add(Box.createVerticalStrut(14));

        // Role
        p.add(makeLabel("Login as"));
        cmbRole = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT"});
        cmbRole.setFont(UITheme.FONT_BODY);
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(cmbRole);
        p.add(Box.createVerticalStrut(20));

        // Error label
        lblError = new JLabel(" ");
        lblError.setForeground(UITheme.DANGER);
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lblError);
        p.add(Box.createVerticalStrut(6));

        // Login button
        btnLogin = UITheme.primaryButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(btnLogin);

        // Hint
        p.add(Box.createVerticalStrut(16));
        JLabel hint = new JLabel("Default admin: admin / admin123");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(hint);

        // ── Action ──────────────────────────────────────────
        ActionListener doLogin = e -> login();
        btnLogin.addActionListener(doLogin);
        txtPassword.addActionListener(doLogin);

        return p;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role     = (String) cmbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        User user = userDAO.login(username, password);
        if (user == null) {
            lblError.setText("Invalid username or password.");
            return;
        }
        if (!user.getRole().equals(role)) {
            lblError.setText("Selected role does not match account role.");
            return;
        }

        dispose();
        SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
