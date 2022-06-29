package com.lolsearcher.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.rank.Rank;

@Repository
public class JpaTestRepository {

	private final EntityManager em;
	
	@Autowired
	public JpaTestRepository(EntityManager em) {
		this.em = em;
	}
	
	public List<Rank> findAllRank(){
		String jpql = "SELECT r FROM Rank r";
		
		return em.createQuery(jpql, Rank.class)
				.getResultList();
	}

	public List<Summoner> findAllSummoner() {
		// TODO Auto-generated method stub
		String jpql = "SELECT s FROM Summoner s";
		
		return em.createQuery(jpql, Summoner.class)
				.getResultList();
	}
}
