# Windows Users
It is HIGHLY recommended to install the 10 October 2020 Update: https://support.microsoft.com/en-us/windows/get-the-windows-10-october-2020-update-7d20e88c-0568-483a-37bc-c3885390d212

You will then want to install the latest version of Docker on Windows: https://docs.docker.com/docker-for-windows/install/


#  Using Docker for your Exercises

You will need to use Docker to run the exercises on your own computer. You can find Docker for your operating system here: https://docs.docker.com/get-docker/



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

# Create a Spark Streaming DataFrame with a Kafka source and write it to the console
Spark is a great stream processing engine, but without connections to outside sources of data, it would be rather pointless. So, let's introduce you to working with Kafka, one of the most popular durable message brokers.

If you don't already have the docker containers running, execute the following commands:

```
cd [repositoryfolder]
docker-compose up
```

You should see 9 containers when you run this command:
```
docker ps
```

- Complete the kafkaconsole.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-1-streaming-dataframes/exercises/starter subfolder of the repository then type: 

    Mac/Linux/Unix:
     ```
     submit-kakfaconsole.sh
     ```

    Windows:
     ```
     submit-kakfaconsole.cmd
     ```    

- Watch the terminal for the values to scroll past
        
# Query a temporary spark view

We've started a Spark cluster, and connected to Kafka. Now it's time to start crunching. Let's do some basic queries on a DataFrame. Using spark.sql we will execute a query "select * from ATMVisits" and see ATM Visit data.

- If you don't already have the docker containers running, execute the following commands:

    ```
    cd [repositoryfolder]
    docker-compose up
    ```

- You should see 9 containers when you run this command:
    ```
    docker ps
    ```
- Complete the atm-visits.py python script
- Submit the application to the spark cluster:
     - From the terminal, cd to the lesson-1-streaming-dataframes/exercises/starter subfolder of the repository, then type: 

    Mac/Linux/Unix:
     ```
     submit-atm-visits.sh
     ```

    Windows:
     ```
     submit-atm-visits.cmd
     ```   
- Watch the terminal for the values to scroll past        