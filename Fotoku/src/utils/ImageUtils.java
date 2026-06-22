package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class ImageUtils {

    /**
     * Scale BufferedImage dengan kualitas tinggi menggunakan teknik
     * progressive downscaling (halving bertahap) dan interpolasi BICUBIC.
     *
     * Metode ini jauh lebih tajam dibanding single-step scaling,
     * terutama saat memperkecil gambar lebih dari 50%.
     *
     * @param src     Gambar sumber
     * @param targetW Lebar target
     * @param targetH Tinggi target
     * @return BufferedImage dengan kualitas tinggi
     */
    public static BufferedImage highQualityScale(BufferedImage src, int targetW, int targetH) {
        if (src == null) return null;

        int currentW = src.getWidth();
        int currentH = src.getHeight();

        // Jangan upscale jika sudah lebih kecil dari target
        if (currentW <= targetW && currentH <= targetH) {
            return src;
        }

        BufferedImage result = src;

        // Progressive downscaling: kurangi separuh-separuh sampai mendekati target
        // Ini menghasilkan kualitas jauh lebih baik dari direct single-step scale
        while (currentW / 2 > targetW || currentH / 2 > targetH) {
            int nextW = Math.max(currentW / 2, targetW);
            int nextH = Math.max(currentH / 2, targetH);

            BufferedImage tmp = createScaledStep(result, nextW, nextH);
            result = tmp;
            currentW = nextW;
            currentH = nextH;
        }

        // Langkah akhir ke ukuran persis target
        if (result.getWidth() != targetW || result.getHeight() != targetH) {
            result = createScaledStep(result, targetW, targetH);
        }

        return result;
    }

    /**
     * Satu langkah scaling dengan rendering hints kualitas tinggi (BICUBIC).
     */
    private static BufferedImage createScaledStep(BufferedImage src, int w, int h) {
        BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return tmp;
    }

    /**
     * Hitung dimensi target untuk muat dalam kotak maxSize x maxSize,
     * sambil mempertahankan aspect ratio asli.
     * Jika gambar lebih kecil dari maxSize, dimensi asli dikembalikan (tidak upscale).
     */
    public static int[] calcTargetDimensions(int origW, int origH, int maxSize) {
        if (origW <= 0 || origH <= 0) return new int[]{maxSize, maxSize};

        if (origW <= maxSize && origH <= maxSize) {
            // Gambar sudah cukup kecil — pertahankan resolusi asli
            return new int[]{origW, origH};
        }

        double ratio = (double) origW / origH;
        int targetW, targetH;
        if (ratio > 1.0) {          // landscape
            targetW = maxSize;
            targetH = (int) (maxSize / ratio);
        } else {                    // portrait atau square
            targetH = maxSize;
            targetW = (int) (maxSize * ratio);
        }
        return new int[]{Math.max(1, targetW), Math.max(1, targetH)};
    }

    /**
     * Buat ImageIcon placeholder (dummy) dengan warna solid.
     */
    public static ImageIcon createDummyImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon loadIcon(String path, int w, int h) {
        try {
            ImageIcon raw = new ImageIcon(path);
            if (raw.getIconWidth() <= 0) return makeFallback(w, h, "?");
            return new ImageIcon(raw.getImage().getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return makeFallback(w, h, "?");
        }
    }

    private static ImageIcon makeFallback(int w, int h, String text) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(200, 200, 200));
        g.fillOval(0, 0, w, h);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        g.drawString(text, 7, 16);
        g.dispose();
        return new ImageIcon(img);
    }
}
