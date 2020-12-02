# Parse a JSON payload into separate fields for analysis
In this exercise we will be working with a Kafka topic created to broadcast deposits in a bank. Each message contains the following data:
- account number
- amount
- date and time

You will create a view with this data so you can query it using spark.sql.


If you don't already have the docker containers running, execute the following commands:

```
cd [repositoryfolder]
docker-compose up
```

You should see 9 containers when you run this command:
```
docker ps
```

- Complete the bank-deposits.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-2-joins-and-json/exercises/starter subfolder of the repository then type: 

    Mac/Linux/Unix:
     ```
     submit-bank-deposits.sh
     ```

    Windows:
     ```
     submit-bank-deposits.cmd
     ```    

- Watch the terminal for the values to scroll past
        
# Join Streaming DataFrames from Different Datasources

In this exercise you will be working with the deposit topic and a topic that contains customer information. Each customer message contains the following information:
- customer name
- email
- phone
- birth day
- account number
- customer location

You will join the information from the bank deposit topic and the customer topic to create a view that contains the customer and the deposit.


- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the customer-deposits.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-2-joins-and-json/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-customer-deposits.sh
     ```

    Windows:
     ```
     submit-customer-deposits.cmd
     ```   
- Watch the terminal for the values to scroll past       

# Write a Streaming Dataframe to Kafka with Aggregated Data

In this exercise you will be working with a bank withdrawals topic and an Automated Teller Machine (ATM) visit topic. The goal will be to join both those data streams in a way that allows you to identify withdrawals connected with an ATM visit.


- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the bank-withdrawals.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-2-joins-and-json/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-bank-withdrawals.sh
     ```

    Windows:
     ```
     submit-bank-withdrawals.cmd
     ```   
- Watch the terminal for the values to scroll past        