package com.lolsearcher.repository.userrepository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.entity.user.LolSearcherUser;

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
		String jpql = "select u from LolSearcherUser u where u.username = :username";
		
		List<LolSearcherUser> list = em.createQuery(jpql, LolSearcherUser.class)
				.setParameter("username", username)
				.getResultList();
		
		if(list.size()==1){
			return list.get(0);
		}else if(list.size()==0) {
			return null;
		}
		
		return null;
	}

	@Override
	public LolSearcherUser findUserByEmail(String email) {
		String jpql = "select u from LolSearcherUser u where u.email = :email";
		
		List<LolSearcherUser> list = em.createQuery(jpql, LolSearcherUser.class)
				.setParameter("email", email)
				.getResultList();
		
		if(list.size()==1){
			return list.get(0);
		}else if(list.size()==0) {
			return null;
		}
		
		return null;
	}

}
