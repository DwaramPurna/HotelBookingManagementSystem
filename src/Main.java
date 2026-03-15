import java.util.*;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1-10
 * @author DwaramPurna
 * @version 10.0
 */

// --- UC9: Custom Exceptions ---
class InvalidRoomException extends Exception { public InvalidRoomException(String m) { super(m); } }
class InsufficientInventoryException extends Exception { public InsufficientInventoryException(String m) { super(m); } }
class ReservationNotFoundException extends Exception { public ReservationNotFoundException(String m) { super(m); } }

// --- UC5 & UC6: Models ---
class Reservation {
    private String guestName;
    private String requestedRoomType;
    private String confirmedRoomID;
    private boolean isCancelled = false;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
    public void setConfirmedRoomID(String id) { this.confirmedRoomID = id; }
    public String getConfirmedRoomID() { return confirmedRoomID; }
    public void cancel() { this.isCancelled = true; }
    public boolean isCancelled() { return isCancelled; }

    @Override
    public String toString() {
        return "Guest: " + guestName + " | Room: " + (isCancelled ? "CANCELLED" : confirmedRoomID);
    }
}

// --- UC10: Hotel Manager with Cancellation & Rollback ---
class HotelManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private List<Reservation> bookingHistory = new ArrayList<>();

    // UC10: Stack for Rollback (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
        allocatedRooms.put(type, new HashSet<>());
    }

    public void processBooking(Reservation res) throws InvalidRoomException, InsufficientInventoryException {
        String type = res.getRequestedRoomType();
        if (!inventory.containsKey(type)) throw new InvalidRoomException("Invalid Type: " + type);

        int count = inventory.get(type);
        if (count <= 0) throw new InsufficientInventoryException("Sold out: " + type);

        String roomID = type.substring(0, 1).toUpperCase() + (100 + count);
        if (allocatedRooms.get(type).add(roomID)) {
            inventory.put(type, count - 1);
            res.setConfirmedRoomID(roomID);
            bookingHistory.add(res);
            System.out.println("SUCCESS: " + res.getGuestName() + " assigned " + roomID);
        }
    }

    // UC10: Cancellation Logic (Inventory Rollback)
    public void cancelBooking(Reservation res) throws ReservationNotFoundException {
        if (res == null || res.getConfirmedRoomID() == null || res.isCancelled()) {
            throw new ReservationNotFoundException("Error: Valid confirmed reservation not found for cancellation.");
        }

        String roomID = res.getConfirmedRoomID();
        String type = res.getRequestedRoomType();

        // 1. Record in Rollback Stack
        rollbackStack.push(roomID);

        // 2. Restore Inventory
        inventory.put(type, inventory.get(type) + 1);

        // 3. Update State
        allocatedRooms.get(type).remove(roomID);
        res.cancel();

        System.out.println("CANCELLED: Room " + roomID + " is now vacant. Inventory restored for " + type);
    }

    public void displayStatus() {
        System.out.println("\n--- UC10: SYSTEM STATUS ---");
        System.out.println("Current Inventory: " + inventory);
        System.out.println("Recent Rollbacks (LIFO): " + rollbackStack);
        System.out.println("History: " + bookingHistory);
    }
}

// --- Main Entry Point ---
public class Main {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v10.0");
        System.out.println("-------------------------------------------");

        HotelManager manager = new HotelManager();
        manager.addRoomType("Single Room", 2);

        try {
            // 1. Make two bookings
            Reservation res1 = new Reservation("Alice", "Single Room");
            Reservation res2 = new Reservation("Bob", "Single Room");
            manager.processBooking(res1);
            manager.processBooking(res2);

            // 2. Cancel Alice's booking (Rollback)
            System.out.println("\n--- Initiating Cancellation ---");
            manager.cancelBooking(res1);

            // 3. Try to cancel same booking again (Should fail)
            manager.cancelBooking(res1);

        } catch (Exception e) {
            System.err.println("CANCELLATION ERROR: " + e.getMessage());
        }

        manager.displayStatus();
        System.out.println("\nUse Case 10: Cancellation & Rollback complete.");
    }
}