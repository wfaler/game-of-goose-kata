package chaordic.goose

import chaordic.goose.Game.Position
import chaordic.goose.UserAction.{AddPlayer, MovePlayer, Quit}

case class Game(positions: List[Position]) {
  type TargetSquare = Int

  /**
   * 'The Goose' and 'The Bridge' are effectively special cases that can be represented as a function
   * from: the square itself and the move, to a tuple of the special output &
   * newly calculated target square for the player.
   */
  type SpecialSquare = (TargetSquare, MovePlayer) => (String, TargetSquare)

  private val theBridge: SpecialSquare = (_: TargetSquare, m: MovePlayer) => {
    (s"The Bridge. ${m.playerName} jumps to 12", 12)
  }

  private val theGoose: SpecialSquare = (t: TargetSquare, m: MovePlayer) => {
    val newTarget = t + m.moveSize
    val output = s"$t, The Goose. ${m.playerName} moves again and goes to"
    specialSquares.get(newTarget).map(sq => {
      val (out, next) = sq(newTarget, m)
      (output + " " + out, next)
    }).getOrElse({
      (output + s" ${newTarget}", newTarget)
    })
  }

  private val specialSquares = Map[Int, SpecialSquare](
    5 -> theGoose,
    6 -> theBridge,
    9 -> theGoose,
    14 -> theGoose,
    18 -> theGoose,
    23 -> theGoose,
    27 -> theGoose
  )

  private def replacePosition(pos: Position): Game = {
    Game(positions.map(p => if (p.player == pos.player) pos else p))
  }

  private def gameHasPlayer(name: String): Boolean =
    positions.exists(p => p.player.toLowerCase() == name.toLowerCase())

  private def playerExists(name: String)(ifExists: (Position) => GameRound): GameRound =
    positions.find(p => p.player.toLowerCase() == name.toLowerCase()).map(ifExists).getOrElse({
      ContinueGame(this, s"Player $name does not exist, invalid input")
    })

  private def prank(previousSquare: Int, finalTarget: Int, playerName: String): (String, Game) = {
    val squareName = if(previousSquare == 0) "Start" else s"$previousSquare"
    positions.find(p => p.square == finalTarget && p.player != playerName).map(p =>{
      (s". On $finalTarget there is ${p.player}, who returns to $squareName", this.replacePosition(p.copy(square = previousSquare)))
    }).getOrElse({
      ("", this)
    })
  }

  /**
   * Takes a UserAction and returns the next GameRound with updated Game-state and output.
   */
  def next(userAction: UserAction): GameRound = {
    userAction match {
      case Quit => QuitGame(this)
      case AddPlayer(player) => {
        if (gameHasPlayer(player)) {
          ContinueGame(this, s"$player: already existing player")
        } else {
          val newGame = Game((Position(player, 0) :: positions).reverse)
          ContinueGame(newGame, s"players: ${newGame.positions.map(_.player).mkString(", ")}")
        }
      }
      case MovePlayer(name, Some((d1, d2))) => {
        playerExists(name)(pos => {
          val mv = MovePlayer(name, Some((d1, d2)))
          val targetSquare = pos.square + d1 + d2
          val startPosition = if (pos.square == 0) "Start" else s"${pos.square}"
          val (endPosition, finalTarget) = specialSquares.get(targetSquare).map(_ (targetSquare, mv)).getOrElse({
            (s"${targetSquare}", targetSquare)
          })
          val output = s"$name rolls $d1, $d2. $name moves from $startPosition to $endPosition"

          if (finalTarget > 63) {
            val newTarget = 63 - (finalTarget - 63)
            val (prankText, postPrankState) = prank(pos.square, newTarget, pos.player)
            val bounceOutput = s"$name rolls $d1, $d2. $name moves from $startPosition to 63. $name bounces! $name returns to $newTarget"
            ContinueGame(postPrankState.replacePosition(Position(pos.player, newTarget)), bounceOutput + prankText)
          } else if (finalTarget == 63) {
            GameWon(pos.player, this.replacePosition(Position(pos.player, finalTarget)), output + s". ${name} Wins!!")
          } else {
            val (prankText, postPrankState) = prank(pos.square, finalTarget, pos.player)
            ContinueGame(postPrankState.replacePosition(Position(pos.player, finalTarget)), output + prankText)
          }
        })
      }
      case _ => ContinueGame(this, s"This is an invalid state. If this is ever hit, there is a bug.")
    }
  }

}

object Game {
  def newGame = ContinueGame(Game(Nil), "")

  case class Position(player: String, square: Int)
}
