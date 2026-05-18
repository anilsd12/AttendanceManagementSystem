package attendance.ui;

import attendance.dao.SubjectDAO;
import attendance.model.Subject;
import attendance.model.User;
import attendance.util.UITheme;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SubjectPanel extends JPanel {

    private final SubjectDAO dao = new SubjectDAO();
    private final User currentUser;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCode, txtName, txtDept;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private int selectedDbId = -1;

    private static final String[] COLS = {"#", "Subject Code", "Subject Name", "Department"};

    public SubjectPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        loadSubjects();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        header.add(UITheme.headerLabel("Subject Management"), BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTable());
        split.setDividerLocation(300);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel p = UITheme.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(290, 0));

        JLabel title = new JLabel("Add / Edit Subject");
        title.setFont(UITheme.FONT_HEADER);
        title.setAlignmentX(LEFT_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(18));

        txtCode = addField(p, "Subject Code *");
        txtName = addField(p, "Subject Name *");
        txtDept = addField(p, "Department");

        p.add(Box.createVerticalStrut(18));

        JPanel btns = new JPanel(new GridLayout(2, 2, 8, 8));
        btns.setOpaque(false);
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        btnAdd    = UITheme.primaryButton("Add");
        btnUpdate = UITheme.successButton("Update");
        btnDelete = UITheme.dangerButton("Delete");
        btnClear  = UITheme.secondaryButton("Clear");
        btnAdd.addActionListener(e -> addSubject());
        btnUpdate.addActionListener(e -> updateSubject());
        btnDelete.addActionListener(e -> deleteSubject());
        btnClear.addActionListener(e -> clearForm());
        btns.add(btnAdd); btns.add(btnUpdate); btns.add(btnDelete); btns.add(btnClear);
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

    private JPanel buildTable() {
        JPanel p = UITheme.card();
        p.setLayout(new BorderLayout(0, 10));

        JButton btnRefresh = UITheme.secondaryButton("Refresh");
        btnRefresh.addActionListener(e -> loadSubjects());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        top.setOpaque(false);
        top.add(btnRefresh);
        p.add(top, BorderLayout.NORTH);

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

    private void loadSubjects() { populate(dao.getAllSubjects()); }

    private void populate(List<Subject> subjects) {
        tableModel.setRowCount(0);
        int i = 1;
        for (Subject s : subjects)
            tableModel.addRow(new Object[]{i++, s.getSubjectCode(), s.getSubjectName(), s.getDepartment()});
    }

    private void addSubject() {
        if (!validateForm()) return;
        Subject s = fromForm();
        if (dao.addSubject(s)) { JOptionPane.showMessageDialog(this, "Subject added."); clearForm(); loadSubjects(); }
        else JOptionPane.showMessageDialog(this, "Failed (duplicate code?).", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateSubject() {
        if (selectedDbId < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        if (!validateForm()) return;
        Subject s = fromForm(); s.setId(selectedDbId);
        if (dao.updateSubject(s)) { JOptionPane.showMessageDialog(this, "Subject updated."); clearForm(); loadSubjects(); }
        else JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteSubject() {
        if (selectedDbId < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int res = JOptionPane.showConfirmDialog(this, "Delete this subject?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION && dao.deleteSubject(selectedDbId)) {
            JOptionPane.showMessageDialog(this, "Subject deleted."); clearForm(); loadSubjects();
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String code = (String) tableModel.getValueAt(row, 1);
        for (Subject s : dao.getAllSubjects()) {
            if (s.getSubjectCode().equals(code)) {
                selectedDbId = s.getId();
                txtCode.setText(s.getSubjectCode());
                txtName.setText(s.getSubjectName());
                txtDept.setText(s.getDepartment() == null ? "" : s.getDepartment());
                break;
            }
        }
    }

    public boolean validateForm() {
        if (txtCode.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Code and Name are required."); return false;
        }
        return true;
    }

    private Subject fromForm() {
        Subject s = new Subject();
        s.setSubjectCode(txtCode.getText().trim());
        s.setSubjectName(txtName.getText().trim());
        s.setDepartment(txtDept.getText().trim());
        return s;
    }

    private void clearForm() {
        selectedDbId = -1;
        txtCode.setText(""); txtName.setText(""); txtDept.setText("");
        table.clearSelection();
    }
}
