package view;

import utils.Theme;
import utils.Session;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import component.CircleImagePanel;
import component.RoundedPanel;
import model.Post;
import model.Comment;

public class PostPanel extends RoundedPanel {
    private Post post;
    private JLabel likesLabel;
    private JLabel commentsLabel;
    private boolean isSaved = false;
    private boolean isLiked = false;
    private ImageIcon likeBeforeImg, likeAfterImg, saveBeforeImg, saveAfterImg, commentImg;
    private JLabel likeIconLabel, saveIconLabel, commentIconLabel;
    private JPanel commentsContainer;
    private JLabel captionText;

    public PostPanel(Post post) {
        super(Theme.RADIUS);
        this.post = post;

        setBackground(Theme.CARD);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setMaximumSize(new Dimension(650, Integer.MAX_VALUE));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        loadIcons();

        add(createHeader());
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(createContent());
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(createFooter());
    }

    private void loadIcons() {
        String iconDir = new java.io.File("Icon").exists() ? "Icon/" : "../Icon/";
        likeBeforeImg  = loadIcon(iconDir + "LikeIconBefore.png",  22, 22);
        likeAfterImg   = loadIcon(iconDir + "likeIconAfter.png",   22, 22);
        saveBeforeImg  = loadIcon(iconDir + "PavoriteIconBefore.png", 22, 22);
        saveAfterImg   = loadIcon(iconDir + "PavoriteIconAfter.png",  22, 22);
        commentImg     = loadIcon(iconDir + "comentIcon.png",     22, 22);
    }

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon raw = new ImageIcon(path);
            if (raw.getIconWidth() <= 0) return makeFallback(w, h, "?");
            return new ImageIcon(raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return makeFallback(w, h, "?");
        }
    }

    private ImageIcon makeFallback(int w, int h, String text) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(200, 200, 200));
        g.fillOval(0, 0, w, h);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString(text, 7, 16);
        g.dispose();
        return new ImageIcon(img);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        userPanel.setOpaque(false);

        CircleImagePanel profileImg = new CircleImagePanel(post.getUser().getProfilePicture(), 40);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel username = new JLabel(post.getUser().getUsername());
        username.setFont(Theme.FONT_BOLD);
        username.setForeground(Theme.TEXT);
        
        JLabel timeLabel = new JLabel(post.getUploadTime());
        timeLabel.setFont(Theme.FONT_SMALL);
        timeLabel.setForeground(Theme.TEXT_MUTED);

        textPanel.add(username);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(timeLabel);

        userPanel.add(profileImg);
        userPanel.add(textPanel);

        MouseAdapter openProfileAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Window w = SwingUtilities.getWindowAncestor(PostPanel.this);
                if (w instanceof MainFrame) {
                    ((MainFrame) w).showUserProfile(post.getUser());
                }
            }
        };
        profileImg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileImg.addMouseListener(openProfileAdapter);
        username.setCursor(new Cursor(Cursor.HAND_CURSOR));
        username.addMouseListener(openProfileAdapter);

        String iconDir = new java.io.File("Icon").exists() ? "Icon/" : "../Icon/";
        ImageIcon editIcon = utils.ImageUtils.loadIcon(iconDir + "edit.png", 20, 20);
        JLabel menuBtn = new JLabel(editIcon);
        menuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPopupMenu popupMenu = new JPopupMenu();
        if (post.getUser().getUsername().equals(Session.getCurrentUser())) {
            JMenuItem editCaptionItem = new JMenuItem("Edit Caption");
            editCaptionItem.addActionListener(e -> {
                String newCaption = JOptionPane.showInputDialog(PostPanel.this, "Masukkan caption baru:", post.getCaption());
                if (newCaption != null && !newCaption.trim().isEmpty()) {
                    try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                        java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE posts SET caption = ? WHERE id = ?");
                        ps.setString(1, newCaption);
                        ps.setInt(2, Integer.parseInt(post.getId()));
                        ps.executeUpdate();
                        post.setCaption(newCaption);
                        if (captionText != null) {
                            captionText.setText("<html><body style='width: 400px'>" + newCaption.replace("\n", "<br>") + "</body></html>");
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            popupMenu.add(editCaptionItem);

            JMenuItem deletePostItem = new JMenuItem("Hapus Postingan");
            deletePostItem.setForeground(new Color(200, 50, 50));
            deletePostItem.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(PostPanel.this, "Yakin ingin menghapus postingan ini?", "Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                        java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM posts WHERE id = ?");
                        ps.setInt(1, Integer.parseInt(post.getId()));
                        ps.executeUpdate();
                        // Refetch feed to remove post visually
                        Window w = SwingUtilities.getWindowAncestor(PostPanel.this);
                        if (w instanceof MainFrame) {
                            MainFrame main = (MainFrame) w;
                            // main.feed.refresh(...) need to be called. We can simulate a click on "Beranda" or reload.
                            // But maybe we can just hide this panel for now.
                            PostPanel.this.setVisible(false);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            });
            popupMenu.add(deletePostItem);
        }

        menuBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (popupMenu.getComponentCount() > 0) {
                    popupMenu.show(menuBtn, e.getX(), e.getY());
                }
            }
        });

        header.add(userPanel, BorderLayout.WEST);
        header.add(menuBtn, BorderLayout.EAST);
        
        JPanel headerWrapper = new JPanel();
        headerWrapper.setLayout(new BoxLayout(headerWrapper, BoxLayout.Y_AXIS));
        headerWrapper.setOpaque(false);
        headerWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerWrapper.add(header);
        headerWrapper.add(Box.createRigidArea(new Dimension(0, 5)));
        
        captionText = new JLabel("<html><body style='width: 400px'>" + post.getCaption().replace("\n", "<br>") + "</body></html>");
        captionText.setFont(Theme.FONT_REGULAR);
        captionText.setForeground(Theme.TEXT_MUTED);
        
        JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        captionPanel.setOpaque(false);
        captionPanel.add(captionText);
        
        headerWrapper.add(captionPanel);
        headerWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
        
        return headerWrapper;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout()) {
            private Image img = post.getImagePath() != null ? post.getImagePath().getImage() : null;
            
            @Override
            public Dimension getPreferredSize() {
                if (img == null) return new Dimension(600, 400);
                Container parent = getParent();
                int w = parent != null ? parent.getWidth() - 40 : 610; // 40 is padding
                if (w <= 0) w = 610;
                int h = (int) ((double) img.getHeight(null) / img.getWidth(null) * w);
                if (h > 600) h = 600; // max height
                return new Dimension(w, h);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE, pref.height);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // Rendering hints kualitas tertinggi
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,   RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    
                    int w = getWidth();
                    int h = getHeight();
                    g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 15, 15));
                    
                    // Fill bounds completely (zoom to fill)
                    double scaleX = (double) w / img.getWidth(null);
                    double scaleY = (double) h / img.getHeight(null);
                    double scale = Math.max(scaleX, scaleY);
                    
                    int drawW = (int) (img.getWidth(null) * scale);
                    int drawH = (int) (img.getHeight(null) * scale);
                    int x = (w - drawW) / 2;
                    int y = (h - drawH) / 2;
                    g2.drawImage(img, x, y, drawW, drawH, null);
                    
                    // Draw 1/1 badge
                    g2.setClip(null);
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRoundRect(w - 45, 15, 30, 20, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("1/1", w - 45 + (30 - fm.stringWidth("1/1"))/2, 15 + fm.getAscent() + (20 - fm.getHeight())/2);
                    
                    g2.dispose();
                } else {
                    g.setColor(Theme.BACKGROUND);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        content.setOpaque(false);
        content.setAlignmentX(Component.CENTER_ALIGNMENT);
        return content;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Actions Row ────────────────────────────────────
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftActions.setOpaque(false);

        isLiked = post.isLiked();
        isSaved = post.isSaved();

        // Like Button
        JPanel likeBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        likeBtn.setOpaque(false);
        likeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        likeIconLabel = new JLabel(isLiked ? likeAfterImg : likeBeforeImg);
        likesLabel = new JLabel(post.getLikes() + " suka");
        likesLabel.setFont(Theme.FONT_BOLD);
        likesLabel.setForeground(Theme.TEXT);
        likeBtn.add(likeIconLabel);
        likeBtn.add(likesLabel);
        
        likeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isLiked = !isLiked;
                if (isLiked) { post.like(); } else { post.unlike(); }
                likeIconLabel.setIcon(isLiked ? likeAfterImg : likeBeforeImg);
                likesLabel.setText(post.getLikes() + " suka");
                
                try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                    java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                    psUser.setString(1, utils.Session.getCurrentUser());
                    java.sql.ResultSet rs = psUser.executeQuery();
                    if (rs.next()) {
                        int uid = rs.getInt("id");
                        if (isLiked) {
                            java.sql.PreparedStatement psLike = conn.prepareStatement("INSERT IGNORE INTO post_likes (user_id, post_id) VALUES (?, ?)");
                            psLike.setInt(1, uid);
                            psLike.setInt(2, Integer.parseInt(post.getId()));
                            psLike.executeUpdate();
                        } else {
                            java.sql.PreparedStatement psLike = conn.prepareStatement("DELETE FROM post_likes WHERE user_id = ? AND post_id = ?");
                            psLike.setInt(1, uid);
                            psLike.setInt(2, Integer.parseInt(post.getId()));
                            psLike.executeUpdate();
                        }
                    }
                } catch (Exception ex) {}
            }
        });

        // Comment Button
        JPanel commentBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        commentBtn.setOpaque(false);
        commentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        commentIconLabel = new JLabel(commentImg);
        commentsLabel = new JLabel(post.getComments().size() + " komentar");
        commentsLabel.setFont(Theme.FONT_BOLD);
        commentsLabel.setForeground(Theme.TEXT);
        commentBtn.add(commentIconLabel);
        commentBtn.add(commentsLabel);
        commentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        commentBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openCommentsModal();
            }
        });

        leftActions.add(likeBtn);
        leftActions.add(Box.createRigidArea(new Dimension(15, 0)));
        leftActions.add(commentBtn);

        // Save / Bookmark Button
        saveIconLabel = new JLabel(isSaved ? saveAfterImg : saveBeforeImg);
        saveIconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isSaved = !isSaved;
                saveIconLabel.setIcon(isSaved ? saveAfterImg : saveBeforeImg);
                
                try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                    java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                    psUser.setString(1, utils.Session.getCurrentUser());
                    java.sql.ResultSet rs = psUser.executeQuery();
                    if (rs.next()) {
                        int uid = rs.getInt("id");
                        if (isSaved) {
                            java.sql.PreparedStatement psSave = conn.prepareStatement("INSERT IGNORE INTO post_saves (user_id, post_id) VALUES (?, ?)");
                            psSave.setInt(1, uid);
                            psSave.setInt(2, Integer.parseInt(post.getId()));
                            psSave.executeUpdate();
                        } else {
                            java.sql.PreparedStatement psSave = conn.prepareStatement("DELETE FROM post_saves WHERE user_id = ? AND post_id = ?");
                            psSave.setInt(1, uid);
                            psSave.setInt(2, Integer.parseInt(post.getId()));
                            psSave.executeUpdate();
                        }
                    }
                } catch (Exception ex) {}
            }
        });

        actions.add(leftActions);
        actions.add(Box.createHorizontalGlue());
        actions.add(saveIconLabel);
        footer.add(actions);
        footer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Caption is moved to header
        // ── Timestamp ─────────────────────────────────────
        JLabel postTimeLabel = new JLabel(post.getUploadTime());
        postTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        postTimeLabel.setForeground(Theme.TEXT_MUTED);
        postTimeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(postTimeLabel);
        footer.add(Box.createRigidArea(new Dimension(0, 15)));

        return footer;
    }

    private void openCommentsModal() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, "Komentar", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND);

        commentsContainer = new JPanel();
        commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        commentsContainer.setBackground(Theme.BACKGROUND);
        commentsContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        for (Comment c : post.getComments()) {
            addCommentRow(c);
        }

        JScrollPane scroll = new JScrollPane(commentsContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Theme.BACKGROUND);
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        RoundedPanel commentBox = new RoundedPanel(20);
        commentBox.setLayout(new BorderLayout(10, 0));
        commentBox.setBackground(Theme.CARD);
        commentBox.setBorder(new EmptyBorder(5, 15, 5, 5));
        commentBox.setPreferredSize(new Dimension(0, 45));

        JTextField commentField = new JTextField("Tambahkan komentar...");
        commentField.setBackground(Theme.CARD);
        commentField.setForeground(Theme.TEXT_MUTED);
        commentField.setCaretColor(Theme.TEXT);
        commentField.setBorder(null);
        commentField.setFont(Theme.FONT_REGULAR);

        commentField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (commentField.getText().equals("Tambahkan komentar...")) {
                    commentField.setText("");
                    commentField.setForeground(Theme.TEXT);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (commentField.getText().isEmpty()) {
                    commentField.setText("Tambahkan komentar...");
                    commentField.setForeground(Theme.TEXT_MUTED);
                }
            }
        });

        RoundedPanel postBtnPanel = new RoundedPanel(15);
        postBtnPanel.setLayout(new BorderLayout());
        postBtnPanel.setBackground(Theme.PRIMARY);
        postBtnPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postBtnPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel postBtn = new JLabel("Kirim");
        postBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        postBtn.setForeground(Color.WHITE);
        postBtnPanel.add(postBtn, BorderLayout.CENTER);

        MouseAdapter sendComment = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String text = commentField.getText().trim();
                if (!text.isEmpty() && !text.equals("Tambahkan komentar...")) {
                    String user = Session.getCurrentUser();

                    try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                        java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                        psUser.setString(1, user);
                        java.sql.ResultSet rs = psUser.executeQuery();
                        if (rs.next()) {
                            int uid = rs.getInt("id");
                            java.sql.PreparedStatement psComment = conn.prepareStatement("INSERT INTO comments (post_id, user_id, text) VALUES (?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
                            psComment.setInt(1, Integer.parseInt(post.getId()));
                            psComment.setInt(2, uid);
                            psComment.setString(3, text);
                            psComment.executeUpdate();

                            int newId = -1;
                            java.sql.ResultSet rsKeys = psComment.getGeneratedKeys();
                            if (rsKeys.next()) newId = rsKeys.getInt(1);

                            model.User cUser = new model.User("-1", user, "Unknown", null, null, null);
                            Comment newComment = new Comment(newId, cUser, text, "Baru saja");
                            post.addComment(newComment);
                            addCommentRow(newComment);
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }

                    commentField.setText("Tambahkan komentar...");
                    commentField.setForeground(Theme.TEXT_MUTED);
                    if (commentsLabel != null) {
                        commentsLabel.setText(post.getComments().size() + " komentar");
                    }
                    commentsContainer.revalidate();
                    commentsContainer.repaint();
                    
                    // scroll to bottom
                    SwingUtilities.invokeLater(() -> {
                        JScrollBar vertical = scroll.getVerticalScrollBar();
                        vertical.setValue(vertical.getMaximum());
                    });
                }
            }
        };
        postBtnPanel.addMouseListener(sendComment);
        commentField.addActionListener(e -> sendComment.mouseClicked(null));

        commentBox.add(commentField, BorderLayout.CENTER);
        commentBox.add(postBtnPanel, BorderLayout.EAST);
        
        bottomPanel.add(commentBox, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private void addCommentRow(Comment comment) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        
        JPanel textRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        textRow.setOpaque(false);
        JLabel userLbl = new JLabel(comment.getUser().getUsername());
        userLbl.setFont(Theme.FONT_BOLD);
        userLbl.setForeground(Theme.TEXT);
        JLabel textLbl = new JLabel(comment.getText());
        textLbl.setFont(Theme.FONT_REGULAR);
        textLbl.setForeground(Theme.TEXT);
        textRow.add(userLbl);
        textRow.add(textLbl);
        
        if (comment.getUser().getUsername().equals(Session.getCurrentUser())) {
            JLabel delBtn = new JLabel("Hapus");
            delBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
            delBtn.setForeground(new Color(200, 50, 50));
            delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            delBtn.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (JOptionPane.showConfirmDialog(PostPanel.this, "Hapus komentar ini?", "Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                            java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM comments WHERE id = ?");
                            ps.setInt(1, comment.getId());
                            ps.executeUpdate();
                            post.getComments().remove(comment);
                            commentsContainer.remove(row);
                            if (commentsLabel != null) {
                                commentsLabel.setText(post.getComments().size() + " komentar");
                            }
                            commentsContainer.revalidate();
                            commentsContainer.repaint();
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            });
            textRow.add(Box.createRigidArea(new Dimension(10, 0)));
            textRow.add(delBtn);
        }
        
        JLabel timeLbl = new JLabel(comment.getDate());
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLbl.setForeground(Theme.TEXT_MUTED);
        
        textRow.add(Box.createRigidArea(new Dimension(5, 0)));
        textRow.add(timeLbl);
        
        row.add(textRow);
        row.add(Box.createRigidArea(new Dimension(0, 5)));
        
        commentsContainer.add(row);
    }
}
