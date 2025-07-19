import java.util.*;
import java.time.*;

class Booking {
    String userId;
    String deskId;
    String date;
    String startTime;
    String endTime;
    String role;

    Booking(String userId, String deskId, String date, String startTime, String endTime, String role) {
        this.userId = userId;
        this.deskId = deskId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.role = role;
    }
}

public class BookingRules {

    static ArrayList<Booking> allBookings = new ArrayList<>();
    static HashMap<String, Boolean> deskRestriction = new HashMap<>();

    public static void checkBooking(String userId, String role, String deskId, String date, String startTime, String endTime) {
        boolean isRestricted = deskRestriction.getOrDefault(deskId, false);

        // Parse date and time
        LocalDate bookingDate;
        LocalTime start, end;
        try {
            bookingDate = LocalDate.parse(date);
            start = LocalTime.parse(startTime);
            end = LocalTime.parse(endTime);
        } catch (Exception e) {
            System.out.println("Error: Invalid date or time format");
            return;
        }

        // Cannot book in past
        LocalDate today = LocalDate.now();
        if (bookingDate.isBefore(today)) {
            System.out.println("Error: Cannot book for past dates");
            return;
        }

        // No bookings on Sunday
        if (bookingDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            System.out.println("Error: Bookings not allowed on Sundays");
            return;
        }

        // Booking at least 1 hour in advance (if same day)
        if (bookingDate.equals(today)) {
            LocalTime now = LocalTime.now();
            Duration gap = Duration.between(now, start);
            if (gap.toMinutes() < 60) {
                System.out.println("Error: Booking must be 1 hour in advance");
                return;
            }
        }

        // Booking within working hours (9 AM to 6 PM)
        if (start.isBefore(LocalTime.of(9, 0)) || end.isAfter(LocalTime.of(18, 0))) {
            System.out.println("Error: Booking must be between 9am and 6pm");
            return;
        }

        // Start time must be before end time
        if (!start.isBefore(end)) {
            System.out.println("Error: Start time must be before end time");
            return;
        }

        // Max 2 bookings per day
        int count = 0;
        for (Booking b : allBookings) {
            if (b.userId.equals(userId) && b.date.equals(date)) {
                count++;
            }
        }
        if (count >= 2) {
            System.out.println("Error: Max bookings per day is 2");
            return;
        }

        // Restricted desk check
        if (isRestricted && !role.equalsIgnoreCase("admin")) {
            System.out.println("Error: This desk is restricted for admins");
            return;
        }

        // No overlapping booking
        for (Booking b : allBookings) {
            if (b.deskId.equals(deskId) && b.date.equals(date)) {
                LocalTime bStart = LocalTime.parse(b.startTime);
                LocalTime bEnd = LocalTime.parse(b.endTime);
                if (start.isBefore(bEnd) && end.isAfter(bStart)) {
                    System.out.println("Error: Desk already booked in this time slot");
                    return;
                }
            }
        }

        // All checks passed, booking allowed
        allBookings.add(new Booking(userId, deskId, date, startTime, endTime, role));
        System.out.println("Booking allowed for user " + userId);
    }

    public static void main(String[] args) {
        // Setup desk restrictions
        deskRestriction.put("deskA", true);
        deskRestriction.put("deskB", false);
        deskRestriction.put("deskC", false);

        // Initial booking
        allBookings.add(new Booking("user1", "deskB", "2025-07-21", "10:00", "11:00", "employee"));

        // Tests
        checkBooking("user1", "employee", "deskB", "2025-07-21", "11:30", "13:00"); // Allowed
        checkBooking("user1", "employee", "deskB", "2025-07-21", "10:50", "12:00"); // Overlap
        checkBooking("user1", "employee", "deskA", "2025-07-21", "10:50", "12:00"); // Desk restricted
        checkBooking("user1", "employee", "deskB", "2025-07-18", "12:30", "14:00"); // Past date
    }
}
