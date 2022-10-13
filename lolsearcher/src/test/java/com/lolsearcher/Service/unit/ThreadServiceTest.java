package com.lolsearcher.Service.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.service.ThreadService;

@ActiveProfiles("test")
@SpringBootTest
public class ThreadServiceTest {

	@Autowired
	private ThreadService threadService;
	
	@Autowired
	ExecutorService threadpool;
	
	@Autowired
	private EntityManager em;
	
	@Test
	void noRollbackByException() {
		Runnable thread1 = ()->{
			Match match1 = new Match();
			match1.setMatchId("match1");
			Match match2 = new Match();
			match2.setMatchId("match2");
			Match match3 = new Match();
			match3.setMatchId("match3");
			
			List<Match> matches = new ArrayList<>();
			matches.add(match1);
			matches.add(match2);
			matches.add(match3);
			
			//threadService.saveMatches(matches);

			
		};
		
		Runnable thread2 = ()->{
			Match match4 = new Match();
			match4.setMatchId("match4");
			Match match5 = new Match();
			match5.setMatchId("match2");
			Match match6 = new Match();
			match6.setMatchId("match5");
			
			List<Match> matches2 = new ArrayList<>();
			matches2.add(match4);
			matches2.add(match5);
			matches2.add(match6);
			
			//threadService.saveMatches(matches2);
		};
		
		threadpool.submit(thread1);
		threadpool.submit(thread2);
		
		try {
			System.out.println("스레드 일시 정지");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String jpql = "select m from Match m";
		List<Match> matches = em.createQuery(jpql, Match.class)
			.getResultList();
		
		for(Match match : matches) {
			System.out.println(match.getMatchId());
		}
		assertThat(matches.size()).isEqualTo(5);
	}
}
