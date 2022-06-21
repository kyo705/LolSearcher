package com.lolsearcher.Service.unit;

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
	void getChampionsCase1() {
		//test Case 1 : 파라미터로 포지션이 전달되었을 때, 해당 포지션에 해당하는 챔피언들을 리턴하는 상황
		
		//given
		//테스트할 메소드 파라미터 값
		String position = "MIDDLE";
		
		//Mock 객체 리턴 값 1
		Champion champion1 = new Champion();
		champion1.setCk(new ChampionCompKey("제드",22,"MIDDLE"));
		champion1.setWins(50);
		champion1.setLosses(45);
		Champion champion2 = new Champion();
		champion2.setCk(new ChampionCompKey("카타리나",22,"MIDDLE"));
		champion2.setWins(40);
		champion2.setLosses(45);
		
		List<Champion> champions = new ArrayList<>();
		champions.add(champion1);
		champions.add(champion2);
		
		when(championReository.findChamps("MIDDLE")).thenReturn(champions);
		
		//when
		List<ChampionDto> championDtos = championService.getChampions(position);
		
		//then
		assertThat(championDtos.size()).isEqualTo(champions.size());
		assertThat(championDtos.get(0).getChampionId()).isEqualTo(champions.get(0).getCk().getChampionId());
	}
	
	@Test
	void getChampionDetailCase1() {
		//test Case 1 : 파라미터로 챔피언 ID가 전달되었을 떄, 
		//				해당 챔피언의 아이템별 승률, 상대하기 쉬운 챔피언을 리턴하는 상황
		
		//given
		//테스트할 메소드 파라미터 값
		String championId = "제드";
		
		//Mock 객체 리턴 값 1
		ChampItem champitem1 = new ChampItem();
		champitem1.setCk(new ChampItemCompKey("제드", 22, 30));
		champitem1.setWins(30);
		champitem1.setLosses(44);
		ChampItem champitem2 = new ChampItem();
		champitem2.setCk(new ChampItemCompKey("제드", 22, 35));
		champitem2.setWins(40);
		champitem2.setLosses(32);
		ChampItem champitem3 = new ChampItem();
		champitem3.setCk(new ChampItemCompKey("제드", 22, 44));
		champitem3.setWins(20);
		champitem3.setLosses(17);
		
		List<ChampItem> championItems = new ArrayList<>();
		championItems.add(champitem1);
		championItems.add(champitem2);
		championItems.add(champitem3);
		
		when(championReository.findChampItems(championId))
		.thenReturn(championItems);
		
		//Mock 객체 리턴 값 2
		ChampEnemy champEnemy1 = new ChampEnemy();
		champEnemy1.setCk(new ChampEnemyCompKey("제드",22, "카타리나"));
		champEnemy1.setWins(25);
		champEnemy1.setLosses(15);
		ChampEnemy champEnemy2 = new ChampEnemy();
		champEnemy2.setCk(new ChampEnemyCompKey("제드",22, "탈론"));
		champEnemy2.setWins(30);
		champEnemy2.setLosses(13);
		
		List<ChampEnemy> championEnemys = new ArrayList<>();
		championEnemys.add(champEnemy1);
		championEnemys.add(champEnemy2);
		
		when(championReository.findChampEnemys("제드")).thenReturn(championEnemys);
		
		//when
		TotalChampDto totalChampDto = championService.getChampionDetail(championId);
		
		//then
		assertThat(totalChampDto.getChampItems().size()).isEqualTo(3);
		assertThat(totalChampDto.getChampItems().get(0).getItemId())
		.isEqualTo(30);
		
		assertThat(totalChampDto.getChampEnemys().size()).isEqualTo(2);
		assertThat(totalChampDto.getChampEnemys().get(0).getEnemychampionId())
		.isEqualTo("카타리나");
	}
}
