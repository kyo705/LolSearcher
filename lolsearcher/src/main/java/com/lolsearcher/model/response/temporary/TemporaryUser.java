package com.lolsearcher.model.response.temporary;

import com.lolsearcher.model.entity.user.LolSearcherUser;
import lombok.Data;

@Data
public class TemporaryUser {

    private final LolSearcherUser user;

    private final int randomNumber;
}
