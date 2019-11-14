package chaordic.goose

import chaordic.goose.UserAction.{AddPlayer, MovePlayer, Quit}

import scala.util.parsing.combinator.RegexParsers

/**
 * Parses user input using Scala's Parser Combinators.
 * As the name implies, they allow us to build bigger parsers from combining/composing smaller ones.
 * Safer and less error-prone than hand-wrangled String parsing/manipulation.
 */
object InputParser extends RegexParsers {
  private val allParsers: List[Parser[UserAction]] =
    addPlayerInput :: userMoveInput :: moveNoDiceInput :: quitInput :: Nil

  def word: Parser[String]   = """[A-Za-z]+""".r       ^^ { _.toString }
  def comma: Parser[String]   = """[,]+""".r       ^^ { _.toString }
  def number: Parser[Int]    = """(?:1|2|3|4|5|6)""".r ^^ { _.toInt}
  def move: Parser[String] = "move".r ^^ { _.toString }
  def add: Parser[String] = "add".r ^^ { _.toString }
  def player: Parser[String] = "player".r ^^ { _.toString }
  def quit: Parser[String] = "quit".r ^^ { _.toString }
  def eol: Parser[String] = "$".r ^^ { _.toString }

  def quitInput: Parser[UserAction] = quit ~ eol  ^^ {
    case _ ~ _ => {
      Quit
    } }

  def userMoveInput: Parser[UserAction] = move ~ word ~ number ~ comma ~ number ~ eol  ^^ {
    case _ ~ wd ~ dice1 ~ _ ~ dice2 ~ _ => MovePlayer(wd, Some((dice1, dice2)))
  }

  def moveNoDiceInput: Parser[UserAction] = move ~ word ~ eol ^^ {
    case _ ~ wd ~ _ =>  MovePlayer(wd, None)
  }
  def addPlayerInput: Parser[UserAction] = add ~ player ~ word ~ eol  ^^ {
    case _ ~ _ ~ wd ~ _ => AddPlayer(wd)
  }

  def invalidInput(str: String) = InvalidInput(s""""$str" is invalid as input""")

  private def parseInput(str: String, parsers: List[Parser[UserAction]]): Either[InvalidInput, UserAction] = {
    parsers match{
      case Nil => Left(invalidInput(str))
      case head :: tail => {
        parse(head, str) match{
          case Success(matched, _) => Right(matched)
          case _ => parseInput(str, tail)
        }
      }
    }
  }

  def parseInput(str: String): Either[InvalidInput, UserAction] =
    InputParser.parseInput(str, InputParser.allParsers)
}


