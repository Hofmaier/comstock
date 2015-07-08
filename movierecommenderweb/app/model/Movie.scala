
package model/**
 * @author lukas
 */
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import scala.collection.Iterable
import scala.collection.JavaConversions.collectionAsScalaIterable
import java.util.Collection

case class Movie(title: String, tags: List[String])

object Movie {
  def doc2movie( doc: SolrDocument) = {
      val title = doc.getFieldValue("title").asInstanceOf[String]
      val tags: String = doc.getFieldValue("tags").asInstanceOf[String]
      val tagarray = tags.split(" ")
      new Movie(title, tagarray.toList)
  }
  
  val list: Seq[Movie] = {
    val client = new HttpSolrClient("http://localhost:8983/solr/collection1")
    val query = new SolrQuery()
    query.set("q","title:Terminator")
   
    val response = client.query(query)
    val documentlist = response.getResults;
    val collection: Collection[SolrDocument] = documentlist
    val solrdocuments : Iterable[SolrDocument] = collection   
    val movielist = solrdocuments.map { doc2movie _}
    
     client.close();
     movielist.toSeq
  }
    /*
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
        
        */
 
}