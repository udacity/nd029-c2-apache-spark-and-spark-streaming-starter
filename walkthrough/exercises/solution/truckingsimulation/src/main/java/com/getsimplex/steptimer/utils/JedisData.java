package com.getsimplex.steptimer.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Sean on 9/1/2015.
 */
public class JedisData {
   private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    private static <T> void loadAndLog(List<T> list, Class clazz) throws Exception{
        Logger infoLogger = Logger.getLogger(JedisData.class.getName());
        Long loadCount = loadToJedis(list, clazz);
        Integer attempted=0;
        if (list!=null && !list.isEmpty()){
            attempted=list.size();
        }
        infoLogger.log(Level.INFO, "Exported: "+loadCount+" "+clazz.getSimpleName()+" records out of: "+attempted+" attempted.");


    }




    public static synchronized <T> ArrayList<T> getEntityList(Class clazz) throws Exception{
        Set<String> set = JedisClient.zrange(clazz.getSimpleName(), 0, -1);
        ArrayList<T> arrayList = new ArrayList<T>();
        for (String string:set){
            arrayList.add((T) gson.fromJson(string, clazz));
        }

        return arrayList;
    }

    public static <T> Long loadToJedis(List<T> list, String keyName) throws Exception{
        for (T object:list){
            String jsonFormatted = gson.toJson(object,object.getClass());
            JedisClient.zadd(keyName, 0, jsonFormatted);
        }

        Long loadCount = JedisClient.zcount(keyName,0d,-1);

        if (loadCount > list.size()){
            JedisClient.zremrangeByScore(keyName, 0, -1);
            throw new Exception("Attempt to load "+list.size()+" elements to key "+keyName+" failed by creating duplicates, reverting to empty list instead");
        }

        return loadCount;

    }

    public static <T> void set(T object, String keyName) throws Exception{
        String jsonFormatted = gson.toJson(object, object.getClass());
        JedisClient.set(keyName, jsonFormatted);
    }

    public static <T> T get(String keyName, Class clazz) throws Exception{
        String jsonFormatted = JedisClient.get(keyName);
        T object = (T) gson.fromJson(jsonFormatted, clazz);
        return object;
    }


    public static <T> void loadToJedis(T record, Class clazz) throws Exception{
        List<T> listOfOne = new ArrayList<T>();
        listOfOne.add(record);
        loadToJedis(listOfOne, clazz);
    }

    public static<T> Long loadToJedis(List<T> list, Class clazz) throws Exception{
        Long loadCount = 0l;
        if (list!=null && !list.isEmpty()) {
            try {
                loadCount=loadToJedis(list, clazz.getSimpleName());
            } catch (Exception e) {
                Thread.sleep(10000);//sleep 10 seconds and try again in case of network failure
                loadCount=loadToJedis(list, clazz.getSimpleName());

                throw (e);
            }
        }
        return loadCount;
    }

    public static <T> Long deleteFromRedis(List<T> list) throws Exception{
        Long deleteCount = 0l;
        int i = 0;
//        public List<CorporateDivision> getCorporateDivisionByID(Integer divisionID){
//            ArrayList<CorporateDivision> filteredDivisions = new ArrayList<>();
        for (T lists: list){
            if(deleteCount<list.size()){
//            if(list.size()>0){
               deleteFromRedis(list.get(i));
                deleteCount++;
                i++;
            }
        }

        return deleteCount;
    }

    public static <T> Long deleteFromRedis (T record) throws Exception{
        String jsonFormatted = gson.toJson(record, record.getClass());
        Long removeCount = JedisClient.zrem(record.getClass().getSimpleName(),jsonFormatted);
        if (removeCount!=1){
            throw new Exception("Attempt to remove the following json from redis failed: "+jsonFormatted);
        }
        return removeCount;

    }



}
