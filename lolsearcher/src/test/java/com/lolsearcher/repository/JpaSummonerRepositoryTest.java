package com.lolsearcher.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.match.Member;
import com.lolsearcher.domain.entity.summoner.match.MemberCompKey;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class JpaSummonerRepositoryTest {

	@Autowired
	EntityManager em;
	
	@Autowired
	SummonerRepository jpaSummonerRepository;
	
	@Autowired
	JpaTestRepository jpaTestRepository;
	
	@Autowired
	MatchRepository repos;
	
	@Test
	public void testMappedBy() {
		Match match = new Match();
		match.setMatchId("match1");
		List<Member> members = new ArrayList<Member>();
		match.setMembers(members);
		
		Member member1 = new Member();
		member1.setCk(new MemberCompKey("match1", 0));
		member1.setChampionid("zed");
		member1.setSummonerid("id1");
		member1.setMatch(match);
		
		jpaSummonerRepository.saveMatch(match);
		em.flush();
		em.clear();
		
		List<Match> matches = jpaSummonerRepository.findMatchList("id1", -1, "all", 5);
		Match match1 = matches.get(0);
		List<Member> members2 = match1.getMembers();
		members2.get(0).setChampionid("yasuo");
		em.flush();
		em.clear();
		
		List<Match> matches2 = jpaSummonerRepository.findMatchList("id1", -1, "all", 5);
		Match match2 = matches2.get(0);
		List<Member> members3 = match2.getMembers();
		System.out.println(members3.get(0)==members2.get(0));
		System.out.println(members3.get(0).getChampionid());
		
	}
	
	@Test
	public void saveSummonerCase1() {
		//test Case 1 : 기존 DB에 데이터 존재하지 않을 경우
		
		//given
		Summoner summoner = new Summoner();
		summoner.setName("푸켓푸켓");
		summoner.setId("id1");
		Summoner summoner2 = new Summoner();
		summoner.setName("푸켓푸켓2");
		summoner.setId("id2");
		Summoner summoner3 = new Summoner();
		summoner.setName("푸켓푸켓3");
		summoner.setId("id3");
		
		//when
		List<Summoner> summoners2 = new ArrayList<>();
		summoners2.add(summoner);
		summoners2.add(summoner2);
		summoners2.add(summoner3);
		//repos.saveAll(summoners2);
		System.out.println("hi");
		//then
		List<Summoner> summoners = jpaTestRepository.findAllSummoner();
		assertThat(summoners.size()).isEqualTo(1);
		Summoner dbSummoner = summoners.get(0);
		assertThat(dbSummoner.getId()).isEqualTo(summoner.getId());
		assertThat(dbSummoner.getName()).isEqualTo(summoner.getName());
	}
	
	@Test
	public void saveSummonerCase2() {
		//test Case 2 : 기존 DB에 데이터 존재하지만 
		//사용자가 저장한 데이터와 다른 데이터일 경우(pk 값이 다를 경우)
		
		//given
		Summoner summoner1 = new Summoner();
		summoner1.setName("푸켓푸켓");
		summoner1.setId("id1");
		
		Summoner summoner2 = new Summoner();
		summoner2.setName("갓버수분장");
		summoner2.setId("id2");
		em.persist(summoner2);
		em.flush();
		em.clear();
		
		//when
		jpaSummonerRepository.saveSummoner(summoner1);
		em.flush();
		em.clear();
		
		//then
		List<Summoner> summoners = jpaTestRepository.findAllSummoner();
		assertThat(summoners.size()).isEqualTo(2);
		assertThat(summoners.get(0).getName()).isEqualTo(summoner2.getName());
		assertThat(summoners.get(1).getName()).isEqualTo(summoner1.getName());
	}
	
	@Test
	public void saveSummonerCase3() {
		//test Case 3 : 기존 DB에 데이터 존재하고 
		//사용자가 저장한 데이터와 같은 데이터일 경우(pk 값이 같을 경우)
		
		//given
		Summoner summoner1 = new Summoner();
		summoner1.setName("푸켓푸켓");
		summoner1.setId("id1");
		summoner1.setPrimaryId(1);
		
		Summoner summoner2 = new Summoner();
		summoner2.setName("갓버수분장");
		summoner2.setId("id1");
		em.persist(summoner2);
		em.flush();
		em.clear();
		
		//when
		jpaSummonerRepository.saveSummoner(summoner1);
		em.flush();
		em.clear();
		
		//then
		List<Summoner> summoners = jpaTestRepository.findAllSummoner();
		for(Summoner summoner : summoners) {
			System.out.println(summoner.getPrimaryId()+" "+ summoner.getId()+" "+summoner.getName());
		}
		
		assertThat(summoners.size()).isEqualTo(2);
		//assertThat(summoners.get(0).getName()).isEqualTo(summoner1.getName());
	}
	
	@Test
	public void findSummonerByIdCase1() {
		//Case 1 :db에 데이터 존재하지 않을 때
		
		//given
		String summonerid = "summonerId1";
		
		//when&then
		assertThrows(EmptyResultDataAccessException.class,()->jpaSummonerRepository.findSummonerById(summonerid));
		List<Summoner> summoners = jpaTestRepository.findAllSummoner();
		assertThat(summoners.size()).isEqualTo(0);
	}
	
	@Test
	public void findSummonerByIdCase2() {
		//Case 1 :db에 데이터 존재할 때
		
		//given
		String summonerid = "summonerId1";
		
		Summoner summoner = new Summoner();
		summoner.setName("푸켓푸켓");
		summoner.setId(summonerid);
		jpaSummonerRepository.saveSummoner(summoner); //해당 메소드는 위의 테스트 케이스들로 검증함
		em.flush();
		em.clear();
		
		
		//when
		Summoner dbsummoner = jpaSummonerRepository.findSummonerById(summonerid);
		em.flush();
		em.clear();
		
		//then
		assertThat(dbsummoner.getId()).isEqualTo(summonerid);
		assertThat(dbsummoner.getName()).isEqualTo(summoner.getName());
		
		List<Summoner> dbSummoners = jpaTestRepository.findAllSummoner();
		assertThat(dbSummoners.size()).isEqualTo(1);
	}
	
	@Test
	public void bulkInsertTest() {
		Match match = new Match();
		match.setMatchId("match1");
		Match match2 = new Match();
		match2.setMatchId("match2");
		Match match3 = new Match();
		match3.setMatchId("match3");
		Match match4 = new Match();
		match4.setMatchId("match4");
		
		em.persist(match);
		em.flush();
		em.persist(match2);
		em.flush();
		em.persist(match3);
		em.flush();
		em.persist(match4);
		/*List<Match> matches = new ArrayList<>();
		matches.add(match);
		matches.add(match2);
		matches.add(match3);
		matches.add(match4);
		repos.saveAll(matches);*/
		
		em.flush();
	}
}
