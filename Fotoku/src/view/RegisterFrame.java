package view;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utils.DatabaseConnection;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        // --- 1. Pengaturan Fullscreen Windows ---
        setTitle("Fotoku - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Mengisi seluruh layar
        setUndecorated(false); 

        // Menggunakan Custom Panel untuk Background Putih Bersih
        WhiteBackgroundPanel mainPanel = new WhiteBackgroundPanel();
        mainPanel.setLayout(new GridBagLayout()); 

        // Container pembungkus seluruh komponen register
        JPanel containerPanel = new JPanel();
        containerPanel.setOpaque(false);
        containerPanel.setPreferredSize(new Dimension(400, 650));
        containerPanel.setLayout(null);

        // --- 2. Komponen Header (Teks Hitam) ---
        JLabel logoLabel = new JLabel("Fotoku", SwingConstants.CENTER);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        logoLabel.setForeground(Color.BLACK);
        logoLabel.setBounds(100, 20, 200, 50);
        containerPanel.add(logoLabel);

        JLabel taglineLabel = new JLabel("create an account to share your vibe.", SwingConstants.CENTER);
        taglineLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        taglineLabel.setForeground(new Color(100, 100, 100)); // Abu-abu gelap
        taglineLabel.setBounds(50, 75, 300, 20);
        containerPanel.add(taglineLabel);

        // --- 3. Panel Form (Kotak Transparan Terang / Card) ---
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card putih lembut
                g2d.setColor(new Color(245, 245, 245, 220)); 
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(null);
        formPanel.setBounds(40, 120, 320, 440);

        // Label Header Form
        JLabel joinLabel = new JLabel("Join the community.", SwingConstants.CENTER);
        joinLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        joinLabel.setForeground(Color.BLACK);
        joinLabel.setBounds(20, 20, 280, 25);
        formPanel.add(joinLabel);

        // Input Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        userLabel.setForeground(new Color(80, 80, 80));
        userLabel.setBounds(30, 65, 260, 15);
        formPanel.add(userLabel);

        JTextField txtUsername = new RoundedTextField(15);
        txtUsername.setBounds(25, 85, 270, 35);
        formPanel.add(txtUsername);

        // Input Email
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        emailLabel.setForeground(new Color(80, 80, 80));
        emailLabel.setBounds(30, 135, 260, 15);
        formPanel.add(emailLabel);

        JTextField txtEmail = new RoundedTextField(15);
        txtEmail.setBounds(25, 155, 270, 35);
        formPanel.add(txtEmail);

        // Input Password
        JLabel passLabel = new JLabel("Password (min. 8 characters)");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        passLabel.setForeground(new Color(80, 80, 80));
        passLabel.setBounds(30, 205, 260, 15);
        formPanel.add(passLabel);

        JPasswordField txtPassword = new RoundedPasswordField(15);
        txtPassword.setBounds(25, 225, 270, 35);
        formPanel.add(txtPassword);

        // --- TOMBOL REGISTER CUSTOM (Hitam Elegan) ---
        JButton btnRegister = new JButton("Sign Up") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradasi hitam ke abu gelap
                GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 20), getWidth(), 0, new Color(60, 60, 60));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
                
                super.paintComponent(g); 
            }
        };
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setForeground(Color.WHITE); // Teks tombol putih
        
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setOpaque(false); 
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setBounds(25, 295, 270, 40);
        formPanel.add(btnRegister);

        // --- 4. Logika Aksi Tombol Register + JDBC ---
        btnRegister.addActionListener(e -> {
            String inputUser = txtUsername.getText().trim();
            String inputEmail = txtEmail.getText().trim();
            String inputPass = new String(txtPassword.getPassword());
            
            // 1. Validasi Input Kosong
            if(inputUser.isEmpty() || inputEmail.isEmpty() || inputPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua kolom pendaftaran wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Validasi Minimal Panjang Password
            if(inputPass.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password terlalu pendek! Minimal harus 8 karakter.", "Validasi Password", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Query SQL untuk memasukkan data ke tabel 'users'
            String sqlQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            
            // 4. Membuka koneksi database menggunakan block Try-with-Resources (otomatis close koneksi)
            try (Connection conn = DatabaseConnection.connect(); 
                 PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                
                // Mengisi parameter tanda tanya (?) pada query SQL
                ps.setString(1, inputUser);
                ps.setString(2, inputEmail);
                ps.setString(3, inputPass); // Disimpan sebagai plain text sesuai struktur tabelmu
                
                // Mengeksekusi query perintah INSERT ke database
                int rowsInserted = ps.executeUpdate();
                
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Akun Fotoku berhasil didaftarkan! Silakan login.");
                    
                    // Berpindah secara otomatis kembali ke halaman LoginFrame
                    new LoginFrame().setVisible(true);
                    this.dispose(); // Menutup halaman register saat ini
                }
                
            } catch (SQLException ex) {
                // Menangani error jika username atau email melanggar aturan UNIQUE (sudah terdaftar sebelumnya)
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this, "Username atau Email sudah terpakai! Silakan gunakan yang lain.", "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                ex.printStackTrace();
            }
        });

        // Label Footer Navigasi
        JLabel lblLogin = new JLabel("Already have an account? Login", SwingConstants.CENTER);
        lblLogin.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLogin.setForeground(Color.BLACK);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.setBounds(25, 385, 270, 20);
        formPanel.add(lblLogin);

        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        // Menggabungkan struktur komponen
        containerPanel.add(formPanel);
        mainPanel.add(containerPanel); 
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegisterFrame().setVisible(true);
        });
    }
}
