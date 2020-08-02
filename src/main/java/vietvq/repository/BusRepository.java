package vietvq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vietvq.entity.Bus;
import vietvq.entity.Route;

public interface BusRepository extends JpaRepository<Bus, Long>{
	Bus findById(long id);
	List<Bus> findByRoute(Route route);
}
