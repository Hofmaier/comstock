package controllers

import java.sql.PreparedStatement
import java.sql.ResultSet
import model.Movie
import play.api._
import play.api.Play.current
import play.api.data.Forms._
import play.api.db.DB
import play.api.libs.json._
import play.api.mvc._
import java.sql.Connection
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocument
import scala.collection.JavaConversions.iterableAsScalaIterable

class Application extends Controller {
  val useridkey = "userid"
  val solrConStr = "http://localhost:8983/solr/collection1"
  
  def index = Action {
     Logger.info("index")
    val userid = 1
    Ok(views.html.main()).withSession(useridkey -> userid.toString())
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
      val selectmoviestmnt = "SELECT id as ID, title as Title FROM movie LIMIT 10"
      val rs:ResultSet = stmt.executeQuery(selectmoviestmnt)
      while (rs.next()) {
        val title = rs.getString("title")
        val id = rs.getString("id")
        val gettagquerystr = "SELECT tag as Tag FROM tag WHERE movieid = ?";
        val preparedstatement:PreparedStatement = conn.prepareStatement(gettagquerystr);
        preparedstatement.setString(1, id)
        val tagresultset = preparedstatement.executeQuery()
        var tags:List[String] = Nil
        while(tagresultset.next()){
          tags = tagresultset.getString("Tag") :: tags
        }
        if(tags.length != 0){
        val tagjson = JsArray(for (t <- tags) yield JsString(t))
        val json = JsObject(Seq(
              "title" -> JsString(title),
              "id" -> JsString(id),
              "tags" -> tagjson
            ))
         jsonarr = jsonarr :+ json   
        }
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
    val userid = request.session.get(useridkey).map{_.toInt}.getOrElse(0)
    val json:JsValue = request.body
    val movieid = (json  \ "movieid").as[String]
    val timestamp = System.currentTimeMillis() /1000
     val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val insertstat = "INSERT INTO like VALUES (?,?,?)";
      val preparedstatement:PreparedStatement = conn.prepareStatement(insertstat);
      val userid = 1;
      preparedstatement.setInt(1, userid)
      preparedstatement.setString(2, movieid)
      preparedstatement.setString(3,timestamp.toString)
      preparedstatement.executeUpdate()
    } finally {
      conn.close()
    }
     Ok("ok")
  }
  
  def tag = Action(BodyParsers.parse.json) { request =>
    val json:JsValue = request.body
    val movieid = (json  \ "movieid").as[String]
    val tagstr = (json \ "tag").as[String]
    val timestamp = System.currentTimeMillis() /1000
     val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val insertstat = "INSERT INTO tag VALUES (?,?,?,?)";
      val preparedstatement:PreparedStatement = conn.prepareStatement(insertstat);
      val userid = "1";
      preparedstatement.setString(1, userid)
      preparedstatement.setString(2, movieid)
      preparedstatement.setString(3, tagstr)
      preparedstatement.setString(4,timestamp.toString)
      preparedstatement.executeUpdate()
    } finally {
      conn.close()
    }
     Ok("ok")
  }
  
  def getResultSet(stmnt:String, userid:Int):ResultSet = {
     val dbconnection:Connection = DB.getConnection()
     val preparedstatement:PreparedStatement = dbconnection.prepareStatement(stmnt);
    preparedstatement.setInt(1, userid)
    preparedstatement.executeQuery()
  }
  
  
  implicit val solrdocWrites = new Writes[SolrDocument]{
    def writes(solrdoc:SolrDocument) = Json.obj(
      "title" -> solrdoc.getFieldValue("title").toString().filter { _ !='['}.filter {']'!= _},
      "id" -> solrdoc.getFieldValue("id").toString().toLong
    )
  }
  
  def topn = Action {request =>
    val userid:Int = request.session.get(useridkey).map{_.toInt}.getOrElse(0)
    val likehistory:String = "SELECT movieid FROM LIKE WHERE userid = ?"
    val resultset:ResultSet = getResultSet(likehistory, userid)
    val iter = new Iterator[String]{
      override def hasNext = resultset.next()
      override def next = resultset.getString("movieid")
    }
    var history:List[String] = iter.toList
    
    val solrClient:SolrClient = new HttpSolrClient(solrConStr);
    val response: QueryResponse = solrClient.query(new SolrQuery().setParam("q", history.mkString(" ")).set("rows",10))
    val list: SolrDocumentList = response.getResults()
    val solrResponse:Iterable[SolrDocument] = list
    val unknownItems = solrResponse.filter {x => !history.contains(x.getFieldValue("id").toString())}
    Ok(Json.toJson(unknownItems))
  }
}

