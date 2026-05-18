package attendance.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Centralised UI theme — deep-blue sidebar, white content area.
 */
public class UITheme {

    // Palette
    public static final Color SIDEBAR_BG     = new Color(25,  42,  86);
    public static final Color SIDEBAR_HOVER  = new Color(41,  65, 122);
    public static final Color SIDEBAR_SELECT = new Color(67, 104, 208);
    public static final Color ACCENT         = new Color(67, 104, 208);
    public static final Color DANGER         = new Color(220,  53,  69);
    public static final Color SUCCESS        = new Color(40,  167,  69);
    public static final Color WARNING        = new Color(255, 193,   7);
    public static final Color CONTENT_BG     = new Color(245, 247, 252);
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color TEXT_PRIMARY   = new Color(33,  37,  41);
    public static final Color TEXT_MUTED     = new Color(108, 117, 125);
    public static final Color TABLE_HEADER   = new Color(52,  73, 126);
    public static final Color TABLE_ALT_ROW  = new Color(235, 240, 255);

    // Fonts
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_NAV    = new Font("Segoe UI", Font.BOLD,  13);

    /** Apply FlatLaf-style global defaults (works without extra library). */
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",        CONTENT_BG);
        UIManager.put("TextField.font",          FONT_BODY);
        UIManager.put("ComboBox.font",           FONT_BODY);
        UIManager.put("Button.font",             FONT_BODY);
        UIManager.put("Label.font",              FONT_BODY);
        UIManager.put("Table.font",              FONT_BODY);
        UIManager.put("TableHeader.font",        FONT_HEADER);
        UIManager.put("OptionPane.messageFont",  FONT_BODY);
    }

    /** Styled primary button (blue). */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_HEADER);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }

    /** Styled danger button (red). */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    /** Styled success button (green). */
    public static JButton successButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(SUCCESS);
        return btn;
    }

    /** Styled secondary (grey) button. */
    public static JButton secondaryButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(new Color(108, 117, 125));
        return btn;
    }

    /** Rounded card panel. */
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 225, 241), 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        return p;
    }

    /** Section header label. */
    public static JLabel headerLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    /** Style a JTable with alternating rows and custom header. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(new Color(228, 233, 245));
        table.setSelectionBackground(new Color(180, 200, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(0, 34));

        // Alternating row colours
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? CARD_BG : TABLE_ALT_ROW);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    /** Padded form field label. */
    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 225)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        );
    }
}
