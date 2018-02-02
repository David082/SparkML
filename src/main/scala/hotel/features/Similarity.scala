package hotel.features

/**
  * Created by yu_wei on 2017/12/12.
  */
object Similarity {

   /**
     * Compute similarity
     * @param categoryVec: Bucketed features
     * @param vectorFeatures: Features to vector
     * @return
     */
   def similarity(categoryVec: String, vectorFeatures: String): Double = {
      val catV = categoryVec.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
      val vec = vectorFeatures.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)

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
      math.sqrt(similarity)
   }

   /**
     * Compute simple similarity
     *
     * @param categoryVec: Bucketed features
     * @param vectorFeatures: Features to vector
     * @return
     */
   def simpleSimilarity(categoryVec: String, vectorFeatures: String): Double = {
      val catV = categoryVec.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)
      val vec = vectorFeatures.replace("(", "").replace(")", "").replace("[", "").replace("]", "").split(",").drop(1).map(_.toDouble)

      val vecM: scala.collection.mutable.Map[Double, Double] = scala.collection.mutable.Map[Double, Double]()
      for (l <- 0 until vec.length / 2) {
         vecM += (vec(l) -> vec(l + vec.length / 2))
      }
      var similarity = 0.0
      for (k <- vecM.keySet) {
         if (k == catV(0)) {
            similarity = vecM.get(k).head
         } else {
            similarity = 0.0
         }
      }
      similarity
   }

}
