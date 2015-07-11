package ch.hsr.searchengineupdater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.MultiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.common.SolrInputDocument;

public class SolrInputFactory {

	public List<SolrInputDocument> createSolrDocs(DataModel dm,
			ItemSimilarity tagsim, 
			ItemSimilarity similarity, ItemSimilarity llrSimilarity) throws TasteException {
		ArrayList<SolrInputDocument> solrdocs = new ArrayList<SolrInputDocument>();
		LongPrimitiveIterator iter = dm.getItemIDs();
		while (iter.hasNext()) {
			SolrInputDocument doc = new SolrInputDocument();
			long itemid = iter.nextLong();
			doc.addField("id", itemid);

			double similaritythreshold = 0.99;
			StringBuilder simrstr = new StringBuilder();
			StringBuilder tagsimstr = new StringBuilder();
			StringBuilder llrstr = new StringBuilder();
			LongPrimitiveIterator iter2 = dm.getItemIDs();
			List<Tuple> itemsimlist = new ArrayList<Tuple>();
			while (iter2.hasNext()) {

				long otheritemid = iter2.nextLong();
				double sim = similarity.itemSimilarity(itemid, otheritemid);
				double tagsimval = tagsim.itemSimilarity(itemid, otheritemid);
				double llrsim = llrSimilarity.itemSimilarity(itemid, otheritemid);
				if (!Double.isNaN(sim)) {
					itemsimlist.add(new Tuple(otheritemid, sim));
				}
				if (!Double.isNaN(tagsimval) && tagsimval > 0.5) {
					tagsimstr.append(" " + otheritemid);
				}
				if (!Double.isNaN(llrsim) && llrsim > 0.9) {
					llrstr.append(" " + otheritemid);
				}
			}
			Collections.sort(itemsimlist, new TupleComparator());
			for (Tuple t : itemsimlist) {
				Tuple temp = t;
				if (temp.similarity > similaritythreshold) {
					simrstr.append(" " + temp.itemid);
				}
			}
			String similaritemidstr = simrstr.toString();
			doc.addField("simr", similaritemidstr);
			doc.addField("tags", tagsimstr.toString());
			doc.addField("llr", llrstr.toString());
			solrdocs.add(doc);
		}
		return solrdocs;
	}

	public Collection<SolrInputDocument> movies() {
		try {
			Reader in;

			in = new FileReader(
					"/home/lukas/Downloads/ml-latest-small/movies.csv");

			BufferedReader br = new BufferedReader(in);
			String line;
			DataReader datareader = new DataReader();
			MultiMap movieId2tags = datareader.movieId2tags();
			ArrayList<SolrInputDocument> solrDocument = new ArrayList<SolrInputDocument>();
			while ((line = br.readLine()) != null) {
				String[] str = line.split(",");
				if (str[0].equals("movieId")) {
					continue;
				}
				Integer movieid = Integer.parseInt(str[0]);
				String title = str[1];
				@SuppressWarnings("unchecked")
				List<String> tags = (List<String>) movieId2tags.get(movieid);

				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", movieid);

				doc.addField("title", title);
				StringBuilder tagsstr = new StringBuilder();
				if (tags != null) {
					for (String tag : tags) {
						tagsstr.append(tag + " ");
					}
					doc.addField("tags", tagsstr.toString());
				}
				solrDocument.add(doc);
			}
			br.close();
			return solrDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
