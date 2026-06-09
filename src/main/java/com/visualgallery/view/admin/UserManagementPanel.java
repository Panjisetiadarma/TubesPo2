package com.visualgallery.view.admin;

import com.visualgallery.controller.UserController;
import com.visualgallery.model.User;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * UserManagementPanel - Admin interface for managing users.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class UserManagementPanel extends JPanel {

    private final UserController userController;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public UserManagementPanel(UserController userController) {
        this.userController = userController;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ─── Header & Toolbar ───
        JPanel headerPanel = new JPanel(new BorderLayout(0, 16));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Manajemen User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        toolbar.setOpaque(false);

        searchField = UIUtils.createTextField("Cari username atau nama...");
        searchField.setPreferredSize(new Dimension(250, 36));
        
        JButton searchBtn = UIUtils.createSecondaryButton("Cari");
        searchBtn.addActionListener(e -> refreshData());

        JButton addBtn = UIUtils.createPrimaryButton("+ Tambah User");
        addBtn.addActionListener(e -> showAddUserDialog());

        toolbar.add(searchField);
        toolbar.add(searchBtn);
        toolbar.add(addBtn);

        headerPanel.add(toolbar, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Table ───
        String[] columns = {"ID", "Username", "Email", "Role", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(tableModel);
        UIUtils.styleTable(userTable);
        
        // Add a bit of padding to columns if possible or center them
        // For simplicity, styling is handled in UIUtils

        JScrollPane scrollPane = UIUtils.createTableScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // ─── Bottom Actions ───
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        JButton deleteBtn = UIUtils.createDangerButton("Hapus User");
        deleteBtn.addActionListener(e -> deleteSelectedUser());
        
        JButton deactBtn = UIUtils.createSecondaryButton("Nonaktifkan");
        deactBtn.addActionListener(e -> deactivateSelectedUser());

        bottomPanel.add(deactBtn);
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText().trim();
        List<User> users = keyword.isEmpty() ? userController.getAllUsers() : userController.searchUsers(keyword);
        
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole(),
                u.isActive() ? "Aktif" : "Nonaktif",
                "Pilih untuk aksi"
            });
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Tambah User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField userF = UIUtils.createTextField("Username");
        JTextField emailF = UIUtils.createTextField("Email");
        JTextField nameF = UIUtils.createTextField("Nama Lengkap");
        JPasswordField passF = UIUtils.createPasswordField("Password");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        p.add(new JLabel("Username")); p.add(userF); p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Email")); p.add(emailF); p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Nama Lengkap")); p.add(nameF); p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Password")); p.add(passF); p.add(Box.createVerticalStrut(10));
        p.add(new JLabel("Role")); p.add(roleCombo); p.add(Box.createVerticalStrut(20));

        JButton saveBtn = UIUtils.createPrimaryButton("Simpan");
        saveBtn.addActionListener(e -> {
            String err = userController.adminCreateUser(
                userF.getText(), emailF.getText(), new String(passF.getPassword()), 
                nameF.getText(), (String)roleCombo.getSelectedItem());
            if (err == null) {
                UIUtils.showSuccess(this, "User dibuat.");
                dialog.dispose();
                refreshData();
            } else {
                UIUtils.showError(this, err);
            }
        });
        p.add(saveBtn);
        
        dialog.add(p);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarning(this, "Pilih user terlebih dahulu.");
            return;
        }
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        if (UIUtils.showConfirm(this, "Hapus permanen user " + username + "?")) {
            if (userController.deleteUser(userId)) {
                UIUtils.showSuccess(this, "User dihapus.");
                refreshData();
            } else {
                UIUtils.showError(this, "Gagal menghapus user.");
            }
        }
    }

    private void deactivateSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarning(this, "Pilih user terlebih dahulu.");
            return;
        }
        int userId = (int) tableModel.getValueAt(row, 0);
        if (userController.deactivateUser(userId)) {
            UIUtils.showSuccess(this, "User dinonaktifkan.");
            refreshData();
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) refreshData();
    }
}
