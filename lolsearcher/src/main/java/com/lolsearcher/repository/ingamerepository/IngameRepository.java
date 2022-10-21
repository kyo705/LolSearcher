package com.lolsearcher.repository.ingamerepository;

import java.util.List;

import com.lolsearcher.domain.entity.ingame.InGame;

public interface IngameRepository {

	void saveIngame(InGame ingame);
	
	InGame getInGame(long gameId);
	
	List<InGame> getInGamesBySummonerId(String summonerid);
	
	void deleteIngameBySummonerId(String summonerid);

	void deleteIngame(InGame ingame);

}
