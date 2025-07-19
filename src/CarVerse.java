import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

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
                    break;
                case 3:
                    customer.customerRegistartion();
                    break;
                case 4:
                    System.out.println("Good Bye>>>");
            }
        }while(choice!=4)
    }
    static void adminMenu()throws SQLException{
        int choice;
        do{
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Car");
            System.out.println("2. View All Cars");
            System.out.println("3. Update Car Details");
            System.out.println("4. View Available Cars");
            System.out.println("5. Update Car Status");
            System.out.println("6. View All Rentals");
            System.out.println("7. View Overdue Rentals");
            System.out.println("8. Generate Reports");
            System.out.println("9. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            switch (choice) {
                case 1 :
                    admin.addCar();
                break;
                case 2 :
                    break;
                case 3 :
                    break;
                case 4 :
                    break;
                case 5 :
                    break;
                case 6 :
                    break;
                case 7 :
                    break;
                case 8 :
                    break;
                case 9 :
                    System.out.println("Admin logged out.");
                    break;
                default : System.out.println("Invalid choice.");
            }
        }while (choice!=9);
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
    int id;
    String model;
    String brand;
    double pricePerKm;
    String availability;

    public Car(int id, String model, String brand, double pricePerKm, String availability) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.pricePerKm = pricePerKm;
        this.availability = availability;
    }
}
class Customer {
    Scanner sc = new Scanner(System.in);
    void customerRegistartion() throws SQLException {
        Connection conn = DBConnect.getConnection();
        Scanner sc=new Scanner(System.in);
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
        System.out.println("Enter date of birth in dd-mm-yyyy format");
        String dob=sc.nextLine();
        PreparedStatement ps=conn.prepareStatement("INSERT INTO customer(name, email, phone_no, address, dob) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone);
        ps.setString(4, address);
        ps.setString(5, dob);
        ps.executeUpdate();
        System.out.println("✅ Registration successful!");
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
            Menu.adminMenu();  // continue with admin functions
        } else {
            System.out.println("❌ Invalid username or password.");
        }
    }
    void addCar()throws SQLException{
        Connection conn = DBConnect.getConnection();
        String sql = "INSERT INTO car (model, brand, price_per_km, availability) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        System.out.println("\n=== Add New Car ===");
        System.out.print("Enter car model: ");
        String model = sc.nextLine();
        pst.setString(1, model);
        System.out.print("Enter car brand: ");
        String brand = sc.nextLine();
        pst.setString(2, brand);
        System.out.print("Enter price per km: ");
        double pricePerKm = sc.nextDouble();
        pst.setDouble(3, pricePerKm);
        String availability = "Available";
        pst.setString(4, availability);
        if (pst.executeUpdate() > 0) {
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1); // get auto-generated ID

                // Create Car object and store in HashMap
                Car car = new Car(id, model, brand, pricePerKm, availability);
                carMap.put(id, car);

                System.out.println("✅ Car added with ID: " + id);
            }
        } else {
            System.out.println("❌ Failed to add car.");
        }
    }
}
class Rental{

}
