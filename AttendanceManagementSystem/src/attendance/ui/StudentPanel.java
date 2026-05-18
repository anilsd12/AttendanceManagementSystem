package attendance.ui;

import attendance.dao.StudentDAO;
import attendance.model.Student;
import attendance.model.User;
import attendance.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentDAO dao = new StudentDAO();
    private final User currentUser;

    private JTable  table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtStudentId, txtName, txtEmail, txtPhone, txtDept;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private int selectedDbId = -1;

    private static final String[] COLS =
        {"#", "Student ID", "Full Name", "Email", "Phone", "Department"};

    public StudentPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        loadStudents();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        header.add(UITheme.headerLabel("Student Management"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Centre split: form left, table right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTable());
        split.setDividerLocation(310);
        split.setDividerSize(6);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);
    }

    // ── Form ─────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel p = UITheme.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(300, 0));

        JLabel title = new JLabel("Add / Edit Student");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(18));

        txtStudentId = addField(p, "Student ID *");
        txtName      = addField(p, "Full Name *");
        txtEmail     = addField(p, "Email");
        txtPhone     = addField(p, "Phone");
        txtDept      = addField(p, "Department");

        p.add(Box.createVerticalStrut(18));

        JPanel btns = new JPanel(new GridLayout(2, 2, 8, 8));
        btns.setOpaque(false);
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        btnAdd    = UITheme.primaryButton("Add");
        btnUpdate = UITheme.successButton("Update");
        btnDelete = UITheme.dangerButton("Delete");
        btnClear  = UITheme.secondaryButton("Clear");

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());

        btns.add(btnAdd); btns.add(btnUpdate);
        btns.add(btnDelete); btns.add(btnClear);
        p.add(btns);
        return p;
    }

    private JTextField addField(JPanel parent, String label) {
        JLabel l = UITheme.fieldLabel(label);
        l.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(l);
        parent.add(Box.createVerticalStrut(3));
        JTextField f = new JTextField();
        f.setFont(UITheme.FONT_BODY);
        f.setBorder(UITheme.fieldBorder());
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(f);
        parent.add(Box.createVerticalStrut(10));
        return f;
    }

    // ── Table ─────────────────────────────────────────────────
    private JPanel buildTable() {
        JPanel p = UITheme.card();
        p.setLayout(new BorderLayout(0, 10));

        // Search bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.setFont(UITheme.FONT_BODY);
        txtSearch.setBorder(UITheme.fieldBorder());
        txtSearch.setToolTipText("Search by ID, name or department");
        JButton btnSearch = UITheme.primaryButton("Search");
        JButton btnRefresh = UITheme.secondaryButton("Refresh");
        btnSearch.setPreferredSize(new Dimension(90, 32));
        btnRefresh.setPreferredSize(new Dimension(90, 32));
        btnSearch.addActionListener(e -> searchStudents());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadStudents(); });
        searchBar.add(txtSearch, BorderLayout.CENTER);
        JPanel searchBtns = new JPanel(new GridLayout(1, 2, 6, 0));
        searchBtns.setOpaque(false);
        searchBtns.add(btnSearch); searchBtns.add(btnRefresh);
        searchBar.add(searchBtns, BorderLayout.EAST);
        p.add(searchBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelect());
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // ── Data operations ──────────────────────────────────────
    private void loadStudents() { populate(dao.getAllStudents()); }

    private void searchStudents() {
        String kw = txtSearch.getText().trim();
        populate(kw.isEmpty() ? dao.getAllStudents() : dao.searchStudents(kw));
    }

    private void populate(List<Student> students) {
        tableModel.setRowCount(0);
        int i = 1;
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                i++, s.getStudentId(), s.getFullName(),
                s.getEmail(), s.getPhone(), s.getDepartment()
            });
        }
    }

    private void addStudent() {
        if (!validateForm()) return;
        Student s = buildFromForm();
        if (dao.addStudent(s)) {
            JOptionPane.showMessageDialog(this, "Student added successfully.");
            clearForm(); loadStudents();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student (duplicate ID?).",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (selectedDbId < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        if (!validateForm()) return;
        Student s = buildFromForm(); s.setId(selectedDbId);
        if (dao.updateStudent(s)) {
            JOptionPane.showMessageDialog(this, "Student updated successfully.");
            clearForm(); loadStudents();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        if (selectedDbId < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int res = JOptionPane.showConfirmDialog(this,
            "Delete this student? All attendance records will be removed.", "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION && dao.deleteStudent(selectedDbId)) {
            JOptionPane.showMessageDialog(this, "Student deleted.");
            clearForm(); loadStudents();
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String rollId = (String) tableModel.getValueAt(row, 1);
        List<Student> all = dao.getAllStudents();
        for (Student s : all) {
            if (s.getStudentId().equals(rollId)) {
                selectedDbId = s.getId();
                txtStudentId.setText(s.getStudentId());
                txtName.setText(s.getFullName());
                txtEmail.setText(s.getEmail() == null ? "" : s.getEmail());
                txtPhone.setText(s.getPhone() == null ? "" : s.getPhone());
                txtDept.setText(s.getDepartment() == null ? "" : s.getDepartment());
                break;
            }
        }
    }

    private boolean validateForm() {
        if (txtStudentId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Full Name are required.");
            return false;
        }
        return true;
    }

    private Student buildFromForm() {
        Student s = new Student();
        s.setStudentId(txtStudentId.getText().trim());
        s.setFullName(txtName.getText().trim());
        s.setEmail(txtEmail.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        s.setDepartment(txtDept.getText().trim());
        return s;
    }

    private void clearForm() {
        selectedDbId = -1;
        txtStudentId.setText(""); txtName.setText("");
        txtEmail.setText(""); txtPhone.setText(""); txtDept.setText("");
        table.clearSelection();
    }
}
