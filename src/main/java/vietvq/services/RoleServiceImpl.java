package vietvq.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import vietvq.entity.Role;
import vietvq.entity.User;
import vietvq.repository.RoleRepository;
import vietvq.repository.UserRepository;

@Service
public class RoleServiceImpl implements RoleService {

	@Qualifier("roleRepository")
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public List<Role> listAllRole() {
		return roleRepository.findAll();
	}

	@Override
	public void setRole(User user, String role) {
		Role userRole = roleRepository.findByRoleName(role);
		roleRepository.setRole(userRole.getId(), user.getId());
	}

	@Override
	public List<User> findUsersByRole(Role role) {
		return userRepository.findByRoles(role);
	}

	@Override
	public Role findRoleByRoleName(String role) {
		return roleRepository.findByRoleName(role);
	}

}
