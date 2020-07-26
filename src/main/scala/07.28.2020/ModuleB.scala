package talks.july2020

import zio._
import moduleA._
import talks.july2020.moduleB.ModuleB.Service

object moduleB {

  // service binding
  type ModuleB = Has[ModuleB.Service]

  // service declaration
  object ModuleB {
    trait Service {
      def run(): UIO[String]
    }
  }

  // service implementation
  val live = ZLayer.fromService((modA: ModuleA.Service) =>
    new Service {
      def run(): UIO[String] = modA.run()
    }
  )

  // Public accessor
  def run(): URIO[ModuleA, String] = ZIO.accessM(_.get.run())
}
