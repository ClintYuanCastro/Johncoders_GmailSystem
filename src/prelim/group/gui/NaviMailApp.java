package prelim.group.gui;

import prelim.exercises.MyDoublyLinkedList;
import prelim.exercises.MyList;
import prelim.group.model.Email;
import prelim.group.model.EmailCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class NaviMailApp extends JFrame {
    // Outer Data Structure: MyList of EmailCategory
    private MyList<EmailCategory> categories;
    private EmailCategory currentCategory;

    // UI Components
    private JTextField searchField;
    private JTable emailTable;
    private DefaultTableModel tableModel;
    private JLabel paginationLabel;
    private JLabel inboxBadgeLabel;
    private JButton primaryTabBtn, promoTabBtn, socialTabBtn;
    private String activeTabName = "Primary";
    private java.util.List<JPanel> sidebarPanels = new ArrayList<>();

    public NaviMailApp() {
        super("Gmail - email");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        // Initialize Data Structure (List of Lists)
        initDataStructure();

        // Main Container Panel with Navy Background
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(6, 50, 79)); // Dark Navy #06324F matching screenshot

        // Top Navigation Header Bar
        rootPanel.add(createTopHeaderPanel(), BorderLayout.NORTH);

        // Center Content Split (Left Sidebar + Center Rounded Card + Right Toolbar)
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setOpaque(false);

        bodyPanel.add(createLeftSidebarPanel(), BorderLayout.WEST);
        bodyPanel.add(createMainContentCard(), BorderLayout.CENTER);

        rootPanel.add(bodyPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Initial Load
        switchCategory("Primary");
    }

    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            File file = new File("src/resources/icons/" + filename);
            if (!file.exists()) {
                file = new File("C:/Users/Ian/Desktop/Media/Icons/" + filename);
            }
            if (!file.exists()) {
                file = new File("bin/resources/icons/" + filename);
            }
            if (file.exists()) {
                ImageIcon original = new ImageIcon(file.getAbsolutePath());
                Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initDataStructure() {
        categories = new MyDoublyLinkedList<>();

        EmailCategory primary = new EmailCategory("Primary");
        EmailCategory promotions = new EmailCategory("Promotions");
        EmailCategory social = new EmailCategory("Social");
        EmailCategory starredCat = new EmailCategory("Starred");
        EmailCategory sentCat = new EmailCategory("Sent");
        EmailCategory draftsCat = new EmailCategory("Drafts");
        EmailCategory workCat = new EmailCategory("Work");
        EmailCategory personalCat = new EmailCategory("Personal");
        EmailCategory teamCat = new EmailCategory("Team");
        EmailCategory newsCat = new EmailCategory("News");

        try {
            categories.insert(primary);
            categories.insert(promotions);
            categories.insert(social);
            categories.insert(starredCat);
            categories.insert(sentCat);
            categories.insert(draftsCat);
            categories.insert(workCat);
            categories.insert(personalCat);
            categories.insert(teamCat);
            categories.insert(newsCat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add single email from Clint Castro as requested
        Email clintEmail = new Email(
                "1",
                "Clint Castro",
                "user@navimail.com",
                "IT212 stuff something something to be done",
                "IT212 stuff something something to be done",
                "Please review and complete the required IT212 stuff and tasks as soon as possible.",
                "Jan, 26",
                "Primary"
        );
        clintEmail.addAttachment("IT212_Task_Guide.pdf");
        clintEmail.addReply(new prelim.group.model.EmailReply("clint.castro@slu.edu.ph", "Jan, 26", "Let me know once this is done."));

        primary.addEmail(clintEmail);

        currentCategory = primary;
    }

    private JPanel createTopHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Left Branding: Hamburger Menu + Bear Mascot + "NaviMail"
        JPanel leftBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftBrand.setOpaque(false);

        JLabel menuBtn = new JLabel("≡");
        menuBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Navi mascot icon + NaviMail text
        JLabel mascotLabel = new JLabel();
        ImageIcon naviImg = loadIcon("navi.png", 48, 48);
        if (naviImg != null) {
            mascotLabel.setIcon(naviImg);
        } else {
            mascotLabel.setText("🐻");
            mascotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        }

        JLabel brandText = new JLabel("NaviMail");
        brandText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandText.setForeground(Color.WHITE);

        leftBrand.add(menuBtn);
        leftBrand.add(mascotLabel);
        leftBrand.add(brandText);
        header.add(leftBrand, BorderLayout.WEST);

        // Center Search Bar: Rounded Search Mail Field
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setOpaque(false);
        searchContainer.setBorder(new EmptyBorder(0, 40, 0, 40));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(new Color(234, 241, 251)); // #EAF1FB light blue/grey
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(234, 241, 251), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        JLabel searchIcon = new JLabel();
        ImageIcon searchImg = loadIcon("search.png", 18, 18);
        if (searchImg != null) {
            searchIcon.setIcon(searchImg);
        } else {
            searchIcon.setText("🔍");
            searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            searchIcon.setForeground(new Color(100, 116, 139));
        }

        searchField = new JTextField("Search mail");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(new Color(100, 116, 139));
        searchField.setBorder(null);
        searchField.setOpaque(false);

        // Search Field Focus Listener & Dynamic Filter
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search mail")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search mail");
                    searchField.setForeground(new Color(100, 116, 139));
                }
            }
        });

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterEmailList(searchField.getText().trim());
            }
        });

        JLabel filterIcon = new JLabel();
        ImageIcon settingImg = loadIcon("setting.png", 18, 18);
        if (settingImg != null) {
            filterIcon.setIcon(settingImg);
        } else {
            filterIcon.setText("⚙");
            filterIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            filterIcon.setForeground(new Color(100, 116, 139));
        }
        filterIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchBar.add(searchIcon, BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(filterIcon, BorderLayout.EAST);
        searchContainer.add(searchBar, BorderLayout.CENTER);
        header.add(searchContainer, BorderLayout.CENTER);

        // Right Icons: Settings & Profile Avatar
        JPanel rightIcons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightIcons.setOpaque(false);

        JLabel settingsIcon = new JLabel();
        ImageIcon setIconImg = loadIcon("setting.png", 20, 20);
        if (setIconImg != null) {
            settingsIcon.setIcon(setIconImg);
        } else {
            settingsIcon.setText("⚙");
            settingsIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            settingsIcon.setForeground(Color.WHITE);
        }

        JLabel avatarLabel = new JLabel();
        ImageIcon accountImg = loadIcon("account.png", 28, 28);
        if (accountImg != null) {
            avatarLabel.setIcon(accountImg);
        } else {
            avatarLabel.setText(" I ");
            avatarLabel.setOpaque(true);
            avatarLabel.setBackground(new Color(11, 87, 208));
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            avatarLabel.setPreferredSize(new Dimension(32, 32));
            avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        }

        rightIcons.add(settingsIcon);
        rightIcons.add(avatarLabel);

        header.add(rightIcons, BorderLayout.EAST);
        return header;
    }

    private JPanel createLeftSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Compose Button (Pill button `#C2E7FF` / `#A8C7FA` light cyan with compose.png icon)
        JButton composeBtn = new JButton(" Compose");
        ImageIcon composeImg = loadIcon("compose.png", 20, 20);
        if (composeImg != null) {
            composeBtn.setIcon(composeImg);
        }
        composeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        composeBtn.setBackground(new Color(194, 231, 255)); // #C2E7FF cyan blue
        composeBtn.setForeground(new Color(0, 29, 53));
        composeBtn.setFocusPainted(false);
        composeBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        composeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        composeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        composeBtn.addActionListener(e -> {
            ComposeDialog dialog = new ComposeDialog(this, categories);
            dialog.setVisible(true);
            if (dialog.isSent() && dialog.getCreatedEmail() != null) {
                Email created = dialog.getCreatedEmail();
                // Find matching category or add to Primary
                EmailCategory targetCat = findCategory(created.getCategory());
                if (targetCat != null) {
                    targetCat.addEmailToTop(created);
                } else {
                    currentCategory.addEmailToTop(created);
                }
                refreshEmailTable();
                updateInboxBadge();
            }
        });

        sidebar.add(composeBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // Navigation Items using icon images (mail-inbox-app.png, star.png, snooze.png, sent.png, draft.png)
        inboxBadgeLabel = new JLabel("14");
        inboxBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        inboxBadgeLabel.setForeground(Color.WHITE);

        sidebar.add(createNavItem("Inbox", "mail-inbox-app.png", true, inboxBadgeLabel, e -> switchCategory("Primary")));
        sidebar.add(createNavItem("Starred", "star.png", false, null, e -> switchCategory("Starred")));
        sidebar.add(createNavItem("Snoozed", "snooze.png", false, null, e -> switchCategory("Primary")));
        sidebar.add(createNavItem("Sent", "sent.png", false, null, e -> switchCategory("Sent")));
        sidebar.add(createNavItem("Drafts", "draft.png", false, null, e -> switchCategory("Drafts")));
        sidebar.add(createNavItem("More", null, false, null, null));

        sidebar.add(Box.createVerticalStrut(25));

        // Labels Section
        JLabel labelsHeader = new JLabel("Labels");
        labelsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelsHeader.setForeground(Color.WHITE);
        labelsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(labelsHeader);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(createLabelItem("Categories", "tag.png", null, e -> switchCategory("Primary")));
        sidebar.add(createLabelItem("Team", "multiple-users-silhouette.png", new Color(34, 197, 94), e -> switchCategory("Team")));
        sidebar.add(createLabelItem("News", "tag.png", new Color(234, 179, 8), e -> switchCategory("News")));
        sidebar.add(createLabelItem("Work", "tag.png", new Color(59, 130, 246), e -> switchCategory("Work")));
        sidebar.add(createLabelItem("Personal", "tag.png", new Color(239, 68, 68), e -> switchCategory("Personal")));

        return sidebar;
    }

    private JPanel createNavItem(String text, String iconName, boolean active, JLabel badge, java.awt.event.ActionListener listener) {
        JPanel navItem = new JPanel(new BorderLayout(10, 0));
        navItem.setOpaque(true);
        navItem.setMaximumSize(new Dimension(200, 36));
        navItem.setPreferredSize(new Dimension(200, 36));
        navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navItem.setBorder(new EmptyBorder(6, 16, 6, 16));

        sidebarPanels.add(navItem);

        if (active) {
            navItem.setBackground(new Color(0, 77, 122)); // Active highlighted blue pill
        } else {
            navItem.setBackground(new Color(6, 50, 79));
        }

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(Color.WHITE);

        if (iconName != null) {
            ImageIcon icon = loadIcon(iconName, 18, 18);
            if (icon != null) {
                label.setIcon(icon);
            }
        }

        navItem.add(label, BorderLayout.WEST);
        if (badge != null) {
            navItem.add(badge, BorderLayout.EAST);
        }

        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveSidebarItem(navItem);
                if (listener != null) {
                    listener.actionPerformed(null);
                }
            }
        });

        navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        return navItem;
    }

    private JPanel createLabelItem(String text, String iconName, Color indicatorColor, java.awt.event.ActionListener listener) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        item.setOpaque(true);
        item.setBackground(new Color(6, 50, 79));
        item.setMaximumSize(new Dimension(200, 32));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sidebarPanels.add(item);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(226, 232, 240));

        if (iconName != null) {
            ImageIcon icon = loadIcon(iconName, 16, 16);
            if (icon != null) {
                label.setIcon(icon);
            }
        }

        item.add(label);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveSidebarItem(item);
                if (listener != null) {
                    listener.actionPerformed(null);
                }
            }
        });

        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        return item;
    }

    private void setActiveSidebarItem(JPanel selectedPanel) {
        for (JPanel panel : sidebarPanels) {
            if (panel == selectedPanel) {
                panel.setBackground(new Color(0, 77, 122)); // Active highlighted blue pill
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JLabel) {
                        comp.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        comp.setForeground(Color.WHITE);
                    }
                }
            } else {
                panel.setBackground(new Color(6, 50, 79)); // Inactive state
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JLabel && comp != inboxBadgeLabel) {
                        comp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        comp.setForeground(new Color(226, 232, 240));
                    }
                }
            }
            panel.revalidate();
            panel.repaint();
        }
    }

    private JPanel createMainContentCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Top Action Bar inside White Card (Checkbox, Refresh, 3-dots, Delete, Pagination)
        JPanel topToolbar = new JPanel(new BorderLayout());
        topToolbar.setOpaque(false);
        topToolbar.setBorder(new EmptyBorder(5, 5, 10, 5));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftActions.setOpaque(false);

        JCheckBox selectAllBox = new JCheckBox();
        selectAllBox.setOpaque(false);
        selectAllBox.addActionListener(e -> {
            boolean sel = selectAllBox.isSelected();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(sel, i, 0);
            }
        });

        JButton deleteBtn = new JButton(" Delete Selected");
        ImageIcon trashImg = loadIcon("trashbin.png", 16, 16);
        if (trashImg != null) {
            deleteBtn.setIcon(trashImg);
        }
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteSelectedEmails());

        leftActions.add(selectAllBox);
        leftActions.add(deleteBtn);

        // Pagination on top right: 1-16 of 16 < >
        JPanel rightPagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPagination.setOpaque(false);

        paginationLabel = new JLabel("1-16 of 16");
        paginationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paginationLabel.setForeground(new Color(100, 116, 139));

        JLabel prevArrow = new JLabel("<");
        prevArrow.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prevArrow.setForeground(new Color(148, 163, 184));

        JLabel nextArrow = new JLabel(">");
        nextArrow.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextArrow.setForeground(new Color(148, 163, 184));

        rightPagination.add(paginationLabel);
        rightPagination.add(prevArrow);
        rightPagination.add(nextArrow);

        topToolbar.add(leftActions, BorderLayout.WEST);
        topToolbar.add(rightPagination, BorderLayout.EAST);

        // Category Tabs Bar using icons: Primary (mail-inbox-app.png), Promotions (tag.png), Social (multiple-users-silhouette.png)
        JPanel tabsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        tabsBar.setOpaque(false);
        tabsBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        primaryTabBtn = createTabButton("Primary", "mail-inbox-app.png", true);
        promoTabBtn = createTabButton("Promotions", "tag.png", false);
        socialTabBtn = createTabButton("Social", "multiple-users-silhouette.png", false);

        primaryTabBtn.addActionListener(e -> switchTab("Primary", primaryTabBtn));
        promoTabBtn.addActionListener(e -> switchTab("Promotions", promoTabBtn));
        socialTabBtn.addActionListener(e -> switchTab("Social", socialTabBtn));

        tabsBar.add(primaryTabBtn);
        tabsBar.add(promoTabBtn);
        tabsBar.add(socialTabBtn);

        // Header Stack (Top Toolbar + Tabs Bar)
        JPanel northStack = new JPanel();
        northStack.setLayout(new BoxLayout(northStack, BoxLayout.Y_AXIS));
        northStack.setOpaque(false);
        northStack.add(topToolbar);
        northStack.add(tabsBar);

        card.add(northStack, BorderLayout.NORTH);

        // Email Table Setup
        String[] columnNames = {"", "★", "Sender", "Subject & Content Snippet", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is directly editable
            }
        };

        emailTable = new JTable(tableModel);
        emailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailTable.setRowHeight(36);
        emailTable.setShowGrid(false);
        emailTable.setIntercellSpacing(new Dimension(0, 0));
        emailTable.setSelectionBackground(new Color(238, 242, 246));
        emailTable.setSelectionForeground(Color.BLACK);

        // Column Width Adjustments
        emailTable.getColumnModel().getColumn(0).setMaxWidth(30); // Checkbox
        emailTable.getColumnModel().getColumn(1).setMaxWidth(30); // Star
        emailTable.getColumnModel().getColumn(2).setPreferredWidth(160); // Sender
        emailTable.getColumnModel().getColumn(3).setPreferredWidth(550); // Subject
        emailTable.getColumnModel().getColumn(4).setPreferredWidth(90); // Date

        // Custom Cell Renderer for Star Icon using star.png!
        ImageIcon starImg = loadIcon("star.png", 16, 16);
        emailTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                if ("★".equals(value)) {
                    if (starImg != null) {
                        label.setIcon(starImg);
                        label.setText("");
                    } else {
                        label.setIcon(null);
                        label.setText("★");
                        label.setForeground(new Color(234, 179, 8));
                    }
                } else {
                    label.setIcon(null);
                    label.setText("☆");
                    label.setForeground(new Color(148, 163, 184));
                }
                return label;
            }
        });

        // Double click listener to open Email Detail Dialog
        emailTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = emailTable.getSelectedRow();
                int col = emailTable.getSelectedColumn();
                if (row != -1) {
                    if (col == 1) { // Toggle Star
                        String currentStar = (String) tableModel.getValueAt(row, 1);
                        boolean isStar = "★".equals(currentStar);
                        tableModel.setValueAt(isStar ? "☆" : "★", row, 1);

                        // Update email object in list
                        Email email = getEmailAtRow(row);
                        if (email != null) {
                            email.setStarred(!isStar);
                        }
                    } else if (e.getClickCount() == 2) {
                        Email email = getEmailAtRow(row);
                        if (email != null) {
                            EmailDetailDialog detailDialog = new EmailDetailDialog(NaviMailApp.this, email);
                            detailDialog.setVisible(true);
                            refreshEmailTable();
                        }
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(emailTable);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(Color.WHITE);

        card.add(tableScroll, BorderLayout.CENTER);

        return card;
    }

    private JButton createTabButton(String text, String iconName, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(active ? new Color(11, 87, 208) : new Color(100, 116, 139));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, active ? 3 : 0, 0, new Color(11, 87, 208)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (iconName != null) {
            ImageIcon icon = loadIcon(iconName, 16, 16);
            if (icon != null) {
                btn.setIcon(icon);
            }
        }

        return btn;
    }

    private JPanel createRightSideToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setOpaque(false);
        toolbar.setPreferredSize(new Dimension(50, 0));
        toolbar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel calIcon = createSideIcon("📅");
        JLabel keepIcon = createSideIcon("💡");
        JLabel taskIcon = createSideIcon("✓");
        JLabel addIcon = createSideIcon("＋");

        toolbar.add(calIcon);
        toolbar.add(Box.createVerticalStrut(20));
        toolbar.add(keepIcon);
        toolbar.add(Box.createVerticalStrut(20));
        toolbar.add(taskIcon);
        toolbar.add(Box.createVerticalStrut(20));
        toolbar.add(addIcon);

        return toolbar;
    }

    private JLabel createSideIcon(String symbol) {
        JLabel lbl = new JLabel(symbol, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(Color.WHITE);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private void switchTab(String categoryName, JButton clickedBtn) {
        activeTabName = categoryName;

        primaryTabBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        primaryTabBtn.setForeground(new Color(100, 116, 139));
        primaryTabBtn.setBorder(null);

        promoTabBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        promoTabBtn.setForeground(new Color(100, 116, 139));
        promoTabBtn.setBorder(null);

        socialTabBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        socialTabBtn.setForeground(new Color(100, 116, 139));
        socialTabBtn.setBorder(null);

        clickedBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clickedBtn.setForeground(new Color(11, 87, 208));
        clickedBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(11, 87, 208)));

        switchCategory(categoryName);
    }

    private void switchCategory(String categoryName) {
        EmailCategory cat = findCategory(categoryName);
        if (cat != null) {
            currentCategory = cat;
            refreshEmailTable();
        }
    }

    private EmailCategory findCategory(String name) {
        for (int i = 0; i < categories.getSize(); i++) {
            EmailCategory cat = categories.getElement(i);
            if (cat.getCategoryName().equalsIgnoreCase(name)) {
                return cat;
            }
        }
        return null;
    }

    private void refreshEmailTable() {
        tableModel.setRowCount(0);
        if (currentCategory == null) return;

        MyList<Email> emails = currentCategory.getEmailList();
        for (int i = 0; i < emails.getSize(); i++) {
            Email email = emails.getElement(i);
            String star = email.isStarred() ? "★" : "☆";
            tableModel.addRow(new Object[]{
                    false,
                    star,
                    email.getSender(),
                    email.getSubject() + " - " + email.getSnippet(),
                    email.getDate()
            });
        }

        int count = emails.getSize();
        paginationLabel.setText(count == 0 ? "0 of 0" : "1-" + count + " of " + count);
        updateInboxBadge();
    }

    private void filterEmailList(String query) {
        if (query.isEmpty() || query.equalsIgnoreCase("Search mail")) {
            refreshEmailTable();
            return;
        }

        tableModel.setRowCount(0);
        MyList<Email> emails = currentCategory.getEmailList();
        int matchCount = 0;

        for (int i = 0; i < emails.getSize(); i++) {
            Email email = emails.getElement(i);
            String q = query.toLowerCase();
            if (email.getSender().toLowerCase().contains(q) ||
                email.getSubject().toLowerCase().contains(q) ||
                email.getSnippet().toLowerCase().contains(q)) {

                String star = email.isStarred() ? "★" : "☆";
                tableModel.addRow(new Object[]{
                        false,
                        star,
                        email.getSender(),
                        email.getSubject() + " - " + email.getSnippet(),
                        email.getDate()
                });
                matchCount++;
            }
        }
        paginationLabel.setText(matchCount == 0 ? "0 of 0" : "1-" + matchCount + " of " + matchCount);
    }

    private Email getEmailAtRow(int row) {
        if (currentCategory == null || row < 0 || row >= currentCategory.getEmailList().getSize()) {
            return null;
        }
        return currentCategory.getEmailList().getElement(row);
    }

    private void deleteSelectedEmails() {
        if (currentCategory == null) return;

        ArrayList<Email> toDelete = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 0);
            if (checked != null && checked) {
                Email email = getEmailAtRow(i);
                if (email != null) {
                    toDelete.add(email);
                }
            }
        }

        if (toDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one email to delete.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + toDelete.size() + " selected email(s)?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            for (Email email : toDelete) {
                currentCategory.removeEmail(email);
            }
            refreshEmailTable();
            JOptionPane.showMessageDialog(this, "Successfully deleted selected emails.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateInboxBadge() {
        EmailCategory primary = findCategory("Primary");
        if (primary != null && inboxBadgeLabel != null) {
            inboxBadgeLabel.setText(String.valueOf(primary.getEmailList().getSize()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            NaviMailApp app = new NaviMailApp();
            app.setVisible(true);
        });
    }
}
