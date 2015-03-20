package orm

import scala.slick.direct.AnnotationMapper.column
import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{Column, ProvenShape}

/**
 * Created by qingwei on 3/18/15.
 */
class ExperimentResults(tag: Tag) extends Table[(Long, Int, Int, Int, String, Long)](tag, "experiment") {
  def loggingTime: Column[Long] = column[Long]("LOGGING_TIME")
  def dataSize: Column[Int] = column[Int]("DATA_SIZE")
  def clusterSize: Column[Int] = column[Int]("CLUSTER_SIZE")
  def noIteration: Column[Int] = column[Int]("NO_ITERATION")
  def algorithm: Column[String] = column[String]("ALGORITHM")
  def timeUsed: Column[Long] = column[Long]("TIME_USED")

  def * : ProvenShape[(Long, Int, Int, Int, String, Long)] =
    (loggingTime, dataSize, clusterSize, noIteration, algorithm, timeUsed)
}
