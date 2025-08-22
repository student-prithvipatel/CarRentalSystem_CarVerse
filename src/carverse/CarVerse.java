package carverse;

import java.sql.*;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CarVerse {
    static Scanner sc = new Scanner(System.in);
    static Admin admin = new Admin();
    static Customer customer = new Customer();

    public static void main(String[] args) throws SQLException {
        int choice=-1;
        do {
            System.out.println();
            System.out.println("<><><> Welcome to CarVerse <><><>");
            System.out.println();
            System.out.println("1. Admin login");
            System.out.println("2. User login");
            System.out.println("3. User registration");
            System.out.println("4. Exit");
            System.out.println();
            System.out.print("Enter choice from 1 to 4: ");

            String input = sc.nextLine();  // always read as string
            try {
                choice = Integer.parseInt(input); // convert to int
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
                        System.out.println("Good Bye >>>");
                        break;
                    default:
                        System.out.println("❌ Invalid choice! Please enter a number between 1 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter only integers between 1 and 4.");
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
            choice = getIntInput(1, 11);
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
                    admin.viewAvailableCars();
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
        } while (choice != 11);
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
            System.out.println("7. Update Profile");
            System.out.println("8. Logout");
            System.out.print("Enter choice: ");
            choice = getIntInput(1, 8);

            switch (choice) {
                case 1:
                    admin.viewAvailableCars();
                    break;
                case 2:
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
                    customer.updateProfile();
                    break;
                case 8:
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 8);
    }

    // Utility method to safely read int
    static int getIntInput(int min, int max) {
        while (true) {
            try {
                int choice = sc.nextInt();
                sc.nextLine(); // clear buffer
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println("❌ Please enter a number between " + min + " and " + max);
                    System.out.println("Enter your choice");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                sc.nextLine();// clear wrong input
                System.out.println("Enter your choice ");
            }
        }
    }
    static {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = """
                UPDATE bookings
                SET status = 'Overdue'
                WHERE status = 'Booked'
                  AND end_datetime < NOW()
            """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Welcome to Car Verse");
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to update overdue bookings: " + e.getMessage());
        }
    }
}
