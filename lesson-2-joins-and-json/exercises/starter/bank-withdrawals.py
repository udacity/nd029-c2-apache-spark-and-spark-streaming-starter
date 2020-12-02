from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, to_json, col, unbase64, base64, split, expr
from pyspark.sql.types import StructField, StructType, StringType, BooleanType, ArrayType, DateType

# TO-DO: create bank withdrawals kafka message schema StructType including the following JSON elements:
#  {"accountNumber":"703934969","amount":625.8,"dateAndTime":"Sep 29, 2020, 10:06:23 AM","transactionId":1601395583682}

# TO-DO: create an atm withdrawals kafka message schema StructType including the following JSON elements:
# {"transactionDate":"Sep 29, 2020, 10:06:23 AM","transactionId":1601395583682,"atmLocation":"Thailand"}

# TO-DO: create a spark session, with an appropriately named application name

#TO-DO: set the log level to WARN

#TO-DO: read the bank-withdrawals kafka topic as a source into a streaming dataframe with the bootstrap server kafka:19092, configuring the stream to read the earliest messages possible                                    

#TO-DO: using a select expression on the streaming dataframe, cast the key and the value columns from kafka as strings, and then select them

#TO-DO: using the kafka message StructType, deserialize the JSON from the streaming dataframe 

# TO-DO: create a temporary streaming view called "BankWithdrawals" 
# it can later be queried with spark.sql

#TO-DO: using spark.sql, select * from BankWithdrawals into a dataframe

#TO-DO: read the atm-withdrawals kafka topic as a source into a streaming dataframe with the bootstrap server kafka:19092, configuring the stream to read the earliest messages possible                                    

#TO-DO: using a select expression on the streaming dataframe, cast the key and the value columns from kafka as strings, and then select them

#TO-DO: using the kafka message StructType, deserialize the JSON from the streaming dataframe 

# TO-DO: create a temporary streaming view called "AtmWithdrawals" 
# it can later be queried with spark.sql

#TO-DO: using spark.sql, select * from AtmWithdrawals into a dataframe

#TO-DO: join the atm withdrawals dataframe with the bank withdrawals dataframe

# TO-DO: write the stream to the kafka in a topic called withdrawals-location, and configure it to run indefinitely, the console will not output anything. You will want to attach to the topic using the kafka-console-consumer inside another terminal
# TO-DO: for the "checkpointLocation" option in the writeStream, be sure to use a unique file path to avoid conflicts with other spark scripts