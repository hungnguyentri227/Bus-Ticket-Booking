package vietvq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vietvq.entity.*;
import vietvq.repository.BillRepository;
import vietvq.repository.BusRepository;
import vietvq.repository.DetailsRepository;
import vietvq.repository.LocationRepository;
import vietvq.repository.RouteRepository;
import vietvq.repository.TicketRepository;
import vietvq.services.UserService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class SellerController {
	@Autowired
    UserService userService;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    BusRepository busRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BillRepository billRepository;
    @Autowired
    DetailsRepository detailsRepository;

    @RequestMapping(value = {"/seller/sellerDashboard"})
    public String viewSellerPage(Model model) {
        model.addAttribute("user", getCurrentUser());
        model.addAttribute("userName123", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
        List<Location> locationsxamlon = locationRepository.findAll();
        List<String> locationsList = new ArrayList<>();
        for (int i = 0; i < locationsxamlon.size(); i++) {
            locationsList.add(locationsxamlon.get(i).getLocationName());
            System.out.println(locationsList.get(i));
        }
        System.out.println(locationsList.size());
        model.addAttribute("locations", locationsList);
        return "seller/sellerDashboard";
    }

    @GetMapping(value = {"/sellerSearchBus"})
    public String searchBusSeller(Model model, @RequestParam("cityFrom") String cityFrom,
                                  @RequestParam("cityTo") String cityTo, @RequestParam("date") java.sql.Date date) {
        model.addAttribute("user", getCurrentUser());
        model.addAttribute("userName123", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
        Date currentDate = this.getCurrentDate();
        Location start = locationRepository.findByLocationName(cityFrom);
        Location des = locationRepository.findByLocationName(cityTo);
        Route route = routeRepository.findByDestinationAndStart(des, start);
        List<Bus> listBus = busRepository.findByRoute(route);
        List<Location> locationsxamlon = locationRepository.findAll();
        List<String> locationsList = new ArrayList<>();
        for (int i = 0; i < locationsxamlon.size(); i++) {
            locationsList.add(locationsxamlon.get(i).getLocationName());
            System.out.println(locationsList.get(i));
        }
        System.out.println(locationsList.size());
        model.addAttribute("locations", locationsList);
        model.addAttribute("busList", listBus);
        model.addAttribute("cityFrom", cityFrom);
        model.addAttribute("cityTo", cityTo);

        List<Route> routes = routeRepository.findAll();
        model.addAttribute("routes", routes);
        model.addAttribute("date", date);

        return "seller/sellerDashboard";
    }

    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    @GetMapping(value = "/sellerViewTicket/{id}/{date}")
    public String showTicketForSeller(@PathVariable(name = "id") int id, Model model, @PathVariable("date") java.sql.Date date) {
    	 model.addAttribute("user", getCurrentUser());
    	Bus bus = busRepository.findById(id);
        model.addAttribute("seat", bus.getSeatCapacity());
        model.addAttribute("bus", bus);
        List<Ticket> ticketList = ticketRepository.findByBusAndDate(bus, date);
        HashMap<Integer, Boolean> listSeat = new HashMap<Integer, Boolean>();
        for (int i = 0; i < bus.getSeatCapacity(); i++) {
            listSeat.put(i + 1, true);
        }
        for (int i = 0; i < ticketList.size(); i++) {
            if (listSeat.containsKey(ticketList.get(i).getSeatNumber())) {
                listSeat.put(ticketList.get(i).getSeatNumber(), false);
            }
        }
        model.addAttribute("listSeat", listSeat);
        model.addAttribute("ticketList", ticketList);
        model.addAttribute("date", date);
        return "seller/sellerViewTicket";
    }

    @RequestMapping(value = "/filterTicket/{id}/{date}")
    public String filterTicket(@PathVariable(name = "id") int id, @PathVariable("date") java.sql.Date date, Model model,
                               @RequestParam(name = "filter") String filter) {
    	model.addAttribute("user", getCurrentUser());
        Bus bus = busRepository.findById(id);
        model.addAttribute("seat", bus.getSeatCapacity());
        model.addAttribute("bus", bus);
        List<Ticket> ticketList = ticketRepository.findByBusAndDate(bus, date);
        HashMap<Integer, Boolean> listSeat = new HashMap<Integer, Boolean>();
        HashMap<Integer, Boolean> listSeat1 = new HashMap<Integer, Boolean>();
        for (int i = 0; i < bus.getSeatCapacity(); i++) {
            listSeat1.put(i + 1, true);
        }

        if (filter.equalsIgnoreCase("Available tickets")) {
			if (ticketList.isEmpty()) {
				listSeat = listSeat1;
			} else {
				for (int i = 0; i < ticketList.size(); i++) {
					System.out.println(ticketList.get(i).getSeatNumber());
					if (listSeat1.containsKey(ticketList.get(i).getSeatNumber())) {
						listSeat1.remove(ticketList.get(i).getSeatNumber());
						listSeat = listSeat1;
					}
				}
			}
		}
        if (filter.equalsIgnoreCase("Unvailable tickets")) {
            for (int i = 0; i < ticketList.size(); i++) {
                if (listSeat1.containsKey(ticketList.get(i).getSeatNumber())) {
                    listSeat.put(ticketList.get(i).getSeatNumber(), false);
                }
            }
        }
        model.addAttribute("listSeat", listSeat);
        model.addAttribute("ticketList", ticketList);
        model.addAttribute("date", date);
        return "seller/sellerViewTicket";
    }

    @RequestMapping(value = {"/seller/sellerEditTicket"}, method = RequestMethod.GET)
    public String viewBooking(Model model) {
    	model.addAttribute("user", getCurrentUser());
        User user = this.getCurrentUser();
        List<Bill> bills = billRepository.findAll();
        List<Details> details = new ArrayList<Details>();
        for (Bill bill : bills) {
        	System.out.println(bill.getId());
            details.add(detailsRepository.findByBillId(bill.getId()));
        }
        System.out.println(details.get(2).getId());
        for (Details detail : details) {
        	System.out.println("Dmm" + detail.getId());
        }
        
//        System.out.println(details.get(0).getBill().getUser().getEmail());
        model.addAttribute("details", details);
        return "seller/sellerEditTicket";
    }

    @RequestMapping(value = "/deleteTicket1", method = RequestMethod.POST)
    public ModelAndView xoa(@RequestParam("DetailId") long DetailId) {
        ModelAndView model = new ModelAndView();
        Details details = detailsRepository.findById(DetailId);

        detailsRepository.deleteById(DetailId);
        billRepository.deleteById(details.getBill().getId());
        ticketRepository.deleteById(details.getTicket().getId());
        model.setViewName("redirect:/seller/sellerEditTicket");
        return model;
    }

    @RequestMapping(value = "/back1")
    public ModelAndView goBack() {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/seller/sellerDashboard");
        return model;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        return user;
    }
    
    @RequestMapping(value = "/seller/information")
	public String information(Model model) {
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);

		model.addAttribute("user", getCurrentUser());

		model.addAttribute("username", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
		model.addAttribute("email", getCurrentUser().getEmail());
		model.addAttribute("date", date);
		return "seller/informationSeller";
	}
}
