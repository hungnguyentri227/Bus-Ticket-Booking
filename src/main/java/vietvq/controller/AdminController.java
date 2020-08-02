package vietvq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vietvq.entity.User;
import vietvq.services.RoleService;
import vietvq.services.UserService;

import java.util.List;

@Controller
public class AdminController {
	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@RequestMapping(value = { "/admin/adminPage" }, method = RequestMethod.GET)
	public String signupAdmin(Model model) {
		model.addAttribute("user", getCurrentUser());
		model.addAttribute("userName", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
		return "admin/adminPage";

	}

	@RequestMapping(value = "/admin/information")
	public String information(Model model) {
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);

		model.addAttribute("user", getCurrentUser());

		model.addAttribute("username", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
		model.addAttribute("email", getCurrentUser().getEmail());
		model.addAttribute("date", date);
		return "admin/informationAdmin";
	}

	@RequestMapping(value = { "/admin/adminEditPage/{id}" }, method = RequestMethod.GET)
	public ModelAndView showEditProductPage(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("admin/adminEditPage");
		User user = userService.getUserById(id);
		mav.addObject("user", user);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user1 = userService.findUserByEmail(auth.getName());
		mav.addObject("userName", user1.getFirstname() + " " + user1.getLastname());
		return mav;
	}

	@RequestMapping(value = "/saveEditUser", method = RequestMethod.POST)
	public String saveEditUser(@ModelAttribute("user") User user) {
		userService.saveUserEdit(user);
		return "redirect:/admin/viewUser";
	}

	@RequestMapping(value = { "/admin/setRoleUser/{id}" }, method = RequestMethod.GET)
	public ModelAndView showSetRole(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("admin/setRoleUser");
		User user = userService.getUserById(id);
		mav.addObject("user", user);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user1 = userService.findUserByEmail(auth.getName());
		mav.addObject("userName", user1.getFirstname() + " " + user1.getLastname());
		return mav;
	}

	@RequestMapping(value = "/home/search")
	public String saveNewUser(@Param("keyword") String keyword, Model model) {
		List<User> listUser = userService.getAllUsersFilterByNameAndEmail(keyword);
		model.addAttribute("listUser", listUser);
		model.addAttribute("keyword", keyword);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user1 = userService.findUserByEmail(auth.getName());
		model.addAttribute("userName", user1.getFirstname() + " " + user1.getLastname());
		model.addAttribute("user", user1);
		return "/admin/viewUser";
	}

	@RequestMapping(value = { "/admin/updateRole" }, method = RequestMethod.POST)
	public ModelAndView updateUser(@ModelAttribute("user") User user,
			@RequestParam(value = "roleRadio", required = false) String role) {
		ModelAndView model = new ModelAndView();

		if (role == null) {
			model.setViewName("redirect:/admin/setRoleUser/" + user.getId());
		} else {
			roleService.setRole(user, role);
			model.setViewName("redirect:/admin/viewUser");
		}
		return model;
	}

	@RequestMapping(value = { "/admin/viewUser" }, method = RequestMethod.GET)
	public String showNewProductPage(Model model) {
		model.addAttribute("user", getCurrentUser());
		model.addAttribute("userName", getCurrentUser().getFirstname() + " " + getCurrentUser().getLastname());
		/* model.setViewName("admin/viewUser"); */
		return "admin/viewUser";
	}

	@RequestMapping(value = { "/saveNewUser" }, method = RequestMethod.POST)
	public ModelAndView saveNewUser(User user, @RequestParam("roleRadioNew") String role, BindingResult bindingResult) {
		ModelAndView model = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());

		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user", "This email already exists!");
		}
		if (bindingResult.hasErrors()) {
		} else {
			userService.saveUser(user, role);
			model.addObject("msg", "User has been registered successfully!");
			model.addObject("user213", new User());
			model.addObject("user", getCurrentUser());
		}
		
		model.setViewName("admin/adminPage");
		return model;
	}

	@RequestMapping(value = "/filterUser")
	public String filterUser(Model model, @RequestParam("radioRole") String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<User> listUser = userService.findByRoles(roleService.findRoleByRoleName(role));
		model.addAttribute("listUser", listUser);
		model.addAttribute("userName", user.getFirstname() + " " + user.getLastname());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("user", user);
		return "/admin/viewUser";
	}

	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(authentication.getName());
		return user;
	}
}
