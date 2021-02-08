package example

import snunit._
import snunit.snunitzio._
import zio._

object Main {
  def main(args: Array[String]): Unit = {
    AsyncServerBuilder()
      .withZIORequestHandler(_ => UIO(Response(StatusCode.OK, "Hello from ZIO!", Seq.empty)))
      .withRequestHandler(_.send(StatusCode.NotFound, "Not found", Seq.empty))
      .build()
  }
}
