from pyspark.sql import SparkSession

# the source for this data pipeline is a kafka topic, defined below
spark = SparkSession.builder.appName("balance-events").getOrCreate()
spark.sparkContext.setLogLevel('WARN')

gearPositionRawStreamingDF = spark                          \
    .readStream                                          \
    .format("kafka")                                     \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe","gear-position")                  \
    .option("startingOffsets","earliest")\
    .load()                                     

#it is necessary for Kafka Data Frame to be readable, to cast each field from a binary to a string
gearPositionStreamingDF = gearPositionRawStreamingDF.selectExpr("cast(key as string) truckId", "cast(value as string) gearPosition")

# this creates a temporary streaming view based on the streaming dataframe
# it can later be queried with spark.sql, we will cover that in the next section 
gearPositionStreamingDF.createOrReplaceTempView("GearPosition")


# this selects the data from the temporary view
gearPositionSelectStarDF = spark.sql("select * from GearPosition")

# this takes the stream and "sinks" it to kafka as it is updated one message at a time:
gearPositionSelectStarDF.selectExpr("cast(truckId as string) as key", "cast(gearPosition as string) as value") \
    .writeStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092")\
    .option("topic", "gear-position-updates")\
    .option("checkpointLocation","/tmp/kafkacheckpoint")\
    .start()\
    .awaitTermination()
