import sbt._
import sbt.Keys._
import play.sbt.PlayRunHook
import play.sbt.PlayImport.PlayKeys._
import play.twirl.sbt.Import.TwirlKeys
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.web.Import._

object PlayGulp {
  lazy val gulpDirectory = SettingKey[File]("gulp-directory", "gulp directory")
  lazy val gulpFile = SettingKey[String]("gulp-file", "gulpfile")
  lazy val gulp = InputKey[Unit]("gulp", "Task to run gulp")
  lazy val gulpBuild = TaskKey[Int]("gulp-dist", "Task to run dist gulp")
  lazy val gulpTest = TaskKey[Unit]("gulp-test", "Task to run gulp test")

  val playGulpSettings: Seq[Setting[_]] = Seq(

    gulpDirectory <<= (baseDirectory in Compile) { _ / "ui" },

    gulpFile := "gulpfile.js",

    gulp := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      runGulp(base, gulpfileName, Def.spaceDelimited("<arg>").parsed.toList).exitValue()
    },

    gulpBuild := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val result = runGulp(base, gulpfileName, List("build")).exitValue()
      if (result == 0) {
        result
      } else throw new Exception("gulp failed")
    },


    gulpTest := {
      val base = (gulpDirectory in Compile).value
      val gulpfileName = (gulpFile in Compile).value
      val result = runGulp(base, gulpfileName, List("test")).exitValue()
      if (result != 0) throw new Exception("gulp failed")
    },

    dist <<= dist dependsOn gulpBuild,

    stage <<= stage dependsOn gulpBuild,

    playRunHooks <+= (gulpDirectory, gulpFile).map {
      (base, fileName) => GulpWatch(base, fileName)
    },

    commands <++= gulpDirectory {
      base =>
        Seq(
          "npm"
        ).map(cmd(_, base))
    }
  )

  private def runGulp(base: sbt.File, fileName: String, args: List[String] = List.empty): Process = {
    if (System.getProperty("os.name").startsWith("Windows")) {
      val process: ProcessBuilder = Process("cmd" :: "/c" :: "gulp" :: "--gulpfile=" + fileName :: args, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      process.run()
    } else {
      val process: ProcessBuilder = Process("gulp" :: "--gulpfile=" + fileName :: args, base)
      println(s"Will run: ${process.toString} in ${base.getPath}")
      process.run()
    }
  }

  import scala.language.postfixOps

  private def cmd(name: String, base: File): Command = {
    if (!base.exists()) {
      base.mkdirs()
    }
    Command.args(name, "<" + name + "-command>") {
      (state, args) =>
        if (System.getProperty("os.name").startsWith("Windows")) {
          Process("cmd" :: "/c" :: name :: args.toList, base) !<
        } else {
          Process(name :: args.toList, base) !<
        }
        state
    }
  }

  object GulpWatch {

    def apply(base: File, fileName: String): PlayRunHook = {

      object GulpSubProcessHook extends PlayRunHook {

        var process: Option[Process] = None

        override def beforeStarted(): Unit = {
          process = Some(runGulp(base, fileName, "build" :: "watch" :: Nil))
        }

        override def afterStopped(): Unit = {
          process.foreach(_.destroy())
          process = None
        }
      }

      GulpSubProcessHook
    }

  }

}
