import java.io.*;
import java.util.*;

/**
 * Hotel Booking Management System
 * Final Use Case 12: Data Persistence & System Recovery
 * @author DwaramPurna
 * @version 12.0
 */

// --- UC12: Base Classes must be Serializable ---
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guestName;
    private String roomType;
    private String roomID;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
    public void setConfirmedRoomID(String id) { this.roomID = id; }
    @Override
    public String toString() { return guestName + " [" + roomID + "]"; }
}

// --- UC12: The Persistent Manager ---
class HotelManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> inventory = new HashMap<>();
    private List<Reservation> history = new ArrayList<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public synchronized void processBooking(String name, String type) {
        int count = inventory.getOrDefault(type, 0);
        if (count > 0) {
            String id = type.substring(0, 1).toUpperCase() + (100 + count);
            inventory.put(type, count - 1);
            Reservation res = new Reservation(name, type);
            res.setConfirmedRoomID(id);
            history.add(res);
            System.out.println("SUCCESS: " + name + " booked " + id);
        } else {
            System.out.println("FAILED: No rooms for " + name);
        }
    }

    public void displayStatus() {
        System.out.println("Current Inventory: " + inventory);
        System.out.println("History: " + history);
    }
}

// --- UC12: Persistence Service ---
class PersistenceService {
    private static final String FILE_NAME = "hotel_data.ser";

    public static void saveState(HotelManager manager) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(manager);
            System.out.println("SYSTEM: State persisted to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("ERROR: Could not save state: " + e.getMessage());
        }
    }

    public static HotelManager loadState() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("SYSTEM: No previous state found. Starting fresh.");
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("SYSTEM: Previous state recovered successfully.");
            return (HotelManager) ois.readObject();
        } catch (Exception e) {
            System.err.println("ERROR: Recovery failed. Starting fresh.");
            return null;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v12.0");
        System.out.println("-------------------------------------------");

        // 1. Recovery Phase
        HotelManager manager = PersistenceService.loadState();

        if (manager == null) {
            manager = new HotelManager();
            manager.addRoomType("Single Room", 5);
            System.out.println("Initialized new inventory.");
        }

        // 2. Operation Phase
        manager.displayStatus();

        // Simulating a new booking
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter Guest Name to book a Single Room (or 'exit'): ");
        String name = scanner.nextLine();

        if (!name.equalsIgnoreCase("exit")) {
            manager.processBooking(name, "Single Room");
        }

        // 3. Persistence Phase (Before shutdown)
        PersistenceService.saveState(manager);

        System.out.println("\n-------------------------------------------");
        System.out.println("System Shutdown. Run again to see recovered data!");
    }
}