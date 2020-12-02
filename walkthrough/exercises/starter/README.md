#  Spark Clusters and your Workspace
Throughout the course you will deploy spark applications on a Spark cluster. That means the cluster needs to be running! This requires some coordination between the worker and the master so work can be delegated. Once they are both up and running, you will be able to submit your application to the master.

It is recommended that you configure Docker to allow it to use up to 2 cores and 6 GB of your host memory for use by the course workspace. If you are running other processes using Docker simultaneously with the workspace, you should take that into account also.


The docker-compose file at the root of the repository creates 9 separate containers:

- Redis
- Zookeeper (for Kafka)
- Kafka
- Banking Simulation
- Trucking Simulation
- STEDI (Application used in Final Project)
- Kafka Connect with Redis Source Connector
- Spark Master
- Spark Worker

It also mounts your repository folder to the Spark Master and Spark Worker containers as a volume  `/home/workspace`, making your code changes instantly available within to the containers running Spark.

Let's get these containers started!

```
cd [repositoryfolder]
docker-compose up
```

You should see 9 containers when you run this command:
```
docker ps
```

# Create a hello world Spark application and submit it to the cluster

- Complete the hellospark.py application (be sure to click File Save when done)

- From the terminal type: 

```
docker exec -it nd029-c2-apache-spark-and-spark-streaming-starter_spark_1 /opt/bitnami/spark/bin/spark-submit /home/workspace/lesson-1-streaming-dataframes/exercises/starter/hellospark.py
```

- This command is using docker exec to target the container running the Spark Master, executing the command `spark-submit`, and passing the path to hellospark.py within the mounted filesystem. 

- Watch for the output at the end for the counts

# Create a Spark Streaming Dataframe with a Kafka source and write it to the console

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the kafkaconsole.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-kafka-console.sh
     ```

    Windows:
     ```
     submit-kafka-console.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)

# Create and query temporary spark view and write to kafka

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the gear-position.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-gear-position.sh
     ```

    Windows:
     ```
     submit-gear-position.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)

# Parse a JSON payload into separate fields for analysis

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the vehicle-status.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-vehicle-status.sh
     ```

    Windows:
     ```
     submit-vehicle-status.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)


# Join streaming dataframes from different sources

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the vehicle-checkin.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-vehicle-checkin.sh
     ```

    Windows:
     ```
     submit-vehicle-checkin.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)



# Write a Streaming Dataframe to Kafka

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Update the vehicle.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-vehicle-checkin.sh
     ```

    Windows:
     ```
     submit-vehicle-checkin.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)



# Manually Save and Read With Redis and Kafka

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


# Parse Base64 With Pyspark

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the reservation-base64.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-reservation-base64.sh
     ```

    Windows:
     ```
     submit-reservation-base64.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)

# Sink a Subset of JSON fields with Pyspark


- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the payment-json-fields.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-payment-json.sh
     ```

    Windows:
     ```
     submit-payment-json.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)

# Join Two Base64 Decoded Dataframes


- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the reservation-payment.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the walkthrough/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-reservation-payment.sh
     ```

    Windows:
     ```
     submit-reservation-payment.cmd
     ```   
- Watch the terminal for the values to scroll past (may take up to 2 minutes)