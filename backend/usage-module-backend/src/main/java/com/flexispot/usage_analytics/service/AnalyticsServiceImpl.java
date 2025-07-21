package com.flexispot.usage_analytics.service;

import com.flexispot.usage_analytics.entity.Booking;
import com.flexispot.usage_analytics.entity.Employee;
import com.flexispot.usage_analytics.entity.Workspace;
import com.flexispot.usage_analytics.repository.BookingRepository;
import com.flexispot.usage_analytics.repository.EmployeeRepository;
import com.flexispot.usage_analytics.repository.WorkspaceRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BookingRepository bookingRepository;
    private final WorkspaceRepository workspaceRepository;
    private final EmployeeRepository employeeRepository;


    public AnalyticsServiceImpl(BookingRepository bookingRepository, WorkspaceRepository workspaceRepository, EmployeeRepository employeeRepository) {
        this.bookingRepository = bookingRepository;
        this.workspaceRepository = workspaceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public double getOccupancyRate(LocalDate date) {
        // Get start and end of the date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Count bookings for the day
        int bookingsCount = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay).size();

        // Count total active workspaces
        long totalDesks = workspaceRepository.count();

        if (totalDesks == 0) return 0.0;

        // Calculate and return occupancy %
        return (bookingsCount * 100.0) / totalDesks;
    }

    @Override
    public Map<String, Object> getPeakHours(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        Map<Integer, Integer> hourCounts = new HashMap<>();

        for (Booking booking : bookings) {
            int startHour = booking.getStartTime().getHour();
            int endHour = booking.getEndTime().getHour();

            for (int hour = startHour; hour <= endHour; hour++) {
                hourCounts.put(hour, hourCounts.getOrDefault(hour, 0) + 1);
            }
        }

        int max = hourCounts.values().stream().max(Integer::compare).orElse(0);
        List<String> peakHours = hourCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == max)
                .map(entry -> String.format("%02d:00", entry.getKey()))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("peakHours", peakHours);
        result.put("maxBookings", max);

        return result;
    }

    @Override
    public Map<String, Integer> getTopDesksUsed(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        Map<String, Integer> deskUsage = new HashMap<>();

        for (Booking booking : bookings) {
            String deskName = booking.getWorkspace().getName();
            deskUsage.put(deskName, deskUsage.getOrDefault(deskName, 0) + 1);
        }

        return deskUsage;
    }

    @Override
    public Map<String, Object> getDailySummary(LocalDate date) {
        Map<String, Object> summary = new HashMap<>();

        // 👇 Use existing methods to fetch pieces
        double occupancyRate = getOccupancyRate(date);
        Map<String, Object> peakData = getPeakHours(date);
        Map<String, Integer> topDesks = getTopDesksUsed(date);

        // 🧠 Prepare output
        summary.put("date", date);
        summary.put("occupancyRate", String.format("%.2f%%", occupancyRate));
        summary.put("peakHours", peakData.get("peakHours"));
        summary.put("topDesks", topDesks);

        return summary;
    }

    @Override
    public Map<String, Object> getEmployeeBookingPatterns(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        Map<String, Integer> employeeBookings = new HashMap<>();

        for (Booking booking : bookings) {
            String employeeName = booking.getEmployee().getName();
            employeeBookings.put(employeeName, employeeBookings.getOrDefault(employeeName, 0) + 1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("employeeBookings", employeeBookings);

        return result;
    }

    @Override
    public Map<String, Object> getFloorwiseOccupancy(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Get all workspaces and group by floor
        List<Workspace> allWorkspaces = workspaceRepository.findAll();
        Map<String, List<Workspace>> floorToDesks = new HashMap<>();

        for (Workspace workspace : allWorkspaces) {
            floorToDesks
                    .computeIfAbsent(workspace.getFloor(), k -> new java.util.ArrayList<>())
                    .add(workspace);
        }

        // Get bookings on the selected date
        List<Booking> bookings = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        // Count bookings per floor
        Map<String, Long> floorBookingCounts = new HashMap<>();
        for (Booking booking : bookings) {
            String floor = booking.getWorkspace().getFloor();
            floorBookingCounts.put(floor, floorBookingCounts.getOrDefault(floor, 0L) + 1);
        }

        // Calculate occupancy per floor
        Map<String, String> occupancyPerFloor = new HashMap<>();

        for (String floor : floorToDesks.keySet()) {
            int totalDesks = floorToDesks.get(floor).size();
            long bookedCount = floorBookingCounts.getOrDefault(floor, 0L);

            String percentage = totalDesks == 0 ? "0.00%" :
                    String.format("%.2f%%", (bookedCount * 100.0) / totalDesks);

            occupancyPerFloor.put(floor, percentage);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("floorOccupancy", occupancyPerFloor);

        return result;
    }

    @Override
    public Map<String, Object> getWeeklySummary(String employeeName, LocalDate weekStart) {
        Map<String, Integer> summary = new LinkedHashMap<>();

        // Get all bookings made by the employee in 7-day range
        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekStart.plusDays(6).atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(start, end);

        // Filter by employee
        bookings = bookings.stream()
                .filter(b -> b.getEmployee().getName().equalsIgnoreCase(employeeName))
                .toList();

        // Initialize counts
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            summary.put(day.toString(), 0);
        }

        // Count bookings per day
        for (Booking booking : bookings) {
            String date = booking.getStartTime().toLocalDate().toString();
            summary.put(date, summary.get(date) + 1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("employee", employeeName);
        result.put("summary", summary);

        return result;
    }

    @Override
    public void exportSummaryCSV(LocalDate date, HttpServletResponse response) throws IOException {
        List<Booking> bookings = bookingRepository.findByStartTimeBetween(
                date.atStartOfDay(),
                date.atTime(23, 59, 59)
        );

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=usage-summary-" + date + ".csv");

        PrintWriter writer = response.getWriter();
        writer.println("Date,Employee,Desk,Start Time,End Time");

        for (Booking booking : bookings) {
            String employee = booking.getEmployee().getName();
            String desk = booking.getWorkspace().getName();
            String start = booking.getStartTime().toLocalTime().toString();
            String end = booking.getEndTime().toLocalTime().toString();

            writer.printf("%s,%s,%s,%s,%s%n", date, employee, desk, start, end);
        }

        writer.flush();
        writer.close();
    }

    @Override
    public Map<String, Object> getHeatmapData(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        Map<String, Map<String, Integer>> heatmap = new HashMap<>();

        for (Booking booking : bookings) {
            String desk = booking.getWorkspace().getName();
            Map<String, Integer> deskHours = heatmap.computeIfAbsent(desk, k -> new HashMap<>());

            int startHour = booking.getStartTime().getHour();
            int endHour = booking.getEndTime().getHour();

            for (int hour = startHour; hour <= endHour; hour++) {
                String hourLabel = String.format("%02d:00", hour);
                deskHours.put(hourLabel, deskHours.getOrDefault(hourLabel, 0) + 1);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("heatmap", heatmap);

        return result;
    }

    @Override
    public Map<String, Object> getMonthlyTrend(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Booking> bookings = bookingRepository.findByStartTimeBetween(start, end);

        Map<String, Integer> dailyCounts = new LinkedHashMap<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailyCounts.put(date.toString(), 0);
        }

        for (Booking booking : bookings) {
            String date = booking.getStartTime().toLocalDate().toString();
            dailyCounts.put(date, dailyCounts.get(date) + 1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("month", month.toString());
        result.put("dailyBookingTrend", dailyCounts);

        return result;
    }

    @Override
    public Map<String, Object> getInactiveEmployees(LocalDate sinceDate) {
        LocalDateTime since = sinceDate.atStartOfDay();
        List<Booking> recentBookings = bookingRepository.findByStartTimeAfter(since);

        // Employees who made recent bookings
        Set<String> activeEmployeeNames = recentBookings.stream()
                .map(b -> b.getEmployee().getName())
                .collect(Collectors.toSet());

        // Get all employees (assumes EmployeeRepository exists)
        List<Employee> allEmployees = employeeRepository.findAll();

        List<String> inactive = allEmployees.stream()
                .map(Employee::getName)
                .filter(name -> !activeEmployeeNames.contains(name))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("inactiveSince", sinceDate.toString());
        result.put("inactiveEmployees", inactive);

        return result;
    }

    @Override
    public Map<String, Object> getMostBookedDesks(int topN) {
        LocalDateTime since = LocalDate.now().minusDays(30).atStartOfDay();
        List<Booking> bookings = bookingRepository.findByStartTimeAfter(since);

        Map<String, Integer> deskCountMap = new HashMap<>();

        for (Booking booking : bookings) {
            String desk = booking.getWorkspace().getName();
            deskCountMap.put(desk, deskCountMap.getOrDefault(desk, 0) + 1);
        }

        List<Map<String, Object>> topDesks = deskCountMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(topN)
                .map(entry -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("desk", entry.getKey());
                    m.put("bookings", entry.getValue());
                    return m;
                })
                .toList();


        Map<String, Object> result = new HashMap<>();
        result.put("top", topN);
        result.put("since", since.toLocalDate().toString());
        result.put("mostBookedDesks", topDesks);

        return result;
    }

    @Override
    public Map<String, Object> suggestBestDays() {
        LocalDateTime since = LocalDate.now().minusDays(30).atStartOfDay();
        List<Booking> bookings = bookingRepository.findByStartTimeAfter(since);

        // Count bookings by weekday (0=Monday, 6=Sunday)
        Map<DayOfWeek, Integer> weekdayCounts = new EnumMap<>(DayOfWeek.class);

        for (Booking booking : bookings) {
            DayOfWeek day = booking.getStartTime().getDayOfWeek();
            weekdayCounts.put(day, weekdayCounts.getOrDefault(day, 0) + 1);
        }

        // Sort by least booked
        List<String> bestDays = weekdayCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // ascending
                .limit(3)
                .map(entry -> entry.getKey().toString()) // returns MONDAY, TUESDAY...
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("suggestedDays", bestDays);
        result.put("basedOn", "Last 30 days");

        return result;
    }

    @Override
    public List<Booking> getBookingsForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);
    }




}

