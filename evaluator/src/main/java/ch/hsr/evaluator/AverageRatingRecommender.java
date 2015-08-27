package ch.hsr.evaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

public class AverageRatingRecommender implements Recommender {
	
	DataModel dataModel;
	private List<RecommendedItem> recommendedItems;
	
	public AverageRatingRecommender() throws TasteException {
		this.recommendedItems = new ArrayList<RecommendedItem>();
		LongPrimitiveIterator itemiter = dataModel.getItemIDs();
		while (itemiter.hasNext()) {
			Long itemID = itemiter.next();
			PreferenceArray prefs = dataModel.getPreferencesForItem(itemID);
			float sum = 0;
			for (Preference pref : prefs) {
				sum += pref.getValue();
			}
			float mean = sum / (float) prefs.length();
			this.recommendedItems.add(new GenericRecommendedItem(itemID, mean));
		}
		Collections.sort(this.recommendedItems, new RecommenderItemComparator());
	}
	
	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {

	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany)
			throws TasteException {
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer) throws TasteException {
		return null;
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
