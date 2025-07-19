import java.sql.*;
import java.util.Scanner;

public class CarVerse {
    public static void main(String[] args) throws SQLException {
        Scanner sc=new Scanner(System.in);
        Connection conn = DBConnect.getConnection();
        Admin admin=new Admin();
        System.out.println("1. Admin login");
        System.out.println("2. User login");
        System.out.println("3. User registration");
        System.out.println("Enter choice from 1 to 3");
        int choice= sc.nextInt();
        switch (choice){
            case 1:
                admin.adminLogin();
                break;
            case 2:
                break;
            case 3:
                break;
        }
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

}
class Buyer{

}
class Admin{
    Scanner sc=new Scanner(System.in);
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
//            Menu.adminMenu();  // continue with admin functions
        } else {
            System.out.println("❌ Invalid username or password.");
        }
    }
}
class Rental{

}