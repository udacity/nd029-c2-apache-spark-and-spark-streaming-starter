from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, to_json, col, unbase64, base64, split, expr
from pyspark.sql.types import StructField, StructType, StringType, BooleanType, ArrayType, DateType

# Schema for the kafka connect redis source: 
# {"key":"dGVzdGtleQ==","existType":"NONE","ch":false,"incr":false,"zSetEntries":[{"element":"dGVzdHZhbHVl","score":0.0}],"zsetEntries":[{"element":"dGVzdHZhbHVl","score":0.0}]}

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
# "{"reservationId":"1603561552180",
# "customerId":"470948385",
# "customerName":"Chuck Jones",
# "truckNumber":"2416",
# "reservationDate":"2020-10-24T17:45:52.180Z",
# "checkInStatus":"CheckedOut",
# "origin":"Arizona",
# "destination":"Michigan"}
reservationSchema = StructType (
    [
        StructField("reservationId", StringType()),
        StructField("customerId", StringType()),
        StructField("customerName", StringType()),
        StructField("truckNumber", StringType()),
        StructField("reservationDate", StringType()),
        StructField("checkInStatus", StringType()),
        StructField("origin", StringType()),
        StructField("destination", StringType()),

    ]   
)


# the source for this data pipeline is a kafka topic, defined below
spark = SparkSession.builder.appName("truck-reservation").getOrCreate()
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
zSetEntriesEncodedStreamingDF=spark.sql("select key, zSetEntries[0].element as reservation from RedisData")

zSetDecodedEntriesStreamingDF= zSetEntriesEncodedStreamingDF.withColumn("reservation", unbase64(zSetEntriesEncodedStreamingDF.reservation).cast("string"))

zSetDecodedEntriesStreamingDF\
    .withColumn("reservation", from_json("reservation", reservationSchema))\
    .select(col('reservation.*'))\
    .createOrReplaceTempView("TruckReservation")\

truckReservationStreamingDF = spark.sql("select * from TruckReservation where reservationDate is not null")

# this takes the stream and "sinks" it to the console as it is updated one message at a time (null means the JSON parsing didn't match the fields in the schema):

truckReservationStreamingDF.writeStream.outputMode("append").format("console").start().awaitTermination()

