package com.lolsearcher.model.entity.ingame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class CurrentParticipantCompKey implements Serializable {
	private static final long serialVersionUID = -7262931192315016423L;

	private long gameId;
	
	private String summonerId;

}
