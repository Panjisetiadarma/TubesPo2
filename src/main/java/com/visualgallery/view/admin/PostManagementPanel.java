package com.visualgallery.view.admin;

import com.visualgallery.controller.PostController;
import com.visualgallery.model.Post;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PostManagementPanel - Admin interface for managing posts.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class PostManagementPanel extends JPanel {

    private final PostController postController;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JTable postTable;
    private DefaultTableModel tableModel;

    public PostManagementPanel(PostController postController) {
        this.postController = postController;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Manajemen Postingan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh Data");
        refreshBtn.addActionListener(e -> refreshData());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Uploader", "Judul", "Tipe", "Tanggal", "Likes", "Komentar"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        postTable = new JTable(tableModel);
        UIUtils.styleTable(postTable);

        JScrollPane scrollPane = UIUtils.createTableScrollPane(postTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        JButton deleteBtn = UIUtils.createDangerButton("Hapus Postingan");
        deleteBtn.addActionListener(e -> deleteSelectedPost());
        bottomPanel.add(deleteBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        List<Post> posts = postController.getAllPostsForAdmin();
        for (Post p : posts) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getUploaderUsername(),
                p.getTitle(),
                p.getMediaType(),
                p.getUploadDate().toLocalDate().toString(),
                p.getTotalLikes(),
                p.getTotalComments()
            });
        }
    }

    private void deleteSelectedPost() {
        int row = postTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarning(this, "Pilih postingan terlebih dahulu.");
            return;
        }
        int postId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 2);

        if (UIUtils.showConfirm(this, "Hapus permanen postingan '" + title + "'?")) {
            if (postController.deletePost(postId)) {
                UIUtils.showSuccess(this, "Postingan dihapus.");
                refreshData();
            } else {
                UIUtils.showError(this, "Gagal menghapus postingan.");
            }
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) refreshData();
    }
}
