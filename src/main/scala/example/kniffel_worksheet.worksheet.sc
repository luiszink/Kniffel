// Method to roll a single dice and return its icon as a string
def rollDice(): String = {
  val randomNumber = scala.util.Random.nextInt(6) + 1
  List("1", "2", "3", "4", "5", "6")(randomNumber - 1)
}

// Method to display the current values of all dice
def displayDiceValues(diceValues: List[String]): Unit = {
  val horizontalLine = "+" + List.fill(diceValues.length)("---").mkString("+") + "+"
  val diceIconsLine = "|" + diceValues.map(value => s" $value ").mkString("|") + "|"

  println(horizontalLine)
  println(diceIconsLine)
  println(horizontalLine)
}

// Roll the dice and display their values
val diceValues = List.fill(5)(rollDice())
displayDiceValues(diceValues)