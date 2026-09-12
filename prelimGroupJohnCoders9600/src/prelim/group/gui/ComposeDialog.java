package prelim.group.gui;

import prelim.group.model.Email;
import prelim.group.model.EmailCategory;
import prelim.exercises.MyList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ComposeDialog extends JDialog {
    private JTextField senderField;
    private JTextField recipientField;
    private JTextField subjectField;
    private JComboBox<String> categoryCombo;
    private JTextArea bodyArea;
    private boolean sent = false;
    private Email createdEmail = null;

    public ComposeDialog(Frame parent, MyList<EmailCategory> categories) {
        super(parent, "New Message", true);
        setSize(560, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header Panel (Gmail style header)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(242, 246, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel titleLabel = new JLabel("New Message");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Sender
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        formPanel.add(new JLabel("From:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9;
        senderField = new JTextField("user@navimail.com");
        senderField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(senderField, gbc);

        // Recipient
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        formPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9;
        recipientField = new JTextField();
        recipientField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(recipientField, gbc);

        // Subject
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9;
        subjectField = new JTextField();
        subjectField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(subjectField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9;
        categoryCombo = new JComboBox<>(new String[]{"Primary", "Promotions", "Social", "Work", "Personal", "Team", "News"});
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(categoryCombo, gbc);

        // Body
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Body:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        bodyArea = new JTextArea();
        bodyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(bodyArea);
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Footer / Actions Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        footerPanel.setBackground(new Color(242, 246, 252));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setBackground(new Color(11, 87, 208));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sendButton.addActionListener((ActionEvent e) -> {
            String sender = senderField.getText().trim();
            String recipient = recipientField.getText().trim();
            String subject = subjectField.getText().trim();
            String cat = (String) categoryCombo.getSelectedItem();
            String body = bodyArea.getText().trim();

            if (recipient.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter recipient and subject.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = "MSG_" + System.currentTimeMillis();
            String snippet = body.length() > 60 ? body.substring(0, 60) + "..." : body;
            String date = "Just now";

            createdEmail = new Email(id, sender, recipient, subject, snippet, body, date, cat);
            sent = true;
            dispose();
        });

        JButton cancelButton = new JButton("Discard");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        footerPanel.add(sendButton);
        footerPanel.add(cancelButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public boolean isSent() {
        return sent;
    }

    public Email getCreatedEmail() {
        return createdEmail;
    }
}
