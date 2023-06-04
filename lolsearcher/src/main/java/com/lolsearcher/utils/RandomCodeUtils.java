package com.lolsearcher.utils;

public class RandomCodeUtils {

    public static String create(int identificationCodeSize) {

        long bound = 1L;
        for(int i=0;i<identificationCodeSize;i++){
            bound *= 10;
        }

        long code = (long)(Math.random()*bound);

        StringBuilder sb = new StringBuilder();
        sb.append(code);

        while(sb.length() < identificationCodeSize) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}
