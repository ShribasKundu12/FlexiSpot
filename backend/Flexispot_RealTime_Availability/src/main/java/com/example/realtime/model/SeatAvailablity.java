//package com.example.realtime.model;
//import java.time.LocalDateTime;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//
//@Entity
//public class SeatAvailablity {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Integer id;
//	 
//	private String seatId;
//	    private boolean isAvailable;
//	    private String location;
//	    private LocalDateTime timeSlot;
//	    private LocalDateTime availableSince;
//	    private Integer durationMinutes; // how long the seat is booked (in minutes)
//	  
//	    private String bookedBy;
//
//	    
//	    
//	    public Integer getId() {
//			return id;
//		}
//		public void setId(Integer id) {
//			this.id = id;
//		}
//		public String getSeatId() {
//			return seatId;
//		}
//		public void setSeatId(String seatId) {
//			this.seatId = seatId;
//		}
//		public boolean isAvailable() {
//			return isAvailable;
//		}
//		public void setAvailable(boolean isAvailable) {
//			this.isAvailable = isAvailable;
//		}
//		public String getLocation() {
//			return location;
//		}
//		public void setLocation(String location) {
//			this.location = location;
//		}
//		public LocalDateTime getTimeSlot() {
//			return timeSlot;
//		}
//		public void setTimeSlot(LocalDateTime timeSlot) {
//			this.timeSlot = timeSlot;
//		}
//		
//		
//		
//		public LocalDateTime getAvailableSince() {
//			return availableSince;
//		}
//		public void setAvailableSince(LocalDateTime availableSince) {
//			this.availableSince = availableSince;
//		}
//		
//		
//		public Integer getDurationMinutes() {
//			return durationMinutes;
//		}
//		public void setDurationMinutes(Integer durationMinutes) {
//			this.durationMinutes = durationMinutes;
//		}
//		
//		
//		
//		
//		public String getBookedBy() {
//			return bookedBy;
//		}
//		public void setBookedBy(String bookedBy) {
//			this.bookedBy = bookedBy;
//		}
//		public SeatAvailablity() {
//			
//		}
//		public SeatAvailablity(Integer id,String seatId, boolean isAvailable, String location, LocalDateTime timeSlot,LocalDateTime availableSince,Integer durationMinutes,String bookedBy) {
//			super();
//			this.id=id;
//			this.seatId = seatId;
//			this.isAvailable = isAvailable;
//			this.location = location;
//			this.timeSlot = timeSlot;
//			this.availableSince=availableSince;
//			this.durationMinutes=durationMinutes;
//			this.bookedBy=bookedBy;
//		}
//		
//	    
//}
package com.example.realtime.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class SeatAvailablity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String seatId;
    private String location;

    @ElementCollection
    @CollectionTable(name = "seat_bookings", joinColumns = @JoinColumn(name = "seat_id"))
    private List<Booking> bookings = new ArrayList<>();

    public SeatAvailablity() {}

    public SeatAvailablity(Integer id, String seatId, String location) {
        this.id = id;
        this.seatId = seatId;
        this.location = location;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Booking> getBookings() {
        // Remove expired bookings before returning
        Iterator<Booking> iterator = bookings.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isExpired()) {
                iterator.remove();
            }
        }
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
