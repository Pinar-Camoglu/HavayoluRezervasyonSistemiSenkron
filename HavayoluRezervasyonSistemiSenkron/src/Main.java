import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ReservationSystem {
    private List<Boolean> seats;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ReservationSystem(int numSeats) {
        seats = new ArrayList<>();
        for (int i = 0; i < numSeats; i++) {
            seats.add(false); // All seats are initially unreserved
        }
    }

    public void queryReservation(String readerName) {
        lock.readLock().lock();
        try {
            System.out.print(getCurrentTime() + " - " + readerName + " looks for available seats. \nState of the seats: ");
            for (int i = 1; i < seats.size(); i++) {
                System.out.print("Seat No " + (i + 1) + " : " + (seats.get(i) ? 1 : 0) + " ");
            }
            System.out.println();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void makeReservation(int seatNumber, String writerName) {
        lock.writeLock().lock();
        try {
            System.out.println(getCurrentTime() + " - " + writerName + " tries to book seat " + (seatNumber + 1));
            if (!seats.get(seatNumber)) {
                seats.set(seatNumber, true);
                System.out.println(getCurrentTime() + " - " + writerName + " successfully booked seat number " + (seatNumber + 1) + ".");
            } else {
                System.out.println(getCurrentTime() + " - " + writerName + " could not book seat number " + (seatNumber + 1) + " because it's already reserved.");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
}

class Reader extends Thread {
    private ReservationSystem reservationSystem;
    private String readerName;

    public Reader(ReservationSystem reservationSystem, String readerName) {
        this.reservationSystem = reservationSystem;
        this.readerName = readerName;
    }

    public void run() {
        try {
            Thread.sleep((int) (Math.random() * 100)); // Random delay to simulate realistic behavior
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reservationSystem.queryReservation(readerName);
    }
}

class Writer extends Thread {
    private ReservationSystem reservationSystem;
    private int seatNumber;
    private String writerName;

    public Writer(ReservationSystem reservationSystem, int seatNumber, String writerName) {
        this.reservationSystem = reservationSystem;
        this.seatNumber = seatNumber;
        this.writerName = writerName;
    }

    public void run() {
        try {
            Thread.sleep((int) (Math.random() * 100)); // Random delay to simulate realistic behavior
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reservationSystem.makeReservation(seatNumber, writerName);
    }
}

public class Main {
    public static void main(String[] args) {
        ReservationSystem reservationSystem = new ReservationSystem(6);

        // Create multiple readers and writers
        Reader reader1 = new Reader(reservationSystem, "Reader1");
        Reader reader2 = new Reader(reservationSystem, "Reader2");
        Reader reader3 = new Reader(reservationSystem, "Reader3");
        Writer writer1 = new Writer(reservationSystem, 0, "Writer1");
        Writer writer2 = new Writer(reservationSystem, 0, "Writer2");
        Writer writer3 = new Writer(reservationSystem, 0, "Writer3");

        // Start threads
        reader1.start();
        reader2.start();
        writer1.start();
        writer2.start();
        writer3.start();
        reader3.start();

        try {
            reader1.join();
            reader2.join();
            writer1.join();
            writer2.join();
            writer3.join();
            reader3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
