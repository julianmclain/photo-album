import play.sbt.PlayRunHook
import sbt._

import scala.sys.process.Process

/**
  * Frontend build play run hook.
  * https://www.playframework.com/documentation/2.8.x/sbtCookbook#Hooking-into-Plays-dev-mode
  */
object FrontendRunHook {
  def apply(base: File): PlayRunHook = {
    object UIBuildHook extends PlayRunHook {
      var process: Option[Process] = None

      /**
        * Pull in NPM commands
        */
      var npmInstall: String = FrontendCommands.dependencyInstall
      var npmRun: String = FrontendCommands.serve

      /**
        * Executed before play run start.
        * Run npm install if node modules are not installed.
        */
      override def beforeStarted(): Unit = {
        if (!(base / "ui" / "node_modules").exists())
          Process(npmInstall, base / "ui").!
      }

      /**
        * Executed after play run start.
        * Run npm start from the `ui` directory
        */
      override def afterStarted(): Unit = {
        process = Option(
          Process(npmRun, base / "ui").run
        )
      }

      /**
        * Executed after play run stop.
        * Cleanup frontend execution processes.
        */
      override def afterStopped(): Unit = {
        process.foreach(_.destroy())
        process = None
      }
    }
    UIBuildHook
  }
}
