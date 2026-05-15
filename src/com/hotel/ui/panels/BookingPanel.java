package com.hotel.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hotel.exception.HotelException;
import com.hotel.model.*;
import com.hotel.service.BookingService;
import com.hotel.service.GuestService;
import com.hotel.service.RoomService;
import com.hotel.ui.PlaceholderTextField;

public class BookingPanel extends JPanel {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 13);

    private final BookingService bookingService;
    private final GuestService   guestService;
    private final RoomService    roomService;

    // ── Form ─────────────────────────────────────────────────────────────────
    private JComboBox<String> guestCombo, roomCombo;
    private JTextField checkInField, checkOutField;
    private JLabel billPreviewLabel;
    private JButton confirmBtn;
    private JLabel statusLabel;

    // ── Table ─────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilterCombo;

    // ── Constructor ──────────────────────────────────────────────────────────

    public BookingPanel(BookingService bookingService, GuestService guestService, RoomService roomService) {
        this.bookingService = bookingService;
        this.guestService   = guestService;
        this.roomService    = roomService;

        setLayout(new BorderLayout(0, 5));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormSection(),  BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);

        refreshGuestCombo();
        refreshRoomCombo();
        refreshTable();
    }

    // ── Form section ─────────────────────────────────────────────────────────

    private JPanel buildFormSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 70, 130)), "New Booking"));
        panel.setBackground(Color.WHITE);

        guestCombo   = new JComboBox<>();
        roomCombo    = new JComboBox<>();
        checkInField  = new PlaceholderTextField(12, "YYYY-MM-DD");
        checkOutField = new PlaceholderTextField(12, "YYYY-MM-DD");
        billPreviewLabel = new JLabel("  —");

        guestCombo.setFont(FONT);
        roomCombo.setFont(FONT);
        checkInField.setFont(FONT);
        checkOutField.setFont(FONT);
        billPreviewLabel.setFont(new Font("Arial", Font.BOLD, 13));
        billPreviewLabel.setForeground(new Color(0, 70, 130));

        panel.add(formRow("Guest:",      guestCombo));
        panel.add(formRow("Room:",       roomCombo));
        panel.add(formRow("Check-in (YYYY-MM-DD):",  checkInField));
        panel.add(formRow("Check-out (YYYY-MM-DD):", checkOutField));
        panel.add(formRow("Bill preview:", billPreviewLabel));

        FocusAdapter dateListener = new FocusAdapter() {
            public void focusLost(FocusEvent e) { updateBillPreview(); }
        };
        checkInField.addFocusListener(dateListener);
        checkOutField.addFocusListener(dateListener);
        roomCombo.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) updateBillPreview(); });

        confirmBtn = new JButton("Confirm Booking");
        confirmBtn.setFont(FONT);
        confirmBtn.setPreferredSize(new Dimension(160, 30));
        confirmBtn.setBackground(new Color(39, 174, 96));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setOpaque(true);
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> handleConfirm());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnRow.setOpaque(false);
        btnRow.add(confirmBtn);
        panel.add(btnRow);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT);
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        statusRow.setOpaque(false);
        statusRow.add(statusLabel);
        panel.add(statusRow);

        return panel;
    }

    private JPanel formRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        row.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(185, 25));
        lbl.setFont(FONT);
        row.add(lbl); row.add(field);
        return row;
    }

    // ── Table section ─────────────────────────────────────────────────────────

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 70, 130)), "All Bookings"));
        panel.setBackground(Color.WHITE);

        statusFilterCombo = new JComboBox<>(new String[]{"All", "ACTIVE", "CANCELLED"});
        statusFilterCombo.setFont(FONT);
        statusFilterCombo.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) refreshTable(); });

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterRow.setOpaque(false);
        filterRow.add(new JLabel("Filter by Status:")); filterRow.add(statusFilterCombo);
        panel.add(filterRow, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Guest", "Room", "Check-in", "Check-out", "Nights", "Total Bill", "Status", "Cancel"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(FONT);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionBackground(new Color(173, 216, 230));

        int[] widths = {90, 150, 120, 95, 95, 55, 90, 90, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(8).setCellRenderer(new CancelButtonRenderer());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 8) return;
                String status = (String) tableModel.getValueAt(row, 7);
                if (!"ACTIVE".equals(status)) return;
                handleCancel(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void handleConfirm() {
        if (guestCombo.getSelectedItem() == null || roomCombo.getSelectedItem() == null) {
            setStatus("Please select a guest and a room.", true); return;
        }
        Booking b = new Booking();
        b.setGuestId(extractId(guestCombo.getSelectedItem().toString()));
        b.setRoomId(extractId(roomCombo.getSelectedItem().toString()));
        b.setCheckInDate(checkInField.getText().trim());
        b.setCheckOutDate(checkOutField.getText().trim());
        try {
            bookingService.add(b);
            refreshTable();
            refreshRoomCombo();
            clearForm();
            setStatus("Booking confirmed!", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleCancel(int row) {
        String id   = (String) tableModel.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel booking " + id + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            bookingService.cancelBooking(id);
            refreshTable();
            refreshRoomCombo();
            setStatus("Booking " + id + " cancelled.", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    // ── Refresh helpers ───────────────────────────────────────────────────────

    public void onPanelShown() {
        refreshGuestCombo();
        refreshRoomCombo();
        refreshTable();
    }

    private void refreshGuestCombo() {
        guestCombo.removeAllItems();
        for (Guest g : guestService.getAll()) {
            guestCombo.addItem(g.getFullName() + " (" + g.getId() + ")");
        }
    }

    public void refreshRoomCombo() {
        roomCombo.removeAllItems();
        for (Room r : roomService.getAll()) {
            if (r.isAvailable()) {
                roomCombo.addItem(r.getRoomNumber() + " - " + r.getRoomType() + " (" + r.getId() + ")");
            }
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        String filter = (String) statusFilterCombo.getSelectedItem();

        Map<String, String> guestNames = new HashMap<>();
        for (Guest g : guestService.getAll()) guestNames.put(g.getId(), g.getFullName());
        Map<String, String> roomNums = new HashMap<>();
        for (Room r : roomService.getAll()) roomNums.put(r.getId(), r.getRoomNumber());

        List<Booking> bookings = bookingService.getAll();
        for (Booking b : bookings) {
            if (!"All".equals(filter) && !b.getStatus().equals(filter)) continue;

            long nights = 0;
            try {
                nights = ChronoUnit.DAYS.between(
                        LocalDate.parse(b.getCheckInDate()),
                        LocalDate.parse(b.getCheckOutDate()));
            } catch (Exception ignored) {}

            tableModel.addRow(new Object[]{
                b.getId(),
                guestNames.getOrDefault(b.getGuestId(), b.getGuestId()),
                roomNums.getOrDefault(b.getRoomId(), b.getRoomId()),
                b.getCheckInDate(), b.getCheckOutDate(),
                nights,
                String.format("%.2f", b.getTotalBill()),
                b.getStatus(),
                "ACTIVE".equals(b.getStatus()) ? "Cancel" : "—"
            });
        }
    }

    private void updateBillPreview() {
        try {
            String roomItem = (String) roomCombo.getSelectedItem();
            if (roomItem == null) return;
            String roomId = extractId(roomItem);
            Room room = roomService.getById(roomId);
            LocalDate in  = LocalDate.parse(checkInField.getText().trim());
            LocalDate out = LocalDate.parse(checkOutField.getText().trim());
            if (!out.isAfter(in)) { billPreviewLabel.setText("Check-out must be after check-in"); return; }
            long nights = ChronoUnit.DAYS.between(in, out);
            double total = nights * room.getPricePerNight();
            billPreviewLabel.setText(nights + " night(s)  ×  " + room.getPricePerNight() + "  =  BDT " + String.format("%.2f", total));
        } catch (Exception ignored) {
            billPreviewLabel.setText("  —");
        }
    }

    private void clearForm() {
        checkInField.setText("");
        checkOutField.setText("");
        billPreviewLabel.setText("  —");
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    private String extractId(String item) {
        int open  = item.lastIndexOf('(');
        int close = item.lastIndexOf(')');
        return (open >= 0 && close > open) ? item.substring(open + 1, close) : "";
    }

    // ── Cancel button renderer ────────────────────────────────────────────────

    private static class CancelButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        CancelButtonRenderer() {
            setOpaque(true); setFont(new Font("Arial", Font.PLAIN, 12));
            setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean selected, boolean focused, int row, int col) {
            String val = value == null ? "" : value.toString();
            setText(val);
            if ("Cancel".equals(val)) {
                setBackground(new Color(180, 60, 60));
                setForeground(Color.WHITE);
                setEnabled(true);
            } else {
                setBackground(new Color(200, 200, 200));
                setForeground(new Color(120, 120, 120));
                setEnabled(false);
            }
            return this;
        }
    }
}
