name := "ESJ" //EmailSceneJudge

version := "0.2"

scalaVersion := "2.10.5"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(

  jdbc,
  ws,
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.slick" % "slick_2.10" % "2.1.0",

  "org.apache.commons" % "commons-dbcp2" % "2.0.1",
  "org.apache.commons" % "commons-pool2" % "2.2",

  "org.apache.hadoop" % "hadoop-hdfs" % "2.4.1",
  "org.apache.hadoop" % "hadoop-common" % "2.4.1",

  "org.apache.hbase" % "hbase-server" % "1.1.1",
  "org.apache.hbase" % "hbase-client" % "1.1.1",
  "org.apache.hbase" % "hbase-common" % "1.1.1",

  "org.apache.kafka" % "kafka_2.10" % "0.8.0",
  "org.apache.spark" % "spark-core_2.10" % "1.3.1",
  "org.scalatest" % "scalatest_2.10" % "2.1.6" % "test",

  "javax.mail" % "mail" % "1.4.6",
  "com.sun.jdmk" % "jmxtools" % "1.2.1",

  "com.googlecode.xmemcached" % "xmemcached" % "2.0.0",
  "net.debasishg" % "redisclient_2.10" % "3.0",

  "org.mongodb" % "casbah_2.10" % "2.8.1",
  "mysql" % "mysql-connector-java" % "5.1.12",

  "net.liftweb" % "lift-json_2.10" % "2.5",
  "com.propensive" % "rapture-json-lift_2.10" % "1.0.6",

  "org.ansj" % "ansj_seg" % "2.0.8",
  "org.jboss.tattletale" % "tattletale" % "1.2.0.Beta2"
)

doc in Compile <<= target.map(_ / "none")
