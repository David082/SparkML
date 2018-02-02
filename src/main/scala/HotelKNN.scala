//import org.apache.spark.SparkContext
//import org.apache.spark.ml.Pipeline
//import org.apache.spark.ml.classification.KNNClassifier
//import org.apache.spark.ml.feature.{PCA, StringIndexer, VectorAssembler}
//import org.apache.spark.mllib.linalg.Vectors
//import org.apache.spark.mllib.regression.LabeledPoint
//import org.apache.spark.mllib.util.MLUtils
//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.{Row, SparkSession}
//import org.apache.spark.sql.types.DoubleType
//import org.apache.spark.storage.StorageLevel
//
//import scala.collection.mutable.ArrayBuffer
//
///**
//  * Created by yu_wei on 2017/9/26.
//  *
//  * refer:
//  * 1. loadLibSVMFile : http://blog.csdn.net/wangzfox/article/details/45787725
//  *
//  */
//object HotelKNN {
//
//   def main(args: Array[String]): Unit = {
//      val featureList = Array("emb_0", "emb_1", "emb_2", "emb_3", "emb_4", "emb_5", "emb_6", "emb_7", "emb_8", "emb_9", "emb_10", "emb_11", "emb_12", "emb_13", "emb_14", "emb_15")
//
//      // Spark conf
//      val warehouseLocation = "/user/hotel/warehouse"
//      val spark = SparkSession
//        .builder()
//        .master("local")
//        .appName("spark knn")
//        .config("spark.sql.warehouse.dir", warehouseLocation)
//        .enableHiveSupport()
//        .getOrCreate()
//      val sc = spark.sparkContext
//      import spark.implicits._
//
//      // 1. Get train sets
//      // val dataset = spark.sql("select * from dw_htlbizdb.word2vec_for_htlembedding_zg_sh where d = '2017-09-14' and hotelid not in ('hotelid', 'UNK')")
//      val dataset = spark.read.format("com.databricks.spark.csv").option("header", "true").load("file:///home/hotel/yw/spark-knn/hotel_emb.csv")
//      //.load("file:///D:/SparkML/hotel_emb.csv")
//      dataset.show()
//      /*
//      val datasetIdx = dataset.select("hotelid", featureList: _*).collect()
//      val datasetRDD = sc.parallelize((0 until (datasetIdx.length)).map(i =>
//         (datasetIdx(i)(0).toString.toDouble, Array(datasetIdx(i)(1).toString.toDouble, datasetIdx(i)(2).toString.toDouble))
//      ))
//      */
//
//      // 2. Get SVM format data sets
//      val featureNum = Array(16)
//      val idx = featureNum.flatMap(i => 0 until i)
//      val datasetSVM = dataset.select("hotelid", featureList: _*).map{ line =>
//         val data = line.toSeq.toArray.map(_.toString.toDouble)
//         val label = data(0)
//         val feature = data.drop(1)
//         // val (indices, feature) = data.drop(1).map{ case (item) => (1, item)}.unzip
//         (label, idx.toArray, feature.toArray)
//      }.rdd
//
//      // 将元组转换成MLLib转用的LabeledPoint并返回
//      val parsed = datasetSVM.map { case (label, indices, feature) =>
//         LabeledPoint(label, Vectors.sparse(featureNum(0).toInt, indices, feature))
//      }.toDF()
//      parsed.show()
//
//      // 3. convert "features" from mllib.linalg.Vector to ml.linalg.Vector
//      val mldataset = MLUtils.convertVectorColumnsToML(parsed)
//      mldataset.show()
//      // val labelTypeCasted = dataset.withColumn("tmpField", dataset("hotelid").cast(DoubleType)).drop("hotelid").withColumnRenamed("tmpField", "hotelid")
//
//      // 4. split training and testing
//      val Array(train, test) = mldataset
//        .randomSplit(Array(0.9, 0.1), seed = 1234L)
//        .map(_.cache())
//
//      // 5. Set Model
//      val pca = new PCA().setInputCol("features").setK(10).setOutputCol("pcaFeatures")
//      val knn = new KNNClassifier()
//        .setTopTreeSize(train.count().toInt / 500)
//        .setFeaturesCol("pcaFeatures")
//        .setPredictionCol("predicted")
//        .setK(1)
//
//      // 6. Fit model
//      val pipeline = new Pipeline().setStages(Array(pca, knn)).fit(train)
//
//      val pred = pipeline.transform(test)
//      // val pred = pipeline.transform(train)
//      pred.printSchema()
//      pred.show()
//      pred.head()
//      pred.first()
//
//      spark.stop()
//   }
//
//   def transformNan(item: Any) = {
//      if (item == null || item == "") 0.0
//      else item
//   }
//
//
//   def loadLibSVMFile(sc: SparkContext, path: String, numFeatures: Int, minPartitions: Int): RDD[LabeledPoint] = {
//      // 读取文件
//      val parsed = sc.textFile(path, minPartitions)
//        .map(_.trim) // 消除每行的两边空格
//        .filter(line => !(line.isEmpty || line.startsWith("#"))) // 排除空行和以#开头的行
//        .map { line =>
//         val items = line.split(' ') // 空格分隔处理过的每行，返回一个array
//         val label = items.head.toDouble // 构造常量label存储行头并转换成Double
//         val (indices, values) = items.tail.filter(_.nonEmpty).map { item =>
//            // 取数组尾部，对每一行进行下面的处理
//            val indexAndValue = item.split(':')    // 以:号分隔数据每个元素，存入indexAndValue
//            val index = indexAndValue(0).toInt - 1 // 将向量下标修改为从0开始
//            val value = indexAndValue(1).toDouble  // 将向量转换成Double
//            (index, value)
//         }.unzip
//         (label, indices.toArray, values.toArray) // 将获得的标签、向量下标、特征向量组成元祖
//      }
//
//      // 判断特征数是否已定义，如无，则利用上面得到的向量下标计算
//      val d = if (numFeatures > 0) {
//         numFeatures
//      } else {
//         parsed.persist(StorageLevel.MEMORY_ONLY)
//         parsed.map { case (label, indices, values) =>
//            indices.lastOption.getOrElse(0)
//         }.reduce(math.max) + 1
//      }
//      // 最后，将元组转换成MLLib转用的LabeledPoint并返回
//      parsed.map { case (label, indices, values) =>
//         LabeledPoint(label, Vectors.sparse(d, indices, values))
//      }
//   }
//
//}
