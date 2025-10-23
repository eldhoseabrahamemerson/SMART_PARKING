/*package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SmartParkingDashboard extends JFrame implements ActionListener {
    // Labels to display parking information
    JLabel totalSlotLbl, occupiedLbl, availableLbl, currentLbl;
    // Buttons for vehicle entry and exit
    JButton entryBtn, exitBtn;

    public SmartParkingDashboard() {
        setTitle("Vehicle Parking Dashboard"); // Set window title
        setSize(700, 500); // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app on window close
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout()); // Use BorderLayout for main layout

        getContentPane().setBackground(new Color(204, 255, 204)); // Set background color

        // Create and style the dashboard title
        JLabel title = new JLabel("VEHICLE PARKING DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 102));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add spacing around title
        add(title, BorderLayout.NORTH); // Add title to top of window

        // Panel to hold the info blocks in a grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(new Color(204, 255, 204));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40)); // Padding around grid

        // Create info blocks for Total, Occupied, Available, Current
        totalSlotLbl = createInfoBlock("Total Slots", "50");
        occupiedLbl = createInfoBlock("Occupied", "30");
        availableLbl = createInfoBlock("Available", "20");
        currentLbl = createInfoBlock("Current Vehicles", "30");

        // Add info blocks to grid panel
        gridPanel.add(totalSlotLbl);
        gridPanel.add(occupiedLbl);
        gridPanel.add(availableLbl);
        gridPanel.add(currentLbl);

        add(gridPanel, BorderLayout.CENTER); // Add grid panel to center of window

        // Bottom panel to hold chart and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Custom panel to draw bar chart
        JPanel graphPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int[] values = {50, 30, 20, 30}; // Data values for chart
                Color[] colors = {Color.GRAY, Color.RED, Color.GREEN, Color.BLUE}; // Bar colors
                String[] labels = {"Total", "Occupied", "Available", "Current"}; // Bar labels
                int width = 80; // Width of each bar
                int gap = 30; // Gap between bars

                // Draw each bar with corresponding label
                for (int i = 0; i < values.length; i++) {
                    g.setColor(colors[i]);
                    int height = values[i] * 2; // Scale height
                    g.fillRect(50 + i * (width + gap), getHeight() - height - 20, width, height);
                    g.setColor(Color.BLACK);
                    g.drawString(labels[i], 50 + i * (width + gap) + 10, getHeight() - 5);
                }
            }
        };
        graphPanel.setPreferredSize(new Dimension(600, 150));
        graphPanel.setBackground(Color.WHITE);
        bottomPanel.add(graphPanel, BorderLayout.CENTER); // Add chart to bottom panel

        // Entry button styling
        entryBtn = new JButton("Entry");
        entryBtn.setFont(new Font("Arial", Font.BOLD, 16));
        entryBtn.setBackground(new Color(0, 102, 204));
        entryBtn.setForeground(Color.WHITE);
        entryBtn.setFocusPainted(false);
        entryBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        entryBtn.addActionListener(this); // Handle click event

        // Exit button styling
        exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        exitBtn.setBackground(new Color(204, 0, 0));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        exitBtn.addActionListener(this); // Handle click event

        // Panel to hold the buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(entryBtn);
        btnPanel.add(exitBtn);

        bottomPanel.add(btnPanel, BorderLayout.SOUTH); // Add buttons to bottom of bottom panel
        add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to main frame

        setVisible(true); // Make window visible
    }

    // Method to create a styled info block (JLabel)
    private JLabel createInfoBlock(String title, String value) {
        JLabel label = new JLabel("<html><center>" + title + "<br><b>" + value + "</b></center></html>", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2)); // Border around label
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == entryBtn) {
            // Open Vehicle Entry Form
            dispose(); // Close dashboard
            new VehicleEntryForm().setVisible(true); // Open entry form
        } else if (e.getSource() == exitBtn) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Exit application
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SmartParkingDashboard(); // Launch dashboard
        });
    }
}*/
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Database.DatabaseManager;

public class SmartParkingDashboard extends JFrame implements ActionListener {
    // Labels to display parking information
    JLabel totalSlotLbl, occupiedLbl, availableLbl, currentLbl;
    // Buttons for vehicle entry and exit
    JButton entryBtn, exitBtn;
    private DatabaseManager dbManager;
    private Timer refreshTimer;

    public SmartParkingDashboard() {
        dbManager = DatabaseManager.getInstance();
        
        setTitle("Vehicle Parking Dashboard"); // Set window title
        setSize(700, 500); // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app on window close
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout()); // Use BorderLayout for main layout

        getContentPane().setBackground(new Color(204, 255, 204)); // Set background color

        // Create and style the dashboard title
        JLabel title = new JLabel("VEHICLE PARKING DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 51, 102));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add spacing around title
        add(title, BorderLayout.NORTH); // Add title to top of window

        // Panel to hold the info blocks in a grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(new Color(204, 255, 204));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40)); // Padding around grid

        // Create info blocks for Total, Occupied, Available, Current
        totalSlotLbl = createInfoBlock("Total Slots", "50");
        occupiedLbl = createInfoBlock("Occupied", "0");
        availableLbl = createInfoBlock("Available", "50");
        currentLbl = createInfoBlock("Current Vehicles", "0");

        // Add info blocks to grid panel
        gridPanel.add(totalSlotLbl);
        gridPanel.add(occupiedLbl);
        gridPanel.add(availableLbl);
        gridPanel.add(currentLbl);

        add(gridPanel, BorderLayout.CENTER); // Add grid panel to center of window

        // Bottom panel to hold chart and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Custom panel to draw bar chart
        JPanel graphPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Get real-time data for the chart
                int totalSlots = dbManager.getTotalSlots();
                int occupied = dbManager.getParkedVehicleCount();
                int available = dbManager.getAvailableSlots();
                
                int[] values = {totalSlots, occupied, available, occupied}; // Data values for chart
                Color[] colors = {Color.GRAY, Color.RED, Color.GREEN, Color.BLUE}; // Bar colors
                String[] labels = {"Total", "Occupied", "Available", "Current"}; // Bar labels
                int width = 80; // Width of each bar
                int gap = 30; // Gap between bars

                // Draw each bar with corresponding label
                for (int i = 0; i < values.length; i++) {
                    g.setColor(colors[i]);
                    int height = Math.min(values[i] * 2, 120); // Scale height with max limit
                    g.fillRect(50 + i * (width + gap), getHeight() - height - 20, width, height);
                    g.setColor(Color.BLACK);
                    g.drawString(labels[i], 50 + i * (width + gap) + 10, getHeight() - 5);
                    // Display value on top of bar
                    g.drawString(String.valueOf(values[i]), 50 + i * (width + gap) + 30, getHeight() - height - 25);
                }
            }
        };
        graphPanel.setPreferredSize(new Dimension(600, 150));
        graphPanel.setBackground(Color.WHITE);
        bottomPanel.add(graphPanel, BorderLayout.CENTER); // Add chart to bottom panel

        // Entry button styling 
        entryBtn = new JButton("Vehicle Entry");
        entryBtn.setFont(new Font("Arial", Font.BOLD, 16));
        entryBtn.setBackground(new Color(0, 102, 204));
        entryBtn.setForeground(Color.WHITE);
        entryBtn.setFocusPainted(false);
        entryBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        entryBtn.addActionListener(this); // Handle click event

        // Exit button styling - CHANGED: Now opens Vehicle Exit form
        exitBtn = new JButton("Vehicle Exit");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 16));
        exitBtn.setBackground(new Color(204, 0, 0));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        exitBtn.addActionListener(this); // Handle click event

        // Panel to hold the buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(entryBtn);
        btnPanel.add(exitBtn);

        bottomPanel.add(btnPanel, BorderLayout.SOUTH); // Add buttons to bottom of bottom panel
        add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to main frame

        // Initialize with real data
        updateDashboardData();
        
        // Set up auto-refresh timer (every 5 seconds)
        refreshTimer = new Timer(5000, e -> updateDashboardData());
        refreshTimer.start();

        setVisible(true); // Make window visible
    }

    // Method to create a styled info block (JLabel)
    private JLabel createInfoBlock(String title, String value) {
        JLabel label = new JLabel("<html><center>" + title + "<br><b>" + value + "</b></center></html>", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2)); // Border around label
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    // NEW METHOD: Update dashboard with real-time data from database
    private void updateDashboardData() {
        try {
            int totalSlots = dbManager.getTotalSlots();
            int occupied = dbManager.getParkedVehicleCount();
            int available = dbManager.getAvailableSlots();
            
            // Update the labels with real data
            totalSlotLbl.setText("<html><center>Total Slots<br><b>" + totalSlots + "</b></center></html>");
            occupiedLbl.setText("<html><center>Occupied<br><b>" + occupied + "</b></center></html>");
            availableLbl.setText("<html><center>Available<br><b>" + available + "</b></center></html>");
            currentLbl.setText("<html><center>Current Vehicles<br><b>" + occupied + "</b></center></html>");
            
            // Repaint the graph to show updated data
            repaint();
            
        } catch (Exception e) {
            System.err.println("Error updating dashboard data: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == entryBtn) {
            // Open Vehicle Entry Form
            dispose(); // Close dashboard
            new VehicleEntryForm().setVisible(true); // Open entry form
        } else if (e.getSource() == exitBtn) {
            // CHANGED: Open Vehicle Exit form instead of closing application
            dispose(); // Close dashboard
            new VehicleExit().setVisible(true); // Open exit form
        }
    }

    // Override dispose to stop the timer when window closes
    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SmartParkingDashboard(); // Launch dashboard
        });
    }
}