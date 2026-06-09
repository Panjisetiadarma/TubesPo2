package com.visualgallery.view.auth;

import com.visualgallery.controller.AuthController;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame - The login screen for Visual Gallery.
 *
 * VSCO-inspired minimalist design with dark/light mode support.
 * Features email/password form, Remember Me, and register navigation.
 *
 * OOP: MVC View, Encapsulation
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class LoginFrame extends JFrame {

    private final AuthController authController;

    // Components
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JCheckBox      rememberMeCheckbox;
    private JButton        loginButton;
    private JLabel         errorLabel;
    private JLabel         statusLabel;

    public LoginFrame(AuthController authController) {
        this.authController = authController;
        initFrame();
        buildUI();
        prefillRememberMe();
    }

    private void initFrame() {
        setTitle("Visual Gallery — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setResizable(true);
        try {
            ImageIcon icon = UIUtils.loadIcon("icons/app_icon.png", 32);
            if (icon != null) setIconImage(icon.getImage());
        } catch (Exception ignored) { }
    }

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(tm.getBackground());
        setContentPane(root);

        // ─────────────────────────────────────────────────────────
        // LEFT PANEL — Branding / Illustration
        // ─────────────────────────────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(0x111111));
        leftPanel.setPreferredSize(new Dimension(400, 0));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(60, 50, 60, 50));

        // Logo text
        JLabel logoLabel = new JLabel("Visual Gallery");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logoLabel.setForeground(ThemeManager.ACCENT);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tagline
        JLabel taglineLabel = new JLabel("<html>Capture moments.<br>Share your vision.<br>Inspire the world.</html>");
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        taglineLabel.setForeground(new Color(0xAAAAAA));
        taglineLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        taglineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Decorative accent line
        JPanel accentLine = new JPanel();
        accentLine.setBackground(ThemeManager.ACCENT);
        accentLine.setPreferredSize(new Dimension(60, 3));
        accentLine.setMaximumSize(new Dimension(60, 3));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        accentLine.setBorder(new EmptyBorder(0, 0, 0, 0));

        brandPanel.add(logoLabel);
        brandPanel.add(Box.createVerticalStrut(16));
        brandPanel.add(accentLine);
        brandPanel.add(taglineLabel);

        // Bottom version
        JLabel versionLabel = new JLabel("v1.0.0 · Tugas Besar PO2");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(0x555555));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandPanel.add(Box.createVerticalGlue());
        brandPanel.add(versionLabel);

        leftPanel.add(brandPanel, BorderLayout.CENTER);

        // ─────────────────────────────────────────────────────────
        // RIGHT PANEL — Login Form
        // ─────────────────────────────────────────────────────────
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(tm.getBackground());
        rightPanel.setBorder(new EmptyBorder(0, 40, 0, 40));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));

        // ─── Form Title ───
        JLabel titleLabel = new JLabel("Masuk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Selamat datang kembali di Visual Gallery");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(tm.getTextMuted());
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(32));

        // ─── Email ───
        JLabel emailLabel = createFieldLabel("Email");
        emailField = UIUtils.createTextField("Masukkan email Anda");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(16));

        // ─── Password ───
        JLabel passLabel = createFieldLabel("Password");
        passwordField = UIUtils.createPasswordField("Masukkan password Anda");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));

        // ─── Remember Me ───
        rememberMeCheckbox = new JCheckBox("Ingat saya");
        rememberMeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheckbox.setOpaque(false);
        rememberMeCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(rememberMeCheckbox);
        formPanel.add(Box.createVerticalStrut(20));

        // ─── Error Label ───
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ThemeManager.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(8));

        // ─── Login Button ───
        loginButton = UIUtils.createPrimaryButton("Masuk");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.addActionListener(e -> performLogin());
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(20));

        // ─── Register Link ───
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerPanel.setOpaque(false);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel registerPrompt = new JLabel("Belum punya akun? ");
        registerPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel registerLink = new JLabel("Daftar sekarang");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(ThemeManager.ACCENT);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openRegisterFrame(); }
            @Override public void mouseEntered(MouseEvent e) { registerLink.setForeground(ThemeManager.ACCENT_LIGHT); }
            @Override public void mouseExited(MouseEvent e)  { registerLink.setForeground(ThemeManager.ACCENT); }
        });

        registerPanel.add(registerPrompt);
        registerPanel.add(registerLink);
        formPanel.add(registerPanel);

        // ─── Status Label ───
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(tm.getTextMuted());
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(statusLabel);

        rightPanel.add(formPanel);

        // ─── Divider ───
        JSeparator divider = new JSeparator(JSeparator.VERTICAL);
        divider.setForeground(new Color(0x2A2A2A));
        divider.setPreferredSize(new Dimension(1, Integer.MAX_VALUE));

        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        // Enter key triggers login
        getRootPane().setDefaultButton(loginButton);

        // Keyboard shortcut
        passwordField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void performLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        boolean remember = rememberMeCheckbox.isSelected();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email dan password tidak boleh kosong.");
            return;
        }

        setLoading(true);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return authController.login(email, password, remember, LoginFrame.this);
            }

            @Override
            protected void done() {
                try {
                    String errorMsg = get();
                    if (errorMsg != null) {
                        showError(errorMsg);
                    }
                    // On success, LoginFrame is disposed by AuthController
                } catch (Exception ex) {
                    showError("Terjadi kesalahan. Periksa koneksi database.");
                } finally {
                    setLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(ThemeManager.ERROR);
    }

    private void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        loginButton.setText(loading ? "Sedang masuk..." : "Masuk");
        statusLabel.setText(loading ? "Memverifikasi kredensial..." : " ");
        emailField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
    }

    private void openRegisterFrame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            RegisterFrame rf = new RegisterFrame(authController);
            rf.setVisible(true);
        });
    }

    private void prefillRememberMe() {
        String rememberedEmail = authController.getRememberedEmail();
        if (rememberedEmail != null) {
            emailField.setText(rememberedEmail);
            rememberMeCheckbox.setSelected(true);
            passwordField.requestFocusInWindow();
        }
    }
}
