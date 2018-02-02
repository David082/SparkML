//import java.text.SimpleDateFormat
//import java.util.{Calendar, Date}
//
//import org.apache.spark.ml.feature.{Bucketizer, OneHotEncoder, VectorAssembler}
//import org.apache.spark.sql.{Row, SparkSession}
//
//import scala.collection.mutable.ArrayBuffer
//
///**
//  * Created by yu_wei on 2017/12/8.
//  *
//  * refer to:
//  * 1. http://spark.apache.org/docs/2.0.2/ml-features.html#onehotencoder
//  * 2. http://blog.csdn.net/sinat_31726559/article/details/52123052
//  * 3. http://blog.csdn.net/wangpei1949/article/details/53140372
//  *
//  */
//object SparkSim {
//
//   def main(args: Array[String]): Unit = {
//      // Data conf
//      // val dataSetsTable = "dw_htlbizdb.lowstar_order_user_query_condition_hotellist"
//      val dataSetsTable = "dw_htlbizdb.tmp_yw_user_hotel_similarity"
//      // val parDate = getDatetime(-5)
//      val parDate = "2017-12-06"
//      val resultTable = "dw_htlbizdb.user_hotel_similarity"
//      val userPriceFreq = (List(100 to 1000 by 50: _*).map(s => "user_sprice_clickfreq_" + s) :+ "user_sprice_clickfreq_1000p").toArray
//
//      // Spark conf
//      val warehouseLocation = "/user/hotel/warehouse"
//      val spark = SparkSession
//        .builder()
//        .appName("Spark Similarity")
//        .master("local")
//        .config("spark.sql.warehouse.dir", warehouseLocation)
//        .enableHiveSupport()
//        .getOrCreate()
//
//      // val getDataSql = s"select clientcode, hotelid, cast(fq_price as double) from $dataSetsTable where cityid = 2 and d = '$parDate'"
//      val getDataSql = s"select clientcode, uid, vid, sid, qid, traceid, d, hotelid, cast(fq_price as double)," +
//        userPriceFreq.take(userPriceFreq.length - 1).map(s => s + ",").mkString("") + userPriceFreq.last.mkString("") +
//        s" from $dataSetsTable where cityid = 2 and d = '$parDate'"
//      val data = spark.sql(getDataSql)
//      data.printSchema()
//
//      import spark.implicits._
//      // Merge bucketed features to vector
//      val merged = data.map{ row =>
//         val merge = mergeCol(row, 9, userPriceFreq.length)
//         ( row(0).toString,
//           row(1).toString,
//           row(2).toString,
//           row(3).toString.toInt,
//           row(4).toString.toInt,
//           row(5).toString,
//           row(6).toString,
//           row(7).toString.toInt,
//           row(8).toString.toDouble,
//           merge.toArray)
//      }.toDF("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "fq_price", "vector")
//
//      // Step 1: Bucketizer transform
//      // val splits = Array(Double.NegativeInfinity, 100, 150, 200, 250, 300, 350, 400, Double.PositiveInfinity)
//      val priceCut = (Double.NegativeInfinity +: List(100 to 1000 by 50: _*).map(s => s.toDouble) :+ Double.PositiveInfinity).toArray
//      val bucketizer = new Bucketizer()
//        .setInputCol("fq_price")
//        .setOutputCol("bucketedFeatures")
//        .setSplits(priceCut)
//      // Transform original data into its bucket index : bucketedFeatures
//      val bucketedData = bucketizer.transform(data)
//
//      // Step 2: One-hot encoding
//      val encoder = new OneHotEncoder()
//        .setInputCol("bucketedFeatures")
//        .setOutputCol("categoryVec")
//        .setDropLast(false) // include last category
//      val encoded = encoder.transform(bucketedData)
//
//      // Step 3: VectorAssembler is a transformer that combines a given list of columns into a single vector column.
//      val assembler = new VectorAssembler()
//        .setInputCols(userPriceFreq)
//        .setOutputCol("vectorFeatures")
//      val vectored = assembler.transform(encoded)
//
//      // Step 4: Compute Similarity
//      val simDF = vectored.select("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "fq_price", "bucketedFeatures",
//         "categoryVec", "vectorFeatures").map { row =>
//         ( row(0).toString,
//           row(1).toString,
//           row(2).toString,
//           row(3).toString.toInt,
//           row(4).toString.toInt,
//           row(5).toString,
//           row(6).toString,
//           row(7).toString.toInt,
//           row(8).toString.toDouble,
//           row(9).toString.toDouble,
//           simpleSimilarity(row(10).toString, row(11).toString))
//      }.toDF("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "fq_price", "bucketedFeatures", "similarity")
//
//      // Step 5: Join similarity to Dataframe
//      val output = merged.join(simDF, Seq("clientcode", "uid", "vid", "sid", "qid", "traceid", "d", "hotelid", "fq_price"), "inner")
//      output.printSchema()
//
//      output.createOrReplaceTempView("output")
//      val insertInto = s"insert overwrite table $resultTable PARTITION (d = '$parDate')" +
//        " select clientcode, uid, vid, sid, qid, traceid, hotelid, fq_price, bucketedFeatures, vector, similarity from output"
//      spark.sql(insertInto)
//
//      spark.stop()
//
//   }
//
//   /**
//     * Compute similarity
//     * @param categoryVec: Bucketed features
//     * @param vectorFeatures: Features to vector
//     * @return
//     */
//   def similarity(categoryVec: String, vectorFeatures: String): Double = {
//      val catV = categoryVec.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
//      val vec = vectorFeatures.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
//
//      val vecM: scala.collection.mutable.Map[Double, Double] = scala.collection.mutable.Map[Double, Double]()
//      for (l <- 0 until vec.length / 2) {
//         vecM += (vec(l) -> vec(l + vec.length / 2))
//      }
//      var similarity = 0.0
//      for (k <- vecM.keySet) {
//         if (k == catV(0)) {
//            var s = math.pow(1.0 - vecM.get(k).head, 2)
//            similarity += s
//         } else {
//            val s = math.pow(vecM.get(k).head, 2)
//            similarity += s
//         }
//      }
//      math.sqrt(similarity)
//   }
//
//   /**
//     * Compute simple similarity
//     * @param categoryVec: Bucketed features
//     * @param vectorFeatures: Features to vector
//     * @return
//     */
//   def simpleSimilarity(categoryVec: String, vectorFeatures: String): Double = {
//      val catV = categoryVec.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
//      val vec = vectorFeatures.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
//
//      val vecM: scala.collection.mutable.Map[Double, Double] = scala.collection.mutable.Map[Double, Double]()
//      for (l <- 0 until vec.length / 2) {
//         vecM += (vec(l) -> vec(l + vec.length / 2))
//      }
//      var similarity = 0.0
//      for (k <- vecM.keySet) {
//         if (k == catV(0)) {
//            similarity = vecM.get(k).head
//         } else {
//            similarity = 0.0
//         }
//      }
//      similarity
//   }
//
//   def mergeCol(row: Row, index: Int, bucketLen: Int) = {
//      val merge = ArrayBuffer[Double]()
//      for (i <- index until (bucketLen + index)) {
//         merge.append(row(i).toString.toDouble)
//      }
//      merge.toList
//   }
//
//   /**
//     * @param num : 0:today, -1:yesterday, -2:the day before yesterday, 1:tomorrow, 2:the day after tomorrow
//     * @return : datetime
//     */
//   def getDatetime(num: Int) = {
//      val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
//      if (num == 0) {
//         val now = new Date()
//         val today = dateFormat.format(now)
//         today
//      } else if (num != 0) {
//         val cal: Calendar = Calendar.getInstance()
//         cal.add(Calendar.DATE, num)
//         val datetime = dateFormat.format(cal.getTime)
//         datetime
//      }
//   }
//
//}
