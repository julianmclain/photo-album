lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """photo-album""",
    version := "2.8.x",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play-slick" % "5.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
      "com.h2database" % "h2" % "1.4.199",
      "org.webjars" %% "webjars-play" % "2.8.0-1", // https://www.webjars.org/documentation
      "org.webjars" % "bootstrap" % "4.5.3",
      specs2 % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
