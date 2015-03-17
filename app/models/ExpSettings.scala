package models

import VariableType._

/**
 * Created by qingwei on 3/16/15.
 */
case class ExpSettings(dataSizeInput: String, clusterSizeInput: String, noIterationInput: String) {
  require(Set(dataSizeInput, clusterSizeInput, noIterationInput).filter(v => v.isEmpty).size == 0,
    "should not have empty string")

  val dataSize = generateExpVarFromString(dataSizeInput, "DataSize")
  val clusterSize = generateExpVarFromString(clusterSizeInput, "ClusterSize")
  val noIteration = generateExpVarFromString(noIterationInput, "NoIteration")

  require(Set(dataSize, clusterSize, noIteration).filter(_.vType == Independent).size == 1,
    "should have one and only one Independent variable, but now having " +
    Set(dataSize, clusterSize, noIteration).filter(_.vType == Independent).size +
    " Independent")

  val independentVar: ExpVariable = Set(dataSize, clusterSize, noIteration)
    .find(v => v.vType == Independent)
    .getOrElse(throw new RuntimeException("no independent variable, which should not happen"))

  val fixedVars: Set[ExpVariable] = Set(dataSize, clusterSize, noIteration).filter(v => v.vType == Fixed)

  /** *
    * create a set of variables for experiment to run for once
    * all variables are fixed with one value
    * @return
    */
  def variableSetsPerRun: Set[ExpSettingPerRun] = {
    (for (v <- independentVar.value) yield {
      val iv = ExpVariable(Fixed, independentVar.vName, v)
      ExpSettingPerRun(fixedVars + iv)
    }).toSet
  }

  def generateExpVarFromString(valueString: String, varName: String): ExpVariable = {
    val values = valueString.split("[\\s,]+").map(_.toInt)
    val vtype = values.size match {
      case 1 => Fixed
      case x if x > 1 => Independent
    }
    ExpVariable(vtype, varName, values:_*)
  }
}
