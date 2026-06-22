package controller;

import java.util.ArrayList;
import java.util.List;
import model.Post;
import java.sql.Timestamp;

public class PostController {
    private List<Post> posts;
    private UserController userController;

    public PostController(UserController userController) {
        this.userController = userController;
        posts = new ArrayList<>();
        
        loadPostsFromDatabase();
    }

    private void loadPostsFromDatabase() {
        int currentUserId = -1;
        try (java.sql.Connection conn = utils.DatabaseConnection.connect()) {
            java.sql.PreparedStatement psUser = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            psUser.setString(1, utils.Session.getCurrentUser());
            java.sql.ResultSet rsUser = psUser.executeQuery();
            if (rsUser.next()) currentUserId = rsUser.getInt("id");
            
            java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, " +
                "(SELECT COUNT(*) FROM post_likes WHERE post_id = p.id) as total_likes, " +
                "(SELECT COUNT(*) FROM post_likes WHERE post_id = p.id AND user_id = ?) as is_liked, " +
                "(SELECT COUNT(*) FROM post_saves WHERE post_id = p.id AND user_id = ?) as is_saved " +
                "FROM posts p ORDER BY p.created_at DESC");
            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);
            
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String userIdStr = String.valueOf(rs.getInt("user_id"));
                model.User postUser = null;
                for (model.User u : userController.getUsers()) {
                    if (u.getId().equals(userIdStr)) {
                        postUser = u;
                        break;
                    }
                }
                
                if (postUser == null) postUser = userController.getCurrentUser();
                
                Timestamp postTs = rs.getTimestamp("created_at");
                Post p = new Post(
                    String.valueOf(rs.getInt("id")),
                    postUser,
                    rs.getString("caption"),
                    getRelativeTime(postTs), 
                    rs.getString("image_path")
                );
                
                // Set likes langsung dari DB (bukan loop p.like())
                int dbLikes = rs.getInt("total_likes");
                p.setLikesCount(dbLikes);
                p.setLikedByCurrentUser(rs.getInt("is_liked") > 0);
                
                p.setSaved(rs.getInt("is_saved") > 0);
                
                // fetch comments
                java.sql.PreparedStatement psComm = conn.prepareStatement(
                    "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.created_at ASC"
                );
                psComm.setInt(1, rs.getInt("id"));
                java.sql.ResultSet rsComm = psComm.executeQuery();
                while (rsComm.next()) {
                    model.User cUser = new model.User("-1", rsComm.getString("username"), "Unknown", null, null, null);
                    Timestamp commTs = rsComm.getTimestamp("created_at");
                    p.addComment(new model.Comment(rsComm.getInt("id"), cUser, rsComm.getString("text"), getRelativeTime(commTs)));
                }
                
                posts.add(p);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public static String getRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "Baru saja";
        long now = System.currentTimeMillis();
        long time = timestamp.getTime();
        long diff = now - time;
        
        if (diff < 1000) return "Baru saja";
        if (diff < 60000) return (diff / 1000) + " detik yang lalu";
        if (diff < 3600000) return (diff / 60000) + " menit yang lalu";
        if (diff < 86400000) return (diff / 3600000) + " jam yang lalu";
        if (diff < 604800000) return (diff / 86400000) + " hari yang lalu";
        return (diff / 604800000) + " minggu yang lalu";
    }

    public List<Post> getPosts() { 
        posts.clear();
        loadPostsFromDatabase();
        return posts; 
    }
}
