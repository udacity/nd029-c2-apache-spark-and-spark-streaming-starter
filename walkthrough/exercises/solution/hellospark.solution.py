from pyspark.sql import SparkSession

logFile = "/home/workspace/Test.txt"  # Should be some file on your system
spark = SparkSession.builder.appName("HelloSpark").getOrCreate()
spark.sparkContext.setLogLevel('WARN')
logData = spark.read.text(logFile).cache()

numDs = logData.filter(logData.value.contains('d')).count()
numSs = logData.filter(logData.value.contains('s')).count()

print("*******")
print("*******")
print("*****Lines with d: %i, lines with s: %i" % (numDs, numSs))
print("*******")
print("*******")

spark.stop()