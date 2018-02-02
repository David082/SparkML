import ml.dmlc.xgboost4j.scala.spark.XGBoost
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * Created by yu_wei on 2018/1/31.
  */
object XGBModel {
   val LABEL = "ordergp"
   val FEATURE_LIST = List("cityid", "starlicence", "star", "goldstar", "ratingoverall", "novoters", "customereval", "country", "province", "zone", "upper_1cycle_gp", "upper_2cycle_gp", "upper_3cycle_gp", "upper_4cycle_gp", "his_28days_gp", "his_3months_gp", "his_6months_gp", "his_9months_gp", "his_12months_gp", "year", "month", "weekofyear", "quarter", "weekdays", "holidays", "gp_holiday_hotel_heat", "gp_holiday_zone_heat", "gp_holiday_city_heat", "hotel_div_zone_heat_gp", "zone_div_city_heat_gp", "upper_1cycle_uv", "upper_2cycle_uv", "upper_3cycle_uv", "upper_4cycle_uv", "his_28days_uv", "his_3months_uv", "his_6months_uv", "hotel_pic_score", "hq_hotel_pic_score", "room_pic_score", "static_info_score")

   def main(args: Array[String]): Unit = {

      // Spark conf
      val spark = SparkSession
        .builder()
        .appName("scala xgboost")
        .master("yarn")
        .enableHiveSupport()
        .getOrCreate()

      val train = spark.sql("select * from dw_htlbizdb.tmp_ym_hotelrankings_train_set where orderdate = '2017-09-18' and ordergp > 0")

      import spark.implicits._
      val trainDF = train.select(LABEL, FEATURE_LIST: _*).map { row =>
         val rowToArray = row.toSeq.toArray.map(_.toString.toDouble)
         val label = rowToArray(0)
         val featuresArray = rowToArray.drop(1)
         val features = Vectors.dense(featuresArray)
         (label, features)
      }.toDF("label", "features")

      // Fitting model
      val numRound = 100
      val paramMap = List(
         "eta" -> 0.05f,
         "max_depth" -> 5,
         "eval_metric" -> "rmse",
         "silent" -> 0, // 0-print, 1-no print
         "objective" -> "reg:linear"
         // "objective" -> "binary:logistic"
      ).toMap

      val model = XGBoost.trainWithDataFrame(trainDF,
         paramMap, numRound, nWorkers = 35, obj = null, eval = null, useExternalMemory = false, Float.NaN,
         "features", "label")
      model.saveModelAsHadoopFile("hdfs:///home/hotel/yw/hotelrank/sparkxgb.model")(spark.sparkContext)

      spark.stop()

   }

}
