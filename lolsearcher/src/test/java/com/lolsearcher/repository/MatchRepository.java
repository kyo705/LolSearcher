package com.lolsearcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolsearcher.domain.entity.summoner.match.Match;

public interface MatchRepository extends JpaRepository<Match, String>{

}
