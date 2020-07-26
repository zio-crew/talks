package talks

import july2020._

import zio.{ ZLayer }
import zio.test.Assertion._
import zio.test._

object BaseSpec extends DefaultRunnableSpec {
  def spec =
    suite("ZLayerSpec")(
      testM("Module A test") {
        val res = moduleA.run()

        assertM(res)(equalTo("kafka"))
      }.provideCustomLayer(liveLayerA),
      testM("Module B test") {
        val res = moduleB.run()

        assertM(res)(equalTo("kafka_cassandra"))
      }.provideCustomLayerShared(liveLayer)
    )

  val kafkaLayer = ZLayer.succeed(new Kafka     {})
  val cassLayer  = ZLayer.succeed(new Cassandra {})

  val liveLayerA = kafkaLayer >>> moduleA.live
  val liveLayer  = (liveLayerA ++ cassLayer) >>> moduleB.live
}
