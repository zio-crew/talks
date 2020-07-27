import java.util.concurrent.TimeUnit
import java.time.{ DateTimeException, OffsetDateTime }

import zio._
import zio.clock._
import zio.console._
import zio.duration._

object Modification {

  /**
   * Takes an effect that potentially schedules actions to take place at some
   * time in the future and returns a version which is otherwise identical but
   * performs those actions immediately.
   */
  def timeless[R <: Clock, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
    zio.updateService[Clock.Service] { clock =>
      new Clock.Service {
        def currentDateTime: IO[DateTimeException, OffsetDateTime] =
          clock.currentDateTime
        def currentTime(unit: TimeUnit): UIO[Long] =
          clock.currentTime(unit)
        def nanoTime: UIO[Long] =
          clock.nanoTime
        def sleep(duration: Duration): UIO[Unit] =
          UIO.unit
      }
    }

  /**
   * A schedule that recurs five times with an exponential delay.
   */
  val schedule: Schedule[Clock, Any, Duration] =
    Schedule.exponential(1.second) <* Schedule.recurs(5)

  val zio1: ZIO[Clock with Console, Nothing, Unit] =
    console.putStrLn("Hello, World!").repeat(schedule).unit

  val zio2: ZIO[Clock with Console, Nothing, Unit] =
    timeless(zio1)

  trait Aspect[-Env] {
    def apply[R <: Env, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A]
  }

  object Aspect {
    val noDelay: Aspect[Clock] =
      new Aspect[Clock] {
        def apply[R <: Clock, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
          timeless(zio)
      }
    implicit final class AspectSyntax[-R, +E, +A](private val self: ZIO[R, E, A]) extends AnyVal {
      def @@[R1 <: R](aspect: Aspect[R1]): ZIO[R1, E, A] =
        aspect(self)
    }
  }

  object Example {
    import Aspect._

    val zio3: ZIO[Clock with Console, Nothing, Unit] =
      zio1 @@ noDelay
  }
}
