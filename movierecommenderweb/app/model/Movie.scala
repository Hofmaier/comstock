

package model/**
 * @author lukas
 */
case class Movie(title: String, tags: List[String])

object Movie {
  
  val list: Seq[Movie] = {
    List(
        Movie(
            "Terminator",
            List("action", "future")
            ),
         Movie(
             "Toy Story",
             List("fantasy, pixar")
             )
        )
  }
}