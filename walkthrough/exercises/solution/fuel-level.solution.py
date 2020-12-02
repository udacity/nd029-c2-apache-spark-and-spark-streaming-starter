from pyspark.sql import SparkSession

# the source for this data pipeline is a kafka topic, defined below
spark = SparkSession.builder.appName("fuel-level").getOrCreate()
spark.sparkContext.setLogLevel('WARN')

fuelLevelRawStreamingDF = spark                          \
    .readStream                                          \
    .format("kafka")                                     \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe","fuel-level")                  \
    .option("startingOffsets","earliest")\
    .load()                                     

#it is necessary for Kafka Data Frame to be readable, to cast each field from a binary to a string
fuelLevelStreamingDF = fuelLevelRawStreamingDF.selectExpr("cast(key as string) key", "cast(value as string) value")

# this creates a temporary streaming view based on the streaming dataframe
# it can later be queried with spark.sql, we will cover that in the next section 
fuelLevelStreamingDF.createOrReplaceTempView("FuelLevel")

# Using spark.sql we can select any valid select statement from the spark view
fuelLevelSelectStarDF=spark.sql("select * from FuelLevel")

# this takes the stream and "sinks" it to the console as it is updated one message at a time:
# +----+-----+
# | key|value|
# +----+-----+
# |8450|    2|
# |3282|   30|
# | 199|    9|
# |3459|   27|
# +----+-----+

fuelLevelSelectStarDF.writeStream.outputMode("append").format("console").start().awaitTermination()

