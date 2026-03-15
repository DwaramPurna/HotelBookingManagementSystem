import java.util.*;

/**
 * Hotel Booking Management System
 * Combined Use Cases 1-7
 * @author DwaramPurna
 * @version 7.0
 */

// --- UC7: Add-On Service Model ---
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() { return name; }
    public double getCost() { return cost; }

    @Override
    public String toString() { return name + " (Rs. " + cost + ")"; }
}

// --- UC2: Domain Modeling ---
abstract class Room {
    protected String roomType;
    protected double price;
    public Room(String roomType, double price) { this.roomType = roomType; this.price = price; }
    public String getRoomType() { return roomType; }
}

class SingleRoom extends Room { public SingleRoom() { super("Single Room", 2000.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Double Room", 3500.0); } }

// --- UC5: Reservation Model ---
class Reservation {
    private String guestName;
    private String requestedRoomType;
    private String confirmedRoomID; // Added in UC6

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
    public void setConfirmedRoomID(String id) { this.confirmedRoomID = id; }
    public String getConfirmedRoomID() { return confirmedRoomID; }
}

// --- UC3, UC6 & UC7: Inventory & Service Manager ---
class HotelManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    // UC7: Mapping Reservation ID to a List of Services (One-to-Many)
    private Map<String, List<AddOnService>> serviceAddOns = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
        allocatedRooms.put(type, new HashSet<>());
    }

    public String allocateRoom(Reservation res) {
        String type = res.getRequestedRoomType();
        int currentCount = inventory.getOrDefault(type, 0);

        if (currentCount > 0) {
            String roomID = type.substring(0, 1).toUpperCase() + (100 + currentCount);
            if (allocatedRooms.get(type).add(roomID)) {
                inventory.put(type, currentCount - 1);
                res.setConfirmedRoomID(roomID);
                return roomID;
            }
        }
        return null;
    }

    // UC7: Add services to a specific room ID
    public void addService(String roomID, AddOnService service) {
        serviceAddOns.computeIfAbsent(roomID, k -> new ArrayList<>()).add(service);
        System.out.println("Service Added: " + service.getName() + " to Room " + roomID);
    }

    public void displayBill(Reservation res) {
        String id = res.getConfirmedRoomID();
        System.out.println("\n--- Final Bill for " + res.getGuestName() + " ---");
        System.out.println("Room ID: " + id);

        double extraCost = 0;
        List<AddOnService> services = serviceAddOns.getOrDefault(id, new ArrayList<>());

        if (services.isEmpty()) {
            System.out.println("Add-ons: None");
        } else {
            System.out.println("Add-ons: " + services);
            for (AddOnService s : services) extraCost += s.getCost();
        }
        System.out.println("Total Extra Service Cost: Rs. " + extraCost);
    }
}

// --- Main Application ---
public class Main {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v7.0");
        System.out.println("-------------------------------------------");

        HotelManager manager = new HotelManager();
        manager.addRoomType("Single Room", 5);

        // 1. Create a Reservation (UC5)
        Reservation aliceRes = new Reservation("Alice", "Single Room");

        // 2. Allocate Room (UC6)
        String roomID = manager.allocateRoom(aliceRes);

        if (roomID != null) {
            System.out.println("Alice confirmed in Room: " + roomID);

            // 3. Add-On Services (UC7)
            manager.addService(roomID, new AddOnService("Breakfast", 500.0));
            manager.addService(roomID, new AddOnService("Late Check-out", 1000.0));

            // 4. Display Results
            manager.displayBill(aliceRes);
        }

        System.out.println("\n-------------------------------------------");
        System.out.println("UC7: Services mapped to Reservation successfully.");
    }
}