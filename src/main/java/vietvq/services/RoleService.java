package vietvq.services;

import java.util.List;

import vietvq.entity.Role;
import vietvq.entity.User;

public interface RoleService {
	public List<Role> listAllRole();

	public void setRole(User user, String role);
	
	public List<User> findUsersByRole(Role role);

	public Role findRoleByRoleName(String role);
}
