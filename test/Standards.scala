/**
 * Created by cwx on 15-12-2.
 */
object Standards {
  def isInt(x: Any): Boolean = x match {
    case p: Int => true
    case _ => false
  }

  def maxOfList[T] (elements: List[T])
                   (implicit orderer: T => Ordered[T]): T =
    elements match {
      case List() =>
        throw new IllegalAccessException("empty list!")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxOfList(rest) (orderer)
        if (orderer(x) > maxRest) x
        else maxRest
    }

  def quick(list: List[Int]): List[Int] = {
    list match {
      case Nil => Nil
      case x :: xs =>
        val (before, after) = xs partition(_ < x)
        quick(before) ++ (x :: quick(after))

    }
  }

  def main(args: Array[String]): Unit ={
    val as = List(1, 10, 9, 4, 5)
    val a = maxOfList(as)
    val b = quick(as)
    println(a)
    println(b)
  }
}
