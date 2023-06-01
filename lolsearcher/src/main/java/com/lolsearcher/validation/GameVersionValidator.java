package com.lolsearcher.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.lolsearcher.search.match.MatchConstant.GAME_VERSION_LIST;
import static java.util.Objects.requireNonNull;

@Component
public class GameVersionValidator implements ConstraintValidator<GameVersion, String> {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) return true;

        return requireNonNull(cacheManager.getCache(GAME_VERSION_LIST)).get(value) != null;
    }
}
