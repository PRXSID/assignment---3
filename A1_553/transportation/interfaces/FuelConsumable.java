package transportation.interfaces;
import transportation.exceptions.*;

public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException;
}