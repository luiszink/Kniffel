import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.kniffel.model.Player
import de.htwg.se.kniffel.model.ScoreCard

class PlayerSpec extends AnyFlatSpec with Matchers {
  
  "A Player" should "have a name" in {
    val player = Player("Alice")
    player.name should be("Alice")
  }
  
  it should "have a default ScoreCard" in {
    val player = Player("Bob")
    player.scoreCard shouldBe a[ScoreCard]
  }
  
  it should "be represented by its name when converted to a string" in {
    val player = Player("Charlie")
    player.toString should be("Charlie")
  }
}
