package vietvq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vietvq.entity.Location;
import vietvq.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	Route findById(long id);
	Route findByDestinationAndStart(Location destination, Location start);
	
}
