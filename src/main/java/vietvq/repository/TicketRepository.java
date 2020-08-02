package vietvq.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vietvq.entity.Bus;
import vietvq.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
	List<Ticket> findByBusAndDate(Bus bus, Date date);
	List<Ticket> findByBus(Bus bus);
	Ticket findById(long id);
	void deleteById(long id);
}
