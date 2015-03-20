package models

/**
 * Created by qingwei on 3/17/15.
 */
case class ExpResultPerRun(setting: ExpSettingPerRun, algorithm: String, timeUsed: Long) {
  val dataSize = setting("DataSize")
  val clusterSize = setting("ClusterSize")
  val noIteration = setting("NoIteration")
}


