package com.lolsearcher.utils;

import java.util.Random;

public class RandomNumberUtils {

    private static final Random RANDOM = new Random();
    public static String create(int identificationNumberSize) {

        StringBuilder sb = new StringBuilder(RANDOM.nextInt(identificationNumberSize));

        while(sb.length() < identificationNumberSize) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
