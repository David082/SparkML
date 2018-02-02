package hotel.orders

import org.apache.spark.sql.{DataFrame, Row}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by yu_wei on 2017/12/20.
  */
object SparkSQL {
   val priceCut = (List(50 to 1000 by 50: _*).map(s => "price_ordercut_" + s) :+ "price_ordercut_1000p").toArray
   val starCut = Array(
      "star_ordercut_0",
      "star_ordercut_2",
      "star_ordercut_3",
      "star_ordercut_4",
      "star_ordercut_5"
   )
   val ratingCut = Array(
      "rating_ordercut_1",
      "rating_ordercut_2",
      "rating_ordercut_3",
      "rating_ordercut_40",
      "rating_ordercut_41",
      "rating_ordercut_42",
      "rating_ordercut_43",
      "rating_ordercut_44",
      "rating_ordercut_45",
      "rating_ordercut_46",
      "rating_ordercut_47",
      "rating_ordercut_48",
      "rating_ordercut_49",
      "rating_ordercut_50"
   )

   def userOrderSQL(dt: String): String = {
      val sql = "select orderid, orderdate, hotel, uid, d, cityid,  ciiquantity, ciireceivable" +
        " from dw_htldb.facthtlordersnap" +
        " where uid not in ('ctriptestat', 'djyd', '13845612110', 'test111111', '13845611999', 'wwwwww', 'test1111', 'jjyd', 'wwwwwww', 'leonwang71', 'jerrygao')" +
        s" and to_date(orderdate) <= date_sub('$dt', 1)" +
        s" and to_date(orderdate) >= date_sub('$dt', 30)" +
        " and orderstatus != 'C'" +
        " and ciiquantity > 0"
      sql
   }

   def userOrderInlandSQL(table: String) = {
      val sql = "select a.orderid," +
        " a.orderdate," +
        " htl.masterhotelid," +
        " a.hotel," +
        " a.uid," +
        " a.d," +
        " htl.cityid," +
        " htl.star," +
        " a.ciiquantity," +
        " a.ciireceivable," +
        " a.ciireceivable/a.ciiquantity as price" +
        " from" +
        s" (select * from $table) a" +
        " inner join" +
        " (select b.masterhotelid," +
        " b.hotel," +
        " b.star," +
        " c.cityid" +
        " from" +
        " (select case when masterhotelid in (0, -1) then hotel else masterhotelid end as masterhotelid," +
        " hotel," +
        " city," +
        " max(star) as star" +
        " from dim_htldb.dimhtlhotel" +
        " group by case when masterhotelid in (0, -1) then hotel else masterhotelid end, hotel, city) b" +
        " inner join" +
        " (select cityid, country" +
        " from dim_hoteldb.dimcity" +
        " where country = 1 and cityid <> 22249 and currentflag = 'T') c" +
        " on b.city = c.cityid) htl" +
        " on a.hotel = htl.hotel"
      sql
   }

   def orderInlandSQL(table: String, dt: String) = {
      val sql = "select a.*," +
        " round(cast(b.rating as float), 1) as rating" +
        s" from $table a" +
        " left outer join" +
        " (select d, masterhotelid, max(wilsonrating) as rating" +
        " from dw_htlbizdb.masterhotel_wilsonrating" +
        s" where d <= date_sub('$dt', 1)" +
        s" and d >= date_sub('$dt', 30)" +
        " group by d, masterhotelid) b" +
        " on a.d = b.d and a.masterhotelid = b.masterhotelid"
      sql
   }

   def priceCutSQL(table: String, id: String, method: String) = {
      val sql = s"select $id," +
        " sum(case when price <= 50 then 1 else 0 end) as price_ordercut_50," +
        " sum(case when price > 50  and price <= 100 then 1 else 0 end) as price_ordercut_100," +
        " sum(case when price > 100 and price <= 150 then 1 else 0 end) as price_ordercut_150," +
        " sum(case when price > 150 and price <= 200 then 1 else 0 end) as price_ordercut_200," +
        " sum(case when price > 200 and price <= 250 then 1 else 0 end) as price_ordercut_250," +
        " sum(case when price > 250 and price <= 300 then 1 else 0 end) as price_ordercut_300," +
        " sum(case when price > 300 and price <= 350 then 1 else 0 end) as price_ordercut_350," +
        " sum(case when price > 350 and price <= 400 then 1 else 0 end) as price_ordercut_400," +
        " sum(case when price > 400 and price <= 450 then 1 else 0 end) as price_ordercut_450," +
        " sum(case when price > 450 and price <= 500 then 1 else 0 end) as price_ordercut_500," +
        " sum(case when price > 500 and price <= 550 then 1 else 0 end) as price_ordercut_550," +
        " sum(case when price > 550 and price <= 600 then 1 else 0 end) as price_ordercut_600," +
        " sum(case when price > 600 and price <= 650 then 1 else 0 end) as price_ordercut_650," +
        " sum(case when price > 650 and price <= 700 then 1 else 0 end) as price_ordercut_700," +
        " sum(case when price > 700 and price <= 750 then 1 else 0 end) as price_ordercut_750," +
        " sum(case when price > 750 and price <= 800 then 1 else 0 end) as price_ordercut_800," +
        " sum(case when price > 800 and price <= 850 then 1 else 0 end) as price_ordercut_850," +
        " sum(case when price > 850 and price <= 900 then 1 else 0 end) as price_ordercut_900," +
        " sum(case when price > 900 and price <= 950 then 1 else 0 end) as price_ordercut_950," +
        " sum(case when price > 950 and price <= 1000 then 1 else 0 end) as price_ordercut_1000," +
        " sum(case when price > 1000 then 1 else 0 end) as price_ordercut_1000p" +
        s" from $table" +
        s" group by $id"
      if (method == "count") sql
      else if (method == "freq") sql.replace("end)", "end)/count(*)")
   }

   def starCutSQL(table: String, id: String, method: String) = {
      val sql = s"select $id," +
        " sum(case when star = 0 then 1 else 0 end) as star_ordercut_0," +
        " sum(case when (star = 1 or star = 2) then 1 else 0 end) as star_ordercut_2," +
        " sum(case when star = 3 then 1 else 0 end) as star_ordercut_3," +
        " sum(case when star = 4 then 1 else 0 end) as star_ordercut_4," +
        " sum(case when star = 5 then 1 else 0 end) as star_ordercut_5" +
        s" from $table" +
        s" group by $id"
      if (method == "count") sql
      else if (method == "freq") sql.replace("end)", "end)/count(*)")
   }

   def ratingCutSQL(table: String, id: String, method: String) = {
      val sql = s"select $id," +
        " sum(case when rating <= 1.0 then 1 else 0 end) as rating_ordercut_1," +
        " sum(case when rating > 1.0 and rating <= 2.0 then 1 else 0 end) as rating_ordercut_2," +
        " sum(case when rating > 2.0 and rating <= 3.0 then 1 else 0 end) as rating_ordercut_3," +
        " sum(case when rating > 3.0 and rating <= 4.0 then 1 else 0 end) as rating_ordercut_40," +
        " sum(case when rating > 4.0 and rating <= 4.1 then 1 else 0 end) as rating_ordercut_41," +
        " sum(case when rating > 4.1 and rating <= 4.2 then 1 else 0 end) as rating_ordercut_42," +
        " sum(case when rating > 4.2 and rating <= 4.3 then 1 else 0 end) as rating_ordercut_43," +
        " sum(case when rating > 4.3 and rating <= 4.4 then 1 else 0 end) as rating_ordercut_44," +
        " sum(case when rating > 4.4 and rating <= 4.5 then 1 else 0 end) as rating_ordercut_45," +
        " sum(case when rating > 4.5 and rating <= 4.6 then 1 else 0 end) as rating_ordercut_46," +
        " sum(case when rating > 4.6 and rating <= 4.7 then 1 else 0 end) as rating_ordercut_47," +
        " sum(case when rating > 4.7 and rating <= 4.8 then 1 else 0 end) as rating_ordercut_48," +
        " sum(case when rating > 4.8 and rating <= 4.9 then 1 else 0 end) as rating_ordercut_49," +
        " sum(case when rating > 4.9 and rating <= 5.0 then 1 else 0 end) as rating_ordercut_50" +
        s" from $table" +
        s" group by $id"
      if (method == "count") sql
      else if (method == "freq") sql.replace("end)", "end)/count(*)")
   }

   def mergeIntCol(row: Row, index: Int, bucketLen: Int) = {
      val merge = ArrayBuffer[Int]()
      for (i <- index until (bucketLen + index)) {
         merge.append(row(i).toString.toInt)
      }
      merge.toList
   }

   def mergeDoubleCol(row: Row, index: Int, bucketLen: Int) = {
      val merge = ArrayBuffer[Double]()
      for (i <- index until (bucketLen + index)) {
         merge.append(row(i).toString.toDouble)
      }
      merge.toList
   }

}
