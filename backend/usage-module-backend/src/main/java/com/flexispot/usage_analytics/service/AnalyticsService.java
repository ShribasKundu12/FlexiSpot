package com.flexispot.usage_analytics.service;

import com.flexispot.usage_analytics.entity.Booking;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    double getOccupancyRate(LocalDate date);
    Map<String, Object> getPeakHours(LocalDate date);

    Map<String, Integer> getTopDesksUsed(LocalDate date);

    Map<String, Object> getDailySummary(LocalDate date);

    Map<String, Object> getEmployeeBookingPatterns(LocalDate date);

    Map<String, Object> getFloorwiseOccupancy(LocalDate date);

    Map<String, Object> getWeeklySummary(String employeeName, LocalDate weekStart);

    void exportSummaryCSV(LocalDate date, HttpServletResponse response) throws IOException;

    Map<String, Object> getHeatmapData(LocalDate date);

    Map<String, Object> getMonthlyTrend(YearMonth month);

//    Returns a list of employees who haven’t booked any desk in the last 7 days.
   Map<String, Object> getInactiveEmployees(LocalDate sinceDate);

    Map<String, Object> getMostBookedDesks(int topN);
//    Returns the top N desks that were booked the most in the last 30 days.

//    Analyzes the last 30 days and suggests the top 3 least crowded weekdays for peaceful working.

    Map<String, Object> suggestBestDays();

    List<Booking> getBookingsForDate(LocalDate date);
}
