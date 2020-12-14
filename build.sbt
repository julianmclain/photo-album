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
      // https://www.webjars.org/documentation
      "org.webjars" %% "webjars-play" % "2.8.0-1",
      "org.webjars" % "font-awesome" % "5.15.1",
      "org.webjars" % "bootstrap" % "4.5.3",
      "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4",
      specs2 % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
