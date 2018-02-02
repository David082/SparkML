//import org.apache.spark.annotation.DeveloperApi
//import org.apache.spark.ml.classification.KNNClassifier
//
////import org.apache.spark.ml.classification.{KNNClassifier}
//import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
//import org.apache.spark.ml.param.{IntParam, ParamMap}
//
////import org.apache.spark.ml.tuning.{ParamGridBuilder}
//import org.apache.spark.ml.util.Identifiable
//import org.apache.spark.ml.{Pipeline, Transformer}
//import org.apache.spark.mllib.util.MLUtils
//import org.apache.spark.sql.types.StructType
//import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
//import org.apache.log4j
//import org.apache.spark.ml.feature.PCA
//
//import scala.collection.mutable
//
///**
//  * Created by yu_wei on 2017/9/20.
//  */
//object SparkKNN {
//   val logger = log4j.Logger.getLogger(getClass)
//
//   def main(args: Array[String]): Unit = {
//
//      // Spark conf
//      val spark = SparkSession.builder().master("local").appName("spark knn").getOrCreate()
//      val sc = spark.sparkContext
//      import spark.implicits._
//
//      /*
//      // 读取文件
//      val parsed = sc.textFile("file:///home/hotel/yw/spark-knn/mnist.bz2").map(_.trim).filter(line => !(line.isEmpty || line.startsWith("#"))).map { line =>
//         val items = line.split(' ')
//         val label = items.head.toDouble
//         val (indices, values) = items.tail.filter(_.nonEmpty).map { item =>
//            val indexAndValue = item.split(':')
//            val index = indexAndValue(0).toInt - 1
//            val value = indexAndValue(1).toDouble
//            (index, value)
//         }.unzip
//         (label, indices.toArray, values.toArray)
//      }
//      */
//
//
//      // read in raw label and features
//      val rawDataset = MLUtils.loadLibSVMFile(sc, "file:///home/hotel/yw/spark-knn/mnist.bz2").toDF()
//      // val rawDataset = MLUtils.loadLibSVMFile(sc, "D:/mnist.bz2").toDF()
//      rawDataset.show()
//      rawDataset.printSchema()
//      // convert "features" from mllib.linalg.Vector to ml.linalg.Vector
//      val dataset = MLUtils.convertVectorColumnsToML(rawDataset)
//
//      dataset.show()
//      dataset.printSchema()
//      dataset.head()
//
//      // split training and testing
//      val Array(train, test) = dataset.randomSplit(Array(0.7, 0.3), seed = 1234L).map(_.cache())
//
//      // create PCA matrix to reduce feature dimensions
//      val pca = new PCA().setInputCol("features").setK(50).setOutputCol("pcaFeatures")
//      val knn = new KNNClassifier().setTopTreeSize(dataset.count().toInt / 500).setFeaturesCol("pcaFeatures").setPredictionCol("predicted").setK(1)
//      val pipeline = new Pipeline().setStages(Array(pca, knn)).fit(train)
//
//      val insample = validate(pipeline.transform(train))
//      val outofsample = validate(pipeline.transform(test))
//
//      val test_pred = pipeline.transform(test)
//      test_pred.show()
//      test_pred.count()
//      val out = test_pred.selectExpr("SUM(CASE WHEN label = predicted THEN 1.0 ELSE 0.0 END) / COUNT(1)").collect()
//
//      // reference accuracy: in-sample 95% out-of-sample 94%
//      logger.info(s"In-sample: $insample, Out-of-sample: $outofsample")
//      println(s"In-sample: $insample, Out-of-sample: $outofsample")
//
//      sc.stop()
//      spark.stop()
//
//   }
//
//   private[this] def validate(results: DataFrame): Double = {
//      results
//        .selectExpr("SUM(CASE WHEN label = predicted THEN 1.0 ELSE 0.0 END) / COUNT(1)")
//        .collect()
//        .head
//        .getDecimal(0)
//        .doubleValue()
//   }
//}
