package vietvq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import vietvq.entity.Bill;
import vietvq.entity.Bus;
import vietvq.entity.Details;
import vietvq.entity.Route;
import vietvq.entity.Ticket;
import vietvq.entity.User;
import vietvq.repository.BillRepository;
import vietvq.repository.BusRepository;
import vietvq.repository.DetailsRepository;
import vietvq.repository.LocationRepository;
import vietvq.repository.RouteRepository;
import vietvq.repository.TicketRepository;
import vietvq.services.UserService;

@SpringBootApplication
public class BookingApplication {
	private static Logger logger = LoggerFactory.getLogger(BookingApplication.class);
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(BookingApplication.class, args);
		logger.info("debug enabled: {}", logger.isDebugEnabled());
	    logger.trace("trace");
	    logger.debug("debug");
	    logger.info("info");
	    logger.warn("warn");
	    logger.error("error");
	    
//	    UserService demoService = applicationContext.getBean(UserService.class);
//	    demoService.saveUser(user, role);
	}
	
//	@Override
//	public void run(String... args) throws Exception {
//		// TODO Auto-generated method stub
//		
//	}

}
