sudo perf stat -d java -jar ./target/scala-2.13/bench_2.13-0.0.1-jmh.jar -w 2 -f 2 -t 4 --bm thrpt custom
