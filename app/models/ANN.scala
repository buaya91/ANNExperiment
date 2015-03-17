package models

import breeze.linalg.{DenseVector => Vec, DenseMatrix => Mat}
import breeze.numerics.sigmoid
import org.apache.spark.rdd.RDD

import scala.collection.generic.CanBuildFrom
import scala.collection.parallel.immutable.ParVector

case class ANN(sizes: Seq[Int]) extends Serializable{
  /**
   * 1. initialize weights where each non-input layer should have one
   *    a) each weights should be a matrix with size of m x n where
   *       m = no of nodes in corresponding layer and
   *       n = no of nodes in previous layer
   * 2. initialize bias where each non-input layer should have one
   *    a) each bias should be a col vector with size of m x 1
   *       m = no of nodes in corresponding layer
   *
   */
  val noOfLayers = sizes.length

  var weights: Seq[Mat[Double]] = for (i <- 1 to sizes.length - 1) yield {
    val x: Mat[Double] = Mat.rand(sizes(i), sizes(i - 1))
    x
  }

  var biases: Seq[Mat[Double]] = for (i <- 1 to sizes.length - 1) yield {
    val x: Mat[Double] = Mat.rand(sizes(i), 1)
    x
  }


  /**
   * 1. feedforward is an iterative process that compute each layer output using formula
   *    output(N) = weights(N)* output(N - 1) + bias(N)
   *    where N start from 1 and stop when N = noOfLayers
   */
  def feedForward(input: Mat[Double]): Mat[Double] = {
    var output = input
    require(output.cols == 1)

    for ((w,b) <- this.weights zip this.biases) {
      output = sigmoid(w.*[Mat[Double],Mat[Double],Mat[Double]](output) + b) // need sigmoidVec
    }
    output
  }

  def trainWithParCol(trainXSeq: Seq[Vec[Double]], trainYSeq: Seq[Vec[Double]], eta: Double, epochs: Int) = {

    val trainXPar = trainXSeq.par
    val trainYPar = trainYSeq.par

    for (i <- 0 to epochs) {
      val (chW, chB) = (trainXPar zip trainYPar).map(XY => updates(XY._1, XY._2, eta)).reduce(sumUpdates(_, _))
      weights = (weights zip chW).map(pair => pair._1 :+ pair._2)
      biases = (biases zip chB).map(pair => pair._1 :+ pair._2)
    }
  }

  def trainWithSeqCol(trainXSeq: Seq[Vec[Double]], trainYSeq: Seq[Vec[Double]], eta: Double, epochs: Int) = {
    for (i <- 0 to epochs) {
      val (chW, chB) = (trainXSeq zip trainYSeq).map(XY => updates(XY._1, XY._2, eta)).reduce(sumUpdates(_, _))
      weights = (weights zip chW).map(pair => pair._1 :+ pair._2)
      biases = (biases zip chB).map(pair => pair._1 :+ pair._2)
    }
  }

  def trainWithSparkCluster(trainX: RDD[Vec[Double]], trainY: RDD[Vec[Double]], eta: Double, epochs: Int) = {
    trainX.cache()
    trainY.cache()
    for (i <- 0 to epochs) {
      val (chW, chB) = (trainX zip trainY).map(XY => updates(XY._1, XY._2, eta)).reduce(sumUpdates(_, _))
      weights = (weights zip chW).map(pair => pair._1 :+ pair._2)
      biases = (biases zip chB).map(pair => pair._1 :+ pair._2)
    }
  }

  def sumUpdates(A: (Seq[Mat[Double]], Seq[Mat[Double]]),
                 B: (Seq[Mat[Double]], Seq[Mat[Double]])): (Seq[Mat[Double]], Seq[Mat[Double]]) = {
    val wPair = A._1 zip B._1
    val bPair = A._2 zip B._2

    val wSum = wPair.map(weightPair => weightPair._1 :+ weightPair._2)
    val bSum = bPair.map(biasPair => biasPair._1 :+ biasPair._2)
    (wSum, bSum)
  }

  /**
   *
   * @param X = input features
   * @param Y = expected output
   * @param eta = learning rate
   * @return = method with side effects that update weights and bias
   */
  def updates(X: Vec[Double], Y: Vec[Double], eta: Double): (Seq[Mat[Double]], Seq[Mat[Double]]) = {

    val (deltaW, deltaB) = backProp(X, Y)
    val changesOfW = deltaW.map(_ :* -eta)
    val changesOfB = deltaB.map(_ :* -eta)
    (changesOfW, changesOfB)
  }

  /**
   *
   */
  def backProp(X: Vec[Double], Y: Vec[Double]): (Seq[Mat[Double]], Seq[Mat[Double]]) = {
    var dCdW: Seq[Mat[Double]] = for (i <- 1 to sizes.length - 1) yield {
      val w: Mat[Double] = Mat.zeros(sizes(i), sizes(i - 1))
      w
    }
    var dCdB: Seq[Mat[Double]] = for (i <- 1 to sizes.length - 1) yield {
      val b: Mat[Double] = Mat.zeros(sizes(i), 1)
      b
    }

    var activation: Mat[Double] = X.asDenseMatrix.t
    var activations: Seq[Mat[Double]] = Seq(activation)

    var zs: Seq[Mat[Double]] = Seq()

    for ((b, w) <- (biases zip weights)) {
      var z: Mat[Double] = w.*[Mat[Double], Mat[Double], Mat[Double]](activation) + b
      zs = zs :+ z
      activation = sigmoid(z)
      activations = activations :+ activation
    }

    val dCdA = costDerivatives(activations.last, Y.asDenseMatrix)
    val sigPrime = sigmoidPrime(zs.last)
    var delta: Mat[Double] = dCdA :* sigPrime
    dCdB = dCdB.updated(dCdB.size - 1, delta)
    dCdW = dCdW.updated(dCdW.size - 1, delta * activations(activations.size - 2).t)

    for (l <- 2 to noOfLayers - 1) {
      val z = zs(zs.size - l)      //obtain z(n) in reversed order
      val sigPrimeZ = sigmoidPrime(z)  //obtain sigmoid'(z(n))
      delta =
        (weights(weights.size - l + 1).t.*[Mat[Double], Mat[Double], Mat[Double]](delta)) :* sigPrimeZ
      dCdB = dCdB.updated(dCdB.size - l, delta)
      dCdW = dCdW.updated(dCdW.size - l, delta * activations(activations.size - l - 1).t)
    }
    (dCdW, dCdB)
  }

  def costDerivatives(a: Mat[Double], expected: Mat[Double]): Mat[Double] = {
    a :- expected
  }

  def sigmoidPrime(z: Mat[Double]): Mat[Double] = {
    sigmoid(z) :* Mat.ones[Double](z.rows, z.cols).:-(sigmoid(z))
  }
}
