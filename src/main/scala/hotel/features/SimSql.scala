package hotel.features

import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

/**
  * Created by yu_wei on 2017/12/12.
  */
object SimSql {

   def mergeCol(row: Row, index: Int, bucketLen: Int) = {
      val merge = ArrayBuffer[Double]()
      for (i <- index until (bucketLen + index)) {
         merge.append(row(i).toString.toDouble)
      }
      merge.toList
   }

   def getDataSql(tableA: String, tableB: String, dt: String): String = {
      val sql = "select a.*," +
        " case when user_sprice_clickfreq_100 is null then 0 else user_sprice_clickfreq_100 end as user_sprice_clickfreq_100," +
        " case when user_sprice_clickfreq_150 is null then 0 else user_sprice_clickfreq_150 end as user_sprice_clickfreq_150," +
        " case when user_sprice_clickfreq_200 is null then 0 else user_sprice_clickfreq_200 end as user_sprice_clickfreq_200," +
        " case when user_sprice_clickfreq_250 is null then 0 else user_sprice_clickfreq_250 end as user_sprice_clickfreq_250," +
        " case when user_sprice_clickfreq_300 is null then 0 else user_sprice_clickfreq_300 end as user_sprice_clickfreq_300," +
        " case when user_sprice_clickfreq_350 is null then 0 else user_sprice_clickfreq_350 end as user_sprice_clickfreq_350," +
        " case when user_sprice_clickfreq_400 is null then 0 else user_sprice_clickfreq_400 end as user_sprice_clickfreq_400," +
        " case when user_sprice_clickfreq_450 is null then 0 else user_sprice_clickfreq_450 end as user_sprice_clickfreq_450," +
        " case when user_sprice_clickfreq_500 is null then 0 else user_sprice_clickfreq_500 end as user_sprice_clickfreq_500," +
        " case when user_sprice_clickfreq_550 is null then 0 else user_sprice_clickfreq_550 end as user_sprice_clickfreq_550," +
        " case when user_sprice_clickfreq_600 is null then 0 else user_sprice_clickfreq_600 end as user_sprice_clickfreq_600," +
        " case when user_sprice_clickfreq_650 is null then 0 else user_sprice_clickfreq_650 end as user_sprice_clickfreq_650," +
        " case when user_sprice_clickfreq_700 is null then 0 else user_sprice_clickfreq_700 end as user_sprice_clickfreq_700," +
        " case when user_sprice_clickfreq_750 is null then 0 else user_sprice_clickfreq_750 end as user_sprice_clickfreq_750," +
        " case when user_sprice_clickfreq_800 is null then 0 else user_sprice_clickfreq_800 end as user_sprice_clickfreq_800," +
        " case when user_sprice_clickfreq_850 is null then 0 else user_sprice_clickfreq_850 end as user_sprice_clickfreq_850," +
        " case when user_sprice_clickfreq_900 is null then 0 else user_sprice_clickfreq_900 end as user_sprice_clickfreq_900," +
        " case when user_sprice_clickfreq_950 is null then 0 else user_sprice_clickfreq_950 end as user_sprice_clickfreq_950," +
        " case when user_sprice_clickfreq_1000 is null then 0 else user_sprice_clickfreq_1000 end as user_sprice_clickfreq_1000," +
        " case when user_sprice_clickfreq_1000p is null then 0 else user_sprice_clickfreq_1000p end as user_sprice_clickfreq_1000p" +
        " from" +
        " (select clientcode," +
        " uid," +
        " vid," +
        " sid," +
        " qid," +
        " traceid," +
        " d," +
        " hotelid," +
        " case when avg_price is null or avg_price = 0 then fq_price else avg_price end as price" +
        s" from $tableA" +
        s" where d = '$dt') a" +
        " left outer join" +
        " (select *" +
        s" from $tableB" +
        s" where d = '$dt') b" +
        " on a.d = b.d and a.clientcode = b.clientcode"
      sql
   }

}
