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
import java.sql.PreparedStatement
import java.util.Date

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

    val conn = DB.getConnection()
    var jsonarr = JsArray()
    try {
      val stmt = conn.createStatement
      
      val rs = stmt.executeQuery("SELECT id as ID, title as Title FROM movie LIMIT 5  ")
      while (rs.next()) {
        val title = rs.getString("title")
        val id = rs.getString("id")
        val json = JsObject(Seq(
              "title" -> JsString(title),
              "id" -> JsString(id)
            ))
         jsonarr = jsonarr :+ json   
      }
    } finally {
      conn.close()
    }
    Ok(jsonarr)
    }
  
  case class Like(id: String)
  
//  implicit val likeReads: Reads[Like] = new Reads[Like]{
//    def reads()
//  }

  def like = Action(BodyParsers.parse.json) { request =>
    val json:JsValue = request.body
    val movieid = (json  \ "movieid").as[String]
     val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val insertstat = "INSERT INTO like VALUES (?,?,?)";
      val preparedstatement:PreparedStatement = conn.prepareStatement(insertstat);
      val userid = "1";
      preparedstatement.setString(1, userid)
      preparedstatement.setString(2, movieid)
      preparedstatement.setString(3,"jetzt")
      preparedstatement.executeUpdate()
    } finally {
      conn.close()
    }
     Ok("ok")
  }
  
}

