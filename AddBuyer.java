import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddBuyer extends JFrame {

    private JTextField nameField, contactField, emailField, addressField;
    private JComboBox<String> genderComboBox;
    
    public AddBuyer() {
        setTitle("Add New Buyer");
        setSize(450, 400);
        setLayout(null); // Manual layout
        setLocationRelativeTo(null);
        
        // --- UI Setup ---
        JLabel title = new JLabel("Add New Buyer Record");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(100, 20, 250, 30);
        add(title);

        // Labels and Fields Positioning
        int y = 70;
        int height = 30;

        add(new JLabel("Name:")).setBounds(50, y, 100, height);
        nameField = new JTextField();
        nameField.setBounds(150, y, 250, height);
        add(nameField);
        
        y += 40;
        add(new JLabel("Contact No:")).setBounds(50, y, 100, height);
        contactField = new JTextField();
        contactField.setBounds(150, y, 250, height);
        add(contactField);

        y += 40;
        add(new JLabel("Email:")).setBounds(50, y, 100, height);
        emailField = new JTextField();
        emailField.setBounds(150, y, 250, height);
        add(emailField);

        y += 40;
        add(new JLabel("Address:")).setBounds(50, y, 100, height);
        addressField = new JTextField();
        addressField.setBounds(150, y, 250, height);
        add(addressField);

        y += 40;
        add(new JLabel("Gender:")).setBounds(50, y, 100, height);
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderComboBox.setBounds(150, y, 250, height);
        add(genderComboBox);
        
        // Buttons
        JButton saveButton = new JButton("Save");
        JButton closeButton = new JButton("Close");
        saveButton.setBounds(150, y + 60, 100, 30);
        closeButton.setBounds(260, y + 60, 100, 30);
        add(saveButton);
        add(closeButton);

        // --- Save Button Logic (SQL INSERT) ---
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String contactNumber = contactField.getText();
            String email = emailField.getText();
            String address = addressField.getText();
            String gender = (String) genderComboBox.getSelectedItem();

            Connection con = DatabaseConnection.getConnection();
            if (con != null) {
                try {
                    Statement st = con.createStatement();
                    String query = "INSERT INTO buyer (name, contactNumber, email, address, gender) VALUES ('" + 
                                   name + "', '" + contactNumber + "', '" + email + "', '" + address + "', '" + gender + "')";
                    
                    st.executeUpdate(query);
                    
                    JOptionPane.showMessageDialog(this, "Buyer Record Saved Successfully!");
                    
                    // Clear/Refresh the form 
                    this.dispose();
                    new AddBuyer().setVisible(true); 
                    
                    con.close();
                } catch (SQLException ex) {
                    // Check for duplicate contact number error 
                    JOptionPane.showMessageDialog(this, "Error: Contact Number may already exist or DB failure. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Close Button Logic
        closeButton.addActionListener(e -> dispose());

        setVisible(true);
    }
}