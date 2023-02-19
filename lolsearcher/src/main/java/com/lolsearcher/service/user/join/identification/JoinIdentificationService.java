package com.lolsearcher.service.user.join.identification;

import com.lolsearcher.model.entity.user.LolSearcherUser;
import com.lolsearcher.model.request.user.JoinAuthentication;

public interface JoinIdentificationService {
    LolSearcherUser authenticate(JoinAuthentication authentication);
}
