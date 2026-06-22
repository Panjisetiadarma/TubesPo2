package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utils.Theme;
import utils.Session;
import component.CircleImagePanel;
import component.RoundedPanel;

public class ProfileHeaderPanel extends RoundedPanel {
    
    private CircleImagePanel profilePicPanel;
    private JLabel lblFullName;
    private JLabel lblBio;
    private JLabel lblPostCount;

    public ProfileHeaderPanel(model.User user, Runnable onUpload) {
        super(15);
        setBackground(Theme.CARD);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // --- Top Section (Info & Picture) ---
        JPanel topSection = new JPanel(new BorderLayout(20, 0));
        topSection.setOpaque(false);

        // Profile Picture
        JPanel picWrapper = new JPanel(new BorderLayout());
        picWrapper.setOpaque(false);
        profilePicPanel = new CircleImagePanel(user.getProfilePicture(), 120);
        profilePicPanel.setPreferredSize(new Dimension(120, 120));
        
        picWrapper.add(profilePicPanel, BorderLayout.CENTER);
        topSection.add(picWrapper, BorderLayout.WEST);

        // User Info (Center)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        String username = user.getUsername();
        JLabel lblUsername = new JLabel(username);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblUsername.setForeground(Theme.TEXT);
        infoPanel.add(lblUsername);
        infoPanel.add(Box.createVerticalStrut(5));

        JLabel lblUsernameHandle = new JLabel("@" + username);
        lblUsernameHandle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsernameHandle.setForeground(Theme.TEXT_MUTED);
        infoPanel.add(lblUsernameHandle);
        infoPanel.add(Box.createVerticalStrut(15));

        lblFullName = new JLabel(user.getFullName());
        lblFullName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFullName.setForeground(Theme.TEXT);
        infoPanel.add(lblFullName);
        infoPanel.add(Box.createVerticalStrut(5));

        lblBio = new JLabel(user.getBio());
        lblBio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBio.setForeground(Theme.TEXT_MUTED);
        infoPanel.add(lblBio);

        topSection.add(infoPanel, BorderLayout.CENTER);

        // Right side (Edit Profile button)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        rightPanel.setOpaque(false);

        if (username.equals(Session.getCurrentUser())) {
            RoundedPanel btnEditBorder = new RoundedPanel(15) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.PRIMARY);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
            };
            btnEditBorder.setLayout(new BorderLayout());
            btnEditBorder.setOpaque(false);
            btnEditBorder.setBackground(Theme.CARD);
            btnEditBorder.setPreferredSize(new Dimension(110, 35));
            btnEditBorder.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblEdit = new JLabel("\u270E Edit Profil", SwingConstants.CENTER);
            lblEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblEdit.setForeground(Theme.PRIMARY);
            btnEditBorder.add(lblEdit, BorderLayout.CENTER);

            btnEditBorder.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    openEditProfileDialog(user, onUpload);
                }
            });

            RoundedPanel btnUploadBorder = new RoundedPanel(15) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
            };
            btnUploadBorder.setLayout(new BorderLayout());
            btnUploadBorder.setOpaque(false);
            btnUploadBorder.setBackground(Theme.PRIMARY);
            btnUploadBorder.setPreferredSize(new Dimension(110, 35));
            btnUploadBorder.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblUpload = new JLabel("\u2795 Upload", SwingConstants.CENTER);
            lblUpload.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblUpload.setForeground(Color.WHITE);
            btnUploadBorder.add(lblUpload, BorderLayout.CENTER);

            btnUploadBorder.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (onUpload != null) onUpload.run();
                }
            });

            rightPanel.add(btnUploadBorder);
            rightPanel.add(Box.createHorizontalStrut(10));
            rightPanel.add(btnEditBorder);
        }

        topSection.add(rightPanel, BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        // --- Bottom Section (Stats) ---
        // Fetch counts
        int postCount = 0;
        int likesCount = 0;
        int savedCount = 0;
        try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM posts WHERE user_id = ?");
            ps.setInt(1, Integer.parseInt(user.getId()));
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) postCount = rs.getInt(1);
            
            java.sql.PreparedStatement psLikes = conn.prepareStatement("SELECT COUNT(*) FROM post_likes pl JOIN posts p ON pl.post_id = p.id WHERE p.user_id = ?");
            psLikes.setInt(1, Integer.parseInt(user.getId()));
            java.sql.ResultSet rsLikes = psLikes.executeQuery();
            if (rsLikes.next()) likesCount = rsLikes.getInt(1);

            java.sql.PreparedStatement psSaved = conn.prepareStatement("SELECT COUNT(*) FROM post_saves WHERE user_id = ?");
            psSaved.setInt(1, Integer.parseInt(user.getId()));
            java.sql.ResultSet rsSaved = psSaved.executeQuery();
            if (rsSaved.next()) savedCount = rsSaved.getInt(1);
        } catch (Exception e) {}

        JPanel statsSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        statsSection.setOpaque(false);
        statsSection.setBorder(new EmptyBorder(20, 0, 10, 0));

        JPanel p1 = createStatBlock(postCount, "Postingan");
        JPanel p2 = createStatBlock(likesCount, "Disukai");
        JPanel p3 = createStatBlock(savedCount, "Disimpan");

        // Add separators manually
        statsSection.add(p1);
        statsSection.add(createDivider());
        statsSection.add(p2);
        statsSection.add(createDivider());
        statsSection.add(p3);

        add(statsSection, BorderLayout.SOUTH);
    }
    
    private JPanel createDivider() {
        JPanel div = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Theme.BORDER);
                g.drawLine(0, 5, 0, getHeight() - 5);
            }
        };
        div.setPreferredSize(new Dimension(1, 40));
        div.setOpaque(false);
        return div;
    }

    private JPanel createStatBlock(int count, String label) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        
        JLabel lCount = new JLabel(String.valueOf(count));
        lCount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lCount.setForeground(Theme.TEXT);
        lCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lLabel = new JLabel(label);
        lLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lLabel.setForeground(Theme.TEXT_MUTED);
        lLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        if (label.equals("Postingan")) {
            lblPostCount = lCount;
        }

        p.add(lCount);
        p.add(Box.createVerticalStrut(5));
        p.add(lLabel);
        return p;
    }
    
    public void updatePostCount(int count) {
        if (lblPostCount != null) {
            lblPostCount.setText(String.valueOf(count));
        }
    }

    private void openEditProfileDialog(model.User user, Runnable onUpload) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Profile", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Full Name:"));
        JTextField txtFullName = new JTextField(user.getFullName());
        formPanel.add(txtFullName);

        formPanel.add(new JLabel("Bio:"));
        JTextField txtBio = new JTextField(user.getBio());
        formPanel.add(txtBio);

        formPanel.add(new JLabel("Profile Picture:"));
        JButton btnChoosePic = new JButton("Choose File");
        final String[] picPath = {null};
        btnChoosePic.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files (JPG, PNG)", "jpg", "jpeg", "png");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = chooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(".jpg") && 
                    !selectedFile.getName().toLowerCase().endsWith(".jpeg") && 
                    !selectedFile.getName().toLowerCase().endsWith(".png")) {
                    JOptionPane.showMessageDialog(dialog, "Format tidak didukung! Mohon pilih foto berformat JPG atau PNG.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                picPath[0] = selectedFile.getAbsolutePath();
                btnChoosePic.setText(selectedFile.getName());
            }
        });
        formPanel.add(btnChoosePic);

        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                java.sql.PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET full_name = ?, bio = ? " + 
                    (picPath[0] != null ? ", profile_picture_url = ? " : "") + 
                    "WHERE id = ?");
                ps.setString(1, txtFullName.getText());
                ps.setString(2, txtBio.getText());
                int paramIndex = 3;
                if (picPath[0] != null) {
                    ps.setString(paramIndex++, picPath[0]);
                }
                ps.setInt(paramIndex, Integer.parseInt(user.getId()));
                ps.executeUpdate();
                // Update user object fields
                user.setFullName(txtFullName.getText());
                user.setBio(txtBio.getText());
                if (picPath[0] != null) {
                    user.reloadProfilePicture(picPath[0]);
                }
                
                // Update UI dynamically
                lblFullName.setText(user.getFullName());
                lblBio.setText(user.getBio());
                profilePicPanel.setImage(user.getProfilePicture());
                
                JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        btnPanel.add(btnSave);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
