package ch.hsr.evaluator;

import java.util.Comparator;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class RecommenderItemComparator implements Comparator<RecommendedItem> {
	
	@Override
	public int compare(RecommendedItem a, RecommendedItem b){
		if(a.getValue() < b.getValue()) return 1;
		if(a.getValue() > b.getValue()) return -1;
		if(a.getValue() == b.getValue()) return 0;
		return 0;
	}
}
