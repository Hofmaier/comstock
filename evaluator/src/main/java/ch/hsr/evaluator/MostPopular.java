package ch.hsr.evaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class MostPopular implements Recommender {
	
	DataModel dataModel;
	private List<RecommendedItem> recommendedItems;
	private List<RecommendedItem> recommendedItems2;
	
	public MostPopular(DataModel dm) throws TasteException{
		this.dataModel = dm;
		LongPrimitiveIterator iter = dataModel.getItemIDs();
		recommendedItems = new ArrayList<RecommendedItem>();
		
		Map<Long, Integer> prefcountermap = new HashMap<Long, Integer>();
		 LongPrimitiveIterator it2 = dataModel.getUserIDs();
	      while (it2.hasNext()) {
	    	  PreferenceArray prefs = dataModel.getPreferencesFromUser(it2.nextLong());
	    	  prefs.sortByValueReversed();
	    	  for(int i = 0; i < prefs.length(); i++){
	    		  float pref = prefs.getValue(i);
	    		  Long itemid = prefs.getItemID(i);
	    		  Integer counter = prefcountermap.get(itemid);
	    		  if(counter == null){
	    			  prefcountermap.put(itemid, 1);
	    		  }
	    		  else{
	    			  prefcountermap.put(itemid, counter + 1);
	    		  }
	    	  }
	      }
	      
	      Set<Long> set = prefcountermap.keySet();
    	  
    	  List<RecommendedItem> itemidcounter = new ArrayList<RecommendedItem>();
    	  for(Long l : set){
    		  Integer counter = prefcountermap.get(l);
    		  itemidcounter.add(new GenericRecommendedItem(l,counter));
    	  }
    	  Collections.sort(itemidcounter, new RecommenderItemComparator());
    	  
	    recommendedItems2 = itemidcounter;
		while(iter.hasNext()){
			Long itemID = iter.next();
			PreferenceArray prefs = dm.getPreferencesForItem(itemID);
			float sum = 0;
			for(Preference pref : prefs){
				sum += pref.getValue();
			}
			float mean = sum/(float)prefs.length();
			recommendedItems.add(new GenericRecommendedItem(itemID, mean));
		}
		Collections.sort(recommendedItems, new RecommenderItemComparator(){
			

		});
		int t = recommendedItems.size();
	}
	
	

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany)
			throws TasteException {
		dataModel.getItemIDs();
		List<RecommendedItem> temp = recommendedItems2.subList(0, howMany);
		return temp;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer) throws TasteException {
		return recommend(userID, howMany);
	}

	@Override
	public float estimatePreference(long userID, long itemID)
			throws TasteException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public DataModel getDataModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
