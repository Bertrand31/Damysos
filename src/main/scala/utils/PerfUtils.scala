package utils

object PerfUtils {

  def time[A](id: String)(block: => A): A = {
    val startTime = System.nanoTime
    val result = block
    val elapsedMicro = Math.round((System.nanoTime() - startTime) / 1000)
    val elapsedStr = if (elapsedMicro > 5000) elapsedMicro / 1000 + "ms" else elapsedMicro + "us"
    println(s"$id took $elapsedStr")
    result
  }

  def profile(id: String)(block: => Any): Long = {
    def print_result(s: String, ns: Long): Unit = {
      val format = java.text.NumberFormat.getIntegerInstance.format(_: Long)
      println(s.padTo(16, " ").mkString + format(ns) + " ns")
    }

    var tmpTime = 0L
    val runtimes = (0 to 50000).map(_ => {
      tmpTime = System.nanoTime
      block
      System.nanoTime - tmpTime
    })

    println("============================")
    println(s"Profiling $id:")


    print_result("Cold run", runtimes.head)
    val hotRuns = runtimes.drop(runtimes.length / 2)
    print_result("Max hot", hotRuns.max)
    print_result("Min hot", hotRuns.min)
    val avg = hotRuns.sum / hotRuns.length
    print_result("Avg hot", avg)
    avg
  }
}
