package com.lolsearcher.unit.service.champion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.lolsearcher.model.entity.champion.position.ChampPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lolsearcher.model.dto.championstatic.ChampPositionDto;
import com.lolsearcher.model.dto.championstatic.TotalChampDto;
import com.lolsearcher.model.entity.champion.position.ChampPositionCompKey;
import com.lolsearcher.model.entity.champion.enemy.ChampEnemy;
import com.lolsearcher.model.entity.champion.enemy.ChampEnemyCompKey;
import com.lolsearcher.model.entity.champion.item.ChampItem;
import com.lolsearcher.model.entity.champion.item.ChampItemCompKey;
import com.lolsearcher.repository.champion.JpaChampionRepository;
import com.lolsearcher.service.statistic.ChampionService;

@ExtendWith(MockitoExtension.class)
class ChampPositionServiceUnitTest {

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
		ChampPosition champPosition1 = new ChampPosition();
		champPosition1.setCk(new ChampPositionCompKey("제드",22,"MIDDLE"));
		champPosition1.setWins(50);
		champPosition1.setLosses(45);
		ChampPosition champPosition2 = new ChampPosition();
		champPosition2.setCk(new ChampPositionCompKey("카타리나",22,"MIDDLE"));
		champPosition2.setWins(40);
		champPosition2.setLosses(45);
		
		List<ChampPosition> champPositions = new ArrayList<>();
		champPositions.add(champPosition1);
		champPositions.add(champPosition2);
		
		when(championReository.findChampPositions("MIDDLE")).thenReturn(champPositions);
		
		//when
		List<ChampPositionDto> champPositionDtos = championService.getChampions(position);
		
		//then
		assertThat(champPositionDtos.size()).isEqualTo(champPositions.size());
		assertThat(champPositionDtos.get(0).getChampionId()).isEqualTo(champPositions.get(0).getCk().getChampionId());
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
		
		when(championReository.findChampEnemies("제드")).thenReturn(championEnemys);
		
		//when
		TotalChampDto totalChampDto = championService.getChampionDetail(championId);
		
		//then
		assertThat(totalChampDto.getChampItems().size()).isEqualTo(3);
		assertThat(totalChampDto.getChampItems().get(0).getItemId())
		.isEqualTo(30);
		
		assertThat(totalChampDto.getChampEnemies().size()).isEqualTo(2);
		assertThat(totalChampDto.getChampEnemies().get(0).getEnemyChampionId())
		.isEqualTo("카타리나");
	}
}
