package chaordic.goose

/**
 * A `GameRound` holds the state of the game after the last user action +
 * the textual output from said action to be conveyed back to a user.
 */
sealed trait GameRound{
  def output: String
  def game: Game
}
case class GameWon(winningPlayer: String, game: Game, output: String) extends GameRound
case class ContinueGame(game: Game, output: String) extends GameRound
case class QuitGame(game: Game) extends GameRound {
  val output = "Quitting game, bye!"
}
