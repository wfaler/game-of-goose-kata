package chaordic.goose

import cats.effect.IO
import chaordic.goose.UserAction.{AddPlayer, MovePlayer}
import org.scalatest.{FlatSpec, Matchers}

class MainSpec extends FlatSpec with Matchers {

  "Given the game, two players and alternating simulated inputs, the app" should "eventually determine a winner" in{
    val initialState = Game.newGame.game.next(AddPlayer("Pippo")).game.next(AddPlayer("Pluto"))

    var state = true
    def randomMove(): IO[UserAction] = {
      if(state){
        state = false
        InputOutput.rollDiceIfNeeded(MovePlayer("Pippo", None))
      }else{
        state = true
        InputOutput.rollDiceIfNeeded(MovePlayer("Pluto", None))
      }
    }

    val hasWinner = Main.loop(initialState, () => randomMove()).unsafeRunSync() match{
      case GameWon(_,_,_) => true
      case _ => false
    }
    hasWinner should be(true)
  }

}
