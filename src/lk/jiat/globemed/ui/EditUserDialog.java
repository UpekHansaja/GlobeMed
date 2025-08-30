package lk.jiat.globemed.ui;

import lk.jiat.globemed.dao.RoleDao;
import lk.jiat.globemed.model.Role;
import lk.jiat.globemed.model.Staff;

import javax.swing.*;
import java.awt.*;

public class EditUserDialog extends JDialog {

    private Staff editedStaff;
    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSave;
    private JButton btnCancel;

    public EditUserDialog(Frame parent, Staff toEdit) {
        super(parent, "Edit User", true);
        this.editedStaff = toEdit;
        initComponents();
        loadRoles();
        populateFields();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(400, 260);
        setLayout(new BorderLayout(8, 8));
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblName = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(lblName, gbc);
        txtName = new JTextField(22);
        gbc.gridx = 1;
        gbc.gridy = 0;
        p.add(txtName, gbc);

        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        p.add(lblEmail, gbc);
        txtEmail = new JTextField(22);
        txtEmail.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        p.add(txtEmail, gbc);

        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(lblPassword, gbc);
        txtPassword = new JPasswordField(22);
        gbc.gridx = 1;
        gbc.gridy = 2;
        p.add(txtPassword, gbc);

        JLabel lblRole = new JLabel("Role:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        p.add(lblRole, gbc);
        cmbRole = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 3;
        p.add(cmbRole, gbc);

        add(p, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");
        buttons.add(btnSave);
        buttons.add(btnCancel);
        add(buttons, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> onCancel());
    }

    private void loadRoles() {
        RoleDao dao = new RoleDao();
        try {
            java.util.List<Role> roles = dao.findAll();
            cmbRole.removeAllItems();
            for (Role r : roles) {
                cmbRole.addItem(r.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed loading roles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields() {
        if (editedStaff == null) {
            return;
        }
        txtName.setText(editedStaff.getName());
        txtEmail.setText(editedStaff.getEmail());
        txtPassword.setText(editedStaff.getPassword());
        if (editedStaff.getRole() != null) {
            cmbRole.setSelectedItem(editedStaff.getRole().getName());
        }
    }

    private void onSave() {
        String name = txtName.getText().trim();
        String pwd = new String(txtPassword.getPassword());
        String roleName = (String) cmbRole.getSelectedItem();

        if (name.isEmpty() || roleName == null) {
            JOptionPane.showMessageDialog(this, "Please fill required fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editedStaff.setName(name);
        if (!pwd.isEmpty()) {
            editedStaff.setPassword(pwd);
        }
        RoleDao rdao = new RoleDao();
        Role role = rdao.findByName(roleName);
        editedStaff.setRole(role);

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public Staff getEditedStaff() {
        return editedStaff;
    }
}
