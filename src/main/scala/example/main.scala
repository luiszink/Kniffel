
object Main {
  def main(args: Array[String]): Unit = {
    KniffelApp.main(Array.empty) // Aufruf der main-Methode von KniffelApp
    println("This should cause a Merge-Conflict")
  }
}