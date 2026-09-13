// Hilde - moved this file into the NaviMail app project (was its own
// standalone login project before) so it now lives under prelim.group.login
// alongside the rest of the login screens it styles.
package prelim.group.login;


import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;


public final class UITheme {


    // ---- Palette -----------------------------------------------------
    public static final Color BLUE_DARK   = new Color(6, 50, 79);
    public static final Color BLUE_PRIMARY = new Color(25, 118, 210); // #1976D2
    public static final Color BLUE_LIGHT  = new Color(227, 242, 253); // #E3F2FD
    public static final Color WHITE       = Color.WHITE;
    public static final Color TEXT_DARK   = new Color(33, 37, 41);
    public static final Color TEXT_MUTED  = new Color(120, 130, 140);
    public static final Color DANGER      = new Color(211, 47, 47);
    public static final Color SUCCESS     = new Color(46, 125, 50);


    // ---- Fonts ---------------------------------------------------------
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTLE  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 15);


    private UITheme() { /* static-only utility class, no instances */ }


    /**
     * Creates a flat rounded button with a hover highlight.
     *
     * @param text            button text
     * @param background      base fill color
     * @param foreground      text color
     * @return a ready-to-add JButton styled to match the rest of the app
     */
    public static JButton roundedButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isRollover() ? background.darker() : background;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }


    /**
     * Creates a small underline-free "hyperlink" style label used for
     * secondary navigation such as "Forgot password?" or "Back to Login".
     * The caller is responsible for adding a MouseListener to react to clicks.
     */
    public static JLabel linkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(BLUE_PRIMARY);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }


    /** Applies the shared rounded-border + font styling to a text field. */
    public static void styleField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setBorder(new RoundedLineBorder(BLUE_LIGHT, 14, 10));
        field.setBackground(WHITE);
        field.setForeground(TEXT_DARK);
    }


    /** Rounded-rectangle border so fields visually match the rounded buttons. */
    public static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int padding;


        public RoundedLineBorder(Color color, int radius, int padding) {
            this.color = color;
            this.radius = radius;
            this.padding = padding;
        }


        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g2.dispose();
        }


        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(padding, padding + 4, padding, padding + 4);
        }


        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(padding, padding + 4, padding, padding + 4);
            return insets;
        }
    }


    /** A JPanel that paints a smooth diagonal blue gradient background. */
    public static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(0, 0, BLUE_DARK, getWidth(), getHeight(), BLUE_PRIMARY);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }


    /** A JPanel that paints a white rounded "card" with a soft drop shadow. */
    public static class CardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // soft shadow
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 12, 28, 28);
            // white card
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 28, 28);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

