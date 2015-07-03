package ch.hsr.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Evaluator {
	private Logger log = LoggerFactory.getLogger(Evaluator.class);
	
	
	
	public double recall(DataModel filedataModel) throws TasteException{
		long userID = 111;
		float threshold = 3.0f;
		DataModel dataModel = filedataModel;
		int nrOfretrievedItems = 5;
		int nrOfRecommendedItems = 100;
		
		DataModel trainingDataModel = removeRelevantItemsFromDataModel(dataModel, userID, threshold);
		//dataModel.removePreference(pref1.getUserID(), pref1.getItemID());
		Recommender recommender = new GenericItemBasedRecommender(trainingDataModel, new PearsonCorrelationSimilarity(trainingDataModel));
		//Recommender recommender = new SVDRecommender(trainingDataModel, new ALSWRFactorizer(trainingDataModel, 10, 0.5, 10));
		List<RecommendedItem> recommendations = recommender.recommend(userID, nrOfRecommendedItems);
		int relevantItemsRetrieved = 0;
		List<Preference> relevantItems = relevantItems(userID, dataModel, threshold);
		int nrRelevantItems = relevantItems.size();
		log.info("nr of relevant items: " + nrRelevantItems);
		log.info("nr of recommendations: " + recommendations.size());
		for(RecommendedItem i : recommendations){
			//log.info("relevant item: " + i.getItemID());
		}
		for(RecommendedItem i : recommendations){
			log.info("item from recommendation: " + i.getItemID());
			for(Preference pref : relevantItems){
				log.info("relevant item: " + pref.getItemID());
				if(pref.getItemID() == i.getItemID()){
					log.info("found relevant item");
					relevantItemsRetrieved++;
				}
			}
		}
		double precision = 0.0;
		log.info("relevant items retrieved: " + relevantItemsRetrieved);
		return precision;
	}
	
	public DataModel removeRelevantItemsFromDataModel(DataModel dataModel, long userid, float threshold) throws TasteException{
		LongPrimitiveIterator useriter = dataModel.getUserIDs();
		FastByIDMap<PreferenceArray> trainingset = new FastByIDMap<PreferenceArray>();
		while(useriter.hasNext()){
			long u = useriter.next();
			if(u != userid){
			trainingset.put(u, dataModel.getPreferencesFromUser(u));
			}
			else{
				addIrrelevant(dataModel, trainingset, userid, threshold);
			}
		}
		DataModel trainingDataModel = new GenericDataModel(trainingset);
		return trainingDataModel;
	}

	private FastByIDMap<PreferenceArray> addIrrelevant(DataModel dataModel, FastByIDMap<PreferenceArray> trainingset, long userid, float threshold)
			throws TasteException {
		List<Preference> irrelevant = new ArrayList<Preference>();
		PreferenceArray userprefs = dataModel.getPreferencesFromUser(userid);
		Iterator<Preference> userprefiter = userprefs.iterator();
		
		while(userprefiter.hasNext()){
			Preference p = userprefiter.next();
			if(p.getValue() < threshold){
				irrelevant.add(p);						
			}
		}
		PreferenceArray trainingUserPrefArray = new GenericUserPreferenceArray(irrelevant);
		trainingset.put(userid, trainingUserPrefArray);
		return trainingset;
	}
	
	public List<Preference> relevantItems(long userID, DataModel dataModel, float threshold ) throws TasteException{
		List<Preference> relevantItems = new ArrayList<Preference>();
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		Iterator<Preference> iter = prefs.iterator();
		while(iter.hasNext()){
			Preference pref = iter.next();
			//log.info("rating : " + pref.getValue());
			if(pref.getValue() > threshold){
				relevantItems.add(pref);
			}
		}
		return relevantItems;
	}
	
	public RecommenderBuilder svd(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				return new SVDRecommender(dataModel,
						new ALSWRFactorizer(dataModel, 10, 0.5, 10));
			}
		};
	}
	
	public RecommenderBuilder itembased(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				ItemSimilarity similarity = new PearsonCorrelationSimilarity(
						dataModel);
				return new GenericItemBasedRecommender(dataModel,
						similarity);
			}
		};
	}
}
