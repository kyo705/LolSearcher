package com.lolsearcher.search.summoner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JpaSummonerRepository implements SummonerRepository {

	private final EntityManager em;
	
	@Override
	public Optional<Summoner> findById(String summonerId){

		String jpql = "SELECT s FROM Summoner s WHERE s.summonerId = :summonerId";
		
		return Optional.of(
				em.createQuery(jpql, Summoner.class)
						.setParameter("summonerId", summonerId)
						.getSingleResult()
				)
				.stream()
				.peek(Summoner::validate)
				.findAny();
	}
	
	@Override
	public List<Summoner> findByName(String summonerName) {

		String jpql = "SELECT s FROM Summoner s WHERE s.summonerName = :summonerName";

		return em.createQuery(jpql, Summoner.class)
				.setParameter("summonerName", summonerName)
				.getResultList()
				.stream()
				.peek(Summoner::validate)
				.collect(Collectors.toList());
	}

}
