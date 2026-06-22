package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import model.Post;
import utils.Theme;

public class FeedPanel extends JPanel {
    private JPanel contentPanel;

    public FeedPanel(List<Post> posts) {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(0, 50, 0, 50));

        JScrollPane feedScroll = new JScrollPane(contentPanel);
        feedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        feedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        feedScroll.setBorder(null);
        feedScroll.setOpaque(false);
        feedScroll.getViewport().setOpaque(false);
        feedScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(feedScroll, BorderLayout.CENTER);
        
        refresh(posts);
    }
    
    public void refresh(List<Post> posts) {
        contentPanel.removeAll();
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (Post post : posts) {
            PostPanel postPanel = new PostPanel(post);
            postPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            contentPanel.add(postPanel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
