package ch.hsr.solrpayload;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SyntaxError;

public class PayloadQParser extends QParser {
	PayloadQueryParser pqParser;
	 public PayloadQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
		    super(qstr, localParams, params, req);
		  }

	@Override
	public Query parse() throws SyntaxError {
	    String qstr = getString();
	    if (qstr == null || qstr.length() == 0) return null;
	 
	    String defaultField = getParam(CommonParams.DF);
	    if (defaultField == null) {
	      defaultField = getReq().getSchema().getDefaultSearchFieldName();
	    }
	    pqParser = new PayloadQueryParser(this, defaultField);
	 
	    pqParser.setDefaultOperator
	        (QueryParsing.getQueryParserDefaultOperator(getReq().getSchema(),
	            getParam(QueryParsing.OP)));
	 
	    return pqParser.parse(qstr);
	}

}
