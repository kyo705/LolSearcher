package com.lolsearcher.model.entity.match;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@NoArgsConstructor
@Data
@Entity
@Table(name = "MATCHES")
public class Match {

	@Id
	@Column(name = "ID")
	private String matchId;
	private long gameDuration;
	private long gameEndTimestamp;
	private int queueId;
	private int season;

	@JsonManagedReference
	@BatchSize(size = 100)
	@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Member> members = new ArrayList<>(10);

	public void addMember(Member member) {
		this.members.add(member);
	}
	public void removeMember(Member member) {
		this.members.remove(member);
	}
}
