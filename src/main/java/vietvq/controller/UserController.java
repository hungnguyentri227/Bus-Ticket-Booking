package vietvq.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vietvq.BookingApplication;
import vietvq.entity.Bill;
import vietvq.entity.Bus;
import vietvq.entity.Details;
import vietvq.entity.Location;
import vietvq.entity.Role;
import vietvq.entity.Route;
import vietvq.entity.Ticket;
import vietvq.entity.User;
import vietvq.repository.BillRepository;
import vietvq.repository.BusRepository;
import vietvq.repository.DetailRepository;
import vietvq.repository.DetailsRepository;
import vietvq.repository.LocationRepository;
import vietvq.repository.RouteRepository;
import vietvq.repository.TicketRepository;
import vietvq.services.RoleServiceImpl;
import vietvq.services.UserServiceImpl;
import vietvq.validator.UserValidator;

@Controller
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private RoleServiceImpl roleService;

	@Autowired
	private UserValidator userValidator;

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
	DetailRepository detailRepository;

	@Autowired
	DetailsRepository detailsRepository;
	
	@GetMapping("/searchdemo")
	public String searchdemo() {
		return "user/search";
	}

	@GetMapping("/403")
	public String error() {
//		return "errors/access_denined";
		return "redirect:/login";
	}

	// Access login.html
	@RequestMapping({ "/login" })
	public ModelAndView accessLoginPage() {
		ModelAndView model = new ModelAndView();
		if (getCurrentUser() == null) {
			model.setViewName("home/login");
			logger.info("No Current User.");
		} else {
			Set<Role> roles = getCurrentUser().getRoles();
			for (Role role : roles) {
				if (role.getRoleName().equals("admin")) {
					User user = new User();
					model.addObject("user", user);
					model.addObject("userName", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
					model.setViewName("redirect:/admin/adminPage");
					logger.info("Current User is ADMIN");
				} else if (role.getRoleName().equals("customer")) {
					model.setViewName("redirect:/user/dashboard");
					logger.info("Current User is CUSTOMER");
				} else if (role.getRoleName().equals("seller")) {
					model.setViewName("redirect:/seller/sellerDashboard");
					logger.info("Current User is SELLER");
				}
			}
		}
		return model;
	}
	
	@RequestMapping({"/"})
	public ModelAndView accessDefaultPage() {
		ModelAndView model = new ModelAndView();
		if (getCurrentUser() == null) {
			model.setViewName("/user/dashboard");
		} else {
			Set<Role> roles = getCurrentUser().getRoles();
			for (Role role : roles) {
				if (role.getRoleName().equals("admin")) {
					User user = new User();
					model.addObject("user", user);
					model.addObject("userName", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
					model.setViewName("redirect:/admin/adminPage");
				} else if (role.getRoleName().equals("customer")) {
					model.setViewName("redirect:/user/dashboard");
				} else if (role.getRoleName().equals("seller")) {
					model.setViewName("redirect:/seller/sellerDashboard");
				}
			}
		}
		return model;
	}

	// Access signup.html
	@GetMapping("/signup")
	public String accessSignUpPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		logger.info("Response to Sign up page");
		return "home/signup";
	}

	// Sign up handler
	@PostMapping("/signup")
	public String signUp(Model model, User user, BindingResult bindingResult) {
		userValidator.validate(user, bindingResult);
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user", "This email already exists!");
		}
		userService.saveUserRegister(user);
		model.addAttribute("msg", "User has been registered successfully!");
		model.addAttribute("user", new User());
		return "home/signup";
	}

	@GetMapping({ "/user/dashboard"})
	public String showDashboard(Model model) {

		List<Route> routes = routeRepository.findAll();
		model.addAttribute("user", getCurrentUser());
		model.addAttribute("routes", routes);

		return "user/dashboard";
	}

	private Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	@GetMapping(value = { "/searchBus" })
	public String searchBus(Model model, @RequestParam("cityFrom") String cityFrom,
			@RequestParam("cityTo") String cityTo, @RequestParam("date") java.sql.Date date) {

		model.addAttribute("user", getCurrentUser());

		Date currentDate = this.getCurrentDate();
		System.out.println(currentDate);
		if (date.after(currentDate)) {
			Location start = locationRepository.findByLocationName(cityFrom);
			Location des = locationRepository.findByLocationName(cityTo);
			Route route = routeRepository.findByDestinationAndStart(des, start);
			List<Bus> listBus = busRepository.findByRoute(route);
			model.addAttribute("busList", listBus);
			model.addAttribute("cityFrom", cityFrom);
			model.addAttribute("cityTo", cityTo);

			List<Route> routes = routeRepository.findAll();
			model.addAttribute("routes", routes);
			model.addAttribute("date", date);
			return "user/viewBus";
		} else {
			return "redirect:/user/dashboard";
		}
	}

	private Date convertStringToDate(String dateString) {
		String[] dateStringArr = dateString.split("-"); // String date format: 2020-07-23
		int year = 0;
		int month = 0;
		int day = 0;
		if (dateStringArr.length == 3) {
			year = Integer.parseInt(dateStringArr[0].trim()) - 1900;
			month = Integer.parseInt(dateStringArr[1].trim()) - 1;
			day = Integer.parseInt(dateStringArr[2].trim());
			Date date = new Date(year, month, day);
			return date;
		}
		return null;
	}

	@GetMapping(value = "/bookingView/{id}/{date}/{busName}/{cityFrom}/{cityTo}")
	public String showTicket(@PathVariable(name = "id") int id, @PathVariable("date") java.sql.Date date, Model model,
			@PathVariable(name = "busName") String busName, @PathVariable(name = "cityFrom") String cityFrom,
			@PathVariable(name = "cityTo") String cityTo) {
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
		model.addAttribute("busNaMe", busName);
		model.addAttribute("cityFrom", cityFrom);
		model.addAttribute("cityTo", cityTo);

		return "user/bookingView";
	}

	@GetMapping(value = "/bookTicket/{id}/{date}/{cityFrom}/{cityTo}")
	public String buyTicket(@PathVariable(name = "id") long busId,
			@RequestParam(required = false, name = "checkTicket") List<Integer> checkTicket,
			@PathVariable("date") java.sql.Date date,
			@PathVariable(name = "cityFrom") String cityFrom,
			@PathVariable(name = "cityTo") String cityTo
			, Model model) {
		
		User user = this.getCurrentUser();
		Bus bus = busRepository.findById(busId);
		String busname = bus.getBusName();
		
		if (checkTicket == null) {
			return "redirect:/bookingView/"+busId+"/"+date+"/"+busname+"/"+cityFrom+"/"+cityTo;
		}else {
			for (Integer seat : checkTicket) {
				Bill bill = new Bill();
				Ticket ticket = new Ticket();
				ticket.setDate(date);
				ticket.setBus(bus);
				ticket.setPrice(bus.getPrice());
				ticket.setSeatNumber(seat);
				bill.setUser(user);
				Details detail = new Details();
				detail.setBill(bill);
				detail.setTicket(ticket);
				billRepository.save(bill);
				ticketRepository.save(ticket);
				detailsRepository.save(detail);
			}
			model.addAttribute("checkTicket", checkTicket);
			return "redirect:/user/dashboard";
		}
		
	}

	@RequestMapping(value = { "/user/viewBooking" }, method = RequestMethod.GET)
	public String viewBooking(Model model) {
		User user = this.getCurrentUser();
		
		List<Bill> bills = billRepository.findByUser(user);
		List<Details> details = new ArrayList<Details>();
		
		for (Bill bill : bills) {
			details.add(detailsRepository.findByBillId(bill.getId()));
		}
		
		model.addAttribute("details", details);
		model.addAttribute("user", user);
		return "user/viewBook";
	}

	@RequestMapping(value = "/deleteTicket", method = RequestMethod.POST)
	public ModelAndView xoa(@RequestParam("DetailId") long DetailId) {
		ModelAndView model = new ModelAndView();
		Details details = detailsRepository.findById(DetailId);

		detailsRepository.deleteById(DetailId);
		billRepository.deleteById(details.getBill().getId());
		ticketRepository.deleteById(details.getTicket().getId());
		model.setViewName("redirect:/user/viewBooking");
		return model;
	}

	@RequestMapping(value = "/back")
	public ModelAndView goBack() {
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:/user/dashboard");
		return model;
	}

	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(authentication.getName());
		return user;
	}

	@RequestMapping(value = "/customer/information")
	public String information(Model model) {
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);

		model.addAttribute("user", getCurrentUser());

		model.addAttribute("username", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
		model.addAttribute("email", getCurrentUser().getEmail());
		model.addAttribute("date", date);
		return "home/information";
	}

}
