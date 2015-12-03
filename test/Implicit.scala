/**
 * Created by cwx on 15-12-2.
 */
class Implicit {

}

class Test {

}

class RunTest(test: Test) {
  def run = {
    println("RunTest-->test =" + test)
  }
}

object Implicit extends App {
  implicit def test2RunTest(test: Test) = new RunTest(test)
  val test = new Test
  test.run
  def testParam(implicit name: String): Unit = {
    println("name =" + name)
  }
  implicit val name = "Implicited..."
  testParam
  implicit class Calc(x: Int) {
    def add(a: Int) =a + x
  }
  println("1.add(2) = " + Calc(1).add(2))
}