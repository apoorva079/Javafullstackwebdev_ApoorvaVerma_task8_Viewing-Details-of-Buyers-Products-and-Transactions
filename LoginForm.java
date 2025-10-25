import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class LoginForm extends JFrame {
    
    // Components
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JCheckBox showPassword = new JCheckBox("Show Password"); 
    
    private Image backgroundImage;
    private final String IMAGE_PATH = "images/images.jpeg"; 
    
    
    private final String CORRECT_USER = "Admin";
    private final String CORRECT_PASS = "12345";

    public LoginForm() {
        setTitle("Login Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300); 
        setLocationRelativeTo(null); 
        
        // --- 1. Background Image Setup ---
        try {
            backgroundImage = ImageIO.read(new File(IMAGE_PATH)); 
        } catch (IOException e) {
            System.err.println("Login background image not found at: " + IMAGE_PATH);
        }

        // Custom JPanel for background
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); 
                }
            }
        };
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
        // --- 2. UI Components (Placed on contentPane) ---
        
        JLabel titleLabel = new JLabel("Login Page");
        titleLabel.setBounds(100, 20, 200, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.YELLOW); // Make text visible
        contentPane.add(titleLabel);

        // Labels
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 70, 80, 25);
        userLabel.setForeground(Color.WHITE);
        contentPane.add(userLabel);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 110, 80, 25);
        passLabel.setForeground(Color.WHITE);
        contentPane.add(passLabel);

        // Fields
        usernameField.setBounds(140, 70, 200, 25); 
        contentPane.add(usernameField);
        passwordField.setBounds(140, 110, 200, 25); 
        contentPane.add(passwordField);

        // Checkbox
        showPassword.setBounds(140, 140, 150, 25);
        showPassword.setForeground(Color.WHITE);
        showPassword.setOpaque(false); 
        contentPane.add(showPassword);
        
        // Buttons
        JButton loginButton = new JButton("Login");
        JButton closeButton = new JButton("Close");
        loginButton.setBounds(140, 190, 90, 30);
        closeButton.setBounds(250, 190, 90, 30);
        contentPane.add(loginButton);
        contentPane.add(closeButton);

        // --- 3. Logic ---
        
        // A. Login Button Logic
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (usernameField.getText().equals(CORRECT_USER) && new String(passwordField.getPassword()).equals(CORRECT_PASS)) { 
                    dispose(); 
                    new Homepage().setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Incorrect Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // B. Show Password Logic
        showPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPassword.isSelected()) {
                    passwordField.setEchoChar((char) 0); 
                } else {
                    passwordField.setEchoChar('*'); 
                }
            }
        });

        // C. Close Button Logic
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int a = JOptionPane.showConfirmDialog(LoginForm.this, "Do you really want to close the application?", "Confirm Close", JOptionPane.YES_NO_OPTION);
                if (a == JOptionPane.YES_OPTION) { 
                    System.exit(0); 
                }
            }
        });
        
        setVisible(true); 
    }
}