from pyspark.sql import SparkSession

# the source for this data pipeline is a kafka topic, defined below
spark = SparkSession.builder.appName("fuel-level").getOrCreate()
spark.sparkContext.setLogLevel('WARN')

kafkaRawStreamingDF = spark                          \
    .readStream                                          \
    .format("kafka")                                     \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe","fuel-level")                  \
    .option("startingOffsets","earliest")\
    .load()                                     

#this is necessary for Kafka Data Frame to be readable, into a single column  value
kafkaStreamingDF = kafkaRawStreamingDF.selectExpr("cast(key as string) key", "cast(value as string) value")

# this takes the stream and "sinks" it to the console as it is updated one at a time like this:
# +--------------------+-----+
# |                 Key|Value|
# +--------------------+-----+
# |1593939359          |13...|
# +--------------------+-----+
kafkaStreamingDF.writeStream.outputMode("append").format("console").start().awaitTermination()