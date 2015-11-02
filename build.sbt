name := "ESJ" //Email Scene Work
version := "0.1"

scalaVersion := "2.10.5"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  ws,
  "com.googlecode.xmemcached" % "xmemcached" % "2.0.0",
  "org.apache.hadoop" % "hadoop-hdfs" % "2.4.1",
  "org.apache.hadoop" % "hadoop-common" % "2.4.1",


  "org.apache.hbase" % "hbase-client" % "1.1.1",
  "org.apache.hbase" % "hbase-common" % "1.1.1",

  "org.hbase" % "asynchbase" % "1.7.0"
)
doc in Compile <<= target.map(_ / "none")
