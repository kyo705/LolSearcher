package com.lolsearcher.Service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.command.MatchParamDto;
import com.lolsearcher.domain.Dto.command.MostchampParamDto;
import com.lolsearcher.domain.Dto.summoner.MatchDto;
import com.lolsearcher.domain.Dto.summoner.MostChampDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.domain.entity.summoner.Summoner;
import com.lolsearcher.domain.entity.summoner.match.Match;
import com.lolsearcher.domain.entity.summoner.match.Member;
import com.lolsearcher.domain.entity.summoner.rank.Rank;
import com.lolsearcher.domain.entity.summoner.rank.RankCompKey;
import com.lolsearcher.repository.JpaTestRepository;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.service.SummonerService;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SummonerServiceIntegrationTest {
	private static final String soloRank = "RANKED_SOLO_5x5";
	private static final String flexRank = "RANKED_FLEX_SR";
	private static final int currentSeasonId = 22;
	
	@Autowired
	SummonerService summonerService;
	@Autowired
	JpaTestRepository testRepository;
	
	@Autowired
	SummonerRepository summonerRepository;
	@Autowired
	RiotRestAPI riotRestApi;
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	EntityManager em;

	//----------------------findDbSummoner() 메소드 Test Case------------------------------------
	
	@Test
	void findDbSummonerCase1() {
		//testCase1 : DB에 특정 닉네임이 1개 존재할 때
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "푸켓푸켓";
		//DB 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id");
		summoner.setName("푸켓푸켓");
		summonerRepository.saveSummoner(summoner);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerName);
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner.getId());
	}
	
	@Test
	void findDbSummonerCase2() {
		//testCase2 : DB에 특정 닉네임이 존재하지 않을 때
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("id");
		summoner.setName("갓버수문장");
		summonerRepository.saveSummoner(summoner);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		assertThat(summonerDto).isEqualTo(null);
	}
	
	@Test
	void findDbSummonerCase3() {
		//testCase3 : DB에 특정 닉네임이 2개 이상 존재할 때 해당 실제 닉네임을 가진 유저가 DB에 존재할 때
		//닉네임은 게임 내에서 변경이 가능하므로 
		//DB 데이터 갱신이 안되면 닉네임 중복상황이 발생할 수 있음
		
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw"); //진짜 "푸켓푸켓" 닉네임을 가진 ID
		dbSummoner1.setName("푸켓푸켓");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY"); //실제 "갓버수문장" 닉네임을 가진 ID
		dbSummoner2.setName("푸켓푸켓");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		Summoner renewedSummoner1 = summonerRepository.findSummonerById(dbSummoner1.getId());
		Summoner renewedSummoner2 = summonerRepository.findSummonerById(dbSummoner2.getId());
		assertThat(renewedSummoner1.getName()).isNotEqualTo(renewedSummoner2.getName());
		assertThat(renewedSummoner1.getId()).isEqualTo(dbSummoner1.getId());
		assertThat(renewedSummoner1.getName()).isEqualTo("푸켓푸켓");
		assertThat(renewedSummoner2.getId()).isEqualTo(dbSummoner2.getId());
		assertThat(renewedSummoner2.getName()).isNotEqualTo("푸켓푸켓");
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto.getName()).isEqualTo("푸켓푸켓");
		assertThat(summonerDto.getSummonerid()).isEqualTo(renewedSummoner1.getId());
		assertThat(summonerDto.getName()).isEqualTo(renewedSummoner1.getName());
	}
	
	@Test
	void findDbSummonerCase4() {
		//testCase4 : DB에 특정 닉네임이 2개 이상 존재할 때 
		//해당 실제 닉네임을 가진 유저가 DB에 존재하지 않을 때
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "페이커";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw"); //실제 "푸켓푸켓" 닉네임을 가진 ID
		dbSummoner1.setName("페이커");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY"); //실제 "갓버수문장" 닉네임을 가진 ID
		dbSummoner2.setName("페이커");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		Summoner renewedSummoner1 = summonerRepository.findSummonerById(dbSummoner1.getId());
		Summoner renewedSummoner2 = summonerRepository.findSummonerById(dbSummoner2.getId());
		assertThat(renewedSummoner1.getName()).isNotEqualTo(renewedSummoner2.getName());
		assertThat(renewedSummoner1.getId()).isEqualTo(dbSummoner1.getId());
		assertThat(renewedSummoner1.getName()).isNotEqualTo("페이커");
		assertThat(renewedSummoner2.getId()).isEqualTo(dbSummoner2.getId());
		assertThat(renewedSummoner2.getName()).isNotEqualTo("페이커");
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto).isEqualTo(null);
	}
	
	@Test
	void findDbSummonerCase5() {
		//testCase5 : DB에 특정 닉네임이 2개 이상 존재할 때 
		//해당 리스트 중 유저 중 삭제된 인원이 있고, 해당 닉네임을 가진 유저 존재할 때
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw"); //실제 "푸켓푸켓" 닉네임을 가지는 ID
		dbSummoner1.setName("푸켓푸켓");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("notExistId1"); //존재하지 않는 ID
		dbSummoner2.setName("푸켓푸켓");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		Summoner renewedSummoner1 = summonerRepository.findSummonerById(dbSummoner1.getId());
		assertThat(renewedSummoner1.getId()).isEqualTo(dbSummoner1.getId());
		assertThat(renewedSummoner1.getName()).isEqualTo("푸켓푸켓");
		
		assertThrows(EmptyResultDataAccessException.class, ()->summonerRepository.findSummonerById(dbSummoner2.getId()));
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto.getSummonerid()).isEqualTo(renewedSummoner1.getId());
		assertThat(summonerDto.getName()).isEqualTo(renewedSummoner1.getName());
	}
	
	@Test
	void findDbSummonerCase6() {
		//testCase6 : DB에 닉네임이 2개 이상 존재할 때 
		//해당 리스트 중 유저 중 삭제된 인원이 있고, 해당 닉네임을 가진 유저 존재하지 않을 때
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY"); //실제 "갓버수문장" 닉네임을 가지는 ID
		dbSummoner1.setName("푸켓푸켓");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("notExistId1"); //존재하지 않는 ID
		dbSummoner2.setName("푸켓푸켓");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		Summoner renewedSummoner1 = summonerRepository.findSummonerById(dbSummoner1.getId());
		assertThat(renewedSummoner1.getId()).isEqualTo(dbSummoner1.getId());
		assertThat(renewedSummoner1.getName()).isNotEqualTo("푸켓푸켓");
		
		assertThrows(EmptyResultDataAccessException.class, ()->summonerRepository.findSummonerById(dbSummoner2.getId()));
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto).isEqualTo(null);
	}
	
	@DisplayName("해당 테스트는 2분에 한번만 실행 가능")
	@Test
	void findDbSummonerCase7() {
		//testCase7 : DB에 닉네임이 2개 이상 존재할 때 
		//해당 리스트 중 유저 정보를 갱신하고 있는 중 rest api 요청 제한 횟수를 초과한 경우
		//예외 발생 전, 수행 된 DB쿼리를 롤백할지 커밋할지 결정해야함(@tranactional(noRollbackFor=?)을 이용해)
		//해당 테스트는 2분에 한번 적용해야함
		
		//given
		//실행될 메소드의 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY"); //실제 "갓버수문장" 닉네임을 가지는 ID
		dbSummoner1.setName("푸켓푸켓");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("notExistId1"); //존재하지 않는 ID
		dbSummoner2.setName("푸켓푸켓");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//REST API 조건 셋팅 (2분동안 최대 요청횟수 100회기 때문에 99회 요청을 해놓은 상태)
		for(int i=0;i<99;i++) {
			riotRestApi.getSummonerById("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY");
		}
		
		//when,then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.findDbSummoner(summonerName));
		em.flush();
		em.clear();
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(e.getStatusCode().value()).isEqualTo(429);
		
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		Summoner renewedSummoner1 = summonerRepository.findSummonerById(dbSummoner1.getId());
		Summoner renewedSummoner2 = summonerRepository.findSummonerById(dbSummoner2.getId());
		assertThat(renewedSummoner1.getId()).isEqualTo(dbSummoner1.getId());
		assertThat(renewedSummoner1.getName()).isNotEqualTo("푸켓푸켓");
		assertThat(renewedSummoner2.getId()).isEqualTo(dbSummoner2.getId());
		assertThat(renewedSummoner2.getName()).isEqualTo("푸켓푸켓");
		
	}
	
	//----------------------updateDbSummoner() 메소드 Test Case------------------------------------
	
	@Test
	public void updateDbSummonerCase1() {
		//test Case 1 : 특정 닉네임을 가지는 Summoner Entity가 DB에 존재하지 않을 경우
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//when
		summonerService.updateDbSummoner(summonerName);
		
		//then
		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(summonerName);
		assertThat(dbSummoners.size()).isEqualTo(0);
	}
	
	@Test
	public void updateDbSummonerCase2() {
		//test Case 2 : 특정 닉네임을 가지는 Summoner Entity가 DB에 1개 이상 존재할 때,
		//				해당 Entity들이 게임 내 모두 존재하는 유저일 경우 => REST API로 올바르게 업데이트 되었는지 확인
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "페이커";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY"); //실제 "갓버수문장" 닉네임을 가지는 객체
		dbSummoner1.setName("페이커");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw"); //실제 "푸켓푸켓" 닉네임을 가지는 객체
		dbSummoner2.setName("페이커");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//when
		summonerService.updateDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(summonerName);
		assertThat(dbSummoners.size()).isEqualTo(0);
		
		Summoner renewedSummoner1 = summonerRepository.findSummonerById(dbSummoner1.getId());
		Summoner renewedSummoner2 = summonerRepository.findSummonerById(dbSummoner2.getId());
		assertThat(renewedSummoner1.getName()).isEqualTo("갓버수문장");
		assertThat(renewedSummoner2.getName()).isEqualTo("푸켓푸켓");
	}
	
	@Test
	public void updateDbSummonerCase3() {
		//test Case 3 : 특정 닉네임을 가지는 Summoner Entity가 DB에 1개 이상 존재할 때,
		//				해당 Entity들 중 게임 내 존재하지 않는 유저가 있을 경우 => REST API로 올바르게 업데이트 되었는지 확인
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "페이커";
		
		//DB 데이터 셋팅
		Summoner dbSummoner1 = new Summoner();
		dbSummoner1.setId("NotExistId1"); //실제 존재하지 않는 객체
		dbSummoner1.setName("페이커");
		Summoner dbSummoner2 = new Summoner();
		dbSummoner2.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw"); //실제 "푸켓푸켓" 닉네임을 가지는 객체
		dbSummoner2.setName("페이커");
		summonerRepository.saveSummoner(dbSummoner1);
		summonerRepository.saveSummoner(dbSummoner2);
		em.flush();
		em.clear();
		
		//when
		summonerService.updateDbSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		List<Summoner> dbSummoners = summonerRepository.findSummonerByName(summonerName);
		assertThat(dbSummoners.size()).isEqualTo(0);
		
		assertThrows(EmptyResultDataAccessException.class, ()->summonerRepository.findSummonerById(dbSummoner1.getId()));
		Summoner renewedSummoner2 = summonerRepository.findSummonerById(dbSummoner2.getId());
		assertThat(renewedSummoner2.getName()).isEqualTo("푸켓푸켓");
	}
	
	//----------------------setSummoner() 메소드 Test Case------------------------------------
	
	@Test
	public void setSummonerCase1() {
		//test Case 1 : 게임 플랫폼에 특정 닉네임을 가진 유저가 존재하고, DB에 해당 닉네임을 가진 데이터가 없을 경우
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//when
		SummonerDto summonerDto = summonerService.setSummoner(summonerName);
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		List<Summoner> renewedSummoners = summonerRepository.findSummonerByName(summonerName);
		assertThat(renewedSummoners.size()).isEqualTo(1);
		Summoner renewedSummoner = renewedSummoners.get(0);
		assertThat(renewedSummoner.getName()).isEqualTo("푸켓푸켓");
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto.getName()).isEqualTo("푸켓푸켓");
		assertThat(summonerDto.getSummonerid()).isEqualTo(renewedSummoner.getId());
	}
	
	@Test
	public void setSummonerCase2() {
		//test Case 2 : DB에 특정 닉네임을 가진 유저가 한명 존재하고, 게임 내 해당 닉네임을 가진 유저가 존재할 때 
		//				해당 두 개의 primary key값(ID)이 서로 다를 경우
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner dbSummoner = new Summoner();
		dbSummoner.setId("OOyICTBD_8x43cmqxZxaHHBHB1AoPxiFh5eRyyWGGPwabuY"); //실제 "갓버수문장" 닉네임을 가지는 ID
		dbSummoner.setName("푸켓푸켓");
		summonerRepository.saveSummoner(dbSummoner);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.setSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		List<Summoner> renewedSummoners = summonerRepository.findSummonerByName(summonerName);
		assertThat(renewedSummoners.size()).isEqualTo(2);
		Summoner renewedSummoner1 = renewedSummoners.get(0); //DB에 새로 들어온 Summoner 객체
		assertThat(renewedSummoner1.getName()).isEqualTo("푸켓푸켓");
		Summoner renewedSummoner2 = renewedSummoners.get(1); //기존의 DB에 존재한 Summoner 객체
		assertThat(renewedSummoner2.getName()).isEqualTo("푸켓푸켓"); 
		assertThat(renewedSummoner2.getId()).isEqualTo(dbSummoner.getId());
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto.getName()).isEqualTo("푸켓푸켓");
		assertThat(summonerDto.getSummonerid()).isEqualTo(renewedSummoner1.getId());
		assertThat(summonerDto.getLastRenewTimeStamp()).isEqualTo(renewedSummoner1.getLastRenewTimeStamp());
		assertThat(summonerDto.getProfileIconId()).isEqualTo(renewedSummoner1.getProfileIconId());
	}
	
	@Test
	public void setSummonerCase3() {
		//test Case 3 : DB에 특정 닉네임을 가진 유저가 한명 존재하고, 게임 내 해당 닉네임을 가진 유저가 존재할 때 
		//				해당 두 개의 primary key값(ID)이 서로 같은 경우
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "푸켓푸켓";
		
		//DB 데이터 셋팅
		Summoner dbSummoner = new Summoner();
		dbSummoner.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw"); //실제 "푸켓푸켓"의 ID값
		dbSummoner.setName("푸켓푸켓");
		dbSummoner.setProfileIconId(10); //잘못된 값 => 갱신된다면 해당 값은 올바른 값으로 바뀌어야함
		summonerRepository.saveSummoner(dbSummoner);
		em.flush();
		em.clear();
		
		//when
		SummonerDto summonerDto = summonerService.setSummoner(summonerName);
		em.flush();
		em.clear();
		
		//then
		//기존 DB데이터가 올바른 값으로 갱신되었는지 확인
		List<Summoner> renewedSummoners = summonerRepository.findSummonerByName(summonerName);
		assertThat(renewedSummoners.size()).isEqualTo(1);
		Summoner renewedSummoner1 = renewedSummoners.get(0);
		assertThat(renewedSummoner1.getName()).isEqualTo("푸켓푸켓");
		assertThat(renewedSummoner1.getId()).isEqualTo(dbSummoner.getId());
		assertThat(renewedSummoner1.getProfileIconId()).isNotEqualTo(dbSummoner.getProfileIconId());
		
		//실행된 비지니스 로직으로부터 리턴받은 값(summonerDto)이 올바른 값인지 확인
		assertThat(summonerDto.getName()).isEqualTo("푸켓푸켓");
		assertThat(summonerDto.getSummonerid()).isEqualTo(renewedSummoner1.getId());
		assertThat(summonerDto.getLastRenewTimeStamp()).isEqualTo(renewedSummoner1.getLastRenewTimeStamp());
		assertThat(summonerDto.getProfileIconId()).isEqualTo(renewedSummoner1.getProfileIconId());
	}
	
	@DisplayName("전달될 파라미터 닉네임이 실제로 존재하면 안됌")
	@Test
	public void setSummonerCase4() {
		//test Case 4 : REST 통신이 실패한 경우(EX. 해당 닉네임을 가진 유저가 존재하지 않을 경우)	
		
		//given
		//실행될 메소드 파라미터 값
		String summonerName = "kyo705닉네임있을까";
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setSummoner(summonerName));
		
		assertThat(e.getStatusCode().value()).isEqualTo(404);
	}
	
	
	//----------------------setLeague() 메소드 Test Case------------------------------------
	
	
	@Test
	public void setLeagueCase1() {
		//test Case 1 : REST 통신을 통해 유저의 랭크 관련 데이터를 가져오는데 성공하고, 
		//				DB에 기존 데이터가 없는 상황1
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//when
		TotalRanksDto totalRanksDto = summonerService.setLeague(summonerDto);
		em.flush();
		em.clear();
		
		//then
		List<Rank> allRank = testRepository.findAllRank();
		
		for(Rank rank : allRank) {
			//DB에 원하는 값만 저장되었는지
			assertThat(rank.getCk().getSeasonId()).isEqualTo(currentSeasonId);
			assertThat(rank.getCk().getSummonerId()).isEqualTo(summonerDto.getSummonerid());
			//테스트한 메소드의 리턴값이 DB값과 일치하는지
			if(rank.getCk().getQueueType().equals(soloRank)) {
				assertThat(totalRanksDto.getSolorank().getTier()).isEqualTo(rank.getTier());
				assertThat(totalRanksDto.getSolorank().getLeagueId()).isEqualTo(rank.getLeagueId());
				assertThat(totalRanksDto.getSolorank().getLeaguePoints()).isEqualTo(rank.getLeaguePoints());
			}else if(rank.getCk().getQueueType().equals(flexRank)) {
				assertThat(totalRanksDto.getTeamrank().getTier()).isEqualTo(rank.getTier());
				assertThat(totalRanksDto.getTeamrank().getLeagueId()).isEqualTo(rank.getLeagueId());
				assertThat(totalRanksDto.getTeamrank().getLeaguePoints()).isEqualTo(rank.getLeaguePoints());
			}
		}
	}
	
	@Test
	public void setLeagueCase2() {
		//test Case 2 : REST 통신을 통해 유저의 랭크 관련 데이터를 가져오는데 성공하고, 
		//				DB에 기존 데이터가 없는 상황2(시즌이 다른 데이터들은 있는 상황)
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Rank dbSeason21SoloRank = new Rank();
		dbSeason21SoloRank.setCk(new RankCompKey(summonerDto.getSummonerid(), soloRank, 21));
		dbSeason21SoloRank.setWins(30);
		dbSeason21SoloRank.setLosses(22);
		dbSeason21SoloRank.setTier("DIAMOND");
		List<Rank> dbSeason21Ranks = new ArrayList<>();
		dbSeason21Ranks.add(dbSeason21SoloRank);
		summonerRepository.saveRanks(dbSeason21Ranks);
		em.flush();
		em.clear();
		
		//when
		TotalRanksDto totalRanksDto = summonerService.setLeague(summonerDto);
		em.flush();
		em.clear();
		
		//then
		List<Rank> allRank = testRepository.findAllRank();
		
		for(Rank rank : allRank) {
			//기존 DB데이터
			if(rank.getCk().getSeasonId()==21) {
				assertThat(rank.getCk().getQueueType()).isEqualTo(soloRank);
				assertThat(rank.getCk().getSummonerId()).isEqualTo(summonerDto.getSummonerid());
				assertThat(rank.getTier()).isEqualTo("DIAMOND");
			}else{
				//DB에 원하는 값만 저장되었는지
				assertThat(rank.getCk().getSeasonId()).isEqualTo(currentSeasonId);
				assertThat(rank.getCk().getSummonerId()).isEqualTo(summonerDto.getSummonerid());
				//테스트한 메소드의 리턴값이 DB값과 일치하는지
				if(rank.getCk().getQueueType().equals(soloRank)) {
					assertThat(totalRanksDto.getSolorank().getTier()).isEqualTo(rank.getTier());
					assertThat(totalRanksDto.getSolorank().getLeagueId()).isEqualTo(rank.getLeagueId());
					assertThat(totalRanksDto.getSolorank().getLeaguePoints()).isEqualTo(rank.getLeaguePoints());
				}else if(rank.getCk().getQueueType().equals(flexRank)) {
					assertThat(totalRanksDto.getTeamrank().getTier()).isEqualTo(rank.getTier());
					assertThat(totalRanksDto.getTeamrank().getLeagueId()).isEqualTo(rank.getLeagueId());
					assertThat(totalRanksDto.getTeamrank().getLeaguePoints()).isEqualTo(rank.getLeaguePoints());
				}
			}
		}
	}
	
	@Test
	public void setLeagueCase3() {
		//test Case 3 : REST 통신을 통해 유저의 랭크 관련 데이터를 가져오는데 성공하고, DB에 기존 데이터가 존재하는 상황
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Rank dbOldSoloRank = new Rank();
		dbOldSoloRank.setCk(new RankCompKey(
				"vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw",
				soloRank,
				currentSeasonId));
		List<Rank> oldRanks = new ArrayList<>();
		oldRanks.add(dbOldSoloRank);
		summonerRepository.saveRanks(oldRanks);
		em.flush();
		em.clear();
		
		//when
		TotalRanksDto totalRanksDto = summonerService.setLeague(summonerDto);
		em.flush();
		em.clear();
		
		//then
		List<Rank> allRank = testRepository.findAllRank();
		for(Rank rank : allRank) {
			//기존 DB 데이터 존재하는지
			if(rank.getCk().getQueueType().equals(dbOldSoloRank.getCk().getQueueType())) {
				assertThat(rank.getCk().getSeasonId()).isEqualTo(dbOldSoloRank.getCk().getSeasonId());
				assertThat(rank.getCk().getSummonerId()).isEqualTo(dbOldSoloRank.getCk().getSummonerId());
			}
			
			//DB에 원하는 값만 저장되었는지
			assertThat(rank.getCk().getSeasonId()).isEqualTo(currentSeasonId);
			assertThat(rank.getCk().getSummonerId()).isEqualTo(summonerDto.getSummonerid());
			
			//테스트한 메소드의 리턴값이 DB값과 일치하는지
			if(rank.getCk().getQueueType().equals(soloRank)) {
				assertThat(totalRanksDto.getSolorank().getTier()).isEqualTo(rank.getTier());
				assertThat(totalRanksDto.getSolorank().getLeagueId()).isEqualTo(rank.getLeagueId());
				assertThat(totalRanksDto.getSolorank().getLeaguePoints()).isEqualTo(rank.getLeaguePoints());
			}else if(rank.getCk().getQueueType().equals(flexRank)) {
				assertThat(totalRanksDto.getTeamrank().getTier()).isEqualTo(rank.getTier());
				assertThat(totalRanksDto.getTeamrank().getLeagueId()).isEqualTo(rank.getLeagueId());
				assertThat(totalRanksDto.getTeamrank().getLeaguePoints()).isEqualTo(rank.getLeaguePoints());
			}
		}
	}
	
	@Test
	public void setLeagueCase4() {
		//test Case 4 : REST 통신이 실패한 경우(EX. 올바르지 못한 파라미터가 전달되었을 때 => 400 error)
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("notExistId1");
		summonerDto.setName("푸켓푸켓");
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setLeague(summonerDto));
		assertThat(e.getStatusCode().value()).isEqualTo(400);
		
		//DB에 올바른 데이터가 적용됐는지 확인
		List<Rank> allRank = testRepository.findAllRank();
		assertThat(allRank.size()).isEqualTo(0);
	}
	
	
	
	//----------------------getLeague() 메소드 Test Case------------------------------------
	
	@Test
	public void getLeagueCase1() {
		//test Case 1 : DB에서 유저 Rank 관련 데이터 가져올 때 soloRank 정보만 있을 경우
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Rank dbSoloRank = new Rank();
		dbSoloRank.setCk(new RankCompKey("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw", soloRank, currentSeasonId));
		dbSoloRank.setTier("GOLD");
		dbSoloRank.setRank("III");
		dbSoloRank.setWins(50);
		dbSoloRank.setLosses(30);
		List<Rank> dbRanks = new ArrayList<>();
		dbRanks.add(dbSoloRank);
		summonerRepository.saveRanks(dbRanks);
		em.flush();
		em.clear();
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summonerDto);
		em.flush();
		em.clear();
		
		//then
		assertThat(totalRankDto.getTeamrank()).isEqualTo(null);
		assertThat(totalRankDto.getSolorank().getSummonerId())
		.isEqualTo(dbSoloRank.getCk().getSummonerId());
		assertThat(totalRankDto.getSolorank().getWins()).isEqualTo(dbSoloRank.getWins());
		assertThat(totalRankDto.getSolorank().getLosses()).isEqualTo(dbSoloRank.getLosses());
	}
	
	@Test
	public void getLeagueCase2() {
		//test Case 2 : DB에서 유저 Rank 관련 데이터 가져올 때 teamRank 정보만 있을 경우
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Rank dbTeamRank = new Rank();
		dbTeamRank.setCk(new RankCompKey("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw", flexRank, currentSeasonId));
		dbTeamRank.setTier("GOLD");
		dbTeamRank.setRank("III");
		dbTeamRank.setWins(50);
		dbTeamRank.setLosses(30);
		List<Rank> dbRanks = new ArrayList<>();
		dbRanks.add(dbTeamRank);
		summonerRepository.saveRanks(dbRanks);
		em.flush();
		em.clear();
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summonerDto);
		
		//then
		assertThat(totalRankDto.getSolorank()).isEqualTo(null);
		assertThat(totalRankDto.getTeamrank().getSummonerId())
		.isEqualTo(dbTeamRank.getCk().getSummonerId());
		assertThat(totalRankDto.getTeamrank().getWins()).isEqualTo(dbTeamRank.getWins());
		assertThat(totalRankDto.getTeamrank().getLosses()).isEqualTo(dbTeamRank.getLosses());
	}
	
	@Test
	public void getLeagueCase3() {
		//test Case 3 : DB에서 유저 Rank 관련 데이터 가져올 때 
		//teamRank, soloRank 정보 둘다 있을 경우
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Rank dbTeamRank = new Rank();
		dbTeamRank.setCk(new RankCompKey("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw", flexRank, currentSeasonId));
		dbTeamRank.setTier("GOLD");
		dbTeamRank.setRank("III");
		dbTeamRank.setWins(50);
		dbTeamRank.setLosses(30);
		Rank dbSoloRank = new Rank();
		dbSoloRank.setCk(new RankCompKey("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw", soloRank, currentSeasonId));
		dbSoloRank.setTier("GOLD");
		dbSoloRank.setRank("II");
		dbSoloRank.setWins(60);
		dbSoloRank.setLosses(50);
		List<Rank> dbRanks = new ArrayList<>();
		dbRanks.add(dbSoloRank);
		dbRanks.add(dbTeamRank);
		summonerRepository.saveRanks(dbRanks);
		em.flush();
		em.clear();
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summonerDto);
		
		//then
		assertThat(totalRankDto.getSolorank().getSummonerId())
		.isEqualTo(dbSoloRank.getCk().getSummonerId());
		assertThat(totalRankDto.getSolorank().getWins()).isEqualTo(dbSoloRank.getWins());
		assertThat(totalRankDto.getSolorank().getLosses()).isEqualTo(dbSoloRank.getLosses());
		
		assertThat(totalRankDto.getTeamrank().getSummonerId())
		.isEqualTo(dbTeamRank.getCk().getSummonerId());
		assertThat(totalRankDto.getTeamrank().getWins()).isEqualTo(dbTeamRank.getWins());
		assertThat(totalRankDto.getTeamrank().getLosses()).isEqualTo(dbTeamRank.getLosses());
	}
	
	
	@Test
	public void getLeagueCase4() {
		//test Case 2 : DB에서 유저 Rank 관련 데이터 가져올 때 데이터가 없는 경우
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summonerDto);
		
		//then
		assertThat(totalRankDto.getSolorank()).isEqualTo(null);
		assertThat(totalRankDto.getTeamrank()).isEqualTo(null);
	}
	
	
	//----------------------setMatches() 메소드 Test Case------------------------------------
	
	@Test
	void setMatchesCase1() {
		//test Case 1 : REST API 통신으로 matchIdList를 가져오는데 성공하고				
		//				가져온 모든 matchId에 해당하는 match 데이터들이 DB에 없는 경우
		
		//given
		int allType = -1;
		String allChamp = "all";
		int startIndex = 0;
		int defaultCount = 20;
		
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setPuuid("ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		List<String> matchList = riotRestApi.getMatchIds(summonerDto.getPuuid(), allType, allChamp, startIndex, defaultCount, "");
		
		Summoner summoner = new Summoner();
		summoner.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summoner.setPuuid("ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid(matchList.get(5));
		summonerRepository.saveSummoner(summoner);
		em.flush();
		em.clear();
		
		//when
		List<MatchDto> recent_match_dtos = summonerService.setMatches(summonerDto);
		
		//then
		assertThat(recent_match_dtos.size()).isEqualTo(5);
		
		Summoner renewedDbSummoner = summonerRepository.findSummonerById("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		assertThat(renewedDbSummoner.getLastmatchid()).isEqualTo(matchList.get(0));
		
		List<Match> dbMatches = summonerRepository.findMatchList(summoner.getId(), allType, allChamp, defaultCount);
		for(Match dbMatch : dbMatches) {
			List<Member> dbMembers =dbMatch.getMembers();
			boolean plag = false;
			for(Member dbMember : dbMembers) {
				if(dbMember.getName().equals("푸켓푸켓")) {
					plag = true;
					break;
				}
			}
			if(plag==false) {
				fail();
			}
		}
	}
	
	@Test
	void setMatchesCase2() {
		//test Case 2 : REST API 통신으로 matchIdList를 가져오는데 성공하고				
		//				가져온 matchIdList에 일부 match정보가 DB에 저장되어있는 경우
		
		//given
		int allType = -1;
		String allChamp = "all";
		int startIndex = 0;
		int defaultCount = 20;
		
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		summonerDto.setPuuid("ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA");
		
		//DB 데이터 셋팅
		List<String> matchList = riotRestApi.getMatchIds(summonerDto.getPuuid(), allType, allChamp, startIndex, defaultCount, "");
		String matchId1 = matchList.get(1);
		Match match1 = riotRestApi.getOneMatch(matchId1);
		summonerRepository.saveMatch(match1);
		
		Summoner summoner = new Summoner();
		summoner.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summoner.setPuuid("ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid(matchList.get(5));
		summonerRepository.saveSummoner(summoner);
		
		em.flush();
		em.clear();
		
		//when
		List<MatchDto> rencent_match_dtos = summonerService.setMatches(summonerDto);
		
		//then
		assertThat(rencent_match_dtos.size()).isEqualTo(4);
		
		Summoner renewedDbSummoner = summonerRepository.findSummonerById(summonerDto.getSummonerid());
		assertThat(renewedDbSummoner.getLastmatchid()).isEqualTo(matchList.get(0));
		
		
		List<Match> dbMatches = summonerRepository.findMatchList(summoner.getId(), allType, allChamp, defaultCount);
		for(Match dbMatch : dbMatches) {
			List<Member> dbMembers =dbMatch.getMembers();
			boolean plag = false;
			for(Member dbMember : dbMembers) {
				if(dbMember.getName().equals("푸켓푸켓")) {
					plag = true;
					break;
				}
			}
			if(plag==false) {
				fail();
			}
		}
	}
	
	@Test
	void setMatchesCase3() {
		//test Case 3 : REST API 통신으로 matchIdList를 가져오는데 성공하고				
		//				가져온 matchIdList의 size()가 0인 경우
		
		//given
		int allType = -1;
		String allChamp = "all";
		int startIndex = 0;
		int defaultCount = 20;
		
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		summonerDto.setPuuid("ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA");
		
		//DB 데이터 셋팅
		List<String> matchList = riotRestApi.getMatchIds(summonerDto.getPuuid(), allType, allChamp, startIndex, defaultCount, "");
		Summoner summoner = new Summoner();
		summoner.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summoner.setPuuid("ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA");
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid(matchList.get(0));
		summonerRepository.saveSummoner(summoner);
		em.flush();
		em.clear();
		
		//when
		List<MatchDto> recent_match_dtos = summonerService.setMatches(summonerDto);
		
		//then
		assertThat(recent_match_dtos.size()).isEqualTo(0);
		
		Summoner renewedDbSummoner = summonerRepository.findSummonerById(summonerDto.getSummonerid());
		assertThat(renewedDbSummoner.getLastmatchid()).isEqualTo(summoner.getLastmatchid());
		
		List<Match> dbMatches = summonerRepository.findMatchList(summoner.getId(), allType, allChamp, defaultCount);
		assertThat(dbMatches.size()).isEqualTo(0);
	}
	
	@Test
	void setMatchesCase4() {
		//test Case 4 : REST API 통신으로 matchIdList를 가져오는데 실패한 경우(EX. 올바르지 못한 puuid값이 주어진 경우)
		
		//given
		//실행될 메소드 파라미터 값
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summonerDto.setName("푸켓푸켓");
		
		//DB 데이터 셋팅
		Summoner summoner = new Summoner();
		summoner.setId("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		summoner.setPuuid("NotExistPuuid1"); //실제 존재하지 않는 값
		summoner.setName("푸켓푸켓");
		summoner.setLastmatchid("");
		summonerRepository.saveSummoner(summoner);
		em.flush();
		em.clear();
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setMatches(summonerDto));
		assertThat(e.getStatusCode().value()).isEqualTo(400);
		
		int allType = -1;
		String allChamp = "all";
		int defaultCount = 20;
		
		List<Match> dbMatches = summonerRepository.findMatchList(summonerDto.getSummonerid(), allType, allChamp, defaultCount);
		assertThat(dbMatches.size()).isEqualTo(0);
	}
	
	
	
	//----------------------getMatches() 메소드 Test Case------------------------------------
	
	@Test
	void getMatchesCase1() {
		//test Case 1 : parameter로 전달 받은 조건들을 이용해 DB에서 적절한 matchList를 반환하는 상황
		
		//given
		int allType = -1;
		String allChamp = "all";
		int startIndex = 0;
		int defaultCount = 20;
		String puuId = "ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA";
		
		//실행될 메소드 파라미터 값
		MatchParamDto matchparam = new MatchParamDto();
		matchparam.setName("푸켓푸켓");
		matchparam.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		
		//DB 데이터 셋팅
		List<String> matchList = riotRestApi.getMatchIds(puuId, allType, allChamp, startIndex, defaultCount, "");
		Match match1 = riotRestApi.getOneMatch(matchList.get(0));
		Match match2 = riotRestApi.getOneMatch(matchList.get(1));
		Match match3 = riotRestApi.getOneMatch(matchList.get(2));
		summonerRepository.saveMatch(match1);
		summonerRepository.saveMatch(match2);
		summonerRepository.saveMatch(match3);
		em.flush();
		em.clear();
		
		//when
		List<MatchDto> matchListDto = summonerService.getMatches(matchparam);
		em.flush();
		em.clear();
		
		//then
		assertThat(matchListDto.size()).isEqualTo(3);
		assertThat(matchListDto.get(0).getMatchid()).isEqualTo(match1.getMatchId());
		assertThat(matchListDto.get(1).getMatchid()).isEqualTo(match2.getMatchId());
		assertThat(matchListDto.get(2).getMatchid()).isEqualTo(match3.getMatchId());
	}
	
	@Test
	void getMatchesCase2() {
		//test Case 2 : parameter로 전달 받은 조건에 만족하는 matchList가 없는 경우
		
		//given
		int soloRankType = 420;
		String allChamp = "all";
		int startIndex = 0;
		int defaultCount = 20;
		String puuId = "ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA";
		
		//실행될 메소드 파라미터 값
		MatchParamDto matchparam1 = new MatchParamDto();
		matchparam1.setName("푸켓푸켓");
		matchparam1.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		matchparam1.setGametype(440);
		
		MatchParamDto matchparam2 = new MatchParamDto();
		matchparam2.setName("푸켓푸켓");
		matchparam2.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		
		//DB 데이터 셋팅 =>솔로랭크 게임 데이터 가져와 DB에 저장
		List<String> matchList = riotRestApi.getMatchIds(puuId, soloRankType, allChamp, startIndex, defaultCount, ""); 
		Match match1 = riotRestApi.getOneMatch(matchList.get(0));
		Match match2 = riotRestApi.getOneMatch(matchList.get(1));
		Match match3 = riotRestApi.getOneMatch(matchList.get(2));
		summonerRepository.saveMatch(match1);
		summonerRepository.saveMatch(match2);
		summonerRepository.saveMatch(match3);
		em.flush();
		em.clear();
		
		//when
		List<MatchDto> matchListDto1 = summonerService.getMatches(matchparam1); // 팀랭크 게임 조회 => 0
		List<MatchDto> matchListDto2 = summonerService.getMatches(matchparam2); // 모든 게임 조회 => 3
		em.flush();
		em.clear();
		
		//then
		assertThat(matchListDto1.size()).isEqualTo(0);
		assertThat(matchListDto2.size()).isEqualTo(3);
	}
	
	//----------------------getMostchamp() 메소드 Test Case------------------------------------
	
	@Test
	void getMostChampCase1() {
		//test Case 1 : parameter로 전달받은 조건에 만족하는 모스트 챔피언 통계 데이터를 가져오는 경우
		//test Case 2 : parameter로 전달받은 조건에 만족하는 모스트 챔피언 통계 데이터가 존재하지 않는 경우
		
		//given
		int allType = -1;
		String allChamp = "all";
		int startIndex = 0;
		int defaultCount = 20;
		String puuId = "ROxYq8Jn3uGRmcgPCx0SoSsgqadU9xSpYs82XufITTL7y4ozdlvVbA2vwc6SXnaRGEnGYhL8BLQRrA";
		
		//실행될 메소드 파라미터 값
		MostchampParamDto mostChampParam1 = new MostchampParamDto();
		mostChampParam1.setGamequeue(-1);
		mostChampParam1.setSeason(12);
		mostChampParam1.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		
		MostchampParamDto mostChampParam2 = new MostchampParamDto();
		mostChampParam2.setGamequeue(-1);
		mostChampParam2.setSeason(11);
		mostChampParam2.setSummonerid("vI8IEER7jJGbdMOw6_1ciINz60FHxhL2jIMJY1SyCO_Bucw");
		
		//DB 데이터 셋팅
		List<String> matchList = riotRestApi.getMatchIds(puuId, allType, allChamp, startIndex, defaultCount, "");
		Match match1 = riotRestApi.getOneMatch(matchList.get(0))
				
				;
		Match match2 = riotRestApi.getOneMatch(matchList.get(1));
		Match match3 = riotRestApi.getOneMatch(matchList.get(2));
		summonerRepository.saveMatch(match1);
		summonerRepository.saveMatch(match2);
		summonerRepository.saveMatch(match3);
		em.flush();
		em.clear();
		
		//when
		List<MostChampDto> mostChamps1 = summonerService.getMostChamp(mostChampParam1); //12 시즌(현재 시즌) 데이터 조회
		List<MostChampDto> mostChamps2 = summonerService.getMostChamp(mostChampParam2); //11 시즌(이전 시즌) 데이터 조회 => 조회되는 데이터 x
		em.flush();
		em.clear();
		
		//then
		int totalgame1 = 0;
		int totalgame2 = 0;

		for(MostChampDto mostChamp : mostChamps1) {
			totalgame1 += mostChamp.getTotalgame();
		}
		
		for(MostChampDto mostChamp : mostChamps2) {
			totalgame2 += mostChamp.getTotalgame();
		}
		
		assertThat(totalgame1).isEqualTo(3);
		assertThat(totalgame2).isEqualTo(0);
	}
	
}
