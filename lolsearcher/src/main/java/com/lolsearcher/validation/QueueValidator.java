package com.lolsearcher.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.lolsearcher.search.match.MatchConstant.QUEUE_ID_LIST;
import static java.util.Objects.requireNonNull;

@Component
public class QueueValidator implements ConstraintValidator<Queue, Integer> {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        if(value == null) return true;

        return requireNonNull(cacheManager.getCache(QUEUE_ID_LIST)).get(value) != null;
    }
}
