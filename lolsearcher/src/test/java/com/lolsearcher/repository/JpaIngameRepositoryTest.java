package com.lolsearcher.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.lolsearcher.repository.ingamerepository.IngameRepository;

@ActiveProfiles("test")
@DataJpaTest
public class JpaIngameRepositoryTest {

	@Autowired
	IngameRepository ingameRepository;
	
	@Autowired
	JpaTestRepository jpaTestRepository;
}
