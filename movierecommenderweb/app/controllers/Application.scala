package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import play.api.Logger
import model.Movie
import play.api.db.DB
import play.api.Play.current
import play.api.db.Databases

class Application extends Controller {

  def index = Action {
     Logger.info("index")
    Ok(views.html.main())
  }
  /*
  case class Movie(title:String)
  
  object Movie{
    implicit val movieformat = Json.format[Movie]
  }
  val form:Form[Movie]=Form{
    mapping(
      "query" -> text  
    )(Movie.apply)(Movie.unapply)
  }
  
  def findmovies = Action {implicit request =>
    val query = form.bindFromRequest.get
    Logger.info(query.title)
    Redirect(routes.Application.index())
    }
  */
  implicit val movieWrites = new Writes[Movie]{
    def writes(movie: Movie) = JsObject(Seq("title" -> JsString(movie.title),
        "tags" -> JsArray(movie.tags.map(JsString))))
    
  }
  def movies = Action {request => 
   val database = Databases(
  driver = "org.sqlite.JDBC",
  url = "jdbc:sqlite:/home/lukas/comstock/python/movie.db")
    val conn = database.getConnection()
    var movies: List[String] = List[String]()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT id as ID, title as Title FROM movie")
      while (rs.next()) {
        val title = rs.getString("title")
        val id = rs.getString("id")
        //val movie = new Movie(id, title, List("action", "romance"));
        movies = title::movies
      }
    } finally {
      conn.close()
    }
     val json = Json.toJson(movies)
   
    Ok(json)
    }
  
}

