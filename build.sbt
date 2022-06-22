lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin)
  .settings(
    inThisBuild(List(
      organization := "com.mikeycaine",
      version      := "0.1-SNAPSHOT",
      scalaVersion := "3.1.3",
    )),

    name := "scamp",

    stFlavour := Flavour.ScalajsReact,

    Compile / npmDevDependencies ++= Seq (
      "file-loader" -> "6.0.0",
      "style-loader" -> "1.2.1",
      "css-loader" -> "3.5.3",
      "html-webpack-plugin" -> "4.3.0",
      "copy-webpack-plugin" -> "5.1.1",
      "webpack-merge" -> "4.2.2"
    ),

    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.2",
      "react-dom" -> "17.0.2",
      "@types/react" -> "17.0.38",
      "@types/react-dom" -> "17.0.11",
//      "react-proxy" -> "1.1.8",
//
      // Three.js
      "three" -> "0.141.0",
      "@types/three" -> "0.141.0",

      // Openlayers
      "ol" -> "6.14.1",
      "@types/ol" -> "6.5.3"
//      "@types/p5" -> "1.3.2",
//
//      "react-p5" -> "1.3.24",
//      "@types/d3" -> "7.1.0",
//      "d3" -> "7.2.1",
//      "d3-geo" -> "3.0.1",
//      "@types/d3-geo"-> "3.0.2",
//
//      "topojson" -> "3.0.2",
//      "topojson-client" -> "3.1.0",
//      "@types/topojson" -> "3.2.3"
    ),

    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom"    % "2.2.0",
      "org.scalatest" %%% "scalatest"      % "3.2.12"    % "test",
      "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1",
      "com.github.japgolly.scalajs-react" %%% "extra" % "2.1.1",
      "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"   % "2.1.1",
      "dev.optics" %% "monocle-core"  % "3.1.0",
    ),
//
//    scalaJSUseMainModuleInitializer := true,
//    Compile / mainClass := Some("Main")
  )

webpack / version := "4.43.0"
startWebpackDevServer / version := "3.11.0"

webpackResources := baseDirectory.value / "webpack" * "*"

fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-fastopt.config.js")
fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-opt.config.js")
Test / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-core.config.js")

fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot")
fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly()

//Test / requireJsDomEnv := true

addCommandAlias("dev", ";fastOptJS::startWebpackDevServer;~fastOptJS")

addCommandAlias("build", "fullOptJS::webpack")

