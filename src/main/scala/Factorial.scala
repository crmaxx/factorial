import java.util.concurrent.Executors

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object Factorial extends App {
  val startTime = System.nanoTime
  val factorials = List(22, 22, 22, 52, 52, 52, 100, 100, 10500, 100, 100, 100, 500, 500, 500, 600, 600, 600)

  val numberOfThreads = 16
  val pool = Executors.newFixedThreadPool(numberOfThreads)
  implicit val ec = ExecutionContext.fromExecutorService(pool)

  implicit val system = ActorSystem("factorial")

  val collector = system.actorOf(Props(new FactorialCollector(factorials)), "collector")

  system.awaitTermination()
  pool.shutdown()

  val elapsedTime = (System.nanoTime - startTime) / 1000000000.0
  println(s"Elapsed time: $elapsedTime s")
}

class FactorialCollector(factorials: List[Int]) extends Actor with ActorLogging {
  var list: List[BigInt] = Nil
  var size = factorials.size

  for (num <- factorials) {
    context.actorOf(Props(new FactorialCalculator)) ! num
  }

  def receive = {
    case (num: Int, fac: BigInt) =>
      log.info(s"factorial for $num is $fac")

      list = num :: list
      size -= 1

      if (size == 0) context.system.shutdown()
  }
}

class FactorialCalculator extends Actor {
  def receive = {
    case num: Int => sender ! (num, factor(num))
  }

  private def factor(num: Int) = factorTail(num, 1)

  @tailrec private def factorTail(num: Int, acc: BigInt): BigInt = {
    (num, acc) match {
      case (0, a) => a
      case (n, a) => factorTail(n - 1, acc = n * a)
    }
  }
}
