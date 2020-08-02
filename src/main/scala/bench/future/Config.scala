package bench.future

import zio.{ ZIO }

import java.util.concurrent.Executors

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object Config {

  val Num    = 100
  val fibers = 16
  val cores  = 4

  implicit val ec1 = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(cores))

  def performCalc(i: Int): Long = (i * i).toLong

  // Future
  def futureCalc(N: Int) = {
    val fut = Future.traverse(1 to N)(i => Future(performCalc(i)))
    Await.result(fut, 5000.millis).sum
  }

  // Fiber
  def eff(x: Int) = ZIO.effectTotal(performCalc(x))

  def fiberCalc(N: Int) = ZIO.foreachParN(fibers)(1 to N)(eff(_)).map(_.sum)

}
