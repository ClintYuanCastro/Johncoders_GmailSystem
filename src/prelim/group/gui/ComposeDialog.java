package prelim.group.gui;


import prelim.group.filehandler.FileHandler;
import prelim.group.login.UITheme;
import prelim.group.model.Email;
import prelim.group.userHandling.CurrentUser;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;


public class ComposeDialog extends JDialog {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    // ---- Palette -----------------------------------------------------
    public static final Color WHITE       = Color.WHITE;
    public static final Color TEXT_DARK   = new Color(33, 37, 41);


    private JTextField txtRecipient;
    private JTextField txtSubject;
    private JTextArea txtBody;
    private JCheckBox chkImportant;
    private Email existingDraft;
    private Runnable onCompleteCallback;
    private Runnable clearOwnerDim;


    public ComposeDialog(Frame owner, Email draft, Runnable onCompleteCallback) {
        super(owner, draft != null ? "Edit Draft" : "New Message", true);
        this.existingDraft = draft;
        this.onCompleteCallback = onCompleteCallback;


        setSize(560, 480);
        setUndecorated(true);
        setResizable(false);
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
                if (clearOwnerDim != null) {
                    Runnable toRun = clearOwnerDim;
                    clearOwnerDim = null;
                    try {
                        toRun.run();
                    } catch (Exception ignored) {}
                }
            }
        });

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        UITheme.GradientPanel headerPanel = new UITheme.GradientPanel();
        headerPanel.setLayout(new GridBagLayout());
        headerPanel.setPreferredSize(new Dimension(0, 48));
        JLabel titleLabel = new JLabel(draft != null ? "Edit Draft" : "New Message");
        titleLabel.setFont(new Font("Google Sans", Font.PLAIN, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(250, 251, 247));
        formPanel.setBorder(new EmptyBorder(16, 20, 8, 20));

        JPanel recipientRow = new JPanel(new BorderLayout(10, 0));
        recipientRow.setOpaque(false);
        recipientRow.setPreferredSize(new Dimension(0, 40));
        recipientRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtRecipient = createPillField("To:");
        recipientRow.add(txtRecipient, BorderLayout.CENTER);
        chkImportant = new JCheckBox("Mark as Important");
        chkImportant.setOpaque(false);
        chkImportant.setFont(new Font("Google Sans Text", Font.PLAIN, 12));
        chkImportant.setForeground(new Color(55, 65, 81));
        chkImportant.setIcon(new StarIcon(new Color(36, 121, 194)));
        recipientRow.add(chkImportant, BorderLayout.EAST);
        formPanel.add(recipientRow);
        formPanel.add(Box.createVerticalStrut(9));

        txtSubject = createPillField("Subject:");
        txtSubject.setPreferredSize(new Dimension(0, 40));
        txtSubject.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formPanel.add(txtSubject);
        formPanel.add(Box.createVerticalStrut(12));

        txtBody = new JTextArea();
        txtBody.setFont(new Font("Google Sans Text", Font.PLAIN, 13));
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);
        txtBody.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane scrollPane = new JScrollPane(txtBody);
        scrollPane.setPreferredSize(new Dimension(0, 190));
        scrollPane.setMinimumSize(new Dimension(0, 150));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        scrollPane.setBorder(new UITheme.RoundedLineBorder(new Color(225, 226, 218), 22, 8));
        scrollPane.getViewport().setBackground(new Color(250, 251, 247));
        formPanel.add(scrollPane);
        add(formPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 8));
        footerPanel.setBackground(new Color(250, 251, 247));
        JButton btnSend = UITheme.roundedButton("Send", UITheme.BLUE_PRIMARY, Color.WHITE);
        btnSend.setFont(new Font("Google Sans Text", Font.BOLD, 13));
        btnSend.setPreferredSize(new Dimension(70, 36));
        btnSend.addActionListener((ActionEvent e) -> processSend());

        JButton btnSaveDraft = textButton("Save Draft");
        btnSaveDraft.addActionListener(e -> processSaveDraft());

        JButton btnDiscard = textButton("Discard");
        btnDiscard.addActionListener(e -> dispose());

        footerPanel.add(btnDiscard);
        footerPanel.add(btnSaveDraft);
        footerPanel.add(btnSend);
        add(footerPanel, BorderLayout.SOUTH);


        if (existingDraft != null) {
            txtRecipient.setText(existingDraft.getRecipient());
            txtSubject.setText(existingDraft.getSubject());
            txtBody.setText(existingDraft.getBody());
            chkImportant.setSelected(existingDraft.isImportant());
        }
    }

    private JTextField createPillField(String prompt) {
        JTextField field = new JTextField(prompt);
        field.setFont(new Font("Google Sans Text", Font.PLAIN, 13));
        field.setForeground(new Color(120, 125, 125));
        field.setBorder(new UITheme.RoundedLineBorder(new Color(225, 226, 218), 24, 10));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if (field.getText().equals(prompt)) field.setText("");
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (field.getText().trim().isEmpty()) field.setText(prompt);
            }
        });
        return field;
    }

    private JButton textButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Google Sans Text", Font.PLAIN, 13));
        button.setForeground(new Color(48, 91, 122));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static final class StarIcon implements Icon {
        private final Color color;

        private StarIcon(Color color) { this.color = color; }

        @Override public int getIconWidth() { return 22; }
        @Override public int getIconHeight() { return 22; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(color);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Polygon star = new Polygon();
            for (int i = 0; i < 10; i++) {
                double angle = -Math.PI / 2 + i * Math.PI / 5;
                int radius = i % 2 == 0 ? 10 : 4;
                star.addPoint(x + 11 + (int) (Math.cos(angle) * radius), y + 11 + (int) (Math.sin(angle) * radius));
            }
            g.fillPolygon(star);
            g.dispose();
        }
    }


    private void processSend() {
        String recipient = fieldValue(txtRecipient, "To:");
        String subject = fieldValue(txtSubject, "Subject:");
        String body = txtBody.getText();


        String sender = CurrentUser.getInstance().getUser() != null ?
                CurrentUser.getInstance().getUser().getEmail() : "user@mail.com";


        if (recipient.isEmpty()) {
            UITheme.showMessage(this, "Recipient email is required.", "Validation Error");
            return;
        }


        if (!EMAIL_PATTERN.matcher(recipient).matches()) {
            UITheme.showMessage(this, "Invalid email format. Please use a valid address.", "Validation Error");
            return;
        }


        if (subject.isEmpty()) {
            int choice = UITheme.showConfirm(this, "Send this message without a subject?", "No Subject");
            if (choice != JOptionPane.YES_OPTION) return;
        }


        FileHandler fh = FileHandler.getInstance();
        if (!fh.userExists(sender)) {
            UITheme.showMessage(this, "Sender account (" + sender + ") does not exist in system records.", "Account Error");
            return;
        }


        if (!fh.userExists(recipient)) {
            UITheme.showMessage(this, "Recipient account (" + recipient + ") does not exist. Unable to send.", "Account Error");
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
            fh.updateEmail(email);
        } else {
            email = new Email(UUID.randomUUID().toString(), sender, recipient, subject, body, realTimeTimestamp);
            email.setImportant(chkImportant.isSelected());
            fh.addEmail(email);
        }


        UITheme.showMessage(this, "Email sent at " + realTimeTimestamp, "Sent");
        dispose();
        if (onCompleteCallback != null) onCompleteCallback.run();
    }


    private void processSaveDraft() {
        String recipient = fieldValue(txtRecipient, "To:");
        String subject = fieldValue(txtSubject, "Subject:");
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
            fh.updateEmail(existingDraft);
        } else {
            Email draft = new Email(UUID.randomUUID().toString(), sender, recipient,
                    subject.isEmpty() ? "(No Subject)" : subject, body, realTimeTimestamp);
            draft.setDraft(true);
            draft.setImportant(chkImportant.isSelected());
            fh.addEmail(draft);
        }


        UITheme.showMessage(this, "Saved to Drafts.", "Draft Saved");
        dispose();
        if (onCompleteCallback != null) onCompleteCallback.run();
    }

    private String fieldValue(JTextField field, String prompt) {
        String value = field.getText().trim();
        return value.equals(prompt) ? "" : value;
    }
}

