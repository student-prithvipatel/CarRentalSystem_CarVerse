package carverse;

import jakarta.activation.*;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Customer {
    Scanner sc = new Scanner(System.in);
    static int customer_Id;
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private LocalDate dob;
    private int password_hash;

    void customerRegistartion(){
        try (Connection conn = DBConnect.getConnection()) {
            System.out.print("Enter name: ");
            name = sc.nextLine();
            while (true) {
                System.out.print("Enter email (must be lowercase and contain '@'): ");
                email = sc.nextLine().toLowerCase();
                if (email.contains("@")
                        && email.equals(email.toLowerCase())
                        && email.indexOf('@') == email.lastIndexOf('@')
                        && email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                    break;
                } else {
                    System.out.println("❌ Invalid email. Try again.");
                }
            }
            while (true) {
                System.out.print("Enter password (min 8 chars, at least 1 special character): ");
                String password = sc.nextLine();
                boolean isLengthValid = password.length() >= 8;
                boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");
                if (!isLengthValid || !hasSpecialChar) {
                    System.out.println("❌ Password must be at least 8 characters and contain at least one special character.");
                    continue;
                }
                System.out.print("Confirm password: ");
                String confirmPassword = sc.nextLine();
                if (password.equals(confirmPassword)) {
                    password_hash = SimpleHash.hash(confirmPassword);
                    break;
                } else {
                    System.out.println("❌ Passwords do not match. Please try again.");
                }
            }
            while (true) {
                System.out.print("Enter 10 digit phone number (not starting with 0): ");
                phoneNo = sc.nextLine();
                if (phoneNo.matches("^[6-9][0-9]{9}$")) {
                    break;
                } else {
                    System.out.println("❌ Invalid phone number. Try again.");
                }
            }
            System.out.print("Enter address: ");
            address = sc.nextLine();
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
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customer(name, email, phone_no, address, dob, password_hash) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phoneNo);
                ps.setString(4, address);
                ps.setDate(5, Date.valueOf(dob));
                ps.setInt(6, password_hash);
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
                CarVerse.customerMenu();
            }
        }catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }
    public void customerLogin() {
        try (Connection conn = DBConnect.getConnection()) {
            while (true) { // main login loop
                System.out.println("===== Customer Login =====");
                System.out.println("1. Login using Email and Password");
                System.out.println("2. Login using Phone Number and Password");
                System.out.println("3. Go back");
                System.out.print("Choose an option (1 to 3): ");
                int choice = CarVerse.getIntInput(1, 3);

                String lookupSql = null;
                String lookupValueLabel = null;
                String lookupValue = null;

                switch (choice) {
                    case 1 :
                        lookupSql = "SELECT * FROM customer WHERE email = ?";
                        lookupValueLabel = "email";

                        while (true) {
                            System.out.print("Enter email: ");
                            String email = sc.nextLine().trim().toLowerCase();

                            // validate email format
                            if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                                System.out.println("❌ Invalid email format.");
                                System.out.println("1. Try again");
                                System.out.println("2. Go back");
                                int opt = CarVerse.getIntInput(1, 2);
                                if (opt == 1) {
                                    continue;
                                } else {
                                    return;
                                }
                            } else {
                                lookupValue = email;
                                break;
                            }
                        }
                        break;
                    case 2 :
                        lookupSql = "SELECT * FROM customer WHERE phone_no = ?";
                        lookupValueLabel = "phone number";

                        while (true) {
                            System.out.print("Enter phone number: ");
                            String phoneNo = sc.nextLine().trim();

                            // simple Indian phone validation
                            if (!phoneNo.matches("^[6-9][0-9]{9}$")) {
                                System.out.println("❌ Invalid phone number.");
                                System.out.println("1. Try again");
                                System.out.println("2. Go back");
                                int opt = CarVerse.getIntInput(1, 2);
                                if (opt == 1) {
                                    continue;
                                } else {
                                    return;
                                }
                            } else {
                                lookupValue = phoneNo;
                                break;
                            }
                        }
                        break;
                    case 3 :
                        return; // go back to main menu
                }

                // --- check DB for account ---
                try (PreparedStatement ps = conn.prepareStatement(lookupSql)) {
                    ps.setString(1, lookupValue);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("❌ No account found with this " + lookupValueLabel + ". Please register first.");
                            return;
                        }

                        // Cache values
                        int customerId   = rs.getInt("customer_id");
                        int dbPassword   = rs.getInt("password_hash");
                        String dbName    = rs.getString("name");
                        String dbEmail   = rs.getString("email");
                        String dbPhone   = rs.getString("phone_no");
                        String dbAddress = rs.getString("address");
                        java.sql.Date sqlDob = rs.getDate("dob");
                        LocalDate dbDob = (sqlDob != null) ? sqlDob.toLocalDate() : null;

                        // --- password attempts ---
                        final int MAX_ATTEMPTS = 3;
                        boolean authed = false;
                        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                            System.out.print("Enter password (" + attempt + "/" + MAX_ATTEMPTS + "): ");
                            String inputPassword = sc.nextLine();
                            int inputHash = SimpleHash.hash(inputPassword);

                            if (inputHash == dbPassword) {
                                authed = true;
                                break;
                            } else if (attempt < MAX_ATTEMPTS) {
                                System.out.println("❌ Incorrect password. Try again.");
                            }
                        }

                        if (!authed) {
                            // after 3 failed tries
                            System.out.println("\n❌ Too many failed attempts.");
                            System.out.println("1. Reset password");
                            System.out.println("2. Go back");
                            int opt = CarVerse.getIntInput(1, 2);

                            if (opt == 1) {
                                if (dbDob == null) {
                                    System.out.println("⚠️ DOB not available. Cannot reset here.");
                                    return;
                                }
                                System.out.print("Enter your DOB (DD-MM-YYYY): ");
                                String dobStr = sc.nextLine().trim();
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    LocalDate dob = LocalDate.parse(dobStr, formatter);
                                    if (!dob.equals(dbDob)) {
                                        System.out.println("❌ DOB verification failed.");
                                        return;
                                    }
                                } catch (Exception ex) {
                                    System.out.println("❌ Invalid date format.");
                                    return;
                                }

                                System.out.print("Enter new password: ");
                                String p1 = sc.nextLine();
                                System.out.print("Re-enter new password: ");
                                String p2 = sc.nextLine();
                                if (!p1.equals(p2)) {
                                    System.out.println("❌ Passwords do not match.");
                                    return;
                                }

                                int newHash = SimpleHash.hash(p1);
                                try (PreparedStatement ups = conn.prepareStatement(
                                        "UPDATE customer SET password_hash = ? WHERE customer_id = ?")) {
                                    ups.setInt(1, newHash);
                                    ups.setInt(2, customerId);
                                    if (ups.executeUpdate() == 1) {
                                        System.out.println("✅ Password updated successfully. Please login again.");
                                    }
                                }
                                return;
                            } else {
                                return;
                            }
                        }

                        // success
                        Customer.customer_Id = customerId;
                        this.name = dbName;
                        this.email = dbEmail;
                        this.phoneNo = dbPhone;
                        this.address = dbAddress;
                        this.dob = dbDob;
                        this.password_hash = dbPassword;

                        System.out.println("✅ Login successful! Welcome, " + this.name + "!");
                        CarVerse.customerMenu();
                        return;
                    }
                }
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
            if (!newPassword.trim().isEmpty()){
                int hash = SimpleHash.hash(newPassword);
                password_hash = hash;
            }

            // Step 2: Update in DB
            String query = "UPDATE customer SET name=?, email=?, phone_no=?, address=?, dob=?, password_hash=? WHERE customer_id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phoneNo);
            ps.setString(4, address);
            ps.setDate(5, java.sql.Date.valueOf(this.dob));
            ps.setInt(6, password_hash);
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
    void bookCar()  {
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
        }catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    void viewMyBookings(){
        try (Connection conn = DBConnect.getConnection()) {

            String baseSelect = """
            SELECT b.booking_id, c.model, c.brand, c.type,
                   b.start_location, b.end_location,
                   b.start_datetime, b.end_datetime,
                   b.total_hours, b.total_cost, b.status
            FROM bookings b
            JOIN car c ON b.car_id = c.car_id
            WHERE b.customer_id = ?
              AND %s
            ORDER BY b.start_datetime DESC
        """;

            // 1) Active
            try (PreparedStatement ps = conn.prepareStatement(String.format(baseSelect, "b.status = 'Booked'"))) {
                ps.setInt(1, customer_Id);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n===============================");
                    System.out.println("✅ Active Bookings");
                    System.out.println("===============================");
                    printBookingsTable(rs);
                }
            }

            // 2) Returned / Completed (add more statuses if you use them)
            try (PreparedStatement ps = conn.prepareStatement(String.format(baseSelect, "b.status IN ('Returned','Completed')"))) {
                ps.setInt(1, customer_Id);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\n===============================");
                    System.out.println("↩️  Returned / Completed");
                    System.out.println("===============================");
                    printBookingsTable(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Something went wrong: " + e.getMessage());
        }
    }

    // Small helper to print rows
    private void printBookingsTable(ResultSet rs) throws SQLException {
        boolean any = false;
        System.out.printf("%-6s %-10s %-10s %-10s %-12s %-12s %-16s %-16s %-7s %-10s %-10s\n",
                "BID", "Model", "Brand", "Type", "From", "To", "Start Date", "End Date", "Hours", "Cost", "Status");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        while (rs.next()) {
            any = true;
            System.out.printf("%-6d %-10s %-10s %-10s %-12s %-12s %-16s %-16s %-7.1f ₹%-9.2f %-10s\n",
                    rs.getInt("booking_id"),
                    rs.getString("model"),
                    rs.getString("brand"),
                    rs.getString("type"),
                    rs.getString("start_location"),
                    rs.getString("end_location"),
                    rs.getTimestamp("start_datetime").toLocalDateTime(),
                    rs.getTimestamp("end_datetime").toLocalDateTime(),
                    rs.getDouble("total_hours"),
                    rs.getDouble("total_cost"),
                    rs.getString("status"));
        }
        if (!any) System.out.println("ℹ️ None.");
    }

    void cancelBooking()
    {
        try (Connection conn = DBConnect.getConnection()) {
            // 1. List active bookings for this customer
            String listBookingsSql =
                    "SELECT booking_id, car_id, start_datetime, end_datetime FROM bookings WHERE customer_id = ? AND status='B1ooked'";

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

                        System.out.printf("%-10d %-10d %-20s %-20s\n",
                                bookingId, carId, start.toString(), end.toString());
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
            sc.nextLine();
        }
    }
    public void returnCar() {
        try (Connection conn = DBConnect.getConnection()) {
            // 1. Show active bookings
            String query = "SELECT * FROM bookings WHERE customer_id = ? AND status = 'Booked' AND start_datetime <= NOW()";
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
                        System.out.println("❌ No bookings eligible to return right now.");
                        System.out.println("ℹ️ If your booking has not started yet, use Cancel Booking.");
                        System.out.println("⏳ If your booking is still in progress, you can return only after the rental end time.");
                        return;
                    }
                }
            }

            // 2. Choose booking
            System.out.print("\nEnter Booking ID to return car: ");
            int bookingId = sc.nextInt();

            String getBooking = "SELECT * FROM bookings WHERE booking_id = ? AND customer_id = ? AND status = 'Booked' AND end_datetime <= NOW()";
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

                    // ---- Payment Process ----
                    System.out.print("Select payment method (Cash / UPI / Card): ");
                    String paymentMethod = processPayment(totalCost);
                    // Insert into payment table
                    String insertPayment = "INSERT INTO payment (booking_id, customer_id, amount, payment_method, bill_text) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psPayment = conn.prepareStatement(insertPayment)) {
                        psPayment.setInt(1, bookingId);
                        psPayment.setInt(2, customer_Id);
                        psPayment.setDouble(3, totalCost);
                        psPayment.setString(4, paymentMethod);

                        // Print Final Real-Time Bill
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                        String bill =
                                "\n========== 🧾 RENTAL BILL ==========\n" +
                                        String.format("📄 Booking ID     : %d\n", bookingId) +
                                        String.format("🚗 Car ID         : %d\n", carId) +
                                        String.format("🔤 Model          : %s\n", model) +
                                        String.format("🏷️ Brand          : %s\n", brand) +
                                        String.format("🚘 Type           : %s\n", type) +
                                        String.format("🪑 Seats          : %d\n", seats) +
                                        "-----------------------------------\n" +
                                        String.format("📍 Start Time     : %s\n", start.format(fmt)) +
                                        String.format("📍 Expected Return: %s\n", expectedEnd.format(fmt)) +
                                        String.format("📍 Actual Return  : %s\n", now.format(fmt)) +
                                        "-----------------------------------\n" +
                                        String.format("⏱ Total Hours     : %.1f hrs\n", rentalHours) +
                                        String.format("💰 Base Price      : ₹%.2f\n", pricePerHour * rentalHours) +
                                        String.format("⏰ Late Hours      : %.1f hrs\n", lateHours) +
                                        String.format("🔻 Late Fee        : ₹%.2f\n", lateFee) +
                                        "-----------------------------------\n" +
                                        String.format("💳 Total Paid      : ₹%.2f\n", totalCost) +
                                        String.format("💳 Payment Method  : %s\n", paymentMethod) +
                                        "✅ Thank you for choosing CarVerse!\n" +
                                        "=====================================\n";

                        // Print to console
                        System.out.println(bill);
                        psPayment.setString(5, bill);

                        int rows = psPayment.executeUpdate();
                        if (rows > 0) {
                            System.out.println("✅ Payment recorded successfully.");
                        } else {
                            System.out.println("❌ Payment failed. Car return aborted.");
                            return; // EXIT here if payment fails
                        }

                        String billFilePath = billing(bill, bookingId);
                        // Ask customer if they want the bill by email
                        while (true) {
                            System.out.print("📧 Do you want the bill emailed to you? (yes/no): ");
                            String choice = sc.nextLine().trim().toLowerCase();

                            if (choice.equals("yes")) {
                                System.out.print("Enter your email address: ");
                                String toEmail = sc.nextLine().trim();
                                if (!toEmail.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                                    System.out.println("❌ Invalid email: " + toEmail);
                                    System.out.println("Sorry we can't shere Bill");
                                    return;
                                }
                                // Send the bill as email
                                sendBillEmail(toEmail, billFilePath);
                                System.out.println("✅ Bill sent to your email.");
                                break;
                            } else if (choice.equals("no")) {
                                System.out.println("ℹ️ Bill not sent by email. You can still find it in the system.");
                                break;
                            } else {
                                System.out.println("Invalid input - write only yes or no");
                            }
                        }
                    }

                    // 7. Update booking status
                    String updateBooking = "DELETE FROM bokkings WHERE booking_id = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateBooking)) {
                        psUpdate.setInt(1, bookingId);
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
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error during return process: " + e.getMessage());
        }
    }
    String processPayment(double amount)
    {
        System.out.println("Choose Payment Method: ");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. UPI");
        System.out.print("Enter your choice: ");

        int choice = Integer.parseInt(sc.nextLine());
        while (true){
            switch (choice) {
                case 1:
                    System.out.println("Payment of ₹" + amount + " received via Cash. Thank you!");
                    return "cash";
                case 2:
                    String cardNumber;
                    while (true) {
                        System.out.print("Enter your 12-digit Card Number: ");
                        cardNumber = sc.nextLine();

                        if (cardNumber.matches("\\d{12}")) {
                            System.out.println("Processing card payment...");
                            System.out.println("Payment of ₹" + amount + " successful via Card. Thank you!");
                            break;
                        } else {
                            System.out.println("Invalid card number! Please enter a 12-digit number.");
                        }
                    }
                    return "card";
                case 3:
                    String upiId;
                    while (true) {
                        System.out.print("Enter your UPI ID (e.g., example@upi): ");
                        upiId = sc.nextLine().trim();

                        // Basic UPI ID validation without regex
                        if (upiId.contains("@") && upiId.indexOf('@') != 0 && upiId.indexOf('@') != upiId.length() - 1) {
                            System.out.println("Processing UPI payment...");
                            System.out.println("Payment of ₹" + amount + " successful via UPI. Thank you!");
                            break;
                        } else
                            System.out.println("Invalid UPI ID! Enter a valid format (e.g., example@upi).");
                    }
                    return "upi";
                default:
                    System.out.println("Invalid choice. Please try again.");
                    // Retry payment
            }
        }
    }
    String billing( String bill, int bookingId) {

        // Save this bill as its own file and RETURN the path (e.g., Bill_123.txt)
        String singleBillPath = "Bill_" + bookingId + ".txt";
        try (FileWriter fw = new FileWriter(singleBillPath)) {
            fw.write(bill);
        } catch (IOException e) {
            System.out.println("❌ Error saving single bill file: " + e.getMessage());
        }

        return singleBillPath;
    }
    public void sendBillEmail(String toEmail, String billFilePath) {
        final String fromEmail = "prithvitpatel@gmail.com";
        final String password  = "zsfm thja pida jfia";

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
