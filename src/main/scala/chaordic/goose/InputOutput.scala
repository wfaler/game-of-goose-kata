package chaordic.goose


import cats.effect.IO
import chaordic.goose.UserAction.MovePlayer
import org.jline.reader.{LineReader, LineReaderBuilder}

import scala.util.Random

/**
 * Functions for IO.
 * Technically in pure FP, reading from StdIn, writing to StdOut and using Random seeds
 * are considered side-effects, therefore, they live here and are appropriately wrapped in cats.effect.IO.
 *
 * From a practical point of view, IO allows us stack-safe recursion (through trampolining in the IO-implementation),
 * which in turn allows us to use immutability & recursion instead of mutable variables for state.
 */
object InputOutput {
  // Use JLine for an interactive prompt
  private val reader: LineReader = LineReaderBuilder.builder.build()

  def printLn(msg: String): IO[Unit] =
    IO(Predef.println(msg))

  def readLine(): IO[String] =
    IO(reader.readLine("$ "))

  def rollDice(): IO[Int] =
    IO((new Random().nextInt(6)) + 1)

  /**
   * Fill a MovePlayer without dice results if it exists, otherwise, just return
   */
  def rollDiceIfNeeded(input: UserAction): IO[UserAction] = {
    input match {
      case MovePlayer(p, None) => for {
        d1 <- rollDice()
        d2 <- rollDice()
      } yield {
        MovePlayer(p, Some((d1, d2)))
      }
      case x => IO.pure(x)
    }
  }

  /**
   * Reads and parses the next user input.
   * If the input is invalid, try self again.
   *
   * @return IO[UserAction]
   */
  def readNextInput(): IO[UserAction] = {
    for {
      in <- readLine()
      parsedInput = InputParser.parseInput(in)
      movedInput <- parsedInput.fold(x => {
        for {
          _ <- printLn(x.msg)
          in <- readNextInput()
        } yield {
          in
        }
      }, in => rollDiceIfNeeded(in))
    } yield {
      movedInput
    }
  }

}
