package chaordic.goose

import chaordic.goose.Game.Position
import chaordic.goose.Game.Position
import chaordic.goose.UserAction.{AddPlayer, MovePlayer}
import org.scalatest.{FlatSpec, Matchers}

class GameSpec extends FlatSpec with Matchers {


  "Add player with name" should "list the names of the players" in {
    val game = Game.newGame
    val nextRound = game.game.next(AddPlayer("Pippo"))
    nextRound.game.positions.size should be(1)
    nextRound.output should be("players: Pippo")
  }

  "Add another player with name" should "list the names of the players in order" in {
    val game = Game.newGame
    val nextRound = game.game.next(AddPlayer("Pippo")).game.next(AddPlayer("Pluto"))
    nextRound.game.positions.size should be(2)
    nextRound.output should be("players: Pippo, Pluto")
  }

  "Add a duplicate player" should "should return an error message" in {
    val game = Game.newGame
    val nextRound = game.game.next(AddPlayer("Pippo")).game.next(AddPlayer("Pippo"))
    nextRound.game.positions.size should be(1)
    nextRound.output should be("Pippo: already existing player")
  }

  "Move player from Start" should "should show the movement with 'from Start' in the origin square text" in {
    val game = Game.newGame
    val nextRound = game.game.next(AddPlayer("Pippo")).game.next(MovePlayer("Pippo", Some((3, 4))))
    nextRound.game.positions.size should be(1)
    nextRound.game.positions(0).square should be(7)
    nextRound.output should be("Pippo rolls 3, 4. Pippo moves from Start to 7")
  }

  "Move player from other square" should "should show the movement" in {
    val game = Game.newGame
    val nextRound = game.game.next(AddPlayer("Pippo")).
      game.next(MovePlayer("Pippo", Some((2, 5)))).
      game.next(MovePlayer("Pippo", Some((2, 2))))

    nextRound.game.positions.size should be(1)
    nextRound.game.positions(0).square should be(11)
    nextRound.output should be("Pippo rolls 2, 2. Pippo moves from 7 to 11")
  }

  "If a player hits exactly square 63, they" should "win the game" in {
    val game = Game(Position("Pippo", 60) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((1, 2))))

    nextRound.game.positions.size should be(1)
    nextRound.output should be("Pippo rolls 1, 2. Pippo moves from 60 to 63. Pippo Wins!!")
  }

  "If a player goes above square 63, they" should "bounce back by the overshoot in their dice" in {
    val game = Game(Position("Pippo", 60) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((3, 2))))

    nextRound.game.positions.size should be(1)
    nextRound.output should be("Pippo rolls 3, 2. Pippo moves from 60 to 63. Pippo bounces! Pippo returns to 61")
  }

  "If a player goes to square 6, The Bridge, they " should "automatically move to square 12" in {
    val game = Game.newGame
    val nextRound = game.game.next(AddPlayer("Pippo")).
      game.next(MovePlayer("Pippo", Some((2, 4))))

    nextRound.game.positions.size should be(1)
    nextRound.game.positions(0).square should be(12)

    nextRound.output should be("Pippo rolls 2, 4. Pippo moves from Start to The Bridge. Pippo jumps to 12")
  }

  "If a player goes to 'the Goose', they" should "should jump forward" in {
    val game = Game(Position("Pippo", 3) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((1, 1))))

    nextRound.game.positions.size should be(1)
    nextRound.game.positions(0).square should be(7)
    nextRound.output should
      be("Pippo rolls 1, 1. Pippo moves from 3 to 5, The Goose. Pippo moves again and goes to 7")
  }

  "If a player goes to 'the Goose' and their jump gets them to the next Goose, they" should
    "do a double jump" in {
    val game = Game(Position("Pippo", 10) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((2, 2))))

    nextRound.game.positions.size should be(1)
    nextRound.game.positions(0).square should be(22)
    nextRound.output should
      be("Pippo rolls 2, 2. Pippo moves from 10 to 14, The Goose. " +
        "Pippo moves again and goes to 18, The Goose. Pippo moves again and goes to 22")
  }

  "If a player lands on the same position as another user, they" should
    "send their competitor back to their previous location." in {
    val game = Game(Position("Pippo", 15) :: Position("Pluto", 17) :: Nil)

    val nextRound = game.next(MovePlayer("Pippo", Some((1, 1))))

    val pipposPosition = nextRound.game.positions.find(p => p.player == "Pippo").map(_.square).getOrElse(0)
    val plutosPosition = nextRound.game.positions.find(p => p.player == "Pluto").map(_.square).getOrElse(0)

    pipposPosition should be(17)
    plutosPosition should be(15)

    nextRound.output should
      be("Pippo rolls 1, 1. Pippo moves from 15 to 17. On 17 there is Pluto, who returns to 15")
  }


  "If a player bounces at 63 and hits another player, they" should
    "send their competitor back to their previous location." in {
    val game = Game(Position("Pippo", 58) :: Position("Pluto", 60) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((5, 3))))

    val pipposPosition = nextRound.game.positions.find(p => p.player == "Pippo").map(_.square).getOrElse(0)
    val plutosPosition = nextRound.game.positions.find(p => p.player == "Pluto").map(_.square).getOrElse(0)

    pipposPosition should be(60)
    plutosPosition should be(58)

    nextRound.output should
      be("Pippo rolls 5, 3. Pippo moves from 58 to 63. Pippo bounces! Pippo returns to 60. " +
        "On 60 there is Pluto, who returns to 58")
  }

  "If a player hits the Goose and hits another player, they" should
    "send their competitor back to their previous location." in {
    val game = Game(Position("Pippo", 0) :: Position("Pluto", 36) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((5, 4))))
    val pipposPosition = nextRound.game.positions.find(p => p.player == "Pippo").map(_.square).getOrElse(0)
    val plutosPosition = nextRound.game.positions.find(p => p.player == "Pluto").map(_.square).getOrElse(0)

    pipposPosition should be(36)
    plutosPosition should be(0)
    println(nextRound.output)

    nextRound.output should
      be("Pippo rolls 5, 4. Pippo moves from Start to 9, The Goose. Pippo moves again and goes to 18, The Goose. " +
        "Pippo moves again and goes to 27, The Goose. Pippo moves again and goes to 36. On 36 there is Pluto, " +
        "who returns to Start")
  }

  "If a player bounces at 63 and hits his old position, they" should
    "just remain at their old position." in {
    val game = Game(Position("Pippo", 58) :: Position("Pluto", 60) :: Nil)
    val nextRound = game.next(MovePlayer("Pippo", Some((5, 5))))

    val pipposPosition = nextRound.game.positions.find(p => p.player == "Pippo").map(_.square).getOrElse(0)
    val plutosPosition = nextRound.game.positions.find(p => p.player == "Pluto").map(_.square).getOrElse(0)

    pipposPosition should be(58)
    plutosPosition should be(60)

    nextRound.output should
      be("Pippo rolls 5, 5. Pippo moves from 58 to 63. Pippo bounces! Pippo returns to 58")
  }
}