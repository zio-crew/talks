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
  val live = ZLayer.fromServices((modA: ModuleA.Service, cass: Cassandra) =>
    new Service {
      def run(): UIO[String] = modA.run().map(_ + "_" + cass.talk())
    }
  )

  // Public accessor
  def run(): URIO[ModuleB, String] = ZIO.accessM(_.get.run())
}
