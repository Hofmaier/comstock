package ch.hsr.searchengineupdater;

import java.io.IOException;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

public class SearchEngine {
	
	public void update(Collection<SolrInputDocument> alldocs) {
		SolrClient client = new HttpSolrClient(
				"http://localhost:8983/solr/collection1");

		try {
			client.deleteByQuery("*:*");
			client.commit();
			client.add(alldocs);
			client.commit();
			client.close();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
