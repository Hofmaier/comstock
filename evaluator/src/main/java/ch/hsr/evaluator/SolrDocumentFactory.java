package ch.hsr.evaluator;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDocumentFactory {
	Logger log = LoggerFactory.getLogger(SolrDocumentFactory.class);

	public List<SolrInputDocument> createSolrDocs(DataModel likedm,
			ItemSimilarity tagSimilarity, ItemSimilarity cosineSimilarity,
			ItemSimilarity llrSimilarity) throws TasteException {
		log.info("start analysis");
		ArrayList<SolrInputDocument> solrdocs = new ArrayList<SolrInputDocument>();
		LongPrimitiveIterator iter = likedm.getItemIDs();
		while (iter.hasNext()) {
			long itemid = iter.nextLong();
			SolrInputDocument doc = buildSolrDoc(likedm, tagSimilarity,
					cosineSimilarity, llrSimilarity, itemid);
			solrdocs.add(doc);
		}
		log.info("solr docs created");
		return solrdocs;
	}

	private SolrInputDocument buildSolrDoc(DataModel datamodel,
			ItemSimilarity tagSimilariy, ItemSimilarity cosineSimilarity,
			ItemSimilarity llrSimilarity, long itemid) throws TasteException {
		SolrInputDocument doc = new SolrInputDocument();

		StringBuilder simrstr = new StringBuilder();
		StringBuilder tagsimstr = new StringBuilder();
		StringBuilder llrstr = new StringBuilder();
		LongPrimitiveIterator iter2 = datamodel.getItemIDs();

		while (iter2.hasNext()) {
			buildIndicatorStrings(tagSimilariy, cosineSimilarity,
					llrSimilarity, itemid, simrstr, tagsimstr, llrstr, iter2);
		}

		doc.addField("id", itemid);
		doc.addField(FieldIdentifier.likes, simrstr.toString());
		doc.addField(FieldIdentifier.tags, tagsimstr.toString());
		doc.addField(FieldIdentifier.llr, llrstr.toString());
		return doc;
	}

	private void buildIndicatorStrings(ItemSimilarity tagsim,
			ItemSimilarity similarity, ItemSimilarity llrSimilarity,
			long itemid, StringBuilder simrstr, StringBuilder tagsimstr,
			StringBuilder llrstr, LongPrimitiveIterator iter2)
			throws TasteException {
		double similaritythreshold = 0.99;
		double likesimilaritythreshold = 1.0;
		long otheritemid = iter2.nextLong();
		double sim = similarity.itemSimilarity(itemid, otheritemid);
		double tagsimval = tagsim.itemSimilarity(itemid, otheritemid);
		double llrsim = llrSimilarity.itemSimilarity(itemid, otheritemid);

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
}
