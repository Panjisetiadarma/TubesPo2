package model;

public class Comment {
    private int id;
    private User user;
    private String text;
    private String date;

    public Comment(int id, User user, String text, String date) {
        this.id = id;
        this.user = user;
        this.text = text;
        this.date = date;
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public String getText() { return text; }
    public String getDate() { return date; }
}
