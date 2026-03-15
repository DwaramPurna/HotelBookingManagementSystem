import java.util.*;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1-6
 * @author DwaramPurna
 * @version 6.0
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

// --- UC5: Reservation Model ---
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }

    @Override
    public String toString() { return guestName + " (" + requestedRoomType + ")"; }
}

// --- UC3 & UC6: Inventory & Allocation Service ---
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();
    // UC6: Set to ensure Unique Room IDs (Prevents Double Booking)
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
        allocatedRooms.put(type, new HashSet<>());
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public String allocateRoom(String type) {
        int currentCount = inventory.get(type);
        String roomID = type.substring(0, 1) + (100 + currentCount); // Simple ID generator

        // UC6: Uniqueness Enforcement
        if (allocatedRooms.get(type).add(roomID)) {
            inventory.put(type, currentCount - 1); // Atomic update
            return roomID;
        }
        return null;
    }

    public void displayStatus() {
        System.out.println("\n--- UC6: Final Inventory & Allocations ---");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Available: " + inventory.get(type) +
                    " | Allocated IDs: " + allocatedRooms.get(type));
        }
    }
}

// --- UC6: Booking Service (Processes the Queue) ---
class BookingService {
    public void processRequests(Queue<Reservation> queue, InventoryService inventory) {
        System.out.println("\n--- UC6: Processing Room Allocations ---");
        while (!queue.isEmpty()) {
            Reservation request = queue.poll(); // Dequeue FIFO (UC5)
            String type = request.getRequestedRoomType();

            if (inventory.isAvailable(type)) {
                String assignedID = inventory.allocateRoom(type);
                System.out.println("CONFIRMED: " + request.getGuestName() + " assigned to " + assignedID);
            } else {
                System.out.println("FAILED: No availability for " + request.getGuestName());
            }
        }
    }
}

// --- Main Application ---
public class Main {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v6.0");

        InventoryService hotelInventory = new InventoryService();
        hotelInventory.addRoomType("Single Room", 2); // Only 2 rooms available
        hotelInventory.addRoomType("Double Room", 1); // Only 1 room available

        // UC5: Guest Requests
        Queue<Reservation> bookingQueue = new LinkedList<>();
        bookingQueue.add(request(new Reservation("Alice", "Single Room")));
        bookingQueue.add(request(new Reservation("Bob", "Single Room")));
        bookingQueue.add(request(new Reservation("Charlie", "Single Room"))); // Should fail (3rd request)
        bookingQueue.add(request(new Reservation("David", "Double Room")));

        // UC6: Process Allocation
        BookingService service = new BookingService();
        service.processRequests(bookingQueue, hotelInventory);

        hotelInventory.displayStatus();
    }

    private static Reservation request(Reservation r) {
        System.out.println("Request Received: " + r);
        return r;
    }
}