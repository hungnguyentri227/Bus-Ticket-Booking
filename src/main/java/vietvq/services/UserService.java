package vietvq.services;

import java.util.List;

import vietvq.entity.Role;
import vietvq.entity.User;

public interface UserService {

	public User findUserByEmail(String email);

	public void saveUser(User user, String role);

	public List<User> getAllUsersFilterByNameAndEmail(String keyword);

	public User getUserById(Long id);

	public void saveUserEdit(User user);

	public void saveUserRegister(User user);

	public List<User> findByRoles(Role role);

	public void setRole(User user,String role);
}
