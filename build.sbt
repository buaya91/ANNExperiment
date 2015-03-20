name := """ANNExperiment"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val sctest = "org.scalatestplus" %% "play" % "1.3.0" % "test"
lazy val mlLib = "org.apache.spark" %% "spark-mllib" % "1.2.0"
lazy val spark = "org.apache.spark" %% "spark-core" % "1.2.0"
lazy val slick = "com.typesafe.slick" %% "slick" % "2.1.0"
lazy val h2 = "com.h2database" % "h2" % "1.3.175"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  sctest,
  mlLib,
  spark,
  slick,
  h2
)


fork in run := true