package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.*;
import com.google.gson.Gson;
import spark.Request;
import com.getsimplex.steptimer.utils.GsonFactory;
import com.getsimplex.steptimer.utils.JedisData;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Created by .
 */
public class StepHistory {
    private static Logger logger = Logger.getLogger(StepHistory.class.getName());
    private static Gson gson = GsonFactory.getGson();
    private static boolean solutionActive = false;

    static {
        solutionActive = Boolean.valueOf(System.getProperty("solutionActive"));//this makes it like the Spark app is sending messages (only for teacher use)
    }


    public static String saveSensorTail(Request request) throws Exception{
        String tokenString = request.headers("suresteps.session.token");
        Optional<User> user = TokenService.getUserFromToken(tokenString);//

        Boolean tokenExpired = SessionValidator.validateToken(tokenString);

        if (!user.isPresent()){
            throw new Exception("Could not find user with token");
        } else if (tokenExpired.equals(true)){
            throw new Exception("Session expired");
        }

        Tail tail = gson.fromJson(request.body(),Tail.class);
        tail.setSessionId(tokenString);

        JedisData.loadToJedis(tail,Tail.class);

        return tokenString;
    }


    public static String getAllTests(String email) throws Exception{
        ArrayList<RapidStepTest> allTests = JedisData.getEntityList(RapidStepTest.class);
        Predicate<RapidStepTest> historicUserPredicate = user -> user.getCustomer().getEmail().equals(email);

        List<RapidStepTest> rapidStepTests = allTests.stream().filter(historicUserPredicate).collect(Collectors.toList());
        return (gson.toJson(rapidStepTests));
    }

    public static String riskScore(String email) throws Exception{
        logger.info("Received score request for: "+email);
        Optional<Customer> customer = FindCustomer.findCustomer(email);

        if (!customer.isPresent()){
            throw new Exception ("Unable to score risk for non-existent customer: "+email);
        }

        ArrayList<RapidStepTest> allTests = JedisData.getEntityList(RapidStepTest.class);
        Predicate<RapidStepTest> historicUserPredicate = stepTest -> stepTest.getCustomer().getEmail().equals(email);

        List<RapidStepTest> rapidStepTestsSortedByDate = allTests.stream().filter(historicUserPredicate).sorted(Comparator.comparing(RapidStepTest::getStartTime)).collect(Collectors.toList());
        if (rapidStepTestsSortedByDate.size()<4){
            throw new Exception("Customer "+email+" has: "+rapidStepTestsSortedByDate.size()+" rapid step tests on file which is less than the required number(4) to calculate fall risk.");
        }

        RapidStepTest mostRecentTest = rapidStepTestsSortedByDate.get(rapidStepTestsSortedByDate.size()-1);
        RapidStepTest secondMostRecentTest = rapidStepTestsSortedByDate.get(rapidStepTestsSortedByDate.size()-2);

        BigDecimal currentTestAverageScore = BigDecimal.valueOf((mostRecentTest.getStopTime()-mostRecentTest.getStartTime())+ (secondMostRecentTest.getStopTime()-secondMostRecentTest.getStartTime())).divide(BigDecimal.valueOf(2l));

        RapidStepTest thirdMostRecentTest = rapidStepTestsSortedByDate.get(rapidStepTestsSortedByDate.size()-3);
        RapidStepTest fourthMostRecentTest = rapidStepTestsSortedByDate.get(rapidStepTestsSortedByDate.size()-4);

        BigDecimal previousTestAverageScore = BigDecimal.valueOf((thirdMostRecentTest.getStopTime()-thirdMostRecentTest.getStartTime())+ (fourthMostRecentTest.getStopTime()-fourthMostRecentTest.getStartTime())).divide(BigDecimal.valueOf(2l));

        BigDecimal riskScore = (previousTestAverageScore.subtract(currentTestAverageScore)).divide(new BigDecimal(1000l));
        //positive means they have improved
        //negative means they have declined

        Integer birthYear = Integer.valueOf(customer.get().getBirthDay().split("-")[0]);

        CustomerRisk customerRisk = new CustomerRisk();
        customerRisk.setScore(new Float(riskScore.setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
        customerRisk.setCustomer(email);
        customerRisk.setRiskDate(new Date(mostRecentTest.getStopTime()));

        if (solutionActive) {//we don't want this data to be visible if the student is working on retrieving it from redis
            customerRisk.setBirthYear(birthYear);
        }

        logger.info("Risk for customer: "+email+" "+gson.toJson(customerRisk));
        return gson.toJson(customerRisk);
    }



}
