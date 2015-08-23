package ch.hsr.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.RandomRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecommenderFactory {
	String connectionString;
	DataModel likeDataModel;
	DataModel tagDataModel;
	Logger log = LoggerFactory.getLogger(RecommenderFactory.class);
	SolrClient solrClient;
	
	public RecommenderFactory(String connectionString, DataModel likedm, DataModel tagDataModel){
		this.connectionString = connectionString;
		this.tagDataModel = tagDataModel;
		this.likeDataModel = likedm;
		this.solrClient = new HttpSolrClient(connectionString);
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
	
	public RecommenderBuilder llrRecommender(){
		return new RecommenderBuilder() {
			public void initializeSolr() {
				try {
				clearSolr();
				DataModel likes = pref2like(likeDataModel);
				log.info("create llrrecommender: " + likes.getNumItems());
				ItemSimilarity likesimilarity = new LogLikelihoodSimilarity(likes);
				ItemSimilarity cosinesimilarity = new UncenteredCosineSimilarity(likeDataModel);
				ItemSimilarity tagsimilarity = new LogLikelihoodSimilarity(tagDataModel);

				SolrDocumentFactory factory = new SolrDocumentFactory();
				List<SolrInputDocument> solrdocs = factory.createSolrDocs(likeDataModel, tagsimilarity, cosinesimilarity, likesimilarity);
				updateSolr(solrdocs);
				} catch (TasteException e) {
					log.error(e.getMessage());
					e.printStackTrace();
				} catch (SolrServerException e) {
					log.error(e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
			
			private void updateSolr(List<SolrInputDocument> solrdocs)
					throws SolrServerException, IOException {
				log.info("update solr " + solrdocs.size());
				solrClient.add(solrdocs);
				solrClient.commit();
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
			public Recommender buildRecommender(DataModel dataModel)
					throws TasteException {
				initializeSolr();
				return new LLRRecommender(dataModel, tagDataModel, connectionString);
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
				ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
				return new GenericItemBasedRecommender(dataModel,similarity);
			}
		};
	}

}
