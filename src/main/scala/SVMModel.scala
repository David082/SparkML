//import org.apache.spark.SparkContext
//import org.apache.spark.mllib.classification.SVMWithSGD
//import org.apache.spark.mllib.regression.LabeledPoint
//import org.apache.spark.sql.SparkSession
//
///**
//  * Created by yu_wei on 2017/9/26.
//  */
//object SVMModel {
//
//   def main(args: Array[String]): Unit = {
//      // Spark conf
//      val spark = SparkSession.builder().master("local").appName("spark knn").getOrCreate()
//      val sc = spark.sparkContext
//      import spark.implicits._
//
//      // Load and parse the data file
//      val data = sc.textFile("mllib/data/sample_svm_data.txt")
//      val parsedData = data.map { line =>
//         val parts = line.split(' ')
//         // LabeledPoint(parts(0).toDouble, parts.tail.map(x => x.toDouble).toArray)
//      }
//      parsedData.foreach(println)
//
//   }
//
//}
