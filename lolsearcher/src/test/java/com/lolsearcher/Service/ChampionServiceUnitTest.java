package com.lolsearcher.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lolsearcher.domain.Dto.championstatic.ChampionDto;
import com.lolsearcher.domain.Dto.championstatic.TotalChampDto;
import com.lolsearcher.domain.entity.championstatic.Champion;
import com.lolsearcher.domain.entity.championstatic.ChampionCompKey;
import com.lolsearcher.domain.entity.championstatic.enemy.ChampEnemy;
import com.lolsearcher.domain.entity.championstatic.enemy.ChampEnemyCompKey;
import com.lolsearcher.domain.entity.championstatic.item.ChampItem;
import com.lolsearcher.domain.entity.championstatic.item.ChampItemCompKey;
import com.lolsearcher.repository.ChampionRepository.JpaChampionRepository;
import com.lolsearcher.service.ChampionService;

@ExtendWith(MockitoExtension.class)
class ChampionServiceUnitTest {

	@Mock
	JpaChampionRepository championReository;
	
	ChampionService championService;
	
	@BeforeEach
	void setup() {
		championService = new ChampionService(championReository);
	}
	
	@Test
	void getChampions() {
		//given
		List<Champion> champions = new ArrayList<>();
		Champion champion1 = new Champion();
		champion1.setCk(new ChampionCompKey("Å»·Ð",22,"MIDDLE"));
		champion1.setWins(50);
		champion1.setLosses(55);
		
		Champion champion2 = new Champion();
		champion2.setCk(new ChampionCompKey("Ä«Å¸¸®³ª",22,"MIDDLE"));
		champion2.setWins(60);
		champion2.setLosses(56);
		
		champions.add(champion1);
		champions.add(champion2);
		
		when(championReository.findChamps("MIDDLE")).thenReturn(champions);
		
		//when
		List<ChampionDto> championDtos = championService.getChampions("MIDDLE");
		
		//then
		assertThat(championDtos.size()).isEqualTo(2);
		assertThat(championDtos.get(0).getChampionId()).isEqualTo(champions.get(0).getCk().getChampionId());
	}
	
	@Test
	void getChampionDetail() {
		
		//given
		List<ChampItem> championItems = new ArrayList<>();
		ChampItem champitem1 = new ChampItem();
		champitem1.setCk(new ChampItemCompKey("Å»·Ð", 22, 30));
		champitem1.setWins(30);
		champitem1.setLosses(44);
		
		ChampItem champitem2 = new ChampItem();
		champitem2.setCk(new ChampItemCompKey("Å»·Ð", 22, 35));
		champitem2.setWins(40);
		champitem2.setLosses(32);
		
		ChampItem champitem3 = new ChampItem();
		champitem3.setCk(new ChampItemCompKey("Å»·Ð", 22, 44));
		champitem3.setWins(20);
		champitem3.setLosses(17);
		
		championItems.add(champitem1);
		championItems.add(champitem2);
		championItems.add(champitem3);
		
		when(championReository.findChampItems("Å»·Ð")).thenReturn(championItems);
		
		List<ChampEnemy> championEnemys = new ArrayList<>();
		ChampEnemy champEnemy1 = new ChampEnemy();
		champEnemy1.setCk(new ChampEnemyCompKey("Å»·Ð",22, "Ä«Å¸¸®³ª"));
		champEnemy1.setWins(25);
		champEnemy1.setLosses(15);
		
		ChampEnemy champEnemy2 = new ChampEnemy();
		champEnemy2.setCk(new ChampEnemyCompKey("Å»·Ð",22, "Á¦µå"));
		champEnemy2.setWins(30);
		champEnemy2.setLosses(13);
		
		championEnemys.add(champEnemy2);
		championEnemys.add(champEnemy1);
		
		when(championReository.findChampEnemys("Å»·Ð")).thenReturn(championEnemys);
		
		//when
		TotalChampDto totalChampDto = championService.getChampionDetail("Å»·Ð");
		
		//then
		assertThat(totalChampDto.getChampItems().size()).isEqualTo(3);
		
		assertThat(totalChampDto.getChampEnemys().size()).isEqualTo(2);
		assertThat(totalChampDto.getChampEnemys().get(0).getEnemychampionId()).isEqualTo("Á¦µå");
	}
}
