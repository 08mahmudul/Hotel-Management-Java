package com.hotel.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import com.hotel.exception.HotelException;
import com.hotel.model.Guest;
import com.hotel.service.GuestService;
import com.hotel.ui.PlaceholderTextField;

public class GuestPanel extends JPanel {

    private static final Font FONT = new Font("Arial", Font.PLAIN, 13);

    private final GuestService guestService;
    private boolean editMode = false;
    private String editingGuestId = null;

    // ── Form fields ──────────────────────────────────────────────────────────
    private JTextField fullNameField, phoneField, nidField, addressField;
    private JButton addBtn, cancelEditBtn;
    private JLabel statusLabel;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;

    // ── Constructor ──────────────────────────────────────────────────────────

    public GuestPanel(GuestService guestService) {
        this.guestService = guestService;
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
                BorderFactory.createLineBorder(new Color(0, 70, 130)), "Register / Edit Guest"));
        panel.setBackground(Color.WHITE);

        fullNameField = new PlaceholderTextField(18, "e.g. Rahim Uddin");
        phoneField    = new PlaceholderTextField(18, "e.g. 01711234567");
        nidField      = new PlaceholderTextField(18, "e.g. 1234567890123");
        addressField  = new PlaceholderTextField(18, "e.g. Dhaka, Bangladesh");

        panel.add(formRow("Full Name:",     fullNameField));
        panel.add(formRow("Phone:",         phoneField));
        panel.add(formRow("NID/Passport:",  nidField));
        panel.add(formRow("Address:",       addressField));

        addBtn        = styledButton("Register Guest");
        JButton clearBtn = styledButton("Clear");
        cancelEditBtn = styledButton("Cancel Edit");
        cancelEditBtn.setBackground(new Color(150, 50, 50));
        cancelEditBtn.setVisible(false);

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
        btn.setPreferredSize(new Dimension(140, 30));
        btn.setBackground(new Color(0, 70, 130));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Table section ─────────────────────────────────────────────────────────

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 70, 130)), "All Guests"));
        panel.setBackground(Color.WHITE);

        String[] cols = {"Guest ID", "Full Name", "Phone", "NID/Passport", "Address", "Registered Date", "Edit", "Delete"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(FONT);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionBackground(new Color(173, 216, 230));

        int[] widths = {75, 150, 110, 130, 150, 120, 65, 65};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        ButtonRenderer btnRenderer = new ButtonRenderer();
        table.getColumnModel().getColumn(6).setCellRenderer(btnRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(btnRenderer);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;
                if (col == 6) handleEdit(row);
                if (col == 7) handleDelete(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void handleAdd() {
        Guest g = new Guest();
        g.setFullName(fullNameField.getText().trim());
        g.setPhone(phoneField.getText().trim());
        g.setNidOrPassport(nidField.getText().trim());
        g.setAddress(addressField.getText().trim());
        try {
            guestService.add(g);
            refreshTable();
            clearForm();
            setStatus("Guest registered successfully!", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleSave() {
        try {
            Guest g = guestService.getById(editingGuestId);
            g.setFullName(fullNameField.getText().trim());
            g.setPhone(phoneField.getText().trim());
            g.setAddress(addressField.getText().trim());
            guestService.update(g);
            refreshTable();
            clearForm();
            setStatus("Guest updated successfully!", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleEdit(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            Guest g = guestService.getById(id);
            fullNameField.setText(g.getFullName());
            phoneField.setText(g.getPhone());
            nidField.setText(g.getNidOrPassport());
            nidField.setEditable(false);
            addressField.setText(g.getAddress() == null ? "" : g.getAddress());
            editMode = true;
            editingGuestId = id;
            addBtn.setText("Save Changes");
            cancelEditBtn.setVisible(true);
            setStatus("Editing guest " + id, false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    private void handleDelete(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete guest \"" + name + "\" (" + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            guestService.delete(id);
            refreshTable();
            setStatus("Guest " + id + " deleted.", false);
        } catch (HotelException ex) {
            setStatus(ex.getMessage(), true);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Guest g : guestService.getAll()) {
            tableModel.addRow(new Object[]{
                g.getId(), g.getFullName(), g.getPhone(),
                g.getNidOrPassport(),
                g.getAddress() == null ? "" : g.getAddress(),
                g.getCreatedAt(),
                "Edit", "Delete"
            });
        }
    }

    private void clearForm() {
        fullNameField.setText(""); phoneField.setText("");
        nidField.setText(""); nidField.setEditable(true);
        addressField.setText("");
        editMode = false; editingGuestId = null;
        addBtn.setText("Register Guest");
        cancelEditBtn.setVisible(false);
        setStatus(" ", false);
    }

    private void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 128, 0));
    }

    // ── Button renderer ───────────────────────────────────────────────────────

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ButtonRenderer() {
            setOpaque(true); setFont(new Font("Arial", Font.PLAIN, 12));
            setForeground(Color.WHITE); setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean selected, boolean focused, int row, int col) {
            setText(value == null ? "" : value.toString());
            setBackground("Delete".equals(value) ? new Color(180, 60, 60) : new Color(0, 100, 180));
            return this;
        }
    }
}
