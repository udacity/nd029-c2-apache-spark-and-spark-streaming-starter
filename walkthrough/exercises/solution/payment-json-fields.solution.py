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
# since we are not using the date and amount in sql calculations, we are going
# to cast them as strings
# {"reservationId":"9856743232","customerName":"Frank Aristotle","date":"Sep 29, 2020, 10:06:23 AM","amount":"946.88"}
paymentJSONSchema = StructType (
    [
        StructField("reservationId",StringType()),
        StructField("customerName",StringType()),
        StructField("date",StringType()),
        StructField("amount",StringType())
    ]
)


# the source for this data pipeline is a kafka topic, defined below
spark = SparkSession.builder.appName("payment-json").getOrCreate()
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
zSetEntriesEncodedStreamingDF=spark.sql("select key, zSetEntries[0].element as payment from RedisData")

zSetDecodedEntriesStreamingDF= zSetEntriesEncodedStreamingDF.withColumn("payment", unbase64(zSetEntriesEncodedStreamingDF.payment).cast("string"))

zSetDecodedEntriesStreamingDF\
    .withColumn("payment", from_json("payment", paymentJSONSchema))\
    .select(col('payment.*'))\
    .createOrReplaceTempView("Payment")\

paymentStreamingDF = spark.sql("select reservationId, amount customerName, from Payment")

paymentFieldsStreamingDF = paymentStreamingDF.select("reservationId","amount",split(paymentFieldsStreamingDF.customerName," ").getItem(1).alias("lastName"))
# this takes the stream and "sinks" it to the console as it is updated one message at a time (null means the JSON parsing didn't match the fields in the schema):
# {"reservationId":"9856743232","amount":"946.88"}

paymentFieldsStreamingDF.selectExpr("CAST(reservationId AS STRING) AS key", "to_json(struct(*)) AS value")\
    .writeStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092")\
    .option("topic", "payment-json")\
    .option("checkpointLocation","/tmp/kafkacheckpoint")\
    .start()\
    .awaitTermination()
