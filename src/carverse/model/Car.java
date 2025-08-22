package carverse.model;

public class Car {
    public int car_id;
    public String model;
    public String brand;
    public String type;
    public double pricePerHour;
    public boolean availability;
    public int seats;

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
