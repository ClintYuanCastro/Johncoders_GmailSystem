package prelim.group.gui;


import prelim.group.filehandler.FileHandler;
import prelim.group.login.UITheme;
import prelim.group.model.Email;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;


public class EmailDetailDialog extends JDialog {

    // ---- Palette -----------------------------------------------------
    public static final Color BLUE_DARK   = new Color(6, 50, 79);
    public static final Color WHITE       = Color.WHITE;
    public static final Color TEXT_MUTED  = new Color(120, 130, 140);


    private Email email;
    private Runnable onRefreshCallback;
    private Runnable clearOwnerDim;


    public EmailDetailDialog(Frame owner, Email email, Runnable onRefreshCallback) {
        super(owner, "Email Details", true);
        this.email = email;
        this.onRefreshCallback = onRefreshCallback;


        if (!email.isRead()) {
            email.setRead(true);
            FileHandler.getInstance().updateEmail(email);
        }


        setSize(650, 580);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        clearOwnerDim = UITheme.dimOwner(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                dispose();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent event) {
                closeAndCleanup();
            }
        });

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );


        // Header Panel (Gmail style header)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));


        JLabel lblSubject = new JLabel("Subject: " + email.getSubject());
        lblSubject.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubject.setForeground(BLUE_DARK);


        JPanel metaPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        metaPanel.setOpaque(false);
        JLabel lblFromTo = new JLabel("From: " + email.getSender() + "   |   To: " + email.getRecipient());
        lblFromTo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFromTo.setForeground(TEXT_MUTED);
        JLabel lblDate = new JLabel("Date: " + email.getTimestamp());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(TEXT_MUTED);
        metaPanel.add(lblFromTo);
        metaPanel.add(lblDate);


        headerPanel.add(lblSubject, BorderLayout.NORTH);
        headerPanel.add(metaPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);


        // Body
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));


        JTextArea txtBody = new JTextArea(email.getBody());
        txtBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBody.setEditable(false);
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);
        txtBody.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WHITE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainContent.add(new JScrollPane(txtBody), BorderLayout.CENTER);


        JPanel attachmentsPanel = createAttachmentsPanel(email.getAttachments());
        if (attachmentsPanel != null) {
            mainContent.add(attachmentsPanel, BorderLayout.SOUTH);
        }


        add(mainContent, BorderLayout.CENTER);


        // Bottom Action Bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(WHITE);


        JButton btnToggleRead = new JButton(email.isRead() ? "Mark Unread" : "Mark Read");
        styleActionButton(btnToggleRead);
        btnToggleRead.addActionListener(e -> {
            email.setRead(!email.isRead());
            FileHandler.getInstance().updateEmail(email);
            dispose();
        });


        JButton btnArchive = new JButton(email.isArchived() ? "Unarchive" : "Archive");
        styleActionButton(btnArchive);
        btnArchive.addActionListener(e -> {
            email.setArchived(!email.isArchived());
            FileHandler.getInstance().updateEmail(email);
            dispose();
        });


        JButton btnTrash = new JButton(email.isTrashed() ? "Restore" : "Move to Trash");
        styleActionButton(btnTrash);
        btnTrash.addActionListener(e -> {
            email.setTrashed(!email.isTrashed());
            FileHandler.getInstance().updateEmail(email);
            dispose();
        });


        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());


        bottomPanel.add(btnToggleRead);
        bottomPanel.add(btnArchive);
        bottomPanel.add(btnTrash);
        bottomPanel.add(btnClose);


        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void styleActionButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(BLUE_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private JPanel createAttachmentsPanel(List<String> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return null;
        }

        JPanel attachmentsPanel = new JPanel(new BorderLayout(8, 6));
        attachmentsPanel.setBackground(WHITE);
        attachmentsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JLabel title = new JLabel("Attachments (" + attachments.size() + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(BLUE_DARK);
        attachmentsPanel.add(title, BorderLayout.NORTH);

        JPanel fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        fileListPanel.setBackground(WHITE);

        for (String attachmentPath : attachments) {
            JLabel attachmentLabel = new JLabel(displayAttachmentName(attachmentPath));
            attachmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            attachmentLabel.setForeground(TEXT_MUTED);
            attachmentLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            attachmentLabel.setToolTipText(attachmentPath);
            fileListPanel.add(attachmentLabel);
        }

        attachmentsPanel.add(fileListPanel, BorderLayout.CENTER);
        return attachmentsPanel;
    }


    private String displayAttachmentName(String attachmentPath) {
        if (attachmentPath == null || attachmentPath.trim().isEmpty()) {
            return "(Unknown attachment)";
        }

        File file = new File(attachmentPath);
        String fileName = file.getName();
        return fileName.isEmpty() ? attachmentPath : fileName;
    }


    private void closeAndCleanup() {
        if (clearOwnerDim != null) {
            Runnable toRun = clearOwnerDim;
            clearOwnerDim = null;
            try {
                toRun.run();
            } catch (Exception ignored) {}
        }
        if (onRefreshCallback != null) {
            Runnable toRun = onRefreshCallback;
            onRefreshCallback = null;
            try {
                toRun.run();
            } catch (Exception ignored) {}
        }
    }
}