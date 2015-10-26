name := "ESJ" //Email Scene Judge
version := "0.1"

scalaVersion := "2.10.5"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(

  jdbc,
  ws
//  "com.typesafe" % "config" % "1.2.1",
//  "com.typesafe.slick" % "slick_2.10" % "2.1.0"
)
doc in Compile <<= target.map(_ / "none")
