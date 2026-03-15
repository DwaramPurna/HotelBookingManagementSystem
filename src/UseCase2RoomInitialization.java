/**
 * UseCase2RoomInitialization
 * Domain modeling using inheritance and abstraction.
 * @author DwaramPurna
 * @version 2.0
 */

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

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 6000.0); }
    @Override
    public void displayDetails() {
        System.out.println("Type: " + roomType + " | Price: " + price);
    }
}

public class UseCase2RoomInitialization {
    public static void main(String[] args) {
        System.out.println("Hotel Booking Management System v2.0");
        System.out.println("------------------------------------");

        int singleRoomAvail = 5;
        int doubleRoomAvail = 3;
        int suiteRoomAvail = 2;

        Room s = new SingleRoom();
        Room d = new DoubleRoom();
        Room st = new SuiteRoom();

        s.displayDetails();
        System.out.println("Availability: " + singleRoomAvail + "\n");

        d.displayDetails();
        System.out.println("Availability: " + doubleRoomAvail + "\n");

        st.displayDetails();
        System.out.println("Availability: " + suiteRoomAvail + "\n");

        System.out.println("------------------------------------");
        System.out.println("Room initialization completed successfully.");
    }
}