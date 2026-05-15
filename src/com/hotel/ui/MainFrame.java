package com.hotel.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import com.hotel.service.BookingService;
import com.hotel.service.GuestService;
import com.hotel.service.RoomService;
import com.hotel.ui.panels.*;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel centerPanel   = new JPanel(cardLayout);
    private JLabel statusLabel;

    private final RoomService    roomService    = new RoomService();
    private final GuestService   guestService   = new GuestService();
    private final BookingService bookingService = new BookingService();

    private final DashboardPanel dashboardPanel;
    private final RoomPanel      roomPanel;
    private final GuestPanel     guestPanel;
    private final BookingPanel   bookingPanel;

    public MainFrame() {
        dashboardPanel = new DashboardPanel(roomService, guestService, bookingService);
        roomPanel      = new RoomPanel(roomService);
        guestPanel     = new GuestPanel(guestService);
        bookingPanel   = new BookingPanel(bookingService, guestService, roomService);

        setTitle("Hotel Management System");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildSidebar(),   BorderLayout.WEST);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        dashboardPanel.refresh();
    }

    // ── Header ───────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 70, 130));
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        header.setPreferredSize(new Dimension(0, 55));

        JLabel title = new JLabel("Hotel Management System");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel date = new JLabel(LocalDate.now().toString());
        date.setFont(new Font("Arial", Font.PLAIN, 13));
        date.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(date,  BorderLayout.EAST);
        return header;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setBackground(new Color(30, 40, 55));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        addSidebarButton(sidebar, "Dashboard",         "dashboard");
        addSidebarButton(sidebar, "Room Management",   "rooms");
        addSidebarButton(sidebar, "Guest Management",  "guests");
        addSidebarButton(sidebar, "Booking Management","bookings");
        return sidebar;
    }

    private void addSidebarButton(JPanel sidebar, String label, String key) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(185, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(new Color(30, 40, 55));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 73, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(30, 40, 55));
            }
        });
        btn.addActionListener(e -> {
            cardLayout.show(centerPanel, key);
            if ("dashboard".equals(key)) dashboardPanel.refresh();
            if ("bookings".equals(key))  bookingPanel.onPanelShown();
        });
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 3)));
    }

    // ── Center ────────────────────────────────────────────────────────────────

    private JPanel buildCenter() {
        centerPanel.add(dashboardPanel, "dashboard");
        centerPanel.add(roomPanel,      "rooms");
        centerPanel.add(guestPanel,     "guests");
        centerPanel.add(bookingPanel,   "bookings");
        cardLayout.show(centerPanel,    "dashboard");
        return centerPanel;
    }

    // ── Status bar ────────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bar.add(statusLabel);
        return bar;
    }

    public void setStatus(String message, boolean isError) {
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
        statusLabel.setText(message);
    }
}
