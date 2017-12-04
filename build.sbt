import PlayGulp._

name := """camotes"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(playGulpSettings)

scalaVersion := "2.12.2"

resolvers += "Atilika" at "http://www.atilika.org/nexus/content/repositories/atilika"

libraryDependencies ++= Seq(
  "ch.qos.logback"               %  "logback-classic"                  % "1.2.3",
  "com.typesafe.play"            %% "play-json-joda"                   % "2.6.0",
  "com.github.cb372"             %% "scalacache-core"                  % "0.9.4",
  "com.github.cb372"             %% "scalacache-guava"                 % "0.9.4",
  "com.google.inject.extensions" % "guice-multibindings"               % "4.1.0",
  "com.h2database"               % "h2"                                % "1.4.192",
  "com.ibm.icu"                  % "icu4j"                             % "58.2",
  "io.kanaka"                    %% "play-monadic-actions"             % "2.1.0",
  "io.lemonlabs"                 %% "scala-uri"                        % "0.5.0",
  "joda-time"                    % "joda-time"                         % "2.9.6",
  "mysql"                        % "mysql-connector-java"              % "6.0.6",
  "net.debasishg"                %% "redisclient"                      % "3.4",
  "org.apache.commons"           % "commons-lang3"                     % "3.1",
  "org.scala-lang.modules"       %% "scala-async"                      % "0.9.6",
  "org.scalatestplus.play"       %% "scalatestplus-play"               % "3.0.0"   % Test,
  "org.scalaz"                   %% "scalaz-core"                      % "7.2.16",
  "org.scalikejdbc"              %% "scalikejdbc"                      % "3.1.0",
  "org.scalikejdbc"              %% "scalikejdbc-config"               % "3.1.0",
  "org.scalikejdbc"              %% "scalikejdbc-play-initializer"     % "2.6.0-scalikejdbc-3.0",
  "org.scalikejdbc"              %% "scalikejdbc-syntax-support-macro" % "3.1.0",
  evolutions,
  guice,
  jdbc,
  specs2 % Test,
  ws
)

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.apache.logging.log4j" % "log4j-core" % "2.9.1",
  "org.apache.logging.log4j" % "log4j-api" % "2.9.1"
)


javaOptions in Test += "-Dconfig.file=conf/test.conf"

PlayKeys.devSettings := Seq("play.server.http.port" -> "9009")

TwirlKeys.templateImports += "application.views._"
TwirlKeys.templateImports += "application.controllers.routes._"
