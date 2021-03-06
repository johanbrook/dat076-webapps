import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "invoicer"
  val appVersion      = "0.1"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    ebeanEnabled := true
  )

}
