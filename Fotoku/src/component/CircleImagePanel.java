package component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class CircleImagePanel extends JPanel {
    private ImageIcon image;
    public CircleImagePanel(ImageIcon image, int size) {
        this.image = image;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setOpaque(false);
    }
    
    public void setImage(ImageIcon image) {
        this.image = image;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int size = Math.min(getWidth(), getHeight());
        
        Shape clip = new Ellipse2D.Float(0, 0, size, size);
        g2.setClip(clip);
        g2.drawImage(image.getImage(), 0, 0, size, size, null);
        
        g2.dispose();
    }
}
