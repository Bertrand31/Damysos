import org.scalatest.FlatSpec
import utils.StringUtils.StringImprovements

class StringUtilsSpec extends FlatSpec {

  behavior of "the padLeft method on String"

  it should "pad a string to its left with a character until it reaches the required length" in {

    val padded = "foo".padLeft(5, 'a')
    assert(padded === "aafoo")
  }

  it should "leave a string untouched when it is the required length" in {

    val padded = "foo".padLeft(3, 'a')
    assert(padded === "foo")
  }

  it should "leave a string untouched when it is shorter than the required length" in {

    val padded = "foo".padLeft(2, 'a')
    assert(padded === "foo")
  }
}
