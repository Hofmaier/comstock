package ch.hsr.solrpayload;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class PayloadParserPlugin extends QParserPlugin {

	@Override
	public void init(NamedList arg0) {
		
	}

	@Override
	public QParser createParser(String arg0, SolrParams arg1, SolrParams arg2,
			SolrQueryRequest arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
