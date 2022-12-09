package com.lolsearcher.model.entity.championstatic.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class ChampItemCompKey implements Serializable{
	private static final long serialVersionUID = 1649475722631843255L;
	
	@Column(name = "CHAMPION_ID")
	private String championId;
	@Column(name = "SEASON_ID")
	private int seasonId;
	@Column(name = "ITEM_ID")
	private int itemId;
}
