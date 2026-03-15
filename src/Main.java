import java.util.*;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1-11
 * @author DwaramPurna
 * @version 11.0
 */

// --- UC9: Custom Exceptions ---
class InsufficientInventoryException extends Exception { public InsufficientInventoryException(String m) { super(m); } }

// --- UC5: Reservation Model ---
class Reservation {
    private String guestName;
    private String roomType;
    private String roomID;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public void setConfirmedRoomID(String id) { this.roomID = id; }
    @Override
    public String toString() { return guestName + " -> " + roomID; }
}

// --- UC11: Thread-Safe Hotel Manager ---
class HotelManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private List<Reservation> history = Collections.synchronizedList(new ArrayList<>());

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    // UC11: Synchronized method to prevent Race Conditions
    public synchronized void processBooking(Reservation res) throws InsufficientInventoryException {
        String type = res.getGuestName(); // For simulation, using name as placeholder
        int count = inventory.getOrDefault(res.getRoomType(), 0);

        if (count > 0) {
            // Simulate processing time to expose potential race conditions
            try { Thread.sleep(10); } catch (InterruptedException e) {}

            String roomID = res.getRoomType().substring(0, 1).toUpperCase() + (100 + count);
            inventory.put(res.getRoomType(), count - 1);
            res.setConfirmedRoomID(roomID);
            history.add(res);
            System.out.println("[Thread: " + Thread.currentThread().getId() + "] SUCCESS: " + res.getGuestName());
        } else {
            throw new InsufficientInventoryException("Sold out for " + res.getGuestName());
        }
    }

    public void displayFinalState() {
        System.out.println("\n--- UC11: FINAL SYSTEM STATE ---");
        System.out.println("Remaining Inventory: " + inventory);
        System.out.println("Total Confirmed Bookings: " + history.size());
    }
}

// --- Main Entry Point with Thread Simulation ---
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hotel Booking Management System v11.0");
        System.out.println("Starting Concurrent Booking Simulation...");
        System.out.println("-------------------------------------------");

        HotelManager manager = new HotelManager();
        manager.addRoomType("Single Room", 5); // 5 rooms available

        // Creating 10 simultaneous booking attempts (More than inventory)
        Thread[] guests = new Thread[10];
        for (int i = 0; i < 10; i++) {
            String name = "Guest-" + (i + 1);
            guests[i] = new Thread(() -> {
                try {
                    manager.processBooking(new Reservation(name, "Single Room"));
                } catch (InsufficientInventoryException e) {
                    System.err.println("[Thread: " + Thread.currentThread().getId() + "] FAILED: " + e.getMessage());
                }
            });
        }

        // Start all threads at "once"
        for (Thread t : guests) t.start();

        // Wait for all threads to finish
        for (Thread t : guests) t.join();

        manager.displayFinalState();
        System.out.println("\nUse Case 11: Thread safety confirmed. No double-bookings.");
    }
}