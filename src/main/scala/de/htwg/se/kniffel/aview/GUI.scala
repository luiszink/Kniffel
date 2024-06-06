package de.htwg.se.kniffel.aview

import de.htwg.se.kniffel.controller.Controller
import de.htwg.se.kniffel.util.Observer
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.control.TableView
import scalafx.scene.control.TableColumn
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.application.Platform
import de.htwg.se.kniffel.model.Player

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  var pane: Pane = null;
  var tableView: TableView[(String, String)] = null;

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Kniffel"
      resizable = false
      scene = new Scene(
        600,
        600
      ) {
        pane = new Pane()
        tableView = new TableView[(String, String)]()
        pane.children = Seq(tableView)
        content = pane
      }
    }
  }

  override def update(message: String): Unit = {
    Platform.runLater(message match {
      case "printScoreCard" => scorecard()
      case "playerAdded"    => scorecard()
      case _                => println("wrong notifyObservers!!!!!!!!!!!!!!")
    })
  }

  def scorecard(): Unit = {
    tableView.columns.clear();
    val score = controller.getCurrentPlayer.scoreCard;
    val players = controller.getPlayers
    val categoryColumn =
      new TableColumn[(String, String), String]("Category") {
        cellValueFactory = { cellData =>
          new StringProperty(this, "category", cellData.value._1)
        }
      }

    val playerColumns: List[
      javafx.scene.control.TableColumn[(String, String), String]
    ] = controller.getPlayers.map { player =>
      new TableColumn[(String, String), String](player.name) {
        cellValueFactory = { cellData =>
          val category = cellData.value._1
          val score = controller.getPlayers
            .find(_.name == player.name)
            .flatMap(_.scoreCard.categories.get(category))
            .map(p => if p.isDefined then p.get.toString() else "-")
          new StringProperty(
            this,
            "score",
            score.getOrElse("-")
          )
        }
      }
    }

// Add columns to the TableView
    tableView.columns ++= List(categoryColumn)
    tableView.columns ++= playerColumns

    val allCategories =
      controller.getPlayers.flatMap(_.scoreCard.categories.keys).distinct
    val scoreCardEntries: ObservableBuffer[(String, String)] = ObservableBuffer(allCategories.map { category =>
      category -> controller.getPlayers.head.scoreCard.categories
        .getOrElse(category, "-")
        .toString
    }: _*)

    tableView.items = scoreCardEntries

  }

}
