package prelim.group.login;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;


import prelim.group.filehandler.FileHandler;
import prelim.group.gui.NaviMailApp;
import prelim.group.userHandling.CurrentUser;
import prelim.group.userHandling.User;


public class LoginPage implements ActionListener {


    private JFrame frame;
    private JButton loginButton;
    private JButton signUpButton;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JLabel forgotPasswordLink;


    public LoginPage() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        frame = new JFrame("NAVIMAIL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setMinimumSize(new Dimension(700, 550));
        frame.setLocationRelativeTo(null);
        try {
            frame.setIconImage(new ImageIcon("src/resources/icons/navi.png").getImage());
        } catch (Exception ignored) {
            // Icon is optional - keep the window usable if the image is missing.
        }


        // ---- Blue gradient background -----------------------------------
        UITheme.GradientPanel background = new UITheme.GradientPanel();
        background.setLayout(new GridBagLayout());
        frame.setContentPane(background);


        // ---- White rounded card -------------------------------------------
        UITheme.CardPanel card = new UITheme.CardPanel();
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 460));


        GridBagConstraints cardGbc = new GridBagConstraints();
        background.add(card, cardGbc);


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 28, 6, 28);


        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.BLUE_DARK);
        c.gridy = 0;
        c.insets = new Insets(36, 28, 2, 28);
        card.add(titleLabel, c);


        JLabel subtitleLabel = new JLabel("Log in to continue to NAVIMAIL", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SUBTLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);
        c.gridy = 1;
        c.insets = new Insets(0, 28, 24, 28);
        card.add(subtitleLabel, c);


        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UITheme.FONT_LABEL);
        emailLabel.setForeground(UITheme.TEXT_DARK);
        c.gridy = 2;
        c.insets = new Insets(6, 28, 2, 28);
        card.add(emailLabel, c);


        emailField = new JTextField();
        UITheme.styleField(emailField);
        c.gridy = 3;
        c.insets = new Insets(0, 28, 14, 28);
        card.add(emailField, c);


        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(UITheme.FONT_LABEL);
        passwordLabel.setForeground(UITheme.TEXT_DARK);
        c.gridy = 4;
        c.insets = new Insets(6, 28, 2, 28);
        card.add(passwordLabel, c);


        passwordField = new JPasswordField();
        UITheme.styleField(passwordField);
        c.gridy = 5;
        c.insets = new Insets(0, 28, 4, 28);
        card.add(passwordField, c);


        // Forgot password link, right aligned
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.setOpaque(false);
        forgotPasswordLink = UITheme.linkLabel("Forgot password?");
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordPage(frame);
            }
        });
        forgotRow.add(forgotPasswordLink);
        c.gridy = 6;
        c.insets = new Insets(0, 28, 18, 28);
        card.add(forgotRow, c);


        // Login button
        loginButton = UITheme.roundedButton("Login", UITheme.BLUE_PRIMARY, UITheme.WHITE);
        loginButton.setPreferredSize(new Dimension(0, 46));
        loginButton.addActionListener(this);
        c.gridy = 7;
        c.insets = new Insets(6, 28, 10, 28);
        card.add(loginButton, c);


        // Pressing Enter in either field also submits the form.
        emailField.addActionListener(this);
        passwordField.addActionListener(this);


        // Status message
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(UITheme.FONT_SUBTLE);
        c.gridy = 8;
        c.insets = new Insets(2, 28, 6, 28);
        card.add(messageLabel, c);


        // Divider + Sign Up
        JLabel signUpPrompt = new JLabel("New here? Create an account", SwingConstants.CENTER);
        signUpPrompt.setFont(UITheme.FONT_SUBTLE);
        signUpPrompt.setForeground(UITheme.TEXT_MUTED);
        c.gridy = 9;
        c.insets = new Insets(10, 28, 4, 28);
        card.add(signUpPrompt, c);


        signUpButton = UITheme.roundedButton("Sign Up", UITheme.BLUE_LIGHT, UITheme.BLUE_DARK);
        signUpButton.setPreferredSize(new Dimension(0, 42));
        signUpButton.addActionListener(this);
        c.gridy = 10;
        c.insets = new Insets(2, 28, 30, 28);
        card.add(signUpButton, c);


        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signUpButton) {
            frame.setVisible(false);
            new SignUpPage(frame);
            return;
        }


        // Triggered by the Login button OR pressing Enter in a field.
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());


        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(UITheme.DANGER);
            messageLabel.setText("Please enter both email and password.");
            return;
        }


        // 1. Validate against the backend user database.
        User authenticatedUser = FileHandler.getInstance().authenticate(email, password);


        if (authenticatedUser != null) {
            // 3. Successful login: set the active user and launch NaviMailApp.
            messageLabel.setForeground(UITheme.SUCCESS);
            messageLabel.setText("Login Successful");


            CurrentUser.getInstance().setUser(authenticatedUser);


            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                NaviMailApp app = new NaviMailApp();
                app.setVisible(true);
            });
        } else if (!FileHandler.getInstance().userExists(email)) {
            // 2. Error handling: unregistered user.
            messageLabel.setForeground(UITheme.DANGER);
            messageLabel.setText("Account not found. Sign up instead?");
        } else {
            // 2. Error handling: registered user, wrong password.
            messageLabel.setForeground(UITheme.DANGER);
            messageLabel.setText("Invalid password. Please try again.");
        }
    }
}

