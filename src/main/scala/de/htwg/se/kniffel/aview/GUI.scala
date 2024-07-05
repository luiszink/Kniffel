package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.ControllerInterface
import de.htwg.se.kniffel.util.{Observer, KniffelEvent}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{Pane, VBox, HBox, StackPane, Priority, BorderPane}
import scalafx.scene.control._
import scalafx.beans.property.{StringProperty, IntegerProperty}
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import scalafx.stage.Screen
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.{Image, ImageView}
import scala.compiletime.uninitialized
import scalafx.Includes.jfxRectangle2D2sfx
import scalafx.scene.control.CheckBox
import scalafx.scene.control.TextField
import java.nio.file.Paths
import com.google.inject.Inject
import scalafx.scene.paint.Color
import scalafx.scene.text.Font
import scalafx.scene.layout.AnchorPane
import scala.util.Random
import java.util.{Timer, TimerTask}

class GUI @Inject() (controller: ControllerInterface)
    extends JFXApp3
    with Observer {
  controller.add(this)

  var tableView: TableView[(String, String)] = uninitialized
  var rollButton: Button = uninitialized
  var diceImageViews: Seq[ImageView] = uninitialized
  var updateCategoryButton: Button = uninitialized
  var selectedCategory: String = ""
  var playerNameFields: Seq[TextField] = uninitialized
  var selectedDiceIndices: Set[Int] = Set()
  var multipleKniffelCheckBox: CheckBox = uninitialized
  var repetitionsLabel: Label = uninitialized
  val windowWidth: Double = 800
  val windowHeight: Double = 600

  override def update(event: KniffelEvent.Value): Unit = {
    Platform.runLater {
      event match {
        case KniffelEvent.PlayerAdded       => scorecard()
        case KniffelEvent.keepDice          => updateDiceResults()
        case KniffelEvent.Undo              =>
          scorecard()
        case KniffelEvent.setScoreUpdater   =>
          startSync()
          scorecard()
        case KniffelEvent.NextPlayer        =>
          scorecard()
          updateDiceResults()
          resetSelectedDice()
          rollButton.disable = false
        case KniffelEvent.noRepetitions     => rollButton.disable = true
        case KniffelEvent.GameEnded         => showEndScene()
        case _                              =>
      }
    }
  }

  override def start(): Unit = {
    try {
      stage = new JFXApp3.PrimaryStage {
        title = "Kniffel"
        resizable = true
        onCloseRequest = _ => {
          controller.saveCurrentState()
          sys.exit(0)
        }
        scene = startScene(windowWidth, windowHeight)
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def startScene(width: Double, height: Double): Scene = {
    new Scene(width, height) {
      stylesheets.add("file:src/main/resources/style.css")
      val pane = new StackPane() {
        id = "main-pane"
        prefWidth = windowWidth
        prefHeight = windowHeight
      }
      playerNameFields = createPlayerNameFields(4)
      val confirmButton = new Button("Confirm") { id = "confirm-button" }
      confirmButton.onAction = _ => handlePlayerNames()

      multipleKniffelCheckBox = new CheckBox("Erlaube mehrere Kniffel") {
        id = "kniffel-label"
      }
      multipleKniffelCheckBox.selected = false

      val titleLabel = new Label("Kniffel") { id = "title-Label" }

      val titleBox = new HBox {
        children = Seq(titleLabel)
        alignment = Pos.Center
        padding = Insets(20)
      }
      val playerNamesVBox = new VBox(10) {
        children = playerNameFields
        padding = Insets(20)
        alignment = Pos.Center
      }
      val optionsVBox = new VBox(10) {
        children = Seq(multipleKniffelCheckBox)
        padding = Insets(20)
        alignment = Pos.Center
      }
      val confirmButtonBox = new HBox {
        children = Seq(confirmButton)
        alignment = Pos.Center
        padding = Insets(20)
      }
      val hbox = new HBox(50) {
        children = Seq(playerNamesVBox, optionsVBox)
        alignment = Pos.Center
      }
      val vbox = new VBox(30) {
        children = Seq(titleBox, hbox, confirmButtonBox)
        alignment = Pos.Center
      }
      pane.children = vbox
      content = pane
    }
  }

  def gameScene(width: Double, height: Double): Scene = {
    new Scene(width, height) {
      stylesheets.add("file:src/main/resources/style.css")
      val pane = new BorderPane() {
        id = "main-pane"
        prefWidth = windowWidth
        prefHeight = windowHeight
      }

      val menuBar = new MenuBar() {
        prefWidth = GUI.this.windowWidth       
      }
      val menu = new Menu("") {
        val homeImage = new ImageView(
          new Image("file:src/main/resources/home.png")
        ) {
          fitHeight = 40
          fitWidth = 40
        }
        graphic = homeImage
      }
      val undoMenuItem = new MenuItem("Undo")
      val backMenuItem = new MenuItem("Back")
      val exitMenuItem = new MenuItem("Exit")

      menu.items.addAll(undoMenuItem, backMenuItem, exitMenuItem)
      menuBar.menus.add(menu)
      pane.top = menuBar

      undoMenuItem.onAction = _ => {
        controller.handleInput("undo")
        updateDiceResults()
      }
      backMenuItem.onAction = _ => stage.scene = startScene(GUI.this.windowWidth, GUI.this.windowHeight)
      exitMenuItem.onAction = _ => Platform.exit()

      tableView = new TableView[(String, String)]() {
        id = "score-table"
        onMouseClicked = _ => handleCategorySelection()
        columnResizePolicy = TableView.ConstrainedResizePolicy
        prefHeight = 441
        prefWidth = 400
      }

      rollButton = new Button("Roll Dice") {
        id = "roll-button"
        tooltip = "Click to roll the dice"
      }

      rollButton.onAction = _ => rollDice()
      rollButton.disable = false

      updateCategoryButton = new Button("Update Category") {
        id = "update-category-button"
        tooltip = "Click to update the selected category"
      }
      updateCategoryButton.onAction = _ => updateSelectedCategory()

      diceImageViews = createDiceImageViews(5)

      repetitionsLabel = new Label(
        s"Remaining Rolls: ${controller.repetitions}"
      ) { id = "repetitions-label" }

      val dicePane = new Pane() {
        prefHeight = 200
        prefWidth = 200
      }

      diceImageViews(0).layoutX = 65
      diceImageViews(0).layoutY = 20

      diceImageViews(1).layoutX = 130
      diceImageViews(1).layoutY = 60

      diceImageViews(2).layoutX = 60
      diceImageViews(2).layoutY = 124

      diceImageViews(3).layoutX = 0
      diceImageViews(3).layoutY = 60

      diceImageViews(4).layoutX = 125
      diceImageViews(4).layoutY = 134

      dicePane.children.addAll(diceImageViews.map(_.delegate)*)

      val controlBox = new VBox(10) {
        id = "game-field"
        children = Seq(
          repetitionsLabel,
          dicePane,
          rollButton,
          updateCategoryButton
        )
        padding = Insets(20)
        alignment = Pos.Center
      }

      val hbox = new HBox(20) {
        children = Seq(tableView, controlBox)
        padding = Insets(20)
        alignment = Pos.Center
      }
      val outerVBox = new VBox(20) {
        children = Seq(hbox)
        alignment = Pos.Center
      }
      pane.center = outerVBox
      content = pane

      updateDiceResults()
    }
  }

  def showEndScene(): Unit = {
    val screenBounds = scalafx.stage.Screen.primary.visualBounds
    val sortedResults = controller.getPlayers
      .map(player => (player.name, player.getTotalScore))
      .sortBy(-_._2)

    val endScene = new Scene(windowWidth, windowHeight) {
      stylesheets.add("file:src/main/resources/style.css")
      val pane = new BorderPane() {
        id = "end-scene-pane"
        prefWidth = windowWidth
        prefHeight = windowHeight
      }

      val resultBox = new VBox {
        alignment = Pos.Center
        spacing = 20
        children = Seq(
          new Label("Spiel beendet!") {
            id = "end-scene-label-title"
          },
          new Label(s"Gewinner: ${sortedResults.head._1}") {
            id = "end-scene-label-winner"
          },
          new Label("Endergebnisse:") {
            id = "end-scene-label-results"
          },
          new ListView[String] {
            id = "end-scene-list-view"
            items = ObservableBuffer(sortedResults.map { case (name, score) =>
              s"$name: $score"
            }*)
            prefWidth = 200
            prefHeight = 150
          },
          new Button("Beenden") {
            id = "end-scene-button"
            onAction = _ => Platform.exit()
          }
        )
      }
      pane.center = resultBox
      content = pane
    }
    stage.scene = endScene
  }

  def createDiceImageViews(count: Int): Seq[ImageView] = {
    (1 to count).map { i =>
      new ImageView {
        fitHeight = 50
        fitWidth = 50
        onMouseClicked = _ => toggleDiceSelection(i)
        id = s"dice-image-$i"
      }
    }
  }

  def createPlayerNameFields(count: Int): Seq[TextField] = {
    (1 to count).map { i =>
      new TextField {
        promptText = s"Player $i Name"
        id = s"player-$i-name-field"
      }
    }
  }

  def startSync(): Unit = {
    stage.scene = gameScene(windowWidth, windowHeight)
  }

  def handlePlayerNames(): Unit = {
    val names = playerNameFields.map(_.text.value.trim).filter(_.nonEmpty)
    names.foreach(controller.addPlayer)
    val multipleKniffelAllowed = multipleKniffelCheckBox.selected.value
    val scoreUpdaterType = if (multipleKniffelAllowed) "y" else "n"
    controller.setScoreUpdater(scoreUpdaterType)
  }

  def handleCategorySelection(): Unit = {
    selectedCategory = tableView.selectionModel().getSelectedItem()._1
  }

  def scorecard(): Unit = {
    if (tableView != null) {
      tableView.columns.clear()
      val score = controller.getCurrentPlayer.scoreCard
      val players = controller.getPlayers

      val numPlayers = players.size
      val totalWidth = 400
      val columnWidth =
        totalWidth / (numPlayers + 1)

      val categoryNameMapping: Map[String, String] = Map(
        "one" -> "Einsen",
        "two" -> "Zweien",
        "three" -> "Dreien",
        "four" -> "Vieren",
        "five" -> "Fünfen",
        "six" -> "Sechsen",
        "bonus" -> "Bonus",
        "upperSectionScore" -> "Obere Abschnitt Punktzahl",
        "threeofakind" -> "Dreierpasch",
        "fourofakind" -> "Viererpasch",
        "fullhouse" -> "Full House",
        "smallstraight" -> "Kleine Straße",
        "largestraight" -> "Große Straße",
        "kniffel" -> "Kniffel",
        "chance" -> "Chance",
        "lowerSectionScore" -> "Untere Abschnitt Punktzahl",
        "totalScore" -> "Gesamtpunktzahl"
      )

      val categoryColumn =
        new TableColumn[(String, String), String]("Category") {
          cellValueFactory = { cellData =>
            val internalCategoryName = cellData.value._1
            val friendlyCategoryName = categoryNameMapping.getOrElse(
              internalCategoryName,
              internalCategoryName
            )
            new StringProperty(this, "category", friendlyCategoryName)
          }
          minWidth = columnWidth
          maxWidth = columnWidth
        }

      val playerColumns
          : List[javafx.scene.control.TableColumn[(String, String), String]] =
        players.map { player =>
          new TableColumn[(String, String), String](player.name) {
            cellValueFactory = { cellData =>
              val category = cellData.value._1
              val score = players
                .find(_.name == player.name)
                .flatMap(_.scoreCard.categories.get(category))
                .map(p => if p.isDefined then p.get.toString() else "-")
              new StringProperty(this, "score", score.getOrElse("-"))
            }
            minWidth = columnWidth
            maxWidth = columnWidth
          }
        }

      tableView.columns ++= List(categoryColumn)
      tableView.columns ++= playerColumns

      val allCategories =
        controller.getPlayers.flatMap(_.scoreCard.categories.keys).distinct
      val scoreCardEntries: ObservableBuffer[(String, String)] =
        ObservableBuffer(
          allCategories.map { category =>
            category -> controller.getPlayers.head.scoreCard.categories
              .getOrElse(category, "-")
              .toString
          }*
        )

      tableView.rowFactory = _ => {
        new TableRow[(String, String)] {
          item.onChange { (_, _, newValue) =>
            if (newValue != null) {
              val category = newValue._1
              styleClass --= Seq(
                "bonus",
                "total",
                "upperSection",
                "lowerSection"
              )
              if (category == "bonus") {
                styleClass += "bonus"
              } else if (category == "totalScore") {
                styleClass += "total"
              } else if (category == "upperSectionScore") {
                styleClass += "upperSection"
              } else if (category == "lowerSectionScore") {
                styleClass += "lowerSection"
              }
            }
          }
        }
      }
      tableView.items = scoreCardEntries
      tableView.columnResizePolicy = TableView.ConstrainedResizePolicy
    }
  }

  def rollDice(): Unit = {
    rollButton.disable = true
    val animationDuration = 1000
    val animationInterval = 100

    val timer = new Timer()
    val startTime = System.currentTimeMillis()

    val task = new TimerTask {
      override def run(): Unit = {
        if (System.currentTimeMillis() - startTime >= animationDuration) {
          timer.cancel()
          Platform.runLater {
            val keptDiceIndices = selectedDiceIndices.toList
            controller.keepDice(keptDiceIndices)
            updateDiceResults()
            rollButton.disable = controller.repetitions == 0
          }
        } else {
          Platform.runLater {
            diceImageViews.zipWithIndex.foreach { case (imageView, index) =>
              if (!selectedDiceIndices.contains(index + 1)) {
                val randomValue = Random.nextInt(6) + 1
                imageView.image = new Image(getDiceImagePath(randomValue))
              }
            }
          }
        }
      }
    }
    timer.scheduleAtFixedRate(task, 0, animationInterval)
  }

  def updateSelectedCategory(): Unit = {
    if (selectedCategory.nonEmpty) {
      controller.updateScore(selectedCategory)
      selectedCategory = ""
    }
  }

  def toggleDiceSelection(index: Int): Unit = {
    if (selectedDiceIndices.contains(index)) {
      selectedDiceIndices -= index
    } else {
      selectedDiceIndices += index
    }
    updateDiceResults()
  }

  def resetSelectedDice(): Unit = {
    selectedDiceIndices = Set()
    updateDiceResults()
  }

  def getDiceImagePath(diceValue: Int): String = {
    val currentPath = Paths.get(".").toAbsolutePath.normalize().toString
    s"file:///$currentPath/src/main/resources/$diceValue.png"
  }

  def updateDiceResults(): Unit = {
    val diceResults = controller.getDice
    repetitionsLabel.text = s"Remaining Rolls: ${controller.repetitions}"
    diceImageViews.zipWithIndex.foreach { case (imageView, index) =>
      val result = diceResults(index)
      imageView.image = new Image(getDiceImagePath(result))
      if (selectedDiceIndices.contains(index + 1)) {
        imageView.fitHeight = 60
        imageView.fitWidth = 60
        imageView.style =
          "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 0, 0.8), 10, 0, 0, 0);"
      } else {
        imageView.fitHeight = 50
        imageView.fitWidth = 50
        imageView.style = ""
      }
    }
  }
}
