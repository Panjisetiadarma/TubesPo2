package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DatabaseConnection;
import utils.Session;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Fotoku - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        WhiteBackgroundPanel mainPanel = new WhiteBackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());

        JPanel containerPanel = new JPanel();
        containerPanel.setOpaque(false);
        containerPanel.setPreferredSize(new Dimension(400, 600));
        containerPanel.setLayout(null);

        JLabel logoLabel = new JLabel("Fotoku", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logoLabel.setForeground(Color.BLACK);
        logoLabel.setBounds(100, 30, 200, 50);
        containerPanel.add(logoLabel);

        JLabel taglineLabel = new JLabel("share your vibe. ignite your aesthetic.", SwingConstants.CENTER);
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taglineLabel.setForeground(new Color(100, 100, 100));
        taglineLabel.setBounds(50, 85, 300, 20);
        containerPanel.add(taglineLabel);

        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(245, 245, 245, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setLayout(null);
        formPanel.setBounds(40, 140, 320, 360);

        JLabel welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBounds(20, 20, 280, 25);
        formPanel.add(welcomeLabel);

        JLabel userLabel = new JLabel("Username or Email");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userLabel.setForeground(new Color(80, 80, 80));
        userLabel.setBounds(30, 65, 260, 15);
        formPanel.add(userLabel);

        JTextField txtUsername = new RoundedTextField(15);
        txtUsername.setBounds(25, 85, 270, 35);
        formPanel.add(txtUsername);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passLabel.setForeground(new Color(80, 80, 80));
        passLabel.setBounds(30, 135, 260, 15);
        formPanel.add(passLabel);

        JPasswordField txtPassword = new RoundedPasswordField(15);
        txtPassword.setBounds(25, 155, 270, 35);
        formPanel.add(txtPassword);

        JButton btnLogin = new JButton("Log In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 20), getWidth(), 0, new Color(60, 60, 60));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(25, 225, 270, 40);
        formPanel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String inputUser = txtUsername.getText();
            String inputPass = new String(txtPassword.getPassword());

            if (inputUser.isEmpty() || inputPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kolom login tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlQuery = "SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                ps.setString(1, inputUser);
                ps.setString(2, inputUser);
                ps.setString(3, inputPass);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String dbUsername = rs.getString("username");
                    Session.setCurrentUser(dbUsername);
                    new MainFrame().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Gagal Login", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JLabel lblSignUp = new JLabel("Don't have an account? Register", SwingConstants.CENTER);
        lblSignUp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSignUp.setForeground(Color.BLACK);
        lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblSignUp.setBounds(25, 315, 270, 20);
        formPanel.add(lblSignUp);

        lblSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new RegisterFrame().setVisible(true);
                dispose();
            }
        });

        containerPanel.add(formPanel);
        mainPanel.add(containerPanel);
        add(mainPanel);
    }
}

class WhiteBackgroundPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}

class RoundedTextField extends JTextField {
    private Shape shape;
    public RoundedTextField(int size) {
        super(size);
        setOpaque(false);
        setForeground(Color.BLACK);
        setCaretColor(Color.BLACK);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        super.paintComponent(g);
    }
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
    }
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        }
        return shape.contains(x, y);
    }
}

class RoundedPasswordField extends JPasswordField {
    private Shape shape;
    public RoundedPasswordField(int size) {
        super(size);
        setOpaque(false);
        setForeground(Color.BLACK);
        setCaretColor(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        super.paintComponent(g);
    }
    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
    }
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        }
        return shape.contains(x, y);
    }
}