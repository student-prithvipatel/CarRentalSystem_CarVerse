import java.sql.*;
import java.util.Scanner;

public class CarVerse {
    public static void main(String[] args) throws SQLException {
        Scanner sc=new Scanner(System.in);
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/carrental","root","");
        System.out.println((con!= null)?"success":"failed");
        System.out.println("1. Admin login");
        System.out.println("2. User login");
        System.out.println("3. User registration");
        System.out.println("Enter choice from 1 to 3");
        int choice= sc.nextInt();
        switch (choice){
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
}
class Car{

}
class User{
    void adminLogin(){

    }
}
class Buyer extends User{

}
class Admin extends User{

}
class Rental{

}