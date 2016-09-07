package com.ircourse.project;

import com.google.gson.Gson;
import com.ircourse.beans.Tweet;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TwitterCrawler {
    public static void main(String[] args) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("CmGTkI5bdCzyaugtN3AhAfqwK")
                .setOAuthConsumerSecret("ewgYxToQ2L96A1csTbbhUH1VWvE2CXS396gveorvfX3aXoVD8P")
                .setOAuthAccessToken("31837968-WnItKCzUNtUSJ77xUWRMzsjx470EhmbWpzvoFhsRg")
                .setOAuthAccessTokenSecret("NCJ54IE3nUFGO8DVup9mdWxhwVqaAqWDJnMcUKxgQ4lDJ");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query = new Query("syria");
        query.setLang("tr");//ko,es,en,tr
        QueryResult result = null;
        query.setCount(100);
        String regexPattern = "[\\uD83D\\uDE00-\\uD83E\\uDD17]+";
        SimpleDateFormat sm = new SimpleDateFormat("YYYY-mm-dd'T'hh:MM:ssZ");
        Pattern pattern = Pattern.compile(regexPattern);
        try {
            result = twitter.search(query);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        File file = new File("C:\\Users\\ronak\\Desktop\\tweets1.txt");
        BufferedWriter filewrtiter = null;
        try {
            filewrtiter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Tweet myTweet;

        Gson gson = new Gson();
        StringBuilder finalJSON = new StringBuilder();
        finalJSON.append("[");
        List<Status> statuses = result.getTweets();
        int count=0;
        long lastId=Long.MAX_VALUE;
        while(count<500){
            for (Status status : statuses) {
                if (!status.isRetweet()) {
                myTweet = new Tweet();
                    myTweet.setRetweet(status.isRetweet());
                myTweet.setDate(sm.format(status.getCreatedAt()));
                myTweet.setText(status.getText());
                myTweet.setId(status.getId());
                    myTweet.setLanguage(status.getLang());
                    if(status.getPlace()!=null){
                        myTweet.setPlace(status.getPlace().getCountry());
                    }
                List<String> urls = new ArrayList<String>();
                for (URLEntity urlEntity : status.getURLEntities()) {
                    urls.add(urlEntity.getURL());
                }
                List<String> mentions=new ArrayList<String>();
                    for(UserMentionEntity mention:status.getUserMentionEntities()){
                        mentions.add(mention.getName());
                    }

                myTweet.setMentions(mentions);

                List<String> emoticons=new ArrayList<String>();
                    Matcher matcher = pattern.matcher(status.getText());
                    while(matcher.find()){
                        emoticons.add(matcher.group());
                    }
                    myTweet.setEmoticons(emoticons);

                List<String> hashtags=new ArrayList<String>();

                 for(HashtagEntity hashtag:status.getHashtagEntities()){
                     hashtags.add(hashtag.getText());
                 }
                 myTweet.setHashtags(hashtags);
                    myTweet.setUser("@"+status.getUser().getName());
                myTweet.setUrls(urls);
                myTweet.setUser(status.getUser().getName());
                finalJSON.append(gson.toJson(myTweet).toString() + ","+"\n");
                    count++;
                    if(status.getId()<lastId){
                        lastId=status.getId();
                    }
                }

            }
            if(500-count<100){
                query.setCount(500-count);
            }
            else{
                query.setCount(100);
            }
            query.setMaxId(lastId-1);
            try {
                result = twitter.search(query);
                statuses=result.getTweets();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }

        finalJSON = new StringBuilder(finalJSON.substring(0, finalJSON.length() - 1));
        finalJSON.append("]");

        try {
            filewrtiter.write(finalJSON.toString());
            filewrtiter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
