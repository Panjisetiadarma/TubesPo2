package controller;

import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserController {
    private List<User> users;
    private User currentUser;

    public UserController() {
        users = new ArrayList<>();
        loadUsersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        try (java.sql.Connection conn = utils.DatabaseConnection.connect();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             java.sql.ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                User u = new User(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("username"),
                    rs.getString("location") != null ? rs.getString("location") : "Unknown",
                    rs.getString("full_name"),
                    rs.getString("bio"),
                    rs.getString("profile_picture_url")
                );
                users.add(u);
                
                if (rs.getString("username").equals(utils.Session.getCurrentUser())) {
                    currentUser = u;
                }
            }
            
            if (currentUser == null && !users.isEmpty()) {
                currentUser = users.get(0);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            loadUsersFromDatabase();
        }
        return currentUser;
    }
}
