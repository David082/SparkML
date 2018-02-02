package hotel.features

import org.apache.spark.ml.feature.{Bucketizer, OneHotEncoder, VectorAssembler}
import org.apache.spark.sql.SparkSession

/**
  * Created by yu_wei on 2017/12/8.
  *
  * refer to:
  * 1. http://spark.apache.org/docs/2.0.2/ml-features.html#onehotencoder
  * 2. http://blog.csdn.net/sinat_31726559/article/details/52123052
  * 3. http://blog.csdn.net/wangpei1949/article/details/53140372
  *
  */
object SparkSim {

   def main(args: Array[String]): Unit = {
      // Data conf
      val tableA = "dw_htlbizdb.lowstar_order_user_query_condition_hotellist"
      val tableB = "dw_htlbizdb.user_spricecut_clickfreq"
      val parDate = SetDate.getDatetime(-2).toString

      val resultTable = "dw_htlbizdb.user_hotel_similarity"
      val userPriceFreq = (List(100 to 1000 by 50: _*).map(s => "user_sprice_clickfreq_" + s) :+ "user_sprice_clickfreq_1000p").toArray

      // Spark conf
      val warehouseLocation = "/user/hotel/warehouse"
      val spark = SparkSession
        .builder()
        .appName("Spark Similarity")
        .master("yarn")
        .config("spark.sql.warehouse.dir", warehouseLocation)
        .enableHiveSupport()
        .getOrCreate()

      val tmpSql = SimSql.getDataSql(tableA, tableB, parDate)
      val tmpData = spark.sql(tmpSql)
      tmpData.createOrReplaceTempView("tmpData")
      val getDataSql = s"select clientcode, uid, vid, sid, qid, traceid, d, hotelid, cast(price as double)," +
        userPriceFreq.take(userPriceFreq.length - 1).map(s => s + ",").mkString("") + userPriceFreq.last.mkString("") +
        s" from tmpData where d = '$parDate'"
      val data = spark.sql(getDataSql)
      data.printSchema()

      import spark.implicits._
      // Merge bucketed features to vector
      val merged = data.map{ row =>
         val merge = SimSql.mergeCol(row, 9, userPriceFreq.length)
         ( row(0).toString,
           row(1).toString,
           row(2).toString,
           row(3).toString.toInt,
           row(4).toString.toInt,
           row(5).toString,
           row(6).toString,
           row(7).toString.toInt,
           row(8).toString.toDouble,
           merge.toArray)
      }.toDF("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "price", "vector")

      // Step 1: Bucketizer: Transform original data into its bucket index
      val priceCut = (Double.NegativeInfinity +: List(100 to 1000 by 50: _*).map(s => s.toDouble) :+ Double.PositiveInfinity).toArray
      val bucketizer = new Bucketizer()
        .setInputCol("price")
        .setOutputCol("bucketedFeatures")
        .setSplits(priceCut)
      val bucketedData = bucketizer.transform(data)

      // Step 2: One-hot encoding
      val encoder = new OneHotEncoder()
        .setInputCol("bucketedFeatures")
        .setOutputCol("categoryVec")
        .setDropLast(false) // include last category
      val encoded = encoder.transform(bucketedData)

      // Step 3: VectorAssembler: combines a given list of columns into a single vector column.
      val assembler = new VectorAssembler()
        .setInputCols(userPriceFreq)
        .setOutputCol("vectorFeatures")
      val vectored = assembler.transform(encoded)

      // Step 4: Compute Similarity
      val simDF = vectored.select("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "price", "bucketedFeatures",
         "categoryVec", "vectorFeatures").map { row =>
         ( row(0).toString,
           row(1).toString,
           row(2).toString,
           row(3).toString.toInt,
           row(4).toString.toInt,
           row(5).toString,
           row(6).toString,
           row(7).toString.toInt,
           row(8).toString.toDouble,
           row(9).toString.toDouble,
           Similarity.simpleSimilarity(row(10).toString, row(11).toString))
      }.toDF("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "price", "bucketedFeatures", "similarity")

      // Step 5: Join similarity to Dataframe
      val output = merged.join(simDF, Seq("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "price"), "inner")
      output.printSchema()

      output.createOrReplaceTempView("output")
      val insertInto = s"insert overwrite table $resultTable PARTITION (d = '$parDate')" +
        " select clientcode, uid, vid, sid, qid, traceid, hotelid, price, bucketedFeatures, vector, similarity from output"
      spark.sql(insertInto)

      spark.stop()

   }

}
