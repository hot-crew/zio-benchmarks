resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.jcenterRepo
)

inThisBuild(
  List(
    scalaVersion := "2.13.3",
    crossScalaVersions := Seq("2.12.12", "2.13.3"),
    organization := "crew.zio",
    homepage := Some(url("https://github.com/zio-crew/zio-benchmarks")),
    startYear := Some(2020),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "<nickName>",
        "<givenName>",
        "<email>",
        url("https://github.com/<developer>")
      )
    ),
    scmInfo := Some(
      ScmInfo(url("https://github.com/zio-crew/zio-benchmarks"), "scm:git@github.com:zio-crew/zio-benchmarks.git")
    )
  )
)

lazy val commonSettings = Seq(
// Refine scalac params from tpolecat
  scalacOptions --= Seq(
    "-Xfatal-warnings"
  ),
  name := "bench",
  version := "0.0.1"
)

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"              % Version.zio,
  "dev.zio" %% "zio-test"         % Version.zio % "test",
  "dev.zio" %% "zio-test-sbt"     % Version.zio % "test",
  "dev.zio" %% "zio-interop-cats" % Version.zioInteropCats
)

lazy val root = (project in file("."))
  .enablePlugins(JmhPlugin)
  .settings(
    maxErrors := 3,
    commonSettings,
    zioDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.4"

// Benchmarks
// Parameters
// iterations, warmup, forks, threads
addCommandAlias("fut0", "jmh:run -i 1 -wi 1 -f1 -t4 .*FutureFiberSimpleBenchmark")
