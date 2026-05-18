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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

/**
 * Panel for marking daily attendance for all students in a subject.
 */
public class MarkAttendancePanel extends JPanel {

    private final User           currentUser;
    private final AttendanceDAO  attDAO     = new AttendanceDAO();
    private final StudentDAO     studentDAO = new StudentDAO();
    private final SubjectDAO     subjectDAO = new SubjectDAO();

    private JComboBox<Subject>   cmbSubject;
    private JSpinner             spinDate;
    private JTable               table;
    private DefaultTableModel    tableModel;
    private JLabel               lblStatus;

    private static final String[] COLS =
        {"#", "Student ID", "Student Name", "Department", "Status", "Remarks"};
    private static final String[] STATUSES = {"PRESENT", "ABSENT", "LATE"};

    public MarkAttendancePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        header.add(UITheme.headerLabel("Mark Attendance"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Controls card
        JPanel controls = UITheme.card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 8));

        controls.add(new JLabel("Subject:"));
        cmbSubject = new JComboBox<>();
        cmbSubject.setFont(UITheme.FONT_BODY);
        cmbSubject.setPreferredSize(new Dimension(260, 34));
        loadSubjects();
        controls.add(cmbSubject);

        controls.add(new JLabel("Date:"));
        SpinnerDateModel sdm = new SpinnerDateModel();
        spinDate = new JSpinner(sdm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinDate, "yyyy-MM-dd");
        spinDate.setEditor(de);
        spinDate.setFont(UITheme.FONT_BODY);
        spinDate.setPreferredSize(new Dimension(130, 34));
        controls.add(spinDate);

        JButton btnLoad = UITheme.primaryButton("Load Students");
        btnLoad.addActionListener(e -> loadStudents());
        controls.add(btnLoad);

        JButton btnAll = UITheme.successButton("Mark All Present");
        btnAll.addActionListener(e -> markAll("PRESENT"));
        controls.add(btnAll);

        JButton btnAllAbs = UITheme.dangerButton("Mark All Absent");
        btnAllAbs.addActionListener(e -> markAll("ABSENT"));
        controls.add(btnAllAbs);

        add(controls, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 4 || c == 5; // Status & Remarks editable
            }
            @Override public Class<?> getColumnClass(int c) {
                return c == 4 ? String.class : Object.class;
            }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Status column: ComboBox editor
        JComboBox<String> statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(UITheme.FONT_BODY);
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusCombo));

        // Status column renderer – colour-coded
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = new JLabel(val == null ? "" : val.toString(), SwingConstants.CENTER);
                l.setOpaque(true);
                l.setFont(UITheme.FONT_BODY);
                String v = val == null ? "" : val.toString();
                if ("PRESENT".equals(v)) { l.setBackground(new Color(212, 245, 220)); l.setForeground(new Color(30,130,60)); }
                else if ("ABSENT".equals(v))  { l.setBackground(new Color(255, 218, 218)); l.setForeground(new Color(160,20,20)); }
                else if ("LATE".equals(v))    { l.setBackground(new Color(255, 243, 205)); l.setForeground(new Color(130,100,0)); }
                else                          { l.setBackground(sel ? UITheme.TABLE_ALT_ROW : Color.WHITE); l.setForeground(UITheme.TEXT_PRIMARY); }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottom = new JPanel(new BorderLayout(14, 0));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.FONT_BODY);
        lblStatus.setForeground(UITheme.TEXT_MUTED);

        JButton btnSave = UITheme.primaryButton("Save Attendance");
        btnSave.setPreferredSize(new Dimension(180, 40));
        btnSave.addActionListener(e -> saveAttendance());

        bottom.add(lblStatus, BorderLayout.CENTER);
        bottom.add(btnSave, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadSubjects() {
        cmbSubject.removeAllItems();
        for (Subject s : subjectDAO.getAllSubjects()) cmbSubject.addItem(s);
    }

    private void loadStudents() {
        Subject subject = (Subject) cmbSubject.getSelectedItem();
        if (subject == null) { JOptionPane.showMessageDialog(this, "No subjects found."); return; }

        Date date = new Date(((java.util.Date) spinDate.getValue()).getTime());

        // Get any existing records for this date+subject
        List<Attendance> existing = attDAO.getAttendanceBySubjectAndDate(subject.getId(), date);

        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        int i = 1;
        for (Student s : students) {
            String status = "PRESENT"; // default
            for (Attendance a : existing) {
                if (a.getStudentId() == s.getId()) { status = a.getStatus(); break; }
            }
            tableModel.addRow(new Object[]{
                i++, s.getStudentId(), s.getFullName(), s.getDepartment(), status, ""
            });
        }
        lblStatus.setText(students.size() + " students loaded for " + date);
    }

    private void markAll(String status) {
        if (tableModel.getRowCount() == 0) return;
        // Stop any active editing first
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        for (int r = 0; r < tableModel.getRowCount(); r++)
            tableModel.setValueAt(status, r, 4);
    }

    private void saveAttendance() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();

        Subject subject = (Subject) cmbSubject.getSelectedItem();
        if (subject == null || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Load students first."); return;
        }
        Date date = new Date(((java.util.Date) spinDate.getValue()).getTime());

        List<Student> students = studentDAO.getAllStudents();
        int saved = 0, failed = 0;

        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String rollId  = (String) tableModel.getValueAt(r, 1);
            String status  = (String) tableModel.getValueAt(r, 4);
            String remarks = tableModel.getValueAt(r, 5) == null ? "" :
                             tableModel.getValueAt(r, 5).toString();

            // Resolve student DB id
            int studentDbId = -1;
            for (Student s : students) {
                if (s.getStudentId().equals(rollId)) { studentDbId = s.getId(); break; }
            }
            if (studentDbId < 0) { failed++; continue; }

            Attendance a = new Attendance();
            a.setStudentId(studentDbId);
            a.setSubjectId(subject.getId());
            a.setDate(date);
            a.setStatus(status == null ? "ABSENT" : status);
            a.setMarkedBy(currentUser.getId());
            a.setRemarks(remarks);

            if (attDAO.markAttendance(a)) saved++; else failed++;
        }

        lblStatus.setText("Saved: " + saved + (failed > 0 ? "  |  Failed: " + failed : ""));
        JOptionPane.showMessageDialog(this,
            "Attendance saved for " + saved + " student(s).\n" +
            (failed > 0 ? failed + " record(s) failed." : ""),
            "Attendance Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}
