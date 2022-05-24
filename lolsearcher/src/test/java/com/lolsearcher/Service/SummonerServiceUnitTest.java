package com.lolsearcher.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.lolsearcher.domain.Dto.summoner.RankDto;
import com.lolsearcher.domain.Dto.summoner.SummonerDto;
import com.lolsearcher.domain.Dto.summoner.TotalRanksDto;
import com.lolsearcher.domain.entity.Summoner;
import com.lolsearcher.domain.entity.match.Match;
import com.lolsearcher.domain.entity.rank.Rank;
import com.lolsearcher.domain.entity.rank.RankCompKey;
import com.lolsearcher.repository.SummonerRepository.SummonerRepository;
import com.lolsearcher.restapi.RiotRestAPI;
import com.lolsearcher.service.SummonerService;

@ExtendWith(MockitoExtension.class)
class SummonerServiceUnitTest {
	
	SummonerService summonerService;
	@Mock 
	SummonerRepository summonerRepository;
	@Mock 
	RiotRestAPI riotRestApi;
	@Mock 
	ApplicationContext applicationContext;
	@Mock
	EntityManager em;
	
	SummonerDto summonerdto1;
	
	@BeforeEach
	void upset() {
		summonerService = new SummonerService(summonerRepository, riotRestApi, applicationContext);
	}

	//----------------------findDbSummoner() �޼ҵ� Test Case------------------------------------
	
	@Test
	void findDbSummonerCase1() {
		//testCase1 : DB�� �г����� 1�� ������ ��
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		List<Summoner> dbSummoners = new ArrayList<>();
		Summoner summoner = new Summoner();
		summoner.setId("id");
		summoner.setName(summonerName);
		dbSummoners.add(summoner);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto.getName()).isEqualTo(summonerName);
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner.getId());
	}
	
	@Test
	void findDbSummonerCase2() {
		//testCase2 : DB�� �г����� �������� ���� ��
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner = new Summoner();
		summoner.setId("id");
		summoner.setName(summonerName);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(new ArrayList<>());
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto).isEqualTo(null);
		
		verify(riotRestApi, times(0)).getSummonerById(anyString());
	}
	
	@Test
	void findDbSummonerCase3() {
		//testCase3 : DB�� �г����� 2�� �̻� ������ �� �ش� ���� �г����� ���� ������ DB�� ������ ��
		//�г����� ���� ������ ������ �����ϹǷ� 
		//DB ������ ������ �ȵǸ� �г��� �ߺ���Ȳ�� �߻��� �� ����
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		
		Summoner summoner2 = new Summoner();
		summoner2.setId("id2");
		summoner2.setName(summonerName);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(summoner1);
		dbSummoners.add(summoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		Summoner summoner3 = new Summoner();
		summoner3.setId("id1");
		summoner3.setName(summonerName);
		summoner3.setProfileIconId(50);
		
		Summoner summoner4 = new Summoner();
		summoner4.setId("id2");
		summoner4.setName("����������");
		
		when(riotRestApi.getSummonerById("id1"))
		.thenReturn(summoner3);
		
		when(riotRestApi.getSummonerById("id2"))
		.thenReturn(summoner4);
		
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner3.getId());
		assertThat(summonerDto.getName()).isEqualTo("Ǫ��Ǫ��");
		assertThat(summonerDto.getProfileIconId()).isEqualTo(50);
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
		verify(summonerRepository, times(2)).saveSummoner(any(Summoner.class));
	}
	
	@Test
	void findDbSummonerCase4() {
		//testCase4 : DB�� �г����� 2�� �̻� ������ �� 
		//�ش� ���� �г����� ���� ������ DB�� �������� ���� ��
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		
		Summoner summoner2 = new Summoner();
		summoner2.setId("id2");
		summoner2.setName(summonerName);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(summoner1);
		dbSummoners.add(summoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		Summoner summoner3 = new Summoner();
		summoner3.setId("id1");
		summoner3.setName("����Ŀ");
		summoner3.setProfileIconId(50);
		
		Summoner summoner4 = new Summoner();
		summoner4.setId("id2");
		summoner4.setName("����������");
		
		when(riotRestApi.getSummonerById("id1"))
		.thenReturn(summoner3);
		
		when(riotRestApi.getSummonerById("id2"))
		.thenReturn(summoner4);
		
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto).isEqualTo(null);
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
		verify(summonerRepository, times(2)).saveSummoner(any(Summoner.class));
	}
	
	@Test
	void findDbSummonerCase5() {
		//testCase5 : DB�� �г����� 2�� �̻� ������ �� 
		//�ش� ����Ʈ �� ���� �� ������ �ο��� �ְ�, �ش� �г����� ���� ���� ������ ��
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		
		Summoner summoner2 = new Summoner();
		summoner2.setId("id2");
		summoner2.setName(summonerName);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(summoner1);
		dbSummoners.add(summoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		when(riotRestApi.getSummonerById("id1"))
		.thenThrow(new WebClientResponseException(404, "�������� �ʴ� �����Դϴ�", null, null, null));
		
		Summoner summoner3 = new Summoner();
		summoner3.setId("id2");
		summoner3.setName("Ǫ��Ǫ��");
		
		when(riotRestApi.getSummonerById("id2"))
		.thenReturn(summoner3);
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner3.getId());
		assertThat(summonerDto.getName()).isEqualTo(summoner3.getName());
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
		verify(summonerRepository, times(1)).saveSummoner(summoner3);
		verify(summonerRepository, times(1)).deleteSummoner(summoner1);
	}
	
	@Test
	void findDbSummonerCase6() {
		//testCase6 : DB�� �г����� 2�� �̻� ������ �� 
		//�ش� ����Ʈ �� ���� �� ������ �ο��� �ְ�, �ش� �г����� ���� ���� �������� ���� ��
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		
		Summoner summoner2 = new Summoner();
		summoner2.setId("id2");
		summoner2.setName(summonerName);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(summoner1);
		dbSummoners.add(summoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		when(riotRestApi.getSummonerById("id1"))
		.thenThrow(new WebClientResponseException(404, "�������� �ʴ� �����Դϴ�", null, null, null));
		
		Summoner summoner3 = new Summoner();
		summoner3.setId("id2");
		summoner3.setName("����Ŀ");
		
		when(riotRestApi.getSummonerById("id2"))
		.thenReturn(summoner3);
		
		//when
		SummonerDto summonerDto = summonerService.findDbSummoner(summonerName);
		
		//then
		assertThat(summonerDto).isEqualTo(null);
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
		verify(summonerRepository, times(1)).saveSummoner(summoner3);
		verify(summonerRepository, times(1)).deleteSummoner(summoner1);
	}
	
	@Test
	void findDbSummonerCase7() {
		//testCase7 : DB�� �г����� 2�� �̻� ������ �� 
		//�ش� ����Ʈ �� ���� ������ �����ϰ� �ִ� �� rest api ��û ���� Ƚ���� �ʰ��� ���
		//������ ���� �� DB������ �ѹ����� Ŀ������ �����ؾ���(@tranactional(noRollbackFor=?)�� �̿���)
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		
		Summoner summoner2 = new Summoner();
		summoner2.setId("id2");
		summoner2.setName(summonerName);
		
		List<Summoner> dbSummoners = new ArrayList<>();
		dbSummoners.add(summoner1);
		dbSummoners.add(summoner2);
		
		when(summonerRepository.findSummonerByName(summonerName))
		.thenReturn(dbSummoners);
		
		Summoner summoner3 = new Summoner();
		summoner3.setId("id1");
		summoner3.setName("����Ŀ");
		
		when(riotRestApi.getSummonerById("id1"))
		.thenReturn(summoner3);
		
		when(riotRestApi.getSummonerById("id2"))
		.thenThrow(new WebClientResponseException(429, "�ʹ� ���� ��û�Դϴ�. ��� ��, �ٽ� �õ����ּ���", null, null, null));
		
		
		//when,then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.findDbSummoner(summonerName));
		
		assertThat(e.getStatusCode().value()).isEqualTo(429);
		assertThat(e.getStatusText()).isEqualTo("�ʹ� ���� ��û�Դϴ�. ��� ��, �ٽ� �õ����ּ���");
		
		verify(riotRestApi, times(2)).getSummonerById(anyString());
		verify(summonerRepository, times(1)).saveSummoner(summoner3);
	}
	
	//----------------------setSummoner() �޼ҵ� Test Case------------------------------------
	
	@Test
	public void setSummonerCase1() {
		//test Case 1 : �ش� �г����� ���� ������ ������ ��
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		Summoner summoner1 = new Summoner();
		summoner1.setId("id1");
		summoner1.setName(summonerName);
		summoner1.setSummonerLevel(333);
		
		when(riotRestApi.getSummonerByName(summonerName))
		.thenReturn(summoner1);
		
		
		//when
		SummonerDto summonerDto = summonerService.setSummoner(summonerName);
		
		
		//then
		assertThat(summonerDto.getSummonerid()).isEqualTo(summoner1.getId());
		assertThat(summonerDto.getName()).isEqualTo(summoner1.getName());
		assertThat(summonerDto.getSummonerLevel()).isEqualTo(summoner1.getSummonerLevel());
		
		verify(summonerRepository, times(1)).saveSummoner(summoner1);
	}
	
	@Test
	public void setSummonerCase2() {
		//test Case 2 : REST ����� ������ ���(EX. �ش� �г����� ���� ������ �������� ���� ���)
		
		//given
		String summonerName = "Ǫ��Ǫ��";
		
		when(riotRestApi.getSummonerByName(summonerName))
		.thenThrow(new WebClientResponseException(404,"�������� �ʴ� �г����Դϴ�.", null, null, null));
		
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setSummoner(summonerName));
		
		assertThat(e.getStatusCode().value()).isEqualTo(404);
		assertThat(e.getStatusText()).isEqualTo("�������� �ʴ� �г����Դϴ�.");
		verify(riotRestApi, times(1)).getSummonerByName(anyString());
		verify(summonerRepository, times(0)).saveSummoner(any(Summoner.class));
	}
	
	
	//----------------------setLeague() �޼ҵ� Test Case------------------------------------
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void setLeagueCase1() {
		//test Case 1 : REST ����� ���� ������ ��ũ ���� ������ ������ ���
		
		//given
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("Ǫ��Ǫ��");
		summoner.setSummonerLevel(553);
		
		RankDto teamRank = new RankDto();
		teamRank.setSummonerId(summoner.getSummonerid());
		teamRank.setQueueType("RANKED_TEAM_5x5");
		teamRank.setRank("IV");
		teamRank.setTier("GOLD");
		teamRank.setWins(100);
		teamRank.setLosses(50);
		
		RankDto soloRank = new RankDto();
		soloRank.setSummonerId(summoner.getSummonerid());
		soloRank.setQueueType("RANKED_SOLO_5x5");
		soloRank.setRank("III");
		soloRank.setTier("GOLD");
		soloRank.setWins(55);
		soloRank.setLosses(33);
		
		List<RankDto> ranks = new ArrayList<>();
		ranks.add(teamRank);
		ranks.add(soloRank);
		
		when(riotRestApi.getLeague(summoner.getSummonerid()))
		.thenReturn(ranks);
		
		
		//when
		TotalRanksDto totalRanksDto = summonerService.setLeague(summoner);
		
		
		//then
		assertThat(totalRanksDto.getSolorank()).isEqualTo(soloRank);
		assertThat(totalRanksDto.getTeamrank()).isEqualTo(teamRank);
		assertThat(totalRanksDto.getSolorank().getSeasonId()).isEqualTo(22);
		assertThat(totalRanksDto.getTeamrank().getSeasonId()).isEqualTo(22);
		
		verify(riotRestApi, times(1)).getLeague(anyString());
		verify(summonerRepository, times(1)).saveLeagueEntry(any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void setLeagueCase2() {
		//test Case 2 : REST ����� ������ ���(EX. ��û ���� Ƚ���� �ʰ��� ���)
		
		//given
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("Ǫ��Ǫ��");
		summoner.setSummonerLevel(553);
		
		when(riotRestApi.getLeague(summoner.getSummonerid()))
		.thenThrow(new WebClientResponseException(429,"���� ��û�� �߻��߽��ϴ�. ��� �� �ٽ� �õ����ּ���.", null, null, null));
		
		
		//when & then
		WebClientResponseException e = assertThrows(WebClientResponseException.class,
				()->summonerService.setLeague(summoner));
		assertThat(e.getStatusCode().value()).isEqualTo(429);
		assertThat(e.getStatusText()).isEqualTo("���� ��û�� �߻��߽��ϴ�. ��� �� �ٽ� �õ����ּ���.");
		
		verify(riotRestApi, times(1)).getLeague(anyString());
		verify(summonerRepository, times(0)).saveLeagueEntry(any(List.class));
	}
	
	
	//----------------------getLeague() �޼ҵ� Test Case------------------------------------
	
	@Test
	public void getLeagueCase1() {
		//test Case 1 : DB���� ���� Rank ���� ������ ������ �� soloRank ������ ���� ���
		
		//given
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("Ǫ��Ǫ��");
		summoner.setSummonerLevel(553);
		
		Rank rank1 = new Rank();
		rank1.setCk(new RankCompKey("id1", "RANKED_SOLO_5x5", 22));
		rank1.setTier("GOLD");
		rank1.setRank("III");
		rank1.setWins(50);
		rank1.setLosses(30);
		
		List<Rank> ranks = new ArrayList<>();
		ranks.add(rank1);
		
		when(summonerRepository.findLeagueEntry(summoner.getSummonerid(), 22))
		.thenReturn(ranks);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getTeamrank()).isEqualTo(null);
		
		assertThat(totalRankDto.getSolorank().getSummonerId())
		.isEqualTo(rank1.getCk().getSummonerId());
		assertThat(totalRankDto.getSolorank().getWins()).isEqualTo(rank1.getWins());
		assertThat(totalRankDto.getSolorank().getLosses()).isEqualTo(rank1.getLosses());
	}
	
	@Test
	public void getLeagueCase2() {
		//test Case 2 : DB���� ���� Rank ���� ������ ������ �� teamRank ������ ���� ���
		
		//given
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("Ǫ��Ǫ��");
		summoner.setSummonerLevel(553);
		
		Rank rank1 = new Rank();
		rank1.setCk(new RankCompKey("id1", "RANKED_TEAM_5x5", 22));
		rank1.setTier("GOLD");
		rank1.setRank("II");
		rank1.setWins(70);
		rank1.setLosses(44);
		
		List<Rank> ranks = new ArrayList<>();
		ranks.add(rank1);
		
		when(summonerRepository.findLeagueEntry(summoner.getSummonerid(), 22))
		.thenReturn(ranks);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getSolorank()).isEqualTo(null);
		
		assertThat(totalRankDto.getTeamrank().getSummonerId())
		.isEqualTo(rank1.getCk().getSummonerId());
		assertThat(totalRankDto.getTeamrank().getWins()).isEqualTo(rank1.getWins());
		assertThat(totalRankDto.getTeamrank().getLosses()).isEqualTo(rank1.getLosses());
	}
	
	@Test
	public void getLeagueCase3() {
		//test Case 3 : DB���� ���� Rank ���� ������ ������ �� 
		//teamRank, soloRank ���� �Ѵ� ���� ���
		
		//given
		SummonerDto summoner = new SummonerDto();
		summoner.setSummonerid("id1");
		summoner.setName("Ǫ��Ǫ��");
		summoner.setSummonerLevel(553);
		
		Rank rank1 = new Rank();
		rank1.setCk(new RankCompKey("id1", "RANKED_SOLO_5x5", 22));
		rank1.setTier("GOLD");
		rank1.setRank("III");
		rank1.setWins(50);
		rank1.setLosses(30);
		
		Rank rank2 = new Rank();
		rank2.setCk(new RankCompKey("id1", "RANKED_TEAM_5x5", 22));
		rank2.setTier("GOLD");
		rank2.setRank("II");
		rank2.setWins(70);
		rank2.setLosses(44);
		
		List<Rank> ranks = new ArrayList<>();
		ranks.add(rank1);
		ranks.add(rank2);
		
		when(summonerRepository.findLeagueEntry(summoner.getSummonerid(), 22))
		.thenReturn(ranks);
		
		
		//when
		TotalRanksDto totalRankDto = summonerService.getLeague(summoner);
		
		//then
		assertThat(totalRankDto.getSolorank().getSummonerId())
		.isEqualTo(rank1.getCk().getSummonerId());
		assertThat(totalRankDto.getSolorank().getWins()).isEqualTo(rank1.getWins());
		assertThat(totalRankDto.getSolorank().getLosses()).isEqualTo(rank1.getLosses());
		
		assertThat(totalRankDto.getTeamrank().getSummonerId())
		.isEqualTo(rank2.getCk().getSummonerId());
		assertThat(totalRankDto.getTeamrank().getWins()).isEqualTo(rank2.getWins());
		assertThat(totalRankDto.getTeamrank().getLosses()).isEqualTo(rank2.getLosses());
	}
	
	//----------------------setMatches() �޼ҵ� Test Case------------------------------------
	
	@Test
	void setMatchesCase1() {
		//test Case 1 : 
		
		//given
		SummonerDto summonerDto = new SummonerDto();
		summonerDto.setPuuid("puuId1");
		summonerDto.setSummonerid("id1");
		summonerDto.setName("Ǫ��Ǫ��");
		
		Summoner summoner = new Summoner();
		summoner.setId("id1");
		summoner.setPuuid("puuId1");
		summoner.setName("Ǫ��Ǫ��");
		summoner.setLastmatchid("");
		
		when(summonerRepository.findSummonerById(summonerDto.getSummonerid()))
		.thenReturn(summoner);
		
		List<String> matchIds = new ArrayList<>();
		matchIds.add("matchId1");
		matchIds.add("matchId2");
		matchIds.add("matchId3");
		matchIds.add("matchId4");
		matchIds.add("matchId5");
		
		when(riotRestApi.listofmatch(summonerDto.getPuuid(),0,"all",0,20,summoner.getLastmatchid()))
		.thenReturn(matchIds);
		
		Match match1 = new Match();
		Match match2 = new Match();
		Match match3 = new Match();
		Match match4 = new Match();
		Match match5 = new Match();
		
		when(riotRestApi.getmatch("matchId1"))
		.thenReturn(match1);
		
		when(riotRestApi.getmatch("matchId2"))
		.thenReturn(match2);
		
		when(riotRestApi.getmatch("matchId3"))
		.thenAnswer(I->{
			matchIds.set(2, "matchId3-2");
			
			throw new WebClientResponseException(429, "TOO_MANY_REQUEST", null, null, null);
		});
		
		when(applicationContext.getBean(EntityManagerFactory.class))
		.thenReturn(null);
		
		when(em.find(Match.class, "matchId3-2"))
		.thenReturn(null);
		when(em.find(Match.class, "matchId3-2"))
		.thenReturn(match1);
		when(em.find(Match.class, "matchId4"))
		.thenReturn(null);
		when(em.find(Match.class, "matchId5"))
		.thenReturn(null);
		
		when(riotRestApi.getmatch("matchId3-2"))
		.thenReturn(match3);
		
		when(riotRestApi.getmatch("matchId4"))
		.thenReturn(match4);
		
		when(riotRestApi.getmatch("matchId5"))
		.thenReturn(match5);
		
		
		//when
		summonerService.setMatches(summonerDto);
		
		//then
		verify(summonerRepository, times(2)).saveMatch(any(Match.class));
	}
	
	//----------------------getMatches() �޼ҵ� Test Case------------------------------------
	
	
	
	//----------------------getMostchamp() �޼ҵ� Test Case----------------------------------
	
	
	
}
