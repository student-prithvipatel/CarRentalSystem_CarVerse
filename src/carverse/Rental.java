package carverse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Rental {
    public static Map<String, Double> costCalculator(
            LocalDateTime start,
            LocalDateTime expectedEnd,
            LocalDateTime actualReturn,
            double pricePerHour,
            double lateFeePerHour) {

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
