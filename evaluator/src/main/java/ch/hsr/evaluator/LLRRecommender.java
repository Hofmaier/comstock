package ch.hsr.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LLRRecommender implements Recommender {

	Logger log = LoggerFactory.getLogger(LLRRecommender.class);
	DataModel dataModel;
	String connectionString = "http://localhost:8983/solr/collection1";
	SolrClient solrClient = new HttpSolrClient(connectionString);

	public LLRRecommender(DataModel dm) {
			dataModel = dm;
		//	updateSolr();
	}

	public void updateSolr() {
		try {
		clearSolr();
		DataModel likes = pref2like(dataModel);
			log.info("create llrrecommender: " + likes.getNumItems());
		ItemSimilarity llrsimilarity = new LogLikelihoodSimilarity(likes);

		List<SolrInputDocument> solrdocs = createSolrDocs(likes,
				llrsimilarity);
		updateSolr(solrdocs);
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateSolr(List<SolrInputDocument> solrdocs)
			throws SolrServerException, IOException {
		log.info("update solr " + solrdocs.size());
		solrClient.add(solrdocs);
		solrClient.commit();
		solrClient.close();
	}

	public GenericDataModel pref2like(DataModel dataModel) {
		try {
			FastByIDMap<PreferenceArray> trainingUsers = new FastByIDMap<PreferenceArray>(
					dataModel.getNumUsers());
			float relevanceThreshold = 3.0f;
			LongPrimitiveIterator iter = dataModel.getUserIDs();
			while (iter.hasNext()) {
				long userid = iter.nextLong();
				PreferenceArray prefs = dataModel
						.getPreferencesFromUser(userid);
				prefs.sortByValueReversed();
				List<Preference> likes = new ArrayList<Preference>();
				for (int i = 0; i < prefs.length(); i++) {
					if (prefs.getValue(i) >= relevanceThreshold) {
						likes.add(prefs.get(i));
					}
				}
				trainingUsers
						.put(userid, new GenericUserPreferenceArray(likes));
			}

			return new GenericDataModel(trainingUsers);
		} catch (TasteException e) {
			log.error("DataModel contains no users");
			return null;
		}
	}

	public List<SolrInputDocument> createSolrDocs(DataModel dm,
			ItemSimilarity similarity) throws TasteException {
		ArrayList<SolrInputDocument> solrdocs = new ArrayList<SolrInputDocument>();
		LongPrimitiveIterator iter = dm.getItemIDs();
		while (iter.hasNext()) {
			SolrInputDocument doc = new SolrInputDocument();
			long itemid = iter.nextLong();
			doc.addField("id", itemid);

			//double similaritythreshold = 0.8;
			StringBuilder simrstr = new StringBuilder();
			LongPrimitiveIterator iter2 = dm.getItemIDs();
			List<Tuple> itemsimlist = new ArrayList<Tuple>();
			while (iter2.hasNext()) {
				long otheritemid = iter2.nextLong();
				itemsimlist.add(new Tuple(otheritemid, similarity.itemSimilarity(itemid, otheritemid)));
			}
				Collections.sort(itemsimlist, new TupleComparator());
				for(int i =0; i < 20; i++){
					Tuple temp = itemsimlist.get(i);
					simrstr.append(" " + temp.itemid);
			}
			doc.addField("simr", simrstr.toString());
			solrdocs.add(doc);
		}
		return solrdocs;
	}

	 void addLikeSim(ItemSimilarity similarity,
			LongPrimitiveIterator iter, long itemid,
			double similaritythreshold, StringBuilder simrstr) {
		long otheritemid = iter.nextLong();

		if (itemid == otheritemid)
			return;
		double sim = 0.0;
		try {
			sim = similarity.itemSimilarity(itemid, otheritemid);
		} catch (TasteException e) {
			log.error("no similiarity between " + itemid + " and "
					+ otheritemid);
		}
		if (!Double.isNaN(sim) && sim >= similaritythreshold) {
			simrstr.append(otheritemid + " ");
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

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany)
			throws TasteException {
		SolrQuery query = createSolrQuery(userID, howMany);
		try {
			List<RecommendedItem> recommendations = retrieveResultset(userID,
					query);
			if(recommendations.size() > howMany){
			recommendations = recommendations.subList(0, howMany);
			}
			log.info("number of recommendations " + recommendations.size());
			return recommendations;
		} catch (Exception e) {
			log.error("could not get resutls");
			e.printStackTrace();
			return null;
		}
	}

	private List<RecommendedItem> retrieveResultset(long userID, SolrQuery query)
			throws SolrServerException, IOException, TasteException {
		solrClient = new HttpSolrClient(connectionString);
		QueryResponse response = solrClient.query(query);
		SolrDocumentList list = response.getResults();
		log.info("response: " + list.size());
		List<RecommendedItem> recommendations = removeKnownItems(userID, list);
		solrClient.close();
		return recommendations;
	}

	private SolrQuery createSolrQuery(long userID, int howMany) throws TasteException {
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		prefs.sortByValueReversed();
		int prefsToConsider = 10;
		StringBuilder strbuilder = new StringBuilder();
		
		strbuilder.append("simr:");
		Iterator<Preference> iter = prefs.iterator();
		for(int i = 0; i < prefsToConsider && iter.hasNext(); i++){
			strbuilder.append(" " + iter.next().getItemID());
		}
		
		strbuilder.append("\n");
		
		strbuilder.append("tags:");
		Iterator<Preference> iter2 = prefs.iterator();
		for(int i = 0; i < prefsToConsider && iter2.hasNext(); i++){
			strbuilder.append(" " + iter2.next().getItemID());
		}
		
		strbuilder.append("\n");
		
		strbuilder.append("llr:");
		Iterator<Preference> iter3 = prefs.iterator();
		for(int i = 0; i < prefsToConsider && iter3.hasNext(); i++){
			strbuilder.append(" " + iter3.next().getItemID());
		}
		
		log.info("querystring is " + strbuilder.toString());

		SolrQuery query = new SolrQuery();
		query.set("q", strbuilder.toString());
		
		query.set("rows", howMany + 30);
		return query;
	}

	private List<RecommendedItem> removeKnownItems(long userID,
			SolrDocumentList list)
			throws TasteException {
		List<RecommendedItem> recommendations = new ArrayList<RecommendedItem>();
		for (SolrDocument doc : list) {
			String id = (String) doc.getFieldValue("id");
			Long itemid = Long.parseLong(id);
			FastIDSet itemsFromUser = this.dataModel
					.getItemIDsFromUser(userID);
		
			if (!itemsFromUser.contains(itemid)) {
				GenericRecommendedItem item = new GenericRecommendedItem(
						itemid, 0);
				recommendations.add(item);
			}
		}
		return recommendations;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer) throws TasteException {
		return recommend(userID, howMany);
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
	}

	@Override
	public float estimatePreference(long userID, long itemID)
			throws TasteException {
		return 0;
	}

	@Override
	public void setPreference(long userID, long itemID, float value)
			throws TasteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePreference(long userID, long itemID)
			throws TasteException {

	}

	@Override
	public DataModel getDataModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
