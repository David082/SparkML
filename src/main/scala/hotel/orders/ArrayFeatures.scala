package hotel.orders

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Created by yu_wei on 2017/12/20.
  */
object ArrayFeatures {
   val tmpTable: String = "orderInland"

   def main(args: Array[String]): Unit = {
      val dt = SetDate.getDatetime(0).toString
      val cityTable = "dw_htlbizdb.city_order_featuresArr"
      val hotelTable = "dw_htlbizdb.hotel_order_featuresArr"

      val spark = SparkSession
        .builder()
        .appName("Spark Array Features")
        .master("yarn")
        .config("spark.sql.warehouse.dir", "/user/hotel/warehouse")
        .enableHiveSupport()
        .getOrCreate()

      val userOrder = spark.sql(SparkSQL.userOrderSQL(dt))
      userOrder.createOrReplaceTempView("userOrder")
      val userOrderInland = spark.sql(SparkSQL.userOrderInlandSQL("userOrder"))
      userOrderInland.createOrReplaceTempView("userOrderInland")
      val orderInland = spark.sql(SparkSQL.orderInlandSQL("userOrderInland", dt))
      orderInland.createOrReplaceTempView(tmpTable)

      userOrder.unpersist(true)
      userOrderInland.unpersist(true)

      // Merge multi col to array
      /* city */
      val cityPriceArr = getFeatureArr(spark, "cityid", "price")
      val cityStarArr = getFeatureArr(spark, "cityid", "star")
      val cityRatingArr = getFeatureArr(spark, "cityid", "rating")
      /* hotel */
      val hotelPriceArr = getFeatureArr(spark, "masterhotelid", "price")
      val hotelStarArr = getFeatureArr(spark, "masterhotelid", "star")
      val hotelRatingArr = getFeatureArr(spark, "masterhotelid", "rating")

      // Result
      val cityArr = featuresArr(cityPriceArr, cityStarArr, cityRatingArr, "cityid")
      val hotelArr = featuresArr(hotelPriceArr, hotelStarArr, hotelRatingArr, "masterhotelid")
      spark.sql(insertInto(cityArr, cityTable, dt))
      spark.sql(insertInto(hotelArr, hotelTable, dt))

      orderInland.unpersist(true)
      spark.stop()

   }

   def insertInto(tmptable: DataFrame, onlineTable: String, dt: String) = {
      tmptable.createOrReplaceTempView("tmptable")
      val sql = s"insert OVERWRITE table $onlineTable PARTITION (d = '$dt')" +
        " select * from tmptable"
      sql
   }

   def getFeatureArr(spark: SparkSession, id: String, col: String) = {
      val countArr = colsToIntArr(spark, tmpTable, id, col, "count")
      val freqArr = colsToDoubleArr(spark, tmpTable, id, col, "freq")
      val arr = joinArr(countArr, freqArr, id)
      arr
   }

   def featuresArr(tableA: DataFrame, tableB: DataFrame, tableC: DataFrame, id: String) = {
      val arr1 = joinArr(tableA, tableB, id)
      val arr2 = joinArr(arr1, tableC, id)
      arr2
   }

   def colsToIntArr(spark: SparkSession, table: String, id: String, col: String, method: String) = {
      import spark.implicits._
      var sqlString = ""
      var colLen = 0
      if (col == "price") {
         sqlString = SparkSQL.priceCutSQL(table, id, method).toString
         colLen = SparkSQL.priceCut.length
      } else if (col == "star") {
         sqlString = SparkSQL.starCutSQL(table, id, method).toString
         colLen = SparkSQL.starCut.length
      } else if (col == "rating") {
         sqlString = SparkSQL.ratingCutSQL(table, id, method).toString
         colLen = SparkSQL.ratingCut.length
      }

      val dfArr = spark.sql(sqlString).map { row =>
         val merge = SparkSQL.mergeIntCol(row, 1, colLen)
         (row(0).toString.toInt,
           merge.toArray)
      }.toDF(id, col + method + "Arr")
      dfArr
   }

   def colsToDoubleArr(spark: SparkSession, table: String, id: String, col: String, method: String) = {
      import spark.implicits._
      var sqlString = ""
      var colLen = 0
      if (col == "price") {
         sqlString = SparkSQL.priceCutSQL(table, id, method).toString
         colLen = SparkSQL.priceCut.length
      } else if (col == "star") {
         sqlString = SparkSQL.starCutSQL(table, id, method).toString
         colLen = SparkSQL.starCut.length
      } else if (col == "rating") {
         sqlString = SparkSQL.ratingCutSQL(table, id, method).toString
         colLen = SparkSQL.ratingCut.length
      }

      val dfArr = spark.sql(sqlString).map { row =>
         val merge = SparkSQL.mergeDoubleCol(row, 1, colLen)
         (row(0).toString.toInt,
           merge.toArray)
      }.toDF(id, col + method + "Arr")
      dfArr
   }

   def joinArr(tableA: DataFrame, tableB: DataFrame, id: String) = {
      tableA.join(tableB, Seq(id), "inner")
   }

}
