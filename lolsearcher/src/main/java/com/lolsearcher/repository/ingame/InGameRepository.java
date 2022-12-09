package com.lolsearcher.repository.ingame;

import java.util.List;

import com.lolsearcher.model.entity.ingame.InGame;

public interface InGameRepository {

	void saveInGame(InGame inGame);
	
	InGame getInGame(long gameId);
	
	List<InGame> getInGamesBySummonerId(String summonerId);
	
	void deleteInGameBySummonerId(String summonerId);

	void deleteInGame(InGame inGame);

}
