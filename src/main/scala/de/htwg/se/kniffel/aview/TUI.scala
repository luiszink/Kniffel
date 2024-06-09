  package de.htwg.se.kniffel.aview

  import scala.io.StdIn
  import de.htwg.se.kniffel.controller.Controller
  import de.htwg.se.kniffel.util.Observer
  import scala.util.{Try, Success, Failure}

  class TUI(controller: Controller) extends Observer {
    controller.add(this)

    def addPlayers(): Unit = {
      println("Enter player names (comma-separated):")
      val input = StdIn.readLine()
      val names = input.split(",").map(_.trim)
      names.foreach(controller.addPlayer)
    }

    def selectScoreUpdater(): Unit = {
    println("Du you want to allow multible Kniffel? (y/n)")
    val input = StdIn.readLine()
    controller.setScoreUpdater(input)
    }

    def run() = {
      addPlayers()
      selectScoreUpdater()
      println(printDice())
      var running = true
      while (running) {
        controller.getCurrentState.name match {
          case "updateState" => 
            printScoreCard()
            println("Enter category (e.g., One, Fullhouse...!):")
            val input = StdIn.readLine()
            controller.handleInput(input)
            printDice()

          case _ =>
            printDice()
            println(s"Enter the indices of the dice you want to keep (e.g., 1 3 5), or Enter category (e.g., One, Fullhouse...) (${controller.repetitions} remaining), or 'undo' to undo last score update:")
            val input = StdIn.readLine()
            controller.handleInput(input)
        }
      }
    }

    override def update(message: String): Unit = {
      message match {
        case "printDice" => println(printDice())
        case "printDiceUndo" => println(printDiceUndo())
        case "printScoreCard" => println(printScoreCard())
        case "playerAdded" => println("") 
        case _ => println("wrong notifyObservers!!!!!!!!!!!!!!")
      }
    }

    def printDiceUndo() = {
      val diceValues: List[Int] = controller.getPreviousDice
      val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
      val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
      val numCounter = " " + diceValues.indices.map(index => s" ${index + 1} ").mkString(" ") + " "

      s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter\n"
    }

    def printDice() = {
      val diceValues: List[Int] = controller.getDice
      val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
      val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"
      val numCounter = " " + diceValues.indices.map(index => s" ${index + 1} ").mkString(" ") + " "

      s"Current Player: ${controller.getCurrentPlayer}\n$horizontalLine\n$diceIconsLine\n$horizontalLine\n$numCounter\n"
    }

    def printScoreCard() = {
      val currentPlayer = controller.getCurrentPlayer
      val scoreCard = currentPlayer.scoreCard.categories.map {
        case (category, score) => s"$category: ${score.getOrElse("_")}"
      }.mkString("\n")
      s"\nCurrent Player: ${currentPlayer.name}\nScoreCard:\n$scoreCard\n"
    }

    /*
    def updateScore(): Unit = {
      println("Enter category (e.g., One, Fullhouse...):")
      val input = StdIn.readLine()
      Try(controller.updateScore(input)) match {
        case Success(_) => // Nichts weiter tun, wenn erfolgreich
        case Failure(e: IllegalArgumentException) =>
          println("Not a category! Please try again.")
          updateScore()
      }
    }
    */

    def displayMessage(message: String): Unit = {
      println(message)
    }
  }