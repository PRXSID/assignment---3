package transportation.abstractclasses;

import transportation.exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {
    private String id, model;
    private double maxSpeed;
    protected double totalMileage;
    protected double mileageSinceMaintenance;

    public Vehicle(String id, String model, double maxSpeed) throws InvalidOperationException {
        if (id == null || id.isBlank()) {
            throw new InvalidOperationException("ID cannot be empty");
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.totalMileage = 0;
        this.mileageSinceMaintenance = 0;
    }

    public String getId() { return id; }
    public String getModel() { return model; }
    public double getMaxSpeed() { return maxSpeed; }

    public double getTotalMileage() { return totalMileage; }
    public double getMileageSinceMaintenance() { return mileageSinceMaintenance; }

    protected void addMileage(double distance) {
        totalMileage += distance;
        mileageSinceMaintenance += distance;
    }

    public void resetMileageSinceMaintenance() {
        mileageSinceMaintenance = 0;
    }

    public void setMileage(double total, double sinceMaintenance) {
        totalMileage = total;
        mileageSinceMaintenance = sinceMaintenance;
    }

    public void displayInfo() {
        System.out.println(
            "ID: " + id +
            ", Model: " + model +
            ", Max Speed: " + maxSpeed + " km/h" +
            ", Total Mileage: " + totalMileage + " km" +
            ", Mileage Since Maintenance: " + mileageSinceMaintenance + " km"
        );
    }

    public abstract void move(double distance) throws Exception;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);

    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(
            this.calculateFuelEfficiency(),
            other.calculateFuelEfficiency()
        );
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%.1f,%.1f,%.1f",
            id, model, maxSpeed, totalMileage, mileageSinceMaintenance);
    }
}
