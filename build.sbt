name := "akka-factorial"

version := "1.0"

scalaVersion := "2.11.5"

val akkaVersion  = "2.3.9"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)
