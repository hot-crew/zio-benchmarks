package bench.future

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import Config._

import zio.{ Runtime }

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class FutureFiberSimpleBenchmark {
  val rt = Runtime.default

  @Benchmark
  def futureBench() = Config.futureCalc(Config.Num)

  @Benchmark
  def fiberBench() = rt.unsafeRun(fiberCalc(Config.Num))

}
