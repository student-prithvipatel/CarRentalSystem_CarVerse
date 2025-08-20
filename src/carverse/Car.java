package carverse;

public class Car {
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
