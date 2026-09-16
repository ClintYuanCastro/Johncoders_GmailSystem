package prelim.group.gui;


import prelim.group.filehandler.FileHandler;
import prelim.group.login.LoginPage;
import prelim.group.login.UITheme;
import prelim.group.model.Email;
import prelim.group.userHandling.CurrentUser;
import prelim.group.userHandling.User;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class NaviMailApp extends JFrame {
    private static final int ITEMS_PER_PAGE = 8;

    // ---- Palette -----------------------------------------------------
    public static final Color BLUE_DARK   = new Color(6, 50, 79);
    public static final Color BLUE_PRIMARY = new Color(25, 118, 210); // #1976D2
    public static final Color BLUE_LIGHT  = new Color(227, 242, 253); // #E3F2FD
    private static final Color ACCENT_BLUE = new Color(11, 87, 208);
    public static final Color WHITE       = Color.WHITE;
    public static final Color TEXT_MUTED  = new Color(120, 130, 140);


    private String activeTab = "INBOX";
    private int currentPage = 1;


    // UI Components
    private JTextField txtSearch;
    private JTable emailTable;
    private DefaultTableModel tableModel;
    private JLabel lblPageInfo;
    private JButton btnPrevPage;
    private JButton btnNextPage;
    private JCheckBox chkSelectAll;
    private JLabel lblUserProfile;
    private JLabel inboxBadgeLabel;
    private JLabel sectionTitleLabel;
    private JPanel emptyStatePanel;
    private final List<JPanel> sidebarPanels = new ArrayList<>();


    private List<Email> currentFilteredEmails = new ArrayList<>();
    private List<Email> pageEmails = new ArrayList<>();


    public NaviMailApp() {
        setTitle("Mail Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 623);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);


        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(235, 241, 247));

        rootPanel.add(createLeftNavigation(), BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(235, 241, 247));
        mainPanel.add(createTopHeader(), BorderLayout.NORTH);

        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        contentArea.setBorder(new EmptyBorder(0, 20, 14, 20));
        contentArea.add(createMainContentCard(), BorderLayout.CENTER);
        mainPanel.add(contentArea, BorderLayout.CENTER);
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        setContentPane(rootPanel);


        refreshEmailList();
    }


    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            File file = new File("src/resources/icons/" + filename);
            if (!file.exists()) {
                file = new File("resources/icons/" + filename);
            }
            if (!file.exists()) {
                file = new File("bin/resources/icons/" + filename);
            }
            if (file.exists()) {
                ImageIcon original = new ImageIcon(file.getAbsolutePath());
                Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }


            java.net.URL url = getClass().getResource("/resources/icons/" + filename);
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // ---------------------------------------------------------------
    // Top Header: mascot + brand, search bar, profile/sign out
    // ---------------------------------------------------------------
    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setBackground(new Color(235, 241, 247));
        header.setBorder(new EmptyBorder(10, 12, 10, 20));


        // Center Search Bar
        JPanel searchContainer = new JPanel(new BorderLayout());

        searchContainer.setOpaque(false);
        searchContainer.setBorder(new EmptyBorder(0, 14, 0, 24));


        JPanel searchBar = new RoundedPanel(28, Color.WHITE, new Color(203, 213, 225));
        searchBar.setLayout(new BorderLayout(8, 0));
        searchBar.setPreferredSize(new Dimension(360, 38));
        searchBar.setBorder(new EmptyBorder(6, 12, 6, 12));


        JLabel searchIcon = new JLabel(new VectorIcon("search", 18, 18, TEXT_MUTED));


        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        txtSearch.setToolTipText("Search mail");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshEmailList();
            }
        });


        searchBar.add(searchIcon, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchContainer.add(searchBar, BorderLayout.CENTER);
        header.add(searchContainer, BorderLayout.CENTER);


        // Right: user profile, avatar, sign out
        JPanel rightIcons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        rightIcons.setOpaque(false);


        User currentUser = CurrentUser.getInstance().getUser();
        String userDisplay = currentUser != null ? currentUser.getFullName() + " (" + currentUser.getEmail() + ")" : "Guest";
        lblUserProfile = new JLabel(userDisplay);
        lblUserProfile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUserProfile.setForeground(new Color(55, 65, 81));


        JLabel avatarLabel = new JLabel(new VectorIcon("profile", 28, 28, BLUE_DARK));
        avatarLabel.setPreferredSize(new Dimension(32, 32));


        JButton btnSignOut = new JButton("Sign Out");
        btnSignOut.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSignOut.setForeground(new Color(55, 65, 81));
        btnSignOut.setContentAreaFilled(false);
        btnSignOut.setFocusPainted(false);
        btnSignOut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignOut.addActionListener(e -> processSignOut());


        rightIcons.add(lblUserProfile);
        rightIcons.add(avatarLabel);
        rightIcons.add(btnSignOut);


        header.add(rightIcons, BorderLayout.EAST);
        return header;
    }


    // ---------------------------------------------------------------
    // Left Sidebar: Compose pill + navigation tabs
    // ---------------------------------------------------------------
    private JPanel createLeftNavigation() {
        JPanel sidebar = new UITheme.GradientPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setMinimumSize(new Dimension(185, 0));
        sidebar.setBorder(new EmptyBorder(12, 10, 10, 10));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(165, 38));

        JLabel mascotLabel = new JLabel();
        ImageIcon naviLogo = loadIcon("navi.png", 30, 30);
        mascotLabel.setIcon(naviLogo != null
            ? naviLogo
            : new VectorIcon("brand", 28, 28, new Color(160, 220, 246)));

        JLabel lblLogo = new JLabel("NaviMail");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        brand.add(mascotLabel);
        brand.add(lblLogo);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(18));


        JButton btnCompose = UITheme.roundedButton("Compose", Color.WHITE, BLUE_DARK);
        btnCompose.setIcon(new VectorIcon("compose", 20, 20, BLUE_DARK));
        btnCompose.setIconTextGap(8);
        btnCompose.setFont(new Font("Google Sans Text", Font.BOLD, 14));
        btnCompose.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCompose.setMaximumSize(new Dimension(165, 40));
        btnCompose.addActionListener(e -> {
            ComposeDialog dialog = new ComposeDialog(this, null, this::refreshEmailList);
            dialog.setVisible(true);
        });


        sidebar.add(btnCompose);
        sidebar.add(Box.createVerticalStrut(20));


        inboxBadgeLabel = new JLabel("0");
        inboxBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        inboxBadgeLabel.setForeground(Color.WHITE);


        sidebar.add(createNavItem("Inbox", "mail-inbox-app.png", "INBOX", true, inboxBadgeLabel));
        sidebar.add(createNavItem("Sent", "sent.png", "SENT", false, null));
        sidebar.add(createNavItem("Drafts", "draft.png", "DRAFTS", false, null));
        sidebar.add(createNavItem("Important", "star.png", "IMPORTANT", false, null));
        sidebar.add(createNavItem("Archive", "tag.png", "ARCHIVE", false, null));
        sidebar.add(createNavItem("Trash", "trashbin.png", "TRASH", false, null));
        sidebar.add(createNavItem("Unread / Read", "snooze.png", "UNREAD", false, null));


        return sidebar;
    }


    private JPanel createNavItem(String text, String iconName, String tabKey, boolean active, JLabel badge) {
        RoundedPanel navItem = new RoundedPanel(9, active ? new Color(78, 139, 202, 185) : new Color(0, 0, 0, 0), null);
        navItem.setLayout(new BorderLayout(10, 0));
        navItem.setMaximumSize(new Dimension(165, 36));
        navItem.setPreferredSize(new Dimension(165, 36));
        navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navItem.setBorder(new EmptyBorder(6, 16, 6, 16));
        navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        navItem.putClientProperty("tabKey", tabKey);


        sidebarPanels.add(navItem);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(Color.WHITE);


        if (iconName != null) {
            label.setIcon(new VectorIcon(iconName, 18, 18, new Color(226, 232, 240)));
            label.setIconTextGap(8);
        }


        navItem.add(label, BorderLayout.WEST);
        if (badge != null) {
            navItem.add(badge, BorderLayout.EAST);
        }


        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveSidebarItem(navItem);
                activeTab = tabKey;
                currentPage = 1;
                refreshEmailList();
            }
        });


        return navItem;
    }


    private void setActiveSidebarItem(JPanel selectedPanel) {
        for (JPanel panel : sidebarPanels) {
            boolean isSelected = (panel == selectedPanel);
            if (panel instanceof RoundedPanel roundedPanel) {
                roundedPanel.setFillColor(isSelected
                        ? new Color(78, 139, 202, 185)
                        : new Color(0, 0, 0, 0));
            }
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JLabel && comp != inboxBadgeLabel) {
                    comp.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 13));
                }
            }
            panel.revalidate();
            panel.repaint();
        }
    }


    // ---------------------------------------------------------------
    // Main White Card: section header, action toolbar, table, pagination
    // ---------------------------------------------------------------
    private JPanel createMainContentCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(12, 16, 16, 16)
        ));


        // Page title sits above the white toolbar/content card.
        sectionTitleLabel = new JLabel("Inbox");
        sectionTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitleLabel.setForeground(new Color(30, 41, 59));
        sectionTitleLabel.setBorder(new EmptyBorder(0, 0, 12, 0));


        // Action Toolbar
        JPanel topToolbar = new JPanel(new BorderLayout());
        topToolbar.setOpaque(false);
        topToolbar.setBorder(new EmptyBorder(12, 0, 12, 0));


        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftActions.setOpaque(false);


        chkSelectAll = new JCheckBox("Select All");
        chkSelectAll.setOpaque(false);
        chkSelectAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkSelectAll.addActionListener(e -> {
            boolean sel = chkSelectAll.isSelected();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(sel, i, 0);
            }
        });


        JButton btnDelete = new JButton(" Delete Selected");
        ImageIcon trashImg = loadIcon("trashbin.png", 16, 16);
        if (trashImg != null) {
            btnDelete.setIcon(trashImg);
        }
        styleToolbarButton(btnDelete);
        btnDelete.addActionListener(e -> processDeleteSelected());


        JButton btnMarkReadUnread = new JButton("Mark as Read / Unread");
        styleToolbarButton(btnMarkReadUnread);
        btnMarkReadUnread.addActionListener(e -> processToggleReadUnreadSelected());


        JButton btnArchive = new JButton("Archive Selected");
        styleToolbarButton(btnArchive);
        btnArchive.addActionListener(e -> processArchiveSelected());


        leftActions.add(chkSelectAll);
        leftActions.add(btnDelete);
        leftActions.add(btnMarkReadUnread);
        leftActions.add(btnArchive);


        JPanel rightPagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPagination.setOpaque(false);


        lblPageInfo = new JLabel("Page 1 of 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPageInfo.setForeground(TEXT_MUTED);


        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        for (JButton arrow : new JButton[]{btnPrevPage, btnNextPage}) {
            arrow.setFont(new Font("Segoe UI", Font.BOLD, 14));
            arrow.setForeground(TEXT_MUTED);
            arrow.setContentAreaFilled(false);
            arrow.setFocusPainted(false);
            arrow.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            arrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderCurrentPage();
            }
        });
        btnNextPage.addActionListener(e -> {
            int maxPages = (int) Math.ceil((double) currentFilteredEmails.size() / ITEMS_PER_PAGE);
            if (currentPage < maxPages) {
                currentPage++;
                renderCurrentPage();
            }
        });


        rightPagination.add(lblPageInfo);
        rightPagination.add(btnPrevPage);
        rightPagination.add(btnNextPage);


        topToolbar.add(leftActions, BorderLayout.WEST);
        topToolbar.add(rightPagination, BorderLayout.EAST);


        JPanel northStack = new JPanel();
        northStack.setLayout(new BoxLayout(northStack, BoxLayout.Y_AXIS));
        northStack.setOpaque(false);
        northStack.add(topToolbar);


        card.add(northStack, BorderLayout.NORTH);


        // Email Table
        String[] columnNames = {"", "\u2605", contactColumnName(), "Subject & Content Snippet", "Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }


            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };


        emailTable = new JTable(tableModel);
        emailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailTable.setRowHeight(36);
        emailTable.setShowGrid(false);
        emailTable.setIntercellSpacing(new Dimension(0, 0));
        emailTable.setSelectionBackground(new Color(238, 242, 246));
        emailTable.setSelectionForeground(Color.BLACK);
        emailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailTable.getTableHeader().setForeground(new Color(71, 85, 105));
        emailTable.getTableHeader().setBackground(new Color(248, 250, 252));
        emailTable.getTableHeader().setPreferredSize(new Dimension(0, 32));


        emailTable.getColumnModel().getColumn(0).setMaxWidth(30);
        emailTable.getColumnModel().getColumn(1).setMaxWidth(30);
        emailTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        emailTable.getColumnModel().getColumn(3).setPreferredWidth(600);
        emailTable.getColumnModel().getColumn(4).setPreferredWidth(140);


        Icon starImg = new VectorIcon("star", 18, 18, new Color(234, 179, 8));
        emailTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                if ("\u2605".equals(value)) {
                    label.setIcon(starImg);
                    label.setText("");
                } else {
                    label.setIcon(null);
                    label.setText("\u2606");
                    label.setForeground(new Color(148, 163, 184));
                }
                return label;
            }
        });


        // Bold Sender/Subject cells for unread emails
        DefaultTableCellRenderer unreadRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                boolean unread = row >= 0 && row < pageEmails.size() && !pageEmails.get(row).isRead() && !"SENT".equals(activeTab);
                label.setFont(new Font("Segoe UI", unread ? Font.BOLD : Font.PLAIN, 13));
                return label;
            }
        };
        emailTable.getColumnModel().getColumn(2).setCellRenderer(unreadRenderer);
        emailTable.getColumnModel().getColumn(3).setCellRenderer(unreadRenderer);


        emailTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = emailTable.getSelectedRow();
                int col = emailTable.getSelectedColumn();
                if (row == -1 || row >= pageEmails.size()) return;
                Email email = pageEmails.get(row);


                if (col == 1) { // Toggle Important/Star
                    email.setImportant(!email.isImportant());
                    FileHandler.getInstance().updateEmail(email);
                    tableModel.setValueAt(email.isImportant() ? "\u2605" : "\u2606", row, 1);
                } else if (e.getClickCount() == 2) {
                    if (email.isDraft()) {
                        ComposeDialog composeDialog = new ComposeDialog(NaviMailApp.this, email, NaviMailApp.this::refreshEmailList);
                        composeDialog.setVisible(true);
                    } else {
                        EmailDetailDialog detailDialog = new EmailDetailDialog(NaviMailApp.this, email, NaviMailApp.this::refreshEmailList);
                        detailDialog.setVisible(true);
                    }
                }
            }
        });


        JScrollPane tableScroll = new JScrollPane(emailTable);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(Color.WHITE);


        emptyStatePanel = createEmptyStatePanel();
        JPanel contentSwitcher = new JPanel(new CardLayout());
        contentSwitcher.setOpaque(false);
        contentSwitcher.setBorder(BorderFactory.createLineBorder(new Color(241, 245, 249)));
        contentSwitcher.add(tableScroll, "table");
        contentSwitcher.add(emptyStatePanel, "empty");
        card.add(contentSwitcher, BorderLayout.CENTER);


        JPanel pageArea = new JPanel(new BorderLayout());
        pageArea.setOpaque(false);
        pageArea.add(sectionTitleLabel, BorderLayout.NORTH);
        pageArea.add(card, BorderLayout.CENTER);
        return pageArea;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 34));

        JLabel version = new JLabel("NaviMail v2.5 (c) 2024");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(100, 116, 139));
        footer.add(version);
        return footer;
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JPanel messageGroup = new JPanel();
        messageGroup.setLayout(new BoxLayout(messageGroup, BoxLayout.Y_AXIS));
        messageGroup.setOpaque(false);

        JLabel illustration = new JLabel(new EmptyMailboxIcon());
        illustration.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Your inbox is empty.");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(55, 65, 81));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Messages will appear here once received.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(75, 85, 99));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        messageGroup.add(illustration);
        messageGroup.add(Box.createVerticalStrut(12));
        messageGroup.add(title);
        messageGroup.add(Box.createVerticalStrut(4));
        messageGroup.add(subtitle);

        panel.add(messageGroup, new GridBagConstraints(
            0, 0, 1, 1, 1, 1,
            GridBagConstraints.CENTER,
            GridBagConstraints.NONE,
            new Insets(12, 12, 12, 12),
            0, 0
        ));
        return panel;
    }

    private void updateEmptyState(boolean isEmpty) {
        if (emptyStatePanel == null || emptyStatePanel.getParent() == null) return;
        CardLayout layout = (CardLayout) emptyStatePanel.getParent().getLayout();
        layout.show(emptyStatePanel.getParent(), isEmpty ? "empty" : "table");
    }


    private void styleToolbarButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(30, 41, 59));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(6, 10, 6, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }




    public void refreshEmailList() {
        List<Email> allEmails = FileHandler.getInstance().getEmails();
        String query = txtSearch.getText().trim().toLowerCase();

        User currentUser = CurrentUser.getInstance().getUser();
        String currentUserEmail = currentUser != null ? currentUser.getEmail() : null;

        currentFilteredEmails = allEmails.stream().filter(e -> {
            if (currentUserEmail == null) return false;
            boolean isRecipient = currentUserEmail.equalsIgnoreCase(e.getRecipient());
            boolean isSender = currentUserEmail.equalsIgnoreCase(e.getSender());
            if (!query.isEmpty()) {
                boolean match = (e.getSubject() != null && e.getSubject().toLowerCase().contains(query)) ||
                        (e.getSender() != null && e.getSender().toLowerCase().contains(query)) ||
                        (e.getRecipient() != null && e.getRecipient().toLowerCase().contains(query)) ||
                        (e.getBody() != null && e.getBody().toLowerCase().contains(query));
                if (!match) return false;
            }


            switch (activeTab) {
                case "SENT":
                    return isSender && !e.isDraft() && !e.isTrashed();
                case "DRAFTS":
                    return isSender && e.isDraft() && !e.isTrashed();
                case "IMPORTANT":
                    return (isRecipient || isSender) && e.isImportant() && !e.isTrashed() && !e.isDraft();
                case "ARCHIVE":
                    return isRecipient && e.isArchived() && !e.isTrashed() && !e.isDraft();
                case "TRASH":
                    return (isRecipient || isSender) && e.isTrashed();
                case "UNREAD":
                    return isRecipient && !e.isRead() && !e.isTrashed() && !e.isDraft();
                case "INBOX":
                default:
                    return isRecipient && !isSender && !e.isArchived() && !e.isTrashed() && !e.isDraft();
            }
        }).collect(Collectors.toList());


        if (sectionTitleLabel != null) {
            sectionTitleLabel.setText(sectionDisplayName(activeTab));
        }


        renderCurrentPage();
        updateInboxBadge();
    }


    private String sectionDisplayName(String tabKey) {
        switch (tabKey) {
            case "SENT": return "Sent";
            case "DRAFTS": return "Drafts";
            case "IMPORTANT": return "Important";
            case "ARCHIVE": return "Archive";
            case "TRASH": return "Trash";
            case "UNREAD": return "Unread / Read";
            case "INBOX":
            default: return "Inbox";
        }
    }


    private void renderCurrentPage() {
        tableModel.setRowCount(0);
        pageEmails.clear();
        emailTable.getColumnModel().getColumn(2).setHeaderValue(contactColumnName());


        int totalItems = currentFilteredEmails.size();
        updateEmptyState(totalItems == 0);
        int maxPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
        if (maxPages == 0) maxPages = 1;


        if (currentPage > maxPages) currentPage = maxPages;
        if (currentPage < 1) currentPage = 1;


        lblPageInfo.setText("Page " + currentPage + " of " + maxPages + " (" + totalItems + " items)");
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < maxPages);
        if (chkSelectAll != null) chkSelectAll.setSelected(false);


        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);


        for (int i = startIndex; i < endIndex; i++) {
            Email email = currentFilteredEmails.get(i);
            pageEmails.add(email);
            String star = email.isImportant() ? "\u2605" : "\u2606";
            String content = email.getSubject() + " - " + email.getBody();
            tableModel.addRow(new Object[]{
                    false,
                    star,
                    contactValue(email),
                    content,
                    email.getTimestamp() != null ? email.getTimestamp() : ""
            });
        }


        emailTable.getTableHeader().repaint();
        emailTable.revalidate();
        emailTable.repaint();
    }


    private String contactColumnName() {
        return "Sender";
    }


    private String contactValue(Email email) {
        return email.getSender() != null ? email.getSender() : "";
    }


    private List<String> getSelectedEmailIds() {
        List<String> selectedIds = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 0);
            if (checked != null && checked && i < pageEmails.size()) {
                selectedIds.add(pageEmails.get(i).getId());
            }
        }
        return selectedIds;
    }


    private void processDeleteSelected() {
        List<String> ids = getSelectedEmailIds();
        if (ids.isEmpty()) {
            UITheme.showMessage(this, "No emails selected.", "Selection Required");
            return;
        }


        FileHandler fh = FileHandler.getInstance();
        for (String id : ids) {
            Email e = fh.getEmails().stream().filter(mail -> mail.getId().equals(id)).findFirst().orElse(null);
            if (e != null) {
                if (activeTab.equals("TRASH")) {
                    fh.deleteEmail(id);
                } else {
                    e.setTrashed(true);
                    fh.updateEmail(e);
                }
            }
        }
        refreshEmailList();
    }


    private void processToggleReadUnreadSelected() {
        List<String> ids = getSelectedEmailIds();
        if (ids.isEmpty()) {
            UITheme.showMessage(this, "No emails selected.", "Selection Required");
            return;
        }


        FileHandler fh = FileHandler.getInstance();
        for (String id : ids) {
            Email e = fh.getEmails().stream().filter(mail -> mail.getId().equals(id)).findFirst().orElse(null);
            if (e != null) {
                e.setRead(!e.isRead());
                fh.updateEmail(e);
            }
        }
        refreshEmailList();
    }


    private void processArchiveSelected() {
        List<String> ids = getSelectedEmailIds();
        if (ids.isEmpty()) {
            UITheme.showMessage(this, "No emails selected.", "Selection Required");
            return;
        }


        FileHandler fh = FileHandler.getInstance();
        for (String id : ids) {
            Email e = fh.getEmails().stream().filter(mail -> mail.getId().equals(id)).findFirst().orElse(null);
            if (e != null) {
                e.setArchived(!e.isArchived());
                fh.updateEmail(e);
            }
        }
        refreshEmailList();
    }


    private void processSignOut() {
        int choice = UITheme.showConfirm(this, "Are you sure you want to sign out?", "Sign Out");
        if (choice == JOptionPane.YES_OPTION) {
            CurrentUser.getInstance().logout();

            // Close the mail client and return to the login screen so the
            // user has to sign in again before they can access their mail.
            dispose();
            SwingUtilities.invokeLater(LoginPage::new);
        }
    }


    private void updateInboxBadge() {
        if (inboxBadgeLabel == null) return;
        User currentUser = CurrentUser.getInstance().getUser();
        if (currentUser == null) {
            inboxBadgeLabel.setText("0");
            return;
        }
        String currentUserEmail = currentUser.getEmail();
        List<Email> allEmails = FileHandler.getInstance().getEmails();
        long count = allEmails.stream().filter(e -> currentUserEmail.equalsIgnoreCase(e.getRecipient())
                        && !e.isArchived() && !e.isTrashed() && !e.isDraft())
                .count();
        inboxBadgeLabel.setText(String.valueOf(count));
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

    private static final class RoundedPanel extends JPanel {
        private final int radius;
        private Color fillColor;
        private final Color borderColor;

        private RoundedPanel(int radius, Color fillColor, Color borderColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        private void setFillColor(Color fillColor) {
            this.fillColor = fillColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (fillColor.getAlpha() > 0) {
                g.setColor(fillColor);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            if (borderColor != null) {
                g.setColor(borderColor);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class VectorIcon implements Icon {
        private final String type;
        private final int size;
        private final Color color;

        private VectorIcon(String type, int width, int height, Color color) {
            this.type = type;
            this.size = Math.min(width, height);
            this.color = color;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (type.equals("search")) {
                g.drawOval(2, 2, size - 8, size - 8);
                g.drawLine(size - 7, size - 7, size - 2, size - 2);
            } else if (type.equals("profile")) {
                g.drawOval(1, 1, size - 3, size - 3);
                g.fillOval(size / 2 - 4, 5, 8, 8);
                g.drawArc(size / 2 - 9, size / 2, 18, 14, 0, 180);
            } else if (type.equals("brand")) {
                g.fillOval(3, 2, size - 6, size - 5);
                g.setColor(BLUE_DARK);
                g.fillOval(size / 2 - 2, 7, 4, 4);
                g.drawLine(size / 2 - 5, size - 5, size / 2 - 7, size - 1);
                g.drawLine(size / 2 + 5, size - 5, size / 2 + 7, size - 1);
            } else if (type.equals("compose")) {
                g.drawRect(2, 3, size - 7, size - 7);
                g.drawLine(size - 8, size - 4, size - 2, size - 10);
                g.drawLine(size - 5, size - 13, size - 2, size - 10);
            } else if (type.contains("inbox")) {
                g.drawRoundRect(1, 4, size - 3, size - 6, 3, 3);
                g.drawLine(2, 5, size / 2, size / 2 + 1);
                g.drawLine(size / 2, size / 2 + 1, size - 2, 5);
            } else if (type.contains("sent")) {
                Polygon arrow = new Polygon(new int[]{2, size - 2, 2}, new int[]{size / 2, 2, size - 2}, 3);
                g.fillPolygon(arrow);
            } else if (type.contains("star")) {
                Polygon star = new Polygon();
                for (int i = 0; i < 10; i++) {
                    double angle = -Math.PI / 2 + i * Math.PI / 5;
                    int radius = i % 2 == 0 ? size / 2 - 1 : size / 4;
                    star.addPoint(size / 2 + (int) (Math.cos(angle) * radius), size / 2 + (int) (Math.sin(angle) * radius));
                }
                g.fillPolygon(star);
            } else if (type.contains("trash")) {
                g.drawRect(4, 5, size - 8, size - 6);
                g.drawLine(2, 4, size - 2, 4);
                g.drawLine(size / 2 - 3, 2, size / 2 + 3, 2);
            } else if (type.contains("snooze")) {
                g.drawOval(2, 2, size - 4, size - 4);
                g.drawLine(size / 2, size / 2, size / 2, 5);
                g.drawLine(size / 2, size / 2, size - 5, size / 2);
            } else {
                g.drawRoundRect(2, 3, size - 5, size - 7, 3, 3);
            }
            g.dispose();
        }
    }

    private static final class EmptyMailboxIcon implements Icon {
        private static final int WIDTH = 82;
        private static final int HEIGHT = 76;

        @Override
        public int getIconWidth() {
            return WIDTH;
        }

        @Override
        public int getIconHeight() {
            return HEIGHT;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);

            g.setColor(new Color(226, 235, 244));
            g.fillOval(14, 5, 52, 52);
            g.setColor(new Color(252, 214, 190));
            g.fillRect(36, 54, 8, 19);
            g.setColor(new Color(55, 65, 81));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(18, 25, 36, 30, 8, 8);
            g.drawLine(18, 39, 54, 39);
            g.drawLine(36, 25, 36, 39);
            g.setColor(new Color(246, 156, 157));
            g.fillRect(45, 14, 15, 12);
            g.setColor(new Color(55, 65, 81));
            g.drawLine(45, 14, 45, 28);
            g.drawLine(45, 14, 60, 16);
            g.drawLine(60, 16, 60, 26);
            g.drawLine(60, 26, 45, 28);
            g.drawLine(10, 18, 6, 14);
            g.drawLine(15, 10, 14, 4);
            g.drawLine(22, 12, 26, 7);
            g.dispose();
        }
    }
}