//import org.apache.spark.ml.classification.LogisticRegression
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.sql.{Row, SQLContext, SparkSession}
//
///**
//  * Created by yu_wei on 2017/9/8.
//  */
//
//object LRModel {
//
//   def main(args: Array[String]): Unit = {
//      // Spark conf
//      val conf = new SparkConf().setMaster("local").setAppName("test")
//      val sc = new SparkContext(conf)
//      val sqlContext = new SQLContext(sc)
//
//      // val warehouseLocation = "/user/hotel/warehouse"
//      val spark = SparkSession.builder()
//        .master("local")
//        .appName("LR")
//        .getOrCreate()
//
//      // Load training data
//      val training = spark.read.format("libsvm").load("D:/sample_libsvm_data.txt")
//      println(training)
//
//      val lr = new LogisticRegression()
//        .setMaxIter(10)
//        .setRegParam(0.3)
//        .setElasticNetParam(0.8)
//
//      // Fit the model
//      val lRModel = lr.fit(training)
//
//      // Print the coefficients and intercept for logistic regression
//      println(s"Coefficients: ${lRModel.coefficients} Intercept: ${lRModel.intercept}")
//
//
//      /*
//      // read csv
//      val data =sqlContext.read.format("com.databricks.spark.csv")
//        .option("header", "true")
//        .load("D:/train2.csv")
//      println(data)
//      data.show()
//      */
//
//      sc.stop()
//
//   }
//
//}
