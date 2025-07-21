package com.example.realtime.model;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;

@Embeddable
public  class Booking {
	private String bookedBy;
	private LocalDateTime startTime;
	private Integer durationMinutes;
	private String password; // 🔐 New field

	public Booking() {}

	public Booking(String bookedBy, LocalDateTime startTime, Integer durationMinutes, String password) {
	    this.bookedBy = bookedBy;
	    this.startTime = startTime;
	    this.durationMinutes = durationMinutes;
	    this.password = password;
	}

	public String getBookedBy() {
	    return bookedBy;
	}

	public void setBookedBy(String bookedBy) {
	    this.bookedBy = bookedBy;
	}

	public LocalDateTime getStartTime() {
	    return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
	    this.startTime = startTime;
	}

	public Integer getDurationMinutes() {
	    return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
	    this.durationMinutes = durationMinutes;
	}

	public String getPassword() {
	    return password;
	}

	public void setPassword(String password) {
	    this.password = password;
	}

	public boolean isExpired() {
	    return LocalDateTime.now().isAfter(startTime.plusMinutes(durationMinutes));
	}

}
