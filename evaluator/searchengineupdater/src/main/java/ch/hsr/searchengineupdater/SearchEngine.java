package ch.hsr.searchengineupdater;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchEngine {
	
	String connectionString = "http://localhost:8983/solr/collection1";
	SolrClient solrClient = new HttpSolrClient(connectionString);
	Logger log = LoggerFactory.getLogger(SearchEngine.class);
	
	public void update(List<SolrInputDocument> solrdocs) {
		try {
			clearSolr();
			updateSolr(solrdocs);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void clearSolr() {
		try {
			solrClient.deleteByQuery("*:*");
			solrClient.commit();
		} catch (SolrServerException e) {
			log.error("couldn't drop db");
		} catch (IOException e) {
			log.error("could not connect to solr");
		}
	}
	
	private void updateSolr(List<SolrInputDocument> solrdocs)
			throws SolrServerException, IOException {
		log.info("update solr " + solrdocs.size());
		solrClient.add(solrdocs);
		solrClient.commit();
		solrClient.close();
	}

}
