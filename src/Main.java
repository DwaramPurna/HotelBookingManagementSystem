import java.util.HashMap;
import java.util.Map;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1, 2, 3, and 4
 * @author DwaramPurna
 * @version 4.0
 */

// --- UC2: Domain Modeling (Room Hierarchy) ---
abstract class Room {
    protected String roomType;
    protected double price;

    public Room(String roomType, double price) {
        this.roomType = roomType;
        this.price = price;
    }

    public String getRoomType() { return roomType; }
    public double getPrice() { return price; }

    public abstract void displayDetails();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 2000.0); }
    @Override
    public void displayDetails() {
        System.out.println("Type: " + roomType + " | Price: " + price + " | Features: Single Bed, Wifi");
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 3500.0); }
    @Override
    public void displayDetails() {
        System.out.println("Type: " + roomType + " | Price: " + price + " | Features: Queen Bed, AC, Wifi");
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 6000.0); }
    @Override
    public void displayDetails() {
        System.out.println("Type: " + roomType + " | Price: " + price + " | Features: King Bed, Mini Bar, Balcony");
    }
}

// --- UC3: Centralized Inventory Manager ---
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllInventory() {
        return inventory;
    }
}

// --- UC4: Room Search Service (Read-Only) ---
class SearchService {
    public void searchAvailableRooms(RoomInventory inventory, Map<String, Room> roomDetails) {
        System.out.println("\n--- UC4: Search Results (Available Rooms Only) ---");
        boolean found = false;

        for (String type : inventory.getAllInventory().keySet()) {
            int count = inventory.getAvailability(type);

            // Filter: Only show rooms with availability > 0
            if (count > 0) {
                Room details = roomDetails.get(type);
                if (details != null) {
                    System.out.print("[" + count + " Left] ");
                    details.displayDetails();
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("No rooms available at the moment.");
        }
    }
}

// --- Main Application Entry Point ---
public class Main {
    public static void main(String[] args) {
        // UC1: Startup
        System.out.println("Welcome to Book My Stay App");
        System.out.println("Hotel Booking Management System Starting...");
        System.out.println("-------------------------------------------");

        // UC2 & UC3: Data Setup
        RoomInventory hotelInventory = new RoomInventory();
        Map<String, Room> roomMap = new HashMap<>();

        // Initialize Rooms
        roomMap.put("Single Room", new SingleRoom());
        roomMap.put("Double Room", new DoubleRoom());
        roomMap.put("Suite Room", new SuiteRoom());

        // Initialize Inventory (UC3)
        hotelInventory.addRoomType("Single Room", 5);
        hotelInventory.addRoomType("Double Room", 0); // Set to 0 to test UC4 filter
        hotelInventory.addRoomType("Suite Room", 2);

        // UC4: Perform Search (Read-Only Access)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(hotelInventory, roomMap);

        System.out.println("-------------------------------------------");
        System.out.println("UC4: Search operation performed without state mutation.");
        System.out.println("Use Case 4 completed successfully.");
    }
}