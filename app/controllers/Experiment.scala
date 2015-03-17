package controllers

import breeze.linalg.{DenseVector => Vec}
import models._
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.{SparkConf, SparkContext}

import models.ExpStatus._

case object Experiment {

  var status = NotRunning
  val conf = new SparkConf().setAppName("ANN Experiment").setMaster("local[*]")

  //return time used to run experiment using
  def experimentResult(expSettings: ExpSettings): Seq[ExpResultPerRun] = {
    status = Running
    val settingsForAllRun = expSettings.variableSetsPerRun
    val results: Seq[ExpResultPerRun] = {
      val x = (for (s <- settingsForAllRun.toSeq) yield {
        (runANN(s), runSVM(s))
      }).unzip
      x._1 ++ x._2
    }
    status = NotRunning
    results
  }

  def runANN(setting: ExpSettingPerRun): ExpResultPerRun = {
    val neuralNetwork = ANN(Seq(2,4,5,2))
    val dataSize = setting("DataSize")
    val clusterSize = setting("ClusterSize")
    val noIteration = setting("NoIteration")

    val runConf = conf.set("spark.cores.max", clusterSize.toString)
    val sc = new SparkContext(runConf)

    val (x, y) = xorData(dataSize)
    val (rddX, rddY) = (sc.parallelize(x), sc.parallelize(y))

    val begin: Long = System.currentTimeMillis()
    neuralNetwork.trainWithSparkCluster(rddX, rddY, 0.02, noIteration)
    val end: Long = System.currentTimeMillis()

    sc.stop()
    val t = end - begin
    ExpResultPerRun(setting, "ANN", t)
  }

  private def runSVM(setting: ExpSettingPerRun): ExpResultPerRun = {
    val dataSize = setting("DataSize")
    val clusterSize = setting("ClusterSize")
    val noIteration = setting("NoIteration")

    val runConf = conf.set("spark.cores.max", clusterSize.toString)
    val sc = new SparkContext(runConf)

    val (x, y) = xorData(dataSize)
    val labelP = pairToLabeledPoint(x, y)
    val labelPRDD = sc.parallelize(labelP)

    val begin: Long = System.currentTimeMillis()
    val model = SVMWithSGD.train(labelPRDD, noIteration)
    val end: Long = System.currentTimeMillis()

    sc.stop()
    val t = end - begin
    ExpResultPerRun(setting, "SVM", t)
  }

  private def xorData(n: Int): (Seq[Vec[Double]], Seq[Vec[Double]]) = {
    val x: Seq[Vec[Double]] = for (i <- 1 to n) yield {
      i % 4 match {
        case 0 => Vec(0.0, 0.0)
        case 1 => Vec(0.0, 1.0)
        case 2 => Vec(1.0, 0.0)
        case 3 => Vec(1.0, 1.0)
      }
    }

    val y: Seq[Vec[Double]] = for (i <- 1 to n) yield {
      i % 4 match {
        case 0 => Vec(1.0, 0.0)
        case 1 => Vec(0.0, 1.0)
        case 2 => Vec(0.0, 1.0)
        case 3 => Vec(1.0, 0.0)
      }
    }
    (x, y)
  }

  private def pairToLabeledPoint(x: Seq[Vec[Double]], y: Seq[Vec[Double]]): Seq[LabeledPoint] = {
    val features: Seq[DenseVector] = x.map(vec => new DenseVector(vec.toArray))
    val labels: Seq[Double] = y.map(vec => vec.findAll(_ == 1.0)(0).toDouble)
    labels zip features map(pair => new LabeledPoint(pair._1, pair._2))
  }
}
