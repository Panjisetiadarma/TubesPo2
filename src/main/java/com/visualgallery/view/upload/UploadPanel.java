package com.visualgallery.view.upload;

import com.visualgallery.controller.PostController;
import com.visualgallery.utils.ThemeManager;
import com.visualgallery.utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * UploadPanel - Handles photo and video uploads.
 *
 * Supports drag-and-drop and file selection via dialog.
 * Shows preview of selected image.
 *
 * OOP: MVC View
 *
 * @author Visual Gallery Team
 * @version 1.0.0
 */
public class UploadPanel extends JPanel {

    private final PostController postController;
    private final Consumer<String> navigateTo;
    private final ThemeManager tm = ThemeManager.getInstance();

    private File selectedFile;
    private boolean isVideo = false;

    private JLabel previewLabel;
    private JTextField titleField;
    private JTextArea captionArea;
    private JTextField tagsField;
    private JComboBox<String> typeCombo;
    private JButton submitBtn;

    public UploadPanel(PostController postController, Consumer<String> navigateTo) {
        this.postController = postController;
        this.navigateTo = navigateTo;
        buildUI();
        setupDragAndDrop();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(tm.getBackground());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ─── Header ───
        JLabel headerLabel = new JLabel("Upload Karya Anda");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(tm.getTextPrimary());
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // ─── Center: Split Pane (Preview + Form) ───
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        centerPanel.setOpaque(false);

        // Left: File Selection / Preview
        JPanel previewContainer = new JPanel(new BorderLayout(0, 16));
        previewContainer.setOpaque(false);

        previewLabel = new JLabel("<html><center>Tarik & Lepas File Di Sini<br>Atau Klik Untuk Memilih</center></html>");
        previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewLabel.setFont(UIUtils.FONT_HEADING);
        previewLabel.setForeground(tm.getTextMuted());
        previewLabel.setBorder(BorderFactory.createDashedBorder(tm.getBorder(), 3, 2));
        previewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        previewLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { openFileChooser(); }
        });

        previewContainer.add(previewLabel, BorderLayout.CENTER);

        JButton clearBtn = UIUtils.createSecondaryButton("Hapus File");
        clearBtn.addActionListener(e -> clearSelection());
        previewContainer.add(clearBtn, BorderLayout.SOUTH);

        centerPanel.add(previewContainer);

        // Right: Metadata Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        typeCombo = new JComboBox<>(new String[]{"Photo", "Video"});
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeCombo.addActionListener(e -> isVideo = "Video".equals(typeCombo.getSelectedItem()));
        
        addFormRow(formPanel, "Tipe Media", typeCombo);
        
        titleField = UIUtils.createTextField("Judul karya...");
        addFormRow(formPanel, "Judul (Wajib)", titleField);

        captionArea = UIUtils.createTextArea("Ceritakan tentang karya ini...", 5, 20);
        JScrollPane scrollPane = new JScrollPane(captionArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFormRow(formPanel, "Caption", scrollPane);

        tagsField = UIUtils.createTextField("Contoh: nature, potrait, blackandwhite");
        addFormRow(formPanel, "Tags (Pisahkan dengan koma)", tagsField);

        formPanel.add(Box.createVerticalGlue());

        submitBtn = UIUtils.createPrimaryButton("Upload Sekarang");
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        submitBtn.addActionListener(e -> performUpload());
        formPanel.add(submitBtn);

        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.FONT_BOLD);
        label.setForeground(tm.getTextPrimary());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field instanceof JTextField) field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        parent.add(label);
        parent.add(Box.createVerticalStrut(6));
        parent.add(field);
        parent.add(Box.createVerticalStrut(20));
    }

    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        if (isVideo) {
            chooser.setFileFilter(new FileNameExtensionFilter("Video Files", "mp4"));
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "webp"));
        }

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            handleFileSelection(chooser.getSelectedFile());
        }
    }

    private void setupDragAndDrop() {
        previewLabel.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles != null && !droppedFiles.isEmpty()) {
                        handleFileSelection(droppedFiles.get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void handleFileSelection(File file) {
        if (file == null || !file.exists()) return;
        this.selectedFile = file;
        
        String name = file.getName().toLowerCase();
        if (name.endsWith(".mp4")) {
            typeCombo.setSelectedItem("Video");
            previewLabel.setText("Video: " + file.getName());
            previewLabel.setIcon(null);
        } else {
            typeCombo.setSelectedItem("Photo");
            previewLabel.setText("");
            // Load preview asynchronously
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
                @Override protected ImageIcon doInBackground() {
                    return UIUtils.loadScaledImage(file.getAbsolutePath(), previewLabel.getWidth(), previewLabel.getHeight());
                }
                @Override protected void done() {
                    try { previewLabel.setIcon(get()); } catch (Exception ignored) {}
                }
            };
            worker.execute();
        }
    }

    private void clearSelection() {
        selectedFile = null;
        previewLabel.setIcon(null);
        previewLabel.setText("<html><center>Tarik & Lepas File Di Sini<br>Atau Klik Untuk Memilih</center></html>");
        titleField.setText("");
        captionArea.setText("");
        tagsField.setText("");
    }

    private void performUpload() {
        if (selectedFile == null) {
            UIUtils.showError(this, "Pilih file terlebih dahulu.");
            return;
        }

        String title = titleField.getText().trim();
        String caption = captionArea.getText().trim();
        String tags = tagsField.getText().trim();

        submitBtn.setEnabled(false);
        submitBtn.setText("Mengupload...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                if (isVideo) {
                    return postController.uploadVideo(title, caption, tags, selectedFile);
                } else {
                    return postController.uploadPhoto(title, caption, tags, selectedFile);
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    if ("SUCCESS".equals(result)) {
                        UIUtils.showSuccess(UploadPanel.this, "Upload berhasil!");
                        clearSelection();
                        navigateTo.accept("MY_GALLERY");
                    } else {
                        UIUtils.showError(UploadPanel.this, result);
                    }
                } catch (Exception ex) {
                    UIUtils.showError(UploadPanel.this, "Terjadi kesalahan sistem.");
                } finally {
                    submitBtn.setEnabled(true);
                    submitBtn.setText("Upload Sekarang");
                }
            }
        };
        worker.execute();
    }
}
