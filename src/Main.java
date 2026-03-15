import java.util.*;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1-8
 * @author DwaramPurna
 * @version 8.0
 */

// --- UC7: Add-On Service Model ---
class AddOnService {
    private String name;
    private double cost;
    public AddOnService(String name, double cost) { this.name = name; this.cost = cost; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    @Override
    public String toString() { return name + " (Rs. " + cost + ")"; }
}

// --- UC2 & UC5: Core Models ---
abstract class Room {
    protected String roomType;
    protected double price;
    public Room(String roomType, double price) { this.roomType = roomType; this.price = price; }
    public String getRoomType() { return roomType; }
    public double getPrice() { return price; }
}

class SingleRoom extends Room { public SingleRoom() { super("Single Room", 2000.0); } }

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
    public String getConfirmedRoomID() { return confirmedRoomID; }

    @Override
    public String toString() {
        return "Guest: " + guestName + " | Room: " + confirmedRoomID + " (" + requestedRoomType + ")";
    }
}

// --- UC8: History & Reporting Service ---
class ReportingService {
    // UC8: List to store confirmed history (Persistence Mindset)
    private List<Reservation> bookingHistory = new ArrayList<>();

    public void recordBooking(Reservation res) {
        bookingHistory.add(res);
    }

    public void generateAdminReport() {
        System.out.println("\n--- UC8: ADMIN BOOKING HISTORY REPORT ---");
        if (bookingHistory.isEmpty()) {
            System.out.println("No historical records found.");
        } else {
            for (int i = 0; i < bookingHistory.size(); i++) {
                System.out.println((i + 1) + ". " + bookingHistory.get(i));
            }
            System.out.println("Total Bookings Processed: " + bookingHistory.size());
        }
        System.out.println("------------------------------------------");
    }
}

// --- Hotel Manager (The Core Controller) ---
class HotelManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private Map<String, List<AddOnService>> serviceAddOns = new HashMap<>();
    private ReportingService reportService = new ReportingService();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
        allocatedRooms.put(type, new HashSet<>());
    }

    public void processBooking(Reservation res) {
        String type = res.getRequestedRoomType();
        int currentCount = inventory.getOrDefault(type, 0);

        if (currentCount > 0) {
            String roomID = type.substring(0, 1).toUpperCase() + (100 + currentCount);
            if (allocatedRooms.get(type).add(roomID)) {
                inventory.put(type, currentCount - 1);
                res.setConfirmedRoomID(roomID);

                // UC8: Log to history after confirmation
                reportService.recordBooking(res);
                System.out.println("SUCCESS: Room " + roomID + " allocated to " + res.getGuestName());
            }
        } else {
            System.out.println("FAILED: No rooms left for " + res.getGuestName());
        }
    }

    public ReportingService getReportService() { return reportService; }
}

// --- Main Entry Point ---
public class Main {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v8.0");
        System.out.println("-------------------------------------------");

        HotelManager manager = new HotelManager();
        manager.addRoomType("Single Room", 2); // Only 2 rooms for testing

        // Simulating Booking Flow
        Reservation res1 = new Reservation("Alice", "Single Room");
        Reservation res2 = new Reservation("Bob", "Single Room");
        Reservation res3 = new Reservation("Charlie", "Single Room"); // Should fail

        manager.processBooking(res1);
        manager.processBooking(res2);
        manager.processBooking(res3);

        // UC8: Admin requests the report
        manager.getReportService().generateAdminReport();

        System.out.println("\nUse Case 8: Reporting and History verified.");
    }
}