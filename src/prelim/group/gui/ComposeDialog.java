package prelim.group.gui;

import prelim.group.filehandler.FileHandler;
import prelim.group.model.Email;
import prelim.group.userHandling.CurrentUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ComposeDialog extends JDialog {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    // Gmail-style palette matching System 1
    private static final Color HEADER_BG = new Color(242, 246, 252);
    private static final Color ACCENT_BLUE = new Color(11, 87, 208);
    private static final Color TITLE_TEXT = new Color(30, 41, 59);

    private JTextField txtRecipient;
    private JTextField txtSubject;
    private JTextArea txtBody;
    private JCheckBox chkImportant;
    private JLabel lblAttachments;
    private List<String> attachmentPaths;
    private Email existingDraft;
    private Runnable onCompleteCallback;

    public ComposeDialog(Frame owner, Email draft, Runnable onCompleteCallback) {
        super(owner, draft != null ? "Edit Draft" : "New Message", true);
        this.existingDraft = draft;
        this.onCompleteCallback = onCompleteCallback;
        this.attachmentPaths = new ArrayList<>();

        setSize(580, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Header Panel (Gmail style header)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel titleLabel = new JLabel(draft != null ? "Edit Draft" : "New Message");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TITLE_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Recipient (To:)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        formPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9;
        txtRecipient = new JTextField();
        txtRecipient.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtRecipient, gbc);

        // Subject
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        formPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.9;
        txtSubject = new JTextField();
        txtSubject.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtSubject, gbc);

        // Header Action Bar: Attach File / Mark as Important / attachment count
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel headerToolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerToolsPanel.setBackground(Color.WHITE);

        JButton btnAddAttachment = new JButton("Attach File...");
        btnAddAttachment.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAddAttachment.setFocusPainted(false);
        btnAddAttachment.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddAttachment.addActionListener(e -> chooseAttachment());
        headerToolsPanel.add(btnAddAttachment);

        chkImportant = new JCheckBox("Mark as Important");
        chkImportant.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkImportant.setBackground(Color.WHITE);
        headerToolsPanel.add(chkImportant);

        lblAttachments = new JLabel("No attachments");
        lblAttachments.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAttachments.setForeground(new Color(100, 116, 139));
        headerToolsPanel.add(lblAttachments);

        formPanel.add(headerToolsPanel, gbc);

        // Body
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Body:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.9; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtBody = new JTextArea();
        txtBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtBody);
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Footer / Actions Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        footerPanel.setBackground(HEADER_BG);

        JButton btnSend = new JButton("Send");
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSend.setBackground(ACCENT_BLUE);
        btnSend.setForeground(Color.WHITE);
        btnSend.setFocusPainted(false);
        btnSend.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.addActionListener((ActionEvent e) -> processSend());

        JButton btnSaveDraft = new JButton("Save Draft");
        btnSaveDraft.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSaveDraft.setFocusPainted(false);
        btnSaveDraft.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveDraft.addActionListener(e -> processSaveDraft());

        JButton btnDiscard = new JButton("Discard");
        btnDiscard.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDiscard.setFocusPainted(false);
        btnDiscard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDiscard.addActionListener(e -> dispose());

        footerPanel.add(btnSend);
        footerPanel.add(btnSaveDraft);
        footerPanel.add(btnDiscard);
        add(footerPanel, BorderLayout.SOUTH);

        if (existingDraft != null) {
            txtRecipient.setText(existingDraft.getRecipient());
            txtSubject.setText(existingDraft.getSubject());
            txtBody.setText(existingDraft.getBody());
            chkImportant.setSelected(existingDraft.isImportant());
            if (existingDraft.getAttachments() != null) {
                attachmentPaths.addAll(existingDraft.getAttachments());
                updateAttachmentLabel();
            }
        }
    }

    private void chooseAttachment() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            attachmentPaths.add(selectedFile.getAbsolutePath());
            updateAttachmentLabel();
        }
    }

    private void updateAttachmentLabel() {
        if (attachmentPaths.isEmpty()) {
            lblAttachments.setText("No attachments");
        } else {
            lblAttachments.setText("Attached: " + attachmentPaths.size() + " file(s)");
        }
    }

    private void processSend() {
        String recipient = txtRecipient.getText().trim();
        String subject = txtSubject.getText().trim();
        String body = txtBody.getText();

        String sender = CurrentUser.getInstance().getUser() != null ?
                CurrentUser.getInstance().getUser().getEmail() : "user@mail.com";

        if (recipient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipient email is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(recipient).matches()) {
            JOptionPane.showMessageDialog(this, "Recipient email format is invalid.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (subject.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(this, "Send this message without a subject?", "No Subject", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        FileHandler fh = FileHandler.getInstance();
        if (!fh.userExists(sender)) {
            JOptionPane.showMessageDialog(this, "Sender account (" + sender + ") does not exist in system records.", "Account Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!fh.userExists(recipient)) {
            JOptionPane.showMessageDialog(this, "Recipient account (" + recipient + ") does not exist. Unable to send.", "Account Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String realTimeTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Email email;
        if (existingDraft != null) {
            email = existingDraft;
            email.setRecipient(recipient);
            email.setSubject(subject);
            email.setBody(body);
            email.setTimestamp(realTimeTimestamp);
            email.setDraft(false);
            email.setImportant(chkImportant.isSelected());
            email.setAttachments(attachmentPaths);
            fh.updateEmail(email);
        } else {
            email = new Email(UUID.randomUUID().toString(), sender, recipient, subject, body, realTimeTimestamp);
            email.setImportant(chkImportant.isSelected());
            email.setAttachments(attachmentPaths);
            fh.addEmail(email);
        }

        JOptionPane.showMessageDialog(this, "Email sent at " + realTimeTimestamp, "Sent", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        if (onCompleteCallback != null) onCompleteCallback.run();
    }

    private void processSaveDraft() {
        String recipient = txtRecipient.getText().trim();
        String subject = txtSubject.getText().trim();
        String body = txtBody.getText();

        String sender = CurrentUser.getInstance().getUser() != null ?
                CurrentUser.getInstance().getUser().getEmail() : "user@mail.com";

        String realTimeTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        FileHandler fh = FileHandler.getInstance();

        if (existingDraft != null) {
            existingDraft.setRecipient(recipient);
            existingDraft.setSubject(subject.isEmpty() ? "(No Subject)" : subject);
            existingDraft.setBody(body);
            existingDraft.setTimestamp(realTimeTimestamp);
            existingDraft.setImportant(chkImportant.isSelected());
            existingDraft.setAttachments(attachmentPaths);
            fh.updateEmail(existingDraft);
        } else {
            Email draft = new Email(UUID.randomUUID().toString(), sender, recipient,
                    subject.isEmpty() ? "(No Subject)" : subject, body, realTimeTimestamp);
            draft.setDraft(true);
            draft.setImportant(chkImportant.isSelected());
            draft.setAttachments(attachmentPaths);
            fh.addEmail(draft);
        }

        JOptionPane.showMessageDialog(this, "Saved to Drafts.", "Draft Saved", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        if (onCompleteCallback != null) onCompleteCallback.run();
    }
}
