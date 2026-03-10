import java.util.*;

public class ParkingApp{
    enum Status { EMPTY, OCCUPIED, DELETED }

    class Spot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;

        void park(String plate) {
            this.licensePlate = plate;
            this.entryTime = System.currentTimeMillis();
            this.status = Status.OCCUPIED;
        }

        void leave() {
            this.status = Status.DELETED;
            this.licensePlate = null;
        }
    }

    private final int capacity = 500;
    private Spot[] lot = new Spot[capacity];
    private int totalParked = 0;
    private int totalProbes = 0;

    public ParkingApp() {
        for (int i = 0; i < capacity; i++) lot[i] = new Spot();
    }

    // Custom Hash Function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    public void parkVehicle(String plate) {
        int preferredSpot = hash(plate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Linear Probing: Find first EMPTY or DELETED spot
        while (lot[currentSpot].status == Status.OCCUPIED) {
            currentSpot = (currentSpot + 1) % capacity;
            probes++;
            if (probes == capacity) {
                System.out.println("Lot is full!");
                return;
            }
        }

        lot[currentSpot].park(plate);
        totalParked++;
        totalProbes += probes;
        System.out.printf("Parked %s at spot #%d (%d probes)\n", plate, currentSpot, probes);
    }

    public void exitVehicle(String plate) {
        int preferredSpot = hash(plate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Linear Probing: Find the specific plate
        while (lot[currentSpot].status != Status.EMPTY) {
            if (lot[currentSpot].status == Status.OCCUPIED &&
                    lot[currentSpot].licensePlate.equals(plate)) {

                long duration = (System.currentTimeMillis() - lot[currentSpot].entryTime) / 1000;
                double fee = Math.max(5.0, duration * 0.05); // Example: $0.05 per second

                lot[currentSpot].leave();
                totalParked--;
                System.out.printf("Exit %s from #%d. Fee: $%.2f\n", plate, currentSpot, fee);
                return;
            }
            currentSpot = (currentSpot + 1) % capacity;
            if (++probes == capacity) break;
        }
        System.out.println("Vehicle not found.");
    }

    public void getStatistics() {
        double occupancy = (totalParked * 100.0) / capacity;
        double avgProbes = totalParked == 0 ? 0 : (double) totalProbes / totalParked;
        System.out.printf("Stats -> Occupancy: %.1f%%, Avg Probes: %.2f\n", occupancy, avgProbes);
    }

    public static void main(String[] args) {
        ParkingApp sps = new ParkingApp();
        sps.parkVehicle("ABC-1234");
        sps.parkVehicle("ABC-1235"); // Likely collision if hash is similar
        sps.getStatistics();
        sps.exitVehicle("ABC-1234");
    }
}