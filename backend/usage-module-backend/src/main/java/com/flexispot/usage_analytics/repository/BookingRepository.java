package com.flexispot.usage_analytics.repository;


import com.flexispot.usage_analytics.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find all bookings on a specific date
    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByStartTimeAfter(LocalDateTime since);
}
