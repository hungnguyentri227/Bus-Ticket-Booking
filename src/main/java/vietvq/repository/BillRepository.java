package vietvq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vietvq.entity.Bill;
import vietvq.entity.User;

public interface BillRepository extends JpaRepository<Bill, Long> {
	List<Bill> findByUser(User user);
	void deleteById(long id);
}
