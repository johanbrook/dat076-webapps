import play.Project._

name := "invoicer"

version := "0.1"

libraryDependencies ++= Seq(
  "joda-time"	% "joda-time"		% "2.1",
  "com.github.nscala-time" %% "nscala-time" % "0.6.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "javax.mail"	% "mail" % "1.4",
  "com.google.inject" % "guice" % "4.0-beta"
)

playJavaSettings
