/**
  * Created by yu_wei on 2017/12/12.
  */
class CodeBackup {

   def codeBackUp(): Unit = {
      /*
      val indexer = new StringIndexer()
        .setInputCol("bucketedFeatures")
        .setOutputCol("categoryIndex")
        .fit(bucketedData)
      val indexed = indexer.transform(bucketedData)
      */

      /*
      val rdd1 = spark.sparkContext.parallelize(Array(
         Array(1.0, 2.0, 3.0, 4.0),
         Array(2.0, 3.0, 4.0, 5.0),
         Array(3.0, 4.0, 5.0, 6.0)
      )).map(f => Vectors.dense(f))
      val RM = new RowMatrix(rdd1)
      val simic1 = RM.columnSimilarities(0.5)
      println(RM.columnSimilarities().entries.collect().foreach(x => println(x)))
      println(RM.columnSimilarities(0.5).entries.collect().foreach(x => println(x)))
      */

      /*
      // Filter
      val fiterData = viewData.where("bucketedFeatures = 19")
      fiterData.show()
      */

      /*
      val first = viewData.first().toSeq.toArray
      // catV
      val catV = first(4).toString.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
      // vecM
      val vec = first(5).toString.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
      val vecM: scala.collection.mutable.Map[Double, Double] = scala.collection.mutable.Map[Double, Double]()
      for (l <- 0 until vec.length / 2) {
         vecM += (vec(l) -> vec(l + vec.length / 2))
      }
      vecM.get(catV(0)).head
      var similarity = 0.0
      for (k <- vecM.keySet) {
         if (k == catV(0)) {
            var s = math.pow(1.0 - vecM.get(k).head, 2)
            similarity += s
         } else {
            val s = math.pow(vecM.get(k).head, 2)
            similarity += s
         }
      }
      */

      /*
      val catV = row(0).toString.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
         val vec = row(1).toString.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)

         val vecM: scala.collection.mutable.Map[Double, Double] = scala.collection.mutable.Map[Double, Double]()
         for (l <- 0 until vec.length / 2) {
            vecM += (vec(l) -> vec(l + vec.length / 2))
         }
         var similarity = 0.0
         for (k <- vecM.keySet) {
            if (k == catV(0)) {
               var s = math.pow(1.0 - vecM.get(k).head, 2)
               similarity += s
            } else {
               val s = math.pow(vecM.get(k).head, 2)
               similarity += s
            }
         }
         similarity
       */
   }

}
