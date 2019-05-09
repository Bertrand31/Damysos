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
}
