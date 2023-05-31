package com.lolsearcher.user.identification;

import org.springframework.stereotype.Repository;

@Repository
public class RedisIdentificationRepository implements IdentificationRepository {

    @Override
    public void save(Long userId, String identificationNum) {

    }

    @Override
    public String find(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
