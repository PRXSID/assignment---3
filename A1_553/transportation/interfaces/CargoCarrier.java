package transportation.interfaces;
import transportation.exceptions.*;

public interface CargoCarrier {
    void loadCargo(double weight) throws OverloadException, InvalidOperationException;
    void unloadCargo(double weight) throws InvalidOperationException;
    double getCargoCapacity();
    double getCurrentCargo();
}