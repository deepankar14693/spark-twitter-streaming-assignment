package edu.knoldus

import java.sql.DriverManager

import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.Status

object TwitterStreaming extends App {

 val sparkObject = new SparkContextCreation
 val url = "jdbc:mysql://localhost:3306/Spark"
 val username = "root"
 val password = "root"

 val windowSize = 60

 val streamData: DStream[Status] = TwitterUtils.createStream(sparkObject.ssc, None)
 val popularHashTags = streamData.flatMap(status => status.getText.split(" ")
  .filter(_.startsWith("#")))
 sparkObject.ssc.checkpoint("_checkpoint")

 val topCounts60 = popularHashTags.map((_, 1))
  .reduceByKeyAndWindow(_ + _, Seconds(windowSize))
  .map { case (topic, count) => (count, topic) }
  .transform(_.sortByKey(false))
 streamData.print()

 /**
   * Fetching Top 3 tweets count in 10 seconds interval and 60 seconds window size
   *
   */
 topCounts60.foreachRDD {
  rdd =>
   rdd.take(3).foreach {
    case (count, hashtag) =>
     Class.forName("com.mysql.jdbc.Driver")
     val conn = DriverManager.getConnection(url, username, password)
     val statement = conn.prepareStatement("INSERT INTO Twitter (Hashtag,Count) VALUES (?,?)")
     statement.setString(1, hashtag)
     statement.setInt(2, count)
     statement.executeUpdate
     conn.close()
   }
 }

 sparkObject.ssc.start()
 sparkObject.ssc.awaitTermination()

}