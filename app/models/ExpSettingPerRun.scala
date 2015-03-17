package models
import VariableType._
/**
 * Created by qingwei on 3/16/15.
 */
case class ExpSettingPerRun(variables: Set[ExpVariable]) {
  require(variables.filter(v => v.vType != Fixed).size == 0, "Variables for each run are constant")

  def apply(s: String): Int = variables.find(v => v.vName == s).get.value(0)
}
