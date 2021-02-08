import Dependencies._
import sys.process._

ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val deploy = taskKey[Unit]("deploy server on NGINX Unit") := {
  val binary = (Compile / nativeLink).value
  val socket = sys.props("os.name") match {
    case "Linux"    => "/var/run/control.unit.sock"
    case "Mac OS X" => "/usr/local/var/run/unit/control.sock"
  }
  def json(numProc: Int) =
    s"""{
       |  "applications": {
       |    "app": {
       |      "type": "external",
       |      "working_directory": "\${binary.getParent}",
       |      "executable": "\${binary.getName}",
       |      "processes": \$numProc
       |    }
       |  },
       |  "listeners": {
       |    "*:9000": {
       |      "pass": "applications/app"
       |    }
       |  }
       |}""".stripMargin

  def command(numProc: Int) = assert(Seq("curl", "-X", "PUT", "-d", json(numProc), "--unix-socket", socket, "http://localhost/config").! == 0)

  command(0)
  command(1)
}

lazy val root = (project in file("."))
  .settings(
    name := "$name$",
    libraryDependencies ++= Seq(
      "com.github.lolgab" %%% "snunit-zio" % "0.0.7"
    ),
    deploy
  )
  .enablePlugins(ScalaNativePlugin)
