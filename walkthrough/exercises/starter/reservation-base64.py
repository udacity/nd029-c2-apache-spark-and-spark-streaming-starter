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

# TO-DO: create a StructType for the Reservation schema for the following fields:
# {"reservationId":"814840107","customerName":"Jim Harris", "truckNumber":"15867", "reservationDate":"Sep 29, 2020, 10:06:23 AM"}

# TO-DO: create a spark session, with an appropriately named application name

#TO-DO: set the log level to WARN

#TO-DO: read the redis-server kafka topic as a source into a streaming dataframe with the bootstrap server kafka:19092, configuring the stream to read the earliest messages possible                                    

#TO-DO: using a select expression on the streaming dataframe, cast the key and the value columns from kafka as strings, and then select them

#TO-DO: using the redisMessageSchema StructType, deserialize the JSON from the streaming dataframe 

# TO-DO: create a temporary streaming view called "RedisData" based on the streaming dataframe
# it can later be queried with spark.sql

#TO-DO: using spark.sql, select key, zSetEntries[0].element as reservation from RedisData

#TO-DO: from the dataframe use the unbase64 function to select a column called reservation with the base64 decoded JSON, and cast it to a string

#TO-DO: using the customer location StructType, deserialize the JSON from the streaming dataframe, selecting column reservation.* as a temporary view called TruckReservation 

#TO-DO: using spark.sql select * from CustomerLocation

# TO-DO: write the stream to the console, and configure it to run indefinitely, the console output will look something like this:



