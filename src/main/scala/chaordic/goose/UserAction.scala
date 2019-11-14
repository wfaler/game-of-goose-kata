package chaordic.goose

sealed trait UserAction

object UserAction{
  type DiceRollResult = (Int, Int)

  case class AddPlayer(playerName: String) extends UserAction
  case class MovePlayer(playerName: String, dice: Option[DiceRollResult]) extends UserAction{
    def moveSize: Int = dice.map(t => t._1 + t._2).getOrElse(0)
  }
  case object Quit extends UserAction
}

