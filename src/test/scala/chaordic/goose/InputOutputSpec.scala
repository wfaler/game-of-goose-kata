package chaordic.goose

import chaordic.goose.UserAction.MovePlayer
import org.scalatest.{FlatSpec, Matchers}

class InputOutputSpec extends FlatSpec with Matchers {
  import InputOutput._

  "Rolling the dice" should "always return a value between 1 and 6" in{
    ((1 to 100).forall(_ => {
      val diceRoll = rollDice().unsafeRunSync();
      diceRoll <= 6 && diceRoll >= 1
    })) should be(true)
  }

  "Rolling the dice" should "return a 6 at least once in 100 throws (test upper edge)" in{
    // Test is not 100% deterministic, but with a limited result space, close enough.
    ((1 to 100).exists(_ => {
      6 == rollDice().unsafeRunSync();
    })) should be(true)
  }

  "Rolling the dice" should "return a 1 at least once in 100 throws (test lower edge)" in{
    ((1 to 100).exists(_ => {
      1 == rollDice().unsafeRunSync();
    })) should be(true)
  }

  "Rolling the dice automatically" should "not roll the dice if values have been provided" in{
    val input = MovePlayer("pippo", Some((1,2)))
    rollDiceIfNeeded(input).unsafeRunSync() should be(input)
  }

}
