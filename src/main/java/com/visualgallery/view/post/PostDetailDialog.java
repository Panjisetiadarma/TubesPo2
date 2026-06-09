package com.visualgallery.view.post;

import com.visualgallery.controller.PostController;
import com.visualgallery.model.Comment;
import com.visualgallery.model.Post;
import com.visualgallery.utils.SessionManager;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * PostDetailDialog - Dialog for viewing a post in full detail.
 *
 * Shows full image/video, description, like button, and comments.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class PostDetailDialog extends JDialog {

    private final Post post;
    private final PostController postController;
    private final ThemeManager tm = ThemeManager.getInstance();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private JLabel likesLabel;
    private JButton likeBtn;
    private JPanel commentsContainer;
    private JTextField commentInput;

    public PostDetailDialog(JFrame parent, Post post, PostController postController) {
        super(parent, true); // Modal
        this.post = postController.getPostById(post.getId()); // Refresh data
        this.postController = postController;
        
        initDialog();
        buildUI();
        loadComments();
    }

    private void initDialog() {
        setTitle(post.getTitle());
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setResizable(false); // Fixed size for simplicity in design
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(tm.getBackground());
        setContentPane(root);

        // ─── Left Panel: Media ───
        JPanel mediaPanel = new JPanel(new BorderLayout());
        mediaPanel.setBackground(Color.BLACK);
        mediaPanel.setPreferredSize(new Dimension(650, 700));

        JLabel mediaLabel = new JLabel();
        mediaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                if (post.isVideo()) {
                    return UIUtils.createPlaceholderIcon(600, 600); // Video placeholder
                } else {
                    return UIUtils.loadScaledImage(post.getMediaPath(), 650, 700); // Scale to fit
                }
            }
            @Override
            protected void done() {
                try { mediaLabel.setIcon(get()); } catch (Exception ignored) {}
            }
        };
        worker.execute();
        
        mediaPanel.add(mediaLabel, BorderLayout.CENTER);
        root.add(mediaPanel, BorderLayout.WEST);

        // ─── Right Panel: Info & Comments ───
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(tm.getSurface());
        rightPanel.setPreferredSize(new Dimension(350, 700));
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, tm.getBorder()));

        // --- Header (Uploader Info) ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, tm.getBorder()),
            new EmptyBorder(16, 16, 16, 16)
        ));

        ImageIcon avatar = UIUtils.createCircularIcon(post.getUploaderProfilePicture(), 36);
        JLabel avatarLabel = new JLabel(avatar);
        
        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel(post.getUploaderUsername());
        nameLabel.setFont(UIUtils.FONT_BOLD);
        nameLabel.setForeground(tm.getTextPrimary());
        JLabel dateLabel = new JLabel(post.getUploadDate().toLocalDate().toString());
        dateLabel.setFont(UIUtils.FONT_SMALL);
        dateLabel.setForeground(tm.getTextMuted());
        namePanel.add(nameLabel);
        namePanel.add(dateLabel);

        headerPanel.add(avatarLabel, BorderLayout.WEST);
        headerPanel.add(namePanel, BorderLayout.CENTER);

        // Optional Delete button for owner/admin
        if (sessionManager.isLoggedIn() && 
            (sessionManager.getCurrentUserId() == post.getUserId() || sessionManager.isAdmin())) {
            JButton delBtn = UIUtils.createDangerButton("Hapus");
            delBtn.setFont(UIUtils.FONT_SMALL);
            delBtn.setPreferredSize(new Dimension(70, 25));
            delBtn.addActionListener(e -> deletePost());
            headerPanel.add(delBtn, BorderLayout.EAST);
        }

        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Center: Caption & Comments List ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(tm.getSurface());

        // Caption section
        JPanel captionPanel = new JPanel(new BorderLayout());
        captionPanel.setOpaque(false);
        captionPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        
        JLabel titleLabel = new JLabel(post.getTitle());
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(tm.getTextPrimary());
        
        JTextArea captionArea = new JTextArea(post.getCaption());
        captionArea.setFont(UIUtils.FONT_BODY);
        captionArea.setForeground(tm.getTextPrimary());
        captionArea.setLineWrap(true);
        captionArea.setWrapStyleWord(true);
        captionArea.setEditable(false);
        captionArea.setOpaque(false);

        JLabel tagsLabel = new JLabel(post.getTags());
        tagsLabel.setFont(UIUtils.FONT_SMALL);
        tagsLabel.setForeground(ThemeManager.ACCENT);

        JPanel captionWrapper = new JPanel(new BorderLayout(0, 8));
        captionWrapper.setOpaque(false);
        captionWrapper.add(titleLabel, BorderLayout.NORTH);
        captionWrapper.add(captionArea, BorderLayout.CENTER);
        captionWrapper.add(tagsLabel, BorderLayout.SOUTH);
        
        captionPanel.add(captionWrapper, BorderLayout.NORTH);
        centerPanel.add(captionPanel);

        // Divider
        JSeparator sep = UIUtils.createSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        centerPanel.add(sep);

        // Comments Container
        commentsContainer = new JPanel();
        commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        commentsContainer.setBackground(tm.getSurface());
        
        JScrollPane scrollPane = new JScrollPane(commentsContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerPanel.add(scrollPane);

        rightPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom: Actions & Add Comment ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, tm.getBorder()));

        // Actions Row (Like)
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        actionsRow.setOpaque(false);

        boolean isLiked = post.isLikedByCurrentUser();
        likeBtn = new JButton(isLiked ? "♥" : "♡");
        likeBtn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        likeBtn.setForeground(isLiked ? ThemeManager.ERROR : tm.getTextPrimary());
        likeBtn.setContentAreaFilled(false);
        likeBtn.setBorderPainted(false);
        likeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        likeBtn.addActionListener(e -> toggleLike());
        
        likesLabel = new JLabel(post.getTotalLikes() + " suka");
        likesLabel.setFont(UIUtils.FONT_BOLD);
        likesLabel.setForeground(tm.getTextPrimary());

        actionsRow.add(likeBtn);
        actionsRow.add(likesLabel);
        bottomPanel.add(actionsRow, BorderLayout.NORTH);

        // Comment Input Row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.setBorder(new EmptyBorder(0, 16, 16, 16));

        commentInput = UIUtils.createTextField("Tambahkan komentar...");
        JButton sendBtn = UIUtils.createPrimaryButton("Kirim");
        sendBtn.setPreferredSize(new Dimension(80, 38));
        
        sendBtn.addActionListener(e -> postComment());
        commentInput.addActionListener(e -> postComment()); // Enter key

        if (!sessionManager.isLoggedIn()) {
            commentInput.setEnabled(false);
            commentInput.setText("Login untuk berkomentar");
            sendBtn.setEnabled(false);
        }

        inputRow.add(commentInput, BorderLayout.CENTER);
        inputRow.add(sendBtn, BorderLayout.EAST);
        bottomPanel.add(inputRow, BorderLayout.SOUTH);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);
        root.add(rightPanel, BorderLayout.EAST);
    }

    private void loadComments() {
        commentsContainer.removeAll();
        
        List<Comment> comments = postController.getComments(post.getId());
        if (comments.isEmpty()) {
            JLabel empty = new JLabel("Belum ada komentar.");
            empty.setFont(UIUtils.FONT_BODY);
            empty.setForeground(tm.getTextMuted());
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(new EmptyBorder(20, 0, 20, 0));
            commentsContainer.add(empty);
        } else {
            for (Comment c : comments) {
                commentsContainer.add(buildCommentPanel(c));
            }
        }
        
        commentsContainer.revalidate();
        commentsContainer.repaint();
    }

    private JPanel buildCommentPanel(Comment c) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 16, 12, 16));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel avatar = new JLabel(UIUtils.createCircularIcon(c.getCommenterProfilePicture(), 30));
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        JLabel name = new JLabel(c.getCommenterUsername());
        name.setFont(UIUtils.FONT_BOLD);
        name.setForeground(tm.getTextPrimary());
        
        JTextArea text = new JTextArea(c.getCommentText());
        text.setFont(UIUtils.FONT_BODY);
        text.setForeground(tm.getTextPrimary());
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setOpaque(false);

        textPanel.add(name, BorderLayout.NORTH);
        textPanel.add(text, BorderLayout.CENTER);

        p.add(avatar, BorderLayout.WEST);
        p.add(textPanel, BorderLayout.CENTER);

        // Delete button for comment owner or admin
        if (sessionManager.isLoggedIn() && 
            (sessionManager.getCurrentUserId() == c.getUserId() || sessionManager.isAdmin())) {
            JLabel delIcon = new JLabel("✕");
            delIcon.setForeground(tm.getTextMuted());
            delIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            delIcon.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (UIUtils.showConfirm(PostDetailDialog.this, "Hapus komentar?")) {
                        if (postController.deleteComment(c.getId(), post.getId())) {
                            loadComments();
                        }
                    }
                }
            });
            p.add(delIcon, BorderLayout.EAST);
        }

        return p;
    }

    private void toggleLike() {
        if (!sessionManager.isLoggedIn()) {
            UIUtils.showWarning(this, "Anda harus login untuk menyukai postingan.");
            return;
        }
        boolean liked = postController.toggleLike(post.getId());
        post.setLikedByCurrentUser(liked);
        post.setTotalLikes(post.getTotalLikes() + (liked ? 1 : -1));
        
        likeBtn.setText(liked ? "♥" : "♡");
        likeBtn.setForeground(liked ? ThemeManager.ERROR : tm.getTextPrimary());
        likesLabel.setText(post.getTotalLikes() + " suka");
    }

    private void postComment() {
        String text = commentInput.getText().trim();
        if (text.isEmpty()) return;
        
        int id = postController.addComment(post.getId(), text);
        if (id > 0) {
            commentInput.setText("");
            loadComments();
        } else {
            UIUtils.showError(this, "Gagal menambahkan komentar.");
        }
    }

    private void deletePost() {
        if (UIUtils.showConfirm(this, "Apakah Anda yakin ingin menghapus postingan ini secara permanen?")) {
            if (postController.deletePost(post.getId())) {
                UIUtils.showSuccess(this, "Postingan dihapus.");
                dispose();
            } else {
                UIUtils.showError(this, "Gagal menghapus postingan.");
            }
        }
    }
}
