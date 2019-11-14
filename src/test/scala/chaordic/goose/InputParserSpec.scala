package chaordic.goose

import chaordic.goose.InputParser.invalidInput
import chaordic.goose.UserAction.{AddPlayer, MovePlayer, Quit}
import org.scalatest.{FlatSpec, Matchers}

class InputParserSpec extends FlatSpec with Matchers {

  "Add player with name" should "return AddPlayer input" in{
    InputParser.parseInput("add player Pippo") should be(Right(AddPlayer("Pippo")))
  }

  "Add player without name" should "return InvalidInput input" in{
    InputParser.parseInput("add player") should be(Left(invalidInput("add player")))
  }

  "Move player with valid dice" should "return MovePlayer input" in{
    InputParser.parseInput("move Pippo 1,3") should be(Right(MovePlayer("Pippo", Some((1,3)))))
  }

  "Move player without dice" should "return MovePlayer input without DiceResult" in{
    InputParser.parseInput("move Pippo") should be(Right(MovePlayer("Pippo", None)))
  }

  "Move player with first dice below 1" should "return InvalidInput" in{
    InputParser.parseInput("move Pippo -1,3") should be(Left(invalidInput("move Pippo -1,3")))
  }

  "Move player with second dice below 1" should "return InvalidInput" in{
    InputParser.parseInput("move Pippo 1,-3") should be(Left(invalidInput("move Pippo 1,-3")))
  }

  "Move player with first dice above 6" should "return InvalidInput" in{
    InputParser.parseInput("move Pippo 7,3") should be(Left(invalidInput("move Pippo 7,3")))
  }

  "Move player with second dice above 6" should "return InvalidInput" in{
    InputParser.parseInput("move Pippo 1,7") should be(Left(invalidInput("move Pippo 1,7")))
  }

  "Move player with numbers inside 1-6, but in tens" should "return InvalidInput" in{
    InputParser.parseInput("move Pippo 22,21") should be(Left(invalidInput("move Pippo 22,21")))
  }

  "Move player with only one dice" should "return InvalidInput" in{
    InputParser.parseInput("move Pippo 1") should be(Left(invalidInput("move Pippo 1")))
  }

  "Writing 'quit'" should "return Quit" in{
    InputParser.parseInput("quit") should be(Right(Quit))
  }

}
