package com.visualgallery.view.admin;

import com.visualgallery.controller.PostController;
import com.visualgallery.controller.UserController;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * StatisticsPanel - Admin interface for visualizing data using JFreeChart.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class StatisticsPanel extends JPanel {

    private final PostController postController;
    private final UserController userController;
    private final ThemeManager tm = ThemeManager.getInstance();

    private JPanel chartContainer;

    public StatisticsPanel(PostController postController, UserController userController) {
        this.postController = postController;
        this.userController = userController;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Laporan Statistik");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(tm.getTextPrimary());
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        chartContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartContainer.setOpaque(false);
        add(chartContainer, BorderLayout.CENTER);
    }

    private void loadCharts() {
        chartContainer.removeAll();

        // 1. Media Type Distribution (Pie Chart)
        DefaultPieDataset mediaData = new DefaultPieDataset();
        mediaData.setValue("Photo", postController.getTotalPhotos());
        mediaData.setValue("Video", postController.getTotalVideos());

        JFreeChart mediaChart = ChartFactory.createPieChart(
                "Distribusi Tipe Media", mediaData, true, true, false);
        ChartPanel mediaPanel = new ChartPanel(mediaChart);
        chartContainer.add(mediaPanel);

        // 2. User Roles Distribution (Pie Chart)
        DefaultPieDataset userData = new DefaultPieDataset();
        userData.setValue("User", userController.getTotalUsers() - userController.getTotalAdmins());
        userData.setValue("Admin", userController.getTotalAdmins());

        JFreeChart userChart = ChartFactory.createPieChart(
                "Distribusi Role Akun", userData, true, true, false);
        ChartPanel userPanel = new ChartPanel(userChart);
        chartContainer.add(userPanel);

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) loadCharts();
    }
}
