package ch.hsr.solrpayload;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.solr.parser.QueryParser;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

public class PayloadQueryParser extends QueryParser{
	PayloadQueryParser(QParser parser, String defaultField){
		super(parser.getReq().getCore().getSolrConfig().luceneMatchVersion, defaultField, parser);
	}

	  @Override
	  protected Query getFieldQuery(String field, String queryText, boolean quoted) throws SyntaxError {
	    SchemaField sf = this.schema.getFieldOrNull(field);

	    if (sf != null && sf.getType().getTypeName().equalsIgnoreCase("payloads")) {
	      return new PayloadTermQuery(new Term(field, queryText), new AveragePayloadFunction(), true);
	    }
	    return super.getFieldQuery(field, queryText, quoted);
	  }
}
