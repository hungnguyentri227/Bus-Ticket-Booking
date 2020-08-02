package vietvq.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vietvq.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
	Location findByLocationName(String locationName);
	
}
