package chaordic.goose

import cats.effect.IO

/**
 * Application entrypoint and main application loop.
 */
object Main extends App{
  import chaordic.goose.InputOutput._

  def help(): IO[Unit] = printLn("""Valid commands are:
                                 |"quit"
                                 |"add player [player name]"
                                 |"move [player name] [dice1], [dice2]"
                                 |"move [player name]" (game rolls dice for you)
                                 |""".stripMargin)

  def intro(): IO[Unit] = {
    for{
      _ <- printLn("Welcome to the Goose Game!")
      _ <- help()
      _ <- printLn("Start by adding a player!")
    }yield{}
  }

  /**
   * We use a recursive loop to represent the passing game to avoid mutable state.
   *
   * Cats IO is "trampolined" in its flatMap evaluation, which means it is stack-safe,
   * even in the absence of being able to use Scala's @tailrec annotation.
   * See: https://typelevel.org/cats-effect/datatypes/io.html#stack-safety
   */
  def loop(nextRound: GameRound, getInput: () => IO[UserAction]): IO[GameRound] = {
    for{
      _ <- printLn(nextRound.output)
      endState <- {
        nextRound match{
          case ContinueGame(game, _) => {
            for{
              input <- getInput()
              end <- loop(game.next(input), getInput)
            }yield{
              end
            }
          }
          case _ => IO.pure(nextRound)
        }
      }
    }yield{
      endState
    }
  }

  val application: IO[Unit] = for{
    _ <- intro()
    _ <- loop(Game.newGame, () => readNextInput())
  }yield{

  }

  application.unsafeRunSync()
}
