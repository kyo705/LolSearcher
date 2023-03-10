package com.lolsearcher.repository.user;

import com.lolsearcher.exception.exception.search.summoner.SameValueExistException;
import com.lolsearcher.model.entity.user.LolSearcherUser;
import library.idgenerator.annotation.IdGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaUserRepository implements UserRepository {

	private final EntityManager em;

	@IdGeneration
	@Override
	public void saveUser(LolSearcherUser user) {
		em.persist(user);
	}
	
	@Override
	public LolSearcherUser findUserByName(String username) {
		LolSearcherUser user = null;
		String jpql = "select u from LolSearcherUser u where u.username = :username";
		
		List<LolSearcherUser> users = em.createQuery(jpql, LolSearcherUser.class)
				.setParameter("username", username)
				.getResultList();
		
		if(users.size()>=2) {
			throw new SameValueExistException();
		}
		if(users.size()==1) {
			user = users.get(0);
		}
		return user;
	}

	@Override
	public LolSearcherUser findUserByEmail(String email) {
		LolSearcherUser user = null;
		String jpql = "select u from LolSearcherUser u where u.email = :email";
		
		List<LolSearcherUser> users = em.createQuery(jpql, LolSearcherUser.class)
				.setParameter("email", email)
				.getResultList();
		
		if(users.size()>=2) {
			throw new SameValueExistException();
		}
		if(users.size()==1) {
			user = users.get(0);
		}
		return user;
	}

	@Override
	public void updateSecurityLevel(LolSearcherUser user, int loginSecurityPolicyLevel) {

		user.setSecurityLevel(loginSecurityPolicyLevel);
	}

}
