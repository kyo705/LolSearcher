package com.lolsearcher.user.identification;

public interface IdentificationRepository {

    void save(Long userId, String identificationNum);

    String find(Long userId);

    Boolean delete(Long userId);


}
