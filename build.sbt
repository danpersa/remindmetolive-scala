import com.typesafe.sbt.packager.archetypes.JavaAppPackaging

name := "remindmetolive"

organization  := "com.remindmetolive"

version := "0.0.1"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8", "-Xexperimental", "-Xfatal-warnings", "-Xlint")

libraryDependencies ++= {
  Seq(
    "io.undertow"              % "undertow-core"                          % "1.3.5.Final",
    "ch.qos.logback"           % "logback-classic"                        % "1.1.3",
    "com.typesafe"             % "config"                                 % "1.3.0",
    "de.zalando.spearheads"   %% "beard"                                  % "0.0.3-SNAPSHOT",
    "com.google.code.findbugs" % "jsr305"                                 % "1.3.9",
    "com.mitchellbosecke"      % "pebble"                                 % "1.6.0",

    // test stuff
    "org.scalatest"           %% "scalatest"                              % "3.0.0-M1"       % "test",
    "org.scalamock"           %% "scalamock-scalatest-support"            % "3.2.2"          % "test"
  )
}
enablePlugins(JavaAppPackaging)
net.virtualvoid.sbt.graph.Plugin.graphSettings