package edu.knoldus

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

class SparkContextCreation {
 val conf = new SparkConf().setMaster("local[*]")
  .setAppName("Spark Streaming - PopularHashTags")
 val sc = new SparkContext(conf)

 sc.setLogLevel("WARN")
 val interval = 10
 val ssc = new StreamingContext(sc, Seconds(interval))
}
