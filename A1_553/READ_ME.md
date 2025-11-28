**README.txt**

**Transportation Fleet Management System & Fleet Highway Simulator**

This Java program includes two main components:

1. **Fleet Management System (CLI)** - A command-line based transportation fleet management system demonstrating key Object-Oriented Programming (OOP) principles. The system manages a diverse fleet of land, air, and water vehicles, handling operations like route planning, cargo and passenger management, and maintenance tracking.

2. **Fleet Highway Simulator (GUI)** - A graphical simulation system (Assignment 3) that demonstrates multithreading, race conditions, and synchronization in Java using AWT/Swing.





**How to Compile and Run**

To compile and run this project, you will need a Java Development Kit (JDK) installed and configured on your system.

Arrange Files: Place all .java files in their respective package directories (abstractclasses, exceptions, interfaces, vehicles,management ) inside a common source folder(transportation). The Main.java and file.csv should be at the root of this folder structure.









**1. Compile the source files:**



Bash

REM 1. Exceptions

javac -d . transportation/exceptions/InvalidOperationException.java

javac -d . transportation/exceptions/InsufficientFuelException.java

javac -d . transportation/exceptions/OverloadException.java





REM 2. Interfaces

javac -d . transportation/interfaces/FuelConsumable.java

javac -d . transportation/interfaces/PassengerCarrier.java

javac -d . transportation/interfaces/CargoCarrier.java

javac -d . transportation/interfaces/Maintainable.java





REM 3. Abstract classes

javac -d . transportation/abstractclasses/Vehicle.java

javac -d . transportation/abstractclasses/AirVehicle.java

javac -d . transportation/abstractclasses/LandVehicle.java

javac -d . transportation/abstractclasses/WaterVehicle.java





REM 4. Concrete vehicles

javac -d . transportation/vehicles/Airplane.java

javac -d . transportation/vehicles/Car.java

javac -d . transportation/vehicles/Truck.java

javac -d . transportation/vehicles/Bus.java

javac -d . transportation/vehicles/CargoShip.java





REM 5. FleetManager

javac -d . transportation/management/FleetManager.java



REM 6. utility

javac -d . transportation/utility/MaxSpeedComparator.java

javac -d . transportation/utility/ModelNameComparator.java

javac -d . transportation/utility/EfficiencyComparator.java

javac -d . transportation/utility/TotalMileageComparator.java



REM 7. Main

javac Main.java







**2. Run the Main class:**

**Bash**

**java Main**



The program will launch a menu-driven CLI, and a demo simulation will run automatically upon startup to showcase the core functionalities.

 **Demonstrating OOP Principles**

This assignment heavily emphasizes core OOP concepts, as outlined below:

      **\* Inheritance:** The program utilizes a multi-level class hierarchy starting with the Vehicle abstract class. LandVehicle, AirVehicle, and WaterVehicle inherit from Vehicle, and concrete classes like Car, Truck, and Bus inherit from LandVehicle. This structure allows for code reuse and the creation of a well-organized hierarchy. For example, Car automatically inherits the id, model, maxSpeed, and currentMileage properties from Vehicle.



      **\* Polymorphism:** This principle is applied extensively, particularly in the FleetManager class. Methods like startAllJourneys and getTotalFuelConsumption leverage polymorphism by calling the move and consumeFuel methods on a generic Vehicle object. This invokes the specific, overridden method for each vehicle type (e.g., "Driving on road..." for a Car, "Flying at \[maxAltitude]..." for an Airplane), allowing for a single method call to produce varied, type-specific behaviors.





      **\* Abstract Classes:** The Vehicle class serves as the root of the hierarchy, defining a common interface with abstract methods like move and calculateFuelEfficiency. This ensures that all subclasses are forced to provide their own unique implementation of these fundamental behaviors. The abstract classes LandVehicle, AirVehicle, and WaterVehicle further refine this structure by adding type-specific properties and overriding methods like estimateJourneyTime with specific journey time adjustments.





      **\* Interfaces:** Interfaces are used to define capabilities that can be shared across different vehicle types, regardless of their inheritance tree. Interfaces like FuelConsumable, CargoCarrier, and Maintainable are used to add modular, multiple behaviors to different vehicle types. For instance, a Car implements FuelConsumable and PassengerCarrier, while a CargoShip implements CargoCarrier. This design allows for flexible and reusable code, as the FleetManager can interact with vehicles based on their capabilities (e.g., refueling all FuelConsumable vehicles) without needing to know their specific class type.



**How to Use the CLI**

Upon running the program, you will be presented with the following menu:

$$$$$ FLEET MANAGEMENT SYSTEM $$$$$

1\. Add Vehicle

2\. Remove Vehicle

3\. Start Journey (All Vehicles)

4\. Refuel All Vehicles

5\. Perform Maintenance (Needed Only)

6\. Embark Passengers (by ID)

7\. Disembark Passengers (by ID)

8\. Load Cargo (by ID)

9\. Unload Cargo (by ID)

10\. Generate Full Report

11\. List Vehicles Needing Maintenance

12\. Calculate Total Fuel Consumption

13\. Search by Type / Interface



14\. Save Fleet to CSV

15\. Load Fleet from CSV



16\. List Distinct Vehicle Models

17\. Sort Fleet

18\. Launch Highway Simulator (GUI)

19\. Exit System





Using the Menu

      1. Enter the number of the action you want and press Enter.



      2. Follow the on-screen prompts to provide details (e.g., vehicle type, ID, model, fuel, passengers, cargo).



      3. The program validates your input to ensure it’s correct.







**Sorting and Ordering**



The system supports FOUR distinct sorting criteria using Java's built-in Collections.sort() method and custom Comparator classes:



By Efficiency (Descending): Uses the custom EfficiencyComparator.



By Max Speed (Descending): Uses the custom MaxSpeedComparator.



By Model Name (Alphabetical): Uses the custom ModelNameComparator.

By Mileage : Uses the custom TotalMileageComparator.



The Collections.max() and Collections.min() utilities are used in the report methods to find the fastest and least efficient vehicles, respectively.







 **Persistence Demo**

The Main class includes a demo that showcases the persistence functionality. A sample fleet is created and then saved to a file named fleet\_data.csv. The Save Fleet and Load Fleet options in the CLI allow you to manually save the current fleet state and reload a previously saved one.

To test this feature:

            **1. Create a sample fleet:**

            \* Run the application by running java Main.

            \* From the main menu, choose option 1 (Add Vehicle).

            \* Follow the prompts to add a few different vehicles (e.g., a Car, a Truck, and an Airplane). This will create your own custom fleet in memory.

            \* You can then choose option 6 (Generate Report) to see a summary of the vehicles you just added.

            **2. Save the fleet:**

            \* Choose option (Save Fleet) from the main menu.

            \* The program will prompt you to enter a filename. Type something like my\_fleet\_backup.csv.

            \* The system will save the current state of the fleet, including the vehicles you just added, to this file.

            **3. Simulate a program restart or data loss:**

            \* To prove the data can be reloaded, you need to clear the current fleet from memory. The easiest way to do this is to simply exit the application and then restart it.

            \* Run java Main again. The new session will start with an empty fleet.

            **4. Load the saved fleet**:

            \* Choose option (Load Fleet) from the new main menu.

            \* The program will again prompt you for a filename. Enter the exact filename you used before, e.g., my\_fleet\_backup.csv.

            \* The system will read the data from this file, recreate the vehicle objects (using a factory method), and populate the fleet with the saved vehicles.

            **5. Verify the loaded data:**

            \* Finally, choose option (Generate Report) to view the fleet summary. You should see the exact same list of vehicles you added in step 1, confirming that the persistence mechanism successfully saved and reloaded your data.



**DEMO FLEET SAVED IN START:**

**Type,Id,Model,MaxSpeed,TotalMileage,MileageSinceMaintenance,Field1,Field2,Field3,Field4,Field5**

**Car,C001,Toyota Camry,180.0,12000.0,2000.0,4,50.0,2,false**

**Truck,T002,Volvo,110.0,0.0,0.0,10,100.0,3000.0,false**

**Bus,B003,Mercedes,140.0,0.0,0.0,6,0.0,0.0,0,false**

**Airplane,A004,Boeing 747,900.0,0.0,0.0,12000.0,500.0,0.0,0,false**

**CargoShip,S005,Titanic,35.0,0.0,0.0,false,0.0,2100.0,false**

**Car,C006,Toyota,200.0,0.0,0.0,4,0.0,0,false**



---

## Assignment 3: Fleet Highway Simulator (GUI)

### Overview

The Fleet Highway Simulator is a graphical application built using Java Swing that demonstrates:
- **Multithreaded Execution**: Multiple vehicles operate in their own threads
- **Shared Resource Management**: A Highway Distance Counter that all vehicles update
- **Race Condition Demonstration**: Shows data inconsistency from unsynchronized access
- **Synchronization Fix**: Uses ReentrantLock to ensure thread-safe operations
- **Integration with Fleet Management System**: Can use vehicles from Assignment 1-2

### Integration with Fleet Management System

The Highway Simulator is now integrated with the Fleet Management System (Assignments 1-2). This integration allows you to:

1. **Launch the simulator directly from the CLI**: Use menu option 18 in the Fleet Management System
2. **Use your actual fleet vehicles**: The simulator can run with vehicles from your managed fleet
3. **Synchronized mileage updates**: Mileage accumulated in the simulator is reflected in the fleet vehicles

#### How the Integration Works

- **FleetVehicleAdapter**: A new adapter class wraps Fleet Management `Vehicle` objects for use in the simulation
- **Bidirectional Updates**: When a vehicle travels in the simulation, its mileage is updated in both the simulation and the original fleet vehicle
- **Fuel Synchronization**: Refueling in the simulation also refuels the underlying fleet vehicle (if it supports fuel operations)

#### Launching the Integrated Simulator

From the Fleet Management CLI:
1. Run the Fleet Management System (`java Main`)
2. Select option **18. Launch Highway Simulator (GUI)**
3. Choose whether to use fleet vehicles or demo vehicles
4. The simulator will open in a new window

### Design and GUI Layout

The GUI consists of:

1. **Title Panel**: Blue header with application title
2. **Highway Distance Counter Panel**: 
   - Large display showing total distance traveled by all vehicles
   - Expected increments counter for comparison
   - Race condition status indicator
   - Synchronization status display
3. **Vehicle Status Panel**: Shows each vehicle with:
   - Vehicle name and ID
   - Current mileage
   - Fuel level (with progress bar)
   - Operational status (Running/Paused/Out-of-Fuel/Stopped)
   - Refuel button
4. **Control Panel**:
   - Start, Pause, Resume, Stop, Reset buttons
   - Synchronization toggle checkbox

### How to Compile and Run the Highway Simulator

```bash
# Navigate to the A1_553 directory
cd A1_553

# Option 1: Compile and run everything (recommended - includes integration)
javac -d . transportation/exceptions/*.java transportation/interfaces/*.java transportation/abstractclasses/*.java transportation/vehicles/*.java transportation/utility/*.java transportation/management/FleetManager.java simulation/*.java Main.java

# Run the Fleet Management System (includes simulator launch option 18)
java Main

# Option 2: Run the standalone simulator (demo vehicles only)
java simulation.FleetHighwaySimulator
```

### Simulation Features

**Vehicle Simulation:**
- 3 vehicles run concurrently (Toyota Camry, Honda Civic, Ford Mustang)
- Each vehicle travels approximately 1 km per second
- Fuel is consumed as vehicles travel
- Vehicles pause when out of fuel until refueled

**GUI Controls:**
- **Start**: Begins the simulation, all vehicles start traveling
- **Pause**: Pauses all vehicles temporarily
- **Resume**: Resumes paused vehicles
- **Stop**: Stops the simulation completely
- **Reset**: Resets all vehicles to initial state
- **Refuel**: Individual refuel buttons for each vehicle

**How Simulation Threads are Controlled via GUI:**

The simulation uses the following threading pattern:
1. Each vehicle runs in its own `VehicleThread` (extends Thread)
2. The GUI is updated using a Swing Timer on the Event Dispatch Thread (EDT)
3. Thread control uses:
   - `volatile boolean` flags for running/paused state
   - `Object` lock for pause/resume synchronization
   - `Thread.interrupt()` for clean shutdown

### Race Condition Demonstration and Fix

**Step 1: Unsynchronized Access (Default Mode)**

When the "Enable Synchronization" checkbox is **unchecked**, the shared `HighwayDistanceCounter` is updated without locks:

```java
// UNSYNCHRONIZED - Causes race condition!
int current = totalDistance;
Thread.sleep(1);  // Delay increases chance of race condition
totalDistance = current + amount;
```

Running the simulation in this mode will show:
- The total distance counter does not match the sum of individual vehicle mileages
- Warning indicator: "⚠ RACE CONDITION DETECTED!"
- Counter discrepancy grows over time

**Step 2: Synchronized Access (Fix)**

When the "Enable Synchronization" checkbox is **checked**, the counter uses `ReentrantLock`:

```java
lock.lock();
try {
    totalDistance += amount;  // Thread-safe
} finally {
    lock.unlock();
}
```

Running with synchronization enabled shows:
- Counter consistently matches sum of vehicle mileages
- Success indicator: "✓ Counter Consistent"

### GUI Thread-Safety Considerations

**Event Dispatch Thread (EDT):**
- All GUI updates occur on the EDT using `SwingUtilities.invokeLater()`
- A Swing Timer (updating every 100ms) ensures thread-safe GUI updates
- Vehicle state is read using synchronized methods in `SimulatedVehicle`

**Thread-Safe Patterns Used:**
1. `volatile` keywords for shared boolean flags
2. `synchronized` methods in `SimulatedVehicle` for state access
3. `ReentrantLock` in `HighwayDistanceCounter` for shared counter
4. `Object.wait()/notifyAll()` for pause/resume control

### Class Structure

```
simulation/
├── HighwayDistanceCounter.java  - Shared counter (demonstrates race condition)
├── SimulatedVehicle.java        - Vehicle model with state management
├── VehicleThread.java           - Thread implementation for each vehicle
├── FleetVehicleAdapter.java     - Adapter for Fleet Management integration
└── FleetHighwaySimulator.java   - Main GUI application
```

### Screenshots Expected Behavior

**Race Condition (Unsynchronized):**
- Counter shows values like "45 km" while sum of mileages is "48 km"
- Red warning indicator appears
- Values diverge more as simulation runs longer

**Correct Behavior (Synchronized):**
- Counter exactly matches sum of individual mileages
- Green checkmark indicator
- Consistent throughout simulation


