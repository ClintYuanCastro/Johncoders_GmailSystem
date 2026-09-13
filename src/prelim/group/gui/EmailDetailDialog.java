package prelim.group.gui;

import prelim.group.filehandler.FileHandler;
import prelim.group.model.Email;

import javax.swing.*;
import java.awt.*;

public class EmailDetailDialog extends JDialog {
    private static final Color HEADER_BG = new Color(242, 246, 252);
    private static final Color TITLE_TEXT = new Color(30, 41, 59);
    private static final Color MUTED_TEXT = new Color(100, 116, 139);
    private static final Color ACCENT_BLUE = new Color(11, 87, 208);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);

    private Email email;
    private Runnable onRefreshCallback;

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

        // Header Panel (Gmail style header)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblSubject = new JLabel("Subject: " + email.getSubject());
        lblSubject.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubject.setForeground(TITLE_TEXT);

        JPanel metaPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        metaPanel.setOpaque(false);
        JLabel lblFromTo = new JLabel("From: " + email.getSender() + "   |   To: " + email.getRecipient());
        lblFromTo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFromTo.setForeground(MUTED_TEXT);
        JLabel lblDate = new JLabel("Date: " + email.getTimestamp());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(MUTED_TEXT);
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
                BorderFactory.createLineBorder(BORDER_LIGHT),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainContent.add(new JScrollPane(txtBody), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Bottom Action Bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(HEADER_BG);

        JButton btnToggleRead = new JButton(email.isRead() ? "Mark Unread" : "Mark Read");
        styleActionButton(btnToggleRead);
        btnToggleRead.addActionListener(e -> {
            email.setRead(!email.isRead());
            FileHandler.getInstance().updateEmail(email);
            if (onRefreshCallback != null) onRefreshCallback.run();
            dispose();
        });

        JButton btnArchive = new JButton(email.isArchived() ? "Unarchive" : "Archive");
        styleActionButton(btnArchive);
        btnArchive.addActionListener(e -> {
            email.setArchived(!email.isArchived());
            FileHandler.getInstance().updateEmail(email);
            if (onRefreshCallback != null) onRefreshCallback.run();
            dispose();
        });

        JButton btnTrash = new JButton(email.isTrashed() ? "Restore" : "Move to Trash");
        styleActionButton(btnTrash);
        btnTrash.addActionListener(e -> {
            email.setTrashed(!email.isTrashed());
            FileHandler.getInstance().updateEmail(email);
            if (onRefreshCallback != null) onRefreshCallback.run();
            dispose();
        });

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(ACCENT_BLUE);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
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
        btn.setForeground(TITLE_TEXT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
