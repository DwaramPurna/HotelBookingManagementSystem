import java.util.*;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1-9
 * @author DwaramPurna
 * @version 9.0
 */

// --- UC9: Custom Exceptions ---
class InvalidRoomException extends Exception {
    public InvalidRoomException(String message) { super(message); }
}

class InsufficientInventoryException extends Exception {
    public InsufficientInventoryException(String message) { super(message); }
}

// --- UC2, UC5, UC7: Models ---
class AddOnService {
    private String name;
    private double cost;
    public AddOnService(String name, double cost) { this.name = name; this.cost = cost; }
    @Override
    public String toString() { return name + " (Rs. " + cost + ")"; }
}

class Reservation {
    private String guestName;
    private String requestedRoomType;
    private String confirmedRoomID;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
    public void setConfirmedRoomID(String id) { this.confirmedRoomID = id; }
    @Override
    public String toString() { return "Guest: " + guestName + " | Room: " + confirmedRoomID + " (" + requestedRoomType + ")"; }
}

// --- UC8: Reporting ---
class ReportingService {
    private List<Reservation> bookingHistory = new ArrayList<>();
    public void recordBooking(Reservation res) { bookingHistory.add(res); }
    public void generateAdminReport() {
        System.out.println("\n--- UC9: ADMIN HISTORY REPORT ---");
        for (int i = 0; i < bookingHistory.size(); i++) {
            System.out.println((i + 1) + ". " + bookingHistory.get(i));
        }
    }
}

// --- Hotel Manager (The Controller with UC9 Validation) ---
class HotelManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private ReportingService reportService = new ReportingService();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
        allocatedRooms.put(type, new HashSet<>());
    }

    // UC9: Method with structured validation and exceptions
    public void processBooking(Reservation res) throws InvalidRoomException, InsufficientInventoryException {
        String type = res.getRequestedRoomType();

        // 1. Validate Room Type (Input Validation)
        if (!inventory.containsKey(type)) {
            throw new InvalidRoomException("Error: Room type '" + type + "' does not exist in our system.");
        }

        // 2. Validate Inventory (State Guarding)
        int currentCount = inventory.get(type);
        if (currentCount <= 0) {
            throw new InsufficientInventoryException("Error: No " + type + "s available for " + res.getGuestName() + ".");
        }

        // 3. Allocation logic (The "Happy Path")
        String roomID = type.substring(0, 1).toUpperCase() + (100 + currentCount);
        if (allocatedRooms.get(type).add(roomID)) {
            inventory.put(type, currentCount - 1);
            res.setConfirmedRoomID(roomID);
            reportService.recordBooking(res);
            System.out.println("SUCCESS: " + res.getGuestName() + " booked " + roomID);
        }
    }

    public ReportingService getReportService() { return reportService; }
}

// --- Main Entry Point ---
public class Main {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v9.0");
        System.out.println("-------------------------------------------");

        HotelManager manager = new HotelManager();
        manager.addRoomType("Single Room", 1); // Only 1 room available

        // UC9: Testing different scenarios with Try-Catch
        List<Reservation> requests = Arrays.asList(
                new Reservation("Alice", "Single Room"),    // Valid
                new Reservation("Bob", "Single Room"),      // Should trigger InsufficientInventory
                new Reservation("Charlie", "Penthouse")     // Should trigger InvalidRoom
        );

        for (Reservation res : requests) {
            try {
                System.out.println("\nProcessing request for: " + res.getGuestName());
                manager.processBooking(res);
            } catch (InvalidRoomException | InsufficientInventoryException e) {
                // Graceful Failure Handling
                System.err.println("VALIDATION FAILED: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected Error: " + e.getMessage());
            }
        }

        manager.getReportService().generateAdminReport();
        System.out.println("\nUse Case 9: Error handling and validation complete.");
    }
}