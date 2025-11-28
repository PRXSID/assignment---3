package transportation.utility;

import java.util.Comparator;
import transportation.abstractclasses.Vehicle;


public class ModelNameComparator implements Comparator<Vehicle> {
    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        return v1.getModel().compareToIgnoreCase(v2.getModel());
    }
}
