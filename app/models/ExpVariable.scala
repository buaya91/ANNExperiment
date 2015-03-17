package models
/**
 * Created by qingwei on 3/16/15.
 *
 */

object VariableType extends Enumeration {
  type VariableType = Value
  val Fixed, Independent, Dependent = Value
}

import VariableType._

case class ExpVariable(vType: VariableType, vName: String, value: Int*) {
  require(!((vType == Fixed) && (value.length > 1)), "fixed variable cannot have more than one variable")
  require(!((vType == Independent) && (value.length <= 1)), "independent variable must have more than one value")
  require(value.length > 0, "variable must have value")
}
