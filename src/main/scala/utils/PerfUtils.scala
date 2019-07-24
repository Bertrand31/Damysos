package utils

object PerfUtils {

  def time[A](id: String)(block: => A): A = {
    val startTime = System.nanoTime
    val result = block
    val elapsedMicro = Math.round((System.nanoTime() - startTime) / 1000)
    val elapsedStr = if (elapsedMicro > 5000) s"${elapsedMicro / 1000}ms" else s"${elapsedMicro}us"
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
    val sortedHotRuns = runtimes.drop(Math.floor(runtimes.length / 2).toInt).sorted
    print_result("Max hot", sortedHotRuns.last)
    print_result("Min hot", sortedHotRuns.head)
    val median = sortedHotRuns(Math.floor(sortedHotRuns.length / 2).toInt)
    print_result("Med hot", median)
    median
  }
}
