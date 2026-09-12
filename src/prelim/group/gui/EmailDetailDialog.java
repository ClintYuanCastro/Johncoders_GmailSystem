package prelim.group.gui;

import prelim.group.model.Email;
import prelim.group.model.EmailReply;
import prelim.exercises.MyList;

import javax.swing.*;
import java.awt.*;

public class EmailDetailDialog extends JDialog {
    private Email email;
    private JPanel repliesPanel;
    private JPanel attachmentsPanel;

    public EmailDetailDialog(Frame parent, Email email) {
        super(parent, email.getSubject(), true);
        this.email = email;
        this.email.setRead(true); // Mark as read

        setSize(650, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(242, 246, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel subjectLabel = new JLabel(email.getSubject());
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subjectLabel.setForeground(new Color(30, 41, 59));

        JLabel dateLabel = new JLabel(email.getDate() + " | Category: " + email.getCategory());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 116, 139));

        headerPanel.add(subjectLabel, BorderLayout.NORTH);
        headerPanel.add(dateLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel (Main Body)
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainContent.setBackground(Color.WHITE);

        // Sender Info
        JLabel senderInfo = new JLabel("From: " + email.getSender() + " -> To: " + email.getRecipient());
        senderInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        senderInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(senderInfo);

        mainContent.add(Box.createVerticalStrut(12));

        // Body Text
        JTextArea bodyArea = new JTextArea(email.getBody());
        bodyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bodyArea.setEditable(false);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainContent.add(bodyArea);

        mainContent.add(Box.createVerticalStrut(15));

        // Attachments Section (Inner-most MyList<String>)
        JLabel attachHeader = new JLabel("Attachments (Inner MyList<String>):");
        attachHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        attachHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(attachHeader);

        attachmentsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        attachmentsPanel.setBackground(Color.WHITE);
        attachmentsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshAttachments();

        mainContent.add(attachmentsPanel);

        JButton addAttachBtn = new JButton("+ Add Attachment");
        addAttachBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        addAttachBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addAttachBtn.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(this, "Enter attachment filename (e.g. document.pdf):");
            if (fileName != null && !fileName.trim().isEmpty()) {
                email.addAttachment(fileName.trim());
                refreshAttachments();
            }
        });
        mainContent.add(addAttachBtn);

        mainContent.add(Box.createVerticalStrut(15));

        // Replies Section (Inner-most MyList<EmailReply>)
        JLabel repliesHeader = new JLabel("Replies Thread (Inner MyList<EmailReply>):");
        repliesHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        repliesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(repliesHeader);

        repliesPanel = new JPanel();
        repliesPanel.setLayout(new BoxLayout(repliesPanel, BoxLayout.Y_AXIS));
        repliesPanel.setBackground(Color.WHITE);
        repliesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshReplies();

        mainContent.add(repliesPanel);

        // Add Reply Button
        JButton addReplyBtn = new JButton("+ Reply to Thread");
        addReplyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addReplyBtn.setBackground(new Color(11, 87, 208));
        addReplyBtn.setForeground(Color.WHITE);
        addReplyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addReplyBtn.addActionListener(e -> {
            String replyText = JOptionPane.showInputDialog(this, "Enter your reply message:");
            if (replyText != null && !replyText.trim().isEmpty()) {
                EmailReply reply = new EmailReply("user@navimail.com", "Just now", replyText.trim());
                email.addReply(reply);
                refreshReplies();
            }
        });
        mainContent.add(Box.createVerticalStrut(8));
        mainContent.add(addReplyBtn);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshAttachments() {
        attachmentsPanel.removeAll();
        MyList<String> list = email.getAttachments();
        if (list.getSize() == 0) {
            JLabel emptyLabel = new JLabel("No attachments.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            attachmentsPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < list.getSize(); i++) {
                JLabel tag = new JLabel("📎 " + list.getElement(i));
                tag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                tag.setOpaque(true);
                tag.setBackground(new Color(238, 242, 246));
                tag.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                attachmentsPanel.add(tag);
            }
        }
        attachmentsPanel.revalidate();
        attachmentsPanel.repaint();
    }

    private void refreshReplies() {
        repliesPanel.removeAll();
        MyList<EmailReply> list = email.getReplies();
        if (list.getSize() == 0) {
            JLabel emptyLabel = new JLabel("No replies yet.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            repliesPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < list.getSize(); i++) {
                EmailReply reply = list.getElement(i);
                JPanel rCard = new JPanel(new BorderLayout());
                rCard.setBackground(new Color(248, 250, 252));
                rCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));

                JLabel rHead = new JLabel(reply.getSender() + " (" + reply.getDate() + "):");
                rHead.setFont(new Font("Segoe UI", Font.BOLD, 11));

                JLabel rBody = new JLabel(reply.getContent());
                rBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                rCard.add(rHead, BorderLayout.NORTH);
                rCard.add(rBody, BorderLayout.SOUTH);

                repliesPanel.add(rCard);
                repliesPanel.add(Box.createVerticalStrut(6));
            }
        }
        repliesPanel.revalidate();
        repliesPanel.repaint();
    }
}
