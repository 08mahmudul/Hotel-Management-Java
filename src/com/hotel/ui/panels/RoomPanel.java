package com.hotel.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;
import com.hotel.exception.HotelException;
import com.hotel.model.*;
import com.hotel.service.RoomService;
import com.hotel.ui.PlaceholderTextField;

public class RoomPanel extends JPanel {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 13);

    private final RoomService roomService;
    private boolean editMode = false;
    private String editingRoomId = null;

    // ── Form fields ──────────────────────────────────────────────────────────
    private JTextField roomNumberField;
    private JComboBox<String> typeCombo;
    private JTextField priceField;

    // Dynamic panels (CardLayout)
    private JPanel dynamicPanel;
    private CardLayout dynamicCardLayout;
    private JTextField amenitiesField;
    private JCheckBox acCheckBox, minibarCheckBox;
    private JTextField floorField, maxOccField;
    private JCheckBox jacuzziCheckBox;

    // Buttons / status
    private JButton addBtn, cancelEditBtn;
    private JLabel statusLabel;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeFilterCombo;
    private JComboBox<String> availFilterCombo;

    // ── Constructor ──────────────────────────────────────────────────────────

    public RoomPanel(RoomService roomService) {
        this.roomService = roomService;
        setLayout(new BorderLayout(0, 5));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormSection(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        refreshTable();
    }

    // ── Form section ─────────────────────────────────────────────────────────

    private JPanel buildFormSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 70, 130)), "Add / Edit Room"));
        panel.setBackground(Color.WHITE);

        roomNumberField = new PlaceholderTextField(15, "e.g. 101");
        typeCombo = new JComboBox<>(new String[]{"STANDARD", "DELUXE", "SUITE"});
        priceField = new PlaceholderTextField(15, "e.g. 2500.00");

        panel.add(formRow("Room Number:", roomNumberField));
        panel.add(formRow("Type:",        typeCombo));
        panel.add(formRow("Price/Night:", priceField));

        // Dynamic section
        dynamicCardLayout = new CardLayout();
        dynamicPanel = new JPanel(dynamicCardLayout);
        dynamicPanel.setOpaque(false);

        amenitiesField = new PlaceholderTextField(20, "e.g. Fan, TV, Bathroom");
        JPanel stdPanel = formRow("Amenities:", amenitiesField);

        acCheckBox = new JCheckBox("Air Conditioning");
        minibarCheckBox = new JCheckBox("Minibar");
        JPanel dlxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        dlxPanel.setOpaque(false);
        dlxPanel.add(acCheckBox);
        dlxPanel.add(minibarCheckBox);

        floorField = new PlaceholderTextField(5, "e.g. 3");
        jacuzziCheckBox = new JCheckBox("Jacuzzi");
        maxOccField = new PlaceholderTextField(5, "e.g. 4");
        JPanel suitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        suitePanel.setOpaque(false);
        JLabel floorLbl = new JLabel("Floor:"); floorLbl.setFont(FONT);
        JLabel maxLbl   = new JLabel("  Max Occupancy:"); maxLbl.setFont(FONT);
        floorField.setFont(FONT); maxOccField.setFont(FONT);
        suitePanel.add(floorLbl); suitePanel.add(floorField);
        suitePanel.add(jacuzziCheckBox); suitePanel.add(maxLbl); suitePanel.add(maxOccField);

        dynamicPanel.add(stdPanel,   "STANDARD");
        dynamicPanel.add(dlxPanel,   "DELUXE");
        dynamicPanel.add(suitePanel, "SUITE");

        typeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                dynamicCardLayout.show(dynamicPanel, (String) typeCombo.getSelectedItem());
            }
        });

        panel.add(dynamicPanel);

        // Buttons
        addBtn        = styledButton("Add Room");
        JButton clearBtn = styledButton("Clear");
        cancelEditBtn = styledButton("Cancel Edit");
        cancelEditBtn.setVisible(false);
        cancelEditBtn.setBackground(new Color(150, 50, 50));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnRow.setOpaque(false);
        btnRow.add(addBtn); btnRow.add(clearBtn); btnRow.add(cancelEditBtn);
        panel.add(btnRow);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT);
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        statusRow.setOpaque(false);
        statusRow.add(statusLabel);
        panel.add(statusRow);

        // Wire buttons
        addBtn.addActionListener(e -> { if (editMode) handleSave(); else handleAdd(); });
        clearBtn.addActionListener(e -> clearForm());
        cancelEditBtn.addActionListener(e -> clearForm());

        return panel;
    }

    private JPanel formRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        row.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(110, 25));
        lbl.setFont(FONT);
        field.setFont(FONT);
        row.add(lbl); row.add(field);
        return row;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setPreferredSize(new Dimension(130, 30));
        btn.setBackground(new Color(0, 70, 130));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Table section ─────────────────────────────────────────────────────────

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 70, 130)), "All Rooms"));
        panel.setBackground(Color.WHITE);

        // Filters
        typeFilterCombo  = new JComboBox<>(new String[]{"All", "STANDARD", "DELUXE", "SUITE"});
        availFilterCombo = new JComboBox<>(new String[]{"All", "Available", "Booked"});
        typeFilterCombo.setFont(FONT);
        availFilterCombo.setFont(FONT);
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterRow.setOpaque(false);
        filterRow.add(new JLabel("Type:"));  filterRow.add(typeFilterCombo);
        filterRow.add(new JLabel("Status:")); filterRow.add(availFilterCombo);
        panel.add(filterRow, BorderLayout.NORTH);

        typeFilterCombo.addItemListener(e  -> { if (e.getStateChange() == ItemEvent.SELECTED) refreshTable(); });
        availFilterCombo.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) refreshTable(); });

        // Table
        String[] cols = {"Room ID", "Room No", "Type", "Price", "Status", "Edit", "Delete"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(FONT);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionBackground(new Color(173, 216, 230));

        // Column widths
        int[] widths = {80, 75, 90, 90, 90, 70, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Button renderer
        ButtonRenderer btnRenderer = new ButtonRenderer();
        table.getColumnModel().getColumn(5).setCellRenderer(btnRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(btnRenderer);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;
                if (col == 5) handleEdit(row);
                if (col == 6) handleDelete(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void handleAdd() {
        String type = (String) typeCombo.getSelectedItem();
        Room room = buildRoomFromForm(type);
        if (room == null) return;
        try {
            roomService.add(room);
            refreshTable();
            clearForm();
            setStatus("Room added successfully!", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleSave() {
        try {
            Room r = roomService.getById(editingRoomId);
            try {
                r.setPricePerNight(Double.parseDouble(priceField.getText().trim()));
            } catch (NumberFormatException ex) {
                setStatus("Price must be a valid number", true); return;
            }
            applyDynamicFields(r);
            roomService.update(r);
            refreshTable();
            clearForm();
            setStatus("Room updated successfully!", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleEdit(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            Room r = roomService.getById(id);
            roomNumberField.setText(r.getRoomNumber());
            roomNumberField.setEditable(false);
            typeCombo.setSelectedItem(r.getRoomType());
            dynamicCardLayout.show(dynamicPanel, r.getRoomType());
            priceField.setText(String.valueOf(r.getPricePerNight()));

            if (r instanceof StandardRoom) {
                amenitiesField.setText(((StandardRoom) r).getAmenities());
            } else if (r instanceof DeluxeRoom) {
                acCheckBox.setSelected(((DeluxeRoom) r).isHasAC());
                minibarCheckBox.setSelected(((DeluxeRoom) r).isHasMinibar());
            } else if (r instanceof Suite) {
                Suite s = (Suite) r;
                floorField.setText(String.valueOf(s.getFloorNumber()));
                jacuzziCheckBox.setSelected(s.isHasJacuzzi());
                maxOccField.setText(String.valueOf(s.getMaxOccupancy()));
            }

            editMode = true;
            editingRoomId = id;
            addBtn.setText("Save Changes");
            cancelEditBtn.setVisible(true);
            setStatus("Editing room " + id, false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleDelete(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete room " + id + "? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            roomService.delete(id);
            refreshTable();
            setStatus("Room " + id + " deleted.", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        String typeFilter  = (String) typeFilterCombo.getSelectedItem();
        String availFilter = (String) availFilterCombo.getSelectedItem();

        List<Room> rooms = roomService.getAll().stream()
                .filter(r -> "All".equals(typeFilter)  || r.getRoomType().equals(typeFilter))
                .filter(r -> {
                    if ("Available".equals(availFilter)) return r.isAvailable();
                    if ("Booked".equals(availFilter))    return !r.isAvailable();
                    return true;
                })
                .collect(Collectors.toList());

        for (Room r : rooms) {
            tableModel.addRow(new Object[]{
                r.getId(), r.getRoomNumber(), r.getRoomType(),
                String.format("%.2f", r.getPricePerNight()),
                r.isAvailable() ? "Available" : "Booked",
                "Edit", "Delete"
            });
        }
    }

    private void clearForm() {
        roomNumberField.setText(""); roomNumberField.setEditable(true);
        typeCombo.setSelectedIndex(0);
        priceField.setText("");
        amenitiesField.setText("");
        acCheckBox.setSelected(false); minibarCheckBox.setSelected(false);
        floorField.setText(""); jacuzziCheckBox.setSelected(false); maxOccField.setText("");
        dynamicCardLayout.show(dynamicPanel, "STANDARD");
        editMode = false; editingRoomId = null;
        addBtn.setText("Add Room");
        cancelEditBtn.setVisible(false);
        setStatus(" ", false);
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Room buildRoomFromForm(String type) {
        Room room;
        if ("STANDARD".equals(type)) {
            StandardRoom sr = new StandardRoom();
            sr.setAmenities(amenitiesField.getText().trim());
            room = sr;
        } else if ("DELUXE".equals(type)) {
            DeluxeRoom dr = new DeluxeRoom();
            dr.setHasAC(acCheckBox.isSelected());
            dr.setHasMinibar(minibarCheckBox.isSelected());
            room = dr;
        } else {
            Suite s = new Suite();
            try { s.setFloorNumber(Integer.parseInt(floorField.getText().trim())); }
            catch (NumberFormatException ex) { setStatus("Floor must be a whole number", true); return null; }
            s.setHasJacuzzi(jacuzziCheckBox.isSelected());
            try { s.setMaxOccupancy(Integer.parseInt(maxOccField.getText().trim())); }
            catch (NumberFormatException ex) { setStatus("Max occupancy must be a whole number", true); return null; }
            room = s;
        }
        room.setRoomNumber(roomNumberField.getText().trim());
        room.setRoomType(type);
        try { room.setPricePerNight(Double.parseDouble(priceField.getText().trim())); }
        catch (NumberFormatException ex) { setStatus("Price must be a valid number", true); return null; }
        return room;
    }

    private void applyDynamicFields(Room r) {
        if (r instanceof StandardRoom) {
            ((StandardRoom) r).setAmenities(amenitiesField.getText().trim());
        } else if (r instanceof DeluxeRoom) {
            ((DeluxeRoom) r).setHasAC(acCheckBox.isSelected());
            ((DeluxeRoom) r).setHasMinibar(minibarCheckBox.isSelected());
        } else if (r instanceof Suite) {
            Suite s = (Suite) r;
            try { s.setFloorNumber(Integer.parseInt(floorField.getText().trim())); } catch (NumberFormatException ignored) {}
            s.setHasJacuzzi(jacuzziCheckBox.isSelected());
            try { s.setMaxOccupancy(Integer.parseInt(maxOccField.getText().trim())); } catch (NumberFormatException ignored) {}
        }
    }

    // ── Button renderer ───────────────────────────────────────────────────────

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 12));
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean selected, boolean focused, int row, int col) {
            setText(value == null ? "" : value.toString());
            boolean isDelete = "Delete".equals(value);
            setBackground(isDelete ? new Color(180, 60, 60) : new Color(0, 100, 180));
            return this;
        }
    }
}
