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
  "dev.zio" %% "zio" % "2.0.0-RC5",
  "dev.zio" %% "zio-streams" % "2.0.0-RC5",
  "dev.zio" %% "zio-test" % "2.0.0-RC5" % "test",
  // URL parsing
  "io.lemonlabs" %% "scala-uri" % "1.4.1"
)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
