package models


import scala.slick.driver.H2Driver.simple._
import orm.ExperimentResults
import models.VariableType._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.TableQuery

/**
 * Created by qingwei on 3/18/15.
 */
case class ExpResults(
                       dataSizesInput: Seq[String],
                       clusterSizesInput: Seq[String],
                       noIterationsInput: Seq[String],
                       algorithmsInput: Seq[String],
                       timeUsedsInput: Seq[String]) {
  val dataSizes = dataSizesInput map (_.toInt)
  val clusterSizes = clusterSizesInput map (_.toInt)
  val noIterations = noIterationsInput map (_.toInt)
  val algorithms = algorithmsInput
  val timeUseds = timeUsedsInput map (_.toLong)

  // return results as list of ExpResultPerRun
  def list: Seq[ExpResultPerRun] = {
    ((dataSizes, clusterSizes, noIterations).zipped.toSeq, algorithms, timeUseds).zipped.toSeq map {
      case ((d, c, n), a, t) => {
        val dS = ExpVariable(Fixed, "DataSize", d)
        val cS = ExpVariable(Fixed, "ClusterSize", c)
        val nI = ExpVariable(Fixed, "NoIteration", n)
        val espr = ExpSettingPerRun(Set(dS, cS, nI))
        ExpResultPerRun(espr, a, t)
      }
    }
  }
}

