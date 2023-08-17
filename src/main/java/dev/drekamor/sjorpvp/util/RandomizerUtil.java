package dev.drekamor.sjorpvp.util;

import java.util.List;
import java.util.Random;

public class RandomizerUtil {
    public static String randomString(List<String> input){
        Random random = new Random();
        return input.get(random.nextInt(input.size()));
    }
}
