package view;

import model.Photo;
import utils.ProfileTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoGridPanel extends JPanel {
    private final List<Photo> allPhotos;
    private List<Photo> visible;
    private int columns = 3;

    public PhotoGridPanel(List<Photo> photos) {
        this.allPhotos = photos;
        setBackground(ProfileTheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        showAll();
    }

    @Override
    public Dimension getPreferredSize() {
        int w = getParent() != null ? getParent().getWidth() : getWidth();
        if (w <= 0) return super.getPreferredSize();
        
        int hGaps = (columns - 1) * 4;
        int cellWidth = (w - 8 - hGaps) / columns;
        if (cellWidth <= 0) cellWidth = 200;
        
        int cellHeight = cellWidth * 3 / 4;
        int rows = (int) Math.ceil((double) visible.size() / columns);
        if (rows == 0) rows = 1;
        
        int totalHeight = rows * cellHeight + (rows - 1) * 4 + 8;
        return new Dimension(w, totalHeight);
    }

    private void populate() {
        setLayout(new GridLayout(0, columns, 4, 4));
        removeAll();
        for (Photo p : visible) {
            PhotoCard card = new PhotoCard(p);
            // Right-click context menu for favorite/save
            JPopupMenu menu = new JPopupMenu();
            JMenuItem favItem = new JMenuItem(p.isFavorite() ? "★ Hapus dari Favorit" : "☆ Tambah ke Favorit");
            favItem.addActionListener(e -> {
                p.setFavorite(!p.isFavorite());
                favItem.setText(p.isFavorite() ? "★ Hapus dari Favorit" : "☆ Tambah ke Favorit");
                
                try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                    java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                    psUser.setString(1, utils.Session.getCurrentUser());
                    java.sql.ResultSet rs = psUser.executeQuery();
                    if (rs.next()) {
                        int uid = rs.getInt("id");
                        if (p.isFavorite()) {
                            java.sql.PreparedStatement psFav = conn.prepareStatement("INSERT IGNORE INTO post_saves (user_id, post_id) VALUES (?, ?)");
                            psFav.setInt(1, uid);
                            psFav.setInt(2, p.getId());
                            psFav.executeUpdate();
                        } else {
                            java.sql.PreparedStatement psFav = conn.prepareStatement("DELETE FROM post_saves WHERE user_id = ? AND post_id = ?");
                            psFav.setInt(1, uid);
                            psFav.setInt(2, p.getId());
                            psFav.executeUpdate();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            JMenuItem saveItem = new JMenuItem(p.isSaved() ? "✔ Hapus dari Tersimpan" : "📌 Simpan Foto");
            saveItem.addActionListener(e -> {
                p.setSaved(!p.isSaved());
                saveItem.setText(p.isSaved() ? "✔ Hapus dari Tersimpan" : "📌 Simpan Foto");
                
                try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
                    java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                    psUser.setString(1, utils.Session.getCurrentUser());
                    java.sql.ResultSet rs = psUser.executeQuery();
                    if (rs.next()) {
                        int uid = rs.getInt("id");
                        if (p.isSaved()) {
                            java.sql.PreparedStatement psSave = conn.prepareStatement("INSERT IGNORE INTO post_saves (user_id, post_id) VALUES (?, ?)");
                            psSave.setInt(1, uid);
                            psSave.setInt(2, p.getId());
                            psSave.executeUpdate();
                        } else {
                            java.sql.PreparedStatement psSave = conn.prepareStatement("DELETE FROM post_saves WHERE user_id = ? AND post_id = ?");
                            psSave.setInt(1, uid);
                            psSave.setInt(2, p.getId());
                            psSave.executeUpdate();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            menu.add(favItem);
            menu.add(saveItem);
            card.setComponentPopupMenu(menu);
            add(card);
        }
        revalidate();
        repaint();
    }

    public void setColumns(int cols) {
        this.columns = cols;
        populate();
    }

    public void filterBySearch(String query) {
        visible = new ArrayList<>();
        for (Photo p : allPhotos) {
            if (query.isEmpty() || p.getName().toLowerCase().contains(query.toLowerCase()))
                visible.add(p);
        }
        populate();
    }

    public void filterLiked() {
        visible = new ArrayList<>();
        for (Photo p : allPhotos) if (p.isLiked()) visible.add(p);
        populate();
        if (getComponentCount() == 0) showEmpty("Belum ada foto yang disukai.\nDouble-klik foto untuk menyukai.");
    }

    public void filterSaved() {
        visible = new ArrayList<>();
        for (Photo p : allPhotos) if (p.isSaved()) visible.add(p);
        populate();
        if (getComponentCount() == 0) showEmpty("Belum ada foto tersimpan.\nKlik kanan pada foto → Simpan Foto.");
    }

    public void showAll() {
        visible = new ArrayList<>();
        for (Photo p : allPhotos) {
            if (p.isOwnPost()) visible.add(p);
        }
        populate();
        if (getComponentCount() == 0) showEmpty("Belum ada foto yang diposting.");
    }

    private void showEmpty(String msg) {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("<html><center>" + msg.replace("\n", "<br>") + "</center></html>", SwingConstants.CENTER);
        lbl.setForeground(ProfileTheme.TEXT_SECONDARY);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(lbl, BorderLayout.CENTER);
    }

    public int getPhotoCount() { return allPhotos.size(); }
    public List<Photo> getAllPhotos() { return allPhotos; }
}
