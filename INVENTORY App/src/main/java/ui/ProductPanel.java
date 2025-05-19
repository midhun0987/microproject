package ui;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import db.MongoConnection;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class ProductPanel extends JPanel {
    private JTextField tfId, tfName, tfPrice, tfQty;
    private JButton btnAdd, btnUpdate, btnDelete;
    private JTable productTable;
    private DefaultTableModel tableModel;

    private MongoCollection<Document> products;

    public ProductPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255)); // Light blue background

        products = MongoConnection.getDatabase().getCollection("products");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("INVENTORY MANAGEMENT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // Product ID
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Product ID:"), gbc);
        tfId = new JTextField(35);
        gbc.gridx = 1;
        add(tfId, gbc);

        // Name
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Name:"), gbc);
        tfName = new JTextField(15);
        gbc.gridx = 1;
        add(tfName, gbc);

        // Price
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Price:"), gbc);
        tfPrice = new JTextField(15);
        gbc.gridx = 1;
        add(tfPrice, gbc);

        // Quantity
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Quantity:"), gbc);
        tfQty = new JTextField(15);
        gbc.gridx = 1;
        add(tfQty, gbc);

        // Buttons panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(240, 248, 255));
        btnAdd = new JButton("Add");
        btnAdd.setBackground(new Color(60, 179, 113)); // Medium sea green
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(100, 149, 237)); // Cornflower blue
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 20, 60)); // Crimson
        btnDelete.setForeground(Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnPanel, gbc);

        // Table to show products
        String[] columns = {"Product ID", "Name", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            // Make table cells non-editable
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(400, 180));
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        // Load existing products into table
        loadProductsToTable();

        // Button actions
        btnAdd.addActionListener(e -> {
            try {
                String id = tfId.getText().trim();
                String name = tfName.getText().trim();
                double price = Double.parseDouble(tfPrice.getText().trim());
                int qty = Integer.parseInt(tfQty.getText().trim());

                Document existing = products.find(Filters.eq("productId", id)).first();
                if (existing != null) {
                    JOptionPane.showMessageDialog(this, "Product ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Document doc = new Document("productId", id)
                        .append("name", name)
                        .append("price", price)
                        .append("quantity", qty);
                products.insertOne(doc);
                JOptionPane.showMessageDialog(this, "Product Added Successfully");

                loadProductsToTable();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            try {
                String id = tfId.getText().trim();
                double price = Double.parseDouble(tfPrice.getText().trim());
                int qty = Integer.parseInt(tfQty.getText().trim());

                Document existing = products.find(Filters.eq("productId", id)).first();
                if (existing == null) {
                    JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                products.updateOne(
                        new Document("productId", id),
                        new Document("$set", new Document("price", price)
                                .append("quantity", qty))
                );
                JOptionPane.showMessageDialog(this, "Product Updated Successfully");

                loadProductsToTable();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            try {
                String id = tfId.getText().trim();

                Document existing = products.find(Filters.eq("productId", id)).first();
                if (existing == null) {
                    JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                products.deleteOne(new Document("productId", id));
                JOptionPane.showMessageDialog(this, "Product Deleted Successfully");

                loadProductsToTable();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // When user clicks a row in the table, fill the form with that data
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                int row = productTable.getSelectedRow();
                tfId.setText((String) tableModel.getValueAt(row, 0));
                tfName.setText((String) tableModel.getValueAt(row, 1));
                tfPrice.setText(tableModel.getValueAt(row, 2).toString());
                tfQty.setText(tableModel.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadProductsToTable() {
        tableModel.setRowCount(0); // Clear existing rows
        try (MongoCursor<Document> cursor = products.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Vector<Object> row = new Vector<>();
                row.add(doc.getString("productId"));
                row.add(doc.getString("name"));
                row.add(doc.getDouble("price"));
                row.add(doc.getInteger("quantity"));
                tableModel.addRow(row);
            }
        }
    }

    private void clearFields() {
        tfId.setText("");
        tfName.setText("");
        tfPrice.setText("");
        tfQty.setText("");
        productTable.clearSelection();
    }
}
