package transportation.utility;

import java.util.Comparator;
import transportation.abstractclasses.Vehicle;

public class TotalMileageComparator implements Comparator<Vehicle> {
    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        return Double.compare(v2.getTotalMileage(), v1.getTotalMileage());
    }
}

