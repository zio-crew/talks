package talks.july2020

import zio._
import talks.july2020.moduleA.ModuleA.Service

object moduleA {

  // service binding
  type ModuleA = Has[ModuleA.Service]

  // service declaration
  object ModuleA {
    trait Service {
      def run(v: Int): UIO[Int]
    }
  }

  // service implementation
  val live = ZLayer.succeed {
    new Service {
      override def run(v: Int): UIO[Int] = UIO(v)
    }
  }

  // Public accessor
  def run(v: Int): URIO[ModuleA, Int] = ZIO.accessM(_.get.run(v))

}
