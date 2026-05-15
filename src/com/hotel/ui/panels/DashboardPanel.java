package com.hotel.ui.panels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import com.hotel.model.Room;
import com.hotel.service.BookingService;
import com.hotel.service.GuestService;
import com.hotel.service.RoomService;

public class DashboardPanel extends JPanel {

    private final RoomService roomService;
    private final GuestService guestService;
    private final BookingService bookingService;

    private final JLabel totalRoomsVal    = new JLabel("0");
    private final JLabel availableRoomsVal = new JLabel("0");
    private final JLabel totalGuestsVal   = new JLabel("0");
    private final JLabel activeBookingsVal = new JLabel("0");

    public DashboardPanel(RoomService roomService, GuestService guestService, BookingService bookingService) {
        this.roomService    = roomService;
        this.guestService   = guestService;
        this.bookingService = bookingService;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel grid = new JPanel(new GridLayout(2, 2, 25, 25));
        grid.setOpaque(false);
        grid.add(createCard("Total Rooms",      totalRoomsVal,     new Color(41,  128, 185)));
        grid.add(createCard("Available Rooms",  availableRoomsVal, new Color(39,  174,  96)));
        grid.add(createCard("Total Guests",     totalGuestsVal,    new Color(142,  68, 173)));
        grid.add(createCard("Active Bookings",  activeBookingsVal, new Color(230, 126,  34)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(grid, gbc);

        refresh();
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accent, 2, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(accent);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 52));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(new Color(50, 50, 50));

        card.add(titleLabel,  BorderLayout.NORTH);
        card.add(valueLabel,  BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        List<Room> rooms = roomService.getAll();
        totalRoomsVal.setText(String.valueOf(rooms.size()));
        long avail = rooms.stream().filter(Room::isAvailable).count();
        availableRoomsVal.setText(String.valueOf(avail));
        totalGuestsVal.setText(String.valueOf(guestService.getAll().size()));
        long active = bookingService.getAll().stream()
                .filter(b -> "ACTIVE".equals(b.getStatus())).count();
        activeBookingsVal.setText(String.valueOf(active));
    }
}
