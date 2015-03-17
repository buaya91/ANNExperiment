import breeze.linalg.{DenseVector => Vec}
import controllers.Experiment
import models.{ExpResultPerRun, ExpVariable, ExpSettingPerRun, ExpSettings}
import models.VariableType._
import org.apache.spark.mllib.regression.LabeledPoint
import org.scalatest.{FlatSpec, MustMatchers, PrivateMethodTester}
/**
 * Created by qingwei on 3/16/15.
 */
class ExperimentTest extends FlatSpec with MustMatchers with PrivateMethodTester{
  "Experiment" should "produce n XOR data records when xorData is invoked" in {
    val xorData = PrivateMethod[(Seq[Vec[Double]], Seq[Vec[Double]])]('xorData)
    val data = Experiment invokePrivate xorData(10)

    assert(data._1.size == 10)
  }

  it should "convert XOR data into labeledpoint when pairToLabeledPoint is invoked" in {
    val xorData = PrivateMethod[(Seq[Vec[Double]], Seq[Vec[Double]])]('xorData)
    val data = Experiment invokePrivate xorData(10)

    val ptoLBP = PrivateMethod[Seq[LabeledPoint]]('pairToLabeledPoint)
    val lbp = Experiment invokePrivate ptoLBP(data._1, data._2)

    assert(lbp.size == 10)
  }

  it should "return experiment result when runANN is invoked" in {
    val dataSizeVar = ExpVariable(Fixed, "DataSize", 100)
    val clusterSizeVar = ExpVariable(Fixed, "ClusterSize", 3)
    val noIterationVar = ExpVariable(Fixed, "NoIteration", 30)

    val exp = ExpSettingPerRun(Set(dataSizeVar, clusterSizeVar, noIterationVar))

    //val runANN = PrivateMethod[ExpResultPerRun]('runANN)
    //val result = Experiment invokePrivate runANN(exp)
    val result = Experiment.runANN(exp)
    assert(result.timeUsed > 200)
  }

  it should "return experiment result when runSVM is invoked" in {
    val dataSizeVar = ExpVariable(Fixed, "DataSize", 100)
    val clusterSizeVar = ExpVariable(Fixed, "ClusterSize", 3)
    val noIterationVar = ExpVariable(Fixed, "NoIteration", 30)

    val exp = ExpSettingPerRun(Set(dataSizeVar, clusterSizeVar, noIterationVar))

    val runSVM = PrivateMethod[ExpResultPerRun]('runSVM)
    val result = Experiment invokePrivate runSVM(exp)
    assert(result.timeUsed > 200)
  }
}
