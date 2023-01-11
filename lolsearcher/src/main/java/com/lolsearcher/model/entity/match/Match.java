package com.lolsearcher.model.entity.match;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.lolsearcher.constant.LolSearcherConstants.THE_NUMBER_OF_TEAMS;

@NoArgsConstructor
@Data
@Entity
@Table(name = "MATCHES", indexes = {@Index(columnList = "matchId", unique = true)})
public class Match implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String matchId; /* REST API로 받아올 때 필요한 고유한 match id */
	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int seasonId;
	private String version;

	@JsonManagedReference
	@BatchSize(size = 200) /* match 100개 => team 200팀 */
	@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Team> teams = new ArrayList<>(THE_NUMBER_OF_TEAMS);

}
