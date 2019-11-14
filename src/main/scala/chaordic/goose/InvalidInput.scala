package chaordic.goose


/**
 * Represents an error parsing user Input.
 * Typically given as a Left() from InputParser.
 */
case class InvalidInput(msg: String)
