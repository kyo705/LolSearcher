package com.lolsearcher.repository.user;

import com.lolsearcher.model.entity.user.LolSearcherUser;

public interface UserRepository {

	void saveUser(LolSearcherUser user);
	
	LolSearcherUser findUserByName(String username);

	LolSearcherUser findUserByEmail(String email);

}
