import Dependencies._
lazy val root = (project in file(".")).
 settings(
   inThisBuild(List(
     organization := "chaordic.goose",
     scalaVersion := "2.12.8", // stuck with Scala 2.12, since many plugins still lag in support
     version      := "0.1.0-SNAPSHOT"
   )),
   name := "game-of-goose",
   parallelExecution in Test := false,
   libraryDependencies ++= Seq(catsEffects, parserCombinators, jline,
     scalatest % Test),
   assemblyJarName in assembly := "game-of-goose.jar",
 )

