package ch.hsr.searchengineupdater;

import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SolrInputFactory inputfactory = new SolrInputFactory();
        Collection<SolrInputDocument> documents = inputfactory.movies();
        SearchEngine searchEngine = new SearchEngine();
        searchEngine.update(documents);
        
    }
}
