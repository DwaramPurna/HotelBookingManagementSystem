import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1, 2, 3, 4, and 5
 * @author DwaramPurna
 * @version 5.0
 */

// --- UC2: Domain Modeling ---
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
        System.out.println("Type: " + roomType + " | Price: " + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 3500.0); }
    @Override
    public void displayDetails() {
        System.out.println("Type: " + roomType + " | Price: " + price);
    }
}

// --- UC5: Reservation Model (The Guest's Intent) ---
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    @Override
    public String toString() {
        return "Request from " + guestName + " for " + requestedRoomType;
    }
}

// --- UC3: Inventory Manager ---
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

// --- UC4: Search Service ---
class SearchService {
    public void searchAvailableRooms(RoomInventory inventory, Map<String, Room> roomDetails) {
        System.out.println("\n--- UC4: Search Results ---");
        for (String type : inventory.getAllInventory().keySet()) {
            if (inventory.getAvailability(type) > 0) {
                roomDetails.get(type).displayDetails();
            }
        }
    }
}

// --- UC5: Booking Request Queue (Fairness & Ordering) ---
class BookingQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation request) {
        requestQueue.add(request);
        System.out.println("Queue Update: " + request + " added to the line.");
    }

    public void displayQueue() {
        System.out.println("\n--- UC5: Current Booking Request Queue (FIFO) ---");
        if (requestQueue.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            for (Reservation res : requestQueue) {
                System.out.println("[WAITING] " + res);
            }
        }
    }
}

// --- Main Application Entry Point ---
public class Main {
    public static void main(String[] args) {
        // UC1: Startup
        System.out.println("Welcome to Book My Stay App v5.0");
        System.out.println("-------------------------------------------");

        // Setup Data
        RoomInventory hotelInventory = new RoomInventory();
        Map<String, Room> roomMap = new HashMap<>();
        roomMap.put("Single Room", new SingleRoom());
        roomMap.put("Double Room", new DoubleRoom());

        hotelInventory.addRoomType("Single Room", 5);
        hotelInventory.addRoomType("Double Room", 3);

        // UC4: Search
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(hotelInventory, roomMap);

        // UC5: Booking Requests (Intake Mechanism)
        System.out.println("\n--- UC5: Processing Incoming Requests ---");
        BookingQueue bookingQueue = new BookingQueue();

        // Simulating guests arriving at different times
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Single Room"));

        // Displaying the queue to prove order is preserved
        bookingQueue.displayQueue();

        System.out.println("\n-------------------------------------------");
        System.out.println("UC5 Completed: Requests queued in arrival order.");
    }
}