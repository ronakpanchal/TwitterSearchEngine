package com.ircourse.project;

import com.google.gson.Gson;
import com.ircourse.beans.Tweet;
import com.ircourse.config.GetAPIInstance;
import com.ircourse.dao.TwitterDao;
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

// hillary OR GOP OR TrumpRomCom OR democrat OR republican for us presidential election
// iphone7 ,appleiphone7 ,iphone7launch,iphone7plus
// syria syria,syriacivilwar,aleppo,islamicstate
// us open usopen,us open tennis
// game of thrones GOT,GameofThrones
public class TwitterCrawler {
    public static void main(String[] args) {


        GetAPIInstance obj=new GetAPIInstance();
        Twitter twitter=obj.getTwitterInstance();
        BufferedWriter filewrtiter = null;
        String regexPattern = "[\\uD83D\\uDE00-\\uD83E\\uDD17|\\uD83C\\uDF00-\\uD83D\\uDDFF]+";
        SimpleDateFormat formatedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        Pattern pattern = Pattern.compile(regexPattern);
        File file = new File("C:\\Users\\ronak\\Desktop\\syria_tr.txt");




        QueryResult result;
        Tweet myTweet;
        int totalNumberOfTweetsToFetch=2000;
        long lastId = Long.MAX_VALUE;




        Query query = new Query("syria");
        query.setLang("es");//ko,es,en,tr
        query.setCount(100);
        query.setMaxId(lastId);

        Gson gson = new Gson();
        StringBuilder finalJSON = new StringBuilder();
        finalJSON.append("[");
        List<Status> statuses;
        int count = 0;
        int retweetcount=0;
        long startTime=System.currentTimeMillis();
        TwitterDao dao=new TwitterDao();
        int noOfRequests=1;
        try {
            result = twitter.search(query);
            statuses= result.getTweets();
            filewrtiter = new BufferedWriter(new FileWriter(file));
            while (count < totalNumberOfTweetsToFetch) {
                for (Status status : statuses) {
                    if (!status.isRetweet()) {
                        myTweet=dao.getTweetBean(status,pattern,formatedDate);
                        finalJSON.append(gson.toJson(myTweet)).append(",").append("\n");
                        count++;
                        if (status.getId() < lastId) {
                            lastId = status.getId();
                        }
                    }

                }
                if (totalNumberOfTweetsToFetch - count < 100) {
                    query.setCount(totalNumberOfTweetsToFetch - count);
                } else {
                    query.setCount(100);
                }
                query.setMaxId(lastId - 1);
                try {
                    if((System.currentTimeMillis()-startTime/1000)>=15*3600 && noOfRequests>15){
                        break;
                    }
                    result = twitter.search(query);
                    noOfRequests++;
                    statuses = result.getTweets();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }






        finalJSON = new StringBuilder(finalJSON.substring(0, finalJSON.length() - 2));
        finalJSON.append("]");

        try {
            filewrtiter.write(finalJSON.toString());
            filewrtiter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        }

    }
}
