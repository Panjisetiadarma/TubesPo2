package view;

import model.Photo;
import utils.ProfileTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ProfilePanel extends JPanel {
    private List<Photo> allPhotos = new ArrayList<>();
    private PhotoGridPanel gridPanel;
    private ToolBar toolBar;
    private model.User user;
    private ProfileHeaderPanel headerPanel;
    private TabBar tabBar;

    public ProfilePanel(model.User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(utils.Theme.BACKGROUND);

        loadPhotosFromDatabase();
        initUI();
    }

    private void loadPhotosFromDatabase() {
        allPhotos.clear();
        try (java.sql.Connection conn = utils.DatabaseConnection.connect();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, " +
                "(SELECT COUNT(*) FROM post_likes WHERE post_id = p.id) as total_likes, " +
                "(SELECT COUNT(*) FROM post_saves WHERE post_id = p.id AND user_id = ?) as is_fav, " +
                "(SELECT COUNT(*) FROM post_saves WHERE post_id = p.id AND user_id = ?) as is_saved, " +
                "(SELECT COUNT(*) FROM post_likes WHERE post_id = p.id AND user_id = ?) as is_liked " +
                "FROM posts p WHERE p.user_id = ? " +
                "OR EXISTS (SELECT 1 FROM post_likes WHERE post_id = p.id AND user_id = ?) " +
                "OR EXISTS (SELECT 1 FROM post_saves WHERE post_id = p.id AND user_id = ?) " +
                "ORDER BY p.created_at DESC")) {
            
            int userId = Integer.parseInt(user.getId());
            for(int i=1; i<=6; i++) {
                ps.setInt(i, userId);
            }
            
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Photo p = new Photo(
                    rs.getInt("id"),
                    "Foto " + rs.getInt("id"),
                    rs.getInt("total_likes"),
                    rs.getString("image_path")
                );
                p.setFavorite(rs.getInt("is_fav") > 0);
                p.setSaved(rs.getInt("is_saved") > 0);
                p.setLiked(rs.getInt("is_liked") > 0);
                p.setOwnPost(rs.getInt("user_id") == userId);
                allPhotos.add(p);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(utils.Theme.BACKGROUND);
        add(root, BorderLayout.CENTER);

        class ScrollablePagePanel extends JPanel implements Scrollable {
            ScrollablePagePanel(LayoutManager m) { super(m); }
            @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
            @Override public int getScrollableUnitIncrement(Rectangle v, int o, int d) { return 40; }
            @Override public int getScrollableBlockIncrement(Rectangle v, int o, int d) { return 40; }
            @Override public boolean getScrollableTracksViewportWidth() { return true; }
            @Override public boolean getScrollableTracksViewportHeight() { return false; }
        }
        
        JPanel pagePanel = new ScrollablePagePanel(new BorderLayout());
        pagePanel.setBackground(utils.Theme.BACKGROUND);
        pagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ── Profile Header ────────────────────────────────
        headerPanel = new ProfileHeaderPanel(user, this::openUploadDialog);
        pagePanel.add(headerPanel, BorderLayout.NORTH);

        // ── Center: tabs + toolbar + grid ─────────────────
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(utils.Theme.BACKGROUND);

        // TabBar and ToolBar in one rounded card
        component.RoundedPanel controlsCard = new component.RoundedPanel(15);
        controlsCard.setLayout(new BorderLayout());
        controlsCard.setBackground(utils.Theme.CARD);
        controlsCard.setBorder(new EmptyBorder(5, 20, 5, 20));
        
        tabBar = new TabBar();
        controlsCard.add(tabBar, BorderLayout.WEST);

        toolBar = new ToolBar((int) allPhotos.stream().filter(model.Photo::isOwnPost).count());
        controlsCard.add(toolBar, BorderLayout.EAST);
        
        JPanel spacingPanel = new JPanel(new BorderLayout());
        spacingPanel.setOpaque(false);
        spacingPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        spacingPanel.add(controlsCard, BorderLayout.CENTER);
        
        centerPanel.add(spacingPanel, BorderLayout.NORTH);

        // Grid
        JPanel gridSection = new JPanel(new BorderLayout());
        gridSection.setBackground(utils.Theme.BACKGROUND);
        
        gridPanel = new PhotoGridPanel(allPhotos);
        gridSection.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(gridSection, BorderLayout.CENTER);

        pagePanel.add(centerPanel, BorderLayout.CENTER);

        // Wrap in scroll pane
        JScrollPane mainScroll = new JScrollPane(pagePanel);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.getVerticalScrollBar().setUnitIncrement(40); // faster, smoother scroll
        styleScrollbar(mainScroll);

        mainScroll.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                gridPanel.revalidate();
            }
        });

        root.add(mainScroll, BorderLayout.CENTER);

        // ── Events ────────────────────────────────────────
        tabBar.setTabListener(tab -> {
            switch (tab) {
                case SEMUA -> {
                    gridPanel.showAll();
                    toolBar.setCount(allPhotos.size());
                }
                case DISUKAI -> {
                    gridPanel.filterLiked();
                    long cnt = allPhotos.stream().filter(Photo::isLiked).count();
                    toolBar.setCount((int) cnt);
                }
                case FAVORIT -> {
                    gridPanel.filterSaved();
                    long cnt = allPhotos.stream().filter(Photo::isSaved).count();
                    toolBar.setCount((int) cnt);
                }
            }
        });

        toolBar.setViewModeListener(cols -> gridPanel.setColumns(cols));
        toolBar.setAddPhotoCallback(this::openUploadDialog);
    }

    public void selectTab(TabBar.Tab tab) {
        tabBar.setActiveTab(tab);
    }

    private void styleScrollbar(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(190, 190, 200);
                trackColor = utils.Theme.BACKGROUND;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            private JButton zeroBtn() {
                JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b;
            }
        });
    }

    public void openUploadDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, "Upload Foto Baru", true);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Upload Foto Baru");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(38, 38, 38));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Pilih gambar dari komputer Anda");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(142, 142, 142));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sub);
        panel.add(Box.createVerticalStrut(20));

        // File label
        final String[] chosenFileName = { null };
        final java.io.File[] chosenFile = { null };
        JLabel fileLabel = new JLabel("Belum ada file dipilih");
        fileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        fileLabel.setForeground(new Color(142, 142, 142));
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnChoose = new JButton("📁  Pilih Foto...");
        btnChoose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnChoose.setBackground(new Color(239, 239, 239));
        btnChoose.setForeground(new Color(38, 38, 38));
        btnChoose.setFocusPainted(false);
        btnChoose.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnChoose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChoose.setOpaque(true);
        btnChoose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnChoose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Gambar (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                chosenFile[0] = chooser.getSelectedFile();
                chosenFileName[0] = chooser.getSelectedFile().getName();
                fileLabel.setText("✓  " + chosenFileName[0]);
                fileLabel.setForeground(new Color(0, 149, 246));
            }
        });
        panel.add(btnChoose);
        panel.add(Box.createVerticalStrut(8));
        panel.add(fileLabel);
        panel.add(Box.createVerticalStrut(18));

        // Caption
        JLabel captionLbl = new JLabel("Caption (opsional)");
        captionLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        captionLbl.setForeground(new Color(100, 100, 100));
        captionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(captionLbl);
        panel.add(Box.createVerticalStrut(4));

        JTextField captionField = new JTextField();
        captionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        captionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        captionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        captionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(captionField);
        panel.add(Box.createVerticalStrut(24));

        // Upload button
        JButton btnUpload = new JButton("Upload Sekarang") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 149, 246));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnUpload.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setContentAreaFilled(false);
        btnUpload.setBorderPainted(false);
        btnUpload.setFocusPainted(false);
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.setPreferredSize(new Dimension(200, 40));
        btnUpload.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnUpload.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpload.addActionListener(e -> {
            if (chosenFile[0] == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih foto terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.io.File uploadDir = new java.io.File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            java.io.File destination = new java.io.File(uploadDir, System.currentTimeMillis() + "_" + chosenFileName[0]);
            try {
                java.nio.file.Files.copy(chosenFile[0].toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                // Insert to DB
                try (java.sql.Connection conn = utils.DatabaseConnection.connect();
                     java.sql.PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO posts (user_id, image_path, caption) VALUES (?, ?, ?)")) {
                    ps.setInt(1, Integer.parseInt(user.getId()));
                    ps.setString(2, destination.getAbsolutePath());
                    ps.setString(3, captionField.getText());
                    ps.executeUpdate();
                }
                
                loadPhotosFromDatabase();
                gridPanel.showAll();
                toolBar.setCount(allPhotos.size());
                if (headerPanel != null) {
                    headerPanel.updatePostCount(allPhotos.size());
                }
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "Foto berhasil diupload! 🎉",
                    "Upload Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengupload foto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnUpload);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}
