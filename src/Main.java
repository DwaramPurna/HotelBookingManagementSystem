import java.util.HashMap;
import java.util.Map;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1, 2, and 3
 * @author DwaramPurna
 * @version 3.0
 */

// --- UC2: Domain Modeling (Inheritance & Abstraction) ---
abstract class Room {
    protected String roomType;
    protected double price;

    public Room(String roomType, double price) {
        this.roomType = roomType;
        this.price = price;
    }

    public abstract void displayDetails();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 2000.0); }
    @Override
    public void displayDetails() { System.out.println("Type: " + roomType + " | Price: " + price); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 3500.0); }
    @Override
    public void displayDetails() { System.out.println("Type: " + roomType + " | Price: " + price); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 6000.0); }
    @Override
    public void displayDetails() { System.out.println("Type: " + roomType + " | Price: " + price); }
}

// --- UC3: Centralized Inventory Management (HashMap) ---
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public void displayInventory() {
        System.out.println("\n--- UC3: Centralized Room Inventory ---");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Availability: " + entry.getValue());
        }
    }
}

// --- Main Application Entry Point ---
public class Main {
    public static void main(String[] args) {
        // UC1: Basic App Startup
        System.out.println("Welcome to Book My Stay App");
        System.out.println("Hotel Booking Management System Starting...");
        System.out.println("-------------------------------------------");

        // UC2: Room Object Initialization & Polymorphism
        System.out.println("--- UC2: Room Details ---");
        Room s = new SingleRoom();
        Room d = new DoubleRoom();
        Room st = new SuiteRoom();

        s.displayDetails();
        d.displayDetails();
        st.displayDetails();

        // UC3: Inventory Setup using HashMap
        RoomInventory hotelInventory = new RoomInventory();
        hotelInventory.addRoomType("Single Room", 5);
        hotelInventory.addRoomType("Double Room", 3);
        hotelInventory.addRoomType("Suite Room", 2);

        hotelInventory.displayInventory();

        System.out.println("-------------------------------------------");
        System.out.println("Use Cases 1, 2, and 3 executed successfully.");
    }
}