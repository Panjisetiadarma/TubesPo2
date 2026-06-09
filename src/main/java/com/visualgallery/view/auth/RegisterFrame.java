package com.visualgallery.view.auth;

import com.visualgallery.controller.AuthController;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * RegisterFrame - Registration screen for Visual Gallery.
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class RegisterFrame extends JFrame {

    private final AuthController authController;

    private JTextField     usernameField;
    private JTextField     emailField;
    private JTextField     fullNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton        registerButton;
    private JLabel         errorLabel;

    public RegisterFrame(AuthController authController) {
        this.authController = authController;
        initFrame();
        buildUI();
    }

    private void initFrame() {
        setTitle("Visual Gallery — Daftar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setMinimumSize(new Dimension(800, 560));
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(tm.getBackground());
        setContentPane(root);

        // ─────────────────────────────────────────────────────────
        // LEFT PANEL — Branding
        // ─────────────────────────────────────────────────────────
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(0x111111));
        leftPanel.setPreferredSize(new Dimension(380, 0));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(60, 50, 60, 50));

        JLabel logoLabel = new JLabel("Visual Gallery");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(ThemeManager.ACCENT);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentLine = new JPanel();
        accentLine.setBackground(ThemeManager.ACCENT);
        accentLine.setMaximumSize(new Dimension(60, 3));
        accentLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><br>Bergabunglah dengan ribuan<br>fotografer dan videografer<br>berbakat dari seluruh dunia.</html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(0xAAAAAA));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] features = {
            "✓  Upload foto & video dengan mudah",
            "✓  Explore karya fotografer lainnya",
            "✓  Like dan komentar postingan",
            "✓  Kelola galeri pribadi Anda"
        };

        brandPanel.add(logoLabel);
        brandPanel.add(Box.createVerticalStrut(12));
        brandPanel.add(accentLine);
        brandPanel.add(tagline);
        brandPanel.add(Box.createVerticalStrut(24));

        for (String feature : features) {
            JLabel fl = new JLabel(feature);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            fl.setForeground(new Color(0x888888));
            fl.setAlignmentX(Component.LEFT_ALIGNMENT);
            fl.setBorder(new EmptyBorder(3, 0, 3, 0));
            brandPanel.add(fl);
        }

        leftPanel.add(brandPanel, BorderLayout.CENTER);

        // ─────────────────────────────────────────────────────────
        // RIGHT PANEL — Register Form
        // ─────────────────────────────────────────────────────────
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(tm.getBackground());
        rightPanel.setBorder(new EmptyBorder(0, 40, 0, 40));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(360, Integer.MAX_VALUE));

        JLabel titleLabel = new JLabel("Buat Akun");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Daftar gratis untuk mulai berbagi karya");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(tm.getTextMuted());
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(24));

        // ─── Form Fields ───
        addFormRow(formPanel, "Nama Lengkap", fullNameField   = UIUtils.createTextField("Nama lengkap Anda"));
        addFormRow(formPanel, "Username",     usernameField   = UIUtils.createTextField("Buat username unik"));
        addFormRow(formPanel, "Email",        emailField      = UIUtils.createTextField("Alamat email aktif"));
        addFormRow(formPanel, "Password",     passwordField   = UIUtils.createPasswordField("Min. 8 karakter"));
        addFormRow(formPanel, "Konfirmasi Password", confirmPasswordField = UIUtils.createPasswordField("Ulangi password Anda"));

        // ─── Error Label ───
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errorLabel.setForeground(ThemeManager.ERROR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(8));

        // ─── Register Button ───
        registerButton = UIUtils.createPrimaryButton("Daftar Sekarang");
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.addActionListener(e -> performRegister());
        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(16));

        // ─── Login Link ───
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginLinkPanel.setOpaque(false);
        loginLinkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prompt = new JLabel("Sudah punya akun? ");
        prompt.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel loginLink = new JLabel("Masuk");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(ThemeManager.ACCENT);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openLoginFrame(); }
            @Override public void mouseEntered(MouseEvent e) { loginLink.setForeground(ThemeManager.ACCENT_LIGHT); }
            @Override public void mouseExited(MouseEvent e)  { loginLink.setForeground(ThemeManager.ACCENT); }
        });

        loginLinkPanel.add(prompt);
        loginLinkPanel.add(loginLink);
        formPanel.add(loginLinkPanel);

        rightPanel.add(formPanel);

        root.add(leftPanel, BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        getRootPane().setDefaultButton(registerButton);
    }

    private void addFormRow(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(4));
        parent.add(field);
        parent.add(Box.createVerticalStrut(12));
    }

    private void performRegister() {
        String username   = usernameField.getText().trim();
        String email      = emailField.getText().trim();
        String fullName   = fullNameField.getText().trim();
        String password   = new String(passwordField.getPassword());
        String confirm    = new String(confirmPasswordField.getPassword());

        setLoading(true);
        errorLabel.setText(" ");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return authController.register(username, email, password, confirm, fullName);
            }

            @Override
            protected void done() {
                try {
                    String errorMsg = get();
                    if (errorMsg == null) {
                        // Success
                        UIUtils.showSuccess(RegisterFrame.this,
                            "Akun berhasil dibuat!\nSilakan login dengan email dan password Anda.");
                        openLoginFrame();
                    } else {
                        errorLabel.setText(errorMsg);
                    }
                } catch (Exception ex) {
                    errorLabel.setText("Terjadi kesalahan. Periksa koneksi database.");
                } finally {
                    setLoading(false);
                }
            }
        };
        worker.execute();
    }

    private void setLoading(boolean loading) {
        registerButton.setEnabled(!loading);
        registerButton.setText(loading ? "Mendaftar..." : "Daftar Sekarang");
        usernameField.setEnabled(!loading);
        emailField.setEnabled(!loading);
        fullNameField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        confirmPasswordField.setEnabled(!loading);
    }

    private void openLoginFrame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame(authController);
            lf.setVisible(true);
        });
    }
}
