package attendance.ui;

import attendance.dao.AttendanceDAO;
import attendance.dao.StudentDAO;
import attendance.dao.SubjectDAO;
import attendance.dao.UserDAO;
import attendance.model.User;
import attendance.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Main window – sidebar on the left, card area on the right.
 */
public class MainFrame extends JFrame {

    private final User    currentUser;
    private JPanel        contentPanel;
    private JButton       activeNavBtn;

    private final StudentDAO    studentDAO    = new StudentDAO();
    private final SubjectDAO    subjectDAO    = new SubjectDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final UserDAO       userDAO       = new UserDAO();

    public MainFrame(User user) {
        this.currentUser = user;
        UITheme.apply();
        initUI();
    }

    // ──────────────────────────────────────────────────────────
    private void initUI() {
        setTitle("Attendance Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          buildSidebar(), buildContentArea());
        split.setDividerLocation(220);
        split.setDividerSize(0);
        split.setEnabled(false);
        add(split);

        showDashboard();
    }

    // ── Sidebar ──────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Logo area
        JPanel logo = new JPanel(new GridBagLayout());
        logo.setBackground(UITheme.SIDEBAR_BG);
        logo.setPreferredSize(new Dimension(220, 76));

        JLabel icon = new JLabel("📋 AMS", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        icon.setForeground(Color.WHITE);
        logo.add(icon);
        sidebar.add(logo, BorderLayout.NORTH);

        // Nav buttons
        JPanel nav = new JPanel();
        nav.setBackground(UITheme.SIDEBAR_BG);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        nav.add(navBtn("🏠  Dashboard",  e -> showDashboard()));
        nav.add(navBtn("👥  Students",   e -> showStudents()));
        nav.add(navBtn("📚  Subjects",   e -> showSubjects()));
        nav.add(navBtn("✅  Mark Attendance", e -> showMarkAttendance()));
        nav.add(navBtn("📊  Reports",    e -> showReports()));
        if (currentUser.getRole().equals("ADMIN")) {
            nav.add(navBtn("🔧  Manage Users", e -> showUsers()));
        }
        sidebar.add(nav, BorderLayout.CENTER);

        // User card at bottom
        JPanel userCard = new JPanel(new GridBagLayout());
        userCard.setBackground(new Color(15, 30, 65));
        userCard.setPreferredSize(new Dimension(220, 70));

        JLabel uLabel = new JLabel("<html><b>" + currentUser.getFullName() +
                "</b><br><font color='#aabbff'>" + currentUser.getRole() + "</font></html>");
        uLabel.setForeground(Color.WHITE);
        uLabel.setFont(UITheme.FONT_SMALL);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(UITheme.FONT_SMALL);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(UITheme.DANGER);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 12, 0, 0);
        userCard.add(uLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 8, 0, 10);
        userCard.add(btnLogout, gbc);

        sidebar.add(userCard, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton navBtn(String label, ActionListener al) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_NAV);
        btn.setForeground(new Color(200, 215, 255));
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(UITheme.SIDEBAR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeNavBtn) btn.setBackground(UITheme.SIDEBAR_BG);
            }
        });
        btn.addActionListener(e -> {
            setActiveNav(btn);
            al.actionPerformed(e);
        });
        return btn;
    }

    private void setActiveNav(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setBackground(UITheme.SIDEBAR_BG);
            activeNavBtn.setForeground(new Color(200, 215, 255));
        }
        activeNavBtn = btn;
        btn.setBackground(UITheme.SIDEBAR_SELECT);
        btn.setForeground(Color.WHITE);
    }

    // ── Content area ─────────────────────────────────────────
    private JPanel buildContentArea() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.CONTENT_BG);
        return contentPanel;
    }

    private void setContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ── Dashboard ────────────────────────────────────────────
    private void showDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(UITheme.CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UITheme.headerLabel("Dashboard"), BorderLayout.WEST);
        JLabel dateLabel = new JLabel(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")));
        dateLabel.setFont(UITheme.FONT_BODY);
        dateLabel.setForeground(UITheme.TEXT_MUTED);
        header.add(dateLabel, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Stat cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.add(statCard("Total Students", String.valueOf(studentDAO.getTotalCount()),
                UITheme.ACCENT, "👥"));
        cards.add(statCard("Total Subjects", String.valueOf(subjectDAO.getTotalCount()),
                new Color(111, 66, 193), "📚"));
        cards.add(statCard("Present Today",  String.valueOf(attendanceDAO.getTodayPresentCount()),
                UITheme.SUCCESS, "✅"));
        cards.add(statCard("Absent Today",   String.valueOf(attendanceDAO.getTodayAbsentCount()),
                UITheme.DANGER, "❌"));
        panel.add(cards, BorderLayout.CENTER);

        // Quick actions
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        quickActions.setOpaque(false);
        JLabel qlabel = new JLabel("Quick Actions:");
        qlabel.setFont(UITheme.FONT_HEADER);
        qlabel.setForeground(UITheme.TEXT_PRIMARY);
        quickActions.add(qlabel);

        JButton qaAtt = UITheme.primaryButton("Mark Attendance");
        qaAtt.addActionListener(e -> showMarkAttendance());
        quickActions.add(qaAtt);

        JButton qaReport = UITheme.secondaryButton("View Reports");
        qaReport.addActionListener(e -> showReports());
        quickActions.add(qaReport);

        JButton qaStudent = UITheme.successButton("Add Student");
        qaStudent.addActionListener(e -> showStudents());
        quickActions.add(qaStudent);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, " + currentUser.getFullName() +
                "  |  Role: " + currentUser.getRole());
        welcome.setFont(UITheme.FONT_BODY);
        welcome.setForeground(UITheme.TEXT_MUTED);
        south.add(welcome, BorderLayout.NORTH);
        south.add(quickActions, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);

        setContent(panel);
    }

    private JPanel statCard(String title, String value, Color accent, String emoji) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 8));

        JLabel iconLbl = new JLabel(emoji + "  " + title);
        iconLbl.setFont(UITheme.FONT_BODY);
        iconLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 38));
        valLbl.setForeground(accent);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230, 235, 248));

        card.add(iconLbl, BorderLayout.NORTH);
        card.add(valLbl,  BorderLayout.CENTER);
        card.add(sep,     BorderLayout.SOUTH);
        return card;
    }

    // ── Delegate panels ──────────────────────────────────────
    private void showStudents()        { setContent(new StudentPanel(currentUser)); }
    private void showSubjects()        { setContent(new SubjectPanel(currentUser)); }
    private void showMarkAttendance()  { setContent(new MarkAttendancePanel(currentUser)); }
    private void showReports()         { setContent(new ReportPanel(currentUser)); }
    private void showUsers()           { setContent(new UserPanel(currentUser)); }

    private void logout() {
        int res = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
