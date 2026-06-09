package com.visualgallery.view.settings;

import com.visualgallery.controller.UserController;
import com.visualgallery.model.Account;
import com.visualgallery.model.User;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * SettingsPanel - User settings (Profile, Password, Theme).
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class SettingsPanel extends JPanel {

    private final Account currentUser;
    private final UserController userController;
    private final ThemeManager tm;
    private final Runnable onThemeChanged;

    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextArea  bioArea;
    
    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;

    public SettingsPanel(Account currentUser, UserController userController, 
                         ThemeManager tm, Runnable onThemeChanged) {
        this.currentUser = currentUser;
        this.userController = userController;
        this.tm = tm;
        this.onThemeChanged = onThemeChanged;
        buildUI();
        loadProfileData();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Pengaturan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BOLD);
        tabbedPane.setFocusable(false);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        tabbedPane.addTab("Profil", buildProfileTab());
        tabbedPane.addTab("Keamanan", buildSecurityTab());
        tabbedPane.addTab("Tampilan", buildAppearanceTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buildProfileTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Profile Picture update
        JPanel picPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        picPanel.setOpaque(false);
        picPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel avatarLabel = new JLabel(UIUtils.createCircularIcon(currentUser.getProfilePicture(), 80));
        JButton changePicBtn = UIUtils.createSecondaryButton("Ubah Foto Profil");
        changePicBtn.addActionListener(e -> updateProfilePicture(avatarLabel));
        
        picPanel.add(avatarLabel);
        picPanel.add(changePicBtn);
        panel.add(picPanel);
        panel.add(Box.createVerticalStrut(20));

        // Form Fields
        usernameField = UIUtils.createTextField("Username");
        emailField    = UIUtils.createTextField("Email");
        fullNameField = UIUtils.createTextField("Nama Lengkap");
        bioArea       = UIUtils.createTextArea("Bio singkat", 3, 20);

        addFormRow(panel, "Username", usernameField);
        addFormRow(panel, "Email", emailField);
        addFormRow(panel, "Nama Lengkap", fullNameField);
        
        JLabel bioLabel = new JLabel("Bio");
        bioLabel.setFont(UIUtils.FONT_BOLD);
        bioLabel.setForeground(tm.getTextPrimary());
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(bioArea);
        scrollPane.setMaximumSize(new Dimension(400, 80));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(bioLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(20));

        JButton saveBtn = UIUtils.createPrimaryButton("Simpan Profil");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveProfile());
        panel.add(saveBtn);

        // Wrapper to keep it top-aligned
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildSecurityTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        oldPassField     = UIUtils.createPasswordField("Password lama");
        newPassField     = UIUtils.createPasswordField("Password baru");
        confirmPassField = UIUtils.createPasswordField("Konfirmasi password baru");

        addFormRow(panel, "Password Lama", oldPassField);
        addFormRow(panel, "Password Baru", newPassField);
        addFormRow(panel, "Konfirmasi Password", confirmPassField);

        JButton saveBtn = UIUtils.createPrimaryButton("Ubah Password");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> changePassword());
        panel.add(saveBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildAppearanceTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel modeLabel = new JLabel("Mode Tampilan");
        modeLabel.setFont(UIUtils.FONT_BOLD);
        modeLabel.setForeground(tm.getTextPrimary());
        modeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(modeLabel);
        panel.add(Box.createVerticalStrut(10));

        JCheckBox darkThemeCheck = new JCheckBox("Dark Mode (Tema Gelap)");
        darkThemeCheck.setFont(UIUtils.FONT_BODY);
        darkThemeCheck.setForeground(tm.getTextPrimary());
        darkThemeCheck.setOpaque(false);
        darkThemeCheck.setSelected(tm.isDarkMode());
        darkThemeCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        darkThemeCheck.addActionListener(e -> {
            tm.setDarkMode(darkThemeCheck.isSelected());
            if (onThemeChanged != null) onThemeChanged.run();
        });

        panel.add(darkThemeCheck);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panel, BorderLayout.NORTH);
        return wrapper;
    }

    private void addFormRow(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.FONT_BOLD);
        label.setForeground(tm.getTextPrimary());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(400, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(label);
        parent.add(Box.createVerticalStrut(5));
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
    }

    private void loadProfileData() {
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
        if (currentUser instanceof User u) {
            fullNameField.setText(u.getFullName());
        }
        bioArea.setText(currentUser.getBio());
    }

    private void saveProfile() {
        String err = userController.updateProfile(
            usernameField.getText(), emailField.getText(), 
            fullNameField.getText(), bioArea.getText());
        if (err == null) {
            UIUtils.showSuccess(this, "Profil berhasil diperbarui.");
        } else {
            UIUtils.showError(this, err);
        }
    }

    private void changePassword() {
        String oldP = new String(oldPassField.getPassword());
        String newP = new String(newPassField.getPassword());
        String conf = new String(confirmPassField.getPassword());
        
        if (oldP.isEmpty() || newP.isEmpty()) {
            UIUtils.showError(this, "Semua field harus diisi.");
            return;
        }

        String err = userController.changePassword(oldP, newP, conf);
        if (err == null) {
            UIUtils.showSuccess(this, "Password berhasil diubah.");
            oldPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
        } else {
            UIUtils.showError(this, err);
        }
    }

    private void updateProfilePicture(JLabel avatarLabel) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "webp"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String err = userController.updateProfilePicture(file);
            if (err == null) {
                // Update avatar immediately
                User u = userController.getCurrentUser();
                if (u != null) {
                    avatarLabel.setIcon(UIUtils.createCircularIcon(u.getProfilePicture(), 80));
                    UIUtils.showSuccess(this, "Foto profil diperbarui.");
                }
            } else {
                UIUtils.showError(this, err);
            }
        }
    }
}
