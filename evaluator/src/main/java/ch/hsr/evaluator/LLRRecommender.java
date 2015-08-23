package ch.hsr.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LLRRecommender implements Recommender {

	Logger log = LoggerFactory.getLogger(LLRRecommender.class);
	DataModel likeDataModel;
	DataModel tagDataModel;
	String connectionString;
	SolrClient solrClient;

	public LLRRecommender(DataModel likedm, DataModel tagdm, String connectionString) {
			log.info("create LLR recommender");
			this.likeDataModel = likedm;
			this.tagDataModel = tagdm;
			this.connectionString = connectionString;
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
		PreferenceArray prefs = likeDataModel.getPreferencesFromUser(userID);
		prefs.sortByValueReversed();
		int prefsToConsider = 10;
		StringBuilder strbuilder = new StringBuilder();
		
		buildindicatorList(prefs, prefsToConsider, strbuilder, FieldIdentifier.likes);
		buildindicatorList(prefs, prefsToConsider, strbuilder, FieldIdentifier.tags);
		buildindicatorList(prefs, prefsToConsider, strbuilder, FieldIdentifier.llr);

		log.info("querystring is " + strbuilder.toString());

		SolrQuery query = new SolrQuery();
		query.set("q", strbuilder.toString());
		query.set("rows", howMany + 30);
		return query;
	}

	private void buildindicatorList(PreferenceArray prefs, int prefsToConsider,
			StringBuilder strbuilder, String fieldStr) {
		strbuilder.append(fieldStr + ":");
		Iterator<Preference> iter = prefs.iterator();
		for(int i = 0; i < prefsToConsider && iter.hasNext(); i++){
			strbuilder.append(" " + iter.next().getItemID());
		}
		
		strbuilder.append("\n");
	}

	private List<RecommendedItem> removeKnownItems(long userID,
			SolrDocumentList list)
			throws TasteException {
		List<RecommendedItem> recommendations = new ArrayList<RecommendedItem>();
		for (SolrDocument doc : list) {
			String id = (String) doc.getFieldValue("id");
			Long itemid = Long.parseLong(id);
			FastIDSet itemsFromUser = this.likeDataModel
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
	}

	@Override
	public void removePreference(long userID, long itemID)
			throws TasteException {

	}

	@Override
	public DataModel getDataModel() {
		return null;
	}

}
