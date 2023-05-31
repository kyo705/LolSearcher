package com.lolsearcher.user;

import java.util.Optional;

public interface UserRepository {

	void save(User user);

	Optional<User> findById(Long id);
	Optional<User> findByEmail(String email);

	void updateUser(User user, UserUpdateRequest request);

	void delete(Long id);


}
