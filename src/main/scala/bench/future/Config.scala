package bench.future

import java.util.concurrent.Executors

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

import zio.{ ZIO }
import zio.blocking.blocking

object Config {

  val Num    = 100
  val fibers = 16
  val cores  = 4

  // Cached EC
  val cachedEc = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  // Work Stealing (fork-join) EC
  val workStealingEc = ExecutionContext.fromExecutor(Executors.newWorkStealingPool(cores))

  // Scheduled TP EC
  val schedTP = ExecutionContext.fromExecutor(Executors.newScheduledThreadPool(cores))

  def factorial(n: Long): Long = {
    @tailrec
    def factorialAccumulator(acc: Long, n: Long): Long =
      if (n == 0) acc
      else factorialAccumulator(n * acc, n - 1)
    factorialAccumulator(1, n)
  }

  def eff(x: Int) = ZIO.effectTotal(factorial(x.toLong))

  // Future, custom thread pool
  def futureCalc(N: Int)(implicit ec: ExecutionContext) = {
    val fut = Future.traverse(1 to N)(i => Future(factorial(i.toLong)))
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
