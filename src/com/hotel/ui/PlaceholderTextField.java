package com.hotel.ui;

import javax.swing.JTextField;
import java.awt.*;

public class PlaceholderTextField extends JTextField {

    private final String placeholder;

    public PlaceholderTextField(int columns, String placeholder) {
        super(columns);
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!getText().isEmpty() || isFocusOwner()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(170, 170, 170));
        g2.setFont(getFont().deriveFont(Font.ITALIC));

        Insets ins = getInsets();
        FontMetrics fm = g2.getFontMetrics();
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(placeholder, ins.left + 2, y);
        g2.dispose();
    }
}
