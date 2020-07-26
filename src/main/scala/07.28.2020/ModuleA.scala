package talks.july2020

import zio._
import talks.july2020.moduleA.ModuleA.Service

object moduleA {

  // service binding
  type ModuleA = Has[ModuleA.Service]

  // service declaration
  object ModuleA {
    trait Service {
      def run(): UIO[String]
    }
  }

  // service implementation
  val live = ZLayer.fromService { kafka: Kafka =>
    new Service {
      override def run() = UIO(kafka.talk())
    }
  }

  // Public accessor
  def run(): URIO[ModuleA, String] = ZIO.accessM(_.get.run())

}
