//package com.example.realtime.service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.example.realtime.model.SeatAvailablity;
//import com.example.realtime.repository.SeatAvailabilityRepo;
//
//@Service
//public class SeatAvailabilityService {
//
//	@Autowired
//	private SeatAvailabilityRepo repo;
//	
//	public List<SeatAvailablity> getAllSeats() {
//	    return repo.findByIsAvailableTrue();
//	    
//	}
//	
//	 public SeatAvailablity saveSlot(SeatAvailablity seat) {
// 
//		 Optional<SeatAvailablity> existingSeat = repo.findBySeatId(seat.getSeatId());
//
//		    if (existingSeat.isPresent()) {
//		        SeatAvailablity existing = existingSeat.get();
//		        existing.setAvailable(seat.isAvailable());
//		        existing.setLocation(seat.getLocation());
//		        existing.setTimeSlot(seat.getTimeSlot());
//		        existing.setDurationMinutes(seat.getDurationMinutes());
//		        existing.setBookedBy(seat.getBookedBy());
//
//		        if (seat.isAvailable()) {
//		            existing.setAvailableSince(LocalDateTime.now());
//		        }
//
//		        return repo.save(existing);
//		    } else {
//		        // New seat – insert
//		        if (seat.isAvailable()) {
//		            seat.setAvailableSince(LocalDateTime.now());
//		        }
//		        return repo.save(seat);
//		    }
//		 
//		 
//	    }
//	 
//	 public List<SeatAvailablity> getAllSeatsRaw() {
//	        return repo.findAll();
//	    }
//	 
//	 public Optional<SeatAvailablity> getBySeatId(String seatId) {
//	        return repo.findBySeatId(seatId);
//	    }
//	 
//	 
//	 public boolean cancelSeat(Integer seatId,String user) {
//	        Optional<SeatAvailablity> optional = repo.findById(seatId);
//	        if (optional.isPresent()) {
//	            SeatAvailablity seat = optional.get();
//	            if (!user.equals(seat.getBookedBy())) {
//	                return false;
//	            }
//	            seat.setAvailable(true);
//	            seat.setAvailableSince(LocalDateTime.now());
//	            seat.setTimeSlot(null); // Optional: clear old timeSlot
//	            seat.setBookedBy(null);
//	            repo.save(seat);
//	            return true;
//	        }
//	        return false;
//	    }
//
//	
//}


package com.example.realtime.service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.realtime.model.Booking;

import com.example.realtime.model.SeatAvailablity;
import com.example.realtime.repository.SeatAvailabilityRepo;

@Service
public class SeatAvailabilityService {

    @Autowired
    private SeatAvailabilityRepo repo;

    public List<SeatAvailablity> getAllSeats() {
        return repo.findAll(); // Return all seats including their bookings
    }

    public Optional<SeatAvailablity> getBySeatId(String seatId) {
        return repo.findBySeatId(seatId);
    }

    public SeatAvailablity saveOrUpdateSeat(SeatAvailablity seat) {
        Optional<SeatAvailablity> existingOpt = repo.findBySeatId(seat.getSeatId());
        if (existingOpt.isPresent()) {
            SeatAvailablity existing = existingOpt.get();
            existing.setLocation(seat.getLocation());
            return repo.save(existing);
        } else {
            return repo.save(seat);
        }
    }

    public String bookSeat(String seatId, String bookedBy, LocalDateTime startTime, int durationMinutes,String password) {
        Optional<SeatAvailablity> optionalSeat = repo.findBySeatId(seatId);
        if (optionalSeat.isEmpty()) {
            return "Seat not found.";
        }

        SeatAvailablity seat = optionalSeat.get();

        // Check for overlapping bookings
        for (Booking booking : seat.getBookings()) {
            LocalDateTime existingStart = booking.getStartTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(booking.getDurationMinutes());
            LocalDateTime newEnd = startTime.plusMinutes(durationMinutes);

            if (!(newEnd.isBefore(existingStart) || startTime.isAfter(existingEnd))) {
                return "Seat already booked for this time slot.";
            }
        }

        // Add booking
        Booking newBooking = new Booking();
        newBooking.setBookedBy(bookedBy);
        newBooking.setStartTime(startTime);
        newBooking.setDurationMinutes(durationMinutes);
        newBooking.setPassword(password);

        seat.getBookings().add(newBooking);
        repo.save(seat);
        return "Booking successful.";
    }

    public boolean cancelBooking(String seatId,String bookedBy, String Password, LocalDateTime startTime) {
        Optional<SeatAvailablity> optionalSeat = repo.findBySeatId(seatId);
        if (optionalSeat.isEmpty()) return false;

        SeatAvailablity seat = optionalSeat.get();
        boolean removed = false;

        Iterator<Booking> iterator = seat.getBookings().iterator();
        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            if (booking.getPassword().equals(Password) &&booking.getBookedBy().equals(bookedBy) && booking.getStartTime().equals(startTime)) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            repo.save(seat);
        }

        return removed;
    }
}






