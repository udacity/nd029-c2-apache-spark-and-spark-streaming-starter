package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.Customer;
import com.getsimplex.steptimer.model.DeviceMessage;
import com.getsimplex.steptimer.model.RapidStepTest;
import com.getsimplex.steptimer.model.StediEvent;
import com.getsimplex.steptimer.utils.JedisData;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.util.*;

public class SimulationDataDriver {

    private static String[] lastNames = {"Jones", "Smith", "Ahmed", "Wu", "Doshi", "Anandh", "Clayton", "Harris", "Gonzalez", "Abram", "Khatib", "Clark", "Mitra", "Habschied", "Jackson", "Phillips", "Lincoln", "Spencer", "Anderson", "Hansen", "Davis", "Jones", "Fibonnaci", "Staples", "Jefferson", "Huey", "Olson", "Howard", "Sanchez", "Aristotle"};
    private static String[] firstNames = {"Sarah", "Bobby", "Frank", "Edward", "Danny", "Chris", "Spencer", "Ashley", "Santosh", "Senthil", "Christina", "Suresh", "Neeraj", "Angie", "Sean", "Lyn", "John", "Ben", "Travis", "David", "Larry", "Jerry", "Gail", "Craig", "Dan", "Jason", "Eric", "Trevor", "Jane", "Jacob", "Jaya", "Manoj", "Liz", "Christina"};
    private static List<Customer> testCustomers = new ArrayList<Customer>();
    private static Random random = new Random();
    private static Gson gson = new Gson();
    private static Session remoteSession;
    private static boolean simulationActive = false;
    private static Map<String, Long> mostRecentTestTime = new HashMap<String, Long>();
    private static boolean solutionActive = false;

    static {
        solutionActive = Boolean.valueOf(System.getProperty("solutionActive"));//this makes it like the Spark app is sending messages (only for teacher use)
    }

    public static synchronized void setSimulationActive(boolean active){
        simulationActive= active;
    }

    public static synchronized  boolean getSimulationActive(){
        return simulationActive;
    }

    public static synchronized void setRemoteSession(Session session){
        remoteSession=session;
    }

    public static synchronized void generateTestCustomers(int numberOfUsers) {
        testCustomers.clear();
        int nextCustomerAge = 55;
        for (int i = 0; i < numberOfUsers - 1; i++) {
            try {
                Customer customer = new Customer();
                String firstName = firstNames[random.nextInt(numberOfUsers)];
                String lastName = lastNames[random.nextInt(numberOfUsers)];
                customer.setCustomerName(firstName + " " + lastName);
                customer.setEmail(firstName + "." + lastName + "@test.com");
                customer.setPhone("8015551212");
                customer.setBirthDay((2020-nextCustomerAge++)+"-01-01");//spread age out evenly
                CreateNewCustomer.createCustomer(customer);
                testCustomers.add(customer);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public static void createRapidStepTests() {
        for (Customer testCustomer:testCustomers){
             try {
                 long randomChange;
                 long testTime;
                 Integer birthYear = Integer.valueOf(testCustomer.getBirthDay().split("-")[0]);
                 Optional<Long> previousTestTime= Optional.empty();
                 if (mostRecentTestTime.containsKey(testCustomer.getCustomerName())){
                     previousTestTime=Optional.of(mostRecentTestTime.get(testCustomer.getCustomerName()));
                 }

                 if (!previousTestTime.isPresent()){//create their first score
                     randomChange = random.nextInt(60);//negative offset (in seconds) from 2 minute test time
                     testTime = (120 - randomChange) * 1000;//test time (in milliseconds)
                 } else{
                    if ( birthYear>=1950 && birthYear <=1960 ){
                        randomChange = random.nextInt(4);//simulate deteriorating change of 1-4 seconds
                        testTime = previousTestTime.get() + (randomChange*1000);
                    } else {
                        randomChange = random.nextInt(60);//random variation (in seconds) from arbitrary 2 minutes (test time)
                        testTime = (120 - randomChange) * 1000;//test time (in milliseconds)
                    }
                 }

                 mostRecentTestTime.put(testCustomer.getCustomerName(), testTime);

                 RapidStepTest rapidStepTest = new RapidStepTest();
                 rapidStepTest.setCustomer(testCustomer);
                 rapidStepTest.setStopTime(System.currentTimeMillis());
                 rapidStepTest.setStartTime(rapidStepTest.getStopTime() - testTime);
                 rapidStepTest.setTestTime(testTime);
                 rapidStepTest.setTotalSteps(30);
                 JedisData.loadToJedis(rapidStepTest, RapidStepTest.class);
                 Thread.sleep(2000);//2 seconds sleep time between each message makes a new message every minute for every customer assuming 30 test customers
                 String riskScoreJson = StepHistory.riskScore(testCustomer.getEmail());//this is logged, and we don't actually need it right here, we just want it to be visible for logging purposes

                 StediEvent event = new StediEvent();
                 event.setMessage(riskScoreJson);

                 MessageIntake.route(event);//this is where we will pick up the CustomerRisk events from outside of STEDI (like in Spark for example)

                 if (solutionActive) {//this is directly simulating the messages that will be coming from Kafka (WITH the birth year) when solved
                     try {
                         DeviceMessage deviceMessage = new DeviceMessage();
                         deviceMessage.setDate(System.currentTimeMillis());
                         deviceMessage.setDeviceId("1234");//this is just a device id used for testing
                         deviceMessage.setMessage(riskScoreJson);
                         MessageIntake.route(deviceMessage);

                     } catch (Exception e) {
                         System.out.println("Error retrieving risk score for customer: " + e.getMessage());
                     }
                 }
             } catch (Exception e){
                 System.out.println(e.getMessage());
             }
        }
    }

}
