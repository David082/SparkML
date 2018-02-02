//package tensorflow.spark
//
//import java.nio.file.Paths
//
//import org.platanios.tensorflow.api._
//import org.platanios.tensorflow.api.ops.training.optimizers.GradientDescent
//import org.platanios.tensorflow.api.tf.learn._
//import org.platanios.tensorflow.data.image.{MNISTDataset, MNISTLoader}
//
//
///**
//  * Created by yu_wei on 2018/1/26.
//  *
//  * refer:
//  * https://stackoverflow.com/questions/22489398/unsupported-major-minor-version-52-0
//  *
//  * 利用IDEA查看和修改spark源码
//  * http://blog.csdn.net/fishseeker/article/details/63741265
//  *
//  * example:
//  * https://github.com/DL-DeepLearning/tensorflow_scala/blob/master/examples/src/main/scala/org/platanios/tensorflow/examples/MNIST.scala
//  *
//  */
//object TensorflowSparkExample {
//
//   def main(args: Array[String]): Unit = {
//
//      val dataSet = MNISTLoader.load(Paths.get("datasets/MNIST"))
//      val trainImages = tf.data.TensorSlicesDataset(dataSet.trainImages)
//      val trainLabels = tf.data.TensorSlicesDataset(dataSet.trainLabels)
//      val testImages = tf.data.TensorSlicesDataset(dataSet.testImages)
//      val testLabels = tf.data.TensorSlicesDataset(dataSet.testLabels)
//
//   }
//
//}
