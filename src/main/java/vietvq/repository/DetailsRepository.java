package vietvq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vietvq.entity.Bill;
import vietvq.entity.Details;

public interface DetailsRepository extends JpaRepository<Details, Long>{
	Details findById(long id);
	Details findByBillId(long id);
	void deleteById(long id);
}
