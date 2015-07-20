package ch.hsr.solrpayload;

import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.schema.SimilarityFactory;

public class PayloadSimilarityFactory extends SimilarityFactory{

	 @Override
	  public void init(SolrParams params) {
	    super.init(params);
	  }
	 
	@Override
	public Similarity getSimilarity() {
		return new PayloadSimilarity();
	}

}
