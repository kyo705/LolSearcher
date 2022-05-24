package com.lolsearcher.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.lolsearcher.domain.entity.championstatic.Champion;
import com.lolsearcher.domain.entity.championstatic.ChampionCompKey;
import com.lolsearcher.repository.ChampionRepository.ChampionReository;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class JpaChampionRepositoryTest {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	ChampionReository jpaChampionRepository;
	
	@Test
	public void findChampionStatic() {
		//given
		Champion champion1 = new Champion();
		champion1.setCk(new ChampionCompKey("Ż��", 22, "JUNGLE"));
		champion1.setWins(50);
		champion1.setLosses(40);
		
		Champion champion2 = new Champion();
		champion2.setCk(new ChampionCompKey("īŸ����", 22, "MIDDLE"));
		champion2.setWins(60);
		champion2.setLosses(40);
		
		Champion champion3 = new Champion();
		champion3.setCk(new ChampionCompKey("īŸ����", 21, "MIDDLE"));
		champion3.setWins(48);
		champion3.setLosses(53);
		
		Champion champion4 = new Champion();
		champion4.setCk(new ChampionCompKey("������", 22, "JUNGLE"));
		champion4.setWins(70);
		champion4.setLosses(70);
		
		//DB�� ����Ǿ��ִٰ� ����(����� DB�� ����� ���� �ƴ϶� ���Ӽ� ���ý�Ʈ�� ����Ǿ�����)
		em.persist(champion1);
		em.persist(champion2);
		em.persist(champion3);
		em.persist(champion4);
		
		//when
		List<Champion> middleChampions = jpaChampionRepository.findChamps("MIDDLE");
		List<Champion> jungleChampions = jpaChampionRepository.findChamps("JUNGLE");
		
		//then
		//jpaChampionRepository�� seasonId �ʵ� ���� 22�� �Ǿ��ֱ� ������ 22���� īŸ���� ��(1��)�� ��ȸ�ȴ�.
		assertThat(middleChampions.size()).isEqualTo(1);
		assertThat(middleChampions.get(0).getCk().getChampionId()).isEqualTo("īŸ����");
		
		//wins+losses �� �����ؼ� �ޱ� ������ ������,Ż�� ������ listä����(������ ���Ӽ�(140��), Ż�� ���Ӽ�(90��))
		assertThat(jungleChampions.size()).isEqualTo(2);
		assertThat(jungleChampions.get(0).getCk().getChampionId()).isEqualTo("������"); 
		assertThat(jungleChampions.get(1).getCk().getChampionId()).isEqualTo("Ż��");
	}
}
