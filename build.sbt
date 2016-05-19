import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

name := "remindmetolive"

organization  := "com.remindmetolive"

version := "0.0.1"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-Xexperimental", "-Xfatal-warnings", "-Xlint")

resolvers ++= Seq(
  "zalando-maven" at "https://dl.bintray.com/zalando/maven"
)

libraryDependencies ++= {
  Seq(
    "org.scala-lang.modules"  %% "scala-xml"                              % "1.0.5",
    "io.undertow"              % "undertow-core"                          % "1.3.22.Final",
    "ch.qos.logback"           % "logback-classic"                        % "1.1.7",
    "com.typesafe"             % "config"                                 % "1.3.0",
    "de.zalando"              %% "beard"                                  % "0.1.0",
    "com.google.code.findbugs" % "jsr305"                                 % "1.3.9",
    "com.mitchellbosecke"      % "pebble"                                 % "2.2.1",

    // test stuff
    "org.scalatest"           %% "scalatest"                              % "3.0.0-M15"       % "test",
    "org.scalamock"           %% "scalamock-scalatest-support"            % "3.2.2"          % "test"
  )
}
enablePlugins(JavaAppPackaging)
net.virtualvoid.sbt.graph.Plugin.graphSettings
