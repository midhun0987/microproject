package ui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Inventory System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Use BorderLayout by default
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Product Management", new ProductPanel());

        add(tabs);

        pack(); // Size frame based on preferred sizes of contents

        setLocationRelativeTo(null); // Center on screen

        setVisible(true);
    }

    public static void main(String[] args) {
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
