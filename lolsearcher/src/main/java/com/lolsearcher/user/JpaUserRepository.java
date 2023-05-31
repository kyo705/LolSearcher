package com.lolsearcher.user;

import com.lolsearcher.utils.encoding.PasswordEncoderUtils;
import lombok.RequiredArgsConstructor;
import org.idgeneration.annotation.IdGeneration;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RequiredArgsConstructor
@Repository
public class JpaUserRepository implements UserRepository {

	private final EntityManager em;

	@IdGeneration
	@Override
	public void save(User user) {

		em.persist(user);
	}

	@Override
	public Optional<User> findById(Long id) {

		return Optional
				.ofNullable(em.find(User.class, id));
	}

	@Override
	public Optional<User> findByEmail(String email) {

		String jpql = "select u from User u where u.email = :email";

		List<User> users = em.createQuery(jpql, User.class)
				.setParameter("email", email)
				.getResultList();

		if(users.size() >= 2) {
			throw new NonUniqueResultException();
		}
		if(users.size() == 1){
			return users.stream().findAny();
		}
		return Optional.empty();
	}

	@Override
	public void updateUser(User user, UserUpdateRequest request) {

		checkArgument(user != null, "user must be provided");

		if(request.getName().isPresent()){
			user.setUsername(request.getName().get());
		}
		if(request.getEmail().isPresent()){
			user.setEmail(request.getEmail().get());
		}
		if(request.getPassword().isPresent()){
			String encodedPassword = PasswordEncoderUtils.encodingPassword(request.getPassword().get());
			user.setPassword(encodedPassword);
		}
		if(request.getRole().isPresent()) {
			user.setRole(request.getRole().get());
		}
		if(request.getLoginSecurity().isPresent()) {
			user.setLoginSecurity(request.getLoginSecurity().get());
		}
	}

	@Override
	public void delete(Long id) {

		User user = em.find(User.class, id);
		em.remove(user);
	}




}
