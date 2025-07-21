
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.time.LocalDateTime;

public class CarVerse {
    static Scanner sc=new Scanner(System.in);
    static Admin admin=new Admin();
    static Customer customer=new Customer();
    public static void main(String[] args) throws SQLException {
        int choice;
        do{
            System.out.println("1. Admin login");
            System.out.println("2. User login");
            System.out.println("3. User registration");
            System.out.println("4. Exit");
            System.out.println("Enter choice from 1 to 4");
            choice= sc.nextInt();
            switch (choice){
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
        }while(choice!=4);
    }
    static void adminMenu()throws SQLException{
        int choice;
        do{
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
            System.out.println("10. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1 :
                    admin.addCar();
                    break;
                case 2 :
                    admin.viewAllCars();
                    break;
                case 3 :
                    admin.updateCarDetails();
                    break;
                case 4 :
                    admin.viewAvailableCars();
                    break;
                case 5 :
                    admin.updateCarAvailability();
                    break;
                case 6 :
                    admin.viewCurrentlyRentedCars();
                    break;
                case 7 :
                    admin.viewOverdueRentals();
                    break;
                case 8 :
                    admin.generateReports();
                    break;
                case 9 :
                    admin.removeCar();
                    break;
                case 10 :
                    System.out.println("Admin logged out.");
                    break;
                default : System.out.println("Invalid choice.");
            }
        }while (choice!=10);
    }
    static void customerMenu() throws SQLException {
        int choice;
        do {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. Search Available Cars");
            System.out.println("2. Book a Car");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Rate a Car");
            System.out.println("6. Logout");
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
                    System.out.println("**************************");
                    System.out.println();
                    System.out.println("Enter car Id to book");
                    int c_id= sc.nextInt();
                    customer.bookCar(c_id);
                    break;
                case 3:
                    customer.viewMyBookings();
                    break;
                case 4:
                    customer.cancelBooking();
                    break;
                case 5:
                    customer.giveRating();
                    break;
                case 6:
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 6);
    }

}
class DBConnect {

    static final String URL = "jdbc:mysql://localhost:3306/carrental"; // replace with your DB name
    static final String USER = "root";       // replace with your DB username
    static final String PASSWORD = "";   // replace with your DB password

    // This method will give you the connection wherever you call it
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
class Car{
    int car_id;
    String model;
    String brand;
    String type;
    double pricePerHour;
    boolean availability;
    int seats;

    public Car(int car_id, String model, String brand,String type, double pricePerHour, boolean availability,int seats) {
        this.car_id= car_id;
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
    Map<String, String> customerPasswordMap = new HashMap<>();

    void customerRegistartion() throws SQLException {
        Connection conn = DBConnect.getConnection();
        System.out.print("Enter name: ");
        String name=sc.nextLine();
        String email;
        while (true) {
            System.out.print("Enter email (must be lowercase and contain '@'): ");
            email=sc.nextLine().toLowerCase();
            if (email.contains("@") && email.equals(email.toLowerCase()) && email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                break;
            } else {
                System.out.println("❌ Invalid email. Try again.");
            }
        }
        String password;
        while (true) {
            System.out.print("Enter password (min 8 chars, at least 1 special character): ");
            password=sc.nextLine();
            boolean isLengthValid=password.length() >= 8;
            boolean hasSpecialChar=password.matches(".*[^a-zA-Z0-9].*");
            if (!isLengthValid || !hasSpecialChar) {
                System.out.println("❌ Password must be at least 8 characters and contain at least one special character.");
                continue;
            }
            System.out.print("Confirm password: ");
            String confirmPassword=sc.nextLine();
            if (password.equals(confirmPassword)) {
                break;
            } else {
                System.out.println("❌ Passwords do not match. Please try again.");
            }
        }
        String phone;
        while (true) {
            System.out.print("Enter 9-digit phone number (not starting with 0): ");
            phone=sc.nextLine();
            if (phone.matches("^[1-9][0-9]{8}$")) {
                break;
            } else {
                System.out.println("❌ Invalid phone number. Try again.");
            }
        }
        System.out.print("Enter address: ");
        String address=sc.nextLine();
        LocalDate dob;
        while (true) {
            System.out.print("Enter date of birth (dd-MM-yyyy): ");
            String dobInput = sc.nextLine();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                dob = LocalDate.parse(dobInput, formatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use dd-MM-yyyy.");
            }
        }
        PreparedStatement ps=conn.prepareStatement("INSERT INTO customer(name, email, phone_no, address, dob) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone);
        ps.setString(4, address);
        ps.setDate(5, Date.valueOf(dob));
        ps.executeUpdate();
        System.out.println("✅ Registration successful!");
        customerPasswordMap.put(email, password);
        customerPasswordMap.put(phone, password);
    }
    void customerLogin() throws SQLException {
        Connection conn = DBConnect.getConnection();
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

                // Check registration from DB
                String query = "SELECT * FROM customer WHERE email = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, input);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("❌ No account found with this email. Please register first.");
                } else if (!customerPasswordMap.containsKey(input)) {
                    System.out.println("❌ Password not found in memory. Please re-register.");
                } else {
                    System.out.print("Enter password: ");
                    password = sc.nextLine();

                    if (customerPasswordMap.get(input).equals(password)) {
                        System.out.println("✅ Login successful! Welcome, " + rs.getString("name") + "!");
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

                // Check registration from DB
                String query = "SELECT * FROM customer WHERE phone_no = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, input);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("❌ No account found with this phone number. Please register first.");
                } else if (!customerPasswordMap.containsKey(input)) {
                    System.out.println("❌ Password not found in memory. Please re-register.");
                } else {
                    System.out.print("Enter password: ");
                    password = sc.nextLine();
                    if (customerPasswordMap.get(input).equals(password)) {
                        System.out.println("✅ Login successful! Welcome, " + rs.getString("name") + "!");
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
    }
    void bookCar(int customerId) throws SQLException {
        Connection conn = DBConnect.getConnection();
        System.out.print("Enter Car ID to book: ");
        int carId = sc.nextInt();
        sc.nextLine();
        // Check if car exists and is available
        String checkCar = "SELECT price_per_hour, is_available FROM cars WHERE car_id = ?";
        PreparedStatement psCheck = conn.prepareStatement(checkCar);
        psCheck.setInt(1, carId);
        ResultSet rs = psCheck.executeQuery();
        if (!rs.next() || !rs.getBoolean("is_available")) {
            System.out.println("❌ Car not available or doesn't exist.");
            return;
        }
        double pricePerHour = rs.getDouble("price_per_hour");

        //Collect trip details
        System.out.print("Enter Start Location: ");
        String startLoc = sc.nextLine();
        System.out.print("Enter End Location: ");
        String endLoc = sc.nextLine();

        System.out.print("Enter Start Date & Time (yyyy-MM-dd HH:mm): ");
        String startInput = sc.nextLine();
        System.out.print("Enter End Date & Time (yyyy-MM-dd HH:mm): ");
        String endInput = sc.nextLine();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startInput, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endInput, formatter);

        if (endDateTime.isBefore(startDateTime)) {
            System.out.println("❌ End time must be after start time.");
            return;
        }

        // Call Rental.costCalculator()
        Map<String, Double> costData = Rental.costCalculator(startDateTime, endDateTime, pricePerHour);
        double hours = costData.get("hours");
        double totalCost = costData.get("totalCost");

        //Insert into bookings table
        String insertBooking = "INSERT INTO bookings (customer_id, car_id, start_location, end_location, start_datetime, end_datetime, total_hours, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement psInsert = conn.prepareStatement(insertBooking);
        psInsert.setInt(1, customerId);
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

    void viewMyBookings()
    {

    }
    void cancelBooking()
    {

    }

    public void giveRating() throws SQLException {
        Connection conn = DBConnect.getConnection();
        System.out.print("Enter Car ID to rate: ");
        int carId = sc.nextInt();
        System.out.print("Enter rating (1 to 5): ");
        int rating = sc.nextInt();
        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating. Must be 1–5.");
            return;
        }
        String query = "UPDATE cars SET rating = ? WHERE car_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, rating);
        ps.setInt(2, carId);
        int r = ps.executeUpdate();
        if (r > 0)
            System.out.println("Rating submitted successfully.");
        else
            System.out.println("Failed to rate car.");
    }

}
class Admin{
    Scanner sc=new Scanner(System.in);
    HashMap<Integer, Car> carMap = new HashMap<>();
    void adminLogin()throws SQLException{
        Connection conn = DBConnect.getConnection();
        String query = "SELECT * FROM admin WHERE adminname = ? AND password = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        System.out.print("Enter admin username: ");
        pst.setString(1, sc.nextLine());
        System.out.print("Enter password: ");
        pst.setString(2, sc.nextLine());

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            System.out.println("✅ Admin login successful!");
            CarVerse.adminMenu();  // continue with admin functions
        } else {
            System.out.println("❌ Invalid username or password.");
        }
    }
    void addCar()throws SQLException{
        Connection conn = DBConnect.getConnection();
        String sql = "INSERT INTO car (model, brand, type, price_per_hour, availability, seats) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
        boolean availability = true;
        pst.setBoolean(5, availability);
        System.out.println("Enter seats: ");
        int seats= sc.nextInt();
        pst.setInt(6,seats);
        if (pst.executeUpdate() > 0) {
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1); // get auto-generated ID

                // Create Car object and store in HashMap
                Car car = new Car(id, model, brand,type, pricePerHour,availability,seats);
                carMap.put(id, car);

                System.out.println("✅ Car added with ID: " + id);
            }
        } else {
            System.out.println("❌ Failed to add car.");
        }
    }
    void viewAllCars() throws SQLException{
        Connection conn = DBConnect.getConnection();
        String query = "SELECT * FROM car";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("\n=============================");
        System.out.println("🚗  Car List");
        System.out.println("=============================");
        System.out.printf("%-5s %-12s %-10s %-10s %-6s %-14s %-12s\n", "ID", "Model", "Brand", "Type", "Seats", "Price/Hour", "Status");
        System.out.println("---------------------------------------------------------------------");

        while (rs.next()) {
            int id = rs.getInt("id");
            String model = rs.getString("model");
            String brand = rs.getString("brand");
            String type = rs.getString("type");
            int seats = rs.getInt("seats");
            double price = rs.getDouble("price_per_hour");

            // Convert boolean to human-readable
            boolean available = rs.getBoolean("availability");
            String status = available ? "Available" : "Booked";

            System.out.printf("%-5d %-12s %-10s %-10s %-6d ₹%-13.2f %-12s\n", id, model, brand, type, seats, price, status);
        }
    }
    void updateCarDetails() throws SQLException {
        Connection conn = DBConnect.getConnection();;
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
        String newValue = "";
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
        newValue = sc.nextLine();
        // Build and run update query
        String query = "UPDATE cars SET " + column + " = ? WHERE car_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        if (column.equals("price_per_hour"))
            ps.setDouble(1, Double.parseDouble(newValue));
        else if (column.equals("availability"))
            ps.setBoolean(1, Boolean.parseBoolean(newValue));
        else
            ps.setString(1, newValue);
        ps.setInt(2, carId);

        int r = ps.executeUpdate();
        System.out.println(r > 0 ? "Car updated successfully." : "Car update failed.");
    }
    void viewAvailableCars() throws SQLException{
        Connection conn = DBConnect.getConnection();
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM car WHERE availability = 1";
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("\n==============================");
        System.out.println("🚗  Available Cars for Rent");
        System.out.println("==============================");

        System.out.printf("%-5s %-12s %-10s %-10s %-6s %-14s\n",
                "ID", "Model", "Brand", "Type", "Seats", "Price/Hour");

        System.out.println("----------------------------------------------------------");

        boolean hasCars = false;
        while (rs.next()) {
            hasCars = true;
            int id = rs.getInt("id");
            String model = rs.getString("model");
            String brand = rs.getString("brand");
            String type = rs.getString("type");
            int seats = rs.getInt("seats");
            double price = rs.getDouble("price_per_hour");

            System.out.printf("%-5d %-12s %-10s %-10s %-6d ₹%-13.2f\n", id, model, brand, type, seats, price);
        }

        if (!hasCars) {
            System.out.println("❌ No cars are currently available.");
        }
    }
    void updateCarAvailability() throws SQLException {
        Connection conn = DBConnect.getConnection();
        System.out.print("Enter Car ID to update availability: ");
        int carId = sc.nextInt();
        sc.nextLine();

        // Check if car exists
        String checkQuery = "SELECT * FROM car WHERE id = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkQuery);
        checkPs.setInt(1, carId);
        ResultSet rs = checkPs.executeQuery();

        if (!rs.next()) {
            System.out.println("❌ Car ID not found.");
            return;
        }

        System.out.print("Set availability (true for available, false for not available): ");
        boolean availability = sc.nextBoolean();

        String updateQuery = "UPDATE car SET availability = ? WHERE id = ?";
        PreparedStatement updatePs = conn.prepareStatement(updateQuery);
        updatePs.setBoolean(1, availability);
        updatePs.setInt(2, carId);

        int rowsUpdated = updatePs.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("✅ Car availability updated successfully.");
            // Optional: update in local HashMap as well if you’re keeping it in memory
            if(carMap.containsKey(carId)) {
                carMap.get(carId).availability = availability;
            }
        } else {
            System.out.println("❌ Failed to update car availability.");
        }
    }
    void viewCurrentlyRentedCars() throws SQLException {
        Connection conn = DBConnect.getConnection();
        // Assumption: rental table exists with columns: rental_id, car_id, customer_id, rent_date, return_date -
        // Only rows with NULL return_date are considered "currently rented".
        String query =
                "SELECT r.rental_id, c.id AS car_id, c.model, c.brand, c.type, c.seats, c.price_per_hour, cu.name AS customer_name, cu.phone_no, r.rent_date " +
                        "FROM rental r " +
                        "JOIN car c ON r.car_id = c.id " +
                        "JOIN customer cu ON r.customer_id = cu.id " +
                        "WHERE r.return_date IS NULL";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\n==============================");
        System.out.println("🚘 Currently Rented Cars");
        System.out.println("==============================");
        System.out.printf("%-5s %-5s %-12s %-10s %-10s %-5s %-12s %-18s %-12s %-12s\n",
                "RId", "CID", "Model", "Brand", "Type", "Seat", "Price/Hour", "Customer Name", "Phone", "Rent Date");

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

            System.out.printf("%-5d %-5d %-12s %-10s %-10s %-5d ₹%-11.2f %-18s %-12s %-12s\n",
                    rentalId, carId, model, brand, type, seats, price, customerName, phone, rentDate);
        }
        if (!hasResults) {
            System.out.println("❌ No cars are currently rented out.");
        }
    }
    void viewOverdueRentals() throws SQLException {
        Connection conn = DBConnect.getConnection();
        // Query: Select all rentals not returned yet and overdue
        String query =
                "SELECT r.rental_id, c.id AS car_id, c.model, c.brand, cu.name AS customer_name, cu.phone_no, r.rent_date, r.due_date " +
                        "FROM rental r " +
                        "JOIN car c ON r.car_id = c.id " +
                        "JOIN customer cu ON r.customer_id = cu.id " +
                        "WHERE r.return_date IS NULL AND r.due_date < CURDATE()";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\n==============================");
        System.out.println("🚨 Overdue Rentals");
        System.out.println("==============================");
        System.out.printf("%-5s %-5s %-12s %-10s %-15s %-12s %-12s %-12s\n",
                "RId", "CID", "Model", "Brand", "Customer", "Phone", "Rent Date", "Due Date");

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

            System.out.printf("%-5d %-5d %-12s %-10s %-15s %-12s %-12s %-12s\n",
                    rentalId, carId, model, brand, customer, phone, rentDate, dueDate);
        }
        if (!found) {
            System.out.println("❌ No overdue rentals found.");
        }
    }
    void generateReports() throws SQLException {
        Connection conn = DBConnect.getConnection();

        // Total number of cars
        String totalCarsQuery = "SELECT COUNT(*) AS total_cars FROM car";
        Statement stmt1 = conn.createStatement();
        ResultSet rs1 = stmt1.executeQuery(totalCarsQuery);
        int totalCars = 0;
        if(rs1.next()) totalCars = rs1.getInt("total_cars");

        // Total rentals
        String totalRentalsQuery = "SELECT COUNT(*) AS total_rentals FROM rental";
        Statement stmt2 = conn.createStatement();
        ResultSet rs2 = stmt2.executeQuery(totalRentalsQuery);
        int totalRentals = 0;
        if(rs2.next()) totalRentals = rs2.getInt("total_rentals");

        // Currently available cars
        String availableCarsQuery = "SELECT COUNT(*) AS available FROM car WHERE availability = 1";
        Statement stmt3 = conn.createStatement();
        ResultSet rs3 = stmt3.executeQuery(availableCarsQuery);
        int availableCars = 0;
        if(rs3.next()) availableCars = rs3.getInt("available");

        // Currently rented cars
        String rentedCarsQuery = "SELECT COUNT(*) AS rented FROM rental WHERE return_date IS NULL";
        Statement stmt4 = conn.createStatement();
        ResultSet rs4 = stmt4.executeQuery(rentedCarsQuery);
        int rentedCars = 0;
        if(rs4.next()) rentedCars = rs4.getInt("rented");

        // Overdue rentals
        String overdueQuery = "SELECT COUNT(*) AS overdue FROM rental WHERE return_date IS NULL AND due_date < CURDATE()";
        Statement stmt5 = conn.createStatement();
        ResultSet rs5 = stmt5.executeQuery(overdueQuery);
        int overdueCount = 0;
        if(rs5.next()) overdueCount = rs5.getInt("overdue");

        // Total revenue (assumes you have a column 'total_price' in rental)
        String totalRevenueQuery = "SELECT IFNULL(SUM(total_price), 0) AS revenue FROM rental WHERE total_price IS NOT NULL";
        Statement stmt6 = conn.createStatement();
        ResultSet rs6 = stmt6.executeQuery(totalRevenueQuery);
        double totalRevenue = 0;
        if(rs6.next()) totalRevenue = rs6.getDouble("revenue");

        System.out.println("\n=========== REPORTS ===========");
        System.out.println("Total Number of Cars: " + totalCars);
        System.out.println("Total Number of Rentals: " + totalRentals);
        System.out.println("Currently Available Cars: " + availableCars);
        System.out.println("Currently Rented Cars: " + rentedCars);
        System.out.println("Overdue Rentals: " + overdueCount);
        System.out.printf("Total Revenue: ₹%.2f\n", totalRevenue);
        System.out.println("===============================\n");
    }
    void removeCar() throws SQLException {
        Connection conn = DBConnect.getConnection();
        System.out.print("Enter Car ID to remove: ");
        int carId = sc.nextInt();
        sc.nextLine();

        // Check if a car exists
        String checkQuery = "SELECT * FROM car WHERE id = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkQuery);
        checkPs.setInt(1, carId);
        ResultSet rs = checkPs.executeQuery();

        if (!rs.next()) {
            System.out.println("❌ Car ID not found.");
            return;
        }

        // Proceed to delete
        String deleteQuery = "DELETE FROM car WHERE id = ?";
        PreparedStatement deletePs = conn.prepareStatement(deleteQuery);
        deletePs.setInt(1, carId);

        int result = deletePs.executeUpdate();
        if (result > 0) {
            carMap.remove(carId); // Remove from HashMap if using in-memory as well
            System.out.println("✅ Car with ID " + carId + " removed successfully.");
        } else {
            System.out.println("❌ Failed to remove car. Please check Car ID.");
        }
    }
}
class Rental{
        public static Map<String, Double> costCalculator(LocalDateTime start, LocalDateTime end, double pricePerHour) {
            Map<String, Double> result = new HashMap<>();

            long hours = Duration.between(start, end).toHours();
            if (hours <= 0) hours = 1; // Minimum billing for 1 hour
            double totalCost = hours * pricePerHour;

            result.put("hours", (double) hours);
            result.put("totalCost", totalCost);

            return result;
        }
    }

