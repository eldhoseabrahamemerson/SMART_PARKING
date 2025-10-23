package Database;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class DatabaseManager {
    // Database credentials - centralized configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
     
    // Singleton instance
    private static DatabaseManager instance;
    private Connection connection;
    
    // Private constructor for singleton pattern
    private DatabaseManager() {
        connectToDatabase();
        createTables();
    }
    
    // Get singleton instance
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    // Establish database connection
    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First, connect without specifying database to create it if needed
            Connection tempConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
            Statement stmt = tempConnection.createStatement();
            
            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS parking");
            stmt.close();
            tempConnection.close();
            
            // Now connect to the parking database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            showError("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            showError("Database connection failed: " + e.getMessage());
        }
    }
    
    // Create all required tables
    private void createTables() {
        createEntriesTable();
    }
    
    // Create entries table for vehicle entry records
    private void createEntriesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS entries ("
            + "id INT AUTO_INCREMENT PRIMARY KEY,"
            + "vehicle_number VARCHAR(50) NOT NULL,"
            + "owner_name VARCHAR(100) NOT NULL,"
            + "phone_number VARCHAR(20),"
            + "vehicle_type VARCHAR(20) NOT NULL,"
            + "slot VARCHAR(10) NOT NULL,"
            + "entry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "exit_time TIMESTAMP NULL,"
            + "status VARCHAR(20) DEFAULT 'PARKED'"
            + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Entries table ready!");
            
            // Check and add missing columns if needed
            checkAndAddMissingColumns();
            
        } catch (SQLException e) {
            showError("Table creation failed: " + e.getMessage());
        }
    }
    
    // Check and add any missing columns
    private void checkAndAddMissingColumns() {
        try (Statement stmt = connection.createStatement()) {
            // Check if entry_time column exists
            try {
                String checkSql = "SELECT entry_time FROM entries LIMIT 1";
                stmt.executeQuery(checkSql);
                System.out.println("✓ entry_time column exists");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("Adding missing 'entry_time' column...");
                String alterSql = "ALTER TABLE entries ADD COLUMN entry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
                stmt.executeUpdate(alterSql);
                System.out.println("✓ entry_time column added successfully!");
            }
            
            // Check if exit_time column exists
            try {
                String checkSql = "SELECT exit_time FROM entries LIMIT 1";
                stmt.executeQuery(checkSql);
                System.out.println("✓ exit_time column exists");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("Adding missing 'exit_time' column...");
                String alterSql = "ALTER TABLE entries ADD COLUMN exit_time TIMESTAMP NULL";
                stmt.executeUpdate(alterSql);
                System.out.println("✓ exit_time column added successfully!");
            }
            
            // Check if status column exists
            try {
                String checkSql = "SELECT status FROM entries LIMIT 1";
                stmt.executeQuery(checkSql);
                System.out.println("✓ status column exists");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                System.out.println("Adding missing 'status' column...");
                String alterSql = "ALTER TABLE entries ADD COLUMN status VARCHAR(20) DEFAULT 'PARKED'";
                stmt.executeUpdate(alterSql);
                
                // Update existing records to have PARKED status
                String updateSql = "UPDATE entries SET status = 'PARKED' WHERE status IS NULL";
                stmt.executeUpdate(updateSql);
                System.out.println("✓ status column added successfully!");
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking/adding columns: " + e.getMessage());
        }
    }
    
    // Get database connection
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
        } catch (SQLException e) {
            showError("Connection check failed: " + e.getMessage());
        }
        return connection;
    }
    
    // Insert vehicle entry
    public boolean insertVehicleEntry(String vehicleNumber, String ownerName, 
                                     String phoneNumber, String vehicleType, String slot) {
        String sql = "INSERT INTO entries (vehicle_number, owner_name, phone_number, vehicle_type, slot) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try {
            // Check connection
            if (connection == null || connection.isClosed()) {
                System.err.println("Connection is closed, reconnecting...");
                connectToDatabase();
            }
            
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, vehicleNumber);
            pstmt.setString(2, ownerName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, vehicleType);
            pstmt.setString(5, slot);
            
            int result = pstmt.executeUpdate();
            pstmt.close();
            
            System.out.println("✓ Vehicle entry inserted successfully! Rows affected: " + result);
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Failed to insert vehicle entry: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to insert vehicle entry: " + e.getMessage());
            return false;
        }
    }
    
    // Get count of currently parked vehicles
    public int getParkedVehicleCount() {
        String sql = "SELECT COUNT(*) FROM entries WHERE status = 'PARKED'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            showError("Failed to get parked vehicle count: " + e.getMessage());
        }
        return 0;
    }
    
    // Get occupied slots
    public ResultSet getOccupiedSlots() {
        String sql = "SELECT slot FROM entries WHERE status = 'PARKED'";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            showError("Failed to get occupied slots: " + e.getMessage());
            return null;
        }
    }
    
    // Mark vehicle as exited
    public boolean markVehicleExit(String vehicleNumber) {
        String sql = "UPDATE entries SET status = 'EXITED', exit_time = CURRENT_TIMESTAMP "
                   + "WHERE vehicle_number = ? AND status = 'PARKED'";
        try {
            if (connection == null || connection.isClosed()) {
                connectToDatabase();
            }
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, vehicleNumber);
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            System.out.println("✓ Vehicle exit marked successfully! Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("✗ Failed to mark vehicle exit: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to mark vehicle exit: " + e.getMessage());
            return false;
        }
    }
    
    // Get vehicle details by vehicle number
    public ResultSet getVehicleByNumber(String vehicleNumber) {
        String sql = "SELECT * FROM entries WHERE vehicle_number = ? AND status = 'PARKED'";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, vehicleNumber);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            showError("Failed to get vehicle details: " + e.getMessage());
            return null;
        }
    }
    
    // Get all parked vehicles - FIXED: Handle missing entry_time column
    public ResultSet getAllParkedVehicles() {
        // First check if entry_time column exists
        if (!checkColumnExists("entry_time")) {
            // If entry_time doesn't exist, use simple query without ordering
            String sql = "SELECT * FROM entries WHERE status = 'PARKED'";
            try {
                Statement stmt = connection.createStatement();
                return stmt.executeQuery(sql);
            } catch (SQLException e) {
                showError("Failed to get parked vehicles: " + e.getMessage());
                return null;
            }
        } else {
            // If entry_time exists, use the ordered query
            String sql = "SELECT * FROM entries WHERE status = 'PARKED' ORDER BY entry_time DESC";
            try {
                Statement stmt = connection.createStatement();
                return stmt.executeQuery(sql);
            } catch (SQLException e) {
                showError("Failed to get parked vehicles: " + e.getMessage());
                return null;
            }
        }
    }
    
    // Check if a specific column exists in the table
    private boolean checkColumnExists(String columnName) {
        String sql = "SELECT " + columnName + " FROM entries LIMIT 1";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Check if slot is occupied
    public boolean isSlotOccupied(String slot) {
        String sql = "SELECT COUNT(*) FROM entries WHERE slot = ? AND status = 'PARKED'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, slot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            showError("Failed to check slot availability: " + e.getMessage());
        }
        return false;
    }
    
    // Get total slots (can be configured)
    public int getTotalSlots() {
        return 50; // This can be made dynamic by storing in database
    }
    
    // Get available slots count
    public int getAvailableSlots() {
        return getTotalSlots() - getParkedVehicleCount();
    }
    
    // Close database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed!");
            }
        } catch (SQLException e) {
            showError("Failed to close connection: " + e.getMessage());
        }
    }
    
    // Show error message
    private void showError(String message) {
        System.err.println("Database Error: " + message);
        // Show GUI error dialog
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    // Execute custom query (for advanced operations)
    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            showError("Query execution failed: " + e.getMessage());
            return null;
        }
    }
    
    // Execute custom update (for advanced operations)
    public boolean executeUpdate(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            showError("Update execution failed: " + e.getMessage());
            return false;
        }
    }
}