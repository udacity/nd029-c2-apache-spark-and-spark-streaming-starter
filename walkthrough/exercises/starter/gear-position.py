from pyspark.sql import SparkSession

# TO-DO: create a spark session, with an appropriately named application name

#TO-DO: set the log level to WARN

#TO-DO: read the gear-position kafka topic as a source into a streaming dataframe with the bootstrap server kafka:19092, configuring the stream to read the earliest messages possible                                    

#TO-DO: using a select expression on the streaming dataframe, cast the key and the value columns from kafka as strings, and then select them

# TO-DO: create a temporary streaming view called "GearPosition" based on the streaming dataframe

# TO-DO: query the temporary view "GearPosition" using spark.sql 

# Write the dataframe from the last query to a kafka broker at kafka:19092, with a topic called gear-position-updates
