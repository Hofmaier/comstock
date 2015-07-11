package ch.hsr.evaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

	public MostPopular(DataModel dm) throws TasteException {
		this.dataModel = dm;
		LongPrimitiveIterator iter2 = dataModel.getItemIDs();
		recommendedItems = new ArrayList<RecommendedItem>();

		Map<Long, Integer> item2pop = new HashMap<Long, Integer>();
		LongPrimitiveIterator iter = dataModel.getUserIDs();
		while (iter.hasNext()) {
			PreferenceArray prefs = dataModel.getPreferencesFromUser(iter
					.nextLong());
			for (int i = 0; i < prefs.length(); i++) {
				Integer counter = item2pop.get(prefs.getItemID(i));
				if (counter == null) {
					item2pop.put(prefs.getItemID(i), 1);
				} else {
					item2pop.put(prefs.getItemID(i), counter + 1);
				}
			}
		}
		List<RecommendedItem> itemidcounter = transfom2list(item2pop);
		Collections.sort(itemidcounter, new RecommenderItemComparator());

		recommendedItems2 = itemidcounter;
		while (iter2.hasNext()) {
			Long itemID = iter2.next();
			PreferenceArray prefs = dm.getPreferencesForItem(itemID);
			float sum = 0;
			for (Preference pref : prefs) {
				sum += pref.getValue();
			}
			float mean = sum / (float) prefs.length();
			recommendedItems.add(new GenericRecommendedItem(itemID, mean));
		}
		Collections.sort(recommendedItems, new RecommenderItemComparator() {

		});
	}

	private List<RecommendedItem> transfom2list(Map<Long, Integer> item2pop) {
		Set<Long> set = item2pop.keySet();

		List<RecommendedItem> itemidcounter = new ArrayList<RecommendedItem>();
		for (Long l : set) {
			Integer counter = item2pop.get(l);
			itemidcounter.add(new GenericRecommendedItem(l, counter));
		}
		return itemidcounter;
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
