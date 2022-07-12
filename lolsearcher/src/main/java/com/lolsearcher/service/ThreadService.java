package com.lolsearcher.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.entity.summoner.match.Match;

@Service
public class ThreadService {

	private final EntityManager em;
	
	@Autowired
	public ThreadService(EntityManager em) {
		this.em = em;
	}
	
	//트랜잭션 고립단계를 serializable하여 한번에 matches를 db에 반영하는 방식과
	//match 하나하나를 트랜잭션 생성해 db에 저장하는 방식 중
	//고립단계를 3단계로 한 이유는 db I/O 비용을 줄일 수 있고 클라이언트에게 제공되는 서비스 속도에는 영향이 없기 때문
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void saveMatches(List<Match> matches) {
		for(Match match : matches) {
			em.persist(match);
		}
	}
	
	@Transactional(readOnly = true)
	public Match readMatch(String matchId) {
		return em.find(Match.class, matchId);
	}
}
