import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.mllib.linalg.{Matrices, Matrix, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

/**
  * Created by yu_wei on 2018/1/24.
  *
  * 1. 数据离散化重组
  * http://blog.csdn.net/lixiaowang_327/article/details/51424787
  *
  */
object FeatureTransform {
   val spark = SparkSession.builder().appName("Feature Transform").master("local").getOrCreate()
   val sqlContext = spark.sqlContext

   /* DataFrame和RDD的隐式转换包 */

   import sqlContext.implicits._

   def main(args: Array[String]): Unit = {
      val rawData = spark.sparkContext.textFile("data/mllib/lr_data.txt")
      val records = rawData.map(line => line.split(" "))

      // 设置(NumericalData)DataFrame的字段名-FieldName
      val TransString = new Array[String](11)
      TransString(0) = "label"
      for (i <- 1 to 10) {
         TransString(i) = "var" + i.toString
      }
      val schemaString = TransString

      val schema = StructType(schemaString.map(fieldName => StructField(fieldName, StringType, true)))

      val newData = records.map { r => r.map(d => d.toDouble).mkString(",") }
      val rddRow = newData.map(_.split(",")).map(k => Row.fromSeq(k.toSeq)) // RDD[Row]


      val rowDataframe = sqlContext.createDataFrame(rddRow, schema)

      def FieldDiscretization(varX: String, varDis: String): DataFrame = {
         val numVar = rowDataframe.select(varX).map(k => k(0).toString.toDouble).collect()
         val dataFrameNumVar = sqlContext.createDataFrame(numVar.map(Tuple1.apply)).toDF("features")
         val sortedVar = numVar.sorted
         val splits = Array(numVar.min, sortedVar(200), sortedVar(400), sortedVar(600), sortedVar(800), numVar.max)
         val bucketizer = new Bucketizer()
           .setInputCol("features")
           .setOutputCol("bucketedFeatures")
           .setSplits(splits)
         val bucketedData = bucketizer.transform(dataFrameNumVar)
         val DisVar = bucketedData
           .select("bucketedFeatures")
           .withColumnRenamed("bucketedFeatures", varDis)
         DisVar
      }

      val label = rowDataframe.select("label")
      var zipOutCome = label

      for (k <- 1 to 10) {
         val Para1: String = "var" + k.toString
         val Para2: String = "var" + k.toString + "Dis"
         val VarDis = FieldDiscretization(Para1, Para2)
         val zipRDD = zipOutCome.rdd.zip(VarDis.rdd).map(x => Row.fromSeq(x._1.toSeq ++ x._2.toSeq))
         val zipSchame = StructType(zipOutCome.schema ++ VarDis.schema)
         zipOutCome = sqlContext.createDataFrame(zipRDD, zipSchame)
      }

      val disDataFrame = zipOutCome // 离散化之后的DataFrame：label - features

      /**
        * 普通的二维表格式转换成机器学习需要的DataFrame的Label和Feature格式
        *
        */
      def twoDimTableToDFLabelAndFeature(inputDF: DataFrame): DataFrame = {
         val inputDFLabeledPoint = inputDF.map { row =>
            val rowToArray = row.toSeq.toArray.map(x => x.toString.toDouble)
            val label = rowToArray(0)
            val featuresArray = rowToArray.drop(1)
            val features = Vectors.dense(featuresArray)
            LabeledPoint(label, features)
         }
         val outputDF = inputDFLabeledPoint.toDF("label", "features")
         outputDF
      }

      val df = twoDimTableToDFLabelAndFeature(disDataFrame)

      spark.stop()

   }


}
