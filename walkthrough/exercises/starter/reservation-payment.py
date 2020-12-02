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

# TO-DO: create a StructType for the Reservation schema for the following fields:
# {"reservationId":"814840107","customerName":"Jim Harris", "truckNumber":"15867", "reservationDate":"Sep 29, 2020, 10:06:23 AM"}

# TO-DO: create a StructType for the Payment schema for the following fields:
# {"reservationId":"9856743232","customerName":"Frank Aristotle","date":"Sep 29, 2020, 10:06:23 AM","amount":"946.88"}

# TO-DO: create a spark session, with an appropriately named application name

#TO-DO: set the log level to WARN

#TO-DO: read the redis-server kafka topic as a source into a streaming dataframe with the bootstrap server localhost:9092, configuring the stream to read the earliest messages possible                                    

#TO-DO: using a select expression on the streaming dataframe, cast the key and the value columns from kafka as strings, and then select them

#TO-DO: using the redisMessageSchema StructType, deserialize the JSON from the streaming dataframe 

# TO-DO: create a temporary streaming view called "RedisData" based on the streaming dataframe
# it can later be queried with spark.sql

#TO-DO: using spark.sql, select key, zSetEntries[0].element as redisEvent from RedisData

#TO-DO: from the dataframe use the unbase64 function to select a column called redisEvent with the base64 decoded JSON, and cast it to a string

#TO-DO: repeat this a second time, so now you have two separate dataframes that contain redisEvent data

#TO-DO: using the reservation StructType, deserialize the JSON from the first redis decoded streaming dataframe, selecting column reservation.* as a temporary view called Reservation 

#TO-DO: using the payment StructType, deserialize the JSON from the second redis decoded streaming dataframe, selecting column payment.* as a temporary view called Payment 

#TO-DO: using spark.sql select select reservationId, reservationDate from Reservation where reservationDate is not null

#TO-DO: using spark.sql select reservationId as paymentReservationId, date as paymentDate, amount as paymentAmount from Payment

#TO-DO: join the reservation and payment data using the expression: reservationId=paymentReservationId

# TO-DO: write the stream to the console, and configure it to run indefinitely
# can you find the reservations who haven't made a payment on their reservation?
