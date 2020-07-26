package hello

import zio.App
import zio.console.{ putStrLn }

object Main extends App {

  def run(args: List[String]) =
    myAppLogic.exitCode

  val myAppLogic =
    for {
      _ <- putStrLn("Hello World")
    } yield ()
}
