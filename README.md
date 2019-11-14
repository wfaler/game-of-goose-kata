# The Game of Goose Kata
This repository contains a Scala implementation of the [Goose Game Kata](https://github.com/xpeppers/goose-game-kata). It implements all the requirements, mandatory and optional, see [`src/test/scala/chaordic/goose/GameSpec.scala`](src/test/scala/chaordic/goose/GameSpec.scala) for test coverage of all cases (there are other tests as well, but game logic is contained in this test).

This code is released under a [MIT License](LICENSE.md).

## Installation & running instructions

 * A recent version of [SBT](https://www.scala-sbt.org/) is required.
 * Run `sbt`
 * To run the application from sbt, simply type `run` at the prompt after starting sbt.
 * To run as a standalone executable jar:
    * Build with `assembly` through the sbt interactive prompt, then
    * Exit sbt
    * Run `java -jar target/scala-2.12/game-of-goose.jar`
 * To run the test-suite, run `test` from the sbt prompt.
 
 ## Comments on the implementation
 This implementation uses a purely functional style using [Cats](https://typelevel.org/cats/) `IO`-monad.
 This gives us a couple of benefits:
 * Side-effecting code is clearly separated from pure code by return type-signature.
 * `IO` allows us to use stack-safe recursion ([more](https://typelevel.org/cats-effect/datatypes/io.html#stack-safety)).
 * Which in turn allows us to implement the entire application immutably with an event-loop, with no mutable state whatsoever.
    * Immutability has clear benefits in reducing the potential number of bugs.
 
Furthermore, we use Scalas [Parser Combinators](https://github.com/scala/scala-parser-combinators) to safely 
parse user input in a less error-prone way than hand-munging `Strings` at no extra code-weight.
 
Finally, we use [JLine3](https://github.com/jline/jline3) to give a more pleasant experience when entering game commands.
 
The application has comprehensive test-coverage (100% branch coverage), which can be inspected with the coverage plugin:
    run `coverageOn`, followed by `clean`, `test` and `coverageReport` in sbt to generate the report.

There are many ways this could be implemented, this is one flavour of it!