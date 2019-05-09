package utils

object PerfUtils {

  def time[A](id: String)(block: => A): A = {
    val startTime = System.nanoTime()
    val result = block
    val elapsed = Math.round((System.nanoTime() - startTime) / Math.pow(1000, 2))
    println(s"$id took ${elapsed}ms")
    result
  }
}
