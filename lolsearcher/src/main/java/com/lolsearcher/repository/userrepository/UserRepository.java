package com.lolsearcher.repository.userrepository;

import com.lolsearcher.domain.entity.user.LolSearcherUser;

public interface UserRepository {

	void saveUser(LolSearcherUser user);
	
	LolSearcherUser findUserByName(String username);

	LolSearcherUser findUserByEmail(String email);

}
