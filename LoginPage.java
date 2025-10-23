package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame implements ActionListener {

    // Components
    private Container container;
    private JLabel titleLabel, userLabel, passLabel, msgLabel;
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton, resetButton;

    public LoginPage() { 
        setTitle("Car Parking Management System - Login");
        setBounds(400, 150, 450, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        container = getContentPane();
        container.setLayout(null);
        // Change background to match dashboard green tint
        container.setBackground(new Color(204, 255, 204));

        // Title Label
        titleLabel = new JLabel("Car Parking Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(55, 20, 350, 30);
        container.add(titleLabel);

        // Username Label & Field
        userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setBounds(70, 80, 100, 25);
        container.add(userLabel);

        userField = new JTextField();
        userField.setBounds(170, 80, 200, 25);
        container.add(userField);

        // Password Label & Field
        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setBounds(70, 120, 100, 25);
        container.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(170, 120, 200, 25);
        container.add(passField);

        // Buttons
        loginButton = new JButton("Login");
        loginButton.setBounds(100, 180, 100, 30);
        loginButton.setBackground(new Color(100, 149, 237));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this);
        container.add(loginButton);

        resetButton = new JButton("Reset");
        resetButton.setBounds(230, 180, 100, 30);
        resetButton.setBackground(new Color(220, 20, 60));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(this);
        container.add(resetButton);

        // Message Label
        msgLabel = new JLabel("");
        msgLabel.setFont(new Font("Arial", Font.BOLD, 13));
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(120, 220, 250, 25);
        container.add(msgLabel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            // Dummy login validation
            if (user.equals("admin") && pass.equals("1234")) {
                msgLabel.setForeground(new Color(34, 139, 34));
                msgLabel.setText("Login Successful!");

                // Proceed to dashboard
                JOptionPane.showMessageDialog(this, "Welcome Admin!");
                dispose(); // Close login window
                new SmartParkingDashboard(); // Open dashboard
            } else {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("Invalid Username or Password!");
            }
        }

        if (e.getSource() == resetButton) {
            userField.setText("");
            passField.setText("");
            msgLabel.setText("");
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}

