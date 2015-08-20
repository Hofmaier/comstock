package ch.hsr.searchengineupdater;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferenceConverter {
	Logger log = LoggerFactory.getLogger(PreferenceConverter.class);

	public GenericBooleanPrefDataModel pref2like(DataModel dataModel,
			float threshold) {
		try {
			FastByIDMap<FastIDSet> userlikes = new FastByIDMap<FastIDSet>(
					dataModel.getNumUsers());
			LongPrimitiveIterator iter = dataModel.getUserIDs();
			while (iter.hasNext()) {
				long userid = iter.nextLong();
				PreferenceArray prefs = dataModel
						.getPreferencesFromUser(userid);
				FastIDSet ids = new FastIDSet(prefs.length());
				for (Preference p : prefs) {
					if (p.getValue() >= threshold) {
						ids.add(p.getItemID());
					}
				}
				userlikes.put(userid, ids);
			}
			log.info("finished 2likes conversion");
			return new GenericBooleanPrefDataModel(userlikes);
		} catch (TasteException e) {
			log.error("DataModel contains no users");
			return null;
		}
	}

}
