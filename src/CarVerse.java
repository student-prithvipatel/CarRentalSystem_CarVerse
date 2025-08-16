import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.*;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;

import java.util.Properties;

public class CarVerse {
    static Scanner sc = new Scanner(System.in);
    static Admin admin = new Admin();
    static Customer customer = new Customer();

    public static void main(String[] args) throws SQLException {
        int choice;
        do {
            System.out.println("1. Admin login");
            System.out.println("2. User login");
            System.out.println("3. User registration");
            System.out.println("4. Exit");
            System.out.println("Enter choice from 1 to 4");
            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    admin.adminLogin();
                    break;
                case 2:
                    customer.customerLogin();
                    break;
                case 3:
                    customer.customerRegistartion();
                    break;
                case 4:
                    System.out.println("Good Bye>>>");
            }
        } while (choice != 4);
    }

    static void adminMenu() throws SQLException {
        int choice;
        do {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Car");
            System.out.println("2. View All Cars");
            System.out.println("3. Update Car Details");
            System.out.println("4. View Available Cars");
            System.out.println("5. Update Car Availability");
            System.out.println("6. Currently Rented Cars");
            System.out.println("7. View Overdue Rentals");
            System.out.println("8. Generate Reports");
            System.out.println("9. Remove Car");
            System.out.println("10. View all customers ");
            System.out.println("11. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    admin.addCar();
                    break;
                case 2:
                    admin.viewAllCars();
                    break;
                case 3:
                    admin.updateCarDetails();
                    break;
                case 4:
                    admin.viewAvailableCars();
                    break;
                case 5:
                    admin.updateCarAvailability();
                    break;
                case 6:
                    admin.viewCurrentlyRentedCars();
                    break;
                case 7:
                    admin.viewOverdueRentals();
                    break;
                case 8:
                    admin.generateReports();
                    break;
                case 9:
                    admin.removeCar();
                    break;
                case 10:
                    admin.viewAllCustomer();
                    break;
                case 11:
                    System.out.println("Admin logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 10);
    }

    static void customerMenu() throws SQLException {
        int choice;
        do {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. Search Available Cars");
            System.out.println("2. Book a Car");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Return Car");
            System.out.println("6. Rate a Car");
            System.out.println("7. Logout");
            System.out.println("8. Update your profile details");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    admin.viewAvailableCars();
                    break;
                case 2:
                    System.out.println("****Available car list*****");
                    admin.viewAvailableCars();
                    customer.bookCar();
                    break;
                case 3:
                    customer.viewMyBookings();
                    break;
                case 4:
                    customer.cancelBooking();
                    break;
                case 5:
                    customer.returnCar();
                    break;
                case 6:
                    customer.giveRating();
                    break;
                case 7:
                    System.out.println("Logged out.");
                    break;
                case 8:
                    customer.updateProfile();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }
}

class DBConnect {
    static final String URL = "jdbc:mysql://localhost:3306/carrental"; // replace with your DB name
    static final String USER = "root";       // replace with your DB username
    static final String PASSWORD = "";   // replace with your DB password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

class Car {
    int car_id;
    String model;
    String brand;
    String type;
    double pricePerHour;
    boolean availability;
    int seats;

    public Car(int car_id, String model, String brand, String type, double pricePerHour, boolean availability, int seats) {
        this.car_id = car_id;
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.pricePerHour = pricePerHour;
        this.availability = availability;
        this.seats = seats;
    }
}

class Customer {
    Scanner sc = new Scanner(System.in);
    static int customer_Id;
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private LocalDate dob;
    private String password;

    void customerRegistartion() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter name: ");
            String name = sc.nextLine();
            while (true) {
                System.out.print("Enter email (must be lowercase and contain '@'): ");
                email = sc.nextLine().toLowerCase();
                if (email.contains("@") && email.equals(email.toLowerCase()) && email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                    break;
                } else {
                    System.out.println("❌ Invalid email. Try again.");
                }
            }
            while (true) {
                System.out.print("Enter password (min 8 chars, at least 1 special character): ");
                password = sc.nextLine();
                boolean isLengthValid = password.length() >= 8;
                boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");
                if (!isLengthValid || !hasSpecialChar) {
                    System.out.println("❌ Password must be at least 8 characters and contain at least one special character.");
                    continue;
                }
                System.out.print("Confirm password: ");
                String confirmPassword = sc.nextLine();
                if (password.equals(confirmPassword)) {
                    break;
                } else {
                    System.out.println("❌ Passwords do not match. Please try again.");
                }
            }
            while (true) {
                System.out.print("Enter phone number (not starting with 0): ");
                phoneNo = sc.nextLine();
                if (phoneNo.matches("^[1-9][0-9]{9}$")) {
                    break;
                } else {
                    System.out.println("❌ Invalid phone number. Try again.");
                }
            }
            System.out.print("Enter address: ");
            String address = sc.nextLine();
            while (true) {
                System.out.print("Enter date of birth (dd-MM-yyyy): ");
                String dobInput = sc.nextLine();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    dob = LocalDate.parse(dobInput, formatter);

                    LocalDate today = LocalDate.now();
                    int age = Period.between(dob, today).getYears();

                    if (age < 18) {
                        System.out.println("❌ You must be at least 18 years old to register.");
                        continue;
                    }
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Invalid date format. Please use dd-MM-yyyy.");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO customer(name, email, phone_no, address, dob, password) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phoneNo);
                ps.setString(4, address);
                ps.setDate(5, Date.valueOf(dob));
                ps.setString(6, password);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int id = rs.getInt(1);
                            Customer.customer_Id = id;
                            System.out.println("✅ Registration successful! Your Customer ID is: " + id);
                        }
                    }
                }
                this.name = name;
                this.email = email;
                this.phoneNo = phoneNo;
                this.address = address;
                this.dob = dob;
                this.password = password;

            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    public void customerLogin() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.println("===== Customer Login =====");
            System.out.println("1. Login using Email and Password");
            System.out.println("2. Login using Phone Number and Password");
            System.out.print("Choose an option (1 or 2): ");
            int choice = sc.nextInt();
            sc.nextLine();

            PreparedStatement ps;
            ResultSet rs;
            String input;
            String password;

            switch (choice) {
                case 1: {
                    System.out.print("Enter email: ");
                    input = sc.nextLine().toLowerCase();

                    String query = "SELECT * FROM customer WHERE email = ?";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, input);
                    rs = ps.executeQuery();

                    if (!rs.next()) {
                        System.out.println("❌ No account found with this email. Please register first.");
                    } else {
                        System.out.print("Enter password: ");
                        password = sc.nextLine();

                        String dbPassword = rs.getString("password");
                        if (password.equals(dbPassword)) {
                            Customer.customer_Id = rs.getInt("customer_id");

                            // ✅ Load all profile details into object
                            this.name = rs.getString("name");
                            this.email = rs.getString("email");
                            this.phoneNo = rs.getString("phone_no");
                            this.address = rs.getString("address");

                            java.sql.Date sqlDob = rs.getDate("dob");
                            this.dob = (sqlDob != null) ? sqlDob.toLocalDate() : null;

                            this.password = dbPassword;

                            System.out.println("✅ Login successful! Welcome, " + name + "!");
                            CarVerse.customerMenu();
                        } else {
                            System.out.println("❌ Incorrect password.");
                        }
                    }
                    break;
                }
                case 2: {
                    System.out.print("Enter phone number: ");
                    input = sc.nextLine();

                    String query = "SELECT * FROM customer WHERE phone_no = ?";
                    ps = conn.prepareStatement(query);
                    ps.setString(1, input);
                    rs = ps.executeQuery();

                    if (!rs.next()) {
                        System.out.println("❌ No account found with this phone number. Please register first.");
                    } else {
                        System.out.print("Enter password: ");
                        password = sc.nextLine();

                        String dbPassword = rs.getString("password");
                        if (password.equals(dbPassword)) {
                            Customer.customer_Id = rs.getInt("customer_id");

                            // ✅ Load all profile details into object
                            this.name = rs.getString("name");
                            this.email = rs.getString("email");
                            this.phoneNo = rs.getString("phone_no");
                            this.address = rs.getString("address");

                            java.sql.Date sqlDob = rs.getDate("dob");
                            this.dob = (sqlDob != null) ? sqlDob.toLocalDate() : null;

                            this.password = dbPassword;

                            System.out.println("✅ Login successful! Welcome, " + name + "!");
                            CarVerse.customerMenu();
                        } else {
                            System.out.println("❌ Incorrect password.");
                        }
                    }
                    break;
                }

                default:
                    System.out.println("❌ Invalid choice. Please select 1 or 2.");
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    public void updateProfile() {

        try (Connection conn = DBConnect.getConnection()) {
            System.out.println("\n=== Update Profile ===");
            System.out.println("Leave blank if you don’t want to update a field.");

            System.out.print("Enter new name (current: " + this.name + "): ");
            String newName = sc.nextLine();
            if (!newName.trim().isEmpty()) name = newName;

            System.out.print("Enter new email (current: " + this.email + "): ");
            String newEmail = sc.nextLine();
            if (!newEmail.trim().isEmpty()) email = newEmail;

            System.out.print("Enter new phone number (current: " + this.phoneNo + "): ");
            String newPhone = sc.nextLine();
            if (!newPhone.trim().isEmpty()) phoneNo = newPhone;

            System.out.print("Enter new address (current: " + this.address + "): ");
            String newAddress = sc.nextLine();
            if (!newAddress.trim().isEmpty()) address = newAddress;

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            System.out.print("Enter new DOB (yyyy-MM-dd) (current: " + (this.dob != null ? dob : "N/A") + "): ");
            String newDob = sc.nextLine();
            if (!newDob.trim().isEmpty()) {
                this.dob = LocalDate.parse(newDob, fmt);
            }
            System.out.print("Enter new password (leave blank to keep current): ");
            String newPassword = sc.nextLine();
            if (!newPassword.trim().isEmpty()) password = newPassword;

            // Step 2: Update in DB
            String query = "UPDATE customer SET name=?, email=?, phone_no=?, address=?, dob=?, password=? WHERE customer_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phoneNo);
            ps.setString(4, address);
            ps.setDate(5, java.sql.Date.valueOf(this.dob));
            ps.setString(6, password);
            ps.setInt(7, Customer.customer_Id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Profile updated successfully!");
            } else {
                System.out.println("❌ Failed to update profile.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error updating profile: " + e.getMessage());
        }
    }

    void bookCar() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to book: ");
            int carId = sc.nextInt();
            sc.nextLine();

            // 1. Check if car exists and is available
            String checkCar = "SELECT price_per_hour, availability FROM car WHERE car_id = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkCar)) {
                psCheck.setInt(1, carId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next() || !rs.getBoolean("availability")) {
                        System.out.println("❌ Car not available or doesn't exist.");
                        return;
                    }

                    double pricePerHour = rs.getDouble("price_per_hour");

                    // 2. Get trip details
                    System.out.print("Enter Start Location: ");
                    String startLoc = sc.nextLine();

                    System.out.print("Enter End Location: ");
                    String endLoc = sc.nextLine();

                    System.out.print("Enter Start Date & Time (yyyy-MM-dd HH:mm): ");
                    String startInput = sc.nextLine();

                    System.out.print("Enter End Date & Time (yyyy-MM-dd HH:mm): ");
                    String endInput = sc.nextLine();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                    try {
                        LocalDateTime startDateTime = LocalDateTime.parse(startInput, formatter);
                        LocalDateTime endDateTime = LocalDateTime.parse(endInput, formatter);
                        LocalDateTime now = LocalDateTime.now();

                        if (endDateTime.isBefore(startDateTime)) {
                            System.out.println("❌ End time must be after start time.");
                            return;
                        }

                        if (startDateTime.isBefore(now)) {
                            System.out.println("❌ Cannot book a car in the past. Please enter future time.");
                            return;
                        }

                        if (Duration.between(startDateTime, endDateTime).toHours() == 0) {
                            System.out.println("❌ Booking duration must be at least 1 hour.");
                            return;
                        }

                        double lateFeePerHour = 250.0;

                        // 3. Cost calculation
                        Map<String, Double> costData = Rental.costCalculator(startDateTime, endDateTime, endDateTime, pricePerHour, lateFeePerHour);
                        double hours = costData.get("totalHours");
                        double totalCost = costData.get("totalCost");

                        // 4. Insert into bookings table
                        String insertBooking = "INSERT INTO bookings (customer_id, car_id, start_location, end_location, start_datetime, end_datetime, total_hours, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement psInsert = conn.prepareStatement(insertBooking)) {
                            psInsert.setInt(1, customer_Id);
                            psInsert.setInt(2, carId);
                            psInsert.setString(3, startLoc);
                            psInsert.setString(4, endLoc);
                            psInsert.setTimestamp(5, Timestamp.valueOf(startDateTime));
                            psInsert.setTimestamp(6, Timestamp.valueOf(endDateTime));
                            psInsert.setDouble(7, hours);
                            psInsert.setDouble(8, totalCost);

                            int rows = psInsert.executeUpdate();
                            if (rows > 0) {
                                String updateAvailability = "UPDATE car SET availability = 0 WHERE car_id = ?";
                                try (PreparedStatement psUpdate = conn.prepareStatement(updateAvailability)) {
                                    psUpdate.setInt(1, carId);
                                    psUpdate.executeUpdate();
                                }
                                System.out.println("✅ Car booked successfully!");
                                System.out.println("⏱ Duration: " + hours + " hours");
                                System.out.println("💰 Total cost: ₹" + totalCost);
                            } else {
                                System.out.println("❌ Booking failed.");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Invalid date format or booking error: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void viewMyBookings() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {

            String query = """
                    SELECT b.booking_id, c.model, c.brand, c.type,
                           b.start_location, b.end_location,
                           b.start_datetime, b.end_datetime,
                           b.total_hours, b.total_cost, b.status
                    FROM bookings b
                    JOIN car c ON b.car_id = c.car_id
                    WHERE b.customer_id = ?
                    ORDER BY b.start_datetime DESC
                    """;

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, customer_Id);
                try (ResultSet rs = ps.executeQuery()) {

                    System.out.println("\n===============================");
                    System.out.println("📋 Your Bookings");
                    System.out.println("===============================");
                    System.out.printf("%-6s %-10s %-10s %-10s %-12s %-12s %-16s %-16s %-7s %-10s %-10s\n", "BID", "Model", "Brand", "Type", "From", "To", "Start Date", "End Date", "Hours", "Cost", "Status");
                    System.out.println("---------------------------------------------------------------------------------------------------------------");

                    boolean hasBookings = false;

                    while (rs.next()) {
                        hasBookings = true;
                        int bid = rs.getInt("booking_id");
                        String model = rs.getString("model");
                        String brand = rs.getString("brand");
                        String type = rs.getString("type");
                        String startLoc = rs.getString("start_location");
                        String endLoc = rs.getString("end_location");
                        Timestamp startDT = rs.getTimestamp("start_datetime");
                        Timestamp endDT = rs.getTimestamp("end_datetime");
                        double hours = rs.getDouble("total_hours");
                        double cost = rs.getDouble("total_cost");
                        String status = rs.getString("status");

                        System.out.printf("%-6d %-10s %-10s %-10s %-12s %-12s %-16s %-16s %-7.1f ₹%-9.2f %-10s\n", bid, model, brand, type, startLoc, endLoc, startDT.toLocalDateTime(), endDT.toLocalDateTime(), hours, cost, status);
                    }

                    if (!hasBookings) {
                        System.out.println("ℹ️ No bookings found.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void cancelBooking() {
        try (Connection conn = DBConnect.getConnection()) {
            // 1. List active bookings for this customer
            String listBookingsSql = "SELECT booking_id, car_id, start_datetime, end_datetime FROM bookings WHERE customer_id = ?";

            try (PreparedStatement psList = conn.prepareStatement(listBookingsSql)) {
                psList.setInt(1, customer_Id);
                try (ResultSet rs = psList.executeQuery()) {
                    boolean hasBookings = false;
                    System.out.println("\nYour Bookings:");
                    System.out.printf("%-10s %-10s %-20s %-20s\n", "BookingID", "CarID", "Start Date", "End Date");
                    while (rs.next()) {
                        hasBookings = true;
                        int bookingId = rs.getInt("booking_id");
                        int carId = rs.getInt("car_id");
                        Timestamp start = rs.getTimestamp("start_datetime");
                        Timestamp end = rs.getTimestamp("end_datetime");

                        System.out.printf("%-10d %-10d %-20s %-20s\n", bookingId, carId, start.toString(), end.toString());
                    }

                    if (!hasBookings) {
                        System.out.println("No bookings found to cancel.");
                        return;
                    }
                }
            }

            // 2. Prompt for booking ID to cancel
            System.out.print("\nEnter Booking ID to cancel or 0 to abort: ");
            int bookingIdToCancel = sc.nextInt();
            sc.nextLine();

            if (bookingIdToCancel == 0) {
                System.out.println("Cancellation aborted.");
                return;
            }

            // 3. Retrieve car_id for the booking to cancel and verify ownership
            String getCarIdSql = "SELECT car_id FROM bookings WHERE booking_id = ? AND customer_id = ?";
            int carId = -1;

            try (PreparedStatement psGetCar = conn.prepareStatement(getCarIdSql)) {
                psGetCar.setInt(1, bookingIdToCancel);
                psGetCar.setInt(2, customer_Id);
                try (ResultSet rs = psGetCar.executeQuery()) {
                    if (rs.next()) {
                        carId = rs.getInt("car_id");
                    } else {
                        System.out.println("Invalid Booking ID or you do not have permission to cancel this booking.");
                        return;
                    }
                }
            }

            // 4. Delete the booking record
            String cancelSql = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?";

            try (PreparedStatement psDelete = conn.prepareStatement(cancelSql)) {
                psDelete.setInt(1, bookingIdToCancel);
                int rowsDeleted = psDelete.executeUpdate();
                if (rowsDeleted == 0) {
                    System.out.println("Cancellation failed. Booking not found.");
                    return;
                }
            }

            // 5. Update car availability to true (available)
            String updateCarSql = "UPDATE car SET availability = TRUE WHERE car_id = ?";

            try (PreparedStatement psCarUpdate = conn.prepareStatement(updateCarSql)) {
                psCarUpdate.setInt(1, carId);
                psCarUpdate.executeUpdate();
            }

            System.out.println("Booking cancelled successfully and car availability updated.");
        } catch (SQLException e) {
            System.out.println("Error cancelling booking: " + e.getMessage());
        }
    }

    public void giveRating() {
        try (Connection conn = DBConnect.getConnection()) {
            // Show available cars first
            System.out.println("\n🚗 Available Cars to Rate:");
            Admin admin = new Admin(); // or reuse an existing Admin instance
            admin.viewAvailableCars();

            System.out.print("Enter Car ID to rate: ");
            int carId = sc.nextInt();

            // Check if car exists and is available
            String checkCar = "SELECT * FROM car WHERE car_id = ? AND availability = 1";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkCar)) {
                checkStmt.setInt(1, carId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Car not found or not available.");
                        return;
                    }
                }
            }

            System.out.print("Enter rating (1 to 5): ");
            int rating = sc.nextInt();
            if (rating < 1 || rating > 5) {
                System.out.println("❌ Invalid rating. Must be between 1 and 5.");
                return;
            }

            // Insert into ratings table
            String insertRating = "INSERT INTO ratings (customer_id, car_id, rating) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertRating)) {
                ps.setInt(1, customer_Id);
                ps.setInt(2, carId);
                ps.setInt(3, rating);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ Rating submitted successfully.");
                } else {
                    System.out.println("❌ Failed to submit rating.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error while submitting rating: " + e.getMessage());
        }
    }

    public void returnCar() {
        try (Connection conn = DBConnect.getConnection()) {
            // 1. Show active bookings
            String query = "SELECT * FROM bookings WHERE customer_id = ? AND status = 'Booked'";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, customer_Id);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n=== Your Active Bookings ===");
                    System.out.printf("%-10s %-10s %-20s %-20s\n", "BookingID", "CarID", "Start Time", "End Time");
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        int bid = rs.getInt("booking_id");
                        int carId = rs.getInt("car_id");
                        Timestamp start = rs.getTimestamp("start_datetime");
                        Timestamp end = rs.getTimestamp("end_datetime");
                        System.out.printf("%-10d %-10d %-20s %-20s\n", bid, carId, start, end);
                    }
                    if (!found) {
                        System.out.println("❌ No active bookings to return.");
                        return;
                    }
                }
            }

            // 2. Choose booking
            System.out.print("\nEnter Booking ID to return car: ");
            int bookingId = sc.nextInt();

            String getBooking = "SELECT * FROM bookings WHERE booking_id = ? AND customer_id = ? AND status = 'Booked'";
            try (PreparedStatement ps = conn.prepareStatement(getBooking)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, customer_Id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Invalid booking ID or already returned.");
                        return;
                    }

                    // 3. Extract data
                    int carId = rs.getInt("car_id");
                    Timestamp startTS = rs.getTimestamp("start_datetime");
                    Timestamp expectedEndTS = rs.getTimestamp("end_datetime");
                    double pricePerHour = rs.getDouble("total_cost") / rs.getDouble("total_hours");

                    LocalDateTime start = startTS.toLocalDateTime();
                    LocalDateTime expectedEnd = expectedEndTS.toLocalDateTime();
                    LocalDateTime now = LocalDateTime.now();

                    // 4. Cost calculation
                    double lateFeePerHour = 250;
                    Map<String, Double> cost = Rental.costCalculator(start, expectedEnd, now, pricePerHour, lateFeePerHour);
                    double totalCost = cost.get("totalCost");
                    double rentalHours = cost.get("totalHours");
                    double lateHours = cost.get("lateHours");
                    double lateFee = cost.get("lateFee");

                    // 5. Simulate payment
                    System.out.printf("\n💳 Final Amount to Pay: ₹%.2f\n", totalCost);
                    System.out.print("Proceed with payment? (yes/no): ");
                    sc.nextLine(); // consume newline
                    String confirm = sc.nextLine().trim().toLowerCase();

                    if (!confirm.equals("yes")) {
                        System.out.println("❌ Payment cancelled. Car not returned.");
                        return;
                    }

                    // ---- Payment Process ----
                    System.out.print("Select payment method (Cash / UPI / Card): ");
                    String paymentMethod = sc.nextLine().trim();

                    // Insert into payment table
                    String insertPayment = "INSERT INTO payment (booking_id, customer_id, amount, payment_method) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psPayment = conn.prepareStatement(insertPayment)) {
                        psPayment.setInt(1, bookingId);
                        psPayment.setInt(2, customer_Id);
                        psPayment.setDouble(3, totalCost);
                        psPayment.setString(4, paymentMethod);
                        int rows = psPayment.executeUpdate();

                        if (rows > 0) {
                            System.out.println("✅ Payment recorded successfully.");
                        } else {
                            System.out.println("❌ Payment failed. Car return aborted.");
                            return; // EXIT here if payment fails
                        }
                    }

                    // 6. Get car details for the bill
                    String getCarDetails = "SELECT model, brand, type, seats FROM car WHERE car_id = ?";
                    String model = "", brand = "", type = "";
                    int seats = 0;

                    try (PreparedStatement psCarDetails = conn.prepareStatement(getCarDetails)) {
                        psCarDetails.setInt(1, carId);
                        try (ResultSet rsCar = psCarDetails.executeQuery()) {
                            if (rsCar.next()) {
                                model = rsCar.getString("model");
                                brand = rsCar.getString("brand");
                                type = rsCar.getString("type");
                                seats = rsCar.getInt("seats");
                            }
                        }
                    }

                    // 7. Update booking status
                    String updateBooking = "UPDATE bookings SET status = 'Returned', end_datetime = ?, total_hours = ?, total_cost = ? WHERE booking_id = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateBooking)) {
                        psUpdate.setTimestamp(1, Timestamp.valueOf(now));
                        psUpdate.setDouble(2, rentalHours);
                        psUpdate.setDouble(3, totalCost);
                        psUpdate.setInt(4, bookingId);
                        psUpdate.executeUpdate();
                    }

                    // 8. Update car availability
                    try (PreparedStatement psCar = conn.prepareStatement("UPDATE car SET availability = 1 WHERE car_id = ?")) {
                        psCar.setInt(1, carId);
                        psCar.executeUpdate();
                    }

                    // 9. Insert into rental table
                    String insertRental = "INSERT INTO rental (car_id, customer_id, rent_date, due_date, return_date, total_price) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement psRental = conn.prepareStatement(insertRental)) {
                        psRental.setInt(1, carId);
                        psRental.setInt(2, customer_Id);
                        psRental.setDate(3, Date.valueOf(start.toLocalDate()));
                        psRental.setDate(4, Date.valueOf(expectedEnd.toLocalDate()));
                        psRental.setDate(5, Date.valueOf(now.toLocalDate()));
                        psRental.setDouble(6, totalCost);
                        psRental.executeUpdate();
                    }

                    // 10. Print Final Real-Time Bill
                    String billFilePath = billing(bookingId, carId, model, brand, type, seats, start, expectedEnd, now, rentalHours, pricePerHour, lateHours, lateFee, totalCost, paymentMethod);

                    System.out.print("Enter customer's email to send bill: ");
                    String toEmail = sc.nextLine().trim();
                    if (!toEmail.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                        System.out.println("❌ Invalid email: " + toEmail);
                        System.out.println("Sorry we can't shere Bill");
                        return;
                    }

                    sendBillEmail(toEmail, billFilePath);


                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error during return process: " + e.getMessage());
        }
    }

    String billing(int bookingId, int carId, String model, String brand, String type, int seats, LocalDateTime start, LocalDateTime expectedEnd, LocalDateTime now, double rentalHours, double pricePerHour, double lateHours, double lateFee, double totalCost, String paymentMethod) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String bill = "\n========== 🧾 RENTAL BILL ==========\n" + String.format("📄 Booking ID     : %d\n", bookingId) + String.format("🚗 Car ID         : %d\n", carId) + String.format("🔤 Model          : %s\n", model) + String.format("🏷️ Brand          : %s\n", brand) + String.format("🚘 Type           : %s\n", type) + String.format("🪑 Seats          : %d\n", seats) + "-----------------------------------\n" + String.format("📍 Start Time     : %s\n", start.format(fmt)) + String.format("📍 Expected Return: %s\n", expectedEnd.format(fmt)) + String.format("📍 Actual Return  : %s\n", now.format(fmt)) + "-----------------------------------\n" + String.format("⏱ Total Hours     : %.1f hrs\n", rentalHours) + String.format("💰 Base Price      : ₹%.2f\n", pricePerHour * rentalHours) + String.format("⏰ Late Hours      : %.1f hrs\n", lateHours) + String.format("🔻 Late Fee        : ₹%.2f\n", lateFee) + "-----------------------------------\n" + String.format("💳 Total Paid      : ₹%.2f\n", totalCost) + String.format("💳 Payment Method  : %s\n", paymentMethod) + "✅ Thank you for choosing CarVerse!\n" + "=====================================\n";

        // 1) Print to console
        System.out.println(bill);

        // 2) Append to a master log file
        try (FileWriter fw = new FileWriter("RentalBills.txt", true)) {
            fw.write(bill);
        } catch (IOException e) {
            System.out.println("❌ Error saving master bill log: " + e.getMessage());
        }

        // 3) Save this bill as its own file and RETURN the path (e.g., Bill_123.txt)
        String singleBillPath = "Bill_" + bookingId + ".txt";
        try (FileWriter fw = new FileWriter(singleBillPath)) {
            fw.write(bill);
        } catch (IOException e) {
            System.out.println("❌ Error saving single bill file: " + e.getMessage());
        }

        return singleBillPath;
    }

    public void sendBillEmail(String toEmail, String billFilePath) {
        final String fromEmail = "prithvitpatel@gmail.com";   // TODO: put your Gmail here
        final String password = "zsfm thja pida jfia";  // TODO: paste app password here

        // quick check
        File f = new File(billFilePath);
        if (!f.exists()) {
            System.out.println("❌ Bill file not found: " + billFilePath);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            // Optional: show a readable sender name
            message.setFrom(new InternetAddress(fromEmail, "CarVerse Billing"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject("Your Car Rental Bill");

            // Body text (UTF-8 so ₹ etc. are fine)
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Dear Customer,\n\nPlease find your rental bill attached.\n\nRegards,\nCarVerse", "UTF-8");

            // Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(billFilePath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("RentalBill.txt");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email sent to " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}

class Admin {
    Scanner sc = new Scanner(System.in);
    HashMap<Integer, Car> carMap = new HashMap<>();

    void adminLogin() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            String query = "SELECT * FROM admin WHERE adminname = ? AND password = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                System.out.print("Enter admin username: ");
                pst.setString(1, sc.nextLine());
                System.out.print("Enter password: ");
                pst.setString(2, sc.nextLine());

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("✅ Admin login successful!");
                        CarVerse.adminMenu();
                    } else {
                        System.out.println("❌ Invalid username or password.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void addCar() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO car (model, brand, type, price_per_hour, seats, availability) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                System.out.println("\n=== Add New Car ===");
                System.out.print("Enter car model: ");
                String model = sc.nextLine();
                pst.setString(1, model);
                System.out.print("Enter car brand: ");
                String brand = sc.nextLine();
                pst.setString(2, brand);
                System.out.print("Enter car type: ");
                String type = sc.nextLine();
                pst.setString(3, type);
                System.out.print("Enter price per hour: ");
                double pricePerHour = sc.nextDouble();
                pst.setDouble(4, pricePerHour);
                System.out.print("Enter seats: ");
                int seats = sc.nextInt();
                pst.setInt(5, seats);
                boolean availability = true;
                pst.setBoolean(6, availability);
                sc.nextLine();
                if (pst.executeUpdate() > 0) {
                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        if (rs.next()) {
                            int id = rs.getInt(1);
                            Car car = new Car(id, model, brand, type, pricePerHour, availability, seats);
                            carMap.put(id, car);
                            System.out.println("✅ Car added with ID: " + id);
                        }
                    }
                } else {
                    System.out.println("❌ Failed to add car.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void viewAllCars() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            String query = "SELECT * FROM car";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("\n=============================");
                System.out.println("🚗  Car List");
                System.out.println("=============================");
                System.out.printf("%-5s %-12s %-10s %-10s %-6s %-14s %-12s %-8s\n", "ID", "Model", "Brand", "Type", "Seats", "Price/Hour", "Status", "Rating");
                System.out.println("---------------------------------------------------------------------");

                while (rs.next()) {
                    int id = rs.getInt("car_id");
                    String model = rs.getString("model");
                    String brand = rs.getString("brand");
                    String type = rs.getString("type");
                    int seats = rs.getInt("seats");
                    double price = rs.getDouble("price_per_hour");
                    boolean available = rs.getBoolean("availability");
                    String status = available ? "Available" : "Booked";
                    double avgRating = getAverageRating(conn, id);
                    System.out.printf("%-5d %-12s %-10s %-10s %-6d ₹%-13.2f %-12s ⭐%.1f\n", id, model, brand, type, seats, price, status, avgRating);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void updateCarDetails() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to update: ");
            int carId = sc.nextInt();
            sc.nextLine();
            System.out.println("Choose detail to update:");
            System.out.println("1. Brand");
            System.out.println("2. Model");
            System.out.println("3. Price per Hour");
            System.out.println("4. Availability (true/false)");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();
            String column = "";
            if (choice == 1) {
                column = "brand";
                System.out.print("Enter new brand: ");
            } else if (choice == 2) {
                column = "model";
                System.out.print("Enter new model: ");
            } else if (choice == 3) {
                column = "price_per_hour";
                System.out.print("Enter new price/hour: ");
            } else if (choice == 4) {
                column = "availability";
                System.out.print("Enter availability (true/false): ");
            } else {
                System.out.println("Invalid choice.");
                return;
            }
            String newValue = sc.nextLine();
            String query = "UPDATE car SET " + column + " = ? WHERE car_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                if (column.equals("price_per_hour")) ps.setDouble(1, Double.parseDouble(newValue));
                else if (column.equals("availability")) ps.setBoolean(1, Boolean.parseBoolean(newValue));
                else ps.setString(1, newValue);
                ps.setInt(2, carId);

                int r = ps.executeUpdate();
                System.out.println(r > 0 ? "Car updated successfully." : "Car update failed.");
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void viewAvailableCars() throws SQLException {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== View Available Cars ===");
        System.out.println("Sort by:");
        System.out.println("1. Brand (A-Z)");
        System.out.println("2. Brand (Z-A)");
        System.out.println("3. Price per hour (Low to High)");
//        System.out.println("4. Price per hour (High to Low)");
        System.out.println("5. Seats (Low to High)");
        System.out.println("6. Seats (High to Low)");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        String orderBy = "";
        switch (choice) {
            case 1 -> orderBy = "ORDER BY brand ASC";
            case 2 -> orderBy = "ORDER BY brand DESC";
            case 3 -> orderBy = "ORDER BY price_per_hour ASC";
//            case 4 -> orderBy = "ORDER BY price_per_hour DESC";
            case 5 -> orderBy = "ORDER BY seats ASC";
            case 6 -> orderBy = "ORDER BY seats DESC";
            default -> {
                System.out.println("Invalid choice. Showing default order.");
                orderBy = "ORDER BY car_id ASC";
            }
        }

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM car WHERE availability = 1 " + orderBy;
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n==============================");
                System.out.println("🚗  Available Cars for Rent");
                System.out.println("==============================");
                System.out.printf("%-5s %-12s %-10s %-10s %-6s %-14s %-8s\n", "ID", "Model", "Brand", "Type", "Seats", "Price/Hour", "Rating");
                System.out.println("----------------------------------------------------------");

                boolean hasCars = false;
                while (rs.next()) {
                    hasCars = true;
                    int id = rs.getInt("car_id");
                    String model = rs.getString("model");
                    String brand = rs.getString("brand");
                    String type = rs.getString("type");
                    int seats = rs.getInt("seats");
                    double price = rs.getDouble("price_per_hour");
                    double avgRating = getAverageRating(conn, id);
                    System.out.printf("%-5d %-12s %-10s %-10s %-6d ₹%-13.2f ⭐%.1f\n", id, model, brand, type, seats, price, avgRating);
                }

                if (!hasCars) {
                    System.out.println("❌ No cars are currently available.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error displaying available cars: " + e.getMessage());
        }
    }

    void updateCarAvailability() {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to update availability: ");
            int carId = sc.nextInt();
            sc.nextLine();

            // Step 1: Check if car exists
            String checkCar = "SELECT * FROM car WHERE car_id = ?";
            try (PreparedStatement checkCarPs = conn.prepareStatement(checkCar)) {
                checkCarPs.setInt(1, carId);
                try (ResultSet carRs = checkCarPs.executeQuery()) {
                    if (!carRs.next()) {
                        System.out.println("❌ Car ID not found.");
                        return;
                    }
                }
            }

            // Step 2: Check if car is currently booked in bookings table
            String checkBooking = "SELECT * FROM bookings WHERE car_id = ? AND status = 'Booked'";
            try (PreparedStatement checkBookingPs = conn.prepareStatement(checkBooking)) {
                checkBookingPs.setInt(1, carId);
                try (ResultSet bookingRs = checkBookingPs.executeQuery()) {
                    if (bookingRs.next()) {
                        System.out.println("❌ This car is currently booked. You cannot change its availability.");
                        return;
                    }
                }
            }

            // Step 3: If not booked, proceed to update availability
            System.out.print("Set availability (true for available, false for not available): ");
            boolean availability = sc.nextBoolean();

            String updateAvailability = "UPDATE car SET availability = ? WHERE car_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateAvailability)) {
                updatePs.setBoolean(1, availability);
                updatePs.setInt(2, carId);

                int updatedRows = updatePs.executeUpdate();
                if (updatedRows > 0) {
                    System.out.println("✅ Car availability updated successfully.");
                    if (carMap.containsKey(carId)) {
                        carMap.get(carId).availability = availability;
                    }
                } else {
                    System.out.println("❌ Failed to update car availability.");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }


    void viewCurrentlyRentedCars() throws SQLException {
        // The following query and schema must match your actual DB!
        // Adjust JOIN and table/column names as per your DB if needed.
        try (Connection conn = DBConnect.getConnection()) {
            String query = "SELECT r.rental_id, c.car_id, c.model, c.brand, c.type, c.seats, c.price_per_hour, cu.name AS customer_name, cu.phone_no, r.rent_date " + "FROM rental r " + "JOIN car c ON r.car_id = c.car_id " + "JOIN customer cu ON r.customer_id = cu.customer_id " + "WHERE r.return_date IS NULL";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("\n==============================");
                System.out.println("🚘 Currently Rented Cars");
                System.out.println("==============================");
                System.out.printf("%-5s %-5s %-12s %-10s %-10s %-5s %-12s %-18s %-12s %-12s\n", "RId", "CID", "Model", "Brand", "Type", "Seat", "Price/Hour", "Customer Name", "Phone", "Rent Date");
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    int rentalId = rs.getInt("rental_id");
                    int carId = rs.getInt("car_id");
                    String model = rs.getString("model");
                    String brand = rs.getString("brand");
                    String type = rs.getString("type");
                    int seats = rs.getInt("seats");
                    double price = rs.getDouble("price_per_hour");
                    String customerName = rs.getString("customer_name");
                    String phone = rs.getString("phone_no");
                    Date rentDate = rs.getDate("rent_date");

                    System.out.printf("%-5d %-5d %-12s %-10s %-10s %-5d ₹%-11.2f %-18s %-12s %-12s\n", rentalId, carId, model, brand, type, seats, price, customerName, phone, rentDate);
                }
                if (!hasResults) {
                    System.out.println("❌ No cars are currently rented out.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void viewOverdueRentals() throws SQLException {
        // Query and schema must match your actual DB!
        try (Connection conn = DBConnect.getConnection()) {
            String query = "SELECT r.rental_id, c.car_id, c.model, c.brand, cu.name AS customer_name, cu.phone_no, r.rent_date, r.due_date " + "FROM rental r " + "JOIN car c ON r.car_id = c.car_id " + "JOIN customer cu ON r.customer_id = cu.customer_id " + "WHERE r.return_date IS NULL AND r.due_date < CURDATE()";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("\n==============================");
                System.out.println("🚨 Overdue Rentals");
                System.out.println("==============================");
                System.out.printf("%-5s %-5s %-12s %-10s %-15s %-12s %-12s %-12s\n", "RId", "CID", "Model", "Brand", "Customer", "Phone", "Rent Date", "Due Date");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    int rentalId = rs.getInt("rental_id");
                    int carId = rs.getInt("car_id");
                    String model = rs.getString("model");
                    String brand = rs.getString("brand");
                    String customer = rs.getString("customer_name");
                    String phone = rs.getString("phone_no");
                    Date rentDate = rs.getDate("rent_date");
                    Date dueDate = rs.getDate("due_date");

                    System.out.printf("%-5d %-5d %-12s %-10s %-15s %-12s %-12s %-12s\n", rentalId, carId, model, brand, customer, phone, rentDate, dueDate);
                }
                if (!found) {
                    System.out.println("❌ No overdue rentals found.");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void generateReports() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            String totalCarsQuery = "SELECT COUNT(*) AS total_cars FROM car";
            String totalRentalsQuery = "SELECT COUNT(*) AS total_rentals FROM rental";
            String availableCarsQuery = "SELECT COUNT(*) AS available FROM car WHERE availability = 1";
            String rentedCarsQuery = "SELECT COUNT(*) AS rented FROM rental WHERE return_date IS NULL";
            String overdueQuery = "SELECT COUNT(*) AS overdue FROM rental WHERE return_date IS NULL AND due_date < CURDATE()";
            String totalRevenueQuery = "SELECT IFNULL(SUM(total_price), 0) AS revenue FROM rental WHERE total_price IS NOT NULL";

            int totalCars = 0, totalRentals = 0, availableCars = 0, rentedCars = 0, overdueCount = 0;
            double totalRevenue = 0;

            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(totalCarsQuery)) {
                    if (rs.next()) totalCars = rs.getInt("total_cars");
                }
                try (ResultSet rs = stmt.executeQuery(totalRentalsQuery)) {
                    if (rs.next()) totalRentals = rs.getInt("total_rentals");
                }
                try (ResultSet rs = stmt.executeQuery(availableCarsQuery)) {
                    if (rs.next()) availableCars = rs.getInt("available");
                }
                try (ResultSet rs = stmt.executeQuery(rentedCarsQuery)) {
                    if (rs.next()) rentedCars = rs.getInt("rented");
                }
                try (ResultSet rs = stmt.executeQuery(overdueQuery)) {
                    if (rs.next()) overdueCount = rs.getInt("overdue");
                }
                try (ResultSet rs = stmt.executeQuery(totalRevenueQuery)) {
                    if (rs.next()) totalRevenue = rs.getDouble("revenue");
                }
            }

            System.out.println("\n=========== REPORTS ===========");
            System.out.println("Total Number of Cars: " + totalCars);
            System.out.println("Total Number of Rentals: " + totalRentals);
            System.out.println("Currently Available Cars: " + availableCars);
            System.out.println("Currently Rented Cars: " + rentedCars);
            System.out.println("Overdue Rentals: " + overdueCount);
            System.out.printf("Total Revenue: ₹%.2f\n", totalRevenue);
            System.out.println("===============================\n");
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void removeCar() throws SQLException {
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter Car ID to remove: ");
            int carId = sc.nextInt();
            sc.nextLine();

            String checkQuery = "SELECT * FROM car WHERE car_id = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
                checkPs.setInt(1, carId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Car ID not found.");
                        return;
                    }
                    String deleteQuery = "DELETE FROM car WHERE car_id = ?";
                    try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
                        deletePs.setInt(1, carId);

                        int result = deletePs.executeUpdate();
                        if (result > 0) {
                            carMap.remove(carId);
                            System.out.println("✅ Car with ID " + carId + " removed successfully.");
                        } else {
                            System.out.println("❌ Failed to remove car. Please check Car ID.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    private double getAverageRating(Connection conn, int carId) {
        String sql = "SELECT AVG(rating) AS avg_rating FROM ratings WHERE car_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error fetching rating for car ID " + carId + ": " + e.getMessage());
        }
        return 0.0;
    }

    void viewAllCustomer() {
        CustomerNode head = null, tail = null;
        int count = 0;
        try (Connection conn = DBConnect.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM customer")) {

            // 1. Load all customers into LinkedList
            while (rs.next()) {
                CustomerNode node = new CustomerNode(rs.getInt("customer_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone_no"), rs.getString("address"), rs.getDate("dob"));
                if (head == null) {
                    head = tail = node;
                } else {
                    tail.next = node;
                    tail = node;
                }
                count++;
            }

            if (count == 0) {
                System.out.println("\n❌ No customers found.");
                return;
            }

            // 2. Print all customers in tabular format from LinkedList
            System.out.println("\n============== All Customers ==============");
            System.out.printf("%-5s %-20s %-25s %-12s %-25s %-12s\n", "ID", "Name", "Email", "Phone", "Address", "DOB");
            System.out.println("-------------------------------------------------------------------------------");

            CustomerNode current = head;
            while (current != null) {
                System.out.printf("%-5d %-20s %-25s %-12s %-25s %-12s\n", current.id, current.name, current.email, current.phone, current.address, current.dob != null ? current.dob.toString() : "");
                current = current.next;
            }
            System.out.println("===========================================\n");

        } catch (Exception e) {
            System.out.println("❌ Error displaying customers: " + e.getMessage());
        }
    }

}

class Rental {
    public static Map<String, Double> costCalculator(LocalDateTime start, LocalDateTime expectedEnd, LocalDateTime actualReturn, double pricePerHour, double lateFeePerHour) {

        Map<String, Double> result = new HashMap<>();

        // Make sure we always calculate in the correct order
        long totalHours = Duration.between(start, actualReturn).toHours();
        if (totalHours < 0) {
            totalHours = Duration.between(actualReturn, start).toHours(); // swap if needed
        }

        if (Duration.between(start, actualReturn).toMinutes() % 60 != 0) {
            totalHours += 1; // round up partial hour
        }

        long lateHours = Duration.between(expectedEnd, actualReturn).toHours();
        lateHours = Math.max(lateHours, 0);

        double rentalCost = totalHours * pricePerHour;
        double lateFee = lateHours * lateFeePerHour;
        double totalCost = rentalCost + lateFee;

        result.put("totalHours", (double) totalHours);
        result.put("lateHours", (double) lateHours);
        result.put("rentalCost", rentalCost);
        result.put("lateFee", lateFee);
        result.put("totalCost", totalCost);

        return result;
    }
}

class CustomerNode {
    int id;
    String name;
    String email;
    String phone;
    String address;
    Date dob;
    CustomerNode next;

    public CustomerNode(int id, String name, String email, String phone, String address, Date dob) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.next = null;
    }
}
