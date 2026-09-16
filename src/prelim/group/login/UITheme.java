// Hilde - moved this file into the NaviMail app project (was its own
// standalone login project before) so it now lives under prelim.group.login
// alongside the rest of the login screens it styles.
package prelim.group.login;


import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


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

    public static JButton outlinedButton(String text, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON.deriveFont(Font.PLAIN, 13f));
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(new RoundedLineBorder(new Color(191, 209, 228), 12, 6));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void showMessage(Component parent, String message, String title) {
        showDialog(parent, message, title, false);
    }

    public static int showConfirm(Component parent, String message, String title) {
        return showDialog(parent, message, title, true);
    }

    public static Runnable dimOwner(Window child) {
        if (child == null) {
            return () -> {};
        }

        List<Runnable> restorations = new ArrayList<>();
        Window owner = child.getOwner();
        while (owner != null) {
            final Window currOwner = owner;
            if (currOwner instanceof RootPaneContainer rpc && currOwner.isDisplayable()) {
                Component oldGlassPane = rpc.getGlassPane();
                boolean oldVisible = oldGlassPane != null && oldGlassPane.isVisible();

                JPanel dimGlassPane = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        Graphics2D g = (Graphics2D) graphics.create();
                        g.setColor(new Color(5, 24, 46, 125));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.dispose();
                    }
                };
                dimGlassPane.setOpaque(false);

                AtomicBoolean restored = new AtomicBoolean(false);
                Runnable restoreThis = () -> {
                    if (!restored.compareAndSet(false, true)) return;
                    dimGlassPane.setVisible(false);
                    if (oldGlassPane != null) {
                        rpc.setGlassPane(oldGlassPane);
                        oldGlassPane.setVisible(oldVisible);
                    }
                };

                dimGlassPane.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!child.isShowing()) {
                            restoreThis.run();
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                            child.toFront();
                            child.requestFocus();
                        }
                    }
                });

                rpc.setGlassPane(dimGlassPane);
                dimGlassPane.setVisible(true);
                restorations.add(restoreThis);
            }
            owner = currOwner.getOwner();
        }

        AtomicBoolean cleanedUp = new AtomicBoolean(false);
        Runnable cleanup = () -> {
            if (!cleanedUp.compareAndSet(false, true)) return;
            Runnable doAll = () -> {
                for (Runnable r : restorations) {
                    try {
                        r.run();
                    } catch (Exception ignored) {}
                }
                restorations.clear();
            };
            if (SwingUtilities.isEventDispatchThread()) {
                doAll.run();
            } else {
                SwingUtilities.invokeLater(doAll);
            }
        };

        child.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup.run();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                cleanup.run();
            }
        });

        child.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                cleanup.run();
            }
        });

        child.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (!child.isShowing()) {
                    cleanup.run();
                }
            }
        });

        return cleanup;
    }

    private static int showDialog(Component parent, String message, String title, boolean confirm) {
        Window owner = parent instanceof Window window
            ? window
            : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Runnable clearDim = dimOwner(dialog);
        JPanel surface = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(WHITE);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g.setColor(new Color(191, 209, 228));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        surface.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE_LIGHT);
        header.setBorder(new javax.swing.border.EmptyBorder(12, 16, 10, 16));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(BLUE_DARK);
        header.add(titleLabel, BorderLayout.WEST);
        surface.add(header, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><div style='width:245px'>" + escapeHtml(message) + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(TEXT_DARK);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(WHITE);
        body.setBorder(new javax.swing.border.EmptyBorder(15, 18, 12, 18));
        body.add(messageLabel, BorderLayout.CENTER);
        surface.add(body, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(WHITE);
        actions.setBorder(new javax.swing.border.EmptyBorder(0, 14, 12, 14));
        final int[] result = {JOptionPane.NO_OPTION};
        if (confirm) {
            JButton no = outlinedButton("No", BLUE_DARK);
            no.addActionListener(e -> dialog.dispose());
            JButton yes = roundedButton("Yes", BLUE_PRIMARY, WHITE);
            yes.addActionListener(e -> { result[0] = JOptionPane.YES_OPTION; dialog.dispose(); });
            actions.add(no);
            actions.add(yes);
        } else {
            JButton ok = outlinedButton("OK", BLUE_PRIMARY);
            ok.addActionListener(e -> dialog.dispose());
            actions.add(ok);
        }
        surface.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(surface);
        dialog.setSize(confirm ? 340 : 380, 155);
        dialog.setLocationRelativeTo(parent);

        dialog.getRootPane().registerKeyboardAction(
            e -> dialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        try {
            dialog.setVisible(true);
        } finally {
            clearDim.run();
        }
        return result[0];
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
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

