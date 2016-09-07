package com.ircourse.project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ronak on 9/4/2016.
 */
public class EmoticonsTest {

    public static void main(String[] args){
        String str="\uD83D\uDE00";
        String utfString="\\uF0 \\u9F \\u98 9E";
        System.out.println("my emoji"+str);
        String emoticon="hats a nice joke \uD83D\uDE09\uD83D\uDE0B\uD83E\uDD14\uD83D\uDE1E";
        String regexPattern = "[\\uD83D\\uDE00-\\uD83E\\uDD17]+";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(emoticon);
        while(matcher.find()){
            System.out.println(matcher.group());
        }
    }
}
