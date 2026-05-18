package attendance.ui;

import attendance.dao.UserDAO;
import attendance.model.User;
import attendance.util.UITheme;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserPanel extends JPanel {

    private final UserDAO  dao = new UserDAO();
    private final User     currentUser;

    private JTable           table;
    private DefaultTableModel tableModel;
    private JTextField       txtUsername, txtPassword, txtFullName;
    private JComboBox<String> cmbRole;
    private JButton          btnAdd, btnUpdate, btnDelete, btnClear;
    private int              selectedDbId = -1;

    private static final String[] COLS = {"#", "Username", "Full Name", "Role"};

    public UserPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        loadUsers();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        header.add(UITheme.headerLabel("User Management"), BorderLayout.WEST);
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

        JLabel title = new JLabel("Add / Edit User");
        title.setFont(UITheme.FONT_HEADER);
        title.setAlignmentX(LEFT_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(18));

        txtUsername = addField(p, "Username *");
        txtPassword = addField(p, "Password *");
        txtFullName = addField(p, "Full Name *");

        JLabel rl = UITheme.fieldLabel("Role");
        rl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(rl);
        p.add(Box.createVerticalStrut(3));
        cmbRole = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT"});
        cmbRole.setFont(UITheme.FONT_BODY);
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbRole.setAlignmentX(LEFT_ALIGNMENT);
        p.add(cmbRole);
        p.add(Box.createVerticalStrut(18));

        JPanel btns = new JPanel(new GridLayout(2, 2, 8, 8));
        btns.setOpaque(false);
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        btnAdd    = UITheme.primaryButton("Add");
        btnUpdate = UITheme.successButton("Update");
        btnDelete = UITheme.dangerButton("Delete");
        btnClear  = UITheme.secondaryButton("Clear");
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
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
        JTextField f = label.contains("Password") ? new JPasswordField() : new JTextField();
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
        btnRefresh.addActionListener(e -> loadUsers());
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

    private void loadUsers() {
        tableModel.setRowCount(0);
        int i = 1;
        for (User u : dao.getAllUsers())
            tableModel.addRow(new Object[]{i++, u.getUsername(), u.getFullName(), u.getRole()});
    }

    private void addUser() {
        if (!validateForm()) return;
        User u = fromForm();
        if (dao.addUser(u)) { JOptionPane.showMessageDialog(this, "User added."); clearForm(); loadUsers(); }
        else JOptionPane.showMessageDialog(this, "Failed (duplicate username?).", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateUser() {
        if (selectedDbId < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        if (!validateForm()) return;
        User u = fromForm(); u.setId(selectedDbId);
        if (dao.updateUser(u)) { JOptionPane.showMessageDialog(this, "User updated."); clearForm(); loadUsers(); }
        else JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void deleteUser() {
        if (selectedDbId < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        if (selectedDbId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "Cannot delete your own account."); return;
        }
        int res = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION && dao.deleteUser(selectedDbId)) {
            JOptionPane.showMessageDialog(this, "User deleted."); clearForm(); loadUsers();
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String uname = (String) tableModel.getValueAt(row, 1);
        for (User u : dao.getAllUsers()) {
            if (u.getUsername().equals(uname)) {
                selectedDbId = u.getId();
                txtUsername.setText(u.getUsername());
                txtPassword.setText("");
                txtFullName.setText(u.getFullName());
                cmbRole.setSelectedItem(u.getRole());
                break;
            }
        }
    }

    public boolean validateForm() {
        if (txtUsername.getText().trim().isEmpty() || txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Full Name are required."); return false;
        }
        return true;
    }

    private User fromForm() {
        User u = new User();
        u.setUsername(txtUsername.getText().trim());
        u.setPassword(txtPassword.getText().trim());
        u.setFullName(txtFullName.getText().trim());
        u.setRole((String) cmbRole.getSelectedItem());
        return u;
    }

    private void clearForm() {
        selectedDbId = -1;
        txtUsername.setText(""); txtPassword.setText(""); txtFullName.setText("");
        cmbRole.setSelectedIndex(0);
        table.clearSelection();
    }
}
