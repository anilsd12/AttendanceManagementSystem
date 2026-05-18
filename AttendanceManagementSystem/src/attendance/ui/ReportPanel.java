package attendance.ui;

import attendance.dao.AttendanceDAO;
import attendance.dao.StudentDAO;
import attendance.dao.SubjectDAO;
import attendance.model.Attendance;
import attendance.model.Student;
import attendance.model.Subject;
import attendance.model.User;
import attendance.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ReportPanel extends JPanel {

    private final User           currentUser;
    private final AttendanceDAO  attDAO     = new AttendanceDAO();
    private final SubjectDAO     subjectDAO = new SubjectDAO();
    private final StudentDAO     studentDAO = new StudentDAO();

    private JComboBox<Subject>  cmbSubject;
    private JComboBox<Student>  cmbStudent;
    private JTable              summaryTable, historyTable;
    private DefaultTableModel   summaryModel, historyModel;
    private JTabbedPane         tabs;

    public ReportPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 14));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        header.add(UITheme.headerLabel("Attendance Reports"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_HEADER);
        tabs.addTab("Subject Summary", buildSummaryTab());
        tabs.addTab("Student History", buildHistoryTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Subject Summary tab ──────────────────────────────────
    private JPanel buildSummaryTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UITheme.CONTENT_BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel ctrl = UITheme.card();
        ctrl.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 8));
        ctrl.add(new JLabel("Subject:"));
        cmbSubject = new JComboBox<>();
        cmbSubject.setFont(UITheme.FONT_BODY);
        cmbSubject.setPreferredSize(new Dimension(260, 34));
        for (Subject s : subjectDAO.getAllSubjects()) cmbSubject.addItem(s);
        ctrl.add(cmbSubject);

        JButton btnLoad = UITheme.primaryButton("Generate Report");
        btnLoad.addActionListener(e -> loadSummaryReport());
        ctrl.add(btnLoad);

        p.add(ctrl, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Full Name", "Department",
                         "Total Classes", "Present", "Absent", "Late", "Attendance %"};
        summaryModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        summaryTable = new JTable(summaryModel);
        UITheme.styleTable(summaryTable);

        // Colour-code the % column
        summaryTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                try {
                    double pct = Double.parseDouble(val.toString().replace("%",""));
                    if (pct >= 75) { setBackground(new Color(212, 245, 220)); setForeground(new Color(30,130,60)); }
                    else if (pct >= 50) { setBackground(new Color(255,243,205)); setForeground(new Color(130,100,0)); }
                    else { setBackground(new Color(255,218,218)); setForeground(new Color(160,20,20)); }
                } catch (Exception ignored) {}
                return this;
            }
        });

        p.add(new JScrollPane(summaryTable), BorderLayout.CENTER);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legend.setOpaque(false);
        legend.add(colourChip(new Color(212,245,220), "≥ 75% (Good)"));
        legend.add(colourChip(new Color(255,243,205), "50–74% (Low)"));
        legend.add(colourChip(new Color(255,218,218), "< 50% (Critical)"));
        p.add(legend, BorderLayout.SOUTH);
        return p;
    }

    private JPanel colourChip(Color bg, String label) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chip.setOpaque(false);
        JLabel box = new JLabel("  ");
        box.setOpaque(true); box.setBackground(bg);
        chip.add(box);
        JLabel l = new JLabel(label);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        chip.add(l);
        return chip;
    }

    private void loadSummaryReport() {
        Subject subject = (Subject) cmbSubject.getSelectedItem();
        if (subject == null) return;
        summaryModel.setRowCount(0);
        for (Object[] row : attDAO.getAttendanceReport(subject.getId()))
            summaryModel.addRow(row);
    }

    // ── Student History tab ──────────────────────────────────
    private JPanel buildHistoryTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UITheme.CONTENT_BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));

        JPanel ctrl = UITheme.card();
        ctrl.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 8));
        ctrl.add(new JLabel("Student:"));
        cmbStudent = new JComboBox<>();
        cmbStudent.setFont(UITheme.FONT_BODY);
        cmbStudent.setPreferredSize(new Dimension(260, 34));
        for (Student s : studentDAO.getAllStudents()) cmbStudent.addItem(s);
        ctrl.add(cmbStudent);

        JButton btnLoad = UITheme.primaryButton("Load History");
        btnLoad.addActionListener(e -> loadStudentHistory());
        ctrl.add(btnLoad);
        p.add(ctrl, BorderLayout.NORTH);

        String[] cols = {"#", "Date", "Subject", "Status", "Remarks"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(historyModel);
        UITheme.styleTable(historyTable);
        historyTable.getColumnModel().getColumn(0).setMaxWidth(40);

        // Status renderer
        historyTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                String v = val == null ? "" : val.toString();
                if (!sel) {
                    if ("PRESENT".equals(v)) { setBackground(new Color(212,245,220)); setForeground(new Color(30,130,60)); }
                    else if ("ABSENT".equals(v)) { setBackground(new Color(255,218,218)); setForeground(new Color(160,20,20)); }
                    else { setBackground(new Color(255,243,205)); setForeground(new Color(130,100,0)); }
                }
                return this;
            }
        });
        p.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return p;
    }

    private void loadStudentHistory() {
        Student student = (Student) cmbStudent.getSelectedItem();
        if (student == null) return;
        historyModel.setRowCount(0);
        List<Attendance> records = attDAO.getAttendanceByStudent(student.getId());
        int i = 1;
        for (Attendance a : records) {
            historyModel.addRow(new Object[]{
                i++, a.getDate(), a.getSubjectName(), a.getStatus(), a.getRemarks()
            });
        }
        if (records.isEmpty())
            JOptionPane.showMessageDialog(this, "No attendance records found for this student.");
    }
}
