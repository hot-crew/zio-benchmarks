package bench.future

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import Config._

import zio.{ Runtime }

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class FutureFiberSimpleBenchmark {
  private val rt = Runtime.default

  @Param(Array("10", "20"))
  var num: Int = _

  @Benchmark
  def futureBench() = Config.futureCalc(num)(workStealingEc)

  // Default thread pool

  // Seq
  @Benchmark
  def fiberCalcSeqBench() = rt.unsafeRun(fiberCalcSeq(num))

  // Par
  @Benchmark
  def fiberCalcParBench() = rt.unsafeRun(fiberCalcPar(num))

  // blocking thread pool

  // Seq
  @Benchmark
  def fiberCalcSeqBlockBench() = rt.unsafeRun(fiberCalcSeqBlock(num))

  // Par
  @Benchmark
  def fiberCalcParBlockBench() = rt.unsafeRun(fiberCalcParBlock(num))

  // work stealing thread pool

  // Seq
  @Benchmark
  def fiberCalcSeqWSTPBench() = rt.unsafeRun(fiberCalcSeqCustom(num)(workStealingEc))

  // Par
  @Benchmark
  def fiberCalcParWSPPBench() = rt.unsafeRun(fiberCalcParCustom(num)(workStealingEc))

  // cached thread pool

  // Seq
  @Benchmark
  def fiberCalcSeqCachedTPBench() = rt.unsafeRun(fiberCalcSeqCustom(num)(cachedEc))

  // Par
  @Benchmark
  def fiberCalcParCachedTPBench() = rt.unsafeRun(fiberCalcParCustom(num)(cachedEc))

}
