package bench.future

import zio.{ ZIO }
import zio.blocking.blocking

import java.util.concurrent.Executors

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object Config {

  val Num    = 100
  val fibers = 16
  val cores  = 4

  // Cached EC
  val cachedEc = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(cores))

  // Work Stealing (fork-join) EC
  val workStealingEc = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(cores))

  def performCalc(i: Int): Long = (i * i).toLong

  def eff(x: Int) = ZIO.effectTotal(performCalc(x))

  // Future, custom thread pool
  def futureCalc(N: Int)(implicit ec: ExecutionContext) = {
    val fut = Future.traverse(1 to N)(i => Future(performCalc(i)))
    Await.result(fut, 1000.millis).sum
  }

  // Fiber, default thread pool

  def fiberCalcSeq(N: Int) = ZIO.foreach(1 to N)(eff(_)).map(_.sum)
  def fiberCalcPar(N: Int) = ZIO.foreachParN(fibers)(1 to N)(eff(_)).map(_.sum)

  // Fiber, blocking thread pool

  def fiberCalcSeqBlock(N: Int) = blocking(ZIO.foreach(1 to N)(eff(_)).map(_.sum))
  def fiberCalcParBlock(N: Int) = blocking(ZIO.foreachParN(fibers)(1 to N)(eff(_)).map(_.sum))

  // Fiber, custom thread pool

  def fiberCalcSeqCustom(N: Int)(ec: ExecutionContext) = ZIO.foreach(1 to N)(eff(_)).map(_.sum).on(ec)
  def fiberCalcParCustom(N: Int)(ec: ExecutionContext) = ZIO.foreachParN(fibers)(1 to N)(eff(_)).map(_.sum).on(ec)

}
