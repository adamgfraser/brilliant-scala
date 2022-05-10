lazy val root = project
  .in(file(""))
  .settings(
    name := "functional-data-modeling-and-design",
    version := "0.1.0",
    scalacOptions ++= Seq(
      "-language:postfixOps"
    ),
    scalaVersion := "2.12.15"
  )

libraryDependencies ++= Seq(
  // ZIO
  "dev.zio" %% "zio" % "2.0.0-RC6",
  "dev.zio" %% "zio-streams" % "2.0.0-RC6",
  "dev.zio" %% "zio-test" % "2.0.0-RC6" % "test",
  "dev.zio" %% "zio-test-sbt"      % "2.0.0-RC6" % "test",
  // URL parsing
  "io.lemonlabs" %% "scala-uri" % "1.4.1"
)

libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.10.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
