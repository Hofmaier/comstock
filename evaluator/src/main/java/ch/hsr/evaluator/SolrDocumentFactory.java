package ch.hsr.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDocumentFactory {
	Logger log = LoggerFactory.getLogger(SolrDocumentFactory.class);
	public List<SolrInputDocument> createSolrDocs(DataModel dm,
			ItemSimilarity tagsim, 
			ItemSimilarity similarity, 
			ItemSimilarity llrSimilarity,
			Map<Long, Movie> movies) throws TasteException {
		log.info("start analysis");
		ArrayList<SolrInputDocument> solrdocs = new ArrayList<SolrInputDocument>();
		LongPrimitiveIterator iter = dm.getItemIDs();
		while (iter.hasNext()) {
			SolrInputDocument doc = new SolrInputDocument();
			long itemid = iter.nextLong();
			doc.addField("id", itemid);
			String title = movies.get(itemid).title;
			doc.addField("title", title);
			double similaritythreshold = 0.99;
			double likesimilaritythreshold = 1.0;
			StringBuilder simrstr = new StringBuilder();
			StringBuilder tagsimstr = new StringBuilder();
			StringBuilder llrstr = new StringBuilder();
			StringBuilder llrindicatorstrbld = new StringBuilder();
			LongPrimitiveIterator iter2 = dm.getItemIDs();
			while (iter2.hasNext()) {

				long otheritemid = iter2.nextLong();
				double sim = similarity.itemSimilarity(itemid, otheritemid);
				double tagsimval = tagsim.itemSimilarity(itemid, otheritemid);
				double llrsim = llrSimilarity.itemSimilarity(itemid, otheritemid);
				if(!Double.isNaN(llrsim)){
					String llrsimstr = String.format("%.1f", llrsim);
					llrindicatorstrbld.append(otheritemid + "|" + llrsimstr + " ");
				}
				if (!Double.isNaN(sim) && sim > similaritythreshold) {
					simrstr.append(" " + otheritemid);
				}
				if (!Double.isNaN(tagsimval) && tagsimval > 0.5) {
					tagsimstr.append(" " + otheritemid);
				}
				if (!Double.isNaN(llrsim) && llrsim > likesimilaritythreshold) {
					llrstr.append(" " + otheritemid);
				}
			}
			//log.info("finished similarity analysis for one item");
			String similaritemidstr = simrstr.toString();
			doc.addField("like", similaritemidstr);
			doc.addField("tags", tagsimstr.toString());
			doc.addField("llr", llrstr.toString());
			doc.addField(llrfield, llrindicatorstrbld.toString());
			solrdocs.add(doc);
		}
		log.info("solr doc created" );
		return solrdocs;
	}

}
