package ch.hsr.evaluator;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.RandomRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class RecommenderFactory {
	
	public RecommenderBuilder svd(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				return new SVDRecommender(dataModel,
						new ALSWRFactorizer(dataModel, 10, 0.5, 10));
			}
		};
	}
	
	public RecommenderBuilder likellr(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				return new LLRRecommender(dataModel);
			}
		};
	}
	
	public RecommenderBuilder random(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				return new RandomRecommender(dataModel);
			}
		};
	}
	
	public RecommenderBuilder mostpopular(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				return new MostPopular(dataModel);
			}
		};
	}
	
	public RecommenderBuilder itembased(){
		return new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				ItemSimilarity similarity = null;
				GenericBooleanPrefDataModel booleanDatamodel = new GenericBooleanPrefDataModel(dataModel);
				//similarity = new PearsonCorrelationSimilarity(dataModel);
				similarity = new UncenteredCosineSimilarity(dataModel);
				return new GenericItemBasedRecommender(booleanDatamodel,similarity);
				//return new GenericBooleanPrefItemBasedRecommender(booleanDatamodel,similarity);
			}
		};
	}
}
