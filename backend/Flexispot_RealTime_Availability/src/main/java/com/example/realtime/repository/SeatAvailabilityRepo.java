package com.example.realtime.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.realtime.model.SeatAvailablity;

@Repository
public interface SeatAvailabilityRepo extends JpaRepository<SeatAvailablity,Integer> {

//	List<SeatAvailablity> findByIsAvailableTrue();

	Optional<SeatAvailablity> findBySeatId(String seatId);
	
	
	
}
