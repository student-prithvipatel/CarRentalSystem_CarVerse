package carverse.payment;

import carverse.main.CarVerse;       // for getIntInput(...)
import java.util.Scanner;

public final class PaymentFactory {
    private PaymentFactory() {}

    /**
     * Prompts the user, constructs the right Payment, runs it,
     * and returns the method name ("cash" | "card" | "upi") if success.
     * Returns null if the user cancels / fails.
     */
    public static String chooseAndPay(Scanner sc, double amount) {
        while (true) {
            System.out.println("Choose Payment Method: ");
            System.out.println("1. Cash");
            System.out.println("2. Card");
            System.out.println("3. UPI");
            System.out.print("Enter your choice: ");

            int choice = CarVerse.getIntInput(1, 3);

            Payment method;
            switch (choice) {
                case 1:
                    method = new CashPayment();
                    break;
                case 2:
                    method = new CardPayment();
                    break;
                case 3:
                    method = new UpiPayment();
                    break;
                default:
                    method = null;
            }

            if (method == null) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            boolean ok = method.pay(amount, sc);
            if (ok) {
                return method.name();
            }

            // optional retry/cancel
            System.out.print("❌ Payment failed. Try again? (yes/no): ");
            String ans = sc.nextLine().trim().toLowerCase();
            if (!ans.equals("yes")){
                return null;
            }
        }
    }
}
