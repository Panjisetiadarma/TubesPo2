package com.visualgallery.view.explore;

import com.visualgallery.controller.PostController;
import com.visualgallery.model.Account;
import com.visualgallery.model.Post;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;
import com.visualgallery.utils.WrapLayout;
import com.visualgallery.view.components.PostCard;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * ExplorePanel - Shows all posts with search and filter capabilities.
 *
 * Implements Infinite Scroll simulation (Pagination).
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class ExplorePanel extends JPanel {

    private final PostController postController;
    private final Account currentUser;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JPanel gridPanel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JComboBox<String> sortCombo;
    private JButton loadMoreBtn;

    private int currentPage = 1;
    private final int pageSize = 12;
    private boolean isSearching = false;

    public ExplorePanel(PostController postController, Account currentUser) {
        this.postController = postController;
        this.currentUser = currentUser;
        buildUI();
        loadPosts(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ─── Header: Title & Search/Filter ───
        JPanel headerPanel = new JPanel(new BorderLayout(0, 16));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = new JLabel("Explore");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        toolbarPanel.setOpaque(false);

        searchField = UIUtils.createTextField("Cari judul, tag, atau username...");
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e)  { handleSearch(); }
            public void removeUpdate(DocumentEvent e)   { handleSearch(); }
            public void insertUpdate(DocumentEvent e)   { handleSearch(); }
        });

        filterCombo = new JComboBox<>(new String[]{"Semua Media", "Hanya Foto", "Hanya Video"});
        filterCombo.setPreferredSize(new Dimension(150, 36));
        filterCombo.addActionListener(e -> {
            if (searchField.getText().trim().isEmpty()) loadPosts(true);
            else handleSearch();
        });

        sortCombo = new JComboBox<>(new String[]{"Terbaru", "Terpopuler", "Terlama"});
        sortCombo.setPreferredSize(new Dimension(120, 36));
        sortCombo.addActionListener(e -> {
            if (searchField.getText().trim().isEmpty()) loadPosts(true);
        });

        toolbarPanel.add(searchField);
        toolbarPanel.add(filterCombo);
        toolbarPanel.add(sortCombo);

        headerPanel.add(toolbarPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Center: Grid ───
        // We use WrapLayout to automatically wrap items when window resizes
        gridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 20, 20));
        gridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        // Add scroll listener for infinite scroll (simple implementation)
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!isSearching && !e.getValueIsAdjusting()) {
                JScrollBar bar = (JScrollBar) e.getAdjustable();
                int extent = bar.getModel().getExtent();
                int max = bar.getModel().getMaximum();
                if (bar.getValue() + extent >= max - 50) {
                    // Reached near bottom
                    // Note: In a real app, we'd debounce this to prevent multiple loads
                }
            }
        });

        add(scrollPane, BorderLayout.CENTER);

        // ─── Bottom: Load More ───
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        loadMoreBtn = UIUtils.createSecondaryButton("Load More");
        loadMoreBtn.addActionListener(e -> loadPosts(false));
        bottomPanel.add(loadMoreBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPosts(boolean clearFirst) {
        isSearching = false;
        if (clearFirst) {
            currentPage = 1;
            gridPanel.removeAll();
        }

        String sortStr = (String) sortCombo.getSelectedItem();
        String sortParam = "NEWEST";
        if ("Terpopuler".equals(sortStr)) sortParam = "POPULAR";
        else if ("Terlama".equals(sortStr)) sortParam = "OLDEST";

        // If filter is active during normal load, we handle it in memory for simplicity,
        // or we can just fetch all and let the SQL handle it (Search handles SQL filter).
        // Since feed SQL doesn't have media filter yet, we filter in memory here.
        String filterStr = (String) filterCombo.getSelectedItem();
        
        List<Post> posts = postController.getFeedPosts(sortParam, currentPage, pageSize);
        
        if (posts.isEmpty()) {
            if (clearFirst) {
                JLabel empty = new JLabel("Belum ada postingan.");
                empty.setFont(UIUtils.FONT_BODY);
                empty.setForeground(tm.getTextMuted());
                gridPanel.add(empty);
            }
            loadMoreBtn.setVisible(false);
        } else {
            for (Post p : posts) {
                boolean show = true;
                if ("Hanya Foto".equals(filterStr) && !p.isPhoto()) show = false;
                if ("Hanya Video".equals(filterStr) && !p.isVideo()) show = false;
                
                if (show) {
                    gridPanel.add(new PostCard(p, postController, s -> {}));
                }
            }
            currentPage++;
            loadMoreBtn.setVisible(posts.size() == pageSize);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPosts(true);
            return;
        }

        isSearching = true;
        loadMoreBtn.setVisible(false); // Disable pagination during search
        gridPanel.removeAll();

        String filterStr = (String) filterCombo.getSelectedItem();
        String filterParam = "ALL";
        if ("Hanya Foto".equals(filterStr)) filterParam = "PHOTO";
        else if ("Hanya Video".equals(filterStr)) filterParam = "VIDEO";

        List<Post> results = postController.searchPosts(keyword, filterParam);

        if (results.isEmpty()) {
            JLabel empty = new JLabel("Tidak ditemukan hasil untuk: " + keyword);
            empty.setFont(UIUtils.FONT_BODY);
            empty.setForeground(tm.getTextMuted());
            gridPanel.add(empty);
        } else {
            for (Post p : results) {
                gridPanel.add(new PostCard(p, postController, s -> {}));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && !isSearching) {
            // Optional: refresh on show
            // loadPosts(true);
        }
    }
}
