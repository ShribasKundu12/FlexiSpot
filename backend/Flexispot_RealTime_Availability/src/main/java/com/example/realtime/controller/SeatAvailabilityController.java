//package com.example.realtime.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.realtime.model.SeatAvailablity;
//import com.example.realtime.service.SeatAvailabilityScheduler;
//import com.example.realtime.service.SeatAvailabilityService;
//
//@Controller
//@RestController
//@CrossOrigin(origins = "http://localhost:3000/") 
//@RequestMapping("/seat")
//public class SeatAvailabilityController {
//
//	@Autowired
//	private SeatAvailabilityService service;
//	
//	@Autowired
//	private SeatAvailabilityScheduler serviceScheduler;
//	
//	@GetMapping("/availability")
//	public List<SeatAvailablity> getSeatAvailability() {
//		serviceScheduler.autoReleaseExpiredSeats();
////	    return service.getAllSeats();
//		return service.getAllSeatsRaw();
//	}
//	
//	@PostMapping("/create")
//    public ResponseEntity<SeatAvailablity> createSeat(@RequestBody SeatAvailablity seat) {
//        SeatAvailablity savedSeat = service.saveSlot(seat);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedSeat);
//    }
//	
//	
//	  @PutMapping("/cancel/{id}")
//	    public ResponseEntity<String> cancelSeat(@PathVariable Integer id,@RequestParam String user) {
//	        boolean result = service.cancelSeat(id,user);
//	        if (result) {
//	            return ResponseEntity.ok("Seat cancelled successfully");
//	        } else {
//	            return ResponseEntity.notFound().build();
//	        }
//	    }
//		
//	
//}




package com.example.realtime.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.realtime.model.SeatAvailablity;
import com.example.realtime.service.SeatAvailabilityScheduler;
import com.example.realtime.service.SeatAvailabilityService;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping("/seat")
public class SeatAvailabilityController {

    @Autowired
    private SeatAvailabilityService service;

    @Autowired
    private SeatAvailabilityScheduler serviceScheduler;

    @GetMapping("/all")
    public List<SeatAvailablity> getAllSeats() {
        serviceScheduler.autoRemoveExpiredBookings();
        return service.getAllSeats();
    }

    @PostMapping("/create")
    public ResponseEntity<SeatAvailablity> createSeat(@RequestBody SeatAvailablity seat) {
        SeatAvailablity savedSeat = service.saveOrUpdateSeat(seat);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSeat);
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookSeat(
            @RequestParam String seatId,
            @RequestParam String bookedBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam int durationMinutes
            ,
            @RequestParam String password    ) {

        String response = service.bookSeat(seatId, bookedBy, startTime, durationMinutes,password);
        if (response.equals("Booking successful.")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<String> cancelSeat(
            @RequestParam String seatId,
            @RequestParam String bookedBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam String password
    		) {

        boolean result = service.cancelBooking(seatId,bookedBy, password, startTime);
        if (result) {
            return ResponseEntity.ok("Booking cancelled successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matching booking found.");
        }
    }
}
