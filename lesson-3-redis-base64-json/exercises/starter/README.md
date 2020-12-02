# Start a Spark Cluster

Working with Redis, it is extremely important to know how to use the Redis Command Line Interface (redis-cli) command. This command allows you to directly interact with the Redis database, by creating, updating, or deleting data.
Now that we have connected Redis with Kafka using the Kafka Connect Redis Source, we should verify that it is working correctly.



- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Run the Redis CLI:
     - From the terminal type: ```docker exec -it nd029-c2-apache-spark-and-spark-streaming_redis_1 redis-cli```
     - From the terminal type: ```keys **```
     - You will see the list of all the Redis tables
     - From another terminal type ```docker exec -it nd029-c2-apache-spark-and-spark-streaming_kafka_1 kafka-console-consumer --bootstrap-server localhost:19092 --topic redis-server```
     - From the first terminal type: ```zadd testkey 0 testvalue```
     - Open the second terminal
     - A JSON message will appear from the redis-server topic
     - The key contains the base64 encoded name of the Redis table
     - zSetEntries and zsetEntries contain the changes made to the Redis sorted set. 
- To decode the name of the Redis table, open a third terminal, and type: ```echo "[encoded table]" | base64 -d```
- To decode the changes made, from the zSetEntries table, copy the "element" value, then from the third terminal type: ```echo "[encoded value]" | base64 -d```

# Parse Base64 with Pyspark
Now that we have validated the Kafka Connect Redis Source, we can start writing code to extract data from the redis-server topic. Redis transmits its data to Kafka in Base64 encoded format. We will need to use the unbase64 method to decode it in our Spark application.


- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the customer-location.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-3-redis-base64-json/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-customer-location.sh
     ```

    Windows:
     ```
     submit-customer-location.cmd
     ```   
- Watch the terminal for the values to scroll past 

# Sink a Subset of JSON
There are a few customer fields we are looking for from the `redis-server` server:

* account number
* location
* birth year

We want to combine those fields into one JSON message and transmit them in a `customer-attributes` topic.


- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the customer-record.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-3-redis-base64-json/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-customer-record.sh
     ```

    Windows:
     ```
     submit-customer-record.cmd
     ```   
- Watch the terminal for the values to scroll past 