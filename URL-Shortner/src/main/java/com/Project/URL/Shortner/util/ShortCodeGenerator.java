package com.Project.URL.Shortner.util;

import java.security.SecureRandom;

public class ShortCodeGenerator {

    private static final String Characters = "ASDFGHJKLQWERTYUIOPZXCVBNM1234567890asdfghjklqwertyuiopzxcvbnm";
    private static final int Length = 6;
    private static final SecureRandom random = new SecureRandom();

    public static String generate(){
        StringBuilder code = new StringBuilder();
        for(int i=0;i<Length;i++){
            code.append(Characters.charAt(random.nextInt(Characters.length()))); // nextInt(bound) => 0 to bound-1
        }

        return  code.toString();
    }


}
