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

  def index = Action {
    Logger.info("index")
    val userid = 1
    Ok(views.html.main()).withSession(useridkey -> userid.toString())
  }

  implicit val movieWrites = new Writes[Movie] {
    def writes(movie: Movie) = JsObject(Seq("title" -> JsString(movie.title),
      "tags" -> JsArray(movie.tags.map(JsString))))

  }
  def movies = Action { request =>

    val conn:Connection = DB.getConnection()
    var jsonarr = JsArray()
    try {
      val stmt = conn.createStatement
      val selectmoviestmnt = "SELECT id as ID, title as Title FROM movie LIMIT 20"
      val resultset: ResultSet = stmt.executeQuery(selectmoviestmnt)
   
      while (resultset.next()) {
        val title = resultset.getString("title")
        val id = resultset.getString("id")
        
        val taglist = gettags(id, conn)
        if (taglist.length != 0) {
          val json = JsObject(Seq(
            "title" -> JsString(title),
            "id" -> JsString(id),
            "tags" -> JsArray(for (t <- taglist) yield JsString(t))))
          jsonarr = jsonarr :+ json
        }
      }
    } finally {
      conn.close()
    }
    Ok(jsonarr)
  }

  case class Like(id: String)

  def like = Action(BodyParsers.parse.json) { request =>
    val userid = request.session.get(useridkey).map { _.toInt }.getOrElse(0)
    val json: JsValue = request.body
    val movieid = (json \ "movieid").as[String]
    val timestamp = System.currentTimeMillis() / 1000
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val insertstat = "INSERT INTO like VALUES (?,?,?)";
      val preparedstatement: PreparedStatement = conn.prepareStatement(insertstat);
      val userid = 1;
      preparedstatement.setInt(1, userid)
      preparedstatement.setString(2, movieid)
      preparedstatement.setString(3, timestamp.toString)
      preparedstatement.executeUpdate()
    } finally {
      conn.close()
    }
    Ok("ok")
  }

  def tag = Action(BodyParsers.parse.json) { request =>
    val json: JsValue = request.body
    val movieid = (json \ "movieid").as[String]
    val tagstr = (json \ "tag").as[String]
    val timestamp = System.currentTimeMillis() / 1000
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val insertstat = "INSERT INTO tag VALUES (?,?,?,?)";
      val preparedstatement: PreparedStatement = conn.prepareStatement(insertstat);
      val userid = "1";
      preparedstatement.setString(1, userid)
      preparedstatement.setString(2, movieid)
      preparedstatement.setString(3, tagstr)
      preparedstatement.setString(4, timestamp.toString)
      preparedstatement.executeUpdate()
    } finally {
      conn.close()
    }
    Ok("ok")
  }

  def getResultSet(stmnt: String, userid: Int): ResultSet = {
    val dbconnection: Connection = DB.getConnection()
    val preparedstatement: PreparedStatement = dbconnection.prepareStatement(stmnt);
    preparedstatement.setInt(1, userid)
    preparedstatement.executeQuery()
  }

  implicit val solrdocWrites = new Writes[SolrDocument] {
    def writes(solrdoc: SolrDocument) = Json.obj(
      "title" -> solrdoc.getFieldValue("title").toString().filter { _ != '[' }.filter { ']' != _ },
      "id" -> solrdoc.getFieldValue("id").toString().toLong)
  }
  
  
  def topn = Action { request =>
    val userid: Int = request.session.get(useridkey).map { _.toInt }.getOrElse(0)
    val likehistory: String = "SELECT movieid FROM LIKE WHERE userid = ?"
    val resultset: ResultSet = getResultSet(likehistory, userid)
    val iter = new Iterator[String] {
      override def hasNext = resultset.next()
      override def next = resultset.getString("movieid")
    }
    var history: List[String] = iter.toList
    val solrConStr = Play.current.configuration.getString("solr.url").getOrElse("solr config not found")
    val solrClient: SolrClient = new HttpSolrClient(solrConStr);
    val response: QueryResponse = solrClient.query(new SolrQuery().setParam("q", history.mkString(" ")).set("rows", 10))
    val solrResponse: Iterable[SolrDocument] = response.getResults()
    def itemIsKnown(x: SolrDocument) = history.contains(x.getFieldValue("id").toString())
    val unknownItems = solrResponse.filter {!itemIsKnown(_)}
    Ok(Json.toJson(unknownItems))
  }
  
  def recommendations = Action { request => 
        Ok(views.html.recommender())
    }
  
  def gettags(movieId:String, conn:Connection):List[String] = {
        val gettagquerystr = "SELECT tag as Tag FROM tag WHERE movieid = ?";
        val preparedstatement: PreparedStatement = conn.prepareStatement(gettagquerystr);
        preparedstatement.setString(1, movieId)
        val tagresultset = preparedstatement.executeQuery()
        val iter = new Iterator[String] {
          override def hasNext = tagresultset.next()
          override def next = tagresultset.getString("Tag")
        }
        val taglist: List[String] = iter.toList
        return taglist
  }
}

