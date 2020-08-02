
package vietvq.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import vietvq.entity.Role;
import vietvq.entity.User;
import vietvq.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleServiceImpl roleService;

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void saveUser(User user, String role) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		Role userRole = roleService.findRoleByRoleName(role);
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
		logger.info("debug enabled: {}", logger.isDebugEnabled());
	    logger.trace("trace");
	    logger.debug("debug");
	    logger.info("info");
	    logger.warn("warn");
	    logger.error("error");

	}

	@Override
	public List<User> getAllUsersFilterByNameAndEmail(String keyword) {
		return userRepository.search(keyword);
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).get();
	}

	@Override
	public void saveUserEdit(User user) {
		userRepository.update(user.getFirstname(), user.getLastname(), user.getId());

	}

	@Override
	public void saveUserRegister(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		Role userRole = roleService.findRoleByRoleName("customer");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);

	}

	@Override
	public List<User> findByRoles(Role role) {
		return userRepository.findByRoles(role);
	}

	@Override
	public void setRole(User user, String role) {

	}

}
