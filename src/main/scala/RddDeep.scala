//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.execution.datasources.csv.CSVFileFormat
//
///**
//  * Created by yu_wei on 2017/9/14.
//  */
//object RddDeep {
//
//   def main(args: Array[String]): Unit = {
//      // spark conf
//      val spark = SparkSession.builder().master("local").appName("rdd deep").getOrCreate()
//
//      // read csv
//      val df = spark.read.format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
//        .option("header", true)
//        .option("delimiter", ",")
//        .load("data/poi_city_minmax.csv")
//      df.show()
//
//   }
//
//}
