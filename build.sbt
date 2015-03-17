name := """ANNExperiment"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val sctest = "org.scalatest" %% "scalatest" % "2.2.1" % "test"
lazy val mlLib = "org.apache.spark" %% "spark-mllib" % "1.2.0"
lazy val spark = "org.apache.spark" %% "spark-core" % "1.2.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  sctest,
  mlLib,
  spark
)


fork in run := true