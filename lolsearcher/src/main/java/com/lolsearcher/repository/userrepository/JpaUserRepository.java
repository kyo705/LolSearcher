package com.lolsearcher.repository.userrepository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.entity.user.LolSearcherUser;
import com.lolsearcher.exception.summoner.SameValueExistException;

@Repository
public class JpaUserRepository implements UserRepository {

	private final EntityManager em;
	
	public JpaUserRepository(EntityManager em) {
		this.em = em;
	}
	
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

}
