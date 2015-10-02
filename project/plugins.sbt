logLevel := Level.Warn

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.0.2")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")