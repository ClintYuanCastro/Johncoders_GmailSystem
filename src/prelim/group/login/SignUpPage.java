package prelim.group.login;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import prelim.group.filehandler.FileHandler;
import prelim.group.userHandling.User;


public class SignUpPage implements ActionListener {


    private final JFrame frame;
    private final JFrame loginFrame; // the LoginPage frame we return to


    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;
    private JButton createAccountButton;
    private JLabel backToLoginLink;


    /**
     * @param loginFrame the LoginPage's JFrame, hidden while this screen is
     *                   open and re-shown when the user goes back or finishes
     *                   signing up
     */
    public SignUpPage(JFrame loginFrame) {
        this.loginFrame = loginFrame;


        frame = new JFrame("NAVIMAIL - Sign Up");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setMinimumSize(new Dimension(700, 600));
        frame.setLocationRelativeTo(null);
        try {
            frame.setIconImage(new ImageIcon("src/resources/icons/navi.png").getImage());
        } catch (Exception ignored) {
            // Icon is optional.
        }


        UITheme.GradientPanel background = new UITheme.GradientPanel();
        background.setLayout(new GridBagLayout());
        frame.setContentPane(background);


        UITheme.CardPanel card = new UITheme.CardPanel();
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 520));
        background.add(card, new GridBagConstraints());


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;


        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.BLUE_DARK);
        c.gridy = 0;
        c.insets = new Insets(28, 28, 2, 28);
        card.add(titleLabel, c);


        JLabel subtitleLabel = new JLabel("Join NaviMAIL - it only takes a minute", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SUBTLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        c.gridy = 1;
        c.insets = new Insets(0, 28, 18, 28);
        card.add(subtitleLabel, c);


        fullNameField = addField(card, c, 2, "Full Name");
        emailField = addField(card, c, 4, "Email");
        passwordField = (JPasswordField) addField(card, c, 6, "Password", true);
        confirmPasswordField = (JPasswordField) addField(card, c, 8, "Confirm Password", true);


        createAccountButton = UITheme.roundedButton("Create Account", UITheme.BLUE_PRIMARY, UITheme.WHITE);
        createAccountButton.setPreferredSize(new Dimension(0, 46));
        createAccountButton.addActionListener(this);
        c.gridy = 10;
        c.insets = new Insets(14, 28, 10, 28);
        card.add(createAccountButton, c);


        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(UITheme.FONT_SUBTLE);
        c.gridy = 11;
        c.insets = new Insets(2, 28, 10, 28);
        card.add(messageLabel, c);


        backToLoginLink = UITheme.linkLabel("Back to Login");
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        backRow.setOpaque(false);
        backRow.add(backToLoginLink);
        backToLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                returnToLogin();
            }
        });
        c.gridy = 12;
        c.insets = new Insets(4, 28, 28, 28);
        card.add(backRow, c);


        frame.setVisible(true);
    }


    /** Small helper to add a "label + rounded field" pair, keeping the constructor readable. */
    private JTextField addField(JPanel card, GridBagConstraints c, int startRow, String labelText) {
        return addField(card, c, startRow, labelText, false);
    }


    private JTextField addField(JPanel card, GridBagConstraints c, int startRow, String labelText, boolean isPassword) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_DARK);
        c.gridy = startRow;
        c.insets = new Insets(6, 28, 2, 28);
        card.add(label, c);


        JTextField field = isPassword ? new JPasswordField() : new JTextField();
        UITheme.styleField(field);
        c.gridy = startRow + 1;
        c.insets = new Insets(0, 28, 8, 28);
        card.add(field, c);
        return field;
    }


    /** Hides this Sign Up window and brings the original LoginPage window back. */
    private void returnToLogin() {
        frame.dispose();
        if (loginFrame != null) {
            loginFrame.setVisible(true);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != createAccountButton) {
            return;
        }


        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());


        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in every field.", UITheme.DANGER);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.", UITheme.DANGER);
            return;
        }
        if (FileHandler.getInstance().userExists(email)) {
            showMessage("An account with that email already exists.", UITheme.DANGER);
            return;
        }


        // Persist the new account through the backend's user database.
        User newUser = new User(email, password, fullName);
        FileHandler.getInstance().registerUser(newUser);


        showMessage("Account created! Returning to Login...", UITheme.SUCCESS);


        // Give the user a moment to read the confirmation, then send them
        // back to the login screen automatically.
        Timer timer = new Timer(1200, evt -> returnToLogin());
        timer.setRepeats(false);
        timer.start();
    }


    private void showMessage(String text, Color color) {
        messageLabel.setForeground(color);
        messageLabel.setText(text);
    }
}

