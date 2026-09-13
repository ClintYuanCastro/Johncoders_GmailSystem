package prelim.group.gui;

import prelim.group.filehandler.FileHandler;
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

    // Navy Gmail-style palette (matches System 1 look & feel)
    private static final Color NAVY_BG = new Color(6, 50, 79);
    private static final Color NAVY_ACTIVE = new Color(0, 77, 122);
    private static final Color SEARCH_BG = new Color(234, 241, 251);
    private static final Color ACCENT_BLUE = new Color(11, 87, 208);
    private static final Color COMPOSE_PILL = new Color(194, 231, 255);
    private static final Color MUTED_TEXT = new Color(100, 116, 139);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);

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
    private final List<JPanel> sidebarPanels = new ArrayList<>();

    private List<Email> currentFilteredEmails = new ArrayList<>();
    private List<Email> pageEmails = new ArrayList<>();

    public NaviMailApp() {
        setTitle("Mail Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(NAVY_BG);

        rootPanel.add(createTopHeader(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setOpaque(false);
        bodyPanel.add(createLeftNavigation(), BorderLayout.WEST);
        bodyPanel.add(createMainContentCard(), BorderLayout.CENTER);

        rootPanel.add(bodyPanel, BorderLayout.CENTER);
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
    // Top Header: hamburger + mascot + brand, search bar, profile/sign out
    // ---------------------------------------------------------------
    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Left Branding
        JPanel leftBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftBrand.setOpaque(false);

        JLabel menuBtn = new JLabel("\u2261");
        menuBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel mascotLabel = new JLabel();
        ImageIcon naviImg = loadIcon("navi.png", 40, 40);
        if (naviImg != null) {
            mascotLabel.setIcon(naviImg);
        } else {
            mascotLabel.setText("\uD83D\uDC3B");
            mascotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        }

        JLabel lblLogo = new JLabel("NaviMail");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);

        leftBrand.add(menuBtn);
        leftBrand.add(mascotLabel);
        leftBrand.add(lblLogo);
        header.add(leftBrand, BorderLayout.WEST);

        // Center Search Bar
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setOpaque(false);
        searchContainer.setBorder(new EmptyBorder(0, 40, 0, 40));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(SEARCH_BG);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SEARCH_BG, 1, true),
                new EmptyBorder(6, 16, 6, 10)
        ));

        JLabel searchIcon = new JLabel();
        ImageIcon searchImg = loadIcon("search.png", 18, 18);
        if (searchImg != null) {
            searchIcon.setIcon(searchImg);
        } else {
            searchIcon.setText("\uD83D\uDD0D");
            searchIcon.setForeground(MUTED_TEXT);
        }

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(null);
        txtSearch.setOpaque(false);
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshEmailList();
            }
        });

        JButton btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSearch.setContentAreaFilled(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setForeground(ACCENT_BLUE);
        btnSearch.setBorder(new EmptyBorder(0, 8, 0, 0));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> refreshEmailList());

        searchBar.add(searchIcon, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(btnSearch, BorderLayout.EAST);
        searchContainer.add(searchBar, BorderLayout.CENTER);
        header.add(searchContainer, BorderLayout.CENTER);

        // Right: user profile, avatar, sign out
        JPanel rightIcons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightIcons.setOpaque(false);

        User currentUser = CurrentUser.getInstance().getUser();
        String userDisplay = currentUser != null ? currentUser.getFullName() + " (" + currentUser.getEmail() + ")" : "Guest";
        lblUserProfile = new JLabel(userDisplay);
        lblUserProfile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUserProfile.setForeground(Color.WHITE);

        JLabel avatarLabel = new JLabel();
        ImageIcon accountImg = loadIcon("account.png", 28, 28);
        if (accountImg != null) {
            avatarLabel.setIcon(accountImg);
        } else {
            avatarLabel.setText(" I ");
            avatarLabel.setOpaque(true);
            avatarLabel.setBackground(ACCENT_BLUE);
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            avatarLabel.setPreferredSize(new Dimension(32, 32));
            avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        }

        JButton btnSignOut = new JButton("Sign Out");
        btnSignOut.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSignOut.setForeground(Color.WHITE);
        btnSignOut.setContentAreaFilled(false);
        btnSignOut.setFocusPainted(false);
        btnSignOut.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
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
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(10, 15, 10, 15));

        JButton btnCompose = new JButton(" + Compose");
        ImageIcon composeImg = loadIcon("compose.png", 20, 20);
        if (composeImg != null) {
            btnCompose.setIcon(composeImg);
        }
        btnCompose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCompose.setBackground(COMPOSE_PILL);
        btnCompose.setForeground(new Color(0, 29, 53));
        btnCompose.setFocusPainted(false);
        btnCompose.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnCompose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompose.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCompose.setMaximumSize(new Dimension(220, 44));
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
        sidebar.add(createNavItem("Drafts", "draft.png", "DRAFTS", false, null));
        sidebar.add(createNavItem("Important", "star.png", "IMPORTANT", false, null));
        sidebar.add(createNavItem("Archive", "tag.png", "ARCHIVE", false, null));
        sidebar.add(createNavItem("Trash", "trashbin.png", "TRASH", false, null));
        sidebar.add(createNavItem("Unread / Read", "snooze.png", "UNREAD", false, null));

        return sidebar;
    }

    private JPanel createNavItem(String text, String iconName, String tabKey, boolean active, JLabel badge) {
        JPanel navItem = new JPanel(new BorderLayout(10, 0));
        navItem.setOpaque(true);
        navItem.setMaximumSize(new Dimension(200, 36));
        navItem.setPreferredSize(new Dimension(200, 36));
        navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navItem.setBorder(new EmptyBorder(6, 16, 6, 16));
        navItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        navItem.putClientProperty("tabKey", tabKey);

        sidebarPanels.add(navItem);
        navItem.setBackground(active ? NAVY_ACTIVE : NAVY_BG);

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
            panel.setBackground(isSelected ? NAVY_ACTIVE : NAVY_BG);
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
                BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Section title (reflects active nav tab), styled like an active tab underline
        JPanel sectionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sectionBar.setOpaque(false);
        sectionBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT));

        sectionTitleLabel = new JLabel("Inbox");
        sectionTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitleLabel.setForeground(ACCENT_BLUE);
        sectionTitleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, ACCENT_BLUE),
                new EmptyBorder(8, 4, 8, 20)
        ));
        sectionBar.add(sectionTitleLabel);

        // Action Toolbar
        JPanel topToolbar = new JPanel(new BorderLayout());
        topToolbar.setOpaque(false);
        topToolbar.setBorder(new EmptyBorder(8, 5, 8, 5));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
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
        lblPageInfo.setForeground(MUTED_TEXT);

        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        for (JButton arrow : new JButton[]{btnPrevPage, btnNextPage}) {
            arrow.setFont(new Font("Segoe UI", Font.BOLD, 14));
            arrow.setForeground(MUTED_TEXT);
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
        northStack.add(sectionBar);
        northStack.add(topToolbar);

        card.add(northStack, BorderLayout.NORTH);

        // Email Table
        String[] columnNames = {"", "\u2605", "Sender", "Subject & Content Snippet", "Time"};
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

        emailTable.getColumnModel().getColumn(0).setMaxWidth(30);
        emailTable.getColumnModel().getColumn(1).setMaxWidth(30);
        emailTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        emailTable.getColumnModel().getColumn(3).setPreferredWidth(600);
        emailTable.getColumnModel().getColumn(4).setPreferredWidth(140);

        ImageIcon starImg = loadIcon("star.png", 16, 16);
        emailTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                if ("\u2605".equals(value)) {
                    if (starImg != null) {
                        label.setIcon(starImg);
                        label.setText("");
                    } else {
                        label.setIcon(null);
                        label.setText("\u2605");
                        label.setForeground(new Color(234, 179, 8));
                    }
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
                boolean unread = row >= 0 && row < pageEmails.size() && !pageEmails.get(row).isRead();
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

        card.add(tableScroll, BorderLayout.CENTER);

        return card;
    }

    private void styleToolbarButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(30, 41, 59));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    public void refreshEmailList() {
        List<Email> allEmails = FileHandler.getInstance().getEmails();
        String query = txtSearch.getText().trim().toLowerCase();

        currentFilteredEmails = allEmails.stream().filter(e -> {
            if (!query.isEmpty()) {
                boolean match = (e.getSubject() != null && e.getSubject().toLowerCase().contains(query)) ||
                        (e.getSender() != null && e.getSender().toLowerCase().contains(query)) ||
                        (e.getBody() != null && e.getBody().toLowerCase().contains(query));
                if (!match) return false;
            }

            switch (activeTab) {
                case "DRAFTS":
                    return e.isDraft() && !e.isTrashed();
                case "IMPORTANT":
                    return e.isImportant() && !e.isTrashed() && !e.isDraft();
                case "ARCHIVE":
                    return e.isArchived() && !e.isTrashed() && !e.isDraft();
                case "TRASH":
                    return e.isTrashed();
                case "UNREAD":
                    return !e.isRead() && !e.isTrashed() && !e.isDraft();
                case "INBOX":
                default:
                    return !e.isArchived() && !e.isTrashed() && !e.isDraft();
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

        int totalItems = currentFilteredEmails.size();
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
                    email.getSender(),
                    content,
                    email.getTimestamp() != null ? email.getTimestamp() : ""
            });
        }

        emailTable.revalidate();
        emailTable.repaint();
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
            JOptionPane.showMessageDialog(this, "No emails selected.", "Selection Required", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "No emails selected.", "Selection Required", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "No emails selected.", "Selection Required", JOptionPane.WARNING_MESSAGE);
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
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Sign Out", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            CurrentUser.getInstance().logout();
            lblUserProfile.setText("Signed Out");
            JOptionPane.showMessageDialog(this, "You have been signed out.", "Signed Out", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateInboxBadge() {
        if (inboxBadgeLabel == null) return;
        List<Email> allEmails = FileHandler.getInstance().getEmails();
        long count = allEmails.stream().filter(e -> !e.isArchived() && !e.isTrashed() && !e.isDraft()).count();
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
}
