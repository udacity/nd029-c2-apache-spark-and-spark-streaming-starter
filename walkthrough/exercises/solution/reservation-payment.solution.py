from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, to_json, col, unbase64, base64, split, expr
from pyspark.sql.types import StructField, StructType, StringType, BooleanType, ArrayType, DateType

# this is a manually created schema - before Spark 3.0.0, schema inference is not automatic

redisMessageSchema = StructType(
    [
        StructField("key", StringType()),
        StructField("value", StringType()),
        StructField("expiredType", StringType()),
        StructField("expiredValue",StringType()),
        StructField("existType", StringType()),
        StructField("ch", StringType()),
        StructField("incr",BooleanType()),
        StructField("zSetEntries", ArrayType( \
            StructType([
                StructField("element", StringType()),\
                StructField("score", StringType())   \
            ]))                                      \
        )

    ]
)

# this is a manually created schema - before Spark 3.0.0, schema inference is not automatic
# {"reservationId":"814840107","customerName":"Jim Harris", "truckNumber":"15867", "reservationDate":"Sep 29, 2020, 10:06:23 AM"}
reservationJSONSchema = StructType (
    [
        StructField("reservationId", StringType()),
        StructField("customerName", StringType()),
        StructField("truckNumber", StringType()),
        StructField("reservationDate", StringType()),
    ]   
)

# this is a manually created schema - before Spark 3.0.0, schema inference is not automatic
# since we are not using the date and amount in sql calculations, we are going
# to cast them as strings
# {"reservationId":"9856743232","customerName":"Frank Aristotle","amount":"946.88"}
paymentJSONSchema = StructType (
    [
        StructField("reservationId",StringType()),
        StructField("customerName",StringType()),
        StructField("amount",StringType())
    ]
)

# the source for this data pipeline is a kafka topic, defined below
spark = SparkSession.builder.appName("reservation-payment").getOrCreate()
spark.sparkContext.setLogLevel('WARN')

redisServerRawStreamingDF = spark                          \
    .readStream                                          \
    .format("kafka")                                     \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe","redis-server")                  \
    .option("startingOffsets","earliest")\
    .load()                                     

#it is necessary for Kafka Data Frame to be readable, to cast each field from a binary to a string
redisServerStreamingDF = redisServerRawStreamingDF.selectExpr("cast(key as string) key", "cast(value as string) value")

# this creates a temporary streaming view based on the streaming dataframe
# it can later be queried with spark.sql, we will cover that in the next section 
redisServerStreamingDF.withColumn("value",from_json("value",redisMessageSchema))\
        .select(col('value.*')) \
        .createOrReplaceTempView("RedisData")

# Using spark.sql we can select any valid select statement from the spark view
zSetEntriesEncodedStreamingDF=spark.sql("select key, zSetEntries[0].element as redisEvent from RedisData")

# Here we are base64 decoding the redisEvent
zSetDecodedEntriesStreamingDF1= zSetEntriesEncodedStreamingDF.withColumn("redisEvent", unbase64(zSetEntriesEncodedStreamingDF.redisEvent).cast("string"))
zSetDecodedEntriesStreamingDF2= zSetEntriesEncodedStreamingDF.withColumn("redisEvent", unbase64(zSetEntriesEncodedStreamingDF.redisEvent).cast("string"))

# Filter DF1 for only those that contain the reservationDate field (customer record)
zSetDecodedEntriesStreamingDF1.filter(col("redisEvent").contains("reservationDate"))

# Filter DF2 for only those that do not (~) contain the birthDay field (all records other than customer) we will filter out null rows later
zSetDecodedEntriesStreamingDF2.filter(~(col("redisEvent").contains("reservationDate")))


# Now we are parsing JSON from the redisEvent that contains reservation data
zSetDecodedEntriesStreamingDF1\
    .withColumn("reservation", from_json("redisEvent", reservationJSONSchema))\
    .select(col('reservation.*'))\
    .createOrReplaceTempView("Reservation")\

# Last we are parsing JSON from the redisEvent that contains payment data
zSetDecodedEntriesStreamingDF2\
    .withColumn("payment", from_json("redisEvent", paymentJSONSchema))\
    .select(col('payment.*'))\
    .createOrReplaceTempView("Payment")\

# Select only the fields you need
reservationStreamingDF = spark.sql("select reservationId, reservationDate from Reservation where reservationDate is not null")

# Let's use some more column alisases on the payment fields
paymentStreamingDF = spark.sql("select reservationId as paymentReservationId, date as paymentDate, amount as paymentAmount from Payment")

paymentStreamingDF = reservationStreamingDF.join(paymentStreamingDF, expr( """
   reservationId=paymentReservationId
"""
))

# This takes the stream and "sinks" it to the console as it is updated one message at a time:
# can you find the reservations who haven't made a payment on their reservation?

paymentStreamingDF.writeStream.outputMode("append").format("console").start().awaitTermination()
