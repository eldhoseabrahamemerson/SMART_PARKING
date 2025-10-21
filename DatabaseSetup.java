package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseSetup {
    
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "smart_parking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    public static void main(String[] args) {
        createDatabaseAndTables();
    }
    
    public static void createDatabaseAndTables() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL (without specifying a database)
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();
            
            // Create Database
            String createDBSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(createDBSQL);
            System.out.println(" Database created successfully: " + DB_NAME);
            
            // Use the database
            stmt.execute("USE " + DB_NAME);
            
            // Create Users Table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    role ENUM('admin', 'operator') DEFAULT 'operator',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_active BOOLEAN DEFAULT TRUE
                )
                """;
            stmt.executeUpdate(createUsersTable);
            System.out.println(" Users table created successfully");
            
            // Create Parking Slots Table
            String createSlotsTable = """
                CREATE TABLE IF NOT EXISTS parking_slots (
                    slot_id INT AUTO_INCREMENT PRIMARY KEY,
                    slot_number VARCHAR(10) UNIQUE NOT NULL,
                    slot_type ENUM('car', 'bike', 'disabled') DEFAULT 'car',
                    floor_level INT DEFAULT 1,
                    is_occupied BOOLEAN DEFAULT FALSE,
                    is_reserved BOOLEAN DEFAULT FALSE,
                    hourly_rate DECIMAL(8,2) NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE
                )
                """;
            stmt.executeUpdate(createSlotsTable);
            System.out.println(" Parking slots table created successfully");
            
            // Create Vehicles Table
            String createVehiclesTable = """
                CREATE TABLE IF NOT EXISTS vehicles (
                    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
                    license_plate VARCHAR(20) UNIQUE NOT NULL,
                    vehicle_type ENUM('car', 'bike', 'other') DEFAULT 'car',
                    vehicle_brand VARCHAR(50),
                    vehicle_color VARCHAR(30),
                    owner_name VARCHAR(100),
                    owner_phone VARCHAR(15)
                )
                """;
            stmt.executeUpdate(createVehiclesTable);
            System.out.println(" Vehicles table created successfully");
            
            // Create Parking Records Table
            String createRecordsTable = """
                CREATE TABLE IF NOT EXISTS parking_records (
                    record_id INT AUTO_INCREMENT PRIMARY KEY,
                    vehicle_id INT,
                    slot_id INT,
                    entry_time DATETIME NOT NULL,
                    exit_time DATETIME NULL,
                    entry_operator_id INT,
                    exit_operator_id INT NULL,
                    total_amount DECIMAL(10,2) DEFAULT 0,
                    payment_status ENUM('pending', 'paid', 'free') DEFAULT 'pending',
                    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
                    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id),
                    FOREIGN KEY (entry_operator_id) REFERENCES users(user_id),
                    FOREIGN KEY (exit_operator_id) REFERENCES users(user_id)
                )
                """;
            stmt.executeUpdate(createRecordsTable);
            System.out.println(" Parking records table created successfully");
            
            // Create Payments Table
            String createPaymentsTable = """
                CREATE TABLE IF NOT EXISTS payments (
                    payment_id INT AUTO_INCREMENT PRIMARY KEY,
                    record_id INT,
                    amount DECIMAL(10,2) NOT NULL,
                    payment_method ENUM('cash', 'card', 'digital') DEFAULT 'cash',
                    payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    received_by INT,
                    FOREIGN KEY (record_id) REFERENCES parking_records(record_id),
                    FOREIGN KEY (received_by) REFERENCES users(user_id)
                )
                """;
            stmt.executeUpdate(createPaymentsTable);
            System.out.println(" Payments table created successfully");
            
            System.out.println("✅ Database setup completed successfully (no sample data inserted).");
            
        } catch (Exception e) {
            System.out.println("❌ Error during database setup: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
