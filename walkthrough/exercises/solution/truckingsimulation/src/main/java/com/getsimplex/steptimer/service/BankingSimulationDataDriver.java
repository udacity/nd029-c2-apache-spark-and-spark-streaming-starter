package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.*;
import com.getsimplex.steptimer.utils.JedisData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BankingSimulationDataDriver {

    private static String[] lastNames = {"Jones", "Smith", "Ahmed", "Wu", "Doshi", "Anandh", "Clayton", "Harris", "Gonzalez", "Abram", "Khatib", "Clark", "Mitra", "Habschied", "Jackson", "Phillips", "Lincoln", "Spencer", "Anderson", "Hansen", "Davis", "Jones", "Fibonnaci", "Staples", "Jefferson", "Huey", "Olson", "Howard", "Sanchez", "Aristotle"};
    private static String[] firstNames = {"Sarah", "Bobby", "Frank", "Edward", "Danny", "Chris", "Spencer", "Ashley", "Santosh", "Senthil", "Christina", "Suresh", "Neeraj", "Angie", "Sean", "Lyn", "John", "Ben", "Travis", "David", "Larry", "Jerry", "Gail", "Craig", "Dan", "Jason", "Eric", "Trevor", "Jane", "Jacob", "Jaya", "Manoj", "Liz", "Christina"};
    private static String[] locations = {"Alabama", "France", "New Mexico", "Georgia", "India", "Australia", "China", "Mexico", "Canada", "New Zealand", "Indonesia", "Thailand", "Phillipines", "Uganda", "Ghana", "Nigeria", "Argentina", "Chile", "Togo", "Ivory Coast", "DR Congo", "South Africa", "Brazil", "Ukraine", "Jordan", "United Arab Emirates", "Egypt", "Afghanistan", "Syria", "Iraq", "Italy"};
    private static List<Customer> testCustomers = new ArrayList<Customer>();
    private static Random random = new Random();
    private static Gson gson = new Gson();
    private static boolean simulationActive = false;


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
                customer.setPhone(String.valueOf(random.nextInt(9)+"015551212"));
                customer.setAccountNumber(String.valueOf(String.valueOf(random.nextInt(999999999))));
                customer.setBirthDay((2020-nextCustomerAge++)+"-01-01");//spread age out evenly
                customer.setLocation(locations[random.nextInt(30)]);
                CreateNewCustomer.createCustomer(customer);
                KafkaTopicMessage customerMessage = new KafkaTopicMessage();
                customerMessage.setTopic("bank-customers");
                customerMessage.setKey(customer.getAccountNumber());
                customerMessage.setMessage(gson.toJson(customer));
                MessageIntake.route(customerMessage);
                testCustomers.add(customer);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public static void createBalanceUpdates(){
        for (Customer testCustomer:testCustomers){
            try {
                KafkaTopicMessage balanceMessage = new KafkaTopicMessage();
                balanceMessage.setTopic("balance-updates");
                balanceMessage.setKey(testCustomer.getAccountNumber());
                balanceMessage.setMessage(String.valueOf(random.nextInt(100000)) + "." + String.valueOf(random.nextInt(99)));
                MessageIntake.route(balanceMessage);
                Thread.sleep(2000);
            } catch (Exception e){
                System.out.println("Error sending balance update for customer: "+testCustomer.getCustomerName()+" "+e.getMessage());
            }
        }
    }

    public static void createATMVisits(){
        for (Customer testCustomer:testCustomers){
            try {
                KafkaTopicMessage atmMessage = new KafkaTopicMessage();
                atmMessage.setTopic("atm-visits");
                atmMessage.setKey(testCustomer.getAccountNumber());
                atmMessage.setMessage(gson.toJson(new Date()));
                MessageIntake.route(atmMessage);
                Thread.sleep(2000);
            } catch (Exception e){
                System.out.println("Error sending atm visit for customer: "+testCustomer.getCustomerName()+" "+e.getMessage());
            }
        }
    }



    public static void createDeposits(){
        for (Customer testCustomer:testCustomers){
            try {

                Deposit deposit = new Deposit();
                deposit.setAmount(Float.valueOf(String.valueOf(random.nextInt(1000)) + "." + String.valueOf(random.nextInt(99))));
                deposit.setAccountNumber(testCustomer.getAccountNumber());
                deposit.setDateAndTime(new Date());
                KafkaTopicMessage depositMessage = new KafkaTopicMessage();
                depositMessage.setTopic("bank-deposits");
                depositMessage.setKey(testCustomer.getAccountNumber());
                depositMessage.setMessage(gson.toJson(deposit));
                MessageIntake.route(depositMessage);
                Thread.sleep(2000);
            } catch (Exception e){
                System.out.println("Error sending deposit for customer: "+testCustomer.getCustomerName()+" "+e.getMessage());
            }
        }
    }

    public static void createATMWithdrawals(){
        for (Customer testCustomer:testCustomers){
            try {
                Long transactionId = System.currentTimeMillis();
                ATMTransaction atmTransaction = new ATMTransaction();
                atmTransaction.setTransactionId(transactionId);
                atmTransaction.setTransactionDate(new Date(transactionId));//this works because the transaction id is the milliseconds of the current time/date
                atmTransaction.setAtmLocation(testCustomer.getLocation());

                if (testCustomer.getCustomerName().startsWith("A") || testCustomer.getCustomerName().startsWith("J")) {//suspicious activity
                    atmTransaction.setAtmLocation(locations[random.nextInt(30)]);
                } else{
                    atmTransaction.setAtmLocation(testCustomer.getLocation());
                }

                CustomerLocation customerLocation = new CustomerLocation();
                customerLocation.setAccountNumber(testCustomer.getAccountNumber());
                if (testCustomer.getCustomerName().startsWith("A") || testCustomer.getCustomerName().startsWith("J")) {//traveling outside of home area
                    customerLocation.setLocation(locations[random.nextInt(30)]);
                } else{
                    customerLocation.setLocation(testCustomer.getLocation());
                }

                JedisData.loadToJedis(customerLocation, CustomerLocation.class);//should create Redis events with customer location

                KafkaTopicMessage atmwithdrawalMessage = new KafkaTopicMessage();
                atmwithdrawalMessage.setTopic("atm-withdrawals");
                atmwithdrawalMessage.setKey(testCustomer.getAccountNumber());
                atmwithdrawalMessage.setMessage(gson.toJson(atmTransaction));
                MessageIntake.route(atmwithdrawalMessage);

                Withdrawal withdrawal = new Withdrawal();
                withdrawal.setTransactionId(transactionId);
                withdrawal.setAmount(Float.valueOf(String.valueOf(random.nextInt(1000)) + "." + String.valueOf(random.nextInt(99))));
                withdrawal.setDateAndTime(new Date(transactionId));//this works because the transaction id is the milliseconds of the current time/date
                withdrawal.setAccountNumber(testCustomer.getAccountNumber());

                KafkaTopicMessage withdrawalMessage = new KafkaTopicMessage();
                withdrawalMessage.setTopic("bank-withdrawals");
                withdrawalMessage.setKey(testCustomer.getAccountNumber());
                withdrawalMessage.setMessage(gson.toJson(withdrawal));
                MessageIntake.route(withdrawalMessage);
                Thread.sleep(2000);
            } catch (Exception e){
                System.out.println("Error sending withdrawal for customer: "+testCustomer.getCustomerName()+" "+e.getMessage());
            }
        }
    }
}
