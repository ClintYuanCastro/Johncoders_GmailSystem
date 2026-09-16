// Hilde - moved into NaviMail under prelim.group.login, no logic changes.
package prelim.group.login;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ForgotPasswordPage extends JDialog implements ActionListener {
    private static ForgotPasswordPage activeDialog;


    private JRadioButton emailOption;
    private JRadioButton phoneOption;
    private JTextField contactField;
    private JButton sendCodeButton;
    private JLabel statusLabel;
    private Runnable clearOwnerDim;


    /**
     * @param owner the LoginPage's JFrame, used so this dialog centers over
     *              it and blocks input to it while open
     */
    public ForgotPasswordPage(Frame owner) {
        super(owner, "Reset Password", true);
        if (activeDialog != null && activeDialog.isDisplayable()) {
            activeDialog.toFront();
            activeDialog.requestFocus();
            dispose();
            return;
        }
        activeDialog = this;
        setSize(380, 420);
        setMinimumSize(new Dimension(340, 400));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        clearOwnerDim = UITheme.dimOwner(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                if (activeDialog == ForgotPasswordPage.this) activeDialog = null;
                dispose();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent event) {
                if (activeDialog == ForgotPasswordPage.this) activeDialog = null;
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


        UITheme.GradientPanel background = new UITheme.GradientPanel();
        background.setLayout(new GridBagLayout());
        setContentPane(background);


        UITheme.CardPanel card = new UITheme.CardPanel();
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(320, 360));
        background.add(card, new GridBagConstraints());


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0; // lets fields/buttons stretch to the card's full width
        c.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE.deriveFont(20f));
        titleLabel.setForeground(UITheme.BLUE_DARK);
        c.gridy = 0;
        c.insets = new Insets(22, 24, 4, 24);
        card.add(titleLabel, c);


        JLabel subtitleLabel = new JLabel("<html><div style='text-align:center;'>Choose how you'd like to receive your verification code.</div></html>", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SUBTLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        c.gridy = 1;
        c.insets = new Insets(0, 24, 14, 24);
        card.add(subtitleLabel, c);


        // ---- Email / Phone choice ----------------------------------------
        emailOption = new JRadioButton("Send code to my Email", true);
        phoneOption = new JRadioButton("Send code to my Phone Number");
        for (JRadioButton option : new JRadioButton[]{emailOption, phoneOption}) {
            option.setOpaque(false);
            option.setFont(UITheme.FONT_LABEL);
            option.setForeground(UITheme.TEXT_DARK);
            option.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        ButtonGroup group = new ButtonGroup();
        group.add(emailOption);
        group.add(phoneOption);


        c.gridy = 2;
        c.insets = new Insets(4, 24, 2, 24);
        card.add(emailOption, c);
        c.gridy = 3;
        c.insets = new Insets(0, 24, 10, 24);
        card.add(phoneOption, c);


        // ---- Contact field --------------------------------------------------
        JLabel contactLabel = new JLabel("Email or Phone Number");
        contactLabel.setFont(UITheme.FONT_LABEL);
        contactLabel.setForeground(UITheme.TEXT_DARK);
        c.gridy = 4;
        c.insets = new Insets(4, 24, 2, 24);
        card.add(contactLabel, c);


        contactField = new JTextField();
        UITheme.styleField(contactField);
        c.gridy = 5;
        c.insets = new Insets(0, 24, 12, 24);
        card.add(contactField, c);


        // ---- Send button -------------------------------------------------
        sendCodeButton = UITheme.roundedButton("Send Verification Code", UITheme.BLUE_PRIMARY, UITheme.WHITE);
        sendCodeButton.setPreferredSize(new Dimension(0, 44));
        sendCodeButton.addActionListener(this);
        c.gridy = 6;
        c.insets = new Insets(4, 24, 8, 24);
        card.add(sendCodeButton, c);


        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(UITheme.FONT_SUBTLE);
        c.gridy = 7;
        c.insets = new Insets(2, 24, 8, 24);
        card.add(statusLabel, c);


        // ---- Beta disclaimer (required by the spec) ------------------------
        JLabel disclaimer = new JLabel(
                "<html><div style='text-align:center;'>&#9888; Beta Feature - Still a Work in Progress</div></html>",
                SwingConstants.CENTER);
        disclaimer.setFont(UITheme.FONT_SUBTLE.deriveFont(Font.ITALIC, 11f));
        disclaimer.setForeground(UITheme.TEXT_MUTED);
        c.gridy = 8;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets(4, 24, 16, 24);
        card.add(disclaimer, c);


        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != sendCodeButton) {
            return;
        }


        String contact = contactField.getText().trim();
        if (contact.isEmpty()) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Please enter your email or phone number.");
            return;
        }


        String destination = emailOption.isSelected() ? "email" : "phone number";
        // NOTE: No real email/SMS is sent - this is a local simulation only,
        // which is exactly why the beta disclaimer below is shown.
        statusLabel.setForeground(UITheme.SUCCESS);
        statusLabel.setText("Verification code 'sent' to your " + destination + " (simulated).");
    }
}

