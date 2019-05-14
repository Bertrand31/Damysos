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
    println("============================")
    println(s"Profiling $id:")
    def print_result(s: String, ns: Long) = {
      val format = java.text.NumberFormat.getIntegerInstance.format(_: Long)
      println(s.padTo(16, " ").mkString + format(ns) + " ns")
    }

    var t0 = System.nanoTime()
    var result = block
    var t1 = System.nanoTime()

    print_result("Cold run", (t1 - t0))

    val runtimes = (1 to 10).map(i => {
      t0 = System.nanoTime()
      result = block
      t1 = System.nanoTime()
      (t1 - t0).toLong
    })

    print_result("Max", runtimes.max)
    print_result("Min", runtimes.min)
    val avg = runtimes.sum / runtimes.length
    print_result("Avg", avg)
    avg
  }
}
