package com.lolsearcher.model.entity.rank;

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
public class RankCompKey implements Serializable {
	private static final long serialVersionUID = -8000650119610519628L;
	
	private String summonerId;
	private String queueType;
	private int seasonId;
}
