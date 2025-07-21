package com.flexispot.usage_analytics.controller;

import com.flexispot.usage_analytics.entity.Booking;
import com.flexispot.usage_analytics.service.AnalyticsService;
import com.flexispot.usage_analytics.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/occupancy-rate")
    public Map<String, Object> getOccupancyRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        double occupancy = analyticsService.getOccupancyRate(date);

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("occupancyRate", String.format("%.2f", occupancy) + "%");

        return response;
    }

    @GetMapping("/peak-hours")
    public Map<String, Object> getPeakHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return analyticsService.getPeakHours(date);
    }

    @GetMapping("/top-desks")
    public Map<String, Integer> getTopDesksUsed(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return analyticsService.getTopDesksUsed(date);
    }

    @GetMapping("/daily-summary")
    public Map<String, Object> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return analyticsService.getDailySummary(date);
    }

    @GetMapping("/employee-booking-patterns")
    public Map<String, Object> getEmployeeBookingPatterns(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return analyticsService.getEmployeeBookingPatterns(date);
    }

    @GetMapping("/floorwise-occupancy")
    public Map<String, Object> getFloorwiseOccupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return analyticsService.getFloorwiseOccupancy(date);
    }

    @GetMapping("/weekly-summary")
    public Map<String, Object> getWeeklySummary(
            @RequestParam String employee,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return analyticsService.getWeeklySummary(employee, startDate);
    }

    @GetMapping("/export-summary-csv")
    public void exportSummaryCSV(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletResponse response) throws IOException {
        analyticsService.exportSummaryCSV(date, response);
    }

    @GetMapping("/heatmap")
    public Map<String, Object> getHeatmapData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return analyticsService.getHeatmapData(date);
    }

    @GetMapping("/monthly-trend")
    public Map<String, Object> getMonthlyTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return analyticsService.getMonthlyTrend(month);
    }

    @GetMapping("/employee-inactive")
    public Map<String, Object> getInactiveEmployees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        return analyticsService.getInactiveEmployees(since);
    }

    @GetMapping("/most-booked-desks")
    public Map<String, Object> getMostBookedDesks(@RequestParam(defaultValue = "5") int top) {
        return analyticsService.getMostBookedDesks(top);
    }

    @GetMapping("/suggestions/best-days")
    public Map<String, Object> suggestBestDays() {
        return analyticsService.suggestBestDays();
    }

    // email report

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-report")
    public ResponseEntity<?> sendReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String email) {
        try {
            List<Booking> bookings = analyticsService.getBookingsForDate(date);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(bos);
            writer.println("Date,Employee,Desk,Start Time,End Time");
            for (Booking booking : bookings) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        date,
                        booking.getEmployee().getName(),
                        booking.getWorkspace().getName(),
                        booking.getStartTime().toLocalTime(),
                        booking.getEndTime().toLocalTime()
                );
            }
            writer.flush(); // Ensure all data is written
            byte[] csvBytes = bos.toByteArray();
            writer.close(); // Safe to close after getting bytes

            String subject = "Workspace Usage Summary – " + date;
            String htmlBody =
                    "<p>Hello,</p>"
                            + "<p>Please find attached the workspace usage summary report for <b>" + date + "</b> in CSV format.</p>"
                            + "<p>If you have any questions regarding the data or require further assistance,<br>"
                            + "feel free to contact us.</p>"
                            + "<p>Best regards,<br>FlexiSpot Analytics Team</p>";

            emailService.sendCSVReport(email, subject, htmlBody, csvBytes, "usage-summary-" + date + ".csv");

            return ResponseEntity.ok("Email sent");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Failed: " + ex.getMessage());
        }
    }









}
