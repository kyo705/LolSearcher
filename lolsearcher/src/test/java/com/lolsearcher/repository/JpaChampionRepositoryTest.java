package com.lolsearcher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.entity.championstatic.Champion;
import com.lolsearcher.domain.entity.championstatic.ChampionCompKey;
import com.lolsearcher.repository.ChampionRepository.ChampionReository;

@ActiveProfiles("test")
@DataJpaTest
class JpaChampionRepositoryTest {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	ChampionReository jpaChampionRepository;
	
	@Test
	public void findChampionStatic() {
		//given
		Champion champion1 = new Champion();
		champion1.setCk(new ChampionCompKey("탈론", 22, "JUNGLE"));
		champion1.setWins(50);
		champion1.setLosses(40);
		
		Champion champion2 = new Champion();
		champion2.setCk(new ChampionCompKey("카타리나", 22, "MIDDLE"));
		champion2.setWins(60);
		champion2.setLosses(40);
		
		Champion champion3 = new Champion();
		champion3.setCk(new ChampionCompKey("카타리나", 21, "MIDDLE"));
		champion3.setWins(48);
		champion3.setLosses(53);
		
		Champion champion4 = new Champion();
		champion4.setCk(new ChampionCompKey("엘리스", 22, "JUNGLE"));
		champion4.setWins(70);
		champion4.setLosses(70);
		
		//DB에 저장되어있다고 가정(현재는 DB에 저장된 것이 아니라 영속성 컨택스트에 저장되어있음)
		em.persist(champion1);
		em.persist(champion2);
		em.persist(champion3);
		em.persist(champion4);
		
		//when
		List<Champion> middleChampions = jpaChampionRepository.findChamps("MIDDLE");
		List<Champion> jungleChampions = jpaChampionRepository.findChamps("JUNGLE");
		
		//then
		//jpaChampionRepository의 seasonId 필드 값이 22로 되어있기 때문에 22시즌 카타리나 값(1개)만 조회된다.
		assertThat(middleChampions.size()).isEqualTo(1);
		assertThat(middleChampions.get(0).getCk().getChampionId()).isEqualTo("īŸ����");
		
		//wins+losses 로 정렬해서 받기 때문에 엘리스,탈론 순으로 list채워짐(엘리스 게임수(140판), 탈론 게임수(90판))
		assertThat(jungleChampions.size()).isEqualTo(2);
		assertThat(jungleChampions.get(0).getCk().getChampionId()).isEqualTo("������"); 
		assertThat(jungleChampions.get(1).getCk().getChampionId()).isEqualTo("Ż��");
	}
}
